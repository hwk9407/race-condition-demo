package com.hwk9407.raceconditiondemo.service;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CartService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StockService stockService;

    public void addToCart(Long productId, Long userId, int quantity) {
        String cartKey = "cart:" + userId;
        String productKey = String.valueOf(productId);

        int remainingStock = stockService.getAvailableStock(productId);
        if (remainingStock < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }

        Boolean existProduct = redisTemplate.opsForHash().hasKey(cartKey, productKey);
        if (existProduct) {
            Integer existingQuantity = (Integer) redisTemplate.opsForHash().get(cartKey, productKey);
            if (existingQuantity != null) {
                stockService.increaseStock(productId, existingQuantity);
            }
        }

        redisTemplate.opsForHash().put(cartKey, productKey, quantity);
        redisTemplate.expire(cartKey, 30, TimeUnit.MINUTES);  // 30분 TTL
        stockService.decreaseStock(productId, quantity);

        Set<Object> cartItems = redisTemplate.opsForHash().keys(cartKey);
        for (Object cartItem : cartItems) {
            String remainingStockKey = "remaining_stock:" + cartItem;
            long ttl = redisTemplate.getExpire(remainingStockKey, TimeUnit.MINUTES);
            if (ttl > 0 && ttl <= 30) {
                redisTemplate.expire(remainingStockKey, 2, TimeUnit.HOURS);
            }
        }
    }

    public void removeFromCart(Long userId, Long productId) {
        String cartKey = "cart:" + userId;
        Integer quantity = (Integer) redisTemplate.opsForHash().get(cartKey, String.valueOf(productId));

        if (quantity == null) throw new IllegalArgumentException("장바구니에 해당 상품이 존재하지 않습니다.");

        redisTemplate.opsForHash().delete(cartKey, String.valueOf(productId));
        stockService.increaseStock(productId, quantity);
    }
}
