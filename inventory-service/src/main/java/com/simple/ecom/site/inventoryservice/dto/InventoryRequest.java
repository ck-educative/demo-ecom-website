package com.simple.ecom.site.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class InventoryRequest {
    private String skuCode;
    private int quantity;
}
