package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.SapPor1R1Asserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.SapPor1R1;
import com.mycompany.myapp.repository.SapPor1R1Repository;
import com.mycompany.myapp.service.dto.SapPor1R1DTO;
import com.mycompany.myapp.service.mapper.SapPor1R1Mapper;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SapPor1R1Resource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SapPor1R1ResourceIT {

  private static final String DEFAULT_LINE_NUM = "AAAAAAAAAA";
  private static final String UPDATED_LINE_NUM = "BBBBBBBBBB";

  private static final String DEFAULT_BASE_REF = "AAAAAAAAAA";
  private static final String UPDATED_BASE_REF = "BBBBBBBBBB";

  private static final String DEFAULT_BASE_ENTRY = "AAAAAAAAAA";
  private static final String UPDATED_BASE_ENTRY = "BBBBBBBBBB";

  private static final String DEFAULT_BASE_LINE = "AAAAAAAAAA";
  private static final String UPDATED_BASE_LINE = "BBBBBBBBBB";

  private static final String DEFAULT_LINE_STATUS = "AAAAAAAAAA";
  private static final String UPDATED_LINE_STATUS = "BBBBBBBBBB";

  private static final String DEFAULT_ITEM_CODE = "AAAAAAAAAA";
  private static final String UPDATED_ITEM_CODE = "BBBBBBBBBB";

  private static final String DEFAULT_DSCRIPTION = "AAAAAAAAAA";
  private static final String UPDATED_DSCRIPTION = "BBBBBBBBBB";

  private static final BigDecimal DEFAULT_QUANTITY = new BigDecimal(1);
  private static final BigDecimal UPDATED_QUANTITY = new BigDecimal(2);

  private static final Instant DEFAULT_SHIP_DATE = Instant.ofEpochMilli(0L);
  private static final Instant UPDATED_SHIP_DATE = Instant.now()
    .truncatedTo(ChronoUnit.MILLIS);

  private static final String DEFAULT_PRICE = "AAAAAAAAAA";
  private static final String UPDATED_PRICE = "BBBBBBBBBB";

  private static final String DEFAULT_CURRENCY = "AAAAAAAAAA";
  private static final String UPDATED_CURRENCY = "BBBBBBBBBB";

  private static final String DEFAULT_DISC_PRCNT = "AAAAAAAAAA";
  private static final String UPDATED_DISC_PRCNT = "BBBBBBBBBB";

  private static final String DEFAULT_TOTAL_SUM_SY = "AAAAAAAAAA";
  private static final String UPDATED_TOTAL_SUM_SY = "BBBBBBBBBB";

  private static final String DEFAULT_OPEN_SUM_SYS = "AAAAAAAAAA";
  private static final String UPDATED_OPEN_SUM_SYS = "BBBBBBBBBB";

  private static final String DEFAULT_INVNT_STTUS = "AAAAAAAAAA";
  private static final String UPDATED_INVNT_STTUS = "BBBBBBBBBB";

  private static final String DEFAULT_BASE_DOC_NUM = "AAAAAAAAAA";
  private static final String UPDATED_BASE_DOC_NUM = "BBBBBBBBBB";

  private static final String DEFAULT_U_TENKYTHUAT = "AAAAAAAAAA";
  private static final String UPDATED_U_TENKYTHUAT = "BBBBBBBBBB";

  private static final String DEFAULT_U_SO = "AAAAAAAAAA";
  private static final String UPDATED_U_SO = "BBBBBBBBBB";

  private static final String DEFAULT_U_M_CODE = "AAAAAAAAAA";
  private static final String UPDATED_U_M_CODE = "BBBBBBBBBB";

  private static final String DEFAULT_DOC_ENTRY = "AAAAAAAAAA";
  private static final String UPDATED_DOC_ENTRY = "BBBBBBBBBB";

  private static final Double DEFAULT_TOTAL_FRGN = 1D;
  private static final Double UPDATED_TOTAL_FRGN = 2D;

  private static final String DEFAULT_VAT_GROUP = "AAAAAAAAAA";
  private static final String UPDATED_VAT_GROUP = "BBBBBBBBBB";

  private static final String DEFAULT_UOM_CODE = "AAAAAAAAAA";
  private static final String UPDATED_UOM_CODE = "BBBBBBBBBB";

  private static final String DEFAULT_UNIT_MSR = "AAAAAAAAAA";
  private static final String UPDATED_UNIT_MSR = "BBBBBBBBBB";

  private static final String DEFAULT_LINE_VENDOR = "AAAAAAAAAA";
  private static final String UPDATED_LINE_VENDOR = "BBBBBBBBBB";

  private static final String DEFAULT_TRGET_ENTRY = "AAAAAAAAAA";
  private static final String UPDATED_TRGET_ENTRY = "BBBBBBBBBB";

  private static final BigDecimal DEFAULT_LINE_TOTAL = new BigDecimal(1);
  private static final BigDecimal UPDATED_LINE_TOTAL = new BigDecimal(2);

  private static final BigDecimal DEFAULT_VAT_PRCNT = new BigDecimal(1);
  private static final BigDecimal UPDATED_VAT_PRCNT = new BigDecimal(2);

  private static final BigDecimal DEFAULT_PRICE_AF_VAT = new BigDecimal(1);
  private static final BigDecimal UPDATED_PRICE_AF_VAT = new BigDecimal(2);

  private static final String DEFAULT_WHS_CODE = "AAAAAAAAAA";
  private static final String UPDATED_WHS_CODE = "BBBBBBBBBB";

  private static final String ENTITY_API_URL = "/api/sap-por-1-r-1-s";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

  private static Random random = new Random();
  private static AtomicLong longCount = new AtomicLong(
    random.nextInt() + (2 * Integer.MAX_VALUE)
  );

  @Autowired
  private ObjectMapper om;

  @Autowired
  private SapPor1R1Repository sapPor1R1Repository;

  @Autowired
  private SapPor1R1Mapper sapPor1R1Mapper;

  @Autowired
  private EntityManager em;

  @Autowired
  private MockMvc restSapPor1R1MockMvc;

  private SapPor1R1 sapPor1R1;

  private SapPor1R1 insertedSapPor1R1;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static SapPor1R1 createEntity() {
    return new SapPor1R1()
      .lineNum(DEFAULT_LINE_NUM)
      .baseRef(DEFAULT_BASE_REF)
      .baseEntry(DEFAULT_BASE_ENTRY)
      .baseLine(DEFAULT_BASE_LINE)
      .lineStatus(DEFAULT_LINE_STATUS)
      .itemCode(DEFAULT_ITEM_CODE)
      .dscription(DEFAULT_DSCRIPTION)
      .quantity(DEFAULT_QUANTITY)
      .shipDate(DEFAULT_SHIP_DATE)
      .price(DEFAULT_PRICE)
      .currency(DEFAULT_CURRENCY)
      .discPrcnt(DEFAULT_DISC_PRCNT)
      .totalSumSy(DEFAULT_TOTAL_SUM_SY)
      .openSumSys(DEFAULT_OPEN_SUM_SYS)
      .invntSttus(DEFAULT_INVNT_STTUS)
      .baseDocNum(DEFAULT_BASE_DOC_NUM)
      .uTenkythuat(DEFAULT_U_TENKYTHUAT)
      .uSo(DEFAULT_U_SO)
      .uMCode(DEFAULT_U_M_CODE)
      .docEntry(DEFAULT_DOC_ENTRY)
      .totalFrgn(DEFAULT_TOTAL_FRGN)
      .vatGroup(DEFAULT_VAT_GROUP)
      .uomCode(DEFAULT_UOM_CODE)
      .unitMsr(DEFAULT_UNIT_MSR)
      .lineVendor(DEFAULT_LINE_VENDOR)
      .trgetEntry(DEFAULT_TRGET_ENTRY)
      .lineTotal(DEFAULT_LINE_TOTAL)
      .vatPrcnt(DEFAULT_VAT_PRCNT)
      .priceAfVat(DEFAULT_PRICE_AF_VAT)
      .whsCode(DEFAULT_WHS_CODE);
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static SapPor1R1 createUpdatedEntity() {
    return new SapPor1R1()
      .lineNum(UPDATED_LINE_NUM)
      .baseRef(UPDATED_BASE_REF)
      .baseEntry(UPDATED_BASE_ENTRY)
      .baseLine(UPDATED_BASE_LINE)
      .lineStatus(UPDATED_LINE_STATUS)
      .itemCode(UPDATED_ITEM_CODE)
      .dscription(UPDATED_DSCRIPTION)
      .quantity(UPDATED_QUANTITY)
      .shipDate(UPDATED_SHIP_DATE)
      .price(UPDATED_PRICE)
      .currency(UPDATED_CURRENCY)
      .discPrcnt(UPDATED_DISC_PRCNT)
      .totalSumSy(UPDATED_TOTAL_SUM_SY)
      .openSumSys(UPDATED_OPEN_SUM_SYS)
      .invntSttus(UPDATED_INVNT_STTUS)
      .baseDocNum(UPDATED_BASE_DOC_NUM)
      .uTenkythuat(UPDATED_U_TENKYTHUAT)
      .uSo(UPDATED_U_SO)
      .uMCode(UPDATED_U_M_CODE)
      .docEntry(UPDATED_DOC_ENTRY)
      .totalFrgn(UPDATED_TOTAL_FRGN)
      .vatGroup(UPDATED_VAT_GROUP)
      .uomCode(UPDATED_UOM_CODE)
      .unitMsr(UPDATED_UNIT_MSR)
      .lineVendor(UPDATED_LINE_VENDOR)
      .trgetEntry(UPDATED_TRGET_ENTRY)
      .lineTotal(UPDATED_LINE_TOTAL)
      .vatPrcnt(UPDATED_VAT_PRCNT)
      .priceAfVat(UPDATED_PRICE_AF_VAT)
      .whsCode(UPDATED_WHS_CODE);
  }

  @BeforeEach
  void initTest() {
    sapPor1R1 = createEntity();
  }

  @AfterEach
  void cleanup() {
    if (insertedSapPor1R1 != null) {
      sapPor1R1Repository.delete(insertedSapPor1R1);
      insertedSapPor1R1 = null;
    }
  }

  @Test
  @Transactional
  void createSapPor1R1() throws Exception {
    long databaseSizeBeforeCreate = getRepositoryCount();
    // Create the SapPor1R1
    SapPor1R1DTO sapPor1R1DTO = sapPor1R1Mapper.toDto(sapPor1R1);
    var returnedSapPor1R1DTO = om.readValue(
      restSapPor1R1MockMvc
        .perform(
          post(ENTITY_API_URL)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsBytes(sapPor1R1DTO))
        )
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString(),
      SapPor1R1DTO.class
    );

    // Validate the SapPor1R1 in the database
    assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
    var returnedSapPor1R1 = sapPor1R1Mapper.toEntity(returnedSapPor1R1DTO);
    assertSapPor1R1UpdatableFieldsEquals(
      returnedSapPor1R1,
      getPersistedSapPor1R1(returnedSapPor1R1)
    );

    insertedSapPor1R1 = returnedSapPor1R1;
  }

  @Test
  @Transactional
  void createSapPor1R1WithExistingId() throws Exception {
    // Create the SapPor1R1 with an existing ID
    sapPor1R1.setId(1L);
    SapPor1R1DTO sapPor1R1DTO = sapPor1R1Mapper.toDto(sapPor1R1);

    long databaseSizeBeforeCreate = getRepositoryCount();

    // An entity with an existing ID cannot be created, so this API call must fail
    restSapPor1R1MockMvc
      .perform(
        post(ENTITY_API_URL)
          .with(csrf())
          .contentType(MediaType.APPLICATION_JSON)
          .content(om.writeValueAsBytes(sapPor1R1DTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the SapPor1R1 in the database
    assertSameRepositoryCount(databaseSizeBeforeCreate);
  }

  @Test
  @Transactional
  void getAllSapPor1R1s() throws Exception {
    // Initialize the database
    insertedSapPor1R1 = sapPor1R1Repository.saveAndFlush(sapPor1R1);

    // Get all the sapPor1R1List
    restSapPor1R1MockMvc
      .perform(get(ENTITY_API_URL + "?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(
        jsonPath("$.[*].id").value(hasItem(sapPor1R1.getId().intValue()))
      )
      .andExpect(jsonPath("$.[*].lineNum").value(hasItem(DEFAULT_LINE_NUM)))
      .andExpect(jsonPath("$.[*].baseRef").value(hasItem(DEFAULT_BASE_REF)))
      .andExpect(jsonPath("$.[*].baseEntry").value(hasItem(DEFAULT_BASE_ENTRY)))
      .andExpect(jsonPath("$.[*].baseLine").value(hasItem(DEFAULT_BASE_LINE)))
      .andExpect(
        jsonPath("$.[*].lineStatus").value(hasItem(DEFAULT_LINE_STATUS))
      )
      .andExpect(jsonPath("$.[*].itemCode").value(hasItem(DEFAULT_ITEM_CODE)))
      .andExpect(
        jsonPath("$.[*].dscription").value(hasItem(DEFAULT_DSCRIPTION))
      )
      .andExpect(
        jsonPath("$.[*].quantity").value(hasItem(sameNumber(DEFAULT_QUANTITY)))
      )
      .andExpect(
        jsonPath("$.[*].shipDate").value(hasItem(DEFAULT_SHIP_DATE.toString()))
      )
      .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
      .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
      .andExpect(jsonPath("$.[*].discPrcnt").value(hasItem(DEFAULT_DISC_PRCNT)))
      .andExpect(
        jsonPath("$.[*].totalSumSy").value(hasItem(DEFAULT_TOTAL_SUM_SY))
      )
      .andExpect(
        jsonPath("$.[*].openSumSys").value(hasItem(DEFAULT_OPEN_SUM_SYS))
      )
      .andExpect(
        jsonPath("$.[*].invntSttus").value(hasItem(DEFAULT_INVNT_STTUS))
      )
      .andExpect(
        jsonPath("$.[*].baseDocNum").value(hasItem(DEFAULT_BASE_DOC_NUM))
      )
      .andExpect(
        jsonPath("$.[*].uTenkythuat").value(hasItem(DEFAULT_U_TENKYTHUAT))
      )
      .andExpect(jsonPath("$.[*].uSo").value(hasItem(DEFAULT_U_SO)))
      .andExpect(jsonPath("$.[*].uMCode").value(hasItem(DEFAULT_U_M_CODE)))
      .andExpect(jsonPath("$.[*].docEntry").value(hasItem(DEFAULT_DOC_ENTRY)))
      .andExpect(jsonPath("$.[*].totalFrgn").value(hasItem(DEFAULT_TOTAL_FRGN)))
      .andExpect(jsonPath("$.[*].vatGroup").value(hasItem(DEFAULT_VAT_GROUP)))
      .andExpect(jsonPath("$.[*].uomCode").value(hasItem(DEFAULT_UOM_CODE)))
      .andExpect(jsonPath("$.[*].unitMsr").value(hasItem(DEFAULT_UNIT_MSR)))
      .andExpect(
        jsonPath("$.[*].lineVendor").value(hasItem(DEFAULT_LINE_VENDOR))
      )
      .andExpect(
        jsonPath("$.[*].trgetEntry").value(hasItem(DEFAULT_TRGET_ENTRY))
      )
      .andExpect(
        jsonPath("$.[*].lineTotal").value(
          hasItem(sameNumber(DEFAULT_LINE_TOTAL))
        )
      )
      .andExpect(
        jsonPath("$.[*].vatPrcnt").value(hasItem(sameNumber(DEFAULT_VAT_PRCNT)))
      )
      .andExpect(
        jsonPath("$.[*].priceAfVat").value(
          hasItem(sameNumber(DEFAULT_PRICE_AF_VAT))
        )
      )
      .andExpect(jsonPath("$.[*].whsCode").value(hasItem(DEFAULT_WHS_CODE)));
  }

  @Test
  @Transactional
  void getSapPor1R1() throws Exception {
    // Initialize the database
    insertedSapPor1R1 = sapPor1R1Repository.saveAndFlush(sapPor1R1);

    // Get the sapPor1R1
    restSapPor1R1MockMvc
      .perform(get(ENTITY_API_URL_ID, sapPor1R1.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").value(sapPor1R1.getId().intValue()))
      .andExpect(jsonPath("$.lineNum").value(DEFAULT_LINE_NUM))
      .andExpect(jsonPath("$.baseRef").value(DEFAULT_BASE_REF))
      .andExpect(jsonPath("$.baseEntry").value(DEFAULT_BASE_ENTRY))
      .andExpect(jsonPath("$.baseLine").value(DEFAULT_BASE_LINE))
      .andExpect(jsonPath("$.lineStatus").value(DEFAULT_LINE_STATUS))
      .andExpect(jsonPath("$.itemCode").value(DEFAULT_ITEM_CODE))
      .andExpect(jsonPath("$.dscription").value(DEFAULT_DSCRIPTION))
      .andExpect(jsonPath("$.quantity").value(sameNumber(DEFAULT_QUANTITY)))
      .andExpect(jsonPath("$.shipDate").value(DEFAULT_SHIP_DATE.toString()))
      .andExpect(jsonPath("$.price").value(DEFAULT_PRICE))
      .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY))
      .andExpect(jsonPath("$.discPrcnt").value(DEFAULT_DISC_PRCNT))
      .andExpect(jsonPath("$.totalSumSy").value(DEFAULT_TOTAL_SUM_SY))
      .andExpect(jsonPath("$.openSumSys").value(DEFAULT_OPEN_SUM_SYS))
      .andExpect(jsonPath("$.invntSttus").value(DEFAULT_INVNT_STTUS))
      .andExpect(jsonPath("$.baseDocNum").value(DEFAULT_BASE_DOC_NUM))
      .andExpect(jsonPath("$.uTenkythuat").value(DEFAULT_U_TENKYTHUAT))
      .andExpect(jsonPath("$.uSo").value(DEFAULT_U_SO))
      .andExpect(jsonPath("$.uMCode").value(DEFAULT_U_M_CODE))
      .andExpect(jsonPath("$.docEntry").value(DEFAULT_DOC_ENTRY))
      .andExpect(jsonPath("$.totalFrgn").value(DEFAULT_TOTAL_FRGN))
      .andExpect(jsonPath("$.vatGroup").value(DEFAULT_VAT_GROUP))
      .andExpect(jsonPath("$.uomCode").value(DEFAULT_UOM_CODE))
      .andExpect(jsonPath("$.unitMsr").value(DEFAULT_UNIT_MSR))
      .andExpect(jsonPath("$.lineVendor").value(DEFAULT_LINE_VENDOR))
      .andExpect(jsonPath("$.trgetEntry").value(DEFAULT_TRGET_ENTRY))
      .andExpect(jsonPath("$.lineTotal").value(sameNumber(DEFAULT_LINE_TOTAL)))
      .andExpect(jsonPath("$.vatPrcnt").value(sameNumber(DEFAULT_VAT_PRCNT)))
      .andExpect(
        jsonPath("$.priceAfVat").value(sameNumber(DEFAULT_PRICE_AF_VAT))
      )
      .andExpect(jsonPath("$.whsCode").value(DEFAULT_WHS_CODE));
  }

  @Test
  @Transactional
  void getNonExistingSapPor1R1() throws Exception {
    // Get the sapPor1R1
    restSapPor1R1MockMvc
      .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
      .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  void putExistingSapPor1R1() throws Exception {
    // Initialize the database
    insertedSapPor1R1 = sapPor1R1Repository.saveAndFlush(sapPor1R1);

    long databaseSizeBeforeUpdate = getRepositoryCount();

    // Update the sapPor1R1
    SapPor1R1 updatedSapPor1R1 = sapPor1R1Repository
      .findById(sapPor1R1.getId())
      .orElseThrow();
    // Disconnect from session so that the updates on updatedSapPor1R1 are not directly saved in db
    em.detach(updatedSapPor1R1);
    updatedSapPor1R1
      .lineNum(UPDATED_LINE_NUM)
      .baseRef(UPDATED_BASE_REF)
      .baseEntry(UPDATED_BASE_ENTRY)
      .baseLine(UPDATED_BASE_LINE)
      .lineStatus(UPDATED_LINE_STATUS)
      .itemCode(UPDATED_ITEM_CODE)
      .dscription(UPDATED_DSCRIPTION)
      .quantity(UPDATED_QUANTITY)
      .shipDate(UPDATED_SHIP_DATE)
      .price(UPDATED_PRICE)
      .currency(UPDATED_CURRENCY)
      .discPrcnt(UPDATED_DISC_PRCNT)
      .totalSumSy(UPDATED_TOTAL_SUM_SY)
      .openSumSys(UPDATED_OPEN_SUM_SYS)
      .invntSttus(UPDATED_INVNT_STTUS)
      .baseDocNum(UPDATED_BASE_DOC_NUM)
      .uTenkythuat(UPDATED_U_TENKYTHUAT)
      .uSo(UPDATED_U_SO)
      .uMCode(UPDATED_U_M_CODE)
      .docEntry(UPDATED_DOC_ENTRY)
      .totalFrgn(UPDATED_TOTAL_FRGN)
      .vatGroup(UPDATED_VAT_GROUP)
      .uomCode(UPDATED_UOM_CODE)
      .unitMsr(UPDATED_UNIT_MSR)
      .lineVendor(UPDATED_LINE_VENDOR)
      .trgetEntry(UPDATED_TRGET_ENTRY)
      .lineTotal(UPDATED_LINE_TOTAL)
      .vatPrcnt(UPDATED_VAT_PRCNT)
      .priceAfVat(UPDATED_PRICE_AF_VAT)
      .whsCode(UPDATED_WHS_CODE);
    SapPor1R1DTO sapPor1R1DTO = sapPor1R1Mapper.toDto(updatedSapPor1R1);

    restSapPor1R1MockMvc
      .perform(
        put(ENTITY_API_URL_ID, sapPor1R1DTO.getId())
          .with(csrf())
          .contentType(MediaType.APPLICATION_JSON)
          .content(om.writeValueAsBytes(sapPor1R1DTO))
      )
      .andExpect(status().isOk());

    // Validate the SapPor1R1 in the database
    assertSameRepositoryCount(databaseSizeBeforeUpdate);
    assertPersistedSapPor1R1ToMatchAllProperties(updatedSapPor1R1);
  }

  @Test
  @Transactional
  void putNonExistingSapPor1R1() throws Exception {
    long databaseSizeBeforeUpdate = getRepositoryCount();
    sapPor1R1.setId(longCount.incrementAndGet());

    // Create the SapPor1R1
    SapPor1R1DTO sapPor1R1DTO = sapPor1R1Mapper.toDto(sapPor1R1);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restSapPor1R1MockMvc
      .perform(
        put(ENTITY_API_URL_ID, sapPor1R1DTO.getId())
          .with(csrf())
          .contentType(MediaType.APPLICATION_JSON)
          .content(om.writeValueAsBytes(sapPor1R1DTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the SapPor1R1 in the database
    assertSameRepositoryCount(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void putWithIdMismatchSapPor1R1() throws Exception {
    long databaseSizeBeforeUpdate = getRepositoryCount();
    sapPor1R1.setId(longCount.incrementAndGet());

    // Create the SapPor1R1
    SapPor1R1DTO sapPor1R1DTO = sapPor1R1Mapper.toDto(sapPor1R1);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restSapPor1R1MockMvc
      .perform(
        put(ENTITY_API_URL_ID, longCount.incrementAndGet())
          .with(csrf())
          .contentType(MediaType.APPLICATION_JSON)
          .content(om.writeValueAsBytes(sapPor1R1DTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the SapPor1R1 in the database
    assertSameRepositoryCount(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void putWithMissingIdPathParamSapPor1R1() throws Exception {
    long databaseSizeBeforeUpdate = getRepositoryCount();
    sapPor1R1.setId(longCount.incrementAndGet());

    // Create the SapPor1R1
    SapPor1R1DTO sapPor1R1DTO = sapPor1R1Mapper.toDto(sapPor1R1);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restSapPor1R1MockMvc
      .perform(
        put(ENTITY_API_URL)
          .with(csrf())
          .contentType(MediaType.APPLICATION_JSON)
          .content(om.writeValueAsBytes(sapPor1R1DTO))
      )
      .andExpect(status().isMethodNotAllowed());

    // Validate the SapPor1R1 in the database
    assertSameRepositoryCount(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void partialUpdateSapPor1R1WithPatch() throws Exception {
    // Initialize the database
    insertedSapPor1R1 = sapPor1R1Repository.saveAndFlush(sapPor1R1);

    long databaseSizeBeforeUpdate = getRepositoryCount();

    // Update the sapPor1R1 using partial update
    SapPor1R1 partialUpdatedSapPor1R1 = new SapPor1R1();
    partialUpdatedSapPor1R1.setId(sapPor1R1.getId());

    partialUpdatedSapPor1R1
      .lineNum(UPDATED_LINE_NUM)
      .quantity(UPDATED_QUANTITY)
      .shipDate(UPDATED_SHIP_DATE)
      .price(UPDATED_PRICE)
      .openSumSys(UPDATED_OPEN_SUM_SYS)
      .invntSttus(UPDATED_INVNT_STTUS)
      .baseDocNum(UPDATED_BASE_DOC_NUM)
      .uSo(UPDATED_U_SO)
      .vatGroup(UPDATED_VAT_GROUP)
      .uomCode(UPDATED_UOM_CODE)
      .trgetEntry(UPDATED_TRGET_ENTRY)
      .lineTotal(UPDATED_LINE_TOTAL)
      .priceAfVat(UPDATED_PRICE_AF_VAT)
      .whsCode(UPDATED_WHS_CODE);

    restSapPor1R1MockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedSapPor1R1.getId())
          .with(csrf())
          .contentType("application/merge-patch+json")
          .content(om.writeValueAsBytes(partialUpdatedSapPor1R1))
      )
      .andExpect(status().isOk());

    // Validate the SapPor1R1 in the database

    assertSameRepositoryCount(databaseSizeBeforeUpdate);
    assertSapPor1R1UpdatableFieldsEquals(
      createUpdateProxyForBean(partialUpdatedSapPor1R1, sapPor1R1),
      getPersistedSapPor1R1(sapPor1R1)
    );
  }

  @Test
  @Transactional
  void fullUpdateSapPor1R1WithPatch() throws Exception {
    // Initialize the database
    insertedSapPor1R1 = sapPor1R1Repository.saveAndFlush(sapPor1R1);

    long databaseSizeBeforeUpdate = getRepositoryCount();

    // Update the sapPor1R1 using partial update
    SapPor1R1 partialUpdatedSapPor1R1 = new SapPor1R1();
    partialUpdatedSapPor1R1.setId(sapPor1R1.getId());

    partialUpdatedSapPor1R1
      .lineNum(UPDATED_LINE_NUM)
      .baseRef(UPDATED_BASE_REF)
      .baseEntry(UPDATED_BASE_ENTRY)
      .baseLine(UPDATED_BASE_LINE)
      .lineStatus(UPDATED_LINE_STATUS)
      .itemCode(UPDATED_ITEM_CODE)
      .dscription(UPDATED_DSCRIPTION)
      .quantity(UPDATED_QUANTITY)
      .shipDate(UPDATED_SHIP_DATE)
      .price(UPDATED_PRICE)
      .currency(UPDATED_CURRENCY)
      .discPrcnt(UPDATED_DISC_PRCNT)
      .totalSumSy(UPDATED_TOTAL_SUM_SY)
      .openSumSys(UPDATED_OPEN_SUM_SYS)
      .invntSttus(UPDATED_INVNT_STTUS)
      .baseDocNum(UPDATED_BASE_DOC_NUM)
      .uTenkythuat(UPDATED_U_TENKYTHUAT)
      .uSo(UPDATED_U_SO)
      .uMCode(UPDATED_U_M_CODE)
      .docEntry(UPDATED_DOC_ENTRY)
      .totalFrgn(UPDATED_TOTAL_FRGN)
      .vatGroup(UPDATED_VAT_GROUP)
      .uomCode(UPDATED_UOM_CODE)
      .unitMsr(UPDATED_UNIT_MSR)
      .lineVendor(UPDATED_LINE_VENDOR)
      .trgetEntry(UPDATED_TRGET_ENTRY)
      .lineTotal(UPDATED_LINE_TOTAL)
      .vatPrcnt(UPDATED_VAT_PRCNT)
      .priceAfVat(UPDATED_PRICE_AF_VAT)
      .whsCode(UPDATED_WHS_CODE);

    restSapPor1R1MockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedSapPor1R1.getId())
          .with(csrf())
          .contentType("application/merge-patch+json")
          .content(om.writeValueAsBytes(partialUpdatedSapPor1R1))
      )
      .andExpect(status().isOk());

    // Validate the SapPor1R1 in the database

    assertSameRepositoryCount(databaseSizeBeforeUpdate);
    assertSapPor1R1UpdatableFieldsEquals(
      partialUpdatedSapPor1R1,
      getPersistedSapPor1R1(partialUpdatedSapPor1R1)
    );
  }

  @Test
  @Transactional
  void patchNonExistingSapPor1R1() throws Exception {
    long databaseSizeBeforeUpdate = getRepositoryCount();
    sapPor1R1.setId(longCount.incrementAndGet());

    // Create the SapPor1R1
    SapPor1R1DTO sapPor1R1DTO = sapPor1R1Mapper.toDto(sapPor1R1);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restSapPor1R1MockMvc
      .perform(
        patch(ENTITY_API_URL_ID, sapPor1R1DTO.getId())
          .with(csrf())
          .contentType("application/merge-patch+json")
          .content(om.writeValueAsBytes(sapPor1R1DTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the SapPor1R1 in the database
    assertSameRepositoryCount(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void patchWithIdMismatchSapPor1R1() throws Exception {
    long databaseSizeBeforeUpdate = getRepositoryCount();
    sapPor1R1.setId(longCount.incrementAndGet());

    // Create the SapPor1R1
    SapPor1R1DTO sapPor1R1DTO = sapPor1R1Mapper.toDto(sapPor1R1);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restSapPor1R1MockMvc
      .perform(
        patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
          .with(csrf())
          .contentType("application/merge-patch+json")
          .content(om.writeValueAsBytes(sapPor1R1DTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the SapPor1R1 in the database
    assertSameRepositoryCount(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void patchWithMissingIdPathParamSapPor1R1() throws Exception {
    long databaseSizeBeforeUpdate = getRepositoryCount();
    sapPor1R1.setId(longCount.incrementAndGet());

    // Create the SapPor1R1
    SapPor1R1DTO sapPor1R1DTO = sapPor1R1Mapper.toDto(sapPor1R1);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restSapPor1R1MockMvc
      .perform(
        patch(ENTITY_API_URL)
          .with(csrf())
          .contentType("application/merge-patch+json")
          .content(om.writeValueAsBytes(sapPor1R1DTO))
      )
      .andExpect(status().isMethodNotAllowed());

    // Validate the SapPor1R1 in the database
    assertSameRepositoryCount(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void deleteSapPor1R1() throws Exception {
    // Initialize the database
    insertedSapPor1R1 = sapPor1R1Repository.saveAndFlush(sapPor1R1);

    long databaseSizeBeforeDelete = getRepositoryCount();

    // Delete the sapPor1R1
    restSapPor1R1MockMvc
      .perform(
        delete(ENTITY_API_URL_ID, sapPor1R1.getId())
          .with(csrf())
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNoContent());

    // Validate the database contains one less item
    assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
  }

  protected long getRepositoryCount() {
    return sapPor1R1Repository.count();
  }

  protected void assertIncrementedRepositoryCount(long countBefore) {
    assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
  }

  protected void assertDecrementedRepositoryCount(long countBefore) {
    assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
  }

  protected void assertSameRepositoryCount(long countBefore) {
    assertThat(countBefore).isEqualTo(getRepositoryCount());
  }

  protected SapPor1R1 getPersistedSapPor1R1(SapPor1R1 sapPor1R1) {
    return sapPor1R1Repository.findById(sapPor1R1.getId()).orElseThrow();
  }

  protected void assertPersistedSapPor1R1ToMatchAllProperties(
    SapPor1R1 expectedSapPor1R1
  ) {
    assertSapPor1R1AllPropertiesEquals(
      expectedSapPor1R1,
      getPersistedSapPor1R1(expectedSapPor1R1)
    );
  }

  protected void assertPersistedSapPor1R1ToMatchUpdatableProperties(
    SapPor1R1 expectedSapPor1R1
  ) {
    assertSapPor1R1AllUpdatablePropertiesEquals(
      expectedSapPor1R1,
      getPersistedSapPor1R1(expectedSapPor1R1)
    );
  }
}
