package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Delivery;
import com.mycompany.myapp.domain.DetailDelivery;
import com.mycompany.myapp.domain.PalletBoxMapping;
import com.mycompany.myapp.domain.PalletMngt;
import com.mycompany.myapp.domain.VendorLabelInfo;
import com.mycompany.myapp.repository.DeliveryRepository;
import com.mycompany.myapp.repository.DetailDeliveryRepository;
import com.mycompany.myapp.repository.PalletBoxMappingRepository;
import com.mycompany.myapp.repository.PalletMngtRepository;
import com.mycompany.myapp.repository.VendorLabelInfoRepository;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.dto.PalletBoxMappingDTO;
import com.mycompany.myapp.service.dto.PalletVendorLabelInfoItemDTO;
import com.mycompany.myapp.service.dto.PalletVendorLabelInfoRequestDTO;
import com.mycompany.myapp.service.dto.PalletVendorLabelInfoResponseDTO;
import com.mycompany.myapp.service.dto.VendorLabelInfoDTO;
import com.mycompany.myapp.service.mapper.VendorLabelInfoMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service that registers the vendor label information of a pallet.
 *
 * Each item of the request is persisted into {@code vendor_label_info} and linked
 * to the pallet through {@code pallet_box_mapping}
 * ({@code serial_pallet} + {@code reel_id_box} = {@code vendor_label_info.reel_id}).
 * The whole operation runs inside a single {@code partner5} transaction, so either
 * every label and every mapping are saved, or nothing is.
 */
@Service
@Transactional("partner5TransactionManager")
public class PalletVendorLabelInfoService {

    private static final Logger LOG = LoggerFactory.getLogger(
        PalletVendorLabelInfoService.class
    );

    private static final String ENTITY_NAME = "palletVendorLabelInfo";

    private final VendorLabelInfoRepository vendorLabelInfoRepository;

    private final VendorLabelInfoMapper vendorLabelInfoMapper;

    private final PalletBoxMappingRepository palletBoxMappingRepository;

    private final PalletMngtRepository palletMngtRepository;

    private final DeliveryRepository deliveryRepository;

    private final DetailDeliveryRepository detailDeliveryRepository;

    public PalletVendorLabelInfoService(
        VendorLabelInfoRepository vendorLabelInfoRepository,
        VendorLabelInfoMapper vendorLabelInfoMapper,
        PalletBoxMappingRepository palletBoxMappingRepository,
        PalletMngtRepository palletMngtRepository,
        DeliveryRepository deliveryRepository,
        DetailDeliveryRepository detailDeliveryRepository
    ) {
        this.vendorLabelInfoRepository = vendorLabelInfoRepository;
        this.vendorLabelInfoMapper = vendorLabelInfoMapper;
        this.palletBoxMappingRepository = palletBoxMappingRepository;
        this.palletMngtRepository = palletMngtRepository;
        this.deliveryRepository = deliveryRepository;
        this.detailDeliveryRepository = detailDeliveryRepository;
    }

    /**
     * Save the vendor label information of one pallet.
     *
     * @param requestDTO the pallet serial and its list of vendor label information.
     * @return the saved vendor label information.
     */
    public PalletVendorLabelInfoResponseDTO savePalletWithVendorLabels(
        PalletVendorLabelInfoRequestDTO requestDTO
    ) {
        LOG.debug("Request to save vendor labels of pallet : {}", requestDTO);

        if (requestDTO == null) {
            throw new BadRequestAlertException(
                "Request body is required",
                ENTITY_NAME,
                "missing"
            );
        }

        String serialPallet = trimToNull(requestDTO.getSerialPallet());
        if (serialPallet == null) {
            throw new BadRequestAlertException(
                "serialPallet is required",
                ENTITY_NAME,
                "serialPalletRequired"
            );
        }
        if (serialPallet.length() > 30) {
            throw new BadRequestAlertException(
                "serialPallet must not exceed 30 characters",
                ENTITY_NAME,
                "serialPalletTooLong"
            );
        }

        List<VendorLabelInfoDTO> vendorLabelInfoDTOList =
            requestDTO.getVendorLabelInfoList();
        if (
            vendorLabelInfoDTOList == null || vendorLabelInfoDTOList.isEmpty()
        ) {
            throw new BadRequestAlertException(
                "vendorLabelInfoList is required",
                ENTITY_NAME,
                "vendorLabelInfoListRequired"
            );
        }

        List<VendorLabelInfoDTO> normalizedDTOList = normalizeDTOList(
            vendorLabelInfoDTOList
        );
        PalletMngt palletMngt = findPalletMngt(serialPallet);
        checkReelIdNotUsed(normalizedDTOList);
        Map<UUID, Delivery> deliveryNotificationMap =
            findDeliveryNotificationMap(normalizedDTOList);
        Map<UUID, DetailDelivery> detailDeliveryMap = findDetailDeliveryMap(
            normalizedDTOList
        );

        String currentUser = SecurityUtils.getCurrentUserLogin().orElse(null);
        Instant now = Instant.now();

        List<VendorLabelInfo> vendorLabelInfoList = new ArrayList<>(
            normalizedDTOList.size()
        );
        for (VendorLabelInfoDTO dto : normalizedDTOList) {
            VendorLabelInfo vendorLabelInfo = vendorLabelInfoMapper.toEntity(
                dto
            );
            vendorLabelInfo.setId(null);
            vendorLabelInfo.setDelivery(
                deliveryNotificationMap.get(dto.getDeliveryId())
            );
            vendorLabelInfo.setDetailDelivery(
                detailDeliveryMap.get(dto.getLineId())
            );
            vendorLabelInfo.setPalletBoxMapping(null);
            if (trimToNull(vendorLabelInfo.getCreatedBy()) == null) {
                vendorLabelInfo.setCreatedBy(limit(currentUser, 20));
            }
            if (vendorLabelInfo.getCreatedAt() == null) {
                vendorLabelInfo.setCreatedAt(now);
            }
            vendorLabelInfo.setUpdatedBy(null);
            vendorLabelInfo.setUpdatedAt(null);
            vendorLabelInfoList.add(vendorLabelInfo);
        }

        List<VendorLabelInfo> savedVendorLabelInfoList =
            vendorLabelInfoRepository.saveAll(vendorLabelInfoList);
        vendorLabelInfoRepository.flush();

        List<PalletBoxMapping> palletBoxMappingList = new ArrayList<>(
            savedVendorLabelInfoList.size()
        );
        for (VendorLabelInfo vendorLabelInfo : savedVendorLabelInfoList) {
            PalletBoxMapping palletBoxMapping = new PalletBoxMapping();
            palletBoxMapping.setSerialPallet(serialPallet);
            palletBoxMapping.setReelIdBox(vendorLabelInfo.getReelId());
            palletBoxMapping.setCreateAt(vendorLabelInfo.getCreatedAt());
            palletBoxMapping.setCreateBy(
                limit(String.valueOf(vendorLabelInfo.getCreatedAt()), 50)
            );
            palletBoxMapping.setPalletMngt(palletMngt);
            palletBoxMapping.setVendorLabelInfo(vendorLabelInfo);
            palletBoxMappingList.add(palletBoxMapping);
        }
        List<PalletBoxMapping> savedPalletBoxMappingList =
            palletBoxMappingRepository.saveAll(palletBoxMappingList);

        PalletVendorLabelInfoResponseDTO responseDTO =
            new PalletVendorLabelInfoResponseDTO();
        responseDTO.setSerialPallet(serialPallet);
        responseDTO.setVendorLabelInfoList(
            buildVendorLabelInfoItemList(
                savedVendorLabelInfoList,
                savedPalletBoxMappingList
            )
        );

        LOG.debug(
            "Saved {} vendor label info for pallet {}",
            responseDTO.getVendorLabelInfoList().size(),
            serialPallet
        );
        return responseDTO;
    }

    private List<PalletVendorLabelInfoItemDTO> buildVendorLabelInfoItemList(
        List<VendorLabelInfo> savedVendorLabelInfoList,
        List<PalletBoxMapping> savedPalletBoxMappingList
    ) {
        List<PalletVendorLabelInfoItemDTO> result = new ArrayList<>(
            savedVendorLabelInfoList.size()
        );
        for (int i = 0; i < savedVendorLabelInfoList.size(); i++) {
            VendorLabelInfo vendorLabelInfo = savedVendorLabelInfoList.get(i);
            PalletBoxMapping palletBoxMapping = i <
                savedPalletBoxMappingList.size()
                ? savedPalletBoxMappingList.get(i)
                : null;

            PalletVendorLabelInfoItemDTO itemDTO =
                new PalletVendorLabelInfoItemDTO();
            BeanUtils.copyProperties(
                vendorLabelInfoMapper.toDto(vendorLabelInfo),
                itemDTO,
                "deliveryNotification",
                "palletBoxMapping"
            );
            if (palletBoxMapping != null) {
                itemDTO.setPalletBoxMappingId(palletBoxMapping.getId());
                itemDTO.setPalletBoxMapping(toDTO(palletBoxMapping));
            }
            result.add(itemDTO);
        }
        return result;
    }

    private PalletBoxMappingDTO toDTO(PalletBoxMapping palletBoxMapping) {
        PalletBoxMappingDTO dto = new PalletBoxMappingDTO();
        dto.setId(palletBoxMapping.getId());
        dto.setSerialPallet(palletBoxMapping.getSerialPallet());
        dto.setReelIdBox(palletBoxMapping.getReelIdBox());
        dto.setCreateAt(palletBoxMapping.getCreateAt());
        dto.setCreateBy(palletBoxMapping.getCreateBy());
        return dto;
    }

    private List<VendorLabelInfoDTO> normalizeDTOList(
        List<VendorLabelInfoDTO> vendorLabelInfoDTOList
    ) {
        List<VendorLabelInfoDTO> result = new ArrayList<>(
            vendorLabelInfoDTOList.size()
        );
        Set<String> reelIds = new LinkedHashSet<>();
        int index = 0;
        for (VendorLabelInfoDTO dto : vendorLabelInfoDTOList) {
            if (dto == null) {
                throw new BadRequestAlertException(
                    "vendorLabelInfoList[" + index + "] must not be null",
                    ENTITY_NAME,
                    "vendorLabelInfoItemNull"
                );
            }
            String reelId = trimToNull(dto.getReelId());
            if (reelId == null) {
                throw new BadRequestAlertException(
                    "vendorLabelInfoList[" + index + "].reelId is required",
                    ENTITY_NAME,
                    "reelIdRequired"
                );
            }
            if (reelId.length() > 50) {
                throw new BadRequestAlertException(
                    "vendorLabelInfoList[" +
                    index +
                    "].reelId must not exceed 50 characters",
                    ENTITY_NAME,
                    "reelIdTooLong"
                );
            }
            if (!reelIds.add(reelId)) {
                throw new BadRequestAlertException(
                    "Duplicated reelId in vendorLabelInfoList : " + reelId,
                    ENTITY_NAME,
                    "reelIdDuplicated"
                );
            }
            dto.setReelId(reelId);
            result.add(dto);
            index++;
        }
        return result;
    }

    private PalletMngt findPalletMngt(String serialPallet) {
        Optional<PalletMngt> palletMngt =
            palletMngtRepository.findBySerialPallet(serialPallet);
        if (!palletMngt.isPresent()) {
            throw new BadRequestAlertException(
                "Pallet not found with serialPallet : " + serialPallet,
                ENTITY_NAME,
                "palletMngtNotFound"
            );
        }
        return palletMngt.get();
    }

    private void checkReelIdNotUsed(List<VendorLabelInfoDTO> dtoList) {
        List<String> reelIds = dtoList
            .stream()
            .map(VendorLabelInfoDTO::getReelId)
            .collect(Collectors.toList());
        vendorLabelInfoRepository
            .findByReelIdIn(reelIds)
            .stream()
            .map(VendorLabelInfo::getReelId)
            .findFirst()
            .ifPresent(existingReelId -> {
                throw new BadRequestAlertException(
                    "reelId already exists in vendor_label_info : " +
                    existingReelId,
                    ENTITY_NAME,
                    "reelIdExists"
                );
            });
    }

    private Map<UUID, Delivery> findDeliveryNotificationMap(
        List<VendorLabelInfoDTO> dtoList
    ) {
        Set<UUID> ids = collectIds(dtoList, VendorLabelInfoDTO::getDeliveryId);
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<UUID, Delivery> map = deliveryRepository
            .findAllById(ids)
            .stream()
            .collect(
                Collectors.toMap(Delivery::getDeliveryId, Function.identity())
            );
        assertAllExist(ids, map.keySet(), "deliveryId");
        return map;
    }

    private Map<UUID, DetailDelivery> findDetailDeliveryMap(
        List<VendorLabelInfoDTO> dtoList
    ) {
        Set<UUID> ids = collectIds(dtoList, VendorLabelInfoDTO::getLineId);
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<UUID, DetailDelivery> map = detailDeliveryRepository
            .findAllById(ids)
            .stream()
            .collect(
                Collectors.toMap(DetailDelivery::getLineId, Function.identity())
            );
        assertAllExist(ids, map.keySet(), "lineId");
        return map;
    }

    private Set<UUID> collectIds(
        List<VendorLabelInfoDTO> dtoList,
        Function<VendorLabelInfoDTO, UUID> extractor
    ) {
        Map<UUID, Boolean> ids = new LinkedHashMap<>();
        for (VendorLabelInfoDTO dto : dtoList) {
            UUID id = extractor.apply(dto);
            if (id != null) {
                ids.put(id, Boolean.TRUE);
            }
        }
        return ids.keySet();
    }

    private void assertAllExist(
        Collection<UUID> requestedIds,
        Collection<UUID> foundIds,
        String fieldName
    ) {
        List<UUID> missingIds = requestedIds
            .stream()
            .filter(id -> !foundIds.contains(id))
            .collect(Collectors.toList());
        if (!missingIds.isEmpty()) {
            throw new BadRequestAlertException(
                fieldName + " not found : " + missingIds,
                ENTITY_NAME,
                fieldName + "NotFound"
            );
        }
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String limit(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() > maxLength
            ? value.substring(0, maxLength)
            : value;
    }
}
