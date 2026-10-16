package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Delivery;
import com.mycompany.myapp.domain.DetailDelivery;
import com.mycompany.myapp.domain.PalletBoxMapping;
import com.mycompany.myapp.domain.VendorLabelInfo;
import com.mycompany.myapp.repository.DeliveryRepository;
import com.mycompany.myapp.repository.DetailDeliveryRepository;
import com.mycompany.myapp.repository.PalletBoxMappingRepository;
import com.mycompany.myapp.repository.VendorLabelInfoRepository;
import com.mycompany.myapp.service.dto.DeliveryNotificationDetailDTO;
import com.mycompany.myapp.service.dto.DetailDeliveryDetailDTO;
import com.mycompany.myapp.service.dto.VendorLabelInfoDetailDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for building the full detail of a {@link com.mycompany.myapp.domain.Delivery}:
 * the delivery itself, its DetailDelivery lines and, inside every line,
 * the VendorLabelInfo boxes enriched with serialPallet resolved from PalletBoxMapping
 * (pallet_box_mapping.reel_id_box = vendor_label_info.reel_id).
 */
@Service
@Transactional(value = "partner5TransactionManager", readOnly = true)
public class DeliveryNotificationDetailService {

    private static final Logger LOG = LoggerFactory.getLogger(
        DeliveryNotificationDetailService.class
    );

    private final DeliveryRepository deliveryRepository;

    private final DetailDeliveryRepository detailDeliveryRepository;

    private final VendorLabelInfoRepository vendorLabelInfoRepository;

    private final PalletBoxMappingRepository palletBoxMappingRepository;

    public DeliveryNotificationDetailService(
        DeliveryRepository deliveryRepository,
        DetailDeliveryRepository detailDeliveryRepository,
        VendorLabelInfoRepository vendorLabelInfoRepository,
        PalletBoxMappingRepository palletBoxMappingRepository
    ) {
        this.deliveryRepository = deliveryRepository;
        this.detailDeliveryRepository = detailDeliveryRepository;
        this.vendorLabelInfoRepository = vendorLabelInfoRepository;
        this.palletBoxMappingRepository = palletBoxMappingRepository;
    }

    /**
     * Get the full detail of a delivery.
     *
     * @param id the id of the delivery.
     * @return the detail, or empty when the delivery does not exist.
     */
    public Optional<DeliveryNotificationDetailDTO> findOneWithDetail(UUID id) {
        LOG.debug("Request to get Delivery detail : {}", id);
        return deliveryRepository
            .findById(id)
            .map(delivery ->
                new DeliveryNotificationDetailDTO(
                    delivery,
                    findDetailDeliveryList(id)
                )
            );
    }

    private List<DetailDeliveryDetailDTO> findDetailDeliveryList(
        UUID deliveryId
    ) {
        List<DetailDelivery> detailDeliveries =
            detailDeliveryRepository.findByDelivery_DeliveryId(deliveryId);
        if (detailDeliveries.isEmpty()) {
            return new ArrayList<>();
        }

        List<UUID> lineIds = detailDeliveries
            .stream()
            .map(DetailDelivery::getLineId)
            .collect(Collectors.toList());

        List<VendorLabelInfo> vendorLabelInfos =
            vendorLabelInfoRepository.findByDetailDelivery_LineIdInOrderByReelIdAsc(
                lineIds
            );
        Map<String, PalletBoxMapping> palletBoxMappingByReelId =
            findPalletBoxMappingByReelId(vendorLabelInfos);

        Map<UUID, List<VendorLabelInfoDetailDTO>> vendorLabelInfoByLineId =
            vendorLabelInfos
                .stream()
                .collect(
                    Collectors.groupingBy(
                        vli ->
                            vli.getDetailDelivery() != null
                                ? vli.getDetailDelivery().getLineId()
                                : null,
                        Collectors.mapping(
                            vli ->
                                new VendorLabelInfoDetailDTO(
                                    vli,
                                    palletBoxMappingByReelId.get(
                                        vli.getReelId()
                                    )
                                ),
                            Collectors.toList()
                        )
                    )
                );

        return detailDeliveries
            .stream()
            .map(detailDelivery -> {
                List<VendorLabelInfoDetailDTO> vendorLabelInfoDTOs =
                    vendorLabelInfoByLineId.getOrDefault(
                        detailDelivery.getLineId(),
                        new ArrayList<>()
                    );
                return new DetailDeliveryDetailDTO(
                    detailDelivery,
                    vendorLabelInfoDTOs
                );
            })
            .collect(Collectors.toList());
    }

    private Map<String, PalletBoxMapping> findPalletBoxMappingByReelId(
        List<VendorLabelInfo> vendorLabelInfoList
    ) {
        List<String> reelIds = new ArrayList<>();
        for (VendorLabelInfo vendorLabelInfo : vendorLabelInfoList) {
            if (
                vendorLabelInfo.getReelId() != null &&
                !reelIds.contains(vendorLabelInfo.getReelId())
            ) {
                reelIds.add(vendorLabelInfo.getReelId());
            }
        }
        if (reelIds.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, PalletBoxMapping> palletBoxMappingByReelId =
            new HashMap<>();
        for (PalletBoxMapping palletBoxMapping : palletBoxMappingRepository.findByReelIdBoxIn(
            reelIds
        )) {
            palletBoxMappingByReelId.putIfAbsent(
                palletBoxMapping.getReelIdBox(),
                palletBoxMapping
            );
        }
        return palletBoxMappingByReelId;
    }
}
