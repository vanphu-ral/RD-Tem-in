package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.PalletBoxMapping;
import com.mycompany.myapp.domain.VendorLabelInfo;
import com.mycompany.myapp.service.dto.VendorLabelInfoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link VendorLabelInfo} and its DTO {@link VendorLabelInfoDTO}.
 */
@Mapper(componentModel = "spring", uses = { PalletBoxMappingMapper.class })
public interface VendorLabelInfoMapper extends EntityMapper<VendorLabelInfoDTO, VendorLabelInfo> {

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(source = "deliveryNotificationId", target = "deliveryNotification.id")
  @Mapping(source = "sapPor1Id", target = "sapPor1.id")
  VendorLabelInfo toEntity(VendorLabelInfoDTO dto);

  @Mapping(source = "deliveryNotification.id", target = "deliveryNotificationId")
  @Mapping(source = "sapPor1.id", target = "sapPor1Id")
  VendorLabelInfoDTO toDto(VendorLabelInfo entity);

  @AfterMapping
  default void linkRelationships(@MappingTarget VendorLabelInfo vendorLabelInfo) {
    if (vendorLabelInfo.getDeliveryNotification() != null && vendorLabelInfo.getDeliveryNotification().getId() == null) {
      vendorLabelInfo.setDeliveryNotification(null);
    }
    if (vendorLabelInfo.getSapPor1() != null && vendorLabelInfo.getSapPor1().getId() == null) {
      vendorLabelInfo.setSapPor1(null);
    }
    if (vendorLabelInfo.getPalletBoxMapping() != null) {
      if (vendorLabelInfo.getPalletBoxMapping().getId() == null && vendorLabelInfo.getPalletBoxMapping().getReelIdBox() == null) {
        vendorLabelInfo.setPalletBoxMapping(null);
      } else {
        vendorLabelInfo.getPalletBoxMapping().setVendorLabelInfo(vendorLabelInfo);
      }
    }
  }
}
