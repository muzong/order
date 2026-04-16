package com.orderpay.inventory.domain.repository;

import com.orderpay.inventory.domain.model.Stock;
import com.orderpay.inventory.domain.model.StockLock;

import java.util.Optional;

public interface InventoryRepository {
    Optional<Stock> findBySkuId(String skuId);
    void save(Stock stock);
    void lock(StockLock lock);
    void unlock(String lockId);
    void deduct(StockLock lock);
}
