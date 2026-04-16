package com.orderpay.inventory.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class SkuId implements Serializable {
    private String value;
}
