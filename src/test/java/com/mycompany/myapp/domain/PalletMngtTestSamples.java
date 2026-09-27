package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PalletMngtTestSamples {

  private static final Random random = new Random();
  private static final AtomicLong longCount = new AtomicLong(
    random.nextInt() + (2 * Integer.MAX_VALUE)
  );
  private static final AtomicInteger intCount = new AtomicInteger(
    random.nextInt() + (2 * Short.MAX_VALUE)
  );

  public static PalletMngt getPalletMngtSample1() {
    return new PalletMngt()
      .id(1L)
      .serialPallet("serialPallet1")
      .locationName("locationName1")
      .numberOfBox(1)
      .totalQuantity(1)
      .status("status1")
      .note("note1")
      .createBy("createBy1")
      .updatedBy("updatedBy1");
  }

  public static PalletMngt getPalletMngtSample2() {
    return new PalletMngt()
      .id(2L)
      .serialPallet("serialPallet2")
      .locationName("locationName2")
      .numberOfBox(2)
      .totalQuantity(2)
      .status("status2")
      .note("note2")
      .createBy("createBy2")
      .updatedBy("updatedBy2");
  }

  public static PalletMngt getPalletMngtRandomSampleGenerator() {
    return new PalletMngt()
      .id(longCount.incrementAndGet())
      .serialPallet(UUID.randomUUID().toString())
      .locationName(UUID.randomUUID().toString())
      .numberOfBox(intCount.incrementAndGet())
      .totalQuantity(intCount.incrementAndGet())
      .status(UUID.randomUUID().toString())
      .note(UUID.randomUUID().toString())
      .createBy(UUID.randomUUID().toString())
      .updatedBy(UUID.randomUUID().toString());
  }
}
