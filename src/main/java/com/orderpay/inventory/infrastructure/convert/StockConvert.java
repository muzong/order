package com.orderpay.inventory.infrastructure.convert;

import com.orderpay.inventory.domain.model.SkuId;
import com.orderpay.inventory.domain.model.StockId;
import com.orderpay.inventory.infrastructure.entity.StockDO;
import com.orderpay.inventory.domain.model.Stock;
import org.springframework.stereotype.Component;

@Component
public class StockConvert {
    public Stock toDomain(StockDO stockDO) {
        return Stock.builder()
            .id(new StockId(stockDO.getId()))
            .skuId(new SkuId(stockDO.getSkuId()))
            .totalStock(stockDO.getTotalStock())
            .lockedStock(stockDO.getLockedStock())
            .availableStock(stockDO.getAvailableStock())
            .build();
    }
    
    public StockDO toDO(Stock stock) {
        return StockDO.builder()
            .id(stock.getId().getValue())
            .skuId(stock.getSkuId().getValue())
            .totalStock(stock.getTotalStock())
            .lockedStock(stock.getLockedStock())
            .availableStock(stock.getAvailableStock())
            .build();
    }
}
