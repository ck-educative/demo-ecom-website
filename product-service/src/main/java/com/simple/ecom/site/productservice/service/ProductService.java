package com.simple.ecom.site.productservice.service;

import java.util.List;
import java.util.stream.Stream;

import com.simple.ecom.site.productservice.model.Product;
import com.simple.ecom.site.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;

import com.simple.ecom.site.productservice.dto.ProductRequest;
import com.simple.ecom.site.productservice.dto.ProductResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);
        log.info("Product {} is saved", product.getId());
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        // method-reference
        return products.stream()
                .map(this::mapToProductResponse).toList();
    }

    public List<ProductResponse> getSearchResults(String name) {
        Stream<Product> products = productRepository.findAll().stream().filter(
                product -> product.getName().contains(name)
        );

        return products.map(this::mapToProductResponse).toList();

    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice()) // Fixed the method name
                .build();
    }

}
