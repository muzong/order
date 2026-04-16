package com.orderpay.inventory.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orderpay.inventory.domain.exception.StockNotFoundException;
import com.orderpay.inventory.domain.model.*;
import com.orderpay.inventory.domain.repository.InventoryRepository;
import com.orderpay.inventory.infrastructure.entity.StockDO;
import com.orderpay.inventory.infrastructure.entity.StockLockDO;
import com.orderpay.inventory.infrastructure.mapper.StockLockMapper;
import com.orderpay.inventory.infrastructure.mapper.StockMapper;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class InventoryRepositoryImpl implements InventoryRepository {
    
    private final StockMapper stockMapper;
    private final StockLockMapper stockLockMapper;
    private final RedissonClient redissonClient;
    
    @Override
    public Optional<Stock> findBySkuId(String skuId) {
        StockDO stockDO = stockMapper.selectOne(
            new LambdaQueryWrapper<StockDO>().eq(StockDO::getSkuId, skuId));
        if (stockDO == null) {
            return Optional.empty();
        }
        return Optional.of(convertToEntity(stockDO));
    }
    
    @Override
    public void save(Stock stock) {
        StockDO stockDO = convertToDO(stock);
        stockMapper.insert(stockDO);
    }
    
    @Override
    public void lock(StockLock lock) {
        String lockKey = "inventory:lock:" + lock.getSkuId().getValue();
        RLock rLock = redissonClient.getLock(lockKey);
        
        try {
            boolean success = rLock.tryLock(0, 30, TimeUnit.SECONDS);
            if (!success) {
                throw new RuntimeException("获取库存锁失败");
            }
            
            Optional<Stock> stockOptional = findBySkuId(lock.getSkuId().getValue());
            if (!stockOptional.isPresent()) {
                throw new StockNotFoundException("库存不存在");
            }
            
            Stock stock = stockOptional.get();
            if (stock.getAvailableStock() < lock.getLockedQty()) {
                throw new RuntimeException("库存不足");
            }
            
            StockDO stockDO = convertToDO(stock);
            stockDO.setLockedStock(stock.getLockedStock() + lock.getLockedQty());
            stockDO.setAvailableStock(stock.getAvailableStock() - lock.getLockedQty());
            stockMapper.updateById(stockDO);
            
            StockLockDO lockDO = StockLockDO.builder()
                .lockId(lock.getLockId())
                .skuId(lock.getSkuId().getValue())
                .lockedQty(lock.getLockedQty())
                .expireAt(lock.getExpireAt() != null ? lock.getExpireAt().toInstant().getEpochSecond() : null)
                .createdAt(System.currentTimeMillis() / 1000)
                .build();
            stockLockMapper.insert(lockDO);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("获取库存锁被中断", e);
        } finally {
            rLock.unlock();
        }
    }
    
    @Override
    public void unlock(String lockId) {
        Optional<StockLockDO> lockOptional = stockLockMapper.selectList(null)
            .stream().filter(l -> lockId.equals(l.getLockId())).findFirst();
            
        if (!lockOptional.isPresent()) {
            return;
        }
        
        StockLockDO stockLockDO = lockOptional.get();
        Optional<Stock> stockOptional = findBySkuId(stockLockDO.getSkuId());
        
        if (stockOptional.isPresent()) {
            StockDO stockDO = convertToDO(stockOptional.get());
            stockDO.setLockedStock(Math.max(0, stockDO.getLockedStock() - stockLockDO.getLockedQty()));
            stockDO.setAvailableStock(stockDO.getAvailableStock() + stockLockDO.getLockedQty());
            stockMapper.updateById(stockDO);
        }
        
        stockLockMapper.deleteById(stockLockDO.getId());
    }
    
    @Override
    public void deduct(StockLock lock) {
        Optional<Stock> stockOptional = findBySkuId(lock.getSkuId().getValue());
        if (!stockOptional.isPresent()) {
            throw new StockNotFoundException("库存不存在");
        }
        
        StockDO stockDO = convertToDO(stockOptional.get());
        stockDO.setTotalStock(stockDO.getTotalStock() - lock.getLockedQty());
        stockDO.setLockedStock(Math.max(0, stockDO.getLockedStock() - lock.getLockedQty()));
        stockMapper.updateById(stockDO);
    }
    
    private Stock convertToEntity(StockDO stockDO) {
        return Stock.builder()
            .id(new StockId(stockDO.getId()))
            .skuId(new SkuId(stockDO.getSkuId()))
            .totalStock(stockDO.getTotalStock())
            .lockedStock(stockDO.getLockedStock())
            .availableStock(stockDO.getAvailableStock())
            .build();
    }
    
    private StockDO convertToDO(Stock stock) {
        return StockDO.builder()
            .id(stock.getId().getValue())
            .skuId(stock.getSkuId().getValue())
            .totalStock(stock.getTotalStock())
            .lockedStock(stock.getLockedStock())
            .availableStock(stock.getAvailableStock())
            .build();
    }
}
