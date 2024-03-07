package com.simple.ecom.site.orderservice.service;

import com.simple.ecom.site.orderservice.dto.*;
import com.simple.ecom.site.orderservice.model.Order;
import com.simple.ecom.site.orderservice.model.OrderLineItems;
import com.simple.ecom.site.orderservice.repository.OrderRepository;
import com.simple.ecom.site.orderservice.customerrors.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    org.apache.commons.logging.Log log =
            org.apache.commons.logging.LogFactory.getLog(OrderService.class);
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    public String placeOrder(OrderRequest orderRequest) throws CustomException {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsListDto()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItem(orderLineItems);

        List<String> skuCodes = order.getOrderLineItem().stream()
                .map(OrderLineItems::getSkuCode).toList();

        List<InventoryRequest> inventoryRequests = order.getOrderLineItem().stream()
                .map(orderLineItems1 -> {
                    InventoryRequest inventoryRequest = new InventoryRequest();
                    inventoryRequest.setSkuCode(orderLineItems1.getSkuCode());
                    inventoryRequest.setQuantity(orderLineItems1.getQuantity());
                    return inventoryRequest;
                }).toList();

        InventoryResponse[] inventoryResponses = webClientBuilder.build().method(HttpMethod.GET)
                .uri("http://inventory-service/api/inventory")
                .body(BodyInserters.fromValue(inventoryRequests))
                .retrieve().bodyToMono(InventoryResponse[].class).block();

        assert inventoryResponses != null;
        log.info("Inventory response size:" + inventoryResponses.length);

        if(inventoryResponses.length > 0) {
            assert Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isAvailable);
            boolean allProductsAvailable = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isAvailable);

            if (allProductsAvailable) {
                orderRepository.save(order);
                InventoryUpdatedResponse[] inventoryUpdatedResponses = webClientBuilder.build().method(HttpMethod.PUT)
                        .uri("http://inventory-service/api/inventory")
                        .body(BodyInserters.fromValue(inventoryRequests))
                        .retrieve().bodyToMono(InventoryUpdatedResponse[].class).block();
                //TODO: Call inventory service to update the inventory
                log.info("Inventory updated response:" + Arrays.toString(inventoryUpdatedResponses));
                return order.getOrderNumber();
            }
            else {
                throw new CustomException("Inventory not available");
            }
        }
        else {
           throw new CustomException("Inventory not available");
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
       OrderLineItems orderLineItems = new OrderLineItems();
       orderLineItems.setPrice(orderLineItemsDto.getPrice());
       orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
       orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());

       return orderLineItems;
    }
}
