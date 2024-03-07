package com.simple.ecom.site.orderservice.controller;

import com.simple.ecom.site.orderservice.customerrors.CustomException;
import com.simple.ecom.site.orderservice.dto.OrderRequest;
import com.simple.ecom.site.orderservice.dto.OrderResponse;
import com.simple.ecom.site.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest){
        try {
            String orderNumber = orderService.placeOrder(orderRequest);
            return ResponseEntity.ok(new OrderResponse(orderNumber, HttpStatus.CREATED, "Order Placed Successfully"));
        } catch (Exception e) {
            if(e instanceof CustomException)
                return ResponseEntity.ok(new OrderResponse(null, HttpStatus.BAD_REQUEST, e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
