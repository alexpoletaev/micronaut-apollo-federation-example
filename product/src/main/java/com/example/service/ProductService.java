package com.example.service;

import com.example.model.Product;
import jakarta.inject.Singleton;

import java.math.BigDecimal;
import java.util.List;

@Singleton
public class ProductService {
    private final List<Product> products = List.of(
            new Product("sku1", "ice cream", new BigDecimal("1.55")),
            new Product("sku2", "orange juice", new BigDecimal("2.55")),
            new Product("sku3", "avocado", new BigDecimal("1.55")));

    public Product getProductBySku(String sku) {
        return products.stream().filter(p -> sku.equals(p.getSku())).findFirst().orElseThrow();
    }

    public List<Product> getProducts() {
        return products;
    }
}
