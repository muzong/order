package com.orderpay.inventory.interfaces.controller;

import com.orderpay.common.result.Result;
import com.orderpay.inventory.application.service.InventoryApplicationService;
import com.orderpay.inventory.interfaces.dto.StockLockRequest;
import com.orderpay.inventory.interfaces.dto.StockReleaseRequest;
import com.orderpay.inventory.interfaces.dto.StockResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
    
    private final InventoryApplicationService inventoryApplicationService;
    
    public InventoryController(InventoryApplicationService inventoryApplicationService) {
        this.inventoryApplicationService = inventoryApplicationService;
    }
    
    @PostMapping("/lock")
    public Result<StockResponse> lock(@RequestBody StockLockRequest request) {
        StockResponse response = inventoryApplicationService.lock(request);
        return Result.success(response);
    }
    
    @PostMapping("/release")
    public Result<Void> release(@RequestBody StockReleaseRequest request) {
        inventoryApplicationService.release(request);
        return Result.success();
    }
    
    @GetMapping("/{skuId}")
    public Result<StockResponse> query(@PathVariable String skuId) {
        StockResponse response = inventoryApplicationService.query(skuId);
        return Result.success(response);
    }
}
