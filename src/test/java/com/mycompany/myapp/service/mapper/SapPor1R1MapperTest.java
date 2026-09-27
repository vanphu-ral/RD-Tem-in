package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.SapPor1R1Asserts.*;
import static com.mycompany.myapp.domain.SapPor1R1TestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SapPor1R1MapperTest {

  private SapPor1R1Mapper sapPor1R1Mapper;

  @BeforeEach
  void setUp() {
    sapPor1R1Mapper = new SapPor1R1MapperImpl();
  }

  @Test
  void shouldConvertToDtoAndBack() {
    var expected = getSapPor1R1Sample1();
    var actual = sapPor1R1Mapper.toEntity(sapPor1R1Mapper.toDto(expected));
    assertSapPor1R1AllPropertiesEquals(expected, actual);
  }
}
