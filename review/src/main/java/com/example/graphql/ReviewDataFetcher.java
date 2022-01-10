package com.example.graphql;

import com.example.model.Review;
import com.example.service.ReviewService;
import graphql.schema.DataFetcher;
import io.micronaut.context.annotation.Factory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Factory
public class ReviewDataFetcher {

    private final ReviewService reviewService;

    public DataFetcher<CompletableFuture<List<Review>>> reviews() {
        return dataFetchingEnvironment -> CompletableFuture.supplyAsync(reviewService::getReviews);
    }
}
