package com.example.graphql.fetcher;

import com.example.model.Review;
import com.example.service.ReviewService;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Singleton
@RequiredArgsConstructor
public class ReviewsByProductSkuDataFetcher implements DataFetcher<List<Review>> {

    private final ReviewService reviewService;

    /**
     * This is called by the graphql engine to fetch the value.  The {@link DataFetchingEnvironment} is a composite
     * context object that tells you all you need to know about how to fetch a data value in graphql type terms.
     *
     * @param environment this is the data fetching environment which contains all the context you need to fetch a value
     * @return a value of type T. May be wrapped in a {@link DataFetcherResult}
     * @throws Exception to relieve the implementations from having to wrap checked exceptions. Any exception thrown
     *                   from a {@code DataFetcher} will eventually be handled by the registered {@link DataFetcherExceptionHandler}
     *                   and the related field will have a value of {@code null} in the result.
     */
    @Override
    public List<Review> get(DataFetchingEnvironment environment) throws Exception {
        String sku = environment.getArgument("sku");

        return reviewService.getReviewsByProductSku(sku);
    }
}
