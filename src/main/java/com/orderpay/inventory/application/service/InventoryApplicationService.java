package com.orderpay.inventory.application.service;

import com.orderpay.inventory.interfaces.dto.StockLockRequest;
import com.orderpay.inventory.interfaces.dto.StockReleaseRequest;
import com.orderpay.inventory.interfaces.dto.StockResponse;

public interface InventoryApplicationService {
    StockResponse lock(StockLockRequest request);
    void release(StockReleaseRequest request);
    StockResponse query(String skuId);
    void setLockId(String lockId);
}
