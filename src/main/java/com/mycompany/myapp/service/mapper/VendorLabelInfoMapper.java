package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.PalletBoxMapping;
import com.mycompany.myapp.domain.VendorLabelInfo;
import com.mycompany.myapp.service.dto.VendorLabelInfoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link VendorLabelInfo} and its DTO {@link VendorLabelInfoDTO}.
 */
@Mapper(componentModel = "spring", uses = { PalletBoxMappingMapper.class })
public interface VendorLabelInfoMapper
    extends EntityMapper<VendorLabelInfoDTO, VendorLabelInfo> {
    @BeanMapping(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(source = "deliveryId", target = "delivery.deliveryId")
    @Mapping(source = "lineId", target = "detailDelivery.lineId")
    VendorLabelInfo toEntity(VendorLabelInfoDTO dto);

    @Mapping(source = "delivery.deliveryId", target = "deliveryId")
    @Mapping(source = "detailDelivery.lineId", target = "lineId")
    VendorLabelInfoDTO toDto(VendorLabelInfo entity);

    @AfterMapping
    default void linkRelationships(
        @MappingTarget VendorLabelInfo vendorLabelInfo
    ) {
        if (
            vendorLabelInfo.getDelivery() != null &&
            vendorLabelInfo.getDelivery().getDeliveryId() == null
        ) {
            vendorLabelInfo.setDelivery(null);
        }
        if (
            vendorLabelInfo.getDetailDelivery() != null &&
            vendorLabelInfo.getDetailDelivery().getLineId() == null
        ) {
            vendorLabelInfo.setDetailDelivery(null);
        }
        if (vendorLabelInfo.getPalletBoxMapping() != null) {
            if (
                vendorLabelInfo.getPalletBoxMapping().getId() == null &&
                vendorLabelInfo.getPalletBoxMapping().getReelIdBox() == null
            ) {
                vendorLabelInfo.setPalletBoxMapping(null);
            } else {
                vendorLabelInfo
                    .getPalletBoxMapping()
                    .setVendorLabelInfo(vendorLabelInfo);
            }
        }
    }
}
