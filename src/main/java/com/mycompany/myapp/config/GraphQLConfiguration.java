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
                    .dataFetcher("updateProductOfRequest", env -> {
                        Map<String, Object> inputMap = env.getArgument("input");
                        return requestResolver.updateProductOfRequest(inputMap);
                    })
                    //lay du lieu request
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
                        GenerateTemResponse response =
                            detailResolver.generateTem(requestId);
                        return response;
                    })
                    .dataFetcher("updateProductOfRequest", env -> {
                        Map<String, Object> inputMap = env.getArgument("input");
                        return requestResolver.updateProductOfRequest(inputMap);
                    })
                    .dataFetcher("updateStorageUnitForRequest", env -> {
                        Integer requestId = env.getArgument("requestId");
                        String storageUnit = env.getArgument("storageUnit");
                        return requestResolver.updateStorageUnitForRequest(
                            requestId,
                            storageUnit
                        );
                    })
            )
            .build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema schema = schemaGenerator.makeExecutableSchema(
            typeRegistry,
            runtimeWiring
        );

        //        log.info("GraphQL Schema loaded successfully!");
        //        log.info("Registered Queries: listRequestCreateTems, listProductOfRequests, infoTemDetails");
        //        log.info("Registered Mutations: generateTem");

        return schema;
    }
}
