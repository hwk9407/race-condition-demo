package com.hwk9407.raceconditiondemo.controller;

import com.hwk9407.raceconditiondemo.dto.request.AddToCartRequest;
import com.hwk9407.raceconditiondemo.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/api/cart")
    public ResponseEntity<Void> addToCart(@RequestBody AddToCartRequest req) {
        cartService.addToCart(req.productId(), req.userId(), req.quantity());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }
}
