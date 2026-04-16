package com.orderpay.inventory.domain.service;

import com.orderpay.inventory.domain.model.StockLock;
import java.util.Optional;

public interface InventoryDomainService {
    boolean lock(StockLock lock);
    void release(String lockId);
    void deduct(StockLock lock);
}
