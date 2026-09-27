package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.VendorLabelInfoAsserts.*;
import static com.mycompany.myapp.domain.VendorLabelInfoTestSamples.*;
import static org.springframework.test.util.ReflectionTestUtils.*;

import com.mycompany.myapp.service.mapper.VendorLabelInfoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VendorLabelInfoMapperTest {

  private VendorLabelInfoMapper vendorLabelInfoMapper;

  @BeforeEach
  void setUp() {
    vendorLabelInfoMapper = new VendorLabelInfoMapperImpl();
    setField(vendorLabelInfoMapper, "palletBoxMappingMapper", new PalletBoxMappingMapperImpl());
  }

  @Test
  void shouldConvertToDtoAndBack() {
    var expected = getVendorLabelInfoSample1();
    var actual = vendorLabelInfoMapper.toEntity(
      vendorLabelInfoMapper.toDto(expected)
    );
    assertVendorLabelInfoAllPropertiesEquals(expected, actual);
  }
}
