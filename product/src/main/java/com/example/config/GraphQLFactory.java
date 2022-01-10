package com.example.config;

import com.apollographql.federation.graphqljava.Federation;
import com.apollographql.federation.graphqljava._Entity;
import com.apollographql.federation.graphqljava.tracing.FederatedTracingInstrumentation;
import com.example.graphql.ProductDataFetcher;
import com.example.model.Product;
import com.example.service.ProductService;
import graphql.GraphQL;
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

    private final ProductDataFetcher productDataFetcher;

    private final ResourceResolver resourceResolver;

    private final ProductService productService;

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
                        .dataFetcher("productBySku", productDataFetcher.productBySku())
                        .dataFetcher("products", productDataFetcher.products()))
                .build();

        // Create the executable schema.
        GraphQLSchema graphQLSchema = Federation.transform(typeRegistry, runtimeWiring)
                .fetchEntities(env -> env.<List<Map<String, Object>>>getArgument(_Entity.argumentName)
                        .stream()
                        .map(value -> {
                            if ("Product".equals(value.get("__typename"))) {
                                final Object sku = value.get("sku");
                                if (sku instanceof String) {
                                    return productService.getProductBySku((String) sku);
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
                    return null;
                })
                .build();

        // Return the GraphQL bean.
        return GraphQL.newGraphQL(graphQLSchema)
                .instrumentation(new FederatedTracingInstrumentation())
                .build();
    }
}
