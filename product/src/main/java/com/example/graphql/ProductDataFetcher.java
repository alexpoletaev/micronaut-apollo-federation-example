package com.example.graphql;

import com.example.model.Product;
import com.example.service.ProductService;
import graphql.schema.DataFetcher;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Singleton
@RequiredArgsConstructor
public class ProductDataFetcher {

    private final ProductService productService;

    public DataFetcher<CompletableFuture<Product>> productBySku() {
        return dataFetchingEnvironment -> CompletableFuture.supplyAsync(() -> {
            String sku = dataFetchingEnvironment.getArgument("sku");

            return productService.getProductBySku(sku);
        });
    }

    public DataFetcher<CompletableFuture<List<Product>>> products() {
        return dataFetchingEnvironment -> CompletableFuture.supplyAsync(productService::getProducts);
    }
}
