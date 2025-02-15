package com.hwk9407.raceconditiondemo.controller;

import com.hwk9407.raceconditiondemo.dto.request.PlaceOrderRequest;
import com.hwk9407.raceconditiondemo.dto.response.PlaceOrderResponse;
import com.hwk9407.raceconditiondemo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/api/order")
    public ResponseEntity<PlaceOrderResponse> placeOrder(@RequestBody PlaceOrderRequest req) {
        PlaceOrderResponse res = orderService.placeOrder(req.productId(), req.userId(), req.quantity());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(res);
    }
}
