package com.simple.ecom.site.inventoryservice.controller;

import com.simple.ecom.site.inventoryservice.dto.InventoryRequest;
import com.simple.ecom.site.inventoryservice.dto.InventoryResponse;
import com.simple.ecom.site.inventoryservice.dto.InventoryUpdateResponse;
import com.simple.ecom.site.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {


    private final InventoryService inventoryService;
    private static final org.apache.commons.logging.Log log =
            org.apache.commons.logging.LogFactory.getLog(InventoryService.class);

    // http://hostname:port/api/inventory?inventoryAvailability[0].skuCode="something"&inventoryAvailability[0].quantity=1400&
    //inventoryAvailability[1].skuCode="iphone14"&inventoryAvailibility[1].quantity=100;
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isAvailable(@RequestBody List<InventoryRequest> objects){
        log.info(objects);
        log.info(objects.stream().toList().getFirst());
        return inventoryService.isAvailable(objects);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryUpdateResponse> updateInventory(@RequestBody List<InventoryRequest> objects) {
        return inventoryService.updateInventory(objects);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<InventoryResponse> createInventory(@RequestBody List<InventoryRequest> objects) {
        return inventoryService.createOrIncrementInventory(objects);
    }
}
