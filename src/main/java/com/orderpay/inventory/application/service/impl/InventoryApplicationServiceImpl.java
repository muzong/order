package com.orderpay.inventory.application.service.impl;

import com.orderpay.inventory.domain.exception.InsufficientStockException;
import com.orderpay.inventory.domain.exception.StockNotFoundException;
import com.orderpay.inventory.domain.model.*;
import com.orderpay.inventory.domain.repository.InventoryRepository;
import com.orderpay.inventory.domain.service.InventoryDomainService;
import com.orderpay.inventory.interfaces.dto.StockLockRequest;
import com.orderpay.inventory.interfaces.dto.StockReleaseRequest;
import com.orderpay.inventory.interfaces.dto.StockResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryApplicationServiceImpl implements InventoryApplicationService {
    
    private final InventoryRepository inventoryRepository;
    private final InventoryDomainService inventoryDomainService;
    
    private String lockId;

    @Override
    @Transactional
    public StockResponse lock(StockLockRequest request) {
        this.lockId = "LOCK_" + System.currentTimeMillis();
        StockLock stockLock = StockLock.builder()
            .lockId(this.lockId)
            .skuId(new SkuId(request.getSkuId()))
            .lockedQty(request.getQuantity())
            .expireAt(LocalDateTime.now().plusSeconds(30))
            .build();
            
        boolean success = inventoryDomainService.lock(stockLock);
        if (!success) {
            throw new InsufficientStockException("库存不足");
        }
        
        StockResponse response = query(request.getSkuId());
        response.setLockId(this.lockId);
        return response;
    }
    
    @Override
    @Transactional
    public void release(StockReleaseRequest request) {
        inventoryDomainService.release(request.getLockId());
    }
    
    @Override
    public StockResponse query(String skuId) {
        Optional<com.orderpay.inventory.domain.model.Stock> stockOptional = inventoryRepository.findBySkuId(skuId);
        if (!stockOptional.isPresent()) {
            throw new StockNotFoundException("库存不存在");
        }
        
        com.orderpay.inventory.domain.model.Stock stock = stockOptional.get();
        return StockResponse.builder()
            .skuId(stock.getSkuId().getValue())
            .totalStock(stock.getTotalStock())
            .lockedStock(stock.getLockedStock())
            .availableStock(stock.getAvailableStock())
            .build();
    }
    
    @Override
    public void setLockId(String lockId) {
        this.lockId = lockId;
    }
}
