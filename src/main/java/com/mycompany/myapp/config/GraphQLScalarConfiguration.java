package com.mycompany.myapp.config;

import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

/**
 * Configuration for custom GraphQL scalars.
 * This registers the DateTime scalar with Spring GraphQL auto-configuration.
 */
@Configuration
public class GraphQLScalarConfiguration {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder.scalar(dateTimeScalar());
    }

    private GraphQLScalarType dateTimeScalar() {
        return GraphQLScalarType.newScalar()
            .name("DateTime")
            .description("DateTime scalar for LocalDateTime")
            .coercing(
                new Coercing<LocalDateTime, String>() {
                    @Override
                    public String serialize(Object dataFetcherResult)
                        throws CoercingSerializeException {
                        if (dataFetcherResult instanceof LocalDateTime) {
                            return ((LocalDateTime) dataFetcherResult).format(
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME
                            );
                        }
                        throw new CoercingSerializeException(
                            "Expected LocalDateTime"
                        );
                    }

                    @Override
                    public LocalDateTime parseValue(Object input)
                        throws CoercingParseValueException {
                        if (input instanceof String) {
                            try {
                                return LocalDateTime.parse(
                                    (String) input,
                                    DateTimeFormatter.ISO_LOCAL_DATE_TIME
                                );
                            } catch (Exception e) {
                                throw new CoercingParseValueException(
                                    "Invalid DateTime format: " + input
                                );
                            }
                        }
                        throw new CoercingParseValueException(
                            "Expected String"
                        );
                    }

                    @Override
                    public LocalDateTime parseLiteral(Object input)
                        throws CoercingParseLiteralException {
                        if (input instanceof String) {
                            try {
                                return LocalDateTime.parse(
                                    (String) input,
                                    DateTimeFormatter.ISO_LOCAL_DATE_TIME
                                );
                            } catch (Exception e) {
                                throw new CoercingParseLiteralException(
                                    "Invalid DateTime format: " + input
                                );
                            }
                        }
                        throw new CoercingParseLiteralException(
                            "Expected String"
                        );
                    }
                }
            )
            .build();
    }
}
