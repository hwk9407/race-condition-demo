package com.hwk9407.raceconditiondemo.dto.response;

public record RetrieveProductResponse(
        Long id,
        String name,
        int availableStock,
        int remainingStock
) {
}
