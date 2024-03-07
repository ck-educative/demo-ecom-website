package com.simple.ecom.site.inventoryservice.service;

import com.simple.ecom.site.inventoryservice.dto.InventoryRequest;
import com.simple.ecom.site.inventoryservice.dto.InventoryResponse;
import com.simple.ecom.site.inventoryservice.dto.InventoryUpdateResponse;
import com.simple.ecom.site.inventoryservice.model.Inventory;
import com.simple.ecom.site.inventoryservice.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    private static final org.apache.commons.logging.Log log =
            org.apache.commons.logging.LogFactory.getLog(InventoryService.class);

    @Transactional
    public List<InventoryResponse> isAvailable(List<InventoryRequest> inventoryRequests){

        List<String> skuCodes = inventoryRequests.stream().map(InventoryRequest::getSkuCode).toList();
        List<Inventory> inventoryList = inventoryRepository.findBySkuCodeIn(skuCodes).stream().toList();

        log.info("Found skuCodes" + inventoryList);

        List<InventoryResponse> inventoryResponses = inventoryRepository.findBySkuCodeIn(skuCodes).stream()
                .map(inventory ->
                        InventoryResponse.builder()
                                .skuCode(inventory.getSkuCode())
                                .quantity(inventory.getQuantity())
                                .isAvailable((inventory.getQuantity() > 0) &&(inventory.getQuantity() > getRequestedQuantityForSkuCode(inventoryRequests,inventory.getSkuCode())))
                                .build()
                ).toList();

        log.debug("Found value in the inventory"+ inventoryResponses);
        return inventoryResponses;
    }

    @Transactional
    public List<InventoryResponse> createOrIncrementInventory(List<InventoryRequest> inventoryRequests){
        List<Inventory> inventoryList = inventoryRequests.stream().map(inventoryRequest -> {
            Inventory inventory = new Inventory();
            if (inventoryRepository.findBySkuCode(inventoryRequest.getSkuCode()).isPresent()) {
                inventory = inventoryRepository.findBySkuCode(inventoryRequest.getSkuCode()).get();
                inventory.setQuantity(inventory.getQuantity() + inventoryRequest.getQuantity());
                return inventory;
            } else {
                inventory.setSkuCode(inventoryRequest.getSkuCode());
                inventory.setQuantity(inventoryRequest.getQuantity());
                return inventory;
            }
        }).toList();

        List<Inventory> savedInventory = inventoryRepository.saveAll(inventoryList);
        return savedInventory.stream().map(inventory ->
                InventoryResponse.builder()
                        .skuCode(inventory.getSkuCode())
                        .quantity(inventory.getQuantity())
                        .isAvailable(true)
                        .build()
        ).toList();
    }

    @Transactional
    public List<InventoryUpdateResponse> updateInventory(List<InventoryRequest> inventoryRequests){
        List<String> skuCodes = inventoryRequests.stream().map(InventoryRequest::getSkuCode).toList();
        List<Inventory> inventoryListFromDB = inventoryRepository.findBySkuCodeIn(skuCodes);
        List<Inventory> updatedResults = new ArrayList<>();
        inventoryListFromDB.forEach(inventory -> {
            inventoryRequests.forEach(inventoryRequest -> {
                if(Objects.equals(inventory.getSkuCode(), inventoryRequest.getSkuCode()) && inventory.getQuantity() > inventoryRequest.getQuantity()){
                    inventory.setQuantity(inventory.getQuantity() - inventoryRequest.getQuantity());
                    updatedResults.add(inventory);
                }
            });
        });

        List<Inventory> savedInventory = inventoryRepository.saveAll(updatedResults);
        log.info("savedInventory" + inventoryRequests.stream().toList());

        return savedInventory.stream().map(inventory ->
                InventoryUpdateResponse.builder()
                        .skuCode(inventory.getSkuCode())
                        .quantity(inventory.getQuantity())
                        .isUpdated(true)
                        .build()
        ).toList();
    }

    private Integer getRequestedQuantityForSkuCode(List<InventoryRequest> inventoryRequests, String skuCode){

        return inventoryRequests.stream().map(inventoryRequest -> {
            if (Objects.equals(inventoryRequest.getSkuCode(), skuCode)){
                return inventoryRequest.getQuantity();
            }
            return 0;
         }).toList().getFirst();
        }
}
