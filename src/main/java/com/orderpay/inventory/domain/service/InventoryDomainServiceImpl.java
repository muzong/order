package com.orderpay.inventory.domain.service;

import com.orderpay.inventory.domain.exception.InsufficientStockException;
import com.orderpay.inventory.domain.exception.StockNotFoundException;
import com.orderpay.inventory.domain.model.Stock;
import com.orderpay.inventory.domain.model.StockLock;
import com.orderpay.inventory.domain.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryDomainServiceImpl implements InventoryDomainService {
    
    private final InventoryRepository inventoryRepository;
    
    @Override
    @Transactional
    public boolean lock(StockLock lock) {
        try {
            inventoryRepository.lock(lock);
            return true;
        } catch (Exception e) {
            log.error("库存锁定失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public void release(String lockId) {
        inventoryRepository.unlock(lockId);
    }
    
    @Override
    @Transactional
    public void deduct(StockLock lock) {
        Optional<Stock> stockOptional = inventoryRepository.findBySkuId(lock.getSkuId().getValue());
        if (!stockOptional.isPresent()) {
            throw new StockNotFoundException("库存不存在");
        }
        
        Stock stock = stockOptional.get();
        if (stock.getAvailableStock() < lock.getLockedQty()) {
            throw new InsufficientStockException("库存不足");
        }
        
        inventoryRepository.deduct(lock);
    }
}
