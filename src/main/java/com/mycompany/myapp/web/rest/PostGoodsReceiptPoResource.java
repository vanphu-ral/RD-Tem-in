package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.PostGoodsReceiptPoService;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Proxy POST nhập kho SAP — tránh CORS khi FE gọi trực tiếp server SAP.
 */
@RestController
@RequestMapping("/api")
public class PostGoodsReceiptPoResource {

    private static final Logger log = LoggerFactory.getLogger(
        PostGoodsReceiptPoResource.class
    );

    private final PostGoodsReceiptPoService postGoodsReceiptPoService;

    public PostGoodsReceiptPoResource(
        PostGoodsReceiptPoService postGoodsReceiptPoService
    ) {
        this.postGoodsReceiptPoService = postGoodsReceiptPoService;
    }

    @PostMapping("/post-goods-receipt-po")
    public ResponseEntity<Object> postGoodsReceiptPo(
        @RequestBody Map<String, Object> payload
    ) {
        log.debug("REST request to proxy PostGoodsReceiptPO");
        return postGoodsReceiptPoService.forwardToSap(payload);
    }
}
