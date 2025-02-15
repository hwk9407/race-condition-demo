package com.hwk9407.raceconditiondemo.controller;

import com.hwk9407.raceconditiondemo.dto.request.CreateProductRequest;
import com.hwk9407.raceconditiondemo.dto.response.CreateProductResponse;
import com.hwk9407.raceconditiondemo.dto.response.RetrieveProductResponse;
import com.hwk9407.raceconditiondemo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/api/product")
    public ResponseEntity<CreateProductResponse> createProduct(@RequestBody CreateProductRequest req) {
        CreateProductResponse res = productService.createProduct(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(res);
    }

    @GetMapping("/api/product/{id}")
    public ResponseEntity<RetrieveProductResponse> retrieveProduct(@PathVariable Long id) {
        RetrieveProductResponse res = productService.retrieveProduct(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }

}
