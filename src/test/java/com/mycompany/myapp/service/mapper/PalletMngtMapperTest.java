package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.PalletMngtAsserts.*;
import static com.mycompany.myapp.domain.PalletMngtTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PalletMngtMapperTest {

  private PalletMngtMapper palletMngtMapper;

  @BeforeEach
  void setUp() {
    palletMngtMapper = new PalletMngtMapperImpl();
  }

  @Test
  void shouldConvertToDtoAndBack() {
    var expected = getPalletMngtSample1();
    var actual = palletMngtMapper.toEntity(palletMngtMapper.toDto(expected));
    assertPalletMngtAllPropertiesEquals(expected, actual);
  }
}
