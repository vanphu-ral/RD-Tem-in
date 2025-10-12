package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.InfoTemDetailService;
import com.mycompany.renderQr.domain.InfoTemDetailResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/info-tem-detail")
public class InfoTemDetailController {

    @Autowired
    private InfoTemDetailService infoTemDetailService;

    @GetMapping("")
    public List<InfoTemDetailResponse> getDataDetail() {
        return infoTemDetailService.getInfoTemDetail();
    }
}
