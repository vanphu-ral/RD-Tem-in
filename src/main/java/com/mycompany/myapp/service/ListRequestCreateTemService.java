package com.mycompany.myapp.service;

import com.mycompany.renderQr.domain.ListProductOfRequest;
import com.mycompany.renderQr.domain.ListProductOfRequestResponse;
import com.mycompany.renderQr.domain.ListRequestCreateTem;
import com.mycompany.renderQr.domain.ListRequestCreateTemResponse;
import com.mycompany.renderQr.domain.RequestCreateTemPage;
import com.mycompany.renderQr.domain.UpdateResponse;
import com.mycompany.renderQr.repository.InfoTemDetailRepository;
import com.mycompany.renderQr.repository.ListProductOfRequestRepository;
import com.mycompany.renderQr.repository.ListRequestCreateTemRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListRequestCreateTemService {

    @Autowired
    private ListRequestCreateTemRepository repository;

    @Autowired
    private ListProductOfRequestRepository productRepository;

    @Autowired
    private InfoTemDetailRepository detailRepository;

    @Transactional(readOnly = true)
    public RequestCreateTemPage getAll(
        String search,
        String status,
        String vendor,
        String vendorName,
        String userData5,
        String createdBy,
        String createdDate,
        Integer page,
        Integer size
    ) {
        int safePage = page != null ? Math.max(page, 0) : 0;
        int safeSize = size != null ? Math.min(Math.max(size, 1), 100) : 25;

        String normalizedSearch = normalizeSearch(search);
        String normalizedStatus = normalizeSearch(status);
        String normalizedVendor = normalizeSearch(vendor);
        String normalizedVendorName = normalizeSearch(vendorName);
        String normalizedUserData5 = normalizeSearch(userData5);
        String normalizedCreatedBy = normalizeSearch(createdBy);

        String searchLike = like(normalizedSearch);
        String vendorLike = like(normalizedVendor);
        String vendorNameLike = like(normalizedVendorName);
        String userData5Like = like(normalizedUserData5);
        String createdByLike = like(normalizedCreatedBy);

        LocalDateTime createdDateStart = parseCreatedDate(createdDate);
        LocalDateTime createdDateEnd = createdDateStart != null
            ? createdDateStart.plusDays(1)
            : null;

        Pageable pageable = PageRequest.of(
            safePage,
            safeSize,
            Sort.by(Sort.Order.desc("createdDate"), Sort.Order.desc("id"))
        );

        Page<ListRequestCreateTem> result = repository.search(
            normalizedSearch,
            searchLike,
            normalizedStatus,
            normalizedVendor,
            vendorLike,
            normalizedVendorName,
            vendorNameLike,
            normalizedUserData5,
            userData5Like,
            normalizedCreatedBy,
            createdByLike,
            createdDateStart,
            createdDateEnd,
            pageable
        );

        return new RequestCreateTemPage(
            result.getContent(),
            result.getTotalElements(),
            result.getNumber(),
            result.getSize(),
            result.getTotalPages()
        );
    }

    private String normalizeSearch(String value) {
        return value != null ? value.trim() : null;
    }

    private String like(String value) {
        return value != null && !value.isBlank()
            ? "%" + value.toLowerCase() + "%"
            : null;
    }

    private LocalDateTime parseCreatedDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(value).atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                "createdDate có định dạng không hợp lệ: " + value
            );
        }
    }

    public List<ListRequestCreateTemResponse> getAll() {
        return repository.findAllProjectedBy();
    }

    @Transactional
    public ListRequestCreateTem createRequest(
        String vendor,
        String vendorName,
        String userData5,
        String createdBy,
        int numberProduction,
        long totalQuantity,
        LocalDateTime createdDate,
        Boolean type,
        LocalDateTime entryDate,
        String whsCode
    ) {
        ListRequestCreateTem request = new ListRequestCreateTem();
        request.setVendor(vendor);
        request.setVendorName(vendorName);
        request.setUserData5(userData5);
        request.setCreatedBy(createdBy != null ? createdBy : "system");
        request.setNumberProduction((short) numberProduction);
        request.setTotalQuantity(totalQuantity);
        request.setWhsCode(whsCode);
        if ("-".equals(userData5)) {
            request.setStatus("chưa có PO");
        } else {
            request.setStatus("Bản nháp");
        }
        //        System.out.println("Input vendorName: " + vendorName);
        //        System.out.println("vendorName is null? " + (vendorName == null));
        //        System.out.println(
        //            "vendorName is blank? " +
        //            (vendorName != null && vendorName.isBlank())
        //        );
        if (createdDate != null) {
            request.setCreatedDate(createdDate);
        } else {
            request.setCreatedDate(LocalDateTime.now());
        }
        request.setType(type);
        request.setEntryDate(entryDate);

        ListRequestCreateTem savedRequest = repository.save(request);

        // Force flush to ensure ID is generated immediately
        repository.flush();
        //        System.out.println(
        //            "After save - vendorName from DB: " + savedRequest.getVendorName()
        //        );

        // Debug: Ensure the ID was generated
        if (savedRequest.getId() == null) {
            throw new RuntimeException(
                "Failed to generate ID for new request after flush"
            );
        }

        //        System.out.println(
        //            "Saved request with generated ID: " + savedRequest.getId()
        //        );
        return savedRequest;
    }

    @Transactional
    public UpdateResponse updateRequest(Long id, Map<String, Object> fields) {
        if (id == null) {
            throw new IllegalArgumentException("id không được null");
        }

        Optional<ListRequestCreateTem> requestOpt = repository.findById(id);
        if (requestOpt.isEmpty()) {
            throw new IllegalArgumentException(
                "Không tìm thấy bản ghi list_request_create_tem có id=" + id
            );
        }

        ListRequestCreateTem request = requestOpt.get();
        if (fields != null) {
            if (fields.containsKey("vendor")) {
                request.setVendor((String) fields.get("vendor"));
            }
            if (fields.containsKey("vendorName")) {
                request.setVendorName((String) fields.get("vendorName"));
            }
            if (fields.containsKey("userData5")) {
                request.setUserData5((String) fields.get("userData5"));
            }
            if (fields.containsKey("createdBy")) {
                request.setCreatedBy((String) fields.get("createdBy"));
            }
            if (fields.containsKey("numberProduction")) {
                Object value = fields.get("numberProduction");
                request.setNumberProduction(
                    value == null ? null : ((Number) value).shortValue()
                );
            }
            if (fields.containsKey("totalQuantity")) {
                Object value = fields.get("totalQuantity");
                request.setTotalQuantity(
                    value == null ? null : ((Number) value).longValue()
                );
            }
            if (fields.containsKey("status")) {
                request.setStatus((String) fields.get("status"));
            } else if (fields.containsKey("userData5")) {
                request.setStatus(
                    "-".equals(fields.get("userData5"))
                        ? "chưa có PO"
                        : "Bản nháp"
                );
            }
            if (fields.containsKey("createdDate")) {
                request.setCreatedDate(
                    parseLocalDateTime(fields.get("createdDate"), "createdDate")
                );
            }
            if (fields.containsKey("type")) {
                request.setType((Boolean) fields.get("type"));
            }
            if (fields.containsKey("entryDate")) {
                request.setEntryDate(
                    parseLocalDateTime(fields.get("entryDate"), "entryDate")
                );
            }
            if (fields.containsKey("WhsCode")) {
                request.setWhsCode((String) fields.get("WhsCode"));
            } else if (fields.containsKey("whsCode")) {
                request.setWhsCode((String) fields.get("whsCode"));
            }
        }

        repository.save(request);
        return new UpdateResponse(true, "Cập nhật yêu cầu thành công");
    }

    private LocalDateTime parseLocalDateTime(Object value, String fieldName) {
        if (value == null) {
            return null;
        }

        String dateValue = value.toString();
        if (dateValue.isBlank()) {
            return null;
        }

        try {
            if (dateValue.length() == 10 && dateValue.charAt(4) == '-') {
                return LocalDate.parse(dateValue).atStartOfDay();
            }
            return LocalDateTime.parse(dateValue);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                fieldName + " có định dạng không hợp lệ: " + dateValue
            );
        }
    }

    @Transactional
    public void deleteRequest(Long id) {
        repository.deleteById(id);
    }

    //update so luong sau khi xoa
    @Transactional
    public void updateRequestProductCount(Long requestId) {
        List<ListProductOfRequest> remainingProducts =
            productRepository.findByRequestCreateTemId(requestId);

        short newCount = (short) remainingProducts.size();

        long newTotalQuantity = remainingProducts
            .stream()
            .mapToLong(p ->
                p.getInitialQuantity() != null ? p.getInitialQuantity() : 0
            )
            .sum();

        Optional<ListRequestCreateTem> requestOpt = repository.findById(
            requestId
        );
        if (requestOpt.isPresent()) {
            ListRequestCreateTem request = requestOpt.get();
            request.setNumberProduction(newCount);
            request.setTotalQuantity(newTotalQuantity);
            repository.save(request);
        }
    }

    //xu ly xoa req product detai
    @Transactional
    public void deleteRequestCascade(Long requestId) {
        // Lấy toàn bộ sản phẩm thuộc request
        List<ListProductOfRequest> products =
            productRepository.findByRequestCreateTemId(requestId);

        for (ListProductOfRequest product : products) {
            Long productId = product.getId();
            // Xóa bảng cháu
            detailRepository.deleteByProductOfRequestId(productId);
        }

        // Xóa bảng con
        productRepository.deleteByRequestCreateTemId(requestId);

        // Xóa bảng cha
        repository.deleteById((long) requestId);
    }
}
