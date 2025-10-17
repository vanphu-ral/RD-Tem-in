package com.mycompany.myapp.service;

import com.mycompany.renderQr.domain.ListProductOfRequest;
import com.mycompany.renderQr.domain.ListProductOfRequestResponse;
import com.mycompany.renderQr.domain.ListRequestCreateTem;
import com.mycompany.renderQr.domain.ListRequestCreateTemResponse;
import com.mycompany.renderQr.repository.InfoTemDetailRepository;
import com.mycompany.renderQr.repository.ListProductOfRequestRepository;
import com.mycompany.renderQr.repository.ListRequestCreateTemRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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

    public List<ListRequestCreateTemResponse> getAll() {
        return repository.findAllProjectedBy();
    }

    @Transactional
    public ListRequestCreateTem createRequest(
        String vendor,
        String userData5,
        String createdBy,
        int numberProduction,
        long totalQuantity,
        LocalDateTime createdDate
    ) {
        ListRequestCreateTem request = new ListRequestCreateTem();
        request.setVendor(vendor);
        request.setUserData5(userData5);
        request.setCreatedBy(createdBy != null ? createdBy : "system");
        request.setNumberProduction((short) numberProduction);
        request.setTotalQuantity(totalQuantity);
        request.setStatus("Bản nháp");
        if (createdDate != null) {
            request.setCreatedDate(createdDate);
        } else {
            request.setCreatedDate(LocalDateTime.now());
        }

        ListRequestCreateTem savedRequest = repository.save(request);

        // Force flush to ensure ID is generated immediately
        repository.flush();

        // Debug: Ensure the ID was generated
        if (savedRequest.getId() == null) {
            throw new RuntimeException(
                "Failed to generate ID for new request after flush"
            );
        }

        System.out.println(
            "Saved request with generated ID: " + savedRequest.getId()
        );
        return savedRequest;
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
