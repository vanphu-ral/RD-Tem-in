package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.PanacimMesApiService;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Proxy PanaCIM MesApi — tách tem (SplitQuantity) cho Xuất chuyển kho.
 */
@RestController
@RequestMapping("/api/panacim")
public class PanacimMesApiResource {

    private static final Logger log = LoggerFactory.getLogger(
        PanacimMesApiResource.class
    );

    private final PanacimMesApiService panacimMesApiService;

    public PanacimMesApiResource(PanacimMesApiService panacimMesApiService) {
        this.panacimMesApiService = panacimMesApiService;
    }

    /** Tách tem: payload ExecuteTransaction với transaction = "SplitQuantity" */
    @PostMapping("/split-quantity")
    public ResponseEntity<String> splitQuantity(
        @RequestBody Map<String, Object> payload
    ) {
        log.debug("REST request to proxy PanaCIM SplitQuantity");
        if (!"SplitQuantity".equals(payload.get("transaction"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                "Chỉ hỗ trợ transaction SplitQuantity."
            );
        }
        return panacimMesApiService.executeTransaction(payload);
    }
}
