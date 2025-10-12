package com.mycompany.myapp.graphql;

import com.mycompany.myapp.service.ListProductOfRequestService;
import com.mycompany.myapp.service.ListRequestCreateTemService;
import com.mycompany.renderQr.domain.ListProductOfRequestResponse;
import com.mycompany.renderQr.domain.ListRequestCreateTemResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Controller
public class RequestResolver {

    @Autowired
    private ListRequestCreateTemService service;

    @QueryMapping
    public List<ListRequestCreateTemResponse> listRequestCreateTems() {
        return service.getAll();
    }
}
