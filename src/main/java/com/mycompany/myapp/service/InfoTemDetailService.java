package com.mycompany.myapp.service;

import com.mycompany.renderQr.domain.*;
import com.mycompany.renderQr.repository.InfoTemDetailRepository;
import com.mycompany.renderQr.repository.ListProductOfRequestRepository;
import com.mycompany.renderQr.repository.ListRequestCreateTemRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InfoTemDetailService {

    @Autowired
    private InfoTemDetailRepository detailRepo;

    @Autowired
    private ListProductOfRequestRepository productRepo;

    @Autowired
    private ListRequestCreateTemRepository requestRepo;

    @Transactional
    public GenerateTemResponse generateTemForAllProducts(Long requestId) {
        try {
            //Lấy TẤT CẢ sản phẩm từ bảng list_product_of_request
            List<ListProductOfRequest> allProducts =
                productRepo.findByRequestCreateTemId(requestId);

            if (allProducts.isEmpty()) {
                return new GenerateTemResponse(
                    false,
                    "Không có sản phẩm nào trong bảng",
                    0
                );
            }

            //Lấy timestamp hiện tại (dùng chung cho tất cả tem)
            String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMddHHmm")
            );
            //            System.out.println("Timestamp: " + timestamp);

            // Lấy sequence number cuối cùng
            int reelCounter = getLastSequenceNumber() + 1;
            //            System.out.println("Bắt đầu từ sequence: " + reelCounter);

            List<InfoTemDetail> allTemList = new ArrayList<>();
            int totalTems = 0;

            //Duyệt qua TỪNG sản phẩm
            for (ListProductOfRequest product : allProducts) {
                // Lấy 2 số cuối của PO code
                String poNumber = extractPONumber(product.getUserData5());
                //                System.out.println("PO Number (2 số cuối): " + poNumber);

                // Tạo TEM theo số lượng Tem_quantity
                for (int i = 0; i < product.getTemQuantity(); i++) {
                    // Generate ReelID: yyyyMMddHHmm + PO(2 số) + XXXX
                    String reelId =
                        timestamp +
                        poNumber +
                        String.format("%04d", reelCounter);

                    // Generate QR Code string
                    // Format: REELID#PartNumber#Vendor#Lot#RankMàu#RankÁp#RankQuang#UserData4#POCode#Qty#MSL#StorageUnit#MFGDate#SapCode
                    String qrCode = String.join(
                        "#",
                        reelId, // REELID
                        product.getPartNumber(), // PartNumber
                        product.getVendor(), // Vendor
                        product.getLot(), // Lot_Number
                        product.getUserData1(), // RankMàu
                        product.getUserData2(), // RankÁp
                        product.getUserData3(), // RankQuang
                        product.getUserData4(), // UserData4
                        product.getUserData5(), // POCode
                        String.valueOf(product.getInitialQuantity()), // Qty
                        "MSL", // MSL (hard-coded)
                        product.getStorageUnit(), // Storage Unit (từ BE)
                        product
                            .getManufacturingDate()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), // MFG Date
                        product.getSapCode() // SapCode
                    );

                    // Tạo entity InfoTemDetail
                    InfoTemDetail detail = new InfoTemDetail();
                    detail.setProductOfRequestId(product.getId());
                    detail.setReelID(reelId);
                    detail.setSapCode(product.getSapCode());
                    detail.setProductName(product.getPartNumber()); // hoặc lấy từ master table
                    detail.setPartNumber(product.getPartNumber());
                    detail.setLot(product.getLot());
                    detail.setInitialQuantity(
                        product.getInitialQuantity().intValue()
                    );
                    detail.setVendor(product.getVendor());
                    detail.setUserData1(product.getUserData1());
                    detail.setUserData2(product.getUserData2());
                    detail.setUserData3(product.getUserData3());
                    detail.setUserData4(product.getUserData4());
                    detail.setUserData5(product.getUserData5());
                    detail.setStorageUnit(product.getStorageUnit()); // LƯU KHO VÀO ĐÂY
                    detail.setExpirationDate(
                        product.getExpirationDate().toLocalDate()
                    );
                    detail.setManufacturingDate(
                        product.getManufacturingDate().toLocalDate()
                    );
                    detail.setArrivalDate(
                        product.getArrivalDate().toLocalDate()
                    );
                    detail.setQrCode(qrCode);

                    // Nếu có trường sl_tem_quantity

                    allTemList.add(detail);
                    //                    System.out.println("  Tem #" + (i + 1) + ": " + reelId);

                    reelCounter++;
                    totalTems++;
                }
            }

            // Lưu TẤT CẢ tem vào database một lần
            if (!allTemList.isEmpty()) {
                detailRepo.saveAll(allTemList);
                //                System.out.println(
                //                    "\n=== ĐÃ LƯU " + totalTems + " TEM VÀO DATABASE ==="
                //                );
            }

            // 7. Cập nhật status  requests thành "Đã tạo mã QR"
            Optional<ListRequestCreateTem> requestOpt = requestRepo.findById(
                requestId
            );
            if (requestOpt.isPresent()) {
                ListRequestCreateTem request = requestOpt.get();
                request.setStatus("Đã tạo mã QR");
                requestRepo.save(request);
            }

            String message = String.format(
                "Tạo tem thành công! Tổng số tem: %d",
                totalTems
            );
            return new GenerateTemResponse(true, message, totalTems);
        } catch (Exception e) {
            //            System.out.println("=== LỖI: " + e.getMessage() + " ===");
            e.printStackTrace();
            return new GenerateTemResponse(false, "Lỗi: " + e.getMessage(), 0);
        }
    }

    /**
     * Trích xuất 2 số CUỐI từ PO code
     * VD: "PO-001" -> "01", "PO-010" -> "10", "PO-123" -> "23"
     */
    private String extractPONumber(String userData5) {
        if (userData5 == null || userData5.isEmpty()) {
            return "00";
        }

        // Lấy tất cả các chữ số từ chuỗi
        String numbers = userData5.replaceAll("\\D+", "");

        if (numbers.isEmpty()) {
            return "00";
        }

        // Lấy 2 số ĐẦU
        if (numbers.length() >= 2) {
            return numbers.substring(0, 2);
        }

        // Nếu chỉ có 1 số, pad thêm 0 ở đầu → ví dụ "9" → "09"
        return String.format("%02d", Integer.parseInt(numbers));
    }

    /**
     * Lấy sequence number cuối cùng từ ReelID trong database
     */
    private int getLastSequenceNumber() {
        Optional<InfoTemDetail> lastTem = detailRepo.findTopByOrderByIdDesc();

        if (!lastTem.isPresent()) {
            return 0;
        }

        String reelId = lastTem.get().getReelID();
        if (reelId == null || reelId.length() < 4) {
            return 0;
        }

        try {
            // Lấy 4 số cuối của ReelID
            String lastFour = reelId.substring(reelId.length() - 4);
            int seq = Integer.parseInt(lastFour);
            //            System.out.println(
            //                "ReelID cuối cùng: " + reelId + ", Sequence: " + seq
            //            );
            return seq;
        } catch (NumberFormatException e) {
            //            System.out.println("Không thể parse sequence từ ReelID: " + reelId);
            return 0;
        }
    }

    public List<InfoTemDetailResponse> getInfoTemDetail() {
        // Implementation existing code
        return detailRepo
            .findAll()
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    public List<InfoTemDetailResponse> getInfoTemDetailByProductId(
        Long productId
    ) {
        //        System.out.println("Loading tem details for productId: " + productId);

        List<InfoTemDetail> details = detailRepo.findByProductOfRequestId(
            productId
        );

        //        System.out.println("Found " + details.size() + " tem details");

        return details
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    private InfoTemDetailResponse convertToResponse(InfoTemDetail entity) {
        return new InfoTemDetailResponse() {
            @Override
            public Long getId() {
                return entity.getId();
            }

            @Override
            public Long getProductOfRequestId() {
                return entity.getProductOfRequestId();
            }

            @Override
            public String getReelId() {
                return entity.getReelID();
            }

            @Override
            public String getSapCode() {
                return entity.getSapCode();
            }

            @Override
            public String getProductName() {
                return entity.getProductName();
            }

            @Override
            public String getPartNumber() {
                return entity.getPartNumber();
            }

            @Override
            public String getLot() {
                return entity.getLot();
            }

            @Override
            public Integer getInitialQuantity() {
                return entity.getInitialQuantity();
            }

            @Override
            public String getVendor() {
                return entity.getVendor();
            }

            @Override
            public String getUserData1() {
                return entity.getUserData1();
            }

            @Override
            public String getUserData2() {
                return entity.getUserData2();
            }

            @Override
            public String getUserData3() {
                return entity.getUserData3();
            }

            @Override
            public String getUserData4() {
                return entity.getUserData4();
            }

            @Override
            public String getUserData5() {
                return entity.getUserData5();
            }

            @Override
            public String getStorageUnit() {
                return entity.getStorageUnit();
            }

            @Override
            public LocalDate getExpirationDate() {
                return entity.getExpirationDate();
            }

            @Override
            public LocalDate getManufacturingDate() {
                return entity.getManufacturingDate();
            }

            @Override
            public LocalDate getArrivalDate() {
                return entity.getArrivalDate();
            }

            @Override
            public String getQrCode() {
                return entity.getQrCode();
            }
        };
    }

    //get detail by requestID
    public List<InfoTemDetailResponse> getTemDetailsByRequestId(
        Long requestId
    ) {
        List<InfoTemDetail> details = detailRepo.findByRequestCreateTemId(
            requestId
        );
        return details
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
}
