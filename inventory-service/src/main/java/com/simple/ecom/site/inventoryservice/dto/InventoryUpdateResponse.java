package com.simple.ecom.site.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryUpdateResponse {
    private String skuCode;
    private int quantity;
    private boolean isUpdated;
}