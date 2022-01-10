package com.example.service;

import com.example.model.Product;
import com.example.model.Review;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class ReviewService {

    private final List<Review> reviews = List.of(
            new Review(UUID.fromString("d0358172-569f-4244-8953-93ee29423769"), "nice", 4.0f, "sku1"),
            new Review(UUID.fromString("826d5138-93c5-47e9-81fa-5e2a79e087e5"), "excellent", 5.0f, "sku1"));

    public List<Review> getReviews() {
        return reviews;
    }

    public List<Review> getReviewsByProductSku(String sku) {
        return reviews.stream().filter(r -> sku.equals(r.getProductSku())).collect(Collectors.toList());
    }

    public Product getProductReviews(String sku, String filter) {
        Product product = new Product();
        //product.setSku(sku);
        product.setReviews(reviews.stream()
                .filter(r -> sku.equals(r.getProductSku()))
                .filter(r -> filter == null || r.getText().equals(filter))
                .collect(Collectors.toList()));

        return product;
    }

    public Map<String, Object> getProductReviewsAsMap(String sku, String filter) {
        List<Review> reviewList = reviews.stream()
                .filter(r -> sku.equals(r.getProductSku()))
                .filter(r -> filter == null || r.getText().equals(filter))
                .collect(Collectors.toList());

        return Map.of("reviews", reviewList);
    }
}
