package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.PalletBoxMappingAsserts.*;
import static com.mycompany.myapp.domain.PalletBoxMappingTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PalletBoxMappingMapperTest {

  private PalletBoxMappingMapper palletBoxMappingMapper;

  @BeforeEach
  void setUp() {
    palletBoxMappingMapper = new PalletBoxMappingMapperImpl();
  }

  @Test
  void shouldConvertToDtoAndBack() {
    var expected = getPalletBoxMappingSample1();
    var actual = palletBoxMappingMapper.toEntity(
      palletBoxMappingMapper.toDto(expected)
    );
    assertPalletBoxMappingAllPropertiesEquals(expected, actual);
  }
}
