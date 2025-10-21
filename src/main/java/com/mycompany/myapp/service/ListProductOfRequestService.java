package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.CreateProductInput;
import com.mycompany.myapp.service.dto.CreateRequestWithProductsInput;
import com.mycompany.myapp.service.dto.CreateRequestWithProductsResponse;
import com.mycompany.myapp.web.graphql.RequestResolver;
import com.mycompany.renderQr.domain.*;
import com.mycompany.renderQr.domain.ListProductOfRequest;
import com.mycompany.renderQr.domain.ListProductOfRequestResponse;
import com.mycompany.renderQr.domain.ListRequestCreateTem;
import com.mycompany.renderQr.repository.InfoTemDetailRepository;
import com.mycompany.renderQr.repository.ListProductOfRequestRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListProductOfRequestService {

    private static final Logger log = LoggerFactory.getLogger(
        ListProductOfRequestService.class
    );

    @Autowired
    private ListProductOfRequestRepository repository;

    @Autowired
    private InfoTemDetailRepository detailRepository;

    @Autowired
    private ListRequestCreateTemService requestService;

    public List<ListProductOfRequestResponse> getAll() {
        return repository.findAllProjectedBy();
    }

    public List<ListProductOfRequest> findByRequestCreateTemId(Long requestId) {
        return repository.findByRequestCreateTemId(requestId);
    }

    // update all for product
    public boolean updateProduct(UpdateProductInput input) {
        Optional<ListProductOfRequest> optional = repository.findById(
            input.getId()
        );
        if (optional.isEmpty()) return false;

        ListProductOfRequest entity = optional.get();

        entity.setSapCode(input.getSapCode());
        entity.setPartNumber(input.getPartNumber());
        entity.setRequestCreateTemId(input.getRequestCreateTemId());
        entity.setTemQuantity(input.getTemQuantity());
        entity.setLot(input.getLot());
        entity.setInitialQuantity(input.getInitialQuantity());
        entity.setVendor(input.getVendor());
        entity.setVendorName(input.getVendorName());
        entity.setUserData1(input.getUserData1());
        entity.setUserData2(input.getUserData2());
        entity.setUserData3(input.getUserData3());
        entity.setUserData4(input.getUserData4());
        entity.setUserData5(input.getUserData5());
        entity.setStorageUnit(input.getStorageUnit());
        entity.setNumberOfPrints(input.getNumberOfPrints());

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        entity.setExpirationDate(
            LocalDateTime.parse(input.getExpirationDate(), formatter)
        );
        entity.setManufacturingDate(
            LocalDateTime.parse(input.getManufacturingDate(), formatter)
        );
        entity.setArrivalDate(
            LocalDateTime.parse(input.getArrivalDate(), formatter)
        );
        if (input.getUploadPanacim() != null) {
            entity.setUploadPanacim(input.getUploadPanacim());
        }

        repository.save(entity);
        return true;
    }

    // update location by requestID
    @Transactional
    public UpdateResponse updateStorageUnitForRequest(
        Long requestId,
        String storageUnit
    ) {
        List<ListProductOfRequest> products =
            repository.findByRequestCreateTemId(requestId);

        if (products.isEmpty()) {
            return new UpdateResponse(
                false,
                "Không tìm thấy request nào thuộc yêu cầu này"
            );
        }

        for (ListProductOfRequest product : products) {
            product.setStorageUnit(storageUnit); // update
        }

        repository.saveAll(products);
        return new UpdateResponse(
            true,
            "Đã cập nhật kho cho " + products.size() + ""
        );
    }

    @Transactional
    public ListProductOfRequest createProduct(CreateProductInput input) {
        ListProductOfRequest product = new ListProductOfRequest();
        mapInputToEntity(input, product);
        return repository.save(product);
    }

    @Transactional
    public List<ListProductOfRequest> createProductsBatch(
        Integer requestId,
        List<CreateProductInput> inputs
    ) {
        if (inputs == null || inputs.isEmpty()) {
            return new ArrayList<>();
        }

        List<ListProductOfRequest> products = new ArrayList<>();

        try {
            for (CreateProductInput input : inputs) {
                if (input == null) {
                    continue;
                }
                ListProductOfRequest product = new ListProductOfRequest();
                // Override requestCreateTemId with the provided requestId
                input.setRequestCreateTemId(requestId);
                mapInputToEntity(input, product);
                products.add(product);
            }

            List<ListProductOfRequest> savedProducts = repository.saveAll(
                products
            );
            return savedProducts != null ? savedProducts : new ArrayList<>();
        } catch (Exception e) {
            System.err.println(
                "Error creating products batch: " + e.getMessage()
            );
            e.printStackTrace();
            throw new RuntimeException(
                "Failed to create products: " + e.getMessage(),
                e
            );
        }
    }

    private void mapInputToEntity(
        CreateProductInput input,
        ListProductOfRequest product
    ) {
        // Handle requestCreateTemId - allow null for new request creation
        if (input.getRequestCreateTemId() != null) {
            product.setRequestCreateTemId(
                input.getRequestCreateTemId().longValue()
            );
        }
        // If null, it will be set later by the calling service method

        // Handle required fields with null checks
        if (input.getSapCode() == null || input.getSapCode().isEmpty()) {
            throw new IllegalArgumentException(
                "sapCode cannot be null or empty"
            );
        }
        product.setSapCode(input.getSapCode());

        if (input.getTemQuantity() == null) {
            throw new IllegalArgumentException("temQuantity cannot be null");
        }
        product.setTemQuantity(input.getTemQuantity());

        if (input.getPartNumber() == null || input.getPartNumber().isEmpty()) {
            throw new IllegalArgumentException(
                "partNumber cannot be null or empty"
            );
        }
        product.setPartNumber(input.getPartNumber());

        if (input.getLot() == null || input.getLot().isEmpty()) {
            throw new IllegalArgumentException("lot cannot be null or empty");
        }
        product.setLot(input.getLot());

        if (input.getInitialQuantity() == null) {
            throw new IllegalArgumentException(
                "initialQuantity cannot be null"
            );
        }
        product.setInitialQuantity(input.getInitialQuantity().longValue());

        if (input.getVendor() == null || input.getVendor().isEmpty()) {
            throw new IllegalArgumentException(
                "vendor cannot be null or empty"
            );
        }
        product.setVendor(input.getVendor());
        product.setVendorName(
            input.getVendorName() != null ? input.getVendorName() : ""
        );

        // Handle optional fields
        product.setProductName(
            input.getProductName() != null
                ? input.getProductName()
                : input.getPartNumber()
        );
        product.setUserData1(
            input.getUserData1() != null ? input.getUserData1() : ""
        );
        product.setUserData2(
            input.getUserData2() != null ? input.getUserData2() : ""
        );
        product.setUserData3(
            input.getUserData3() != null ? input.getUserData3() : ""
        );
        product.setUserData4(
            input.getUserData4() != null ? input.getUserData4() : ""
        );
        product.setUserData5(
            input.getUserData5() != null ? input.getUserData5() : ""
        );
        product.setStorageUnit(
            input.getStorageUnit() != null ? input.getStorageUnit() : ""
        );
        product.setNumberOfPrints(0);

        // Parse date strings to LocalDateTime with validation
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        if (
            input.getExpirationDate() == null ||
            input.getExpirationDate().isEmpty()
        ) {
            throw new IllegalArgumentException(
                "expirationDate cannot be null or empty"
            );
        }
        product.setExpirationDate(
            parseDate(input.getExpirationDate(), formatter)
        );

        if (
            input.getManufacturingDate() == null ||
            input.getManufacturingDate().isEmpty()
        ) {
            throw new IllegalArgumentException(
                "manufacturingDate cannot be null or empty"
            );
        }
        product.setManufacturingDate(
            parseDate(input.getManufacturingDate(), formatter)
        );

        if (
            input.getArrivalDate() == null || input.getArrivalDate().isEmpty()
        ) {
            throw new IllegalArgumentException(
                "arrivalDate cannot be null or empty"
            );
        }
        log.info(
            "mapInputToEntity - input vendorName: {}",
            input.getVendorName()
        );
        log.info("mapInputToEntity - after set: {}", product.getVendorName());
        product.setArrivalDate(parseDate(input.getArrivalDate(), formatter));
    }

    private LocalDateTime parseDate(
        String dateStr,
        DateTimeFormatter formatter
    ) {
        try {
            if (dateStr != null && !dateStr.isEmpty()) {
                return LocalDateTime.parse(dateStr + "T00:00:00");
            }
        } catch (Exception e) {
            System.err.println(
                "Error parsing date: " + dateStr + " - " + e.getMessage()
            );
        }
        return LocalDateTime.now();
    }

    @Transactional
    public CreateRequestWithProductsResponse createRequestAndProducts(
        CreateRequestWithProductsInput input
    ) {
        try {
            // Validate input
            if (input == null) {
                log.error("Input is null");
                throw new IllegalArgumentException("Input cannot be null");
            }

            if (input.getProducts() == null || input.getProducts().isEmpty()) {
                log.error("Products list is null or empty");
                throw new IllegalArgumentException(
                    "Products list cannot be null or empty"
                );
            }

            // Calculate total quantity and number of products
            int numberProduction = input.getProducts().size();
            long totalQuantity = input
                .getProducts()
                .stream()
                .mapToLong(p ->
                    p.getInitialQuantity() != null
                        ? p.getInitialQuantity().longValue()
                        : 0L
                )
                .sum();

            LocalDateTime parsedCreatedDate;

            if (
                input.getCreatedDate() != null &&
                !input.getCreatedDate().isBlank()
            ) {
                try {
                    parsedCreatedDate = LocalDate.parse(
                        input.getCreatedDate()
                    ).atStartOfDay();
                } catch (DateTimeParseException e) {
                    log.warn(
                        "Invalid createdDate format '{}', using current time instead",
                        input.getCreatedDate()
                    );
                    parsedCreatedDate = LocalDateTime.now();
                }
            } else {
                log.warn("createdDate is null or blank, using current time");
                parsedCreatedDate = LocalDateTime.now();
            }

            ListRequestCreateTem request = requestService.createRequest(
                input.getVendor(),
                input.getVendorName(),
                input.getUserData5(),
                input.getCreatedBy(),
                numberProduction,
                totalQuantity,
                parsedCreatedDate
            );

            // Debug: Check if request was created successfully
            if (request == null || request.getId() == null) {
                log.error(
                    "Failed to create request - request or request ID is null"
                );
                throw new RuntimeException(
                    "Failed to create request - request or request ID is null"
                );
            }

            List<ListProductOfRequest> savedProducts = createProductsBatch(
                request.getId().intValue(),
                input.getProducts()
            );

            if (savedProducts == null || savedProducts.isEmpty()) {
                log.error(
                    "Failed to create products - savedProducts is null or empty"
                );
                throw new RuntimeException("Failed to create products");
            }

            //            log.info("Successfully created {} products", savedProducts.size());

            // Step 3: Return response with request ID and products
            // Ensure all fields are non-null
            CreateRequestWithProductsResponse response =
                new CreateRequestWithProductsResponse();
            response.setRequestId(request.getId().intValue());
            response.setProducts(savedProducts);
            response.setMessage(
                "Successfully created request and " +
                savedProducts.size() +
                " products"
            );

            // Validate response before returning
            if (response.getRequestId() == null) {
                log.error("Response requestId is null after setting");
                throw new RuntimeException(
                    "Failed to set requestId in response"
                );
            }

            if (response.getProducts() == null) {
                log.error("Response products is null after setting");
                throw new RuntimeException(
                    "Failed to set products in response"
                );
            }

            //            log.info("=== createRequestAndProducts completed successfully ===");
            //            log.info(
            //                "Response: requestId={}, products count={}, message={}",
            //                response.getRequestId(),
            //                response.getProducts().size(),
            //                response.getMessage()
            //            );
            log.info("Service received vendorName: {}", input.getVendorName());
            log.info(
                "First product vendorName: {}",
                input.getProducts().get(0).getVendorName()
            );

            return response;
        } catch (Exception e) {
            log.error(
                "Error in createRequestAndProducts: {}",
                e.getMessage(),
                e
            );
            throw new RuntimeException(
                "Failed to create request and products: " + e.getMessage(),
                e
            );
        }
    }

    @Transactional
    public List<ListProductOfRequest> updateRequestProducts(
        Integer requestId,
        List<CreateProductInput> inputs
    ) {
        if (inputs == null || inputs.isEmpty()) {
            throw new IllegalArgumentException(
                "Products list cannot be null or empty"
            );
        }

        if (requestId == null) {
            throw new IllegalArgumentException("Request ID cannot be null");
        }

        //        System.out.println("=== Starting updateRequestProducts ===");
        //        System.out.println("Request ID: " + requestId);
        //        System.out.println("Number of products to update: " + inputs.size());

        //        System.out.println("Deleting existing products...");
        repository.deleteByRequestCreateTemId(requestId.longValue());
        repository.flush(); // Force the delete to execute immediately
        //        System.out.println(
        //            "Successfully deleted existing products for request: " + requestId
        //        );

        //        System.out.println("Step 2: Creating new product entities...");
        List<ListProductOfRequest> products = new ArrayList<>();
        for (int i = 0; i < inputs.size(); i++) {
            CreateProductInput input = inputs.get(i);
            if (input == null) {
                //                System.out.println("Skipping null input at index " + i);
                continue;
            }
            ListProductOfRequest product = new ListProductOfRequest();
            // Set the requestCreateTemId
            input.setRequestCreateTemId(requestId);
            mapInputToEntity(input, product);
            products.add(product);
            //            System.out.println(
            //                "Created product entity " + (i + 1) + ": " + input.getSapCode()
            //            );
        }

        if (products.isEmpty()) {
            throw new RuntimeException("No valid products to save");
        }

        //        System.out.println(
        //            "Created " + products.size() + " new product entities"
        //        );

        //        System.out.println("Step 3: Saving products to database...");
        List<ListProductOfRequest> savedProducts = repository.saveAll(products);
        repository.flush(); // Ensure all products are saved

        if (savedProducts == null || savedProducts.isEmpty()) {
            throw new RuntimeException(
                "Failed to save products - no products returned"
            );
        }

        //        System.out.println(
        //            "Successfully saved " + savedProducts.size() + " products"
        //        );
        //        System.out.println(
        //            "=== updateRequestProducts completed successfully ==="
        //        );

        return savedProducts;
    }

    // delete
    @Transactional
    public void deleteProductById(Long productId) {
        Optional<ListProductOfRequest> productOpt = repository.findById(
            productId
        );

        if (productOpt.isEmpty()) {
            throw new EntityNotFoundException(
                "Không tìm thấy sản phẩm với ID: " + productId
            );
        }

        ListProductOfRequest product = productOpt.get();
        Long requestId = product.getRequestCreateTemId();

        if (requestId == null) {
            throw new IllegalStateException(
                "requestCreateTemId bị null cho productId: " + productId
            );
        }
        detailRepository.deleteByProductOfRequestId(productId);

        // Xóa sản phẩm
        repository.deleteById(productId);

        // Cập nhật số lượng sản phẩm trong bảng cha
        requestService.updateRequestProductCount(requestId);
    }
}
