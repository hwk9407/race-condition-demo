package com.hwk9407.raceconditiondemo.dto.request;

public record AddToCartRequest(
        Long userId,
        Long productId,
        int quantity
) {
}
