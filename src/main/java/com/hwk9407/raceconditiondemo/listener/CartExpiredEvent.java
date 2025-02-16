package com.hwk9407.raceconditiondemo.listener;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CartExpiredEvent extends ApplicationEvent {
    private final Long userId;

    public CartExpiredEvent(Object source, Long userId) {
        super(source);
        this.userId = userId;
    }
}