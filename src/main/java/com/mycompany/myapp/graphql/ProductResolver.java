package com.mycompany.myapp.graphql;

import com.mycompany.myapp.service.ListProductOfRequestService;
import com.mycompany.renderQr.domain.ListProductOfRequestResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Controller
public class ProductResolver {

    @Autowired
    private ListProductOfRequestService service;

    @QueryMapping
    public List<ListProductOfRequestResponse> listProductOfRequests() {
        return service.getAll();
    }
}
