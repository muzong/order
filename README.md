# Order Pay System

基于 DDD 架构的订单支付系统

## 项目结构

```
src/main/java/com/orderpay/
├── OrderPayApplication.java                 # 启动类
├── common/                                   # 公共模块
│   ├── result/                              # 统一返回
│   ├── exception/                           # 异常处理
│   ├── constant/                            # 常量定义
│   └── util/                                # 工具类
├── order/                                    # 订单子域
│   ├── domain/                              # 领域层
│   │   ├── model/                           # Order, OrderItem, Money, OrderStatus
│   │   ├── repository/                      # OrderRepository
│   │   ├── service/                         # OrderDomainService
│   │   └── event/                           # OrderCreatedEvent, OrderCancelledEvent
│   ├── application/                         # 应用层
│   │   └── service/                         # OrderApplicationService
│   ├── interfaces/                          # 接口层
│   │   ├── controller/                      # OrderController
│   │   └── dto/                             # CreateOrderRequest, OrderResponse
│   └── infrastructure/                      # 基础设施层
│       ├── entity/                          # OrderDO, OrderItemDO
│       ├── mapper/                          # OrderMapper
│       ├── convert/                         # OrderConvert
│       ├── repository/                      # OrderRepositoryImpl
│       └── config/                          # MyBatisConfig
├── payment/                                  # 支付子域
│   ├── domain/
│   ├── application/
│   ├── interfaces/
│   └── infrastructure/
└── inventory/                                # 库存子域
    ├── domain/
    ├── application/
    ├── interfaces/
    └── infrastructure/
```

## 技术栈

- Spring Boot 3.2.5
- MyBatis Plus 3.5.5
- MySQL 8.0
- Redis 7.0
- RocketMQ 5.0
- Lombok

## 启动步骤

1. 配置数据库连接 (application-dev.yml)
2. 执行 schema.sql 创建表
3. 启动应用: `mvn spring-boot:run`

## 接口说明

- POST /api/v1/order/create - 创建订单
- POST /api/v1/order/cancel - 取消订单  
- GET /api/v1/order/query - 查询订单
