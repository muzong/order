# 库存模块代码说明

## 文件清单

### 领域层 (domain/)
- **Stock.java** - 库存聚合根实体
- **StockId.java** - 库存ID值对象
- **SkuId.java** - SKU ID值对象
- **StockLock.java** - 库存锁定值对象
- **StockStatus.java** - 库存状态枚举
- **InventoryRepository.java** - 仓储接口
- **InventoryDomainService.java** - 领域服务接口
- **exception/InventoryException.java** - 通用库存异常
- **exception/StockNotFoundException.java** - 库存不存在异常
- **exception/InsufficientStockException.java** - 库存不足异常

### 应用层 (application/)
- **InventoryApplicationService.java** - 应用服务接口

### 基础设施层 (infrastructure/)
- **entity/StockDO.java** - 库存数据对象
- **entity/StockLockDO.java** - 库存锁定数据对象
- **repository/StockMapper.java** - MyBatis-Plus Mapper
- **repository/StockLockMapper.java** - MyBatis-Plus Mapper
- **repository/InventoryRepositoryImpl.java** - 仓储实现
- **config/RedisLockConfig.java** - Redis分布式锁配置

### 接口层 (interfaces/)
- **controller/InventoryController.java** - 库存控制器
- **dto/StockLockRequest.java** - 库存锁定请求
- **dto/StockReleaseRequest.java** - 库存释放请求
- **dto/StockResponse.java** - 库存查询响应

### 数据库 (resources/db/)
- **schema.sql** - 数据库表结构定义
- **inventory.sql** - 库存相关SQL

## 业务规则

### 1. 库存计算公式
```
可售库存 = 总库存 - 预占库存 - 已售库存
```

### 2. 并发控制
- 使用 Redis RedLock 分布式锁
- 锁超时时间: 30秒
- SKU 级别锁: `lock:stock:{skuId}`

### 3. 库存锁定流程
1. 检查可售库存是否充足
2. 获取分布式锁
3. 增加预占库存
4. 记录锁定日志

### 4. 库存释放流程
1. 检查预占库存是否足够
2. 获取分布式锁
3. 减少预占库存
4. 清理锁定记录

### 5. 库存扣减流程（支付成功后）
1. 检查预占库存是否足够
2. 获取分布式锁
3. 预占库存转为已售库存
4. 清理预占库存

## API 接口

### 1. 库存锁定
```
POST /api/v1/inventory/lock
请求体:
{
    "skuId": "SKU001",
    "quantity": 2,
    "lockId": "LOCK:ORDER001"
}
```

### 2. 库存释放
```
POST /api/v1/inventory/release
请求体:
{
    "skuId": "SKU001",
    "quantity": 2,
    "lockId": "LOCK:ORDER001"
}
```

### 3. 库存查询
```
GET /api/v1/inventory/query?skuId=SKU001
响应:
{
    "skuId": "SKU001",
    "totalStock": 1000,
    "lockedStock": 50,
    "soldStock": 100,
    "availableStock": 850
}
```

## DDD 四层架构说明

1. **Domain 层**：核心业务逻辑，不依赖其他层
2. **Application 层**：业务流程编排，事务管理
3. **Infrastructure 层**：数据库访问、Redis、MQ
4. **Interfaces 层**：HTTP API 接口

## 使用说明

1. 确保 Redis 服务正常运行
2. 执行 schema.sql 创建表结构
3. 配置 MyBatis-Plus 数据源
4. 启动 Spring Boot 应用

## 注意事项

1. 预占库存超时时间为 30 秒
2. 订单超时时间为 15 分钟
3. 所有库存操作必须加分布式锁防止超卖
4. 库存变更必须使用原子操作
