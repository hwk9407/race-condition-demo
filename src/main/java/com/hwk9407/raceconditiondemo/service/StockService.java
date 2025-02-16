package com.hwk9407.raceconditiondemo.service;

import com.hwk9407.raceconditiondemo.entity.Product;
import com.hwk9407.raceconditiondemo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class StockService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductRepository productRepository;

    public int getAvailableStock(Long productId) {
        String remainingStockKey = "remaining_stock:" + productId;
        Integer remainingStock = (Integer) redisTemplate.opsForValue().get(remainingStockKey);

        if (remainingStock == null) {
            remainingStock = loadStockFromDatabase(productId);
        }

        return remainingStock;
    }

    private int loadStockFromDatabase(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
        int stock = product.getStock();
        String remainingStockKey = "remaining_stock:" + productId;
        redisTemplate.opsForValue().set(remainingStockKey, stock, 2, TimeUnit.HOURS);

        return stock;
    }

    public void decreaseStock(Long productId, int quantity) {
        String remainingStockKey = "remaining_stock:" + productId;
        redisTemplate.opsForValue().decrement(remainingStockKey, quantity);
    }

    public void increaseStock(Long productId, int quantity) {
        String remainingStockKey = "remaining_stock:" + productId;
        redisTemplate.opsForValue().increment(remainingStockKey, quantity);
    }
}
