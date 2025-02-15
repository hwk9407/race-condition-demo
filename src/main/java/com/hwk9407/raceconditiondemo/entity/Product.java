package com.hwk9407.raceconditiondemo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int stock;


    public void decrementStock(int quantity) {
        if (this.stock - quantity < 0) {
            throw new IllegalArgumentException("주문한 상품량이 재고량보다 큽니다.");
        }
        stock -= quantity;
    }
}
