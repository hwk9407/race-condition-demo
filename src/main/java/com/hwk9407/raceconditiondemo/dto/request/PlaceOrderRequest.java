package com.hwk9407.raceconditiondemo.dto.request;

public record PlaceOrderRequest(
        Long productId,
        Long userId,
        int quantity
) {}
