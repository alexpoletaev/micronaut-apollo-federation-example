package com.example.config;

import com.apollographql.federation.graphqljava.Federation;
import com.apollographql.federation.graphqljava._Entity;
import com.apollographql.federation.graphqljava.tracing.FederatedTracingInstrumentation;
import com.example.graphql.ReviewDataFetcher;
import com.example.graphql.fetcher.ReviewsByProductSkuDataFetcher;
import com.example.model.Product;
import com.example.model.Review;
import com.example.service.ReviewService;
import graphql.GraphQL;
import graphql.language.*;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.micronaut.context.annotation.Factory;
import io.micronaut.core.io.ResourceResolver;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Factory
public class GraphQLFactory {

    private static final String PATH = "classpath:schema.graphqls";

    private final ReviewDataFetcher reviewDataFetcher;

    private final ReviewsByProductSkuDataFetcher reviewsByProductSkuDataFetcher;

    private final ReviewService reviewService;

    private final ResourceResolver resourceResolver;

    @Singleton
    public GraphQL graphQL() {
        SchemaParser schemaParser = new SchemaParser();

        // Parse the schema.
        TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();
        typeRegistry.merge(schemaParser.parse(new BufferedReader(new InputStreamReader(
                resourceResolver.getResourceAsStream(PATH).orElseThrow()))));

        // Create the runtime wiring.
        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", typeWiring -> typeWiring
                        .dataFetcher("reviews", reviewDataFetcher.reviews()))
                .type("Query", typeWiring -> typeWiring
                        .dataFetcher("reviewsByProductSku", reviewsByProductSkuDataFetcher))
                .build();

        // Create the executable schema.
        GraphQLSchema graphQLSchema = Federation.transform(typeRegistry, runtimeWiring)
                .fetchEntities(env -> env.<List<Map<String, Object>>>getArgument(_Entity.argumentName)
                    .stream()
                        .map(value -> {
                            if ("Product".equals(value.get("__typename"))) {
                                final Object sku = value.get("sku");
                                List<Selection> selections = env.getField().getSelectionSet().getSelections();
                                List<Argument> arguments = ((Field) ((InlineFragment) selections.get(0)).getSelectionSet().getSelections().get(0)).getArguments();
                                String filter = arguments.isEmpty() ? null : ((StringValue) arguments.get(0).getValue()).getValue();
                                if (sku instanceof String) {
                                    return reviewService.getProductReviews((String) sku, filter);
                                    //return reviewService.getProductReviewsAsMap((String) sku, filter);
                                }
                            }
                            return null;
                        })
                        .collect(Collectors.toList()))
                .resolveEntityType(env -> {
                    final Object src = env.getObject();
                    if (src instanceof Product) {
                        return env.getSchema().getObjectType("Product");
                    }

                    if (src instanceof Map) {
                        return env.getSchema().getObjectType("Product");
                    }

                    if (src instanceof List && ((List<?>) src).get(0) instanceof Review) {
                        return env.getSchema().getObjectType("Review");
                    }

                    return null;
                })
                .build();

        // Return the GraphQL bean.
        return GraphQL.newGraphQL(graphQLSchema)
                .instrumentation(new FederatedTracingInstrumentation())
                .build();
    }
}
