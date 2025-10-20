package com.mycompany.myapp.config;

import com.mycompany.myapp.graphql.DetailResolver;
import com.mycompany.myapp.graphql.ProductResolver;
import com.mycompany.myapp.graphql.RequestResolver;
import com.mycompany.renderQr.domain.GenerateTemResponse;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
public class GraphQLConfiguration {

    private static final Logger log = LoggerFactory.getLogger(
        GraphQLConfiguration.class
    );

    private final RequestResolver requestResolver;
    private final ProductResolver productResolver;
    private final DetailResolver detailResolver;

    public GraphQLConfiguration(
        RequestResolver requestResolver,
        ProductResolver productResolver,
        DetailResolver detailResolver
    ) {
        this.requestResolver = requestResolver;
        this.productResolver = productResolver;
        this.detailResolver = detailResolver;

        // THÊM LOG ĐỂ KIỂM TRA
        log.info("==========================================");
        log.info("GraphQLConfiguration initialized");
        log.info("DetailResolver: {}", detailResolver);
        log.info("==========================================");
    }

    @Bean
    public GraphQL graphQL() throws IOException {
        log.info("==========================================");
        log.info("Initializing GraphQL manually...");
        log.info("==========================================");
        return GraphQL.newGraphQL(graphQLSchema()).build();
    }

    private GraphQLSchema graphQLSchema() throws IOException {
        PathMatchingResourcePatternResolver resolver =
            new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource(
            "classpath:graphql/schema.graphqls"
        );

        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeRegistry;

        try (
            InputStreamReader reader = new InputStreamReader(
                resource.getInputStream()
            );
        ) {
            typeRegistry = schemaParser.parse(reader);
        }

        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
            // QUERY
            .type("Query", builder ->
                builder
                    .dataFetcher("listRequestCreateTems", env -> {
                        log.debug("Query: listRequestCreateTems called");
                        return requestResolver.listRequestCreateTems();
                    })
                    .dataFetcher("listProductOfRequests", env -> {
                        log.debug("Query: listProductOfRequests called");
                        return productResolver.listProductOfRequests();
                    })
                    .dataFetcher("infoTemDetails", env -> {
                        log.debug("Query: infoTemDetails called");
                        return detailResolver.infoTemDetails();
                    })
                    .dataFetcher("infoTemDetailsByProductId", env -> {
                        Integer productId = env.getArgument("productId");
                        return detailResolver.infoTemDetailsByProductId(
                            productId
                        );
                    })
                    .dataFetcher("infoTemDetailsByRequestId", env -> {
                        Integer requestId = env.getArgument("requestId");
                        return detailResolver.infoTemDetailsByRequestId(
                            requestId
                        );
                    })
                    // lay du lieu request
                    .dataFetcher("getProductOfRequestByRequestId", env -> {
                        Integer requestId = env.getArgument("requestId");
                        return requestResolver.listProductOfRequestByRequestId(
                            requestId
                        );
                    })
            )
            // MUTATION - THÊM PHẦN NÀY!
            .type("Mutation", builder ->
                builder
                    .dataFetcher("generateTem", env -> {
                        Integer requestId = env.getArgument("requestId");

                        try {
                            GenerateTemResponse response =
                                detailResolver.generateTem(requestId);

                            if (response == null) {
                                return new GenerateTemResponse(
                                    false,
                                    "Resolver returned null",
                                    0
                                );
                            }

                            return response;
                        } catch (Exception e) {
                            return new GenerateTemResponse(
                                false,
                                "Error: " + e.getMessage(),
                                0
                            );
                        }
                    })
                    .dataFetcher("createRequestAndProducts", env -> {
                        Map<String, Object> inputMap = env.getArgument("input");
                        log.info("Input map: {}", inputMap);

                        try {
                            com.mycompany.myapp.graphql.dto.CreateRequestWithProductsInput input =
                                convertMapToInput(inputMap);

                            log.info(
                                "Converted input - vendor: {}, userData5: {}, products count: {}",
                                input.getVendor(),
                                input.getUserData5(),
                                input.getProducts() != null
                                    ? input.getProducts().size()
                                    : 0
                            );

                            com.mycompany.myapp.graphql.dto.CreateRequestWithProductsResponse response =
                                productResolver.createRequestAndProducts(input);

                            if (response == null) {
                                log.error(
                                    "ProductResolver.createRequestAndProducts returned NULL!"
                                );
                                throw new RuntimeException(
                                    "Resolver returned null response"
                                );
                            }

                            log.info(
                                "Response: requestId={}, products count={}, message={}",
                                response.getRequestId(),
                                response.getProducts() != null
                                    ? response.getProducts().size()
                                    : 0,
                                response.getMessage()
                            );

                            return response;
                        } catch (Exception e) {
                            log.error(
                                "Error in createRequestAndProducts DataFetcher: {}",
                                e.getMessage(),
                                e
                            );
                            throw e;
                        }
                    })
                    .dataFetcher("updateStorageUnitForRequest", env -> {
                        Integer requestId = env.getArgument("requestId");
                        String storageUnit = env.getArgument("storageUnit");
                        return requestResolver.updateStorageUnitForRequest(
                            requestId,
                            storageUnit
                        );
                    })
                    .dataFetcher("deleteProduct", env -> {
                        Integer productId = env.getArgument("productId");
                        return requestResolver.deleteProduct(productId);
                    })
                    .dataFetcher("deleteRequest", env -> {
                        Integer requestId = env.getArgument("requestId");
                        return requestResolver.deleteRequest(requestId);
                    })
                    .dataFetcher("updateProductOfRequest", env -> {
                        Map<String, Object> inputMap = env.getArgument("input");
                        return requestResolver.updateProductOfRequest(inputMap);
                    })
            )
            .build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema schema = schemaGenerator.makeExecutableSchema(
            typeRegistry,
            runtimeWiring
        );

        // log.info("GraphQL Schema loaded successfully!");
        // log.info("Registered Queries: listRequestCreateTems, listProductOfRequests,
        // infoTemDetails");
        // log.info("Registered Mutations: generateTem");

        return schema;
    }

    private com.mycompany.myapp.graphql.dto.CreateRequestWithProductsInput convertMapToInput(
        java.util.Map<String, Object> inputMap
    ) {
        com.mycompany.myapp.graphql.dto.CreateRequestWithProductsInput input =
            new com.mycompany.myapp.graphql.dto.CreateRequestWithProductsInput();

        input.setVendor((String) inputMap.get("vendor"));
        input.setVendorName((String) inputMap.get("vendorName"));
        input.setUserData5((String) inputMap.get("userData5"));
        input.setCreatedBy((String) inputMap.get("createdBy"));

        @SuppressWarnings("unchecked")
        java.util.List<java.util.Map<String, Object>> productsMap =
            (java.util.List<java.util.Map<String, Object>>) inputMap.get(
                "products"
            );
        java.util.List<
            com.mycompany.myapp.graphql.dto.CreateProductInput
        > products = new java.util.ArrayList<>();

        if (productsMap != null) {
            for (java.util.Map<String, Object> productMap : productsMap) {
                com.mycompany.myapp.graphql.dto.CreateProductInput productInput =
                    new com.mycompany.myapp.graphql.dto.CreateProductInput();

                productInput.setRequestCreateTemId(
                    (Integer) productMap.get("requestCreateTemId")
                );
                productInput.setProductName(
                    (String) productMap.get("productName")
                );
                productInput.setSapCode((String) productMap.get("sapCode"));
                productInput.setTemQuantity(
                    (Integer) productMap.get("temQuantity")
                );
                productInput.setPartNumber(
                    (String) productMap.get("partNumber")
                );
                productInput.setLot((String) productMap.get("lot"));
                productInput.setInitialQuantity(
                    (Integer) productMap.get("initialQuantity")
                );
                productInput.setVendor((String) productMap.get("vendor"));
                productInput.setVendorName(
                    (String) productMap.get("vendorName")
                );
                productInput.setUserData1((String) productMap.get("userData1"));
                productInput.setUserData2((String) productMap.get("userData2"));
                productInput.setUserData3((String) productMap.get("userData3"));
                productInput.setUserData4((String) productMap.get("userData4"));
                productInput.setUserData5((String) productMap.get("userData5"));
                productInput.setStorageUnit(
                    (String) productMap.get("storageUnit")
                );
                productInput.setExpirationDate(
                    (String) productMap.get("expirationDate")
                );
                productInput.setManufacturingDate(
                    (String) productMap.get("manufacturingDate")
                );
                productInput.setArrivalDate(
                    (String) productMap.get("arrivalDate")
                );

                products.add(productInput);
            }
        }

        input.setProducts(products);
        return input;
    }
}
