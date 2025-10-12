package com.mycompany.myapp.web.rest;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/graphql")
public class GraphResource {

    private static final Logger log = LoggerFactory.getLogger(
        GraphResource.class
    );

    private final GraphQL graphQL;

    public GraphResource(GraphQL graphQL) {
        this.graphQL = graphQL;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> executeQuery(
        @RequestBody Map<String, Object> request
    ) {
        log.info("GraphQL request received: {}", request.get("query"));

        String query = (String) request.get("query");
        String operationName = (String) request.get("operationName");
        Map<String, Object> variables = (Map<String, Object>) request.get(
            "variables"
        );

        if (variables == null) {
            variables = new HashMap<>();
        }

        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
            .query(query)
            .operationName(operationName)
            .variables(variables)
            .build();

        ExecutionResult executionResult = graphQL.execute(executionInput);

        Map<String, Object> result = new HashMap<>();
        result.put("data", executionResult.getData());

        if (
            executionResult.getErrors() != null &&
            !executionResult.getErrors().isEmpty()
        ) {
            result.put("errors", executionResult.getErrors());
        }

        return ResponseEntity.ok(result);
    }
}
