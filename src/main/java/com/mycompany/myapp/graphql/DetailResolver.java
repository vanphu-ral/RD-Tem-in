package com.mycompany.myapp.graphql;

import com.mycompany.myapp.service.InfoTemDetailService;
import com.mycompany.renderQr.domain.GenerateTemResponse;
import com.mycompany.renderQr.domain.InfoTemDetailResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
// QUAN TRỌNG: Import đúng annotation từ Spring GraphQL
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * GraphQL Resolver cho InfoTemDetail
 * QUAN TRỌNG: Phải dùng @Controller để Spring GraphQL nhận diện
 */
@Controller
public class DetailResolver {

    @Autowired
    private InfoTemDetailService service;

    // XÓA name = "infoTemDetails"
    @QueryMapping
    public List<InfoTemDetailResponse> infoTemDetails() {
        return service.getInfoTemDetail();
    }

    @QueryMapping
    public List<InfoTemDetailResponse> infoTemDetailsByRequestId(
        @Argument Integer requestId
    ) {
        return service.getTemDetailsByRequestId(Long.valueOf(requestId));
    }

    // XÓA name = "generateTem"
    //    @MutationMapping
    public GenerateTemResponse generateTem(@Argument Integer requestId) {
        return service.generateTemForAllProducts(Long.valueOf(requestId));
    }

    @QueryMapping
    public List<InfoTemDetailResponse> infoTemDetailsByProductId(
        @Argument Integer productId
    ) {
        System.out.println(
            "Query: infoTemDetailsByProductId with productId=" + productId
        );
        return service.getInfoTemDetailByProductId(productId.longValue());
    }
}
