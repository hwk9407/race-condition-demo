package com.hwk9407.raceconditiondemo.service;

import com.hwk9407.raceconditiondemo.dto.request.CreateProductRequest;
import com.hwk9407.raceconditiondemo.dto.response.CreateProductResponse;
import com.hwk9407.raceconditiondemo.dto.response.RetrieveProductResponse;
import com.hwk9407.raceconditiondemo.entity.Product;
import com.hwk9407.raceconditiondemo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StockService stockService;

    public CreateProductResponse createProduct(CreateProductRequest req) {
        productRepository.findByName(req.name()).ifPresent(product -> {
            throw new IllegalArgumentException("같은 이름으로 된 상품이 이미 존재합니다.");
        });
        Product newProduct = productRepository.save(Product.builder()
                .name(req.name())
                .stock(req.stock())
                .build()
        );

        return new CreateProductResponse(newProduct.getId());
    }

    public RetrieveProductResponse retrieveProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        int remainingStock = stockService.getAvailableStock(product.getId());

        return new RetrieveProductResponse(
                product.getId(),
                product.getName(),
                product.getStock(),
                remainingStock
        );
    }
}
