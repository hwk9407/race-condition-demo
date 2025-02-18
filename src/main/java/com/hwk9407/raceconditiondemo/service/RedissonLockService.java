package com.hwk9407.raceconditiondemo.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedissonLockService {

    private final RedissonClient redissonClient;

    public void lock(String key, Duration timeout, Runnable function) {
        RLock lock = redissonClient.getLock("lock:" + key);
        try {
            if (!lock.tryLock(timeout.getSeconds(), timeout.getSeconds(), TimeUnit.SECONDS)) {
                throw new RuntimeException("Lock 획득 실패");
            }
            function.run();
        } catch (Exception ex) {
            throw new RuntimeException("Lock 획득 실패");
        } finally {
            lock.unlock();
        }

    }
}