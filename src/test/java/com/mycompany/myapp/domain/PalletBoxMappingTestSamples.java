package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PalletBoxMappingTestSamples {

  private static final Random random = new Random();
  private static final AtomicLong longCount = new AtomicLong(
    random.nextInt() + (2 * Integer.MAX_VALUE)
  );

  public static PalletBoxMapping getPalletBoxMappingSample1() {
    return new PalletBoxMapping()
      .id(1L)
      .serialPallet("serialPallet1")
      .reelIdBox("reelIdBox1")
      .createBy("createBy1");
  }

  public static PalletBoxMapping getPalletBoxMappingSample2() {
    return new PalletBoxMapping()
      .id(2L)
      .serialPallet("serialPallet2")
      .reelIdBox("reelIdBox2")
      .createBy("createBy2");
  }

  public static PalletBoxMapping getPalletBoxMappingRandomSampleGenerator() {
    return new PalletBoxMapping()
      .id(longCount.incrementAndGet())
      .serialPallet(UUID.randomUUID().toString())
      .reelIdBox(UUID.randomUUID().toString())
      .createBy(UUID.randomUUID().toString());
  }
}
