package com.hwk9407.raceconditiondemo.cart;

import com.hwk9407.raceconditiondemo.entity.Product;
import com.hwk9407.raceconditiondemo.repository.ProductRepository;
import com.hwk9407.raceconditiondemo.service.CartService;
import com.hwk9407.raceconditiondemo.service.StockService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class CartConcurrencyTest {

    private static final int THREAD_COUNT = 500;  // 동시에 요청할 쓰레드 수

    @Autowired
    private CartService cartService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StockService stockService;

    @Autowired
    private ProductRepository productRepository;

    private final Long productId = 1L;
    private final int initialStock = 50; // 초기 상품의 재고

    @BeforeEach
    void setup() {

        Product product = Product.builder()
                // .id(productId)
                .name("테스트 상품")
                .stock(initialStock)
                .build();
        productRepository.save(product);
        redisTemplate.opsForValue().set("remaining_stock:" + productId, initialStock);
    }

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Test
    @DisplayName("동시에 장바구니에 상품을 추가할 때 경쟁 상태 확인")
    public void concurrentCartAdditionTest() throws InterruptedException {
        // GIVEN
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT);

        // WHEN
        for (int i = 0; i < THREAD_COUNT; i++) {
            long userId = i + 1;
            executorService.execute(() -> {
                try {
                    barrier.await();
                    cartService.addToCart(productId, userId, 1);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        // THEN
        Integer remainingStock = (Integer) redisTemplate.opsForValue().get("remaining_stock:" + productId);
        assertNotNull(remainingStock);
        assertThat(remainingStock).isEqualTo(0);

    }
}
