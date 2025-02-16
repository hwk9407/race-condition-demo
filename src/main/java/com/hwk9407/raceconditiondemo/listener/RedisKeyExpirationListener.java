package com.hwk9407.raceconditiondemo.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisKeyExpirationListener implements MessageListener {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();

        if (expiredKey.startsWith("cart:")) {
            Long userId = Long.parseLong(expiredKey.split(":")[1]);
            log.info("장바구니 TTL 만료 감지 - 사용자 ID: {}", userId);

            eventPublisher.publishEvent(new CartExpiredEvent(this, userId));
        }
    }
}