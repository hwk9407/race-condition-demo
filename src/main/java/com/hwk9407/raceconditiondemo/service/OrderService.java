package com.hwk9407.raceconditiondemo.service;

import com.hwk9407.raceconditiondemo.dto.response.PlaceOrderResponse;
import com.hwk9407.raceconditiondemo.entity.Product;
import com.hwk9407.raceconditiondemo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;

    public PlaceOrderResponse placeOrder(Long productId, Long userId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // 주문 테이블 생략

        product.decrementStock(quantity);
        productRepository.save(product);

        return new PlaceOrderResponse(product.getId());
    }
}
