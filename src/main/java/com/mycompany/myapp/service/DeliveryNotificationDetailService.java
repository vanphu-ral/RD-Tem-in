package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.PalletBoxMapping;
import com.mycompany.myapp.domain.SapPor1R1;
import com.mycompany.myapp.domain.VendorLabelInfo;
import com.mycompany.myapp.repository.DeliveryNotificationRepository;
import com.mycompany.myapp.repository.PalletBoxMappingRepository;
import com.mycompany.myapp.repository.SapPor1R1Repository;
import com.mycompany.myapp.repository.VendorLabelInfoRepository;
import com.mycompany.myapp.service.dto.DeliveryNotificationDetailDTO;
import com.mycompany.myapp.service.dto.SapPor1R1DetailDTO;
import com.mycompany.myapp.service.dto.VendorLabelInfoDetailDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for building the full detail of a {@link com.mycompany.myapp.domain.DeliveryNotification}:
 * the delivery notification itself, its SapPor1R1 lines and, inside every line,
 * the VendorLabelInfo boxes enriched with serialPallet resolved from PalletBoxMapping
 * (pallet_box_mapping.reel_id_box = vendor_label_info.reel_id).
 */
@Service
@Transactional(value = "partner5TransactionManager", readOnly = true)
public class DeliveryNotificationDetailService {

    private static final Logger LOG = LoggerFactory.getLogger(
        DeliveryNotificationDetailService.class
    );

    private final DeliveryNotificationRepository deliveryNotificationRepository;

    private final SapPor1R1Repository sapPor1R1Repository;

    private final VendorLabelInfoRepository vendorLabelInfoRepository;

    private final PalletBoxMappingRepository palletBoxMappingRepository;

    public DeliveryNotificationDetailService(
        DeliveryNotificationRepository deliveryNotificationRepository,
        SapPor1R1Repository sapPor1R1Repository,
        VendorLabelInfoRepository vendorLabelInfoRepository,
        PalletBoxMappingRepository palletBoxMappingRepository
    ) {
        this.deliveryNotificationRepository = deliveryNotificationRepository;
        this.sapPor1R1Repository = sapPor1R1Repository;
        this.vendorLabelInfoRepository = vendorLabelInfoRepository;
        this.palletBoxMappingRepository = palletBoxMappingRepository;
    }

    /**
     * Get the full detail of a delivery notification.
     *
     * @param id the id of the delivery notification.
     * @return the detail, or empty when the delivery notification does not exist.
     */
    public Optional<DeliveryNotificationDetailDTO> findOneWithDetail(Long id) {
        LOG.debug("Request to get DeliveryNotification detail : {}", id);
        return deliveryNotificationRepository
            .findById(id)
            .map(deliveryNotification ->
                new DeliveryNotificationDetailDTO(
                    deliveryNotification,
                    findSapPor1R1List(id)
                )
            );
    }

    /**
     * Get every {@link VendorLabelInfo} linked to a delivery notification,
     * enriched with the {@code serialPallet} it belongs to (resolved from
     * {@code pallet_box_mapping} where {@code reel_id_box} = {@code reel_id}).
     *
     * @param deliveryNotificationId the id of the delivery notification.
     * @return the list of vendor label information with serial pallet, or an
     *         empty list when the delivery notification has no vendor label info.
     */
    public List<
        VendorLabelInfoDetailDTO
    > findVendorLabelInfosWithPalletByDeliveryNotificationId(
        Long deliveryNotificationId
    ) {
        LOG.debug(
            "Request to get VendorLabelInfo list with serialPallet by deliveryNotificationId : {}",
            deliveryNotificationId
        );
        List<VendorLabelInfo> vendorLabelInfoList =
            vendorLabelInfoRepository.findByDeliveryNotificationIdOrderByReelIdAsc(
                deliveryNotificationId
            );
        if (vendorLabelInfoList.isEmpty()) {
            return new ArrayList<>();
        }
        Map<String, PalletBoxMapping> palletBoxMappingByReelId =
            findPalletBoxMappingByReelId(vendorLabelInfoList);
        return vendorLabelInfoList
            .stream()
            .map(vendorLabelInfo ->
                new VendorLabelInfoDetailDTO(
                    vendorLabelInfo,
                    palletBoxMappingByReelId.get(vendorLabelInfo.getReelId())
                )
            )
            .collect(Collectors.toList());
    }

    private List<SapPor1R1DetailDTO> findSapPor1R1List(
        Long deliveryNotificationId
    ) {
        List<SapPor1R1> sapPor1R1List =
            sapPor1R1Repository.findByDeliveryNotificationIdOrderByIdAsc(
                deliveryNotificationId
            );
        if (sapPor1R1List.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, List<VendorLabelInfo>> vendorLabelInfoBySapPor1Id =
            findVendorLabelInfoMap(sapPor1R1List);
        List<VendorLabelInfo> flatVendorLabelInfos = vendorLabelInfoBySapPor1Id
            .values()
            .stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());
        Map<String, PalletBoxMapping> palletBoxMappingByReelId =
            findPalletBoxMappingByReelId(flatVendorLabelInfos);

        return sapPor1R1List
            .stream()
            .map(sapPor1R1 ->
                new SapPor1R1DetailDTO(
                    sapPor1R1,
                    vendorLabelInfoBySapPor1Id
                        .getOrDefault(sapPor1R1.getId(), new ArrayList<>())
                        .stream()
                        .map(vendorLabelInfo ->
                            new VendorLabelInfoDetailDTO(
                                vendorLabelInfo,
                                palletBoxMappingByReelId.get(
                                    vendorLabelInfo.getReelId()
                                )
                            )
                        )
                        .collect(Collectors.toList())
                )
            )
            .collect(Collectors.toList());
    }

    private Map<Long, List<VendorLabelInfo>> findVendorLabelInfoMap(
        List<SapPor1R1> sapPor1R1List
    ) {
        List<Long> sapPor1Ids = sapPor1R1List
            .stream()
            .map(SapPor1R1::getId)
            .collect(Collectors.toList());
        return vendorLabelInfoRepository
            .findBySapPor1IdInOrderByReelIdAsc(sapPor1Ids)
            .stream()
            .filter(vendorLabelInfo -> vendorLabelInfo.getSapPor1() != null)
            .collect(
                Collectors.groupingBy(vendorLabelInfo ->
                    vendorLabelInfo.getSapPor1().getId()
                )
            );
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
