package com.hwk9407.raceconditiondemo.listener;

import com.hwk9407.raceconditiondemo.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class CartExpiredEventListener {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StockService stockService;

    @EventListener
    public void handleCartExpiration(CartExpiredEvent event) {
        Long userId = event.getUserId();
        log.info("장바구니 TTL 만료 이벤트 처리 - 사용자 ID: {}", userId);

        checkAgainRemainingStock();
    }

    private void checkAgainRemainingStock() {
        Set<String> stockKeys = redisTemplate.keys("remaining_stock:*");
        if (!stockKeys.isEmpty()) redisTemplate.delete(stockKeys);

        Set<String> cartKeys = redisTemplate.keys("cart:*");

        if (cartKeys.isEmpty()) return;

        for (String cartKey : cartKeys) {
            Map<Object, Object> cartItems = redisTemplate.opsForHash().entries(cartKey);

            for (Map.Entry<Object, Object> entry : cartItems.entrySet()) {
                Long productId = Long.valueOf((String) entry.getKey());
                Integer quantity = (Integer) entry.getValue();

                stockService.getAvailableStock(productId);
                stockService.decreaseStock(productId, quantity);
            }
        }
    }
}
