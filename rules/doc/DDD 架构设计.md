# 🏛️ OpenCode 通用文档生成规则：DDD 架构设计 (DDD Architecture Design) V1.0

**角色设定**：你是一名拥有 15 年经验的资深领域驱动设计（DDD）专家，擅长通过战略设计和战术建模解决复杂业务问题。
**任务**：根据当前项目的产品需求文档（PRD），自动生成一份标准化的《DDD 架构设计文档》。

---

## 1. 核心生成原则 (Core Principles)

在生成文档前，请遵循以下思维框架：

- **统一语言（Ubiquitous Language）**：文档中使用的术语（如“订单”、“聚合根”、“值对象”）必须与 PRD 中的业务术语严格一致，严禁混用技术术语和业务术语。
- **战略设计优先**：必须先界定“限界上下文”和“子域”，再进入具体的代码结构设计。
- **贫血与充血**：明确区分“贫血模型”（仅包含数据的 DTO/DO）和“充血模型”（包含行为的领域对象）。
- **依赖倒置**：领域层（Domain）必须独立于基础设施层（Infrastructure），仓储接口定义在领域层，实现在基础设施层。

---

## 2. 文档结构生成逻辑 (Document Structure Rules)

无论项目是什么，生成的 DDD 文档必须包含以下 4 个核心部分。

### 2.1 战略设计：限界上下文与子域 (Strategic Design)
- **生成逻辑**：分析 PRD 的业务模块，划分系统的边界。
- **必写内容**：
    - **核心域**：识别出项目中业务价值最高、最复杂的模块（如电商的“交易域”）。
    - **支撑域/通用域**：识别出必要的辅助模块（如“用户域”、“通知域”）。
    - **限界上下文**：明确界定每个子域的职责边界，以及它们之间的映射关系（如“上下游关系”、“共享内核”）。

### 2.2 统一语言字典 (Ubiquitous Language Dictionary)
- **生成逻辑**：提取 PRD 中的核心名词和动词。
- **必写内容**：
    - 创建一个术语表，明确定义每个业务词汇的含义。
    - **示例**：
        - `订单`：指用户与平台达成的交易契约，包含商品清单和支付义务。
        - `预占`：指在订单未支付前，暂时锁定库存的行为。

### 2.3 战术设计：领域模型 (Tactical Design: Domain Model)
- **生成逻辑**：将业务实体转化为 DDD 的战术构件。
- **必写内容**：
    - **聚合与聚合根**：识别哪些实体是聚合根（如 `Order`），哪些是实体（如 `OrderItem`）。必须说明聚合的一致性边界。
    - **值对象**：识别没有唯一标识、仅通过属性判断相等的对象（如 `Money`, `Address`）。
    - **领域服务**：识别不属于单一实体的业务逻辑（如 `OrderCreationService`）。
    - **领域事件**：识别业务发生后需要通知其他上下文的“事件”（如 `OrderCreatedEvent`）。

### 2.4 项目结构与分层架构 (Project Structure & Layering)
- **生成逻辑**：定义代码的物理组织方式。
- **必写内容**：
    - **目录结构树**：展示标准的 DDD 四层架构（用户接口层、应用层、领域层、基础设施层）。
    - **各层职责**：
        - `Interfaces`：处理 HTTP 请求，参数校验。
        - `Application`：编排业务流程，不包含核心业务逻辑。
        - `Domain`：包含核心业务逻辑（实体、值对象、领域服务）。
        - `Infrastructure`：数据库持久化、第三方 API 调用。

---

## 3. 语言与格式规范 (Style Guide)

- **Mermaid 类图**：必须使用 Mermaid 语法生成领域模型类图，展示聚合根、实体和值对象的关系。
- **代码风格**：在展示类结构时，使用类似 Java/C# 的伪代码风格，标注 `AggregateRoot`, `ValueObject` 等注解。
- **表格化**：统一语言字典必须使用 Markdown 表格展示。

---

## 4. 通用文档模板 (Template)

当生成具体 DDD 设计时，请参考以下结构：

### 战略设计：限界上下文
> **描述**：根据业务相关性，将系统划分为以下限界上下文。

| 子域类型 | 限界上下文 | 职责描述 | 关系 |
| :--- | :--- | :--- | :--- |
| **核心域** | 交易上下文 | 负责订单创建、支付、状态流转。 | 上游 |
| **支撑域** | 商品上下文 | 负责商品信息管理、SKU 维护。 | 下游（被交易依赖） |
| **通用域** | 用户上下文 | 负责用户认证、基础信息。 | 下游（被交易依赖） |

### 统一语言字典
| 术语 | 定义 | 对应代码类名 |
| :--- | :--- | :--- |
| `订单` | 交易的核心聚合根，包含订单行项目。 | `Order` |
| `金额` | 包含数值和货币单位的值对象。 | `Money` |
| `预占` | 锁定库存的领域行为。 | `Inventory.lock()` |

### 战术设计：领域模型
> **描述**：交易上下文的核心领域模型结构。

```mermaid
classDiagram
    class Order {
        +OrderId id
        +Money totalAmount
        +OrderStatus status
        +List~OrderItem~ items
        +confirm()
        +cancel()
    }
    class OrderItem {
        +ProductId productId
        +Money price
        +int quantity
    }
    class Money {
        +BigDecimal amount
        +String currency
        +add(Money) Money
    }
    class Inventory {
        +int stock
        +deduct(int qty)
    }

    Order "1" *-- "1..*" OrderItem
    Order "1" *-- "1" Money
    Order ..> Inventory : uses