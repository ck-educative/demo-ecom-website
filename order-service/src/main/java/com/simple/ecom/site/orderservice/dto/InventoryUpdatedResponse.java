package com.simple.ecom.site.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryUpdatedResponse {
    private String skuCode;
    private boolean isAvailable;
}