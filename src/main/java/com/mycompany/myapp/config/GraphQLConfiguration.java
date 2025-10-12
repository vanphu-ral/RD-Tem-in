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
            )
            // MUTATION - THÊM PHẦN NÀY!
            .type("Mutation", builder ->
                builder.dataFetcher("generateTem", env -> {
                    //                    log.info("==========================================");
                    //                    log.info("Mutation: generateTem called!");
                    //                    log.info("==========================================");

                    String storageUnit = env.getArgument("storageUnit");
                    //                    log.info("Argument storageUnit: {}", storageUnit);

                    try {
                        GenerateTemResponse response =
                            detailResolver.generateTem(storageUnit);

                        if (response == null) {
                            //                            log.error("DetailResolver.generateTem returned NULL!");
                            return new GenerateTemResponse(
                                false,
                                "Resolver returned null",
                                0
                            );
                        }

                        log.info(
                            "Response: success={}, totalTems={}",
                            response.isSuccess(),
                            response.getTotalTems()
                        );

                        return response;
                    } catch (Exception e) {
                        log.error(
                            "Error in generateTem DataFetcher: {}",
                            e.getMessage(),
                            e
                        );
                        return new GenerateTemResponse(
                            false,
                            "Error: " + e.getMessage(),
                            0
                        );
                    }
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
