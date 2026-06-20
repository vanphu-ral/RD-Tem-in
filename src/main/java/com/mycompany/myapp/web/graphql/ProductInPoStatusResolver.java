package com.mycompany.myapp.web.graphql;

import com.mycompany.myapp.repository.ProductInPoStatusRepository;
import com.mycompany.myapp.service.dto.ProductInPoStatusDTO;
import com.mycompany.renderQr.domain.UpdateResponse;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ProductInPoStatusResolver {

    @Autowired
    private ProductInPoStatusRepository productInPoStatusRepository;

    @QueryMapping
    public ProductInPoStatusPage productInPoStatuses(
        @Argument String keyword,
        @Argument String sapCode,
        @Argument String userData5,
        @Argument String whsCode,
        @Argument Integer page,
        @Argument Integer size
    ) {
        Page<ProductInPoStatusDTO> result;
        PageRequest pageRequest = PageRequest.of(
            page != null ? page : 0,
            size != null ? size : 20
        );

        if (sapCode != null && !sapCode.isEmpty()) {
            result = productInPoStatusRepository.findBySapCode(
                sapCode,
                pageRequest
            );
        } else if (userData5 != null && !userData5.isEmpty()) {
            result = productInPoStatusRepository.findByUserData5(
                userData5,
                pageRequest
            );
        } else if (whsCode != null && !whsCode.isEmpty()) {
            result = productInPoStatusRepository.findByWhsCode(
                whsCode,
                pageRequest
            );
        } else if (keyword != null && !keyword.isEmpty()) {
            result = productInPoStatusRepository.search(keyword, pageRequest);
        } else {
            result = productInPoStatusRepository.findAll(pageRequest);
        }

        ProductInPoStatusPage pageObj = new ProductInPoStatusPage();
        pageObj.setContent(result.getContent());
        pageObj.setTotalElements((int) result.getTotalElements());
        pageObj.setPage(result.getNumber());
        pageObj.setSize(result.getSize());
        pageObj.setTotalPages(result.getTotalPages());
        return pageObj;
    }

    @QueryMapping
    public ProductInPoStatusPage productInPoStatusBySapCode(
        @Argument String sapCode,
        @Argument Integer page,
        @Argument Integer size
    ) {
        Page<ProductInPoStatusDTO> result =
            productInPoStatusRepository.findBySapCode(
                sapCode,
                PageRequest.of(
                    page != null ? page : 0,
                    size != null ? size : 20
                )
            );
        ProductInPoStatusPage pageObj = new ProductInPoStatusPage();
        pageObj.setContent(result.getContent());
        pageObj.setTotalElements((int) result.getTotalElements());
        pageObj.setPage(result.getNumber());
        pageObj.setSize(result.getSize());
        pageObj.setTotalPages(result.getTotalPages());
        return pageObj;
    }

    @QueryMapping
    public List<ProductInPoStatusDTO> productInPoStatusByUserData5(
        @Argument String userData5
    ) {
        return productInPoStatusRepository.findByUserData5List(userData5);
    }

    @QueryMapping
    public ProductInPoStatusPage productInPoStatusByWhsCode(
        @Argument String whsCode,
        @Argument Integer page,
        @Argument Integer size
    ) {
        Page<ProductInPoStatusDTO> result =
            productInPoStatusRepository.findByWhsCode(
                whsCode,
                PageRequest.of(
                    page != null ? page : 0,
                    size != null ? size : 20
                )
            );
        ProductInPoStatusPage pageObj = new ProductInPoStatusPage();
        pageObj.setContent(result.getContent());
        pageObj.setTotalElements((int) result.getTotalElements());
        pageObj.setPage(result.getNumber());
        pageObj.setSize(result.getSize());
        pageObj.setTotalPages(result.getTotalPages());
        return pageObj;
    }

    @QueryMapping
    public ProductInPoStatusPage productInPoStatusByListRequestCreateTemId(
        @Argument Integer listRequestCreateTemId,
        @Argument Integer page,
        @Argument Integer size
    ) {
        Page<ProductInPoStatusDTO> result =
            productInPoStatusRepository.findByListRequestCreateTemId(
                listRequestCreateTemId.longValue(),
                PageRequest.of(
                    page != null ? page : 0,
                    size != null ? size : 20
                )
            );
        ProductInPoStatusPage pageObj = new ProductInPoStatusPage();
        pageObj.setContent(result.getContent());
        pageObj.setTotalElements((int) result.getTotalElements());
        pageObj.setPage(result.getNumber());
        pageObj.setSize(result.getSize());
        pageObj.setTotalPages(result.getTotalPages());
        return pageObj;
    }

    @QueryMapping
    public ProductInPoStatusDTO productInPoStatus(@Argument Integer id) {
        return productInPoStatusRepository
            .findById(id.longValue())
            .orElse(null);
    }

    @MutationMapping
    public ProductInPoStatusDTO createProductInPoStatus(
        @Argument("input") Map<String, Object> inputMap
    ) {
        ProductInPoStatusDTO dto = new ProductInPoStatusDTO();
        dto.setSapCode((String) inputMap.get("sapCode"));
        dto.setProductName((String) inputMap.get("productName"));
        dto.setWhsCode((String) inputMap.getOrDefault("whsCode", null));
        dto.setUserData5((String) inputMap.get("userData5"));
        dto.setCreateBy((String) inputMap.get("createBy"));

        Object quantityObj = inputMap.get("quantityByPo");
        if (quantityObj instanceof Number) {
            dto.setQuantityByPo(((Number) quantityObj).intValue());
        }

        dto.setVendor((String) inputMap.get("vendor"));
        dto.setVendorName((String) inputMap.get("vendorName"));
        dto.setUOMCode((String) inputMap.getOrDefault("uomcode", null));

        Object listRequestCreateTemIdObj = inputMap.get(
            "listRequestCreateTemId"
        );
        if (listRequestCreateTemIdObj instanceof Number) {
            dto.setListRequestCreateTemId(
                ((Number) listRequestCreateTemIdObj).longValue()
            );
        }

        return productInPoStatusRepository.insert(dto);
    }

    @MutationMapping
    public ProductInPoStatusDTO updateProductInPoStatus(
        @Argument("input") Map<String, Object> inputMap
    ) {
        Object idObj = inputMap.get("id");
        if (idObj == null) {
            return null;
        }
        Long id = idObj instanceof Number
            ? ((Number) idObj).longValue()
            : Long.valueOf(idObj.toString());

        ProductInPoStatusDTO existing = productInPoStatusRepository
            .findById(id)
            .orElse(null);
        if (existing == null) {
            return null;
        }

        if (inputMap.containsKey("sapCode")) {
            existing.setSapCode((String) inputMap.get("sapCode"));
        }
        if (inputMap.containsKey("productName")) {
            existing.setProductName((String) inputMap.get("productName"));
        }
        if (inputMap.containsKey("whsCode")) {
            existing.setWhsCode((String) inputMap.get("whsCode"));
        }
        if (inputMap.containsKey("userData5")) {
            existing.setUserData5((String) inputMap.get("userData5"));
        }
        if (inputMap.containsKey("createBy")) {
            existing.setCreateBy((String) inputMap.get("createBy"));
        }
        if (inputMap.containsKey("quantityByPo")) {
            Object quantityObj = inputMap.get("quantityByPo");
            if (quantityObj instanceof Number) {
                existing.setQuantityByPo(((Number) quantityObj).intValue());
            }
        }
        if (inputMap.containsKey("vendor")) {
            existing.setVendor((String) inputMap.get("vendor"));
        }
        if (inputMap.containsKey("vendorName")) {
            existing.setVendorName((String) inputMap.get("vendorName"));
        }
        if (inputMap.containsKey("uomcode")) {
            existing.setUOMCode((String) inputMap.get("uomcode"));
        }
        if (inputMap.containsKey("listRequestCreateTemId")) {
            Object listRequestCreateTemIdObj = inputMap.get(
                "listRequestCreateTemId"
            );
            if (listRequestCreateTemIdObj instanceof Number) {
                existing.setListRequestCreateTemId(
                    ((Number) listRequestCreateTemIdObj).longValue()
                );
            } else if (listRequestCreateTemIdObj == null) {
                existing.setListRequestCreateTemId(null);
            }
        }

        return productInPoStatusRepository.update(existing).orElse(null);
    }

    @MutationMapping
    public UpdateResponse deleteProductInPoStatus(@Argument Integer id) {
        boolean deleted = productInPoStatusRepository.deleteById(
            id.longValue()
        );
        return new UpdateResponse(
            deleted,
            deleted ? "Xóa thành công" : "Xóa thất bại"
        );
    }
}
