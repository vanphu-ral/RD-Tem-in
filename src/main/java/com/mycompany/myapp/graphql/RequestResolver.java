package com.mycompany.myapp.graphql;

import com.mycompany.myapp.service.ListProductOfRequestService;
import com.mycompany.myapp.service.ListRequestCreateTemService;
import com.mycompany.renderQr.domain.*;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Controller
public class RequestResolver {

    private static final Logger log = LoggerFactory.getLogger(
        RequestResolver.class
    );

    @Autowired
    private ListRequestCreateTemService service;

    @Autowired
    private ListProductOfRequestService productService;

    @QueryMapping
    public List<ListRequestCreateTemResponse> listRequestCreateTems() {
        return service.getAll();
    }

    @QueryMapping(name = "getProductOfRequestByRequestId")
    public List<ListProductOfRequest> listProductOfRequestByRequestId(
        @Argument Integer requestId
    ) {
        return productService.findByRequestCreateTemId(Long.valueOf(requestId));
    }

    // update
    @MutationMapping(name = "updateProductOfRequest")
    public UpdateResponse updateProductOfRequest(
        @Argument("input") Map<String, Object> inputMap
    ) {
        UpdateProductInput input = convertToInput(inputMap); // chỉ cần map 1 lần
        boolean updated = productService.updateProduct(input);
        return new UpdateResponse(
            updated,
            updated ? "Cập nhật thành công" : "Cập nhật thất bại"
        );
    }

    private UpdateProductInput convertToInput(Map<String, Object> map) {
        UpdateProductInput input = new UpdateProductInput();

        try {
            Object idObj = map.get("id");
            if (idObj != null) {
                input.setId(Long.valueOf(idObj.toString()));
            } else {
                log.warn("Thiếu trường 'id'");
            }

            input.setSapCode((String) map.getOrDefault("sapCode", null));
            input.setPartNumber((String) map.getOrDefault("partNumber", null));
            input.setLot((String) map.getOrDefault("lot", null));
            input.setVendor((String) map.getOrDefault("vendor", null));
            input.setVendorName((String) map.getOrDefault("vendorName", null));
            input.setUserData1((String) map.getOrDefault("userData1", null));
            input.setUserData2((String) map.getOrDefault("userData2", null));
            input.setUserData3((String) map.getOrDefault("userData3", null));
            input.setUserData4((String) map.getOrDefault("userData4", null));
            input.setUserData5((String) map.getOrDefault("userData5", null));
            input.setStorageUnit(
                (String) map.getOrDefault("storageUnit", null)
            );

            Object temQuantityObj = map.get("temQuantity");
            if (temQuantityObj instanceof Number) {
                input.setTemQuantity(((Number) temQuantityObj).intValue());
            } else {
                log.warn("temQuantity không phải kiểu số: {}", temQuantityObj);
            }

            Object requestCreateTemIdObj = map.get("requestCreateTemId");
            if (requestCreateTemIdObj instanceof Number) {
                input.setRequestCreateTemId(
                    ((Number) requestCreateTemIdObj).longValue()
                );
            } else {
                log.warn(
                    "requestCreateTemId không phải kiểu số: {}",
                    requestCreateTemIdObj
                );
            }

            Object quantityObj = map.get("initialQuantity");
            if (quantityObj != null) {
                input.setInitialQuantity(Long.valueOf(quantityObj.toString()));
            } else {
                log.warn("Thiếu trường 'initialQuantity'");
            }

            Object printsObj = map.get("numberOfPrints");
            if (printsObj != null) {
                input.setNumberOfPrints(Integer.valueOf(printsObj.toString()));
            } else {
                log.warn("Thiếu trường 'numberOfPrints'");
            }

            input.setExpirationDate(
                (String) map.getOrDefault("expirationDate", null)
            );
            input.setManufacturingDate(
                (String) map.getOrDefault("manufacturingDate", null)
            );
            input.setArrivalDate(
                (String) map.getOrDefault("arrivalDate", null)
            );
            Object uploadPanacimObj = map.get("UploadPanacim");
            if (uploadPanacimObj instanceof Boolean) {
                input.setUploadPanacim((Boolean) uploadPanacimObj);
            } else {
                log.warn(
                    "UploadPanacim không phải kiểu boolean: {}",
                    uploadPanacimObj
                );
            }

            log.info("Convert thành công: {}", input);
        } catch (Exception e) {
            log.error("Lỗi khi convert input map: {}", e.getMessage(), e);
            throw e;
        }

        return input;
    }

    // Update location by request id
    @MutationMapping(name = "updateStorageUnitForRequest")
    public UpdateResponse updateStorageUnitForRequest(
        @Argument Integer requestId,
        @Argument String storageUnit
    ) {
        return productService.updateStorageUnitForRequest(
            requestId.longValue(),
            storageUnit
        );
    }

    //delete product
    @MutationMapping(name = "deleteProduct")
    public UpdateResponse deleteProduct(
        @Argument("productId") Integer productId
    ) {
        if (productId == null) {
            return new UpdateResponse(false, "productId bị null từ FE");
        }

        try {
            productService.deleteProductById(productId.longValue());
            return new UpdateResponse(true, "Xóa thành công");
        } catch (Exception e) {
            return new UpdateResponse(false, "Lỗi khi xóa: " + e.getMessage());
        }
    }

    //xoa request
    @MutationMapping(name = "deleteRequest")
    public UpdateResponse deleteRequest(@Argument Integer requestId) {
        try {
            service.deleteRequestCascade(Long.valueOf(requestId));
            return new UpdateResponse(
                true,
                "Xóa yêu cầu và toàn bộ dữ liệu liên quan thành công"
            );
        } catch (Exception e) {
            return new UpdateResponse(false, "Lỗi khi xóa: " + e.getMessage());
        }
    }
}
