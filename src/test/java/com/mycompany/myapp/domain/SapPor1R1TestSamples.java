package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SapPor1R1TestSamples {

  private static final Random random = new Random();
  private static final AtomicLong longCount = new AtomicLong(
    random.nextInt() + (2 * Integer.MAX_VALUE)
  );

  public static SapPor1R1 getSapPor1R1Sample1() {
    return new SapPor1R1()
      .id(1L)
      .lineNum("lineNum1")
      .baseRef("baseRef1")
      .baseEntry("baseEntry1")
      .baseLine("baseLine1")
      .lineStatus("lineStatus1")
      .itemCode("itemCode1")
      .dscription("dscription1")
      .price("price1")
      .currency("currency1")
      .discPrcnt("discPrcnt1")
      .totalSumSy("totalSumSy1")
      .openSumSys("openSumSys1")
      .invntSttus("invntSttus1")
      .baseDocNum("baseDocNum1")
      .uTenkythuat("uTenkythuat1")
      .uSo("uSo1")
      .uMCode("uMCode1")
      .docEntry("docEntry1")
      .vatGroup("vatGroup1")
      .uomCode("uomCode1")
      .unitMsr("unitMsr1")
      .lineVendor("lineVendor1")
      .trgetEntry("trgetEntry1")
      .whsCode("whsCode1");
  }

  public static SapPor1R1 getSapPor1R1Sample2() {
    return new SapPor1R1()
      .id(2L)
      .lineNum("lineNum2")
      .baseRef("baseRef2")
      .baseEntry("baseEntry2")
      .baseLine("baseLine2")
      .lineStatus("lineStatus2")
      .itemCode("itemCode2")
      .dscription("dscription2")
      .price("price2")
      .currency("currency2")
      .discPrcnt("discPrcnt2")
      .totalSumSy("totalSumSy2")
      .openSumSys("openSumSys2")
      .invntSttus("invntSttus2")
      .baseDocNum("baseDocNum2")
      .uTenkythuat("uTenkythuat2")
      .uSo("uSo2")
      .uMCode("uMCode2")
      .docEntry("docEntry2")
      .vatGroup("vatGroup2")
      .uomCode("uomCode2")
      .unitMsr("unitMsr2")
      .lineVendor("lineVendor2")
      .trgetEntry("trgetEntry2")
      .whsCode("whsCode2");
  }

  public static SapPor1R1 getSapPor1R1RandomSampleGenerator() {
    return new SapPor1R1()
      .id(longCount.incrementAndGet())
      .lineNum(UUID.randomUUID().toString())
      .baseRef(UUID.randomUUID().toString())
      .baseEntry(UUID.randomUUID().toString())
      .baseLine(UUID.randomUUID().toString())
      .lineStatus(UUID.randomUUID().toString())
      .itemCode(UUID.randomUUID().toString())
      .dscription(UUID.randomUUID().toString())
      .price(UUID.randomUUID().toString())
      .currency(UUID.randomUUID().toString())
      .discPrcnt(UUID.randomUUID().toString())
      .totalSumSy(UUID.randomUUID().toString())
      .openSumSys(UUID.randomUUID().toString())
      .invntSttus(UUID.randomUUID().toString())
      .baseDocNum(UUID.randomUUID().toString())
      .uTenkythuat(UUID.randomUUID().toString())
      .uSo(UUID.randomUUID().toString())
      .uMCode(UUID.randomUUID().toString())
      .docEntry(UUID.randomUUID().toString())
      .vatGroup(UUID.randomUUID().toString())
      .uomCode(UUID.randomUUID().toString())
      .unitMsr(UUID.randomUUID().toString())
      .lineVendor(UUID.randomUUID().toString())
      .trgetEntry(UUID.randomUUID().toString())
      .whsCode(UUID.randomUUID().toString());
  }
}
