package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.SapPoInfoAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.SapPoInfo;
import com.mycompany.myapp.repository.partner6.SapPoInfoRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
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
 * Integration tests for the {@link SapPoInfoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SapPoInfoResourceIT {

    private static final String DEFAULT_OPOR_BRANCH = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_BRANCH = "BBBBBBBBBB";

    private static final String DEFAULT_OPOR_CANCELED = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_CANCELED = "BBBBBBBBBB";

    private static final String DEFAULT_OPOR_CARD_CODE = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_CARD_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_OPOR_CARD_NAME = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_CARD_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_OPOR_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_COMMENTS = "BBBBBBBBBB";

    private static final Instant DEFAULT_OPOR_CREATE_DATE =
        Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_OPOR_CREATE_DATE =
        Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_OPOR_DEPARTMENT = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_DEPARTMENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_OPOR_DOC_DATE = Instant.ofEpochMilli(
        0L
    );
    private static final Instant UPDATED_OPOR_DOC_DATE =
        Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_OPOR_DOC_DUE_DATE =
        Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_OPOR_DOC_DUE_DATE =
        Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_OPOR_DOC_ENTRY = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_DOC_ENTRY = "BBBBBBBBBB";

    private static final String DEFAULT_OPOR_DOC_NUM = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_DOC_NUM = "BBBBBBBBBB";

    private static final String DEFAULT_OPOR_DOC_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_DOC_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_OPOR_INVNT_STTUS = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_INVNT_STTUS = "BBBBBBBBBB";

    private static final String DEFAULT_OPOR_JRNL_MEMO = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_JRNL_MEMO = "BBBBBBBBBB";

    private static final String DEFAULT_OPOR_U_CO_ADD = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_U_CO_ADD = "BBBBBBBBBB";

    private static final String DEFAULT_OPOR_U_CODE_INV = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_U_CODE_INV = "BBBBBBBBBB";

    private static final String DEFAULT_OPOR_U_CODE_SERIAL = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_U_CODE_SERIAL = "BBBBBBBBBB";

    private static final String DEFAULT_OPOR_U_CONTRACT_DATE = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_U_CONTRACT_DATE = "BBBBBBBBBB";

    private static final String DEFAULT_OPOR_U_DECLARE_PD = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_U_DECLARE_PD = "BBBBBBBBBB";

    private static final String DEFAULT_OPOR_U_DOC_NUM = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_U_DOC_NUM = "BBBBBBBBBB";

    private static final String DEFAULT_OPOR_U_INV_CODE = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_U_INV_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_OPOR_U_INV_CODE_2 = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_U_INV_CODE_2 = "BBBBBBBBBB";

    private static final String DEFAULT_OPOR_U_INV_SERIAL = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_U_INV_SERIAL = "BBBBBBBBBB";

    private static final String DEFAULT_OPOR_U_PUR_NV_GIAO = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_U_PUR_NV_GIAO = "BBBBBBBBBB";

    private static final Instant DEFAULT_OPOR_UPDATE_DATE =
        Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_OPOR_UPDATE_DATE =
        Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_OPOR_USER_SIGN = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_USER_SIGN = "BBBBBBBBBB";

    private static final Instant DEFAULT_OPOR_TAX_DATE = Instant.ofEpochMilli(
        0L
    );
    private static final Instant UPDATED_OPOR_TAX_DATE =
        Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_OPOR_CNTCT_CODE = 1;
    private static final Integer UPDATED_OPOR_CNTCT_CODE = 2;
    private static final Integer SMALLER_OPOR_CNTCT_CODE = 1 - 1;

    private static final String DEFAULT_OPOR_NUM_AT_CARD = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_NUM_AT_CARD = "BBBBBBBBBB";

    private static final Integer DEFAULT_OPOR_SLP_CODE = 1;
    private static final Integer UPDATED_OPOR_SLP_CODE = 2;
    private static final Integer SMALLER_OPOR_SLP_CODE = 1 - 1;

    private static final Integer DEFAULT_OPOR_OWNER_CODE = 1;
    private static final Integer UPDATED_OPOR_OWNER_CODE = 2;
    private static final Integer SMALLER_OPOR_OWNER_CODE = 1 - 1;

    private static final BigDecimal DEFAULT_OPOR_VAT_SUM = new BigDecimal(1);
    private static final BigDecimal UPDATED_OPOR_VAT_SUM = new BigDecimal(2);
    private static final BigDecimal SMALLER_OPOR_VAT_SUM = new BigDecimal(
        1 - 1
    );

    private static final BigDecimal DEFAULT_OPOR_DOC_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_OPOR_DOC_TOTAL = new BigDecimal(2);
    private static final BigDecimal SMALLER_OPOR_DOC_TOTAL = new BigDecimal(
        1 - 1
    );

    private static final BigDecimal DEFAULT_OPOR_VAT_SUM_SY = new BigDecimal(1);
    private static final BigDecimal UPDATED_OPOR_VAT_SUM_SY = new BigDecimal(2);
    private static final BigDecimal SMALLER_OPOR_VAT_SUM_SY = new BigDecimal(
        1 - 1
    );

    private static final String DEFAULT_OPOR_U_HT = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_U_HT = "BBBBBBBBBB";

    private static final String DEFAULT_OPOR_U_PAYMENT = "AAAAAAAAAA";
    private static final String UPDATED_OPOR_U_PAYMENT = "BBBBBBBBBB";

    private static final String DEFAULT_POR_1_BASE_DOC_NUM = "AAAAAAAAAA";
    private static final String UPDATED_POR_1_BASE_DOC_NUM = "BBBBBBBBBB";

    private static final String DEFAULT_POR_1_BASE_ENTRY = "AAAAAAAAAA";
    private static final String UPDATED_POR_1_BASE_ENTRY = "BBBBBBBBBB";

    private static final String DEFAULT_POR_1_BASE_LINE = "AAAAAAAAAA";
    private static final String UPDATED_POR_1_BASE_LINE = "BBBBBBBBBB";

    private static final String DEFAULT_POR_1_BASE_REF = "AAAAAAAAAA";
    private static final String UPDATED_POR_1_BASE_REF = "BBBBBBBBBB";

    private static final String DEFAULT_POR_1_CURRENCY = "AAAAAAAAAA";
    private static final String UPDATED_POR_1_CURRENCY = "BBBBBBBBBB";

    private static final String DEFAULT_POR_1_DISC_PRCNT = "AAAAAAAAAA";
    private static final String UPDATED_POR_1_DISC_PRCNT = "BBBBBBBBBB";

    private static final String DEFAULT_PO_DOC_ENTRY = "AAAAAAAAAA";
    private static final String UPDATED_PO_DOC_ENTRY = "BBBBBBBBBB";

    private static final String DEFAULT_POR_1_DSCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_POR_1_DSCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_POR_1_ITEM_CODE = "AAAAAAAAAA";
    private static final String UPDATED_POR_1_ITEM_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_POR_1_LINE_NUM = "AAAAAAAAAA";
    private static final String UPDATED_POR_1_LINE_NUM = "BBBBBBBBBB";

    private static final String DEFAULT_POR_1_LINE_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_POR_1_LINE_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_POR_1_LINE_VENDOR = "AAAAAAAAAA";
    private static final String UPDATED_POR_1_LINE_VENDOR = "BBBBBBBBBB";

    private static final String DEFAULT_POR_1_OPEN_SUM_SYS = "AAAAAAAAAA";
    private static final String UPDATED_POR_1_OPEN_SUM_SYS = "BBBBBBBBBB";

    private static final String DEFAULT_POR_1_PRICE = "AAAAAAAAAA";
    private static final String UPDATED_POR_1_PRICE = "BBBBBBBBBB";

    private static final String DEFAULT_POR_1_QUANTITY = "AAAAAAAAAA";
    private static final String UPDATED_POR_1_QUANTITY = "BBBBBBBBBB";

    private static final Instant DEFAULT_POR_1_SHIP_DATE = Instant.ofEpochMilli(
        0L
    );
    private static final Instant UPDATED_POR_1_SHIP_DATE =
        Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_POR_1_TOTAL_FRGN = "AAAAAAAAAA";
    private static final String UPDATED_POR_1_TOTAL_FRGN = "BBBBBBBBBB";

    private static final String DEFAULT_POR_1_TOTAL_SUMSY = "AAAAAAAAAA";
    private static final String UPDATED_POR_1_TOTAL_SUMSY = "BBBBBBBBBB";

    private static final String DEFAULT_POR_1_TRGET_ENTRY = "AAAAAAAAAA";
    private static final String UPDATED_POR_1_TRGET_ENTRY = "BBBBBBBBBB";

    private static final String DEFAULT_POR_1_U_MCODE = "AAAAAAAAAA";
    private static final String UPDATED_POR_1_U_MCODE = "BBBBBBBBBB";

    private static final String DEFAULT_POR_1_U_SO = "AAAAAAAAAA";
    private static final String UPDATED_POR_1_U_SO = "BBBBBBBBBB";

    private static final String DEFAULT_POR_1_U_TENKYTHUAT = "AAAAAAAAAA";
    private static final String UPDATED_POR_1_U_TENKYTHUAT = "BBBBBBBBBB";

    private static final String DEFAULT_POR_1_UNIT_MSR = "AAAAAAAAAA";
    private static final String UPDATED_POR_1_UNIT_MSR = "BBBBBBBBBB";

    private static final String DEFAULT_POR_1_UOM_CODE = "AAAAAAAAAA";
    private static final String UPDATED_POR_1_UOM_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_POR_1_VAT_GROUP = "AAAAAAAAAA";
    private static final String UPDATED_POR_1_VAT_GROUP = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_POR_1_LINE_TOTAL = new BigDecimal(
        1
    );
    private static final BigDecimal UPDATED_POR_1_LINE_TOTAL = new BigDecimal(
        2
    );
    private static final BigDecimal SMALLER_POR_1_LINE_TOTAL = new BigDecimal(
        1 - 1
    );

    private static final BigDecimal DEFAULT_POR_1_VAT_PRCNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_POR_1_VAT_PRCNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_POR_1_VAT_PRCNT = new BigDecimal(
        1 - 1
    );

    private static final BigDecimal DEFAULT_POR_1_PRICE_AF_VAT = new BigDecimal(
        1
    );
    private static final BigDecimal UPDATED_POR_1_PRICE_AF_VAT = new BigDecimal(
        2
    );
    private static final BigDecimal SMALLER_POR_1_PRICE_AF_VAT = new BigDecimal(
        1 - 1
    );

    private static final String DEFAULT_POR_1_WHS_CODE = "AAAAAAAAAA";
    private static final String UPDATED_POR_1_WHS_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_PR_MAP_PO = "AAAAAAAAAA";
    private static final String UPDATED_PR_MAP_PO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sap-po-infos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SapPoInfoRepository sapPoInfoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSapPoInfoMockMvc;

    private SapPoInfo sapPoInfo;

    private SapPoInfo insertedSapPoInfo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SapPoInfo createEntity() {
        return new SapPoInfo()
            .oporBranch(DEFAULT_OPOR_BRANCH)
            .oporCanceled(DEFAULT_OPOR_CANCELED)
            .oporCardCode(DEFAULT_OPOR_CARD_CODE)
            .oporCardName(DEFAULT_OPOR_CARD_NAME)
            .oporComments(DEFAULT_OPOR_COMMENTS)
            .oporCreateDate(DEFAULT_OPOR_CREATE_DATE)
            .oporDepartment(DEFAULT_OPOR_DEPARTMENT)
            .oporDocDate(DEFAULT_OPOR_DOC_DATE)
            .oporDocDueDate(DEFAULT_OPOR_DOC_DUE_DATE)
            .oporDocEntry(DEFAULT_OPOR_DOC_ENTRY)
            .oporDocNum(DEFAULT_OPOR_DOC_NUM)
            .oporDocStatus(DEFAULT_OPOR_DOC_STATUS)
            .oporInvntSttus(DEFAULT_OPOR_INVNT_STTUS)
            .oporJrnlMemo(DEFAULT_OPOR_JRNL_MEMO)
            .oporUCoAdd(DEFAULT_OPOR_U_CO_ADD)
            .oporUCodeInv(DEFAULT_OPOR_U_CODE_INV)
            .oporUCodeSerial(DEFAULT_OPOR_U_CODE_SERIAL)
            .oporUContractDate(DEFAULT_OPOR_U_CONTRACT_DATE)
            .oporUDeclarePd(DEFAULT_OPOR_U_DECLARE_PD)
            .oporUDocNum(DEFAULT_OPOR_U_DOC_NUM)
            .oporUInvCode(DEFAULT_OPOR_U_INV_CODE)
            .oporUInvCode2(DEFAULT_OPOR_U_INV_CODE_2)
            .oporUInvSerial(DEFAULT_OPOR_U_INV_SERIAL)
            .oporUPurNVGiao(DEFAULT_OPOR_U_PUR_NV_GIAO)
            .oporUpdateDate(DEFAULT_OPOR_UPDATE_DATE)
            .oporUserSign(DEFAULT_OPOR_USER_SIGN)
            .oporTaxDate(DEFAULT_OPOR_TAX_DATE)
            .oporCntctCode(DEFAULT_OPOR_CNTCT_CODE)
            .oporNumAtCard(DEFAULT_OPOR_NUM_AT_CARD)
            .oporSlpCode(DEFAULT_OPOR_SLP_CODE)
            .oporOwnerCode(DEFAULT_OPOR_OWNER_CODE)
            .oporVatSum(DEFAULT_OPOR_VAT_SUM)
            .oporDocTotal(DEFAULT_OPOR_DOC_TOTAL)
            .oporVatSumSy(DEFAULT_OPOR_VAT_SUM_SY)
            .oporUHt(DEFAULT_OPOR_U_HT)
            .oporUPayment(DEFAULT_OPOR_U_PAYMENT)
            .por1BaseDocNum(DEFAULT_POR_1_BASE_DOC_NUM)
            .por1BaseEntry(DEFAULT_POR_1_BASE_ENTRY)
            .por1BaseLine(DEFAULT_POR_1_BASE_LINE)
            .por1BaseRef(DEFAULT_POR_1_BASE_REF)
            .por1Currency(DEFAULT_POR_1_CURRENCY)
            .por1DiscPrcnt(DEFAULT_POR_1_DISC_PRCNT)
            .poDocEntry(DEFAULT_PO_DOC_ENTRY)
            .por1Dscription(DEFAULT_POR_1_DSCRIPTION)
            .por1ItemCode(DEFAULT_POR_1_ITEM_CODE)
            .por1LineNum(DEFAULT_POR_1_LINE_NUM)
            .por1LineStatus(DEFAULT_POR_1_LINE_STATUS)
            .por1LineVendor(DEFAULT_POR_1_LINE_VENDOR)
            .por1OpenSumSys(DEFAULT_POR_1_OPEN_SUM_SYS)
            .por1Price(DEFAULT_POR_1_PRICE)
            .por1Quantity(DEFAULT_POR_1_QUANTITY)
            .por1ShipDate(DEFAULT_POR_1_SHIP_DATE)
            .por1TotalFrgn(DEFAULT_POR_1_TOTAL_FRGN)
            .por1TotalSumsy(DEFAULT_POR_1_TOTAL_SUMSY)
            .por1TrgetEntry(DEFAULT_POR_1_TRGET_ENTRY)
            .por1UMcode(DEFAULT_POR_1_U_MCODE)
            .por1USo(DEFAULT_POR_1_U_SO)
            .por1UTenkythuat(DEFAULT_POR_1_U_TENKYTHUAT)
            .por1UnitMsr(DEFAULT_POR_1_UNIT_MSR)
            .por1UOMCode(DEFAULT_POR_1_UOM_CODE)
            .por1VatGroup(DEFAULT_POR_1_VAT_GROUP)
            .por1LineTotal(DEFAULT_POR_1_LINE_TOTAL)
            .por1VatPrcnt(DEFAULT_POR_1_VAT_PRCNT)
            .por1PriceAfVat(DEFAULT_POR_1_PRICE_AF_VAT)
            .por1WhsCode(DEFAULT_POR_1_WHS_CODE)
            .prMapPo(DEFAULT_PR_MAP_PO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SapPoInfo createUpdatedEntity() {
        return new SapPoInfo()
            .oporBranch(UPDATED_OPOR_BRANCH)
            .oporCanceled(UPDATED_OPOR_CANCELED)
            .oporCardCode(UPDATED_OPOR_CARD_CODE)
            .oporCardName(UPDATED_OPOR_CARD_NAME)
            .oporComments(UPDATED_OPOR_COMMENTS)
            .oporCreateDate(UPDATED_OPOR_CREATE_DATE)
            .oporDepartment(UPDATED_OPOR_DEPARTMENT)
            .oporDocDate(UPDATED_OPOR_DOC_DATE)
            .oporDocDueDate(UPDATED_OPOR_DOC_DUE_DATE)
            .oporDocEntry(UPDATED_OPOR_DOC_ENTRY)
            .oporDocNum(UPDATED_OPOR_DOC_NUM)
            .oporDocStatus(UPDATED_OPOR_DOC_STATUS)
            .oporInvntSttus(UPDATED_OPOR_INVNT_STTUS)
            .oporJrnlMemo(UPDATED_OPOR_JRNL_MEMO)
            .oporUCoAdd(UPDATED_OPOR_U_CO_ADD)
            .oporUCodeInv(UPDATED_OPOR_U_CODE_INV)
            .oporUCodeSerial(UPDATED_OPOR_U_CODE_SERIAL)
            .oporUContractDate(UPDATED_OPOR_U_CONTRACT_DATE)
            .oporUDeclarePd(UPDATED_OPOR_U_DECLARE_PD)
            .oporUDocNum(UPDATED_OPOR_U_DOC_NUM)
            .oporUInvCode(UPDATED_OPOR_U_INV_CODE)
            .oporUInvCode2(UPDATED_OPOR_U_INV_CODE_2)
            .oporUInvSerial(UPDATED_OPOR_U_INV_SERIAL)
            .oporUPurNVGiao(UPDATED_OPOR_U_PUR_NV_GIAO)
            .oporUpdateDate(UPDATED_OPOR_UPDATE_DATE)
            .oporUserSign(UPDATED_OPOR_USER_SIGN)
            .oporTaxDate(UPDATED_OPOR_TAX_DATE)
            .oporCntctCode(UPDATED_OPOR_CNTCT_CODE)
            .oporNumAtCard(UPDATED_OPOR_NUM_AT_CARD)
            .oporSlpCode(UPDATED_OPOR_SLP_CODE)
            .oporOwnerCode(UPDATED_OPOR_OWNER_CODE)
            .oporVatSum(UPDATED_OPOR_VAT_SUM)
            .oporDocTotal(UPDATED_OPOR_DOC_TOTAL)
            .oporVatSumSy(UPDATED_OPOR_VAT_SUM_SY)
            .oporUHt(UPDATED_OPOR_U_HT)
            .oporUPayment(UPDATED_OPOR_U_PAYMENT)
            .por1BaseDocNum(UPDATED_POR_1_BASE_DOC_NUM)
            .por1BaseEntry(UPDATED_POR_1_BASE_ENTRY)
            .por1BaseLine(UPDATED_POR_1_BASE_LINE)
            .por1BaseRef(UPDATED_POR_1_BASE_REF)
            .por1Currency(UPDATED_POR_1_CURRENCY)
            .por1DiscPrcnt(UPDATED_POR_1_DISC_PRCNT)
            .poDocEntry(UPDATED_PO_DOC_ENTRY)
            .por1Dscription(UPDATED_POR_1_DSCRIPTION)
            .por1ItemCode(UPDATED_POR_1_ITEM_CODE)
            .por1LineNum(UPDATED_POR_1_LINE_NUM)
            .por1LineStatus(UPDATED_POR_1_LINE_STATUS)
            .por1LineVendor(UPDATED_POR_1_LINE_VENDOR)
            .por1OpenSumSys(UPDATED_POR_1_OPEN_SUM_SYS)
            .por1Price(UPDATED_POR_1_PRICE)
            .por1Quantity(UPDATED_POR_1_QUANTITY)
            .por1ShipDate(UPDATED_POR_1_SHIP_DATE)
            .por1TotalFrgn(UPDATED_POR_1_TOTAL_FRGN)
            .por1TotalSumsy(UPDATED_POR_1_TOTAL_SUMSY)
            .por1TrgetEntry(UPDATED_POR_1_TRGET_ENTRY)
            .por1UMcode(UPDATED_POR_1_U_MCODE)
            .por1USo(UPDATED_POR_1_U_SO)
            .por1UTenkythuat(UPDATED_POR_1_U_TENKYTHUAT)
            .por1UnitMsr(UPDATED_POR_1_UNIT_MSR)
            .por1UOMCode(UPDATED_POR_1_UOM_CODE)
            .por1VatGroup(UPDATED_POR_1_VAT_GROUP)
            .por1LineTotal(UPDATED_POR_1_LINE_TOTAL)
            .por1VatPrcnt(UPDATED_POR_1_VAT_PRCNT)
            .por1PriceAfVat(UPDATED_POR_1_PRICE_AF_VAT)
            .por1WhsCode(UPDATED_POR_1_WHS_CODE)
            .prMapPo(UPDATED_PR_MAP_PO);
    }

    @BeforeEach
    void initTest() {
        sapPoInfo = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSapPoInfo != null) {
            sapPoInfoRepository.delete(insertedSapPoInfo);
            insertedSapPoInfo = null;
        }
    }

    @Test
    @Transactional
    void createSapPoInfo() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SapPoInfo
        var returnedSapPoInfo = om.readValue(
            restSapPoInfoMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(sapPoInfo))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SapPoInfo.class
        );

        // Validate the SapPoInfo in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSapPoInfoUpdatableFieldsEquals(
            returnedSapPoInfo,
            getPersistedSapPoInfo(returnedSapPoInfo)
        );

        insertedSapPoInfo = returnedSapPoInfo;
    }

    @Test
    @Transactional
    void createSapPoInfoWithExistingId() throws Exception {
        // Create the SapPoInfo with an existing ID
        sapPoInfo.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSapPoInfoMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sapPoInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the SapPoInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSapPoInfos() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList
        restSapPoInfoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(sapPoInfo.getId().intValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].oporBranch").value(hasItem(DEFAULT_OPOR_BRANCH))
            )
            .andExpect(
                jsonPath("$.[*].oporCanceled").value(
                    hasItem(DEFAULT_OPOR_CANCELED)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporCardCode").value(
                    hasItem(DEFAULT_OPOR_CARD_CODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporCardName").value(
                    hasItem(DEFAULT_OPOR_CARD_NAME)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporComments").value(
                    hasItem(DEFAULT_OPOR_COMMENTS)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporCreateDate").value(
                    hasItem(DEFAULT_OPOR_CREATE_DATE.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].oporDepartment").value(
                    hasItem(DEFAULT_OPOR_DEPARTMENT)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporDocDate").value(
                    hasItem(DEFAULT_OPOR_DOC_DATE.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].oporDocDueDate").value(
                    hasItem(DEFAULT_OPOR_DOC_DUE_DATE.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].oporDocEntry").value(
                    hasItem(DEFAULT_OPOR_DOC_ENTRY)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporDocNum").value(
                    hasItem(DEFAULT_OPOR_DOC_NUM)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporDocStatus").value(
                    hasItem(DEFAULT_OPOR_DOC_STATUS)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporInvntSttus").value(
                    hasItem(DEFAULT_OPOR_INVNT_STTUS)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporJrnlMemo").value(
                    hasItem(DEFAULT_OPOR_JRNL_MEMO)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUCoAdd").value(
                    hasItem(DEFAULT_OPOR_U_CO_ADD)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUCodeInv").value(
                    hasItem(DEFAULT_OPOR_U_CODE_INV)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUCodeSerial").value(
                    hasItem(DEFAULT_OPOR_U_CODE_SERIAL)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUContractDate").value(
                    hasItem(DEFAULT_OPOR_U_CONTRACT_DATE)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUDeclarePd").value(
                    hasItem(DEFAULT_OPOR_U_DECLARE_PD)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUDocNum").value(
                    hasItem(DEFAULT_OPOR_U_DOC_NUM)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUInvCode").value(
                    hasItem(DEFAULT_OPOR_U_INV_CODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUInvCode2").value(
                    hasItem(DEFAULT_OPOR_U_INV_CODE_2)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUInvSerial").value(
                    hasItem(DEFAULT_OPOR_U_INV_SERIAL)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUPurNVGiao").value(
                    hasItem(DEFAULT_OPOR_U_PUR_NV_GIAO)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUpdateDate").value(
                    hasItem(DEFAULT_OPOR_UPDATE_DATE.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUserSign").value(
                    hasItem(DEFAULT_OPOR_USER_SIGN)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporTaxDate").value(
                    hasItem(DEFAULT_OPOR_TAX_DATE.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].oporCntctCode").value(
                    hasItem(DEFAULT_OPOR_CNTCT_CODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporNumAtCard").value(
                    hasItem(DEFAULT_OPOR_NUM_AT_CARD)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporSlpCode").value(
                    hasItem(DEFAULT_OPOR_SLP_CODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporOwnerCode").value(
                    hasItem(DEFAULT_OPOR_OWNER_CODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporVatSum").value(
                    hasItem(sameNumber(DEFAULT_OPOR_VAT_SUM))
                )
            )
            .andExpect(
                jsonPath("$.[*].oporDocTotal").value(
                    hasItem(sameNumber(DEFAULT_OPOR_DOC_TOTAL))
                )
            )
            .andExpect(
                jsonPath("$.[*].oporVatSumSy").value(
                    hasItem(sameNumber(DEFAULT_OPOR_VAT_SUM_SY))
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUHt").value(hasItem(DEFAULT_OPOR_U_HT))
            )
            .andExpect(
                jsonPath("$.[*].oporUPayment").value(
                    hasItem(DEFAULT_OPOR_U_PAYMENT)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1BaseDocNum").value(
                    hasItem(DEFAULT_POR_1_BASE_DOC_NUM)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1BaseEntry").value(
                    hasItem(DEFAULT_POR_1_BASE_ENTRY)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1BaseLine").value(
                    hasItem(DEFAULT_POR_1_BASE_LINE)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1BaseRef").value(
                    hasItem(DEFAULT_POR_1_BASE_REF)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1Currency").value(
                    hasItem(DEFAULT_POR_1_CURRENCY)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1DiscPrcnt").value(
                    hasItem(DEFAULT_POR_1_DISC_PRCNT)
                )
            )
            .andExpect(
                jsonPath("$.[*].poDocEntry").value(
                    hasItem(DEFAULT_PO_DOC_ENTRY)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1Dscription").value(
                    hasItem(DEFAULT_POR_1_DSCRIPTION)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1ItemCode").value(
                    hasItem(DEFAULT_POR_1_ITEM_CODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1LineNum").value(
                    hasItem(DEFAULT_POR_1_LINE_NUM)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1LineStatus").value(
                    hasItem(DEFAULT_POR_1_LINE_STATUS)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1LineVendor").value(
                    hasItem(DEFAULT_POR_1_LINE_VENDOR)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1OpenSumSys").value(
                    hasItem(DEFAULT_POR_1_OPEN_SUM_SYS)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1Price").value(hasItem(DEFAULT_POR_1_PRICE))
            )
            .andExpect(
                jsonPath("$.[*].por1Quantity").value(
                    hasItem(DEFAULT_POR_1_QUANTITY)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1ShipDate").value(
                    hasItem(DEFAULT_POR_1_SHIP_DATE.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].por1TotalFrgn").value(
                    hasItem(DEFAULT_POR_1_TOTAL_FRGN)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1TotalSumsy").value(
                    hasItem(DEFAULT_POR_1_TOTAL_SUMSY)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1TrgetEntry").value(
                    hasItem(DEFAULT_POR_1_TRGET_ENTRY)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1UMcode").value(
                    hasItem(DEFAULT_POR_1_U_MCODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1USo").value(hasItem(DEFAULT_POR_1_U_SO))
            )
            .andExpect(
                jsonPath("$.[*].por1UTenkythuat").value(
                    hasItem(DEFAULT_POR_1_U_TENKYTHUAT)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1UnitMsr").value(
                    hasItem(DEFAULT_POR_1_UNIT_MSR)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1UOMCode").value(
                    hasItem(DEFAULT_POR_1_UOM_CODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1VatGroup").value(
                    hasItem(DEFAULT_POR_1_VAT_GROUP)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1LineTotal").value(
                    hasItem(sameNumber(DEFAULT_POR_1_LINE_TOTAL))
                )
            )
            .andExpect(
                jsonPath("$.[*].por1VatPrcnt").value(
                    hasItem(sameNumber(DEFAULT_POR_1_VAT_PRCNT))
                )
            )
            .andExpect(
                jsonPath("$.[*].por1PriceAfVat").value(
                    hasItem(sameNumber(DEFAULT_POR_1_PRICE_AF_VAT))
                )
            )
            .andExpect(
                jsonPath("$.[*].por1WhsCode").value(
                    hasItem(DEFAULT_POR_1_WHS_CODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].prMapPo").value(hasItem(DEFAULT_PR_MAP_PO))
            );
    }

    @Test
    @Transactional
    void getSapPoInfo() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get the sapPoInfo
        restSapPoInfoMockMvc
            .perform(get(ENTITY_API_URL_ID, sapPoInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sapPoInfo.getId().intValue()))
            .andExpect(jsonPath("$.oporBranch").value(DEFAULT_OPOR_BRANCH))
            .andExpect(jsonPath("$.oporCanceled").value(DEFAULT_OPOR_CANCELED))
            .andExpect(jsonPath("$.oporCardCode").value(DEFAULT_OPOR_CARD_CODE))
            .andExpect(jsonPath("$.oporCardName").value(DEFAULT_OPOR_CARD_NAME))
            .andExpect(jsonPath("$.oporComments").value(DEFAULT_OPOR_COMMENTS))
            .andExpect(
                jsonPath("$.oporCreateDate").value(
                    DEFAULT_OPOR_CREATE_DATE.toString()
                )
            )
            .andExpect(
                jsonPath("$.oporDepartment").value(DEFAULT_OPOR_DEPARTMENT)
            )
            .andExpect(
                jsonPath("$.oporDocDate").value(
                    DEFAULT_OPOR_DOC_DATE.toString()
                )
            )
            .andExpect(
                jsonPath("$.oporDocDueDate").value(
                    DEFAULT_OPOR_DOC_DUE_DATE.toString()
                )
            )
            .andExpect(jsonPath("$.oporDocEntry").value(DEFAULT_OPOR_DOC_ENTRY))
            .andExpect(jsonPath("$.oporDocNum").value(DEFAULT_OPOR_DOC_NUM))
            .andExpect(
                jsonPath("$.oporDocStatus").value(DEFAULT_OPOR_DOC_STATUS)
            )
            .andExpect(
                jsonPath("$.oporInvntSttus").value(DEFAULT_OPOR_INVNT_STTUS)
            )
            .andExpect(jsonPath("$.oporJrnlMemo").value(DEFAULT_OPOR_JRNL_MEMO))
            .andExpect(jsonPath("$.oporUCoAdd").value(DEFAULT_OPOR_U_CO_ADD))
            .andExpect(
                jsonPath("$.oporUCodeInv").value(DEFAULT_OPOR_U_CODE_INV)
            )
            .andExpect(
                jsonPath("$.oporUCodeSerial").value(DEFAULT_OPOR_U_CODE_SERIAL)
            )
            .andExpect(
                jsonPath("$.oporUContractDate").value(
                    DEFAULT_OPOR_U_CONTRACT_DATE
                )
            )
            .andExpect(
                jsonPath("$.oporUDeclarePd").value(DEFAULT_OPOR_U_DECLARE_PD)
            )
            .andExpect(jsonPath("$.oporUDocNum").value(DEFAULT_OPOR_U_DOC_NUM))
            .andExpect(
                jsonPath("$.oporUInvCode").value(DEFAULT_OPOR_U_INV_CODE)
            )
            .andExpect(
                jsonPath("$.oporUInvCode2").value(DEFAULT_OPOR_U_INV_CODE_2)
            )
            .andExpect(
                jsonPath("$.oporUInvSerial").value(DEFAULT_OPOR_U_INV_SERIAL)
            )
            .andExpect(
                jsonPath("$.oporUPurNVGiao").value(DEFAULT_OPOR_U_PUR_NV_GIAO)
            )
            .andExpect(
                jsonPath("$.oporUpdateDate").value(
                    DEFAULT_OPOR_UPDATE_DATE.toString()
                )
            )
            .andExpect(jsonPath("$.oporUserSign").value(DEFAULT_OPOR_USER_SIGN))
            .andExpect(
                jsonPath("$.oporTaxDate").value(
                    DEFAULT_OPOR_TAX_DATE.toString()
                )
            )
            .andExpect(
                jsonPath("$.oporCntctCode").value(DEFAULT_OPOR_CNTCT_CODE)
            )
            .andExpect(
                jsonPath("$.oporNumAtCard").value(DEFAULT_OPOR_NUM_AT_CARD)
            )
            .andExpect(jsonPath("$.oporSlpCode").value(DEFAULT_OPOR_SLP_CODE))
            .andExpect(
                jsonPath("$.oporOwnerCode").value(DEFAULT_OPOR_OWNER_CODE)
            )
            .andExpect(
                jsonPath("$.oporVatSum").value(sameNumber(DEFAULT_OPOR_VAT_SUM))
            )
            .andExpect(
                jsonPath("$.oporDocTotal").value(
                    sameNumber(DEFAULT_OPOR_DOC_TOTAL)
                )
            )
            .andExpect(
                jsonPath("$.oporVatSumSy").value(
                    sameNumber(DEFAULT_OPOR_VAT_SUM_SY)
                )
            )
            .andExpect(jsonPath("$.oporUHt").value(DEFAULT_OPOR_U_HT))
            .andExpect(jsonPath("$.oporUPayment").value(DEFAULT_OPOR_U_PAYMENT))
            .andExpect(
                jsonPath("$.por1BaseDocNum").value(DEFAULT_POR_1_BASE_DOC_NUM)
            )
            .andExpect(
                jsonPath("$.por1BaseEntry").value(DEFAULT_POR_1_BASE_ENTRY)
            )
            .andExpect(
                jsonPath("$.por1BaseLine").value(DEFAULT_POR_1_BASE_LINE)
            )
            .andExpect(jsonPath("$.por1BaseRef").value(DEFAULT_POR_1_BASE_REF))
            .andExpect(jsonPath("$.por1Currency").value(DEFAULT_POR_1_CURRENCY))
            .andExpect(
                jsonPath("$.por1DiscPrcnt").value(DEFAULT_POR_1_DISC_PRCNT)
            )
            .andExpect(jsonPath("$.poDocEntry").value(DEFAULT_PO_DOC_ENTRY))
            .andExpect(
                jsonPath("$.por1Dscription").value(DEFAULT_POR_1_DSCRIPTION)
            )
            .andExpect(
                jsonPath("$.por1ItemCode").value(DEFAULT_POR_1_ITEM_CODE)
            )
            .andExpect(jsonPath("$.por1LineNum").value(DEFAULT_POR_1_LINE_NUM))
            .andExpect(
                jsonPath("$.por1LineStatus").value(DEFAULT_POR_1_LINE_STATUS)
            )
            .andExpect(
                jsonPath("$.por1LineVendor").value(DEFAULT_POR_1_LINE_VENDOR)
            )
            .andExpect(
                jsonPath("$.por1OpenSumSys").value(DEFAULT_POR_1_OPEN_SUM_SYS)
            )
            .andExpect(jsonPath("$.por1Price").value(DEFAULT_POR_1_PRICE))
            .andExpect(jsonPath("$.por1Quantity").value(DEFAULT_POR_1_QUANTITY))
            .andExpect(
                jsonPath("$.por1ShipDate").value(
                    DEFAULT_POR_1_SHIP_DATE.toString()
                )
            )
            .andExpect(
                jsonPath("$.por1TotalFrgn").value(DEFAULT_POR_1_TOTAL_FRGN)
            )
            .andExpect(
                jsonPath("$.por1TotalSumsy").value(DEFAULT_POR_1_TOTAL_SUMSY)
            )
            .andExpect(
                jsonPath("$.por1TrgetEntry").value(DEFAULT_POR_1_TRGET_ENTRY)
            )
            .andExpect(jsonPath("$.por1UMcode").value(DEFAULT_POR_1_U_MCODE))
            .andExpect(jsonPath("$.por1USo").value(DEFAULT_POR_1_U_SO))
            .andExpect(
                jsonPath("$.por1UTenkythuat").value(DEFAULT_POR_1_U_TENKYTHUAT)
            )
            .andExpect(jsonPath("$.por1UnitMsr").value(DEFAULT_POR_1_UNIT_MSR))
            .andExpect(jsonPath("$.por1UOMCode").value(DEFAULT_POR_1_UOM_CODE))
            .andExpect(
                jsonPath("$.por1VatGroup").value(DEFAULT_POR_1_VAT_GROUP)
            )
            .andExpect(
                jsonPath("$.por1LineTotal").value(
                    sameNumber(DEFAULT_POR_1_LINE_TOTAL)
                )
            )
            .andExpect(
                jsonPath("$.por1VatPrcnt").value(
                    sameNumber(DEFAULT_POR_1_VAT_PRCNT)
                )
            )
            .andExpect(
                jsonPath("$.por1PriceAfVat").value(
                    sameNumber(DEFAULT_POR_1_PRICE_AF_VAT)
                )
            )
            .andExpect(jsonPath("$.por1WhsCode").value(DEFAULT_POR_1_WHS_CODE))
            .andExpect(jsonPath("$.prMapPo").value(DEFAULT_PR_MAP_PO));
    }

    @Test
    @Transactional
    void getSapPoInfosByIdFiltering() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        Long id = sapPoInfo.getId();

        defaultSapPoInfoFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSapPoInfoFiltering(
            "id.greaterThanOrEqual=" + id,
            "id.greaterThan=" + id
        );

        defaultSapPoInfoFiltering(
            "id.lessThanOrEqual=" + id,
            "id.lessThan=" + id
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporBranchIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporBranch equals to
        defaultSapPoInfoFiltering(
            "oporBranch.equals=" + DEFAULT_OPOR_BRANCH,
            "oporBranch.equals=" + UPDATED_OPOR_BRANCH
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporBranchIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporBranch in
        defaultSapPoInfoFiltering(
            "oporBranch.in=" + DEFAULT_OPOR_BRANCH + "," + UPDATED_OPOR_BRANCH,
            "oporBranch.in=" + UPDATED_OPOR_BRANCH
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporBranchIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporBranch is not null
        defaultSapPoInfoFiltering(
            "oporBranch.specified=true",
            "oporBranch.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporBranchContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporBranch contains
        defaultSapPoInfoFiltering(
            "oporBranch.contains=" + DEFAULT_OPOR_BRANCH,
            "oporBranch.contains=" + UPDATED_OPOR_BRANCH
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporBranchNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporBranch does not contain
        defaultSapPoInfoFiltering(
            "oporBranch.doesNotContain=" + UPDATED_OPOR_BRANCH,
            "oporBranch.doesNotContain=" + DEFAULT_OPOR_BRANCH
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCanceledIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCanceled equals to
        defaultSapPoInfoFiltering(
            "oporCanceled.equals=" + DEFAULT_OPOR_CANCELED,
            "oporCanceled.equals=" + UPDATED_OPOR_CANCELED
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCanceledIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCanceled in
        defaultSapPoInfoFiltering(
            "oporCanceled.in=" +
            DEFAULT_OPOR_CANCELED +
            "," +
            UPDATED_OPOR_CANCELED,
            "oporCanceled.in=" + UPDATED_OPOR_CANCELED
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCanceledIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCanceled is not null
        defaultSapPoInfoFiltering(
            "oporCanceled.specified=true",
            "oporCanceled.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCanceledContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCanceled contains
        defaultSapPoInfoFiltering(
            "oporCanceled.contains=" + DEFAULT_OPOR_CANCELED,
            "oporCanceled.contains=" + UPDATED_OPOR_CANCELED
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCanceledNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCanceled does not contain
        defaultSapPoInfoFiltering(
            "oporCanceled.doesNotContain=" + UPDATED_OPOR_CANCELED,
            "oporCanceled.doesNotContain=" + DEFAULT_OPOR_CANCELED
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCardCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCardCode equals to
        defaultSapPoInfoFiltering(
            "oporCardCode.equals=" + DEFAULT_OPOR_CARD_CODE,
            "oporCardCode.equals=" + UPDATED_OPOR_CARD_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCardCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCardCode in
        defaultSapPoInfoFiltering(
            "oporCardCode.in=" +
            DEFAULT_OPOR_CARD_CODE +
            "," +
            UPDATED_OPOR_CARD_CODE,
            "oporCardCode.in=" + UPDATED_OPOR_CARD_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCardCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCardCode is not null
        defaultSapPoInfoFiltering(
            "oporCardCode.specified=true",
            "oporCardCode.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCardCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCardCode contains
        defaultSapPoInfoFiltering(
            "oporCardCode.contains=" + DEFAULT_OPOR_CARD_CODE,
            "oporCardCode.contains=" + UPDATED_OPOR_CARD_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCardCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCardCode does not contain
        defaultSapPoInfoFiltering(
            "oporCardCode.doesNotContain=" + UPDATED_OPOR_CARD_CODE,
            "oporCardCode.doesNotContain=" + DEFAULT_OPOR_CARD_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCardNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCardName equals to
        defaultSapPoInfoFiltering(
            "oporCardName.equals=" + DEFAULT_OPOR_CARD_NAME,
            "oporCardName.equals=" + UPDATED_OPOR_CARD_NAME
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCardNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCardName in
        defaultSapPoInfoFiltering(
            "oporCardName.in=" +
            DEFAULT_OPOR_CARD_NAME +
            "," +
            UPDATED_OPOR_CARD_NAME,
            "oporCardName.in=" + UPDATED_OPOR_CARD_NAME
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCardNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCardName is not null
        defaultSapPoInfoFiltering(
            "oporCardName.specified=true",
            "oporCardName.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCardNameContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCardName contains
        defaultSapPoInfoFiltering(
            "oporCardName.contains=" + DEFAULT_OPOR_CARD_NAME,
            "oporCardName.contains=" + UPDATED_OPOR_CARD_NAME
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCardNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCardName does not contain
        defaultSapPoInfoFiltering(
            "oporCardName.doesNotContain=" + UPDATED_OPOR_CARD_NAME,
            "oporCardName.doesNotContain=" + DEFAULT_OPOR_CARD_NAME
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCommentsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporComments equals to
        defaultSapPoInfoFiltering(
            "oporComments.equals=" + DEFAULT_OPOR_COMMENTS,
            "oporComments.equals=" + UPDATED_OPOR_COMMENTS
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCommentsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporComments in
        defaultSapPoInfoFiltering(
            "oporComments.in=" +
            DEFAULT_OPOR_COMMENTS +
            "," +
            UPDATED_OPOR_COMMENTS,
            "oporComments.in=" + UPDATED_OPOR_COMMENTS
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCommentsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporComments is not null
        defaultSapPoInfoFiltering(
            "oporComments.specified=true",
            "oporComments.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCommentsContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporComments contains
        defaultSapPoInfoFiltering(
            "oporComments.contains=" + DEFAULT_OPOR_COMMENTS,
            "oporComments.contains=" + UPDATED_OPOR_COMMENTS
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCommentsNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporComments does not contain
        defaultSapPoInfoFiltering(
            "oporComments.doesNotContain=" + UPDATED_OPOR_COMMENTS,
            "oporComments.doesNotContain=" + DEFAULT_OPOR_COMMENTS
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCreateDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCreateDate equals to
        defaultSapPoInfoFiltering(
            "oporCreateDate.equals=" + DEFAULT_OPOR_CREATE_DATE,
            "oporCreateDate.equals=" + UPDATED_OPOR_CREATE_DATE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCreateDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCreateDate in
        defaultSapPoInfoFiltering(
            "oporCreateDate.in=" +
            DEFAULT_OPOR_CREATE_DATE +
            "," +
            UPDATED_OPOR_CREATE_DATE,
            "oporCreateDate.in=" + UPDATED_OPOR_CREATE_DATE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCreateDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCreateDate is not null
        defaultSapPoInfoFiltering(
            "oporCreateDate.specified=true",
            "oporCreateDate.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDepartmentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDepartment equals to
        defaultSapPoInfoFiltering(
            "oporDepartment.equals=" + DEFAULT_OPOR_DEPARTMENT,
            "oporDepartment.equals=" + UPDATED_OPOR_DEPARTMENT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDepartmentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDepartment in
        defaultSapPoInfoFiltering(
            "oporDepartment.in=" +
            DEFAULT_OPOR_DEPARTMENT +
            "," +
            UPDATED_OPOR_DEPARTMENT,
            "oporDepartment.in=" + UPDATED_OPOR_DEPARTMENT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDepartmentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDepartment is not null
        defaultSapPoInfoFiltering(
            "oporDepartment.specified=true",
            "oporDepartment.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDepartmentContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDepartment contains
        defaultSapPoInfoFiltering(
            "oporDepartment.contains=" + DEFAULT_OPOR_DEPARTMENT,
            "oporDepartment.contains=" + UPDATED_OPOR_DEPARTMENT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDepartmentNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDepartment does not contain
        defaultSapPoInfoFiltering(
            "oporDepartment.doesNotContain=" + UPDATED_OPOR_DEPARTMENT,
            "oporDepartment.doesNotContain=" + DEFAULT_OPOR_DEPARTMENT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocDate equals to
        defaultSapPoInfoFiltering(
            "oporDocDate.equals=" + DEFAULT_OPOR_DOC_DATE,
            "oporDocDate.equals=" + UPDATED_OPOR_DOC_DATE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocDate in
        defaultSapPoInfoFiltering(
            "oporDocDate.in=" +
            DEFAULT_OPOR_DOC_DATE +
            "," +
            UPDATED_OPOR_DOC_DATE,
            "oporDocDate.in=" + UPDATED_OPOR_DOC_DATE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocDate is not null
        defaultSapPoInfoFiltering(
            "oporDocDate.specified=true",
            "oporDocDate.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocDueDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocDueDate equals to
        defaultSapPoInfoFiltering(
            "oporDocDueDate.equals=" + DEFAULT_OPOR_DOC_DUE_DATE,
            "oporDocDueDate.equals=" + UPDATED_OPOR_DOC_DUE_DATE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocDueDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocDueDate in
        defaultSapPoInfoFiltering(
            "oporDocDueDate.in=" +
            DEFAULT_OPOR_DOC_DUE_DATE +
            "," +
            UPDATED_OPOR_DOC_DUE_DATE,
            "oporDocDueDate.in=" + UPDATED_OPOR_DOC_DUE_DATE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocDueDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocDueDate is not null
        defaultSapPoInfoFiltering(
            "oporDocDueDate.specified=true",
            "oporDocDueDate.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocEntryIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocEntry equals to
        defaultSapPoInfoFiltering(
            "oporDocEntry.equals=" + DEFAULT_OPOR_DOC_ENTRY,
            "oporDocEntry.equals=" + UPDATED_OPOR_DOC_ENTRY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocEntryIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocEntry in
        defaultSapPoInfoFiltering(
            "oporDocEntry.in=" +
            DEFAULT_OPOR_DOC_ENTRY +
            "," +
            UPDATED_OPOR_DOC_ENTRY,
            "oporDocEntry.in=" + UPDATED_OPOR_DOC_ENTRY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocEntryIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocEntry is not null
        defaultSapPoInfoFiltering(
            "oporDocEntry.specified=true",
            "oporDocEntry.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocEntryContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocEntry contains
        defaultSapPoInfoFiltering(
            "oporDocEntry.contains=" + DEFAULT_OPOR_DOC_ENTRY,
            "oporDocEntry.contains=" + UPDATED_OPOR_DOC_ENTRY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocEntryNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocEntry does not contain
        defaultSapPoInfoFiltering(
            "oporDocEntry.doesNotContain=" + UPDATED_OPOR_DOC_ENTRY,
            "oporDocEntry.doesNotContain=" + DEFAULT_OPOR_DOC_ENTRY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocNumIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocNum equals to
        defaultSapPoInfoFiltering(
            "oporDocNum.equals=" + DEFAULT_OPOR_DOC_NUM,
            "oporDocNum.equals=" + UPDATED_OPOR_DOC_NUM
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocNumIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocNum in
        defaultSapPoInfoFiltering(
            "oporDocNum.in=" +
            DEFAULT_OPOR_DOC_NUM +
            "," +
            UPDATED_OPOR_DOC_NUM,
            "oporDocNum.in=" + UPDATED_OPOR_DOC_NUM
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocNumIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocNum is not null
        defaultSapPoInfoFiltering(
            "oporDocNum.specified=true",
            "oporDocNum.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocNumContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocNum contains
        defaultSapPoInfoFiltering(
            "oporDocNum.contains=" + DEFAULT_OPOR_DOC_NUM,
            "oporDocNum.contains=" + UPDATED_OPOR_DOC_NUM
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocNumNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocNum does not contain
        defaultSapPoInfoFiltering(
            "oporDocNum.doesNotContain=" + UPDATED_OPOR_DOC_NUM,
            "oporDocNum.doesNotContain=" + DEFAULT_OPOR_DOC_NUM
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocStatus equals to
        defaultSapPoInfoFiltering(
            "oporDocStatus.equals=" + DEFAULT_OPOR_DOC_STATUS,
            "oporDocStatus.equals=" + UPDATED_OPOR_DOC_STATUS
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocStatus in
        defaultSapPoInfoFiltering(
            "oporDocStatus.in=" +
            DEFAULT_OPOR_DOC_STATUS +
            "," +
            UPDATED_OPOR_DOC_STATUS,
            "oporDocStatus.in=" + UPDATED_OPOR_DOC_STATUS
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocStatus is not null
        defaultSapPoInfoFiltering(
            "oporDocStatus.specified=true",
            "oporDocStatus.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocStatus contains
        defaultSapPoInfoFiltering(
            "oporDocStatus.contains=" + DEFAULT_OPOR_DOC_STATUS,
            "oporDocStatus.contains=" + UPDATED_OPOR_DOC_STATUS
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocStatusNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocStatus does not contain
        defaultSapPoInfoFiltering(
            "oporDocStatus.doesNotContain=" + UPDATED_OPOR_DOC_STATUS,
            "oporDocStatus.doesNotContain=" + DEFAULT_OPOR_DOC_STATUS
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporInvntSttusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporInvntSttus equals to
        defaultSapPoInfoFiltering(
            "oporInvntSttus.equals=" + DEFAULT_OPOR_INVNT_STTUS,
            "oporInvntSttus.equals=" + UPDATED_OPOR_INVNT_STTUS
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporInvntSttusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporInvntSttus in
        defaultSapPoInfoFiltering(
            "oporInvntSttus.in=" +
            DEFAULT_OPOR_INVNT_STTUS +
            "," +
            UPDATED_OPOR_INVNT_STTUS,
            "oporInvntSttus.in=" + UPDATED_OPOR_INVNT_STTUS
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporInvntSttusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporInvntSttus is not null
        defaultSapPoInfoFiltering(
            "oporInvntSttus.specified=true",
            "oporInvntSttus.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporInvntSttusContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporInvntSttus contains
        defaultSapPoInfoFiltering(
            "oporInvntSttus.contains=" + DEFAULT_OPOR_INVNT_STTUS,
            "oporInvntSttus.contains=" + UPDATED_OPOR_INVNT_STTUS
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporInvntSttusNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporInvntSttus does not contain
        defaultSapPoInfoFiltering(
            "oporInvntSttus.doesNotContain=" + UPDATED_OPOR_INVNT_STTUS,
            "oporInvntSttus.doesNotContain=" + DEFAULT_OPOR_INVNT_STTUS
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporJrnlMemoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporJrnlMemo equals to
        defaultSapPoInfoFiltering(
            "oporJrnlMemo.equals=" + DEFAULT_OPOR_JRNL_MEMO,
            "oporJrnlMemo.equals=" + UPDATED_OPOR_JRNL_MEMO
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporJrnlMemoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporJrnlMemo in
        defaultSapPoInfoFiltering(
            "oporJrnlMemo.in=" +
            DEFAULT_OPOR_JRNL_MEMO +
            "," +
            UPDATED_OPOR_JRNL_MEMO,
            "oporJrnlMemo.in=" + UPDATED_OPOR_JRNL_MEMO
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporJrnlMemoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporJrnlMemo is not null
        defaultSapPoInfoFiltering(
            "oporJrnlMemo.specified=true",
            "oporJrnlMemo.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporJrnlMemoContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporJrnlMemo contains
        defaultSapPoInfoFiltering(
            "oporJrnlMemo.contains=" + DEFAULT_OPOR_JRNL_MEMO,
            "oporJrnlMemo.contains=" + UPDATED_OPOR_JRNL_MEMO
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporJrnlMemoNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporJrnlMemo does not contain
        defaultSapPoInfoFiltering(
            "oporJrnlMemo.doesNotContain=" + UPDATED_OPOR_JRNL_MEMO,
            "oporJrnlMemo.doesNotContain=" + DEFAULT_OPOR_JRNL_MEMO
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUCoAddIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUCoAdd equals to
        defaultSapPoInfoFiltering(
            "oporUCoAdd.equals=" + DEFAULT_OPOR_U_CO_ADD,
            "oporUCoAdd.equals=" + UPDATED_OPOR_U_CO_ADD
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUCoAddIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUCoAdd in
        defaultSapPoInfoFiltering(
            "oporUCoAdd.in=" +
            DEFAULT_OPOR_U_CO_ADD +
            "," +
            UPDATED_OPOR_U_CO_ADD,
            "oporUCoAdd.in=" + UPDATED_OPOR_U_CO_ADD
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUCoAddIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUCoAdd is not null
        defaultSapPoInfoFiltering(
            "oporUCoAdd.specified=true",
            "oporUCoAdd.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUCoAddContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUCoAdd contains
        defaultSapPoInfoFiltering(
            "oporUCoAdd.contains=" + DEFAULT_OPOR_U_CO_ADD,
            "oporUCoAdd.contains=" + UPDATED_OPOR_U_CO_ADD
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUCoAddNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUCoAdd does not contain
        defaultSapPoInfoFiltering(
            "oporUCoAdd.doesNotContain=" + UPDATED_OPOR_U_CO_ADD,
            "oporUCoAdd.doesNotContain=" + DEFAULT_OPOR_U_CO_ADD
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUCodeInvIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUCodeInv equals to
        defaultSapPoInfoFiltering(
            "oporUCodeInv.equals=" + DEFAULT_OPOR_U_CODE_INV,
            "oporUCodeInv.equals=" + UPDATED_OPOR_U_CODE_INV
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUCodeInvIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUCodeInv in
        defaultSapPoInfoFiltering(
            "oporUCodeInv.in=" +
            DEFAULT_OPOR_U_CODE_INV +
            "," +
            UPDATED_OPOR_U_CODE_INV,
            "oporUCodeInv.in=" + UPDATED_OPOR_U_CODE_INV
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUCodeInvIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUCodeInv is not null
        defaultSapPoInfoFiltering(
            "oporUCodeInv.specified=true",
            "oporUCodeInv.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUCodeInvContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUCodeInv contains
        defaultSapPoInfoFiltering(
            "oporUCodeInv.contains=" + DEFAULT_OPOR_U_CODE_INV,
            "oporUCodeInv.contains=" + UPDATED_OPOR_U_CODE_INV
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUCodeInvNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUCodeInv does not contain
        defaultSapPoInfoFiltering(
            "oporUCodeInv.doesNotContain=" + UPDATED_OPOR_U_CODE_INV,
            "oporUCodeInv.doesNotContain=" + DEFAULT_OPOR_U_CODE_INV
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUCodeSerialIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUCodeSerial equals to
        defaultSapPoInfoFiltering(
            "oporUCodeSerial.equals=" + DEFAULT_OPOR_U_CODE_SERIAL,
            "oporUCodeSerial.equals=" + UPDATED_OPOR_U_CODE_SERIAL
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUCodeSerialIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUCodeSerial in
        defaultSapPoInfoFiltering(
            "oporUCodeSerial.in=" +
            DEFAULT_OPOR_U_CODE_SERIAL +
            "," +
            UPDATED_OPOR_U_CODE_SERIAL,
            "oporUCodeSerial.in=" + UPDATED_OPOR_U_CODE_SERIAL
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUCodeSerialIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUCodeSerial is not null
        defaultSapPoInfoFiltering(
            "oporUCodeSerial.specified=true",
            "oporUCodeSerial.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUCodeSerialContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUCodeSerial contains
        defaultSapPoInfoFiltering(
            "oporUCodeSerial.contains=" + DEFAULT_OPOR_U_CODE_SERIAL,
            "oporUCodeSerial.contains=" + UPDATED_OPOR_U_CODE_SERIAL
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUCodeSerialNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUCodeSerial does not contain
        defaultSapPoInfoFiltering(
            "oporUCodeSerial.doesNotContain=" + UPDATED_OPOR_U_CODE_SERIAL,
            "oporUCodeSerial.doesNotContain=" + DEFAULT_OPOR_U_CODE_SERIAL
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUContractDateIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUContractDate equals to
        defaultSapPoInfoFiltering(
            "oporUContractDate.equals=" + DEFAULT_OPOR_U_CONTRACT_DATE,
            "oporUContractDate.equals=" + UPDATED_OPOR_U_CONTRACT_DATE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUContractDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUContractDate in
        defaultSapPoInfoFiltering(
            "oporUContractDate.in=" +
            DEFAULT_OPOR_U_CONTRACT_DATE +
            "," +
            UPDATED_OPOR_U_CONTRACT_DATE,
            "oporUContractDate.in=" + UPDATED_OPOR_U_CONTRACT_DATE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUContractDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUContractDate is not null
        defaultSapPoInfoFiltering(
            "oporUContractDate.specified=true",
            "oporUContractDate.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUContractDateContainsSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUContractDate contains
        defaultSapPoInfoFiltering(
            "oporUContractDate.contains=" + DEFAULT_OPOR_U_CONTRACT_DATE,
            "oporUContractDate.contains=" + UPDATED_OPOR_U_CONTRACT_DATE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUContractDateNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUContractDate does not contain
        defaultSapPoInfoFiltering(
            "oporUContractDate.doesNotContain=" + UPDATED_OPOR_U_CONTRACT_DATE,
            "oporUContractDate.doesNotContain=" + DEFAULT_OPOR_U_CONTRACT_DATE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUDeclarePdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUDeclarePd equals to
        defaultSapPoInfoFiltering(
            "oporUDeclarePd.equals=" + DEFAULT_OPOR_U_DECLARE_PD,
            "oporUDeclarePd.equals=" + UPDATED_OPOR_U_DECLARE_PD
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUDeclarePdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUDeclarePd in
        defaultSapPoInfoFiltering(
            "oporUDeclarePd.in=" +
            DEFAULT_OPOR_U_DECLARE_PD +
            "," +
            UPDATED_OPOR_U_DECLARE_PD,
            "oporUDeclarePd.in=" + UPDATED_OPOR_U_DECLARE_PD
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUDeclarePdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUDeclarePd is not null
        defaultSapPoInfoFiltering(
            "oporUDeclarePd.specified=true",
            "oporUDeclarePd.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUDeclarePdContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUDeclarePd contains
        defaultSapPoInfoFiltering(
            "oporUDeclarePd.contains=" + DEFAULT_OPOR_U_DECLARE_PD,
            "oporUDeclarePd.contains=" + UPDATED_OPOR_U_DECLARE_PD
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUDeclarePdNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUDeclarePd does not contain
        defaultSapPoInfoFiltering(
            "oporUDeclarePd.doesNotContain=" + UPDATED_OPOR_U_DECLARE_PD,
            "oporUDeclarePd.doesNotContain=" + DEFAULT_OPOR_U_DECLARE_PD
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUDocNumIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUDocNum equals to
        defaultSapPoInfoFiltering(
            "oporUDocNum.equals=" + DEFAULT_OPOR_U_DOC_NUM,
            "oporUDocNum.equals=" + UPDATED_OPOR_U_DOC_NUM
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUDocNumIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUDocNum in
        defaultSapPoInfoFiltering(
            "oporUDocNum.in=" +
            DEFAULT_OPOR_U_DOC_NUM +
            "," +
            UPDATED_OPOR_U_DOC_NUM,
            "oporUDocNum.in=" + UPDATED_OPOR_U_DOC_NUM
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUDocNumIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUDocNum is not null
        defaultSapPoInfoFiltering(
            "oporUDocNum.specified=true",
            "oporUDocNum.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUDocNumContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUDocNum contains
        defaultSapPoInfoFiltering(
            "oporUDocNum.contains=" + DEFAULT_OPOR_U_DOC_NUM,
            "oporUDocNum.contains=" + UPDATED_OPOR_U_DOC_NUM
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUDocNumNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUDocNum does not contain
        defaultSapPoInfoFiltering(
            "oporUDocNum.doesNotContain=" + UPDATED_OPOR_U_DOC_NUM,
            "oporUDocNum.doesNotContain=" + DEFAULT_OPOR_U_DOC_NUM
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUInvCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUInvCode equals to
        defaultSapPoInfoFiltering(
            "oporUInvCode.equals=" + DEFAULT_OPOR_U_INV_CODE,
            "oporUInvCode.equals=" + UPDATED_OPOR_U_INV_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUInvCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUInvCode in
        defaultSapPoInfoFiltering(
            "oporUInvCode.in=" +
            DEFAULT_OPOR_U_INV_CODE +
            "," +
            UPDATED_OPOR_U_INV_CODE,
            "oporUInvCode.in=" + UPDATED_OPOR_U_INV_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUInvCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUInvCode is not null
        defaultSapPoInfoFiltering(
            "oporUInvCode.specified=true",
            "oporUInvCode.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUInvCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUInvCode contains
        defaultSapPoInfoFiltering(
            "oporUInvCode.contains=" + DEFAULT_OPOR_U_INV_CODE,
            "oporUInvCode.contains=" + UPDATED_OPOR_U_INV_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUInvCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUInvCode does not contain
        defaultSapPoInfoFiltering(
            "oporUInvCode.doesNotContain=" + UPDATED_OPOR_U_INV_CODE,
            "oporUInvCode.doesNotContain=" + DEFAULT_OPOR_U_INV_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUInvCode2IsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUInvCode2 equals to
        defaultSapPoInfoFiltering(
            "oporUInvCode2.equals=" + DEFAULT_OPOR_U_INV_CODE_2,
            "oporUInvCode2.equals=" + UPDATED_OPOR_U_INV_CODE_2
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUInvCode2IsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUInvCode2 in
        defaultSapPoInfoFiltering(
            "oporUInvCode2.in=" +
            DEFAULT_OPOR_U_INV_CODE_2 +
            "," +
            UPDATED_OPOR_U_INV_CODE_2,
            "oporUInvCode2.in=" + UPDATED_OPOR_U_INV_CODE_2
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUInvCode2IsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUInvCode2 is not null
        defaultSapPoInfoFiltering(
            "oporUInvCode2.specified=true",
            "oporUInvCode2.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUInvCode2ContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUInvCode2 contains
        defaultSapPoInfoFiltering(
            "oporUInvCode2.contains=" + DEFAULT_OPOR_U_INV_CODE_2,
            "oporUInvCode2.contains=" + UPDATED_OPOR_U_INV_CODE_2
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUInvCode2NotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUInvCode2 does not contain
        defaultSapPoInfoFiltering(
            "oporUInvCode2.doesNotContain=" + UPDATED_OPOR_U_INV_CODE_2,
            "oporUInvCode2.doesNotContain=" + DEFAULT_OPOR_U_INV_CODE_2
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUInvSerialIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUInvSerial equals to
        defaultSapPoInfoFiltering(
            "oporUInvSerial.equals=" + DEFAULT_OPOR_U_INV_SERIAL,
            "oporUInvSerial.equals=" + UPDATED_OPOR_U_INV_SERIAL
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUInvSerialIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUInvSerial in
        defaultSapPoInfoFiltering(
            "oporUInvSerial.in=" +
            DEFAULT_OPOR_U_INV_SERIAL +
            "," +
            UPDATED_OPOR_U_INV_SERIAL,
            "oporUInvSerial.in=" + UPDATED_OPOR_U_INV_SERIAL
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUInvSerialIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUInvSerial is not null
        defaultSapPoInfoFiltering(
            "oporUInvSerial.specified=true",
            "oporUInvSerial.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUInvSerialContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUInvSerial contains
        defaultSapPoInfoFiltering(
            "oporUInvSerial.contains=" + DEFAULT_OPOR_U_INV_SERIAL,
            "oporUInvSerial.contains=" + UPDATED_OPOR_U_INV_SERIAL
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUInvSerialNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUInvSerial does not contain
        defaultSapPoInfoFiltering(
            "oporUInvSerial.doesNotContain=" + UPDATED_OPOR_U_INV_SERIAL,
            "oporUInvSerial.doesNotContain=" + DEFAULT_OPOR_U_INV_SERIAL
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUPurNVGiaoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUPurNVGiao equals to
        defaultSapPoInfoFiltering(
            "oporUPurNVGiao.equals=" + DEFAULT_OPOR_U_PUR_NV_GIAO,
            "oporUPurNVGiao.equals=" + UPDATED_OPOR_U_PUR_NV_GIAO
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUPurNVGiaoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUPurNVGiao in
        defaultSapPoInfoFiltering(
            "oporUPurNVGiao.in=" +
            DEFAULT_OPOR_U_PUR_NV_GIAO +
            "," +
            UPDATED_OPOR_U_PUR_NV_GIAO,
            "oporUPurNVGiao.in=" + UPDATED_OPOR_U_PUR_NV_GIAO
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUPurNVGiaoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUPurNVGiao is not null
        defaultSapPoInfoFiltering(
            "oporUPurNVGiao.specified=true",
            "oporUPurNVGiao.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUPurNVGiaoContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUPurNVGiao contains
        defaultSapPoInfoFiltering(
            "oporUPurNVGiao.contains=" + DEFAULT_OPOR_U_PUR_NV_GIAO,
            "oporUPurNVGiao.contains=" + UPDATED_OPOR_U_PUR_NV_GIAO
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUPurNVGiaoNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUPurNVGiao does not contain
        defaultSapPoInfoFiltering(
            "oporUPurNVGiao.doesNotContain=" + UPDATED_OPOR_U_PUR_NV_GIAO,
            "oporUPurNVGiao.doesNotContain=" + DEFAULT_OPOR_U_PUR_NV_GIAO
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUpdateDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUpdateDate equals to
        defaultSapPoInfoFiltering(
            "oporUpdateDate.equals=" + DEFAULT_OPOR_UPDATE_DATE,
            "oporUpdateDate.equals=" + UPDATED_OPOR_UPDATE_DATE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUpdateDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUpdateDate in
        defaultSapPoInfoFiltering(
            "oporUpdateDate.in=" +
            DEFAULT_OPOR_UPDATE_DATE +
            "," +
            UPDATED_OPOR_UPDATE_DATE,
            "oporUpdateDate.in=" + UPDATED_OPOR_UPDATE_DATE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUpdateDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUpdateDate is not null
        defaultSapPoInfoFiltering(
            "oporUpdateDate.specified=true",
            "oporUpdateDate.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUserSignIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUserSign equals to
        defaultSapPoInfoFiltering(
            "oporUserSign.equals=" + DEFAULT_OPOR_USER_SIGN,
            "oporUserSign.equals=" + UPDATED_OPOR_USER_SIGN
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUserSignIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUserSign in
        defaultSapPoInfoFiltering(
            "oporUserSign.in=" +
            DEFAULT_OPOR_USER_SIGN +
            "," +
            UPDATED_OPOR_USER_SIGN,
            "oporUserSign.in=" + UPDATED_OPOR_USER_SIGN
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUserSignIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUserSign is not null
        defaultSapPoInfoFiltering(
            "oporUserSign.specified=true",
            "oporUserSign.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUserSignContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUserSign contains
        defaultSapPoInfoFiltering(
            "oporUserSign.contains=" + DEFAULT_OPOR_USER_SIGN,
            "oporUserSign.contains=" + UPDATED_OPOR_USER_SIGN
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUserSignNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUserSign does not contain
        defaultSapPoInfoFiltering(
            "oporUserSign.doesNotContain=" + UPDATED_OPOR_USER_SIGN,
            "oporUserSign.doesNotContain=" + DEFAULT_OPOR_USER_SIGN
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporTaxDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporTaxDate equals to
        defaultSapPoInfoFiltering(
            "oporTaxDate.equals=" + DEFAULT_OPOR_TAX_DATE,
            "oporTaxDate.equals=" + UPDATED_OPOR_TAX_DATE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporTaxDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporTaxDate in
        defaultSapPoInfoFiltering(
            "oporTaxDate.in=" +
            DEFAULT_OPOR_TAX_DATE +
            "," +
            UPDATED_OPOR_TAX_DATE,
            "oporTaxDate.in=" + UPDATED_OPOR_TAX_DATE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporTaxDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporTaxDate is not null
        defaultSapPoInfoFiltering(
            "oporTaxDate.specified=true",
            "oporTaxDate.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCntctCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCntctCode equals to
        defaultSapPoInfoFiltering(
            "oporCntctCode.equals=" + DEFAULT_OPOR_CNTCT_CODE,
            "oporCntctCode.equals=" + UPDATED_OPOR_CNTCT_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCntctCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCntctCode in
        defaultSapPoInfoFiltering(
            "oporCntctCode.in=" +
            DEFAULT_OPOR_CNTCT_CODE +
            "," +
            UPDATED_OPOR_CNTCT_CODE,
            "oporCntctCode.in=" + UPDATED_OPOR_CNTCT_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCntctCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCntctCode is not null
        defaultSapPoInfoFiltering(
            "oporCntctCode.specified=true",
            "oporCntctCode.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCntctCodeIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCntctCode is greater than or equal to
        defaultSapPoInfoFiltering(
            "oporCntctCode.greaterThanOrEqual=" + DEFAULT_OPOR_CNTCT_CODE,
            "oporCntctCode.greaterThanOrEqual=" + UPDATED_OPOR_CNTCT_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCntctCodeIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCntctCode is less than or equal to
        defaultSapPoInfoFiltering(
            "oporCntctCode.lessThanOrEqual=" + DEFAULT_OPOR_CNTCT_CODE,
            "oporCntctCode.lessThanOrEqual=" + SMALLER_OPOR_CNTCT_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCntctCodeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCntctCode is less than
        defaultSapPoInfoFiltering(
            "oporCntctCode.lessThan=" + UPDATED_OPOR_CNTCT_CODE,
            "oporCntctCode.lessThan=" + DEFAULT_OPOR_CNTCT_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporCntctCodeIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporCntctCode is greater than
        defaultSapPoInfoFiltering(
            "oporCntctCode.greaterThan=" + SMALLER_OPOR_CNTCT_CODE,
            "oporCntctCode.greaterThan=" + DEFAULT_OPOR_CNTCT_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporNumAtCardIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporNumAtCard equals to
        defaultSapPoInfoFiltering(
            "oporNumAtCard.equals=" + DEFAULT_OPOR_NUM_AT_CARD,
            "oporNumAtCard.equals=" + UPDATED_OPOR_NUM_AT_CARD
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporNumAtCardIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporNumAtCard in
        defaultSapPoInfoFiltering(
            "oporNumAtCard.in=" +
            DEFAULT_OPOR_NUM_AT_CARD +
            "," +
            UPDATED_OPOR_NUM_AT_CARD,
            "oporNumAtCard.in=" + UPDATED_OPOR_NUM_AT_CARD
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporNumAtCardIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporNumAtCard is not null
        defaultSapPoInfoFiltering(
            "oporNumAtCard.specified=true",
            "oporNumAtCard.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporNumAtCardContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporNumAtCard contains
        defaultSapPoInfoFiltering(
            "oporNumAtCard.contains=" + DEFAULT_OPOR_NUM_AT_CARD,
            "oporNumAtCard.contains=" + UPDATED_OPOR_NUM_AT_CARD
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporNumAtCardNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporNumAtCard does not contain
        defaultSapPoInfoFiltering(
            "oporNumAtCard.doesNotContain=" + UPDATED_OPOR_NUM_AT_CARD,
            "oporNumAtCard.doesNotContain=" + DEFAULT_OPOR_NUM_AT_CARD
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporSlpCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporSlpCode equals to
        defaultSapPoInfoFiltering(
            "oporSlpCode.equals=" + DEFAULT_OPOR_SLP_CODE,
            "oporSlpCode.equals=" + UPDATED_OPOR_SLP_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporSlpCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporSlpCode in
        defaultSapPoInfoFiltering(
            "oporSlpCode.in=" +
            DEFAULT_OPOR_SLP_CODE +
            "," +
            UPDATED_OPOR_SLP_CODE,
            "oporSlpCode.in=" + UPDATED_OPOR_SLP_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporSlpCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporSlpCode is not null
        defaultSapPoInfoFiltering(
            "oporSlpCode.specified=true",
            "oporSlpCode.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporSlpCodeIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporSlpCode is greater than or equal to
        defaultSapPoInfoFiltering(
            "oporSlpCode.greaterThanOrEqual=" + DEFAULT_OPOR_SLP_CODE,
            "oporSlpCode.greaterThanOrEqual=" + UPDATED_OPOR_SLP_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporSlpCodeIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporSlpCode is less than or equal to
        defaultSapPoInfoFiltering(
            "oporSlpCode.lessThanOrEqual=" + DEFAULT_OPOR_SLP_CODE,
            "oporSlpCode.lessThanOrEqual=" + SMALLER_OPOR_SLP_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporSlpCodeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporSlpCode is less than
        defaultSapPoInfoFiltering(
            "oporSlpCode.lessThan=" + UPDATED_OPOR_SLP_CODE,
            "oporSlpCode.lessThan=" + DEFAULT_OPOR_SLP_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporSlpCodeIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporSlpCode is greater than
        defaultSapPoInfoFiltering(
            "oporSlpCode.greaterThan=" + SMALLER_OPOR_SLP_CODE,
            "oporSlpCode.greaterThan=" + DEFAULT_OPOR_SLP_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporOwnerCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporOwnerCode equals to
        defaultSapPoInfoFiltering(
            "oporOwnerCode.equals=" + DEFAULT_OPOR_OWNER_CODE,
            "oporOwnerCode.equals=" + UPDATED_OPOR_OWNER_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporOwnerCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporOwnerCode in
        defaultSapPoInfoFiltering(
            "oporOwnerCode.in=" +
            DEFAULT_OPOR_OWNER_CODE +
            "," +
            UPDATED_OPOR_OWNER_CODE,
            "oporOwnerCode.in=" + UPDATED_OPOR_OWNER_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporOwnerCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporOwnerCode is not null
        defaultSapPoInfoFiltering(
            "oporOwnerCode.specified=true",
            "oporOwnerCode.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporOwnerCodeIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporOwnerCode is greater than or equal to
        defaultSapPoInfoFiltering(
            "oporOwnerCode.greaterThanOrEqual=" + DEFAULT_OPOR_OWNER_CODE,
            "oporOwnerCode.greaterThanOrEqual=" + UPDATED_OPOR_OWNER_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporOwnerCodeIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporOwnerCode is less than or equal to
        defaultSapPoInfoFiltering(
            "oporOwnerCode.lessThanOrEqual=" + DEFAULT_OPOR_OWNER_CODE,
            "oporOwnerCode.lessThanOrEqual=" + SMALLER_OPOR_OWNER_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporOwnerCodeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporOwnerCode is less than
        defaultSapPoInfoFiltering(
            "oporOwnerCode.lessThan=" + UPDATED_OPOR_OWNER_CODE,
            "oporOwnerCode.lessThan=" + DEFAULT_OPOR_OWNER_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporOwnerCodeIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporOwnerCode is greater than
        defaultSapPoInfoFiltering(
            "oporOwnerCode.greaterThan=" + SMALLER_OPOR_OWNER_CODE,
            "oporOwnerCode.greaterThan=" + DEFAULT_OPOR_OWNER_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporVatSumIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporVatSum equals to
        defaultSapPoInfoFiltering(
            "oporVatSum.equals=" + DEFAULT_OPOR_VAT_SUM,
            "oporVatSum.equals=" + UPDATED_OPOR_VAT_SUM
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporVatSumIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporVatSum in
        defaultSapPoInfoFiltering(
            "oporVatSum.in=" +
            DEFAULT_OPOR_VAT_SUM +
            "," +
            UPDATED_OPOR_VAT_SUM,
            "oporVatSum.in=" + UPDATED_OPOR_VAT_SUM
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporVatSumIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporVatSum is not null
        defaultSapPoInfoFiltering(
            "oporVatSum.specified=true",
            "oporVatSum.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporVatSumIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporVatSum is greater than or equal to
        defaultSapPoInfoFiltering(
            "oporVatSum.greaterThanOrEqual=" + DEFAULT_OPOR_VAT_SUM,
            "oporVatSum.greaterThanOrEqual=" + UPDATED_OPOR_VAT_SUM
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporVatSumIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporVatSum is less than or equal to
        defaultSapPoInfoFiltering(
            "oporVatSum.lessThanOrEqual=" + DEFAULT_OPOR_VAT_SUM,
            "oporVatSum.lessThanOrEqual=" + SMALLER_OPOR_VAT_SUM
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporVatSumIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporVatSum is less than
        defaultSapPoInfoFiltering(
            "oporVatSum.lessThan=" + UPDATED_OPOR_VAT_SUM,
            "oporVatSum.lessThan=" + DEFAULT_OPOR_VAT_SUM
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporVatSumIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporVatSum is greater than
        defaultSapPoInfoFiltering(
            "oporVatSum.greaterThan=" + SMALLER_OPOR_VAT_SUM,
            "oporVatSum.greaterThan=" + DEFAULT_OPOR_VAT_SUM
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocTotal equals to
        defaultSapPoInfoFiltering(
            "oporDocTotal.equals=" + DEFAULT_OPOR_DOC_TOTAL,
            "oporDocTotal.equals=" + UPDATED_OPOR_DOC_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocTotalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocTotal in
        defaultSapPoInfoFiltering(
            "oporDocTotal.in=" +
            DEFAULT_OPOR_DOC_TOTAL +
            "," +
            UPDATED_OPOR_DOC_TOTAL,
            "oporDocTotal.in=" + UPDATED_OPOR_DOC_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocTotal is not null
        defaultSapPoInfoFiltering(
            "oporDocTotal.specified=true",
            "oporDocTotal.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocTotalIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocTotal is greater than or equal to
        defaultSapPoInfoFiltering(
            "oporDocTotal.greaterThanOrEqual=" + DEFAULT_OPOR_DOC_TOTAL,
            "oporDocTotal.greaterThanOrEqual=" + UPDATED_OPOR_DOC_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocTotalIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocTotal is less than or equal to
        defaultSapPoInfoFiltering(
            "oporDocTotal.lessThanOrEqual=" + DEFAULT_OPOR_DOC_TOTAL,
            "oporDocTotal.lessThanOrEqual=" + SMALLER_OPOR_DOC_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocTotal is less than
        defaultSapPoInfoFiltering(
            "oporDocTotal.lessThan=" + UPDATED_OPOR_DOC_TOTAL,
            "oporDocTotal.lessThan=" + DEFAULT_OPOR_DOC_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporDocTotalIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporDocTotal is greater than
        defaultSapPoInfoFiltering(
            "oporDocTotal.greaterThan=" + SMALLER_OPOR_DOC_TOTAL,
            "oporDocTotal.greaterThan=" + DEFAULT_OPOR_DOC_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporVatSumSyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporVatSumSy equals to
        defaultSapPoInfoFiltering(
            "oporVatSumSy.equals=" + DEFAULT_OPOR_VAT_SUM_SY,
            "oporVatSumSy.equals=" + UPDATED_OPOR_VAT_SUM_SY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporVatSumSyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporVatSumSy in
        defaultSapPoInfoFiltering(
            "oporVatSumSy.in=" +
            DEFAULT_OPOR_VAT_SUM_SY +
            "," +
            UPDATED_OPOR_VAT_SUM_SY,
            "oporVatSumSy.in=" + UPDATED_OPOR_VAT_SUM_SY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporVatSumSyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporVatSumSy is not null
        defaultSapPoInfoFiltering(
            "oporVatSumSy.specified=true",
            "oporVatSumSy.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporVatSumSyIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporVatSumSy is greater than or equal to
        defaultSapPoInfoFiltering(
            "oporVatSumSy.greaterThanOrEqual=" + DEFAULT_OPOR_VAT_SUM_SY,
            "oporVatSumSy.greaterThanOrEqual=" + UPDATED_OPOR_VAT_SUM_SY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporVatSumSyIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporVatSumSy is less than or equal to
        defaultSapPoInfoFiltering(
            "oporVatSumSy.lessThanOrEqual=" + DEFAULT_OPOR_VAT_SUM_SY,
            "oporVatSumSy.lessThanOrEqual=" + SMALLER_OPOR_VAT_SUM_SY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporVatSumSyIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporVatSumSy is less than
        defaultSapPoInfoFiltering(
            "oporVatSumSy.lessThan=" + UPDATED_OPOR_VAT_SUM_SY,
            "oporVatSumSy.lessThan=" + DEFAULT_OPOR_VAT_SUM_SY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporVatSumSyIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporVatSumSy is greater than
        defaultSapPoInfoFiltering(
            "oporVatSumSy.greaterThan=" + SMALLER_OPOR_VAT_SUM_SY,
            "oporVatSumSy.greaterThan=" + DEFAULT_OPOR_VAT_SUM_SY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUHtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUHt equals to
        defaultSapPoInfoFiltering(
            "oporUHt.equals=" + DEFAULT_OPOR_U_HT,
            "oporUHt.equals=" + UPDATED_OPOR_U_HT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUHtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUHt in
        defaultSapPoInfoFiltering(
            "oporUHt.in=" + DEFAULT_OPOR_U_HT + "," + UPDATED_OPOR_U_HT,
            "oporUHt.in=" + UPDATED_OPOR_U_HT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUHtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUHt is not null
        defaultSapPoInfoFiltering(
            "oporUHt.specified=true",
            "oporUHt.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUHtContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUHt contains
        defaultSapPoInfoFiltering(
            "oporUHt.contains=" + DEFAULT_OPOR_U_HT,
            "oporUHt.contains=" + UPDATED_OPOR_U_HT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUHtNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUHt does not contain
        defaultSapPoInfoFiltering(
            "oporUHt.doesNotContain=" + UPDATED_OPOR_U_HT,
            "oporUHt.doesNotContain=" + DEFAULT_OPOR_U_HT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUPaymentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUPayment equals to
        defaultSapPoInfoFiltering(
            "oporUPayment.equals=" + DEFAULT_OPOR_U_PAYMENT,
            "oporUPayment.equals=" + UPDATED_OPOR_U_PAYMENT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUPaymentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUPayment in
        defaultSapPoInfoFiltering(
            "oporUPayment.in=" +
            DEFAULT_OPOR_U_PAYMENT +
            "," +
            UPDATED_OPOR_U_PAYMENT,
            "oporUPayment.in=" + UPDATED_OPOR_U_PAYMENT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUPaymentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUPayment is not null
        defaultSapPoInfoFiltering(
            "oporUPayment.specified=true",
            "oporUPayment.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUPaymentContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUPayment contains
        defaultSapPoInfoFiltering(
            "oporUPayment.contains=" + DEFAULT_OPOR_U_PAYMENT,
            "oporUPayment.contains=" + UPDATED_OPOR_U_PAYMENT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByOporUPaymentNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where oporUPayment does not contain
        defaultSapPoInfoFiltering(
            "oporUPayment.doesNotContain=" + UPDATED_OPOR_U_PAYMENT,
            "oporUPayment.doesNotContain=" + DEFAULT_OPOR_U_PAYMENT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1BaseDocNumIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1BaseDocNum equals to
        defaultSapPoInfoFiltering(
            "por1BaseDocNum.equals=" + DEFAULT_POR_1_BASE_DOC_NUM,
            "por1BaseDocNum.equals=" + UPDATED_POR_1_BASE_DOC_NUM
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1BaseDocNumIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1BaseDocNum in
        defaultSapPoInfoFiltering(
            "por1BaseDocNum.in=" +
            DEFAULT_POR_1_BASE_DOC_NUM +
            "," +
            UPDATED_POR_1_BASE_DOC_NUM,
            "por1BaseDocNum.in=" + UPDATED_POR_1_BASE_DOC_NUM
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1BaseDocNumIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1BaseDocNum is not null
        defaultSapPoInfoFiltering(
            "por1BaseDocNum.specified=true",
            "por1BaseDocNum.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1BaseDocNumContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1BaseDocNum contains
        defaultSapPoInfoFiltering(
            "por1BaseDocNum.contains=" + DEFAULT_POR_1_BASE_DOC_NUM,
            "por1BaseDocNum.contains=" + UPDATED_POR_1_BASE_DOC_NUM
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1BaseDocNumNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1BaseDocNum does not contain
        defaultSapPoInfoFiltering(
            "por1BaseDocNum.doesNotContain=" + UPDATED_POR_1_BASE_DOC_NUM,
            "por1BaseDocNum.doesNotContain=" + DEFAULT_POR_1_BASE_DOC_NUM
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1BaseEntryIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1BaseEntry equals to
        defaultSapPoInfoFiltering(
            "por1BaseEntry.equals=" + DEFAULT_POR_1_BASE_ENTRY,
            "por1BaseEntry.equals=" + UPDATED_POR_1_BASE_ENTRY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1BaseEntryIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1BaseEntry in
        defaultSapPoInfoFiltering(
            "por1BaseEntry.in=" +
            DEFAULT_POR_1_BASE_ENTRY +
            "," +
            UPDATED_POR_1_BASE_ENTRY,
            "por1BaseEntry.in=" + UPDATED_POR_1_BASE_ENTRY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1BaseEntryIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1BaseEntry is not null
        defaultSapPoInfoFiltering(
            "por1BaseEntry.specified=true",
            "por1BaseEntry.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1BaseEntryContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1BaseEntry contains
        defaultSapPoInfoFiltering(
            "por1BaseEntry.contains=" + DEFAULT_POR_1_BASE_ENTRY,
            "por1BaseEntry.contains=" + UPDATED_POR_1_BASE_ENTRY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1BaseEntryNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1BaseEntry does not contain
        defaultSapPoInfoFiltering(
            "por1BaseEntry.doesNotContain=" + UPDATED_POR_1_BASE_ENTRY,
            "por1BaseEntry.doesNotContain=" + DEFAULT_POR_1_BASE_ENTRY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1BaseLineIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1BaseLine equals to
        defaultSapPoInfoFiltering(
            "por1BaseLine.equals=" + DEFAULT_POR_1_BASE_LINE,
            "por1BaseLine.equals=" + UPDATED_POR_1_BASE_LINE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1BaseLineIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1BaseLine in
        defaultSapPoInfoFiltering(
            "por1BaseLine.in=" +
            DEFAULT_POR_1_BASE_LINE +
            "," +
            UPDATED_POR_1_BASE_LINE,
            "por1BaseLine.in=" + UPDATED_POR_1_BASE_LINE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1BaseLineIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1BaseLine is not null
        defaultSapPoInfoFiltering(
            "por1BaseLine.specified=true",
            "por1BaseLine.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1BaseLineContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1BaseLine contains
        defaultSapPoInfoFiltering(
            "por1BaseLine.contains=" + DEFAULT_POR_1_BASE_LINE,
            "por1BaseLine.contains=" + UPDATED_POR_1_BASE_LINE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1BaseLineNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1BaseLine does not contain
        defaultSapPoInfoFiltering(
            "por1BaseLine.doesNotContain=" + UPDATED_POR_1_BASE_LINE,
            "por1BaseLine.doesNotContain=" + DEFAULT_POR_1_BASE_LINE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1BaseRefIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1BaseRef equals to
        defaultSapPoInfoFiltering(
            "por1BaseRef.equals=" + DEFAULT_POR_1_BASE_REF,
            "por1BaseRef.equals=" + UPDATED_POR_1_BASE_REF
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1BaseRefIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1BaseRef in
        defaultSapPoInfoFiltering(
            "por1BaseRef.in=" +
            DEFAULT_POR_1_BASE_REF +
            "," +
            UPDATED_POR_1_BASE_REF,
            "por1BaseRef.in=" + UPDATED_POR_1_BASE_REF
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1BaseRefIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1BaseRef is not null
        defaultSapPoInfoFiltering(
            "por1BaseRef.specified=true",
            "por1BaseRef.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1BaseRefContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1BaseRef contains
        defaultSapPoInfoFiltering(
            "por1BaseRef.contains=" + DEFAULT_POR_1_BASE_REF,
            "por1BaseRef.contains=" + UPDATED_POR_1_BASE_REF
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1BaseRefNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1BaseRef does not contain
        defaultSapPoInfoFiltering(
            "por1BaseRef.doesNotContain=" + UPDATED_POR_1_BASE_REF,
            "por1BaseRef.doesNotContain=" + DEFAULT_POR_1_BASE_REF
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1CurrencyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1Currency equals to
        defaultSapPoInfoFiltering(
            "por1Currency.equals=" + DEFAULT_POR_1_CURRENCY,
            "por1Currency.equals=" + UPDATED_POR_1_CURRENCY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1CurrencyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1Currency in
        defaultSapPoInfoFiltering(
            "por1Currency.in=" +
            DEFAULT_POR_1_CURRENCY +
            "," +
            UPDATED_POR_1_CURRENCY,
            "por1Currency.in=" + UPDATED_POR_1_CURRENCY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1CurrencyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1Currency is not null
        defaultSapPoInfoFiltering(
            "por1Currency.specified=true",
            "por1Currency.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1CurrencyContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1Currency contains
        defaultSapPoInfoFiltering(
            "por1Currency.contains=" + DEFAULT_POR_1_CURRENCY,
            "por1Currency.contains=" + UPDATED_POR_1_CURRENCY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1CurrencyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1Currency does not contain
        defaultSapPoInfoFiltering(
            "por1Currency.doesNotContain=" + UPDATED_POR_1_CURRENCY,
            "por1Currency.doesNotContain=" + DEFAULT_POR_1_CURRENCY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1DiscPrcntIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1DiscPrcnt equals to
        defaultSapPoInfoFiltering(
            "por1DiscPrcnt.equals=" + DEFAULT_POR_1_DISC_PRCNT,
            "por1DiscPrcnt.equals=" + UPDATED_POR_1_DISC_PRCNT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1DiscPrcntIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1DiscPrcnt in
        defaultSapPoInfoFiltering(
            "por1DiscPrcnt.in=" +
            DEFAULT_POR_1_DISC_PRCNT +
            "," +
            UPDATED_POR_1_DISC_PRCNT,
            "por1DiscPrcnt.in=" + UPDATED_POR_1_DISC_PRCNT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1DiscPrcntIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1DiscPrcnt is not null
        defaultSapPoInfoFiltering(
            "por1DiscPrcnt.specified=true",
            "por1DiscPrcnt.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1DiscPrcntContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1DiscPrcnt contains
        defaultSapPoInfoFiltering(
            "por1DiscPrcnt.contains=" + DEFAULT_POR_1_DISC_PRCNT,
            "por1DiscPrcnt.contains=" + UPDATED_POR_1_DISC_PRCNT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1DiscPrcntNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1DiscPrcnt does not contain
        defaultSapPoInfoFiltering(
            "por1DiscPrcnt.doesNotContain=" + UPDATED_POR_1_DISC_PRCNT,
            "por1DiscPrcnt.doesNotContain=" + DEFAULT_POR_1_DISC_PRCNT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPoDocEntryIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where poDocEntry equals to
        defaultSapPoInfoFiltering(
            "poDocEntry.equals=" + DEFAULT_PO_DOC_ENTRY,
            "poDocEntry.equals=" + UPDATED_PO_DOC_ENTRY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPoDocEntryIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where poDocEntry in
        defaultSapPoInfoFiltering(
            "poDocEntry.in=" +
            DEFAULT_PO_DOC_ENTRY +
            "," +
            UPDATED_PO_DOC_ENTRY,
            "poDocEntry.in=" + UPDATED_PO_DOC_ENTRY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPoDocEntryIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where poDocEntry is not null
        defaultSapPoInfoFiltering(
            "poDocEntry.specified=true",
            "poDocEntry.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPoDocEntryContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where poDocEntry contains
        defaultSapPoInfoFiltering(
            "poDocEntry.contains=" + DEFAULT_PO_DOC_ENTRY,
            "poDocEntry.contains=" + UPDATED_PO_DOC_ENTRY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPoDocEntryNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where poDocEntry does not contain
        defaultSapPoInfoFiltering(
            "poDocEntry.doesNotContain=" + UPDATED_PO_DOC_ENTRY,
            "poDocEntry.doesNotContain=" + DEFAULT_PO_DOC_ENTRY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1DscriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1Dscription equals to
        defaultSapPoInfoFiltering(
            "por1Dscription.equals=" + DEFAULT_POR_1_DSCRIPTION,
            "por1Dscription.equals=" + UPDATED_POR_1_DSCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1DscriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1Dscription in
        defaultSapPoInfoFiltering(
            "por1Dscription.in=" +
            DEFAULT_POR_1_DSCRIPTION +
            "," +
            UPDATED_POR_1_DSCRIPTION,
            "por1Dscription.in=" + UPDATED_POR_1_DSCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1DscriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1Dscription is not null
        defaultSapPoInfoFiltering(
            "por1Dscription.specified=true",
            "por1Dscription.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1DscriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1Dscription contains
        defaultSapPoInfoFiltering(
            "por1Dscription.contains=" + DEFAULT_POR_1_DSCRIPTION,
            "por1Dscription.contains=" + UPDATED_POR_1_DSCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1DscriptionNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1Dscription does not contain
        defaultSapPoInfoFiltering(
            "por1Dscription.doesNotContain=" + UPDATED_POR_1_DSCRIPTION,
            "por1Dscription.doesNotContain=" + DEFAULT_POR_1_DSCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1ItemCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1ItemCode equals to
        defaultSapPoInfoFiltering(
            "por1ItemCode.equals=" + DEFAULT_POR_1_ITEM_CODE,
            "por1ItemCode.equals=" + UPDATED_POR_1_ITEM_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1ItemCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1ItemCode in
        defaultSapPoInfoFiltering(
            "por1ItemCode.in=" +
            DEFAULT_POR_1_ITEM_CODE +
            "," +
            UPDATED_POR_1_ITEM_CODE,
            "por1ItemCode.in=" + UPDATED_POR_1_ITEM_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1ItemCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1ItemCode is not null
        defaultSapPoInfoFiltering(
            "por1ItemCode.specified=true",
            "por1ItemCode.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1ItemCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1ItemCode contains
        defaultSapPoInfoFiltering(
            "por1ItemCode.contains=" + DEFAULT_POR_1_ITEM_CODE,
            "por1ItemCode.contains=" + UPDATED_POR_1_ITEM_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1ItemCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1ItemCode does not contain
        defaultSapPoInfoFiltering(
            "por1ItemCode.doesNotContain=" + UPDATED_POR_1_ITEM_CODE,
            "por1ItemCode.doesNotContain=" + DEFAULT_POR_1_ITEM_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1LineNumIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1LineNum equals to
        defaultSapPoInfoFiltering(
            "por1LineNum.equals=" + DEFAULT_POR_1_LINE_NUM,
            "por1LineNum.equals=" + UPDATED_POR_1_LINE_NUM
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1LineNumIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1LineNum in
        defaultSapPoInfoFiltering(
            "por1LineNum.in=" +
            DEFAULT_POR_1_LINE_NUM +
            "," +
            UPDATED_POR_1_LINE_NUM,
            "por1LineNum.in=" + UPDATED_POR_1_LINE_NUM
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1LineNumIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1LineNum is not null
        defaultSapPoInfoFiltering(
            "por1LineNum.specified=true",
            "por1LineNum.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1LineNumContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1LineNum contains
        defaultSapPoInfoFiltering(
            "por1LineNum.contains=" + DEFAULT_POR_1_LINE_NUM,
            "por1LineNum.contains=" + UPDATED_POR_1_LINE_NUM
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1LineNumNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1LineNum does not contain
        defaultSapPoInfoFiltering(
            "por1LineNum.doesNotContain=" + UPDATED_POR_1_LINE_NUM,
            "por1LineNum.doesNotContain=" + DEFAULT_POR_1_LINE_NUM
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1LineStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1LineStatus equals to
        defaultSapPoInfoFiltering(
            "por1LineStatus.equals=" + DEFAULT_POR_1_LINE_STATUS,
            "por1LineStatus.equals=" + UPDATED_POR_1_LINE_STATUS
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1LineStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1LineStatus in
        defaultSapPoInfoFiltering(
            "por1LineStatus.in=" +
            DEFAULT_POR_1_LINE_STATUS +
            "," +
            UPDATED_POR_1_LINE_STATUS,
            "por1LineStatus.in=" + UPDATED_POR_1_LINE_STATUS
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1LineStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1LineStatus is not null
        defaultSapPoInfoFiltering(
            "por1LineStatus.specified=true",
            "por1LineStatus.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1LineStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1LineStatus contains
        defaultSapPoInfoFiltering(
            "por1LineStatus.contains=" + DEFAULT_POR_1_LINE_STATUS,
            "por1LineStatus.contains=" + UPDATED_POR_1_LINE_STATUS
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1LineStatusNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1LineStatus does not contain
        defaultSapPoInfoFiltering(
            "por1LineStatus.doesNotContain=" + UPDATED_POR_1_LINE_STATUS,
            "por1LineStatus.doesNotContain=" + DEFAULT_POR_1_LINE_STATUS
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1LineVendorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1LineVendor equals to
        defaultSapPoInfoFiltering(
            "por1LineVendor.equals=" + DEFAULT_POR_1_LINE_VENDOR,
            "por1LineVendor.equals=" + UPDATED_POR_1_LINE_VENDOR
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1LineVendorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1LineVendor in
        defaultSapPoInfoFiltering(
            "por1LineVendor.in=" +
            DEFAULT_POR_1_LINE_VENDOR +
            "," +
            UPDATED_POR_1_LINE_VENDOR,
            "por1LineVendor.in=" + UPDATED_POR_1_LINE_VENDOR
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1LineVendorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1LineVendor is not null
        defaultSapPoInfoFiltering(
            "por1LineVendor.specified=true",
            "por1LineVendor.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1LineVendorContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1LineVendor contains
        defaultSapPoInfoFiltering(
            "por1LineVendor.contains=" + DEFAULT_POR_1_LINE_VENDOR,
            "por1LineVendor.contains=" + UPDATED_POR_1_LINE_VENDOR
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1LineVendorNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1LineVendor does not contain
        defaultSapPoInfoFiltering(
            "por1LineVendor.doesNotContain=" + UPDATED_POR_1_LINE_VENDOR,
            "por1LineVendor.doesNotContain=" + DEFAULT_POR_1_LINE_VENDOR
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1OpenSumSysIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1OpenSumSys equals to
        defaultSapPoInfoFiltering(
            "por1OpenSumSys.equals=" + DEFAULT_POR_1_OPEN_SUM_SYS,
            "por1OpenSumSys.equals=" + UPDATED_POR_1_OPEN_SUM_SYS
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1OpenSumSysIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1OpenSumSys in
        defaultSapPoInfoFiltering(
            "por1OpenSumSys.in=" +
            DEFAULT_POR_1_OPEN_SUM_SYS +
            "," +
            UPDATED_POR_1_OPEN_SUM_SYS,
            "por1OpenSumSys.in=" + UPDATED_POR_1_OPEN_SUM_SYS
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1OpenSumSysIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1OpenSumSys is not null
        defaultSapPoInfoFiltering(
            "por1OpenSumSys.specified=true",
            "por1OpenSumSys.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1OpenSumSysContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1OpenSumSys contains
        defaultSapPoInfoFiltering(
            "por1OpenSumSys.contains=" + DEFAULT_POR_1_OPEN_SUM_SYS,
            "por1OpenSumSys.contains=" + UPDATED_POR_1_OPEN_SUM_SYS
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1OpenSumSysNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1OpenSumSys does not contain
        defaultSapPoInfoFiltering(
            "por1OpenSumSys.doesNotContain=" + UPDATED_POR_1_OPEN_SUM_SYS,
            "por1OpenSumSys.doesNotContain=" + DEFAULT_POR_1_OPEN_SUM_SYS
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1PriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1Price equals to
        defaultSapPoInfoFiltering(
            "por1Price.equals=" + DEFAULT_POR_1_PRICE,
            "por1Price.equals=" + UPDATED_POR_1_PRICE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1PriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1Price in
        defaultSapPoInfoFiltering(
            "por1Price.in=" + DEFAULT_POR_1_PRICE + "," + UPDATED_POR_1_PRICE,
            "por1Price.in=" + UPDATED_POR_1_PRICE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1PriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1Price is not null
        defaultSapPoInfoFiltering(
            "por1Price.specified=true",
            "por1Price.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1PriceContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1Price contains
        defaultSapPoInfoFiltering(
            "por1Price.contains=" + DEFAULT_POR_1_PRICE,
            "por1Price.contains=" + UPDATED_POR_1_PRICE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1PriceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1Price does not contain
        defaultSapPoInfoFiltering(
            "por1Price.doesNotContain=" + UPDATED_POR_1_PRICE,
            "por1Price.doesNotContain=" + DEFAULT_POR_1_PRICE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1QuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1Quantity equals to
        defaultSapPoInfoFiltering(
            "por1Quantity.equals=" + DEFAULT_POR_1_QUANTITY,
            "por1Quantity.equals=" + UPDATED_POR_1_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1QuantityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1Quantity in
        defaultSapPoInfoFiltering(
            "por1Quantity.in=" +
            DEFAULT_POR_1_QUANTITY +
            "," +
            UPDATED_POR_1_QUANTITY,
            "por1Quantity.in=" + UPDATED_POR_1_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1QuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1Quantity is not null
        defaultSapPoInfoFiltering(
            "por1Quantity.specified=true",
            "por1Quantity.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1QuantityContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1Quantity contains
        defaultSapPoInfoFiltering(
            "por1Quantity.contains=" + DEFAULT_POR_1_QUANTITY,
            "por1Quantity.contains=" + UPDATED_POR_1_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1QuantityNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1Quantity does not contain
        defaultSapPoInfoFiltering(
            "por1Quantity.doesNotContain=" + UPDATED_POR_1_QUANTITY,
            "por1Quantity.doesNotContain=" + DEFAULT_POR_1_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1ShipDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1ShipDate equals to
        defaultSapPoInfoFiltering(
            "por1ShipDate.equals=" + DEFAULT_POR_1_SHIP_DATE,
            "por1ShipDate.equals=" + UPDATED_POR_1_SHIP_DATE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1ShipDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1ShipDate in
        defaultSapPoInfoFiltering(
            "por1ShipDate.in=" +
            DEFAULT_POR_1_SHIP_DATE +
            "," +
            UPDATED_POR_1_SHIP_DATE,
            "por1ShipDate.in=" + UPDATED_POR_1_SHIP_DATE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1ShipDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1ShipDate is not null
        defaultSapPoInfoFiltering(
            "por1ShipDate.specified=true",
            "por1ShipDate.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1TotalFrgnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1TotalFrgn equals to
        defaultSapPoInfoFiltering(
            "por1TotalFrgn.equals=" + DEFAULT_POR_1_TOTAL_FRGN,
            "por1TotalFrgn.equals=" + UPDATED_POR_1_TOTAL_FRGN
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1TotalFrgnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1TotalFrgn in
        defaultSapPoInfoFiltering(
            "por1TotalFrgn.in=" +
            DEFAULT_POR_1_TOTAL_FRGN +
            "," +
            UPDATED_POR_1_TOTAL_FRGN,
            "por1TotalFrgn.in=" + UPDATED_POR_1_TOTAL_FRGN
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1TotalFrgnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1TotalFrgn is not null
        defaultSapPoInfoFiltering(
            "por1TotalFrgn.specified=true",
            "por1TotalFrgn.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1TotalFrgnContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1TotalFrgn contains
        defaultSapPoInfoFiltering(
            "por1TotalFrgn.contains=" + DEFAULT_POR_1_TOTAL_FRGN,
            "por1TotalFrgn.contains=" + UPDATED_POR_1_TOTAL_FRGN
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1TotalFrgnNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1TotalFrgn does not contain
        defaultSapPoInfoFiltering(
            "por1TotalFrgn.doesNotContain=" + UPDATED_POR_1_TOTAL_FRGN,
            "por1TotalFrgn.doesNotContain=" + DEFAULT_POR_1_TOTAL_FRGN
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1TotalSumsyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1TotalSumsy equals to
        defaultSapPoInfoFiltering(
            "por1TotalSumsy.equals=" + DEFAULT_POR_1_TOTAL_SUMSY,
            "por1TotalSumsy.equals=" + UPDATED_POR_1_TOTAL_SUMSY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1TotalSumsyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1TotalSumsy in
        defaultSapPoInfoFiltering(
            "por1TotalSumsy.in=" +
            DEFAULT_POR_1_TOTAL_SUMSY +
            "," +
            UPDATED_POR_1_TOTAL_SUMSY,
            "por1TotalSumsy.in=" + UPDATED_POR_1_TOTAL_SUMSY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1TotalSumsyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1TotalSumsy is not null
        defaultSapPoInfoFiltering(
            "por1TotalSumsy.specified=true",
            "por1TotalSumsy.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1TotalSumsyContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1TotalSumsy contains
        defaultSapPoInfoFiltering(
            "por1TotalSumsy.contains=" + DEFAULT_POR_1_TOTAL_SUMSY,
            "por1TotalSumsy.contains=" + UPDATED_POR_1_TOTAL_SUMSY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1TotalSumsyNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1TotalSumsy does not contain
        defaultSapPoInfoFiltering(
            "por1TotalSumsy.doesNotContain=" + UPDATED_POR_1_TOTAL_SUMSY,
            "por1TotalSumsy.doesNotContain=" + DEFAULT_POR_1_TOTAL_SUMSY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1TrgetEntryIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1TrgetEntry equals to
        defaultSapPoInfoFiltering(
            "por1TrgetEntry.equals=" + DEFAULT_POR_1_TRGET_ENTRY,
            "por1TrgetEntry.equals=" + UPDATED_POR_1_TRGET_ENTRY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1TrgetEntryIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1TrgetEntry in
        defaultSapPoInfoFiltering(
            "por1TrgetEntry.in=" +
            DEFAULT_POR_1_TRGET_ENTRY +
            "," +
            UPDATED_POR_1_TRGET_ENTRY,
            "por1TrgetEntry.in=" + UPDATED_POR_1_TRGET_ENTRY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1TrgetEntryIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1TrgetEntry is not null
        defaultSapPoInfoFiltering(
            "por1TrgetEntry.specified=true",
            "por1TrgetEntry.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1TrgetEntryContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1TrgetEntry contains
        defaultSapPoInfoFiltering(
            "por1TrgetEntry.contains=" + DEFAULT_POR_1_TRGET_ENTRY,
            "por1TrgetEntry.contains=" + UPDATED_POR_1_TRGET_ENTRY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1TrgetEntryNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1TrgetEntry does not contain
        defaultSapPoInfoFiltering(
            "por1TrgetEntry.doesNotContain=" + UPDATED_POR_1_TRGET_ENTRY,
            "por1TrgetEntry.doesNotContain=" + DEFAULT_POR_1_TRGET_ENTRY
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1UMcodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1UMcode equals to
        defaultSapPoInfoFiltering(
            "por1UMcode.equals=" + DEFAULT_POR_1_U_MCODE,
            "por1UMcode.equals=" + UPDATED_POR_1_U_MCODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1UMcodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1UMcode in
        defaultSapPoInfoFiltering(
            "por1UMcode.in=" +
            DEFAULT_POR_1_U_MCODE +
            "," +
            UPDATED_POR_1_U_MCODE,
            "por1UMcode.in=" + UPDATED_POR_1_U_MCODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1UMcodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1UMcode is not null
        defaultSapPoInfoFiltering(
            "por1UMcode.specified=true",
            "por1UMcode.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1UMcodeContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1UMcode contains
        defaultSapPoInfoFiltering(
            "por1UMcode.contains=" + DEFAULT_POR_1_U_MCODE,
            "por1UMcode.contains=" + UPDATED_POR_1_U_MCODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1UMcodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1UMcode does not contain
        defaultSapPoInfoFiltering(
            "por1UMcode.doesNotContain=" + UPDATED_POR_1_U_MCODE,
            "por1UMcode.doesNotContain=" + DEFAULT_POR_1_U_MCODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1USoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1USo equals to
        defaultSapPoInfoFiltering(
            "por1USo.equals=" + DEFAULT_POR_1_U_SO,
            "por1USo.equals=" + UPDATED_POR_1_U_SO
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1USoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1USo in
        defaultSapPoInfoFiltering(
            "por1USo.in=" + DEFAULT_POR_1_U_SO + "," + UPDATED_POR_1_U_SO,
            "por1USo.in=" + UPDATED_POR_1_U_SO
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1USoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1USo is not null
        defaultSapPoInfoFiltering(
            "por1USo.specified=true",
            "por1USo.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1USoContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1USo contains
        defaultSapPoInfoFiltering(
            "por1USo.contains=" + DEFAULT_POR_1_U_SO,
            "por1USo.contains=" + UPDATED_POR_1_U_SO
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1USoNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1USo does not contain
        defaultSapPoInfoFiltering(
            "por1USo.doesNotContain=" + UPDATED_POR_1_U_SO,
            "por1USo.doesNotContain=" + DEFAULT_POR_1_U_SO
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1UTenkythuatIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1UTenkythuat equals to
        defaultSapPoInfoFiltering(
            "por1UTenkythuat.equals=" + DEFAULT_POR_1_U_TENKYTHUAT,
            "por1UTenkythuat.equals=" + UPDATED_POR_1_U_TENKYTHUAT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1UTenkythuatIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1UTenkythuat in
        defaultSapPoInfoFiltering(
            "por1UTenkythuat.in=" +
            DEFAULT_POR_1_U_TENKYTHUAT +
            "," +
            UPDATED_POR_1_U_TENKYTHUAT,
            "por1UTenkythuat.in=" + UPDATED_POR_1_U_TENKYTHUAT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1UTenkythuatIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1UTenkythuat is not null
        defaultSapPoInfoFiltering(
            "por1UTenkythuat.specified=true",
            "por1UTenkythuat.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1UTenkythuatContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1UTenkythuat contains
        defaultSapPoInfoFiltering(
            "por1UTenkythuat.contains=" + DEFAULT_POR_1_U_TENKYTHUAT,
            "por1UTenkythuat.contains=" + UPDATED_POR_1_U_TENKYTHUAT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1UTenkythuatNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1UTenkythuat does not contain
        defaultSapPoInfoFiltering(
            "por1UTenkythuat.doesNotContain=" + UPDATED_POR_1_U_TENKYTHUAT,
            "por1UTenkythuat.doesNotContain=" + DEFAULT_POR_1_U_TENKYTHUAT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1UnitMsrIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1UnitMsr equals to
        defaultSapPoInfoFiltering(
            "por1UnitMsr.equals=" + DEFAULT_POR_1_UNIT_MSR,
            "por1UnitMsr.equals=" + UPDATED_POR_1_UNIT_MSR
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1UnitMsrIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1UnitMsr in
        defaultSapPoInfoFiltering(
            "por1UnitMsr.in=" +
            DEFAULT_POR_1_UNIT_MSR +
            "," +
            UPDATED_POR_1_UNIT_MSR,
            "por1UnitMsr.in=" + UPDATED_POR_1_UNIT_MSR
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1UnitMsrIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1UnitMsr is not null
        defaultSapPoInfoFiltering(
            "por1UnitMsr.specified=true",
            "por1UnitMsr.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1UnitMsrContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1UnitMsr contains
        defaultSapPoInfoFiltering(
            "por1UnitMsr.contains=" + DEFAULT_POR_1_UNIT_MSR,
            "por1UnitMsr.contains=" + UPDATED_POR_1_UNIT_MSR
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1UnitMsrNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1UnitMsr does not contain
        defaultSapPoInfoFiltering(
            "por1UnitMsr.doesNotContain=" + UPDATED_POR_1_UNIT_MSR,
            "por1UnitMsr.doesNotContain=" + DEFAULT_POR_1_UNIT_MSR
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1UOMCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1UOMCode equals to
        defaultSapPoInfoFiltering(
            "por1UOMCode.equals=" + DEFAULT_POR_1_UOM_CODE,
            "por1UOMCode.equals=" + UPDATED_POR_1_UOM_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1UOMCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1UOMCode in
        defaultSapPoInfoFiltering(
            "por1UOMCode.in=" +
            DEFAULT_POR_1_UOM_CODE +
            "," +
            UPDATED_POR_1_UOM_CODE,
            "por1UOMCode.in=" + UPDATED_POR_1_UOM_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1UOMCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1UOMCode is not null
        defaultSapPoInfoFiltering(
            "por1UOMCode.specified=true",
            "por1UOMCode.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1UOMCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1UOMCode contains
        defaultSapPoInfoFiltering(
            "por1UOMCode.contains=" + DEFAULT_POR_1_UOM_CODE,
            "por1UOMCode.contains=" + UPDATED_POR_1_UOM_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1UOMCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1UOMCode does not contain
        defaultSapPoInfoFiltering(
            "por1UOMCode.doesNotContain=" + UPDATED_POR_1_UOM_CODE,
            "por1UOMCode.doesNotContain=" + DEFAULT_POR_1_UOM_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1VatGroupIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1VatGroup equals to
        defaultSapPoInfoFiltering(
            "por1VatGroup.equals=" + DEFAULT_POR_1_VAT_GROUP,
            "por1VatGroup.equals=" + UPDATED_POR_1_VAT_GROUP
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1VatGroupIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1VatGroup in
        defaultSapPoInfoFiltering(
            "por1VatGroup.in=" +
            DEFAULT_POR_1_VAT_GROUP +
            "," +
            UPDATED_POR_1_VAT_GROUP,
            "por1VatGroup.in=" + UPDATED_POR_1_VAT_GROUP
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1VatGroupIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1VatGroup is not null
        defaultSapPoInfoFiltering(
            "por1VatGroup.specified=true",
            "por1VatGroup.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1VatGroupContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1VatGroup contains
        defaultSapPoInfoFiltering(
            "por1VatGroup.contains=" + DEFAULT_POR_1_VAT_GROUP,
            "por1VatGroup.contains=" + UPDATED_POR_1_VAT_GROUP
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1VatGroupNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1VatGroup does not contain
        defaultSapPoInfoFiltering(
            "por1VatGroup.doesNotContain=" + UPDATED_POR_1_VAT_GROUP,
            "por1VatGroup.doesNotContain=" + DEFAULT_POR_1_VAT_GROUP
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1LineTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1LineTotal equals to
        defaultSapPoInfoFiltering(
            "por1LineTotal.equals=" + DEFAULT_POR_1_LINE_TOTAL,
            "por1LineTotal.equals=" + UPDATED_POR_1_LINE_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1LineTotalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1LineTotal in
        defaultSapPoInfoFiltering(
            "por1LineTotal.in=" +
            DEFAULT_POR_1_LINE_TOTAL +
            "," +
            UPDATED_POR_1_LINE_TOTAL,
            "por1LineTotal.in=" + UPDATED_POR_1_LINE_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1LineTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1LineTotal is not null
        defaultSapPoInfoFiltering(
            "por1LineTotal.specified=true",
            "por1LineTotal.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1LineTotalIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1LineTotal is greater than or equal to
        defaultSapPoInfoFiltering(
            "por1LineTotal.greaterThanOrEqual=" + DEFAULT_POR_1_LINE_TOTAL,
            "por1LineTotal.greaterThanOrEqual=" + UPDATED_POR_1_LINE_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1LineTotalIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1LineTotal is less than or equal to
        defaultSapPoInfoFiltering(
            "por1LineTotal.lessThanOrEqual=" + DEFAULT_POR_1_LINE_TOTAL,
            "por1LineTotal.lessThanOrEqual=" + SMALLER_POR_1_LINE_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1LineTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1LineTotal is less than
        defaultSapPoInfoFiltering(
            "por1LineTotal.lessThan=" + UPDATED_POR_1_LINE_TOTAL,
            "por1LineTotal.lessThan=" + DEFAULT_POR_1_LINE_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1LineTotalIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1LineTotal is greater than
        defaultSapPoInfoFiltering(
            "por1LineTotal.greaterThan=" + SMALLER_POR_1_LINE_TOTAL,
            "por1LineTotal.greaterThan=" + DEFAULT_POR_1_LINE_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1VatPrcntIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1VatPrcnt equals to
        defaultSapPoInfoFiltering(
            "por1VatPrcnt.equals=" + DEFAULT_POR_1_VAT_PRCNT,
            "por1VatPrcnt.equals=" + UPDATED_POR_1_VAT_PRCNT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1VatPrcntIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1VatPrcnt in
        defaultSapPoInfoFiltering(
            "por1VatPrcnt.in=" +
            DEFAULT_POR_1_VAT_PRCNT +
            "," +
            UPDATED_POR_1_VAT_PRCNT,
            "por1VatPrcnt.in=" + UPDATED_POR_1_VAT_PRCNT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1VatPrcntIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1VatPrcnt is not null
        defaultSapPoInfoFiltering(
            "por1VatPrcnt.specified=true",
            "por1VatPrcnt.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1VatPrcntIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1VatPrcnt is greater than or equal to
        defaultSapPoInfoFiltering(
            "por1VatPrcnt.greaterThanOrEqual=" + DEFAULT_POR_1_VAT_PRCNT,
            "por1VatPrcnt.greaterThanOrEqual=" + UPDATED_POR_1_VAT_PRCNT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1VatPrcntIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1VatPrcnt is less than or equal to
        defaultSapPoInfoFiltering(
            "por1VatPrcnt.lessThanOrEqual=" + DEFAULT_POR_1_VAT_PRCNT,
            "por1VatPrcnt.lessThanOrEqual=" + SMALLER_POR_1_VAT_PRCNT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1VatPrcntIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1VatPrcnt is less than
        defaultSapPoInfoFiltering(
            "por1VatPrcnt.lessThan=" + UPDATED_POR_1_VAT_PRCNT,
            "por1VatPrcnt.lessThan=" + DEFAULT_POR_1_VAT_PRCNT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1VatPrcntIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1VatPrcnt is greater than
        defaultSapPoInfoFiltering(
            "por1VatPrcnt.greaterThan=" + SMALLER_POR_1_VAT_PRCNT,
            "por1VatPrcnt.greaterThan=" + DEFAULT_POR_1_VAT_PRCNT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1PriceAfVatIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1PriceAfVat equals to
        defaultSapPoInfoFiltering(
            "por1PriceAfVat.equals=" + DEFAULT_POR_1_PRICE_AF_VAT,
            "por1PriceAfVat.equals=" + UPDATED_POR_1_PRICE_AF_VAT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1PriceAfVatIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1PriceAfVat in
        defaultSapPoInfoFiltering(
            "por1PriceAfVat.in=" +
            DEFAULT_POR_1_PRICE_AF_VAT +
            "," +
            UPDATED_POR_1_PRICE_AF_VAT,
            "por1PriceAfVat.in=" + UPDATED_POR_1_PRICE_AF_VAT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1PriceAfVatIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1PriceAfVat is not null
        defaultSapPoInfoFiltering(
            "por1PriceAfVat.specified=true",
            "por1PriceAfVat.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1PriceAfVatIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1PriceAfVat is greater than or equal to
        defaultSapPoInfoFiltering(
            "por1PriceAfVat.greaterThanOrEqual=" + DEFAULT_POR_1_PRICE_AF_VAT,
            "por1PriceAfVat.greaterThanOrEqual=" + UPDATED_POR_1_PRICE_AF_VAT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1PriceAfVatIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1PriceAfVat is less than or equal to
        defaultSapPoInfoFiltering(
            "por1PriceAfVat.lessThanOrEqual=" + DEFAULT_POR_1_PRICE_AF_VAT,
            "por1PriceAfVat.lessThanOrEqual=" + SMALLER_POR_1_PRICE_AF_VAT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1PriceAfVatIsLessThanSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1PriceAfVat is less than
        defaultSapPoInfoFiltering(
            "por1PriceAfVat.lessThan=" + UPDATED_POR_1_PRICE_AF_VAT,
            "por1PriceAfVat.lessThan=" + DEFAULT_POR_1_PRICE_AF_VAT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1PriceAfVatIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1PriceAfVat is greater than
        defaultSapPoInfoFiltering(
            "por1PriceAfVat.greaterThan=" + SMALLER_POR_1_PRICE_AF_VAT,
            "por1PriceAfVat.greaterThan=" + DEFAULT_POR_1_PRICE_AF_VAT
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1WhsCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1WhsCode equals to
        defaultSapPoInfoFiltering(
            "por1WhsCode.equals=" + DEFAULT_POR_1_WHS_CODE,
            "por1WhsCode.equals=" + UPDATED_POR_1_WHS_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1WhsCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1WhsCode in
        defaultSapPoInfoFiltering(
            "por1WhsCode.in=" +
            DEFAULT_POR_1_WHS_CODE +
            "," +
            UPDATED_POR_1_WHS_CODE,
            "por1WhsCode.in=" + UPDATED_POR_1_WHS_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1WhsCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1WhsCode is not null
        defaultSapPoInfoFiltering(
            "por1WhsCode.specified=true",
            "por1WhsCode.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1WhsCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1WhsCode contains
        defaultSapPoInfoFiltering(
            "por1WhsCode.contains=" + DEFAULT_POR_1_WHS_CODE,
            "por1WhsCode.contains=" + UPDATED_POR_1_WHS_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPor1WhsCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where por1WhsCode does not contain
        defaultSapPoInfoFiltering(
            "por1WhsCode.doesNotContain=" + UPDATED_POR_1_WHS_CODE,
            "por1WhsCode.doesNotContain=" + DEFAULT_POR_1_WHS_CODE
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPrMapPoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where prMapPo equals to
        defaultSapPoInfoFiltering(
            "prMapPo.equals=" + DEFAULT_PR_MAP_PO,
            "prMapPo.equals=" + UPDATED_PR_MAP_PO
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPrMapPoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where prMapPo in
        defaultSapPoInfoFiltering(
            "prMapPo.in=" + DEFAULT_PR_MAP_PO + "," + UPDATED_PR_MAP_PO,
            "prMapPo.in=" + UPDATED_PR_MAP_PO
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPrMapPoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where prMapPo is not null
        defaultSapPoInfoFiltering(
            "prMapPo.specified=true",
            "prMapPo.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPrMapPoContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where prMapPo contains
        defaultSapPoInfoFiltering(
            "prMapPo.contains=" + DEFAULT_PR_MAP_PO,
            "prMapPo.contains=" + UPDATED_PR_MAP_PO
        );
    }

    @Test
    @Transactional
    void getAllSapPoInfosByPrMapPoNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        // Get all the sapPoInfoList where prMapPo does not contain
        defaultSapPoInfoFiltering(
            "prMapPo.doesNotContain=" + UPDATED_PR_MAP_PO,
            "prMapPo.doesNotContain=" + DEFAULT_PR_MAP_PO
        );
    }

    private void defaultSapPoInfoFiltering(
        String shouldBeFound,
        String shouldNotBeFound
    ) throws Exception {
        defaultSapPoInfoShouldBeFound(shouldBeFound);
        defaultSapPoInfoShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSapPoInfoShouldBeFound(String filter) throws Exception {
        restSapPoInfoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(sapPoInfo.getId().intValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].oporBranch").value(hasItem(DEFAULT_OPOR_BRANCH))
            )
            .andExpect(
                jsonPath("$.[*].oporCanceled").value(
                    hasItem(DEFAULT_OPOR_CANCELED)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporCardCode").value(
                    hasItem(DEFAULT_OPOR_CARD_CODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporCardName").value(
                    hasItem(DEFAULT_OPOR_CARD_NAME)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporComments").value(
                    hasItem(DEFAULT_OPOR_COMMENTS)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporCreateDate").value(
                    hasItem(DEFAULT_OPOR_CREATE_DATE.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].oporDepartment").value(
                    hasItem(DEFAULT_OPOR_DEPARTMENT)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporDocDate").value(
                    hasItem(DEFAULT_OPOR_DOC_DATE.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].oporDocDueDate").value(
                    hasItem(DEFAULT_OPOR_DOC_DUE_DATE.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].oporDocEntry").value(
                    hasItem(DEFAULT_OPOR_DOC_ENTRY)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporDocNum").value(
                    hasItem(DEFAULT_OPOR_DOC_NUM)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporDocStatus").value(
                    hasItem(DEFAULT_OPOR_DOC_STATUS)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporInvntSttus").value(
                    hasItem(DEFAULT_OPOR_INVNT_STTUS)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporJrnlMemo").value(
                    hasItem(DEFAULT_OPOR_JRNL_MEMO)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUCoAdd").value(
                    hasItem(DEFAULT_OPOR_U_CO_ADD)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUCodeInv").value(
                    hasItem(DEFAULT_OPOR_U_CODE_INV)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUCodeSerial").value(
                    hasItem(DEFAULT_OPOR_U_CODE_SERIAL)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUContractDate").value(
                    hasItem(DEFAULT_OPOR_U_CONTRACT_DATE)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUDeclarePd").value(
                    hasItem(DEFAULT_OPOR_U_DECLARE_PD)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUDocNum").value(
                    hasItem(DEFAULT_OPOR_U_DOC_NUM)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUInvCode").value(
                    hasItem(DEFAULT_OPOR_U_INV_CODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUInvCode2").value(
                    hasItem(DEFAULT_OPOR_U_INV_CODE_2)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUInvSerial").value(
                    hasItem(DEFAULT_OPOR_U_INV_SERIAL)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUPurNVGiao").value(
                    hasItem(DEFAULT_OPOR_U_PUR_NV_GIAO)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUpdateDate").value(
                    hasItem(DEFAULT_OPOR_UPDATE_DATE.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUserSign").value(
                    hasItem(DEFAULT_OPOR_USER_SIGN)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporTaxDate").value(
                    hasItem(DEFAULT_OPOR_TAX_DATE.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].oporCntctCode").value(
                    hasItem(DEFAULT_OPOR_CNTCT_CODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporNumAtCard").value(
                    hasItem(DEFAULT_OPOR_NUM_AT_CARD)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporSlpCode").value(
                    hasItem(DEFAULT_OPOR_SLP_CODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporOwnerCode").value(
                    hasItem(DEFAULT_OPOR_OWNER_CODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].oporVatSum").value(
                    hasItem(sameNumber(DEFAULT_OPOR_VAT_SUM))
                )
            )
            .andExpect(
                jsonPath("$.[*].oporDocTotal").value(
                    hasItem(sameNumber(DEFAULT_OPOR_DOC_TOTAL))
                )
            )
            .andExpect(
                jsonPath("$.[*].oporVatSumSy").value(
                    hasItem(sameNumber(DEFAULT_OPOR_VAT_SUM_SY))
                )
            )
            .andExpect(
                jsonPath("$.[*].oporUHt").value(hasItem(DEFAULT_OPOR_U_HT))
            )
            .andExpect(
                jsonPath("$.[*].oporUPayment").value(
                    hasItem(DEFAULT_OPOR_U_PAYMENT)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1BaseDocNum").value(
                    hasItem(DEFAULT_POR_1_BASE_DOC_NUM)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1BaseEntry").value(
                    hasItem(DEFAULT_POR_1_BASE_ENTRY)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1BaseLine").value(
                    hasItem(DEFAULT_POR_1_BASE_LINE)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1BaseRef").value(
                    hasItem(DEFAULT_POR_1_BASE_REF)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1Currency").value(
                    hasItem(DEFAULT_POR_1_CURRENCY)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1DiscPrcnt").value(
                    hasItem(DEFAULT_POR_1_DISC_PRCNT)
                )
            )
            .andExpect(
                jsonPath("$.[*].poDocEntry").value(
                    hasItem(DEFAULT_PO_DOC_ENTRY)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1Dscription").value(
                    hasItem(DEFAULT_POR_1_DSCRIPTION)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1ItemCode").value(
                    hasItem(DEFAULT_POR_1_ITEM_CODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1LineNum").value(
                    hasItem(DEFAULT_POR_1_LINE_NUM)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1LineStatus").value(
                    hasItem(DEFAULT_POR_1_LINE_STATUS)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1LineVendor").value(
                    hasItem(DEFAULT_POR_1_LINE_VENDOR)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1OpenSumSys").value(
                    hasItem(DEFAULT_POR_1_OPEN_SUM_SYS)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1Price").value(hasItem(DEFAULT_POR_1_PRICE))
            )
            .andExpect(
                jsonPath("$.[*].por1Quantity").value(
                    hasItem(DEFAULT_POR_1_QUANTITY)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1ShipDate").value(
                    hasItem(DEFAULT_POR_1_SHIP_DATE.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].por1TotalFrgn").value(
                    hasItem(DEFAULT_POR_1_TOTAL_FRGN)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1TotalSumsy").value(
                    hasItem(DEFAULT_POR_1_TOTAL_SUMSY)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1TrgetEntry").value(
                    hasItem(DEFAULT_POR_1_TRGET_ENTRY)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1UMcode").value(
                    hasItem(DEFAULT_POR_1_U_MCODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1USo").value(hasItem(DEFAULT_POR_1_U_SO))
            )
            .andExpect(
                jsonPath("$.[*].por1UTenkythuat").value(
                    hasItem(DEFAULT_POR_1_U_TENKYTHUAT)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1UnitMsr").value(
                    hasItem(DEFAULT_POR_1_UNIT_MSR)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1UOMCode").value(
                    hasItem(DEFAULT_POR_1_UOM_CODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1VatGroup").value(
                    hasItem(DEFAULT_POR_1_VAT_GROUP)
                )
            )
            .andExpect(
                jsonPath("$.[*].por1LineTotal").value(
                    hasItem(sameNumber(DEFAULT_POR_1_LINE_TOTAL))
                )
            )
            .andExpect(
                jsonPath("$.[*].por1VatPrcnt").value(
                    hasItem(sameNumber(DEFAULT_POR_1_VAT_PRCNT))
                )
            )
            .andExpect(
                jsonPath("$.[*].por1PriceAfVat").value(
                    hasItem(sameNumber(DEFAULT_POR_1_PRICE_AF_VAT))
                )
            )
            .andExpect(
                jsonPath("$.[*].por1WhsCode").value(
                    hasItem(DEFAULT_POR_1_WHS_CODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].prMapPo").value(hasItem(DEFAULT_PR_MAP_PO))
            );

        // Check, that the count call also returns 1
        restSapPoInfoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSapPoInfoShouldNotBeFound(String filter)
        throws Exception {
        restSapPoInfoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSapPoInfoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSapPoInfo() throws Exception {
        // Get the sapPoInfo
        restSapPoInfoMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSapPoInfo() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sapPoInfo
        SapPoInfo updatedSapPoInfo = sapPoInfoRepository
            .findById(sapPoInfo.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedSapPoInfo are not
        // directly saved in db
        em.detach(updatedSapPoInfo);
        updatedSapPoInfo
            .oporBranch(UPDATED_OPOR_BRANCH)
            .oporCanceled(UPDATED_OPOR_CANCELED)
            .oporCardCode(UPDATED_OPOR_CARD_CODE)
            .oporCardName(UPDATED_OPOR_CARD_NAME)
            .oporComments(UPDATED_OPOR_COMMENTS)
            .oporCreateDate(UPDATED_OPOR_CREATE_DATE)
            .oporDepartment(UPDATED_OPOR_DEPARTMENT)
            .oporDocDate(UPDATED_OPOR_DOC_DATE)
            .oporDocDueDate(UPDATED_OPOR_DOC_DUE_DATE)
            .oporDocEntry(UPDATED_OPOR_DOC_ENTRY)
            .oporDocNum(UPDATED_OPOR_DOC_NUM)
            .oporDocStatus(UPDATED_OPOR_DOC_STATUS)
            .oporInvntSttus(UPDATED_OPOR_INVNT_STTUS)
            .oporJrnlMemo(UPDATED_OPOR_JRNL_MEMO)
            .oporUCoAdd(UPDATED_OPOR_U_CO_ADD)
            .oporUCodeInv(UPDATED_OPOR_U_CODE_INV)
            .oporUCodeSerial(UPDATED_OPOR_U_CODE_SERIAL)
            .oporUContractDate(UPDATED_OPOR_U_CONTRACT_DATE)
            .oporUDeclarePd(UPDATED_OPOR_U_DECLARE_PD)
            .oporUDocNum(UPDATED_OPOR_U_DOC_NUM)
            .oporUInvCode(UPDATED_OPOR_U_INV_CODE)
            .oporUInvCode2(UPDATED_OPOR_U_INV_CODE_2)
            .oporUInvSerial(UPDATED_OPOR_U_INV_SERIAL)
            .oporUPurNVGiao(UPDATED_OPOR_U_PUR_NV_GIAO)
            .oporUpdateDate(UPDATED_OPOR_UPDATE_DATE)
            .oporUserSign(UPDATED_OPOR_USER_SIGN)
            .oporTaxDate(UPDATED_OPOR_TAX_DATE)
            .oporCntctCode(UPDATED_OPOR_CNTCT_CODE)
            .oporNumAtCard(UPDATED_OPOR_NUM_AT_CARD)
            .oporSlpCode(UPDATED_OPOR_SLP_CODE)
            .oporOwnerCode(UPDATED_OPOR_OWNER_CODE)
            .oporVatSum(UPDATED_OPOR_VAT_SUM)
            .oporDocTotal(UPDATED_OPOR_DOC_TOTAL)
            .oporVatSumSy(UPDATED_OPOR_VAT_SUM_SY)
            .oporUHt(UPDATED_OPOR_U_HT)
            .oporUPayment(UPDATED_OPOR_U_PAYMENT)
            .por1BaseDocNum(UPDATED_POR_1_BASE_DOC_NUM)
            .por1BaseEntry(UPDATED_POR_1_BASE_ENTRY)
            .por1BaseLine(UPDATED_POR_1_BASE_LINE)
            .por1BaseRef(UPDATED_POR_1_BASE_REF)
            .por1Currency(UPDATED_POR_1_CURRENCY)
            .por1DiscPrcnt(UPDATED_POR_1_DISC_PRCNT)
            .poDocEntry(UPDATED_PO_DOC_ENTRY)
            .por1Dscription(UPDATED_POR_1_DSCRIPTION)
            .por1ItemCode(UPDATED_POR_1_ITEM_CODE)
            .por1LineNum(UPDATED_POR_1_LINE_NUM)
            .por1LineStatus(UPDATED_POR_1_LINE_STATUS)
            .por1LineVendor(UPDATED_POR_1_LINE_VENDOR)
            .por1OpenSumSys(UPDATED_POR_1_OPEN_SUM_SYS)
            .por1Price(UPDATED_POR_1_PRICE)
            .por1Quantity(UPDATED_POR_1_QUANTITY)
            .por1ShipDate(UPDATED_POR_1_SHIP_DATE)
            .por1TotalFrgn(UPDATED_POR_1_TOTAL_FRGN)
            .por1TotalSumsy(UPDATED_POR_1_TOTAL_SUMSY)
            .por1TrgetEntry(UPDATED_POR_1_TRGET_ENTRY)
            .por1UMcode(UPDATED_POR_1_U_MCODE)
            .por1USo(UPDATED_POR_1_U_SO)
            .por1UTenkythuat(UPDATED_POR_1_U_TENKYTHUAT)
            .por1UnitMsr(UPDATED_POR_1_UNIT_MSR)
            .por1UOMCode(UPDATED_POR_1_UOM_CODE)
            .por1VatGroup(UPDATED_POR_1_VAT_GROUP)
            .por1LineTotal(UPDATED_POR_1_LINE_TOTAL)
            .por1VatPrcnt(UPDATED_POR_1_VAT_PRCNT)
            .por1PriceAfVat(UPDATED_POR_1_PRICE_AF_VAT)
            .por1WhsCode(UPDATED_POR_1_WHS_CODE)
            .prMapPo(UPDATED_PR_MAP_PO);

        restSapPoInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSapPoInfo.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedSapPoInfo))
            )
            .andExpect(status().isOk());

        // Validate the SapPoInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSapPoInfoToMatchAllProperties(updatedSapPoInfo);
    }

    @Test
    @Transactional
    void putNonExistingSapPoInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sapPoInfo.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSapPoInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sapPoInfo.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sapPoInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the SapPoInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSapPoInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sapPoInfo.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSapPoInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sapPoInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the SapPoInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSapPoInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sapPoInfo.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSapPoInfoMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sapPoInfo))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SapPoInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSapPoInfoWithPatch() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sapPoInfo using partial update
        SapPoInfo partialUpdatedSapPoInfo = new SapPoInfo();
        partialUpdatedSapPoInfo.setId(sapPoInfo.getId());

        partialUpdatedSapPoInfo
            .oporBranch(UPDATED_OPOR_BRANCH)
            .oporCardCode(UPDATED_OPOR_CARD_CODE)
            .oporCardName(UPDATED_OPOR_CARD_NAME)
            .oporComments(UPDATED_OPOR_COMMENTS)
            .oporCreateDate(UPDATED_OPOR_CREATE_DATE)
            .oporDocNum(UPDATED_OPOR_DOC_NUM)
            .oporDocStatus(UPDATED_OPOR_DOC_STATUS)
            .oporInvntSttus(UPDATED_OPOR_INVNT_STTUS)
            .oporJrnlMemo(UPDATED_OPOR_JRNL_MEMO)
            .oporUCodeSerial(UPDATED_OPOR_U_CODE_SERIAL)
            .oporUDocNum(UPDATED_OPOR_U_DOC_NUM)
            .oporUInvCode2(UPDATED_OPOR_U_INV_CODE_2)
            .oporUserSign(UPDATED_OPOR_USER_SIGN)
            .oporTaxDate(UPDATED_OPOR_TAX_DATE)
            .oporCntctCode(UPDATED_OPOR_CNTCT_CODE)
            .oporNumAtCard(UPDATED_OPOR_NUM_AT_CARD)
            .oporSlpCode(UPDATED_OPOR_SLP_CODE)
            .oporVatSum(UPDATED_OPOR_VAT_SUM)
            .oporDocTotal(UPDATED_OPOR_DOC_TOTAL)
            .oporVatSumSy(UPDATED_OPOR_VAT_SUM_SY)
            .oporUPayment(UPDATED_OPOR_U_PAYMENT)
            .por1BaseLine(UPDATED_POR_1_BASE_LINE)
            .por1BaseRef(UPDATED_POR_1_BASE_REF)
            .por1DiscPrcnt(UPDATED_POR_1_DISC_PRCNT)
            .por1Dscription(UPDATED_POR_1_DSCRIPTION)
            .por1ItemCode(UPDATED_POR_1_ITEM_CODE)
            .por1LineStatus(UPDATED_POR_1_LINE_STATUS)
            .por1OpenSumSys(UPDATED_POR_1_OPEN_SUM_SYS)
            .por1TotalFrgn(UPDATED_POR_1_TOTAL_FRGN)
            .por1TotalSumsy(UPDATED_POR_1_TOTAL_SUMSY)
            .por1TrgetEntry(UPDATED_POR_1_TRGET_ENTRY)
            .por1USo(UPDATED_POR_1_U_SO)
            .por1UTenkythuat(UPDATED_POR_1_U_TENKYTHUAT)
            .por1UnitMsr(UPDATED_POR_1_UNIT_MSR)
            .por1UOMCode(UPDATED_POR_1_UOM_CODE)
            .por1VatGroup(UPDATED_POR_1_VAT_GROUP)
            .por1LineTotal(UPDATED_POR_1_LINE_TOTAL)
            .por1VatPrcnt(UPDATED_POR_1_VAT_PRCNT)
            .por1PriceAfVat(UPDATED_POR_1_PRICE_AF_VAT);

        restSapPoInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSapPoInfo.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSapPoInfo))
            )
            .andExpect(status().isOk());

        // Validate the SapPoInfo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSapPoInfoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSapPoInfo, sapPoInfo),
            getPersistedSapPoInfo(sapPoInfo)
        );
    }

    @Test
    @Transactional
    void fullUpdateSapPoInfoWithPatch() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sapPoInfo using partial update
        SapPoInfo partialUpdatedSapPoInfo = new SapPoInfo();
        partialUpdatedSapPoInfo.setId(sapPoInfo.getId());

        partialUpdatedSapPoInfo
            .oporBranch(UPDATED_OPOR_BRANCH)
            .oporCanceled(UPDATED_OPOR_CANCELED)
            .oporCardCode(UPDATED_OPOR_CARD_CODE)
            .oporCardName(UPDATED_OPOR_CARD_NAME)
            .oporComments(UPDATED_OPOR_COMMENTS)
            .oporCreateDate(UPDATED_OPOR_CREATE_DATE)
            .oporDepartment(UPDATED_OPOR_DEPARTMENT)
            .oporDocDate(UPDATED_OPOR_DOC_DATE)
            .oporDocDueDate(UPDATED_OPOR_DOC_DUE_DATE)
            .oporDocEntry(UPDATED_OPOR_DOC_ENTRY)
            .oporDocNum(UPDATED_OPOR_DOC_NUM)
            .oporDocStatus(UPDATED_OPOR_DOC_STATUS)
            .oporInvntSttus(UPDATED_OPOR_INVNT_STTUS)
            .oporJrnlMemo(UPDATED_OPOR_JRNL_MEMO)
            .oporUCoAdd(UPDATED_OPOR_U_CO_ADD)
            .oporUCodeInv(UPDATED_OPOR_U_CODE_INV)
            .oporUCodeSerial(UPDATED_OPOR_U_CODE_SERIAL)
            .oporUContractDate(UPDATED_OPOR_U_CONTRACT_DATE)
            .oporUDeclarePd(UPDATED_OPOR_U_DECLARE_PD)
            .oporUDocNum(UPDATED_OPOR_U_DOC_NUM)
            .oporUInvCode(UPDATED_OPOR_U_INV_CODE)
            .oporUInvCode2(UPDATED_OPOR_U_INV_CODE_2)
            .oporUInvSerial(UPDATED_OPOR_U_INV_SERIAL)
            .oporUPurNVGiao(UPDATED_OPOR_U_PUR_NV_GIAO)
            .oporUpdateDate(UPDATED_OPOR_UPDATE_DATE)
            .oporUserSign(UPDATED_OPOR_USER_SIGN)
            .oporTaxDate(UPDATED_OPOR_TAX_DATE)
            .oporCntctCode(UPDATED_OPOR_CNTCT_CODE)
            .oporNumAtCard(UPDATED_OPOR_NUM_AT_CARD)
            .oporSlpCode(UPDATED_OPOR_SLP_CODE)
            .oporOwnerCode(UPDATED_OPOR_OWNER_CODE)
            .oporVatSum(UPDATED_OPOR_VAT_SUM)
            .oporDocTotal(UPDATED_OPOR_DOC_TOTAL)
            .oporVatSumSy(UPDATED_OPOR_VAT_SUM_SY)
            .oporUHt(UPDATED_OPOR_U_HT)
            .oporUPayment(UPDATED_OPOR_U_PAYMENT)
            .por1BaseDocNum(UPDATED_POR_1_BASE_DOC_NUM)
            .por1BaseEntry(UPDATED_POR_1_BASE_ENTRY)
            .por1BaseLine(UPDATED_POR_1_BASE_LINE)
            .por1BaseRef(UPDATED_POR_1_BASE_REF)
            .por1Currency(UPDATED_POR_1_CURRENCY)
            .por1DiscPrcnt(UPDATED_POR_1_DISC_PRCNT)
            .poDocEntry(UPDATED_PO_DOC_ENTRY)
            .por1Dscription(UPDATED_POR_1_DSCRIPTION)
            .por1ItemCode(UPDATED_POR_1_ITEM_CODE)
            .por1LineNum(UPDATED_POR_1_LINE_NUM)
            .por1LineStatus(UPDATED_POR_1_LINE_STATUS)
            .por1LineVendor(UPDATED_POR_1_LINE_VENDOR)
            .por1OpenSumSys(UPDATED_POR_1_OPEN_SUM_SYS)
            .por1Price(UPDATED_POR_1_PRICE)
            .por1Quantity(UPDATED_POR_1_QUANTITY)
            .por1ShipDate(UPDATED_POR_1_SHIP_DATE)
            .por1TotalFrgn(UPDATED_POR_1_TOTAL_FRGN)
            .por1TotalSumsy(UPDATED_POR_1_TOTAL_SUMSY)
            .por1TrgetEntry(UPDATED_POR_1_TRGET_ENTRY)
            .por1UMcode(UPDATED_POR_1_U_MCODE)
            .por1USo(UPDATED_POR_1_U_SO)
            .por1UTenkythuat(UPDATED_POR_1_U_TENKYTHUAT)
            .por1UnitMsr(UPDATED_POR_1_UNIT_MSR)
            .por1UOMCode(UPDATED_POR_1_UOM_CODE)
            .por1VatGroup(UPDATED_POR_1_VAT_GROUP)
            .por1LineTotal(UPDATED_POR_1_LINE_TOTAL)
            .por1VatPrcnt(UPDATED_POR_1_VAT_PRCNT)
            .por1PriceAfVat(UPDATED_POR_1_PRICE_AF_VAT)
            .por1WhsCode(UPDATED_POR_1_WHS_CODE)
            .prMapPo(UPDATED_PR_MAP_PO);

        restSapPoInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSapPoInfo.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSapPoInfo))
            )
            .andExpect(status().isOk());

        // Validate the SapPoInfo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSapPoInfoUpdatableFieldsEquals(
            partialUpdatedSapPoInfo,
            getPersistedSapPoInfo(partialUpdatedSapPoInfo)
        );
    }

    @Test
    @Transactional
    void patchNonExistingSapPoInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sapPoInfo.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSapPoInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sapPoInfo.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sapPoInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the SapPoInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSapPoInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sapPoInfo.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSapPoInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sapPoInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the SapPoInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSapPoInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sapPoInfo.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSapPoInfoMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sapPoInfo))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SapPoInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSapPoInfo() throws Exception {
        // Initialize the database
        insertedSapPoInfo = sapPoInfoRepository.saveAndFlush(sapPoInfo);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the sapPoInfo
        restSapPoInfoMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, sapPoInfo.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return sapPoInfoRepository.count();
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

    protected SapPoInfo getPersistedSapPoInfo(SapPoInfo sapPoInfo) {
        return sapPoInfoRepository.findById(sapPoInfo.getId()).orElseThrow();
    }

    protected void assertPersistedSapPoInfoToMatchAllProperties(
        SapPoInfo expectedSapPoInfo
    ) {
        assertSapPoInfoAllPropertiesEquals(
            expectedSapPoInfo,
            getPersistedSapPoInfo(expectedSapPoInfo)
        );
    }

    protected void assertPersistedSapPoInfoToMatchUpdatableProperties(
        SapPoInfo expectedSapPoInfo
    ) {
        assertSapPoInfoAllUpdatablePropertiesEquals(
            expectedSapPoInfo,
            getPersistedSapPoInfo(expectedSapPoInfo)
        );
    }
}
