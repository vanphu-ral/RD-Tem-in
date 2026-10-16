package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Delivery;
import com.mycompany.myapp.domain.DetailDelivery;
import com.mycompany.myapp.domain.VendorLabelInfo;
import com.mycompany.myapp.repository.DeliveryRepository;
import com.mycompany.myapp.repository.VendorLabelInfoRepository;
import com.mycompany.myapp.service.dto.DeliveryDTO;
import com.mycompany.myapp.service.dto.DeliveryDetailDTO;
import com.mycompany.myapp.service.dto.VendorLabelInfoDTO;
import com.mycompany.myapp.service.mapper.DeliveryMapper;
import com.mycompany.myapp.service.mapper.VendorLabelInfoMapper;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Delivery}.
 */
@Service
@Transactional
public class DeliveryService {

    private final Logger log = LoggerFactory.getLogger(DeliveryService.class);

    private final DeliveryRepository deliveryRepository;

    private final DeliveryMapper deliveryMapper;

    private final VendorLabelInfoRepository vendorLabelInfoRepository;

    private final VendorLabelInfoMapper vendorLabelInfoMapper;

    public DeliveryService(
        DeliveryRepository deliveryRepository,
        DeliveryMapper deliveryMapper,
        VendorLabelInfoRepository vendorLabelInfoRepository,
        VendorLabelInfoMapper vendorLabelInfoMapper
    ) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryMapper = deliveryMapper;
        this.vendorLabelInfoRepository = vendorLabelInfoRepository;
        this.vendorLabelInfoMapper = vendorLabelInfoMapper;
    }

    /**
     * Save a delivery.
     *
     * @param deliveryDTO the entity to save.
     * @return the persisted entity.
     */
    public DeliveryDTO save(DeliveryDTO deliveryDTO) {
        log.debug("Request to save Delivery : {}", deliveryDTO);
        Delivery delivery = deliveryMapper.toEntity(deliveryDTO);
        delivery = deliveryRepository.save(delivery);
        return deliveryMapper.toDto(delivery);
    }

    /**
     * Update a delivery.
     *
     * @param deliveryDTO the entity to save.
     * @return the persisted entity.
     */
    public DeliveryDTO update(DeliveryDTO deliveryDTO) {
        log.debug("Request to update Delivery : {}", deliveryDTO);
        Delivery delivery = deliveryMapper.toEntity(deliveryDTO);
        delivery = deliveryRepository.save(delivery);
        return deliveryMapper.toDto(delivery);
    }

    /**
     * Partially update a delivery.
     *
     * @param deliveryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DeliveryDTO> partialUpdate(DeliveryDTO deliveryDTO) {
        log.debug("Request to partially update Delivery : {}", deliveryDTO);

        return deliveryRepository
            .findById(deliveryDTO.getDeliveryId())
            .map(existingDelivery -> {
                deliveryMapper.partialUpdate(existingDelivery, deliveryDTO);

                return existingDelivery;
            })
            .map(deliveryRepository::save)
            .map(deliveryMapper::toDto);
    }

    /**
     * Get one delivery by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DeliveryDTO> findOne(UUID id) {
        log.debug("Request to get Delivery : {}", id);
        return deliveryRepository.findById(id).map(deliveryMapper::toDto);
    }

    public Optional<DeliveryDetailDTO> findOneDetail(UUID id) {
        log.debug("Request to get Delivery Detail : {}", id);
        return deliveryRepository
            .findWithDetailDeliveriesById(id)
            .map(delivery -> {
                DeliveryDetailDTO dto = new DeliveryDetailDTO();
                dto.setDeliveryId(delivery.getDeliveryId());
                dto.setType(delivery.getType());
                dto.setNoticeNo(delivery.getNoticeNo());
                dto.setNoticeDate(delivery.getNoticeDate());
                dto.setSupplierCode(delivery.getSupplierCode());
                dto.setSupplierName(delivery.getSupplierName());
                dto.setFactory(delivery.getFactory());
                dto.setDateArrivalRangDong(delivery.getDateArrivalRangDong());
                dto.setVehicleNo(delivery.getVehicleNo());
                dto.setNumberOfPo(delivery.getNumberOfPo());
                dto.setNumberOfItem(delivery.getNumberOfItem());
                dto.setSource(delivery.getSource());
                dto.setCreatedAt(delivery.getCreatedAt());
                dto.setCreatedBy(delivery.getCreatedBy());
                dto.setDeletedAt(delivery.getDeletedAt());
                dto.setDeletedBy(delivery.getDeletedBy());

                java.util.Collection<DetailDelivery> detailDeliveries =
                    delivery.getDetailDeliveries();
                java.util.List<UUID> lineIds = detailDeliveries
                    .stream()
                    .map(DetailDelivery::getLineId)
                    .collect(Collectors.toList());

                // Query toàn bộ labels 1 lần duy nhất để tránh N+1 và lỗi LOB stream
                java.util.List<VendorLabelInfo> vendorLabelInfos =
                    vendorLabelInfoRepository.findByDetailDelivery_LineIdInOrderByReelIdAsc(
                        lineIds
                    );
                java.util.Map<
                    UUID,
                    java.util.List<VendorLabelInfoDTO>
                > labelsByLineId = vendorLabelInfos
                    .stream()
                    .map(vli -> {
                        VendorLabelInfoDTO labelDto = new VendorLabelInfoDTO();
                        labelDto.setId(vli.getId());
                        labelDto.setReelId(vli.getReelId());
                        labelDto.setPartNumber(vli.getPartNumber());
                        labelDto.setVendor(vli.getVendor());
                        labelDto.setLot(vli.getLot());
                        labelDto.setStatus(vli.getStatus());
                        labelDto.setLineId(
                            vli.getDetailDelivery() != null
                                ? vli.getDetailDelivery().getLineId()
                                : null
                        );
                        return labelDto;
                    })
                    .collect(
                        Collectors.groupingBy(l ->
                            l.getLineId() != null
                                ? l.getLineId()
                                : java.util.UUID.randomUUID()
                        )
                    );

                java.util.List<
                    DeliveryDetailDTO.DetailDeliveryWithLabelsDTO
                > details = detailDeliveries
                    .stream()
                    .map(dd -> {
                        DeliveryDetailDTO.DetailDeliveryWithLabelsDTO ddDto =
                            new DeliveryDetailDTO.DetailDeliveryWithLabelsDTO();
                        ddDto.setLineId(dd.getLineId());
                        ddDto.setDeliveryId(delivery.getDeliveryId());
                        ddDto.setPoSmart(dd.getPoSmart());
                        ddDto.setPoErp(dd.getPoErp());
                        ddDto.setPrNumber(dd.getPrNumber());
                        ddDto.setContractNo(dd.getContractNo());
                        ddDto.setLineItemStt(dd.getLineItemStt());
                        ddDto.setPoLineItem(dd.getPoLineItem());
                        ddDto.setItemCode(dd.getItemCode());
                        ddDto.setItemName(dd.getItemName());
                        ddDto.setWhsCode(dd.getWhsCode());
                        ddDto.setUnit(dd.getUnit());
                        ddDto.setQuantity(dd.getQuantity());
                        ddDto.setInvoiceSmartNo(dd.getInvoiceSmartNo());
                        ddDto.setInvoiceNo(dd.getInvoiceNo());
                        ddDto.setInvoiceDate(dd.getInvoiceDate());
                        ddDto.setDeliveryTurn(dd.getDeliveryTurn());

                        ddDto.setVendorLabelInfoList(
                            labelsByLineId.getOrDefault(
                                dd.getLineId(),
                                java.util.Collections.emptyList()
                            )
                        );
                        return ddDto;
                    })
                    .collect(Collectors.toList());

                dto.setDetailDelivery(details);
                return dto;
            });
    }

    /**
     * Delete the delivery by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete Delivery : {}", id);
        deliveryRepository.deleteById(id);
    }
}
