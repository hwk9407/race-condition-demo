package com.hwk9407.raceconditiondemo.repository;

import com.hwk9407.raceconditiondemo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Object> findByName(String name);
}
