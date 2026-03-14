package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.VendorTemDetailAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.PoDetail;
import com.mycompany.myapp.domain.VendorTemDetail;
import com.mycompany.myapp.repository.partner5.VendorTemDetailRepository;
import com.mycompany.myapp.service.dto.VendorTemDetailDTO;
import com.mycompany.myapp.service.mapper.VendorTemDetailMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
 * Integration tests for the {@link VendorTemDetailResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VendorTemDetailResourceIT {

    private static final String DEFAULT_REEL_ID = "AAAAAAAAAA";
    private static final String UPDATED_REEL_ID = "BBBBBBBBBB";

    private static final String DEFAULT_PART_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PART_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_VENDOR = "AAAAAAAAAA";
    private static final String UPDATED_VENDOR = "BBBBBBBBBB";

    private static final String DEFAULT_LOT = "AAAAAAAAAA";
    private static final String UPDATED_LOT = "BBBBBBBBBB";

    private static final String DEFAULT_USER_DATA_1 = "AAAAAAAAAA";
    private static final String UPDATED_USER_DATA_1 = "BBBBBBBBBB";

    private static final String DEFAULT_USER_DATA_2 = "AAAAAAAAAA";
    private static final String UPDATED_USER_DATA_2 = "BBBBBBBBBB";

    private static final String DEFAULT_USER_DATA_3 = "AAAAAAAAAA";
    private static final String UPDATED_USER_DATA_3 = "BBBBBBBBBB";

    private static final String DEFAULT_USER_DATA_4 = "AAAAAAAAAA";
    private static final String UPDATED_USER_DATA_4 = "BBBBBBBBBB";

    private static final String DEFAULT_USER_DATA_5 = "AAAAAAAAAA";
    private static final String UPDATED_USER_DATA_5 = "BBBBBBBBBB";

    private static final Integer DEFAULT_INITIAL_QUANTITY = 1;
    private static final Integer UPDATED_INITIAL_QUANTITY = 2;
    private static final Integer SMALLER_INITIAL_QUANTITY = 1 - 1;

    private static final String DEFAULT_MSD_LEVEL = "AAAAAAAAAA";
    private static final String UPDATED_MSD_LEVEL = "BBBBBBBBBB";

    private static final String DEFAULT_MSD_INITIAL_FLOOR_TIME = "AAAAAAAAAA";
    private static final String UPDATED_MSD_INITIAL_FLOOR_TIME = "BBBBBBBBBB";

    private static final String DEFAULT_MSD_BAG_SEAL_DATE = "AAAAAAAAAA";
    private static final String UPDATED_MSD_BAG_SEAL_DATE = "BBBBBBBBBB";

    private static final String DEFAULT_MARKET_USAGE = "AAAAAAAAAA";
    private static final String UPDATED_MARKET_USAGE = "BBBBBBBBBB";

    private static final Integer DEFAULT_QUANTITY_OVERRIDE = 1;
    private static final Integer UPDATED_QUANTITY_OVERRIDE = 2;
    private static final Integer SMALLER_QUANTITY_OVERRIDE = 1 - 1;

    private static final String DEFAULT_SHELF_TIME = "AAAAAAAAAA";
    private static final String UPDATED_SHELF_TIME = "BBBBBBBBBB";

    private static final String DEFAULT_SP_MATERIAL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SP_MATERIAL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_WARNING_LIMIT = "AAAAAAAAAA";
    private static final String UPDATED_WARNING_LIMIT = "BBBBBBBBBB";

    private static final String DEFAULT_MAXIMUM_LIMIT = "AAAAAAAAAA";
    private static final String UPDATED_MAXIMUM_LIMIT = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTS = "BBBBBBBBBB";

    private static final String DEFAULT_WARMUP_TIME = "AAAAAAAAAA";
    private static final String UPDATED_WARMUP_TIME = "BBBBBBBBBB";

    private static final String DEFAULT_STORAGE_UNIT = "AAAAAAAAAA";
    private static final String UPDATED_STORAGE_UNIT = "BBBBBBBBBB";

    private static final String DEFAULT_SUB_STORAGE_UNIT = "AAAAAAAAAA";
    private static final String UPDATED_SUB_STORAGE_UNIT = "BBBBBBBBBB";

    private static final String DEFAULT_LOCATION_OVERRIDE = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION_OVERRIDE = "BBBBBBBBBB";

    private static final String DEFAULT_EXPIRATION_DATE = "AAAAAAAAAA";
    private static final String UPDATED_EXPIRATION_DATE = "BBBBBBBBBB";

    private static final String DEFAULT_MANUFACTURING_DATE = "AAAAAAAAAA";
    private static final String UPDATED_MANUFACTURING_DATE = "BBBBBBBBBB";

    private static final String DEFAULT_PART_CLASS = "AAAAAAAAAA";
    private static final String UPDATED_PART_CLASS = "BBBBBBBBBB";

    private static final String DEFAULT_SAP_CODE = "AAAAAAAAAA";
    private static final String UPDATED_SAP_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_VENDOR_QR_CODE = "AAAAAAAAAA";
    private static final String UPDATED_VENDOR_QR_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(
        ZoneId.systemDefault()
    ).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_AT =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String DEFAULT_UPDATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_UPDATED_BY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_UPDATED_AT =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_AT = ZonedDateTime.now(
        ZoneId.systemDefault()
    ).withNano(0);
    private static final ZonedDateTime SMALLER_UPDATED_AT =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/vendor-tem-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VendorTemDetailRepository vendorTemDetailRepository;

    @Autowired
    private VendorTemDetailMapper vendorTemDetailMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVendorTemDetailMockMvc;

    private VendorTemDetail vendorTemDetail;

    private VendorTemDetail insertedVendorTemDetail;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VendorTemDetail createEntity() {
        return new VendorTemDetail()
            .reelId(DEFAULT_REEL_ID)
            .partNumber(DEFAULT_PART_NUMBER)
            .vendor(DEFAULT_VENDOR)
            .lot(DEFAULT_LOT)
            .userData1(DEFAULT_USER_DATA_1)
            .userData2(DEFAULT_USER_DATA_2)
            .userData3(DEFAULT_USER_DATA_3)
            .userData4(DEFAULT_USER_DATA_4)
            .userData5(DEFAULT_USER_DATA_5)
            .initialQuantity(DEFAULT_INITIAL_QUANTITY)
            .msdLevel(DEFAULT_MSD_LEVEL)
            .msdInitialFloorTime(DEFAULT_MSD_INITIAL_FLOOR_TIME)
            .msdBagSealDate(DEFAULT_MSD_BAG_SEAL_DATE)
            .marketUsage(DEFAULT_MARKET_USAGE)
            .quantityOverride(DEFAULT_QUANTITY_OVERRIDE)
            .shelfTime(DEFAULT_SHELF_TIME)
            .spMaterialName(DEFAULT_SP_MATERIAL_NAME)
            .warningLimit(DEFAULT_WARNING_LIMIT)
            .maximumLimit(DEFAULT_MAXIMUM_LIMIT)
            .comments(DEFAULT_COMMENTS)
            .warmupTime(DEFAULT_WARMUP_TIME)
            .storageUnit(DEFAULT_STORAGE_UNIT)
            .subStorageUnit(DEFAULT_SUB_STORAGE_UNIT)
            .locationOverride(DEFAULT_LOCATION_OVERRIDE)
            .expirationDate(DEFAULT_EXPIRATION_DATE)
            .manufacturingDate(DEFAULT_MANUFACTURING_DATE)
            .partClass(DEFAULT_PART_CLASS)
            .sapCode(DEFAULT_SAP_CODE)
            .vendorQrCode(DEFAULT_VENDOR_QR_CODE)
            .status(DEFAULT_STATUS)
            .createdBy(DEFAULT_CREATED_BY)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedBy(DEFAULT_UPDATED_BY)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VendorTemDetail createUpdatedEntity() {
        return new VendorTemDetail()
            .reelId(UPDATED_REEL_ID)
            .partNumber(UPDATED_PART_NUMBER)
            .vendor(UPDATED_VENDOR)
            .lot(UPDATED_LOT)
            .userData1(UPDATED_USER_DATA_1)
            .userData2(UPDATED_USER_DATA_2)
            .userData3(UPDATED_USER_DATA_3)
            .userData4(UPDATED_USER_DATA_4)
            .userData5(UPDATED_USER_DATA_5)
            .initialQuantity(UPDATED_INITIAL_QUANTITY)
            .msdLevel(UPDATED_MSD_LEVEL)
            .msdInitialFloorTime(UPDATED_MSD_INITIAL_FLOOR_TIME)
            .msdBagSealDate(UPDATED_MSD_BAG_SEAL_DATE)
            .marketUsage(UPDATED_MARKET_USAGE)
            .quantityOverride(UPDATED_QUANTITY_OVERRIDE)
            .shelfTime(UPDATED_SHELF_TIME)
            .spMaterialName(UPDATED_SP_MATERIAL_NAME)
            .warningLimit(UPDATED_WARNING_LIMIT)
            .maximumLimit(UPDATED_MAXIMUM_LIMIT)
            .comments(UPDATED_COMMENTS)
            .warmupTime(UPDATED_WARMUP_TIME)
            .storageUnit(UPDATED_STORAGE_UNIT)
            .subStorageUnit(UPDATED_SUB_STORAGE_UNIT)
            .locationOverride(UPDATED_LOCATION_OVERRIDE)
            .expirationDate(UPDATED_EXPIRATION_DATE)
            .manufacturingDate(UPDATED_MANUFACTURING_DATE)
            .partClass(UPDATED_PART_CLASS)
            .sapCode(UPDATED_SAP_CODE)
            .vendorQrCode(UPDATED_VENDOR_QR_CODE)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        vendorTemDetail = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedVendorTemDetail != null) {
            vendorTemDetailRepository.delete(insertedVendorTemDetail);
            insertedVendorTemDetail = null;
        }
    }

    @Test
    @Transactional
    void createVendorTemDetail() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the VendorTemDetail
        VendorTemDetailDTO vendorTemDetailDTO = vendorTemDetailMapper.toDto(
            vendorTemDetail
        );
        var returnedVendorTemDetailDTO = om.readValue(
            restVendorTemDetailMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(vendorTemDetailDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            VendorTemDetailDTO.class
        );

        // Validate the VendorTemDetail in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedVendorTemDetail = vendorTemDetailMapper.toEntity(
            returnedVendorTemDetailDTO
        );
        assertVendorTemDetailUpdatableFieldsEquals(
            returnedVendorTemDetail,
            getPersistedVendorTemDetail(returnedVendorTemDetail)
        );

        insertedVendorTemDetail = returnedVendorTemDetail;
    }

    @Test
    @Transactional
    void createVendorTemDetailWithExistingId() throws Exception {
        // Create the VendorTemDetail with an existing ID
        vendorTemDetail.setId(1L);
        VendorTemDetailDTO vendorTemDetailDTO = vendorTemDetailMapper.toDto(
            vendorTemDetail
        );

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVendorTemDetailMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(vendorTemDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VendorTemDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllVendorTemDetails() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList
        restVendorTemDetailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(vendorTemDetail.getId().intValue())
                )
            )
            .andExpect(jsonPath("$.[*].reelId").value(hasItem(DEFAULT_REEL_ID)))
            .andExpect(
                jsonPath("$.[*].partNumber").value(hasItem(DEFAULT_PART_NUMBER))
            )
            .andExpect(jsonPath("$.[*].vendor").value(hasItem(DEFAULT_VENDOR)))
            .andExpect(jsonPath("$.[*].lot").value(hasItem(DEFAULT_LOT)))
            .andExpect(
                jsonPath("$.[*].userData1").value(hasItem(DEFAULT_USER_DATA_1))
            )
            .andExpect(
                jsonPath("$.[*].userData2").value(hasItem(DEFAULT_USER_DATA_2))
            )
            .andExpect(
                jsonPath("$.[*].userData3").value(hasItem(DEFAULT_USER_DATA_3))
            )
            .andExpect(
                jsonPath("$.[*].userData4").value(hasItem(DEFAULT_USER_DATA_4))
            )
            .andExpect(
                jsonPath("$.[*].userData5").value(hasItem(DEFAULT_USER_DATA_5))
            )
            .andExpect(
                jsonPath("$.[*].initialQuantity").value(
                    hasItem(DEFAULT_INITIAL_QUANTITY)
                )
            )
            .andExpect(
                jsonPath("$.[*].msdLevel").value(hasItem(DEFAULT_MSD_LEVEL))
            )
            .andExpect(
                jsonPath("$.[*].msdInitialFloorTime").value(
                    hasItem(DEFAULT_MSD_INITIAL_FLOOR_TIME)
                )
            )
            .andExpect(
                jsonPath("$.[*].msdBagSealDate").value(
                    hasItem(DEFAULT_MSD_BAG_SEAL_DATE)
                )
            )
            .andExpect(
                jsonPath("$.[*].marketUsage").value(
                    hasItem(DEFAULT_MARKET_USAGE)
                )
            )
            .andExpect(
                jsonPath("$.[*].quantityOverride").value(
                    hasItem(DEFAULT_QUANTITY_OVERRIDE)
                )
            )
            .andExpect(
                jsonPath("$.[*].shelfTime").value(hasItem(DEFAULT_SHELF_TIME))
            )
            .andExpect(
                jsonPath("$.[*].spMaterialName").value(
                    hasItem(DEFAULT_SP_MATERIAL_NAME)
                )
            )
            .andExpect(
                jsonPath("$.[*].warningLimit").value(
                    hasItem(DEFAULT_WARNING_LIMIT)
                )
            )
            .andExpect(
                jsonPath("$.[*].maximumLimit").value(
                    hasItem(DEFAULT_MAXIMUM_LIMIT)
                )
            )
            .andExpect(
                jsonPath("$.[*].comments").value(hasItem(DEFAULT_COMMENTS))
            )
            .andExpect(
                jsonPath("$.[*].warmupTime").value(hasItem(DEFAULT_WARMUP_TIME))
            )
            .andExpect(
                jsonPath("$.[*].storageUnit").value(
                    hasItem(DEFAULT_STORAGE_UNIT)
                )
            )
            .andExpect(
                jsonPath("$.[*].subStorageUnit").value(
                    hasItem(DEFAULT_SUB_STORAGE_UNIT)
                )
            )
            .andExpect(
                jsonPath("$.[*].locationOverride").value(
                    hasItem(DEFAULT_LOCATION_OVERRIDE)
                )
            )
            .andExpect(
                jsonPath("$.[*].expirationDate").value(
                    hasItem(DEFAULT_EXPIRATION_DATE)
                )
            )
            .andExpect(
                jsonPath("$.[*].manufacturingDate").value(
                    hasItem(DEFAULT_MANUFACTURING_DATE)
                )
            )
            .andExpect(
                jsonPath("$.[*].partClass").value(hasItem(DEFAULT_PART_CLASS))
            )
            .andExpect(
                jsonPath("$.[*].sapCode").value(hasItem(DEFAULT_SAP_CODE))
            )
            .andExpect(
                jsonPath("$.[*].vendorQrCode").value(
                    hasItem(DEFAULT_VENDOR_QR_CODE)
                )
            )
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(
                jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY))
            )
            .andExpect(
                jsonPath("$.[*].createdAt").value(
                    hasItem(sameInstant(DEFAULT_CREATED_AT))
                )
            )
            .andExpect(
                jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY))
            )
            .andExpect(
                jsonPath("$.[*].updatedAt").value(
                    hasItem(sameInstant(DEFAULT_UPDATED_AT))
                )
            );
    }

    @Test
    @Transactional
    void getVendorTemDetail() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get the vendorTemDetail
        restVendorTemDetailMockMvc
            .perform(get(ENTITY_API_URL_ID, vendorTemDetail.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.id").value(vendorTemDetail.getId().intValue())
            )
            .andExpect(jsonPath("$.reelId").value(DEFAULT_REEL_ID))
            .andExpect(jsonPath("$.partNumber").value(DEFAULT_PART_NUMBER))
            .andExpect(jsonPath("$.vendor").value(DEFAULT_VENDOR))
            .andExpect(jsonPath("$.lot").value(DEFAULT_LOT))
            .andExpect(jsonPath("$.userData1").value(DEFAULT_USER_DATA_1))
            .andExpect(jsonPath("$.userData2").value(DEFAULT_USER_DATA_2))
            .andExpect(jsonPath("$.userData3").value(DEFAULT_USER_DATA_3))
            .andExpect(jsonPath("$.userData4").value(DEFAULT_USER_DATA_4))
            .andExpect(jsonPath("$.userData5").value(DEFAULT_USER_DATA_5))
            .andExpect(
                jsonPath("$.initialQuantity").value(DEFAULT_INITIAL_QUANTITY)
            )
            .andExpect(jsonPath("$.msdLevel").value(DEFAULT_MSD_LEVEL))
            .andExpect(
                jsonPath("$.msdInitialFloorTime").value(
                    DEFAULT_MSD_INITIAL_FLOOR_TIME
                )
            )
            .andExpect(
                jsonPath("$.msdBagSealDate").value(DEFAULT_MSD_BAG_SEAL_DATE)
            )
            .andExpect(jsonPath("$.marketUsage").value(DEFAULT_MARKET_USAGE))
            .andExpect(
                jsonPath("$.quantityOverride").value(DEFAULT_QUANTITY_OVERRIDE)
            )
            .andExpect(jsonPath("$.shelfTime").value(DEFAULT_SHELF_TIME))
            .andExpect(
                jsonPath("$.spMaterialName").value(DEFAULT_SP_MATERIAL_NAME)
            )
            .andExpect(jsonPath("$.warningLimit").value(DEFAULT_WARNING_LIMIT))
            .andExpect(jsonPath("$.maximumLimit").value(DEFAULT_MAXIMUM_LIMIT))
            .andExpect(jsonPath("$.comments").value(DEFAULT_COMMENTS))
            .andExpect(jsonPath("$.warmupTime").value(DEFAULT_WARMUP_TIME))
            .andExpect(jsonPath("$.storageUnit").value(DEFAULT_STORAGE_UNIT))
            .andExpect(
                jsonPath("$.subStorageUnit").value(DEFAULT_SUB_STORAGE_UNIT)
            )
            .andExpect(
                jsonPath("$.locationOverride").value(DEFAULT_LOCATION_OVERRIDE)
            )
            .andExpect(
                jsonPath("$.expirationDate").value(DEFAULT_EXPIRATION_DATE)
            )
            .andExpect(
                jsonPath("$.manufacturingDate").value(
                    DEFAULT_MANUFACTURING_DATE
                )
            )
            .andExpect(jsonPath("$.partClass").value(DEFAULT_PART_CLASS))
            .andExpect(jsonPath("$.sapCode").value(DEFAULT_SAP_CODE))
            .andExpect(jsonPath("$.vendorQrCode").value(DEFAULT_VENDOR_QR_CODE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(
                jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT))
            )
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
            .andExpect(
                jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT))
            );
    }

    @Test
    @Transactional
    void getVendorTemDetailsByIdFiltering() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        Long id = vendorTemDetail.getId();

        defaultVendorTemDetailFiltering(
            "id.equals=" + id,
            "id.notEquals=" + id
        );

        defaultVendorTemDetailFiltering(
            "id.greaterThanOrEqual=" + id,
            "id.greaterThan=" + id
        );

        defaultVendorTemDetailFiltering(
            "id.lessThanOrEqual=" + id,
            "id.lessThan=" + id
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByReelIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where reelId equals to
        defaultVendorTemDetailFiltering(
            "reelId.equals=" + DEFAULT_REEL_ID,
            "reelId.equals=" + UPDATED_REEL_ID
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByReelIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where reelId in
        defaultVendorTemDetailFiltering(
            "reelId.in=" + DEFAULT_REEL_ID + "," + UPDATED_REEL_ID,
            "reelId.in=" + UPDATED_REEL_ID
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByReelIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where reelId is not null
        defaultVendorTemDetailFiltering(
            "reelId.specified=true",
            "reelId.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByReelIdContainsSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where reelId contains
        defaultVendorTemDetailFiltering(
            "reelId.contains=" + DEFAULT_REEL_ID,
            "reelId.contains=" + UPDATED_REEL_ID
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByReelIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where reelId does not contain
        defaultVendorTemDetailFiltering(
            "reelId.doesNotContain=" + UPDATED_REEL_ID,
            "reelId.doesNotContain=" + DEFAULT_REEL_ID
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByPartNumberIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where partNumber equals to
        defaultVendorTemDetailFiltering(
            "partNumber.equals=" + DEFAULT_PART_NUMBER,
            "partNumber.equals=" + UPDATED_PART_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByPartNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where partNumber in
        defaultVendorTemDetailFiltering(
            "partNumber.in=" + DEFAULT_PART_NUMBER + "," + UPDATED_PART_NUMBER,
            "partNumber.in=" + UPDATED_PART_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByPartNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where partNumber is not null
        defaultVendorTemDetailFiltering(
            "partNumber.specified=true",
            "partNumber.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByPartNumberContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where partNumber contains
        defaultVendorTemDetailFiltering(
            "partNumber.contains=" + DEFAULT_PART_NUMBER,
            "partNumber.contains=" + UPDATED_PART_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByPartNumberNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where partNumber does not contain
        defaultVendorTemDetailFiltering(
            "partNumber.doesNotContain=" + UPDATED_PART_NUMBER,
            "partNumber.doesNotContain=" + DEFAULT_PART_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByVendorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where vendor equals to
        defaultVendorTemDetailFiltering(
            "vendor.equals=" + DEFAULT_VENDOR,
            "vendor.equals=" + UPDATED_VENDOR
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByVendorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where vendor in
        defaultVendorTemDetailFiltering(
            "vendor.in=" + DEFAULT_VENDOR + "," + UPDATED_VENDOR,
            "vendor.in=" + UPDATED_VENDOR
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByVendorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where vendor is not null
        defaultVendorTemDetailFiltering(
            "vendor.specified=true",
            "vendor.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByVendorContainsSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where vendor contains
        defaultVendorTemDetailFiltering(
            "vendor.contains=" + DEFAULT_VENDOR,
            "vendor.contains=" + UPDATED_VENDOR
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByVendorNotContainsSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where vendor does not contain
        defaultVendorTemDetailFiltering(
            "vendor.doesNotContain=" + UPDATED_VENDOR,
            "vendor.doesNotContain=" + DEFAULT_VENDOR
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByLotIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where lot equals to
        defaultVendorTemDetailFiltering(
            "lot.equals=" + DEFAULT_LOT,
            "lot.equals=" + UPDATED_LOT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByLotIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where lot in
        defaultVendorTemDetailFiltering(
            "lot.in=" + DEFAULT_LOT + "," + UPDATED_LOT,
            "lot.in=" + UPDATED_LOT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByLotIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where lot is not null
        defaultVendorTemDetailFiltering(
            "lot.specified=true",
            "lot.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByLotContainsSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where lot contains
        defaultVendorTemDetailFiltering(
            "lot.contains=" + DEFAULT_LOT,
            "lot.contains=" + UPDATED_LOT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByLotNotContainsSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where lot does not contain
        defaultVendorTemDetailFiltering(
            "lot.doesNotContain=" + UPDATED_LOT,
            "lot.doesNotContain=" + DEFAULT_LOT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData1IsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData1 equals to
        defaultVendorTemDetailFiltering(
            "userData1.equals=" + DEFAULT_USER_DATA_1,
            "userData1.equals=" + UPDATED_USER_DATA_1
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData1IsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData1 in
        defaultVendorTemDetailFiltering(
            "userData1.in=" + DEFAULT_USER_DATA_1 + "," + UPDATED_USER_DATA_1,
            "userData1.in=" + UPDATED_USER_DATA_1
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData1IsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData1 is not null
        defaultVendorTemDetailFiltering(
            "userData1.specified=true",
            "userData1.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData1ContainsSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData1 contains
        defaultVendorTemDetailFiltering(
            "userData1.contains=" + DEFAULT_USER_DATA_1,
            "userData1.contains=" + UPDATED_USER_DATA_1
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData1NotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData1 does not contain
        defaultVendorTemDetailFiltering(
            "userData1.doesNotContain=" + UPDATED_USER_DATA_1,
            "userData1.doesNotContain=" + DEFAULT_USER_DATA_1
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData2IsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData2 equals to
        defaultVendorTemDetailFiltering(
            "userData2.equals=" + DEFAULT_USER_DATA_2,
            "userData2.equals=" + UPDATED_USER_DATA_2
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData2IsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData2 in
        defaultVendorTemDetailFiltering(
            "userData2.in=" + DEFAULT_USER_DATA_2 + "," + UPDATED_USER_DATA_2,
            "userData2.in=" + UPDATED_USER_DATA_2
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData2IsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData2 is not null
        defaultVendorTemDetailFiltering(
            "userData2.specified=true",
            "userData2.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData2ContainsSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData2 contains
        defaultVendorTemDetailFiltering(
            "userData2.contains=" + DEFAULT_USER_DATA_2,
            "userData2.contains=" + UPDATED_USER_DATA_2
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData2NotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData2 does not contain
        defaultVendorTemDetailFiltering(
            "userData2.doesNotContain=" + UPDATED_USER_DATA_2,
            "userData2.doesNotContain=" + DEFAULT_USER_DATA_2
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData3IsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData3 equals to
        defaultVendorTemDetailFiltering(
            "userData3.equals=" + DEFAULT_USER_DATA_3,
            "userData3.equals=" + UPDATED_USER_DATA_3
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData3IsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData3 in
        defaultVendorTemDetailFiltering(
            "userData3.in=" + DEFAULT_USER_DATA_3 + "," + UPDATED_USER_DATA_3,
            "userData3.in=" + UPDATED_USER_DATA_3
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData3IsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData3 is not null
        defaultVendorTemDetailFiltering(
            "userData3.specified=true",
            "userData3.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData3ContainsSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData3 contains
        defaultVendorTemDetailFiltering(
            "userData3.contains=" + DEFAULT_USER_DATA_3,
            "userData3.contains=" + UPDATED_USER_DATA_3
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData3NotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData3 does not contain
        defaultVendorTemDetailFiltering(
            "userData3.doesNotContain=" + UPDATED_USER_DATA_3,
            "userData3.doesNotContain=" + DEFAULT_USER_DATA_3
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData4IsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData4 equals to
        defaultVendorTemDetailFiltering(
            "userData4.equals=" + DEFAULT_USER_DATA_4,
            "userData4.equals=" + UPDATED_USER_DATA_4
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData4IsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData4 in
        defaultVendorTemDetailFiltering(
            "userData4.in=" + DEFAULT_USER_DATA_4 + "," + UPDATED_USER_DATA_4,
            "userData4.in=" + UPDATED_USER_DATA_4
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData4IsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData4 is not null
        defaultVendorTemDetailFiltering(
            "userData4.specified=true",
            "userData4.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData4ContainsSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData4 contains
        defaultVendorTemDetailFiltering(
            "userData4.contains=" + DEFAULT_USER_DATA_4,
            "userData4.contains=" + UPDATED_USER_DATA_4
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData4NotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData4 does not contain
        defaultVendorTemDetailFiltering(
            "userData4.doesNotContain=" + UPDATED_USER_DATA_4,
            "userData4.doesNotContain=" + DEFAULT_USER_DATA_4
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData5IsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData5 equals to
        defaultVendorTemDetailFiltering(
            "userData5.equals=" + DEFAULT_USER_DATA_5,
            "userData5.equals=" + UPDATED_USER_DATA_5
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData5IsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData5 in
        defaultVendorTemDetailFiltering(
            "userData5.in=" + DEFAULT_USER_DATA_5 + "," + UPDATED_USER_DATA_5,
            "userData5.in=" + UPDATED_USER_DATA_5
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData5IsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData5 is not null
        defaultVendorTemDetailFiltering(
            "userData5.specified=true",
            "userData5.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData5ContainsSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData5 contains
        defaultVendorTemDetailFiltering(
            "userData5.contains=" + DEFAULT_USER_DATA_5,
            "userData5.contains=" + UPDATED_USER_DATA_5
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUserData5NotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where userData5 does not contain
        defaultVendorTemDetailFiltering(
            "userData5.doesNotContain=" + UPDATED_USER_DATA_5,
            "userData5.doesNotContain=" + DEFAULT_USER_DATA_5
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByInitialQuantityIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where initialQuantity equals to
        defaultVendorTemDetailFiltering(
            "initialQuantity.equals=" + DEFAULT_INITIAL_QUANTITY,
            "initialQuantity.equals=" + UPDATED_INITIAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByInitialQuantityIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where initialQuantity in
        defaultVendorTemDetailFiltering(
            "initialQuantity.in=" +
            DEFAULT_INITIAL_QUANTITY +
            "," +
            UPDATED_INITIAL_QUANTITY,
            "initialQuantity.in=" + UPDATED_INITIAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByInitialQuantityIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where initialQuantity is not null
        defaultVendorTemDetailFiltering(
            "initialQuantity.specified=true",
            "initialQuantity.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByInitialQuantityIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where initialQuantity is greater than or equal to
        defaultVendorTemDetailFiltering(
            "initialQuantity.greaterThanOrEqual=" + DEFAULT_INITIAL_QUANTITY,
            "initialQuantity.greaterThanOrEqual=" + UPDATED_INITIAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByInitialQuantityIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where initialQuantity is less than or equal to
        defaultVendorTemDetailFiltering(
            "initialQuantity.lessThanOrEqual=" + DEFAULT_INITIAL_QUANTITY,
            "initialQuantity.lessThanOrEqual=" + SMALLER_INITIAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByInitialQuantityIsLessThanSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where initialQuantity is less than
        defaultVendorTemDetailFiltering(
            "initialQuantity.lessThan=" + UPDATED_INITIAL_QUANTITY,
            "initialQuantity.lessThan=" + DEFAULT_INITIAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByInitialQuantityIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where initialQuantity is greater than
        defaultVendorTemDetailFiltering(
            "initialQuantity.greaterThan=" + SMALLER_INITIAL_QUANTITY,
            "initialQuantity.greaterThan=" + DEFAULT_INITIAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMsdLevelIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where msdLevel equals to
        defaultVendorTemDetailFiltering(
            "msdLevel.equals=" + DEFAULT_MSD_LEVEL,
            "msdLevel.equals=" + UPDATED_MSD_LEVEL
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMsdLevelIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where msdLevel in
        defaultVendorTemDetailFiltering(
            "msdLevel.in=" + DEFAULT_MSD_LEVEL + "," + UPDATED_MSD_LEVEL,
            "msdLevel.in=" + UPDATED_MSD_LEVEL
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMsdLevelIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where msdLevel is not null
        defaultVendorTemDetailFiltering(
            "msdLevel.specified=true",
            "msdLevel.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMsdLevelContainsSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where msdLevel contains
        defaultVendorTemDetailFiltering(
            "msdLevel.contains=" + DEFAULT_MSD_LEVEL,
            "msdLevel.contains=" + UPDATED_MSD_LEVEL
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMsdLevelNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where msdLevel does not contain
        defaultVendorTemDetailFiltering(
            "msdLevel.doesNotContain=" + UPDATED_MSD_LEVEL,
            "msdLevel.doesNotContain=" + DEFAULT_MSD_LEVEL
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMsdInitialFloorTimeIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where msdInitialFloorTime equals to
        defaultVendorTemDetailFiltering(
            "msdInitialFloorTime.equals=" + DEFAULT_MSD_INITIAL_FLOOR_TIME,
            "msdInitialFloorTime.equals=" + UPDATED_MSD_INITIAL_FLOOR_TIME
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMsdInitialFloorTimeIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where msdInitialFloorTime in
        defaultVendorTemDetailFiltering(
            "msdInitialFloorTime.in=" +
            DEFAULT_MSD_INITIAL_FLOOR_TIME +
            "," +
            UPDATED_MSD_INITIAL_FLOOR_TIME,
            "msdInitialFloorTime.in=" + UPDATED_MSD_INITIAL_FLOOR_TIME
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMsdInitialFloorTimeIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where msdInitialFloorTime is not null
        defaultVendorTemDetailFiltering(
            "msdInitialFloorTime.specified=true",
            "msdInitialFloorTime.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMsdInitialFloorTimeContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where msdInitialFloorTime contains
        defaultVendorTemDetailFiltering(
            "msdInitialFloorTime.contains=" + DEFAULT_MSD_INITIAL_FLOOR_TIME,
            "msdInitialFloorTime.contains=" + UPDATED_MSD_INITIAL_FLOOR_TIME
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMsdInitialFloorTimeNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where msdInitialFloorTime does not contain
        defaultVendorTemDetailFiltering(
            "msdInitialFloorTime.doesNotContain=" +
            UPDATED_MSD_INITIAL_FLOOR_TIME,
            "msdInitialFloorTime.doesNotContain=" +
            DEFAULT_MSD_INITIAL_FLOOR_TIME
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMsdBagSealDateIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where msdBagSealDate equals to
        defaultVendorTemDetailFiltering(
            "msdBagSealDate.equals=" + DEFAULT_MSD_BAG_SEAL_DATE,
            "msdBagSealDate.equals=" + UPDATED_MSD_BAG_SEAL_DATE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMsdBagSealDateIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where msdBagSealDate in
        defaultVendorTemDetailFiltering(
            "msdBagSealDate.in=" +
            DEFAULT_MSD_BAG_SEAL_DATE +
            "," +
            UPDATED_MSD_BAG_SEAL_DATE,
            "msdBagSealDate.in=" + UPDATED_MSD_BAG_SEAL_DATE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMsdBagSealDateIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where msdBagSealDate is not null
        defaultVendorTemDetailFiltering(
            "msdBagSealDate.specified=true",
            "msdBagSealDate.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMsdBagSealDateContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where msdBagSealDate contains
        defaultVendorTemDetailFiltering(
            "msdBagSealDate.contains=" + DEFAULT_MSD_BAG_SEAL_DATE,
            "msdBagSealDate.contains=" + UPDATED_MSD_BAG_SEAL_DATE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMsdBagSealDateNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where msdBagSealDate does not contain
        defaultVendorTemDetailFiltering(
            "msdBagSealDate.doesNotContain=" + UPDATED_MSD_BAG_SEAL_DATE,
            "msdBagSealDate.doesNotContain=" + DEFAULT_MSD_BAG_SEAL_DATE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMarketUsageIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where marketUsage equals to
        defaultVendorTemDetailFiltering(
            "marketUsage.equals=" + DEFAULT_MARKET_USAGE,
            "marketUsage.equals=" + UPDATED_MARKET_USAGE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMarketUsageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where marketUsage in
        defaultVendorTemDetailFiltering(
            "marketUsage.in=" +
            DEFAULT_MARKET_USAGE +
            "," +
            UPDATED_MARKET_USAGE,
            "marketUsage.in=" + UPDATED_MARKET_USAGE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMarketUsageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where marketUsage is not null
        defaultVendorTemDetailFiltering(
            "marketUsage.specified=true",
            "marketUsage.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMarketUsageContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where marketUsage contains
        defaultVendorTemDetailFiltering(
            "marketUsage.contains=" + DEFAULT_MARKET_USAGE,
            "marketUsage.contains=" + UPDATED_MARKET_USAGE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMarketUsageNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where marketUsage does not contain
        defaultVendorTemDetailFiltering(
            "marketUsage.doesNotContain=" + UPDATED_MARKET_USAGE,
            "marketUsage.doesNotContain=" + DEFAULT_MARKET_USAGE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByQuantityOverrideIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where quantityOverride equals to
        defaultVendorTemDetailFiltering(
            "quantityOverride.equals=" + DEFAULT_QUANTITY_OVERRIDE,
            "quantityOverride.equals=" + UPDATED_QUANTITY_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByQuantityOverrideIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where quantityOverride in
        defaultVendorTemDetailFiltering(
            "quantityOverride.in=" +
            DEFAULT_QUANTITY_OVERRIDE +
            "," +
            UPDATED_QUANTITY_OVERRIDE,
            "quantityOverride.in=" + UPDATED_QUANTITY_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByQuantityOverrideIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where quantityOverride is not null
        defaultVendorTemDetailFiltering(
            "quantityOverride.specified=true",
            "quantityOverride.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByQuantityOverrideIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where quantityOverride is greater than or equal to
        defaultVendorTemDetailFiltering(
            "quantityOverride.greaterThanOrEqual=" + DEFAULT_QUANTITY_OVERRIDE,
            "quantityOverride.greaterThanOrEqual=" + UPDATED_QUANTITY_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByQuantityOverrideIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where quantityOverride is less than or equal to
        defaultVendorTemDetailFiltering(
            "quantityOverride.lessThanOrEqual=" + DEFAULT_QUANTITY_OVERRIDE,
            "quantityOverride.lessThanOrEqual=" + SMALLER_QUANTITY_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByQuantityOverrideIsLessThanSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where quantityOverride is less than
        defaultVendorTemDetailFiltering(
            "quantityOverride.lessThan=" + UPDATED_QUANTITY_OVERRIDE,
            "quantityOverride.lessThan=" + DEFAULT_QUANTITY_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByQuantityOverrideIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where quantityOverride is greater than
        defaultVendorTemDetailFiltering(
            "quantityOverride.greaterThan=" + SMALLER_QUANTITY_OVERRIDE,
            "quantityOverride.greaterThan=" + DEFAULT_QUANTITY_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByShelfTimeIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where shelfTime equals to
        defaultVendorTemDetailFiltering(
            "shelfTime.equals=" + DEFAULT_SHELF_TIME,
            "shelfTime.equals=" + UPDATED_SHELF_TIME
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByShelfTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where shelfTime in
        defaultVendorTemDetailFiltering(
            "shelfTime.in=" + DEFAULT_SHELF_TIME + "," + UPDATED_SHELF_TIME,
            "shelfTime.in=" + UPDATED_SHELF_TIME
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByShelfTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where shelfTime is not null
        defaultVendorTemDetailFiltering(
            "shelfTime.specified=true",
            "shelfTime.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByShelfTimeContainsSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where shelfTime contains
        defaultVendorTemDetailFiltering(
            "shelfTime.contains=" + DEFAULT_SHELF_TIME,
            "shelfTime.contains=" + UPDATED_SHELF_TIME
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByShelfTimeNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where shelfTime does not contain
        defaultVendorTemDetailFiltering(
            "shelfTime.doesNotContain=" + UPDATED_SHELF_TIME,
            "shelfTime.doesNotContain=" + DEFAULT_SHELF_TIME
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsBySpMaterialNameIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where spMaterialName equals to
        defaultVendorTemDetailFiltering(
            "spMaterialName.equals=" + DEFAULT_SP_MATERIAL_NAME,
            "spMaterialName.equals=" + UPDATED_SP_MATERIAL_NAME
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsBySpMaterialNameIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where spMaterialName in
        defaultVendorTemDetailFiltering(
            "spMaterialName.in=" +
            DEFAULT_SP_MATERIAL_NAME +
            "," +
            UPDATED_SP_MATERIAL_NAME,
            "spMaterialName.in=" + UPDATED_SP_MATERIAL_NAME
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsBySpMaterialNameIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where spMaterialName is not null
        defaultVendorTemDetailFiltering(
            "spMaterialName.specified=true",
            "spMaterialName.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsBySpMaterialNameContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where spMaterialName contains
        defaultVendorTemDetailFiltering(
            "spMaterialName.contains=" + DEFAULT_SP_MATERIAL_NAME,
            "spMaterialName.contains=" + UPDATED_SP_MATERIAL_NAME
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsBySpMaterialNameNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where spMaterialName does not contain
        defaultVendorTemDetailFiltering(
            "spMaterialName.doesNotContain=" + UPDATED_SP_MATERIAL_NAME,
            "spMaterialName.doesNotContain=" + DEFAULT_SP_MATERIAL_NAME
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByWarningLimitIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where warningLimit equals to
        defaultVendorTemDetailFiltering(
            "warningLimit.equals=" + DEFAULT_WARNING_LIMIT,
            "warningLimit.equals=" + UPDATED_WARNING_LIMIT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByWarningLimitIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where warningLimit in
        defaultVendorTemDetailFiltering(
            "warningLimit.in=" +
            DEFAULT_WARNING_LIMIT +
            "," +
            UPDATED_WARNING_LIMIT,
            "warningLimit.in=" + UPDATED_WARNING_LIMIT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByWarningLimitIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where warningLimit is not null
        defaultVendorTemDetailFiltering(
            "warningLimit.specified=true",
            "warningLimit.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByWarningLimitContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where warningLimit contains
        defaultVendorTemDetailFiltering(
            "warningLimit.contains=" + DEFAULT_WARNING_LIMIT,
            "warningLimit.contains=" + UPDATED_WARNING_LIMIT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByWarningLimitNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where warningLimit does not contain
        defaultVendorTemDetailFiltering(
            "warningLimit.doesNotContain=" + UPDATED_WARNING_LIMIT,
            "warningLimit.doesNotContain=" + DEFAULT_WARNING_LIMIT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMaximumLimitIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where maximumLimit equals to
        defaultVendorTemDetailFiltering(
            "maximumLimit.equals=" + DEFAULT_MAXIMUM_LIMIT,
            "maximumLimit.equals=" + UPDATED_MAXIMUM_LIMIT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMaximumLimitIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where maximumLimit in
        defaultVendorTemDetailFiltering(
            "maximumLimit.in=" +
            DEFAULT_MAXIMUM_LIMIT +
            "," +
            UPDATED_MAXIMUM_LIMIT,
            "maximumLimit.in=" + UPDATED_MAXIMUM_LIMIT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMaximumLimitIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where maximumLimit is not null
        defaultVendorTemDetailFiltering(
            "maximumLimit.specified=true",
            "maximumLimit.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMaximumLimitContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where maximumLimit contains
        defaultVendorTemDetailFiltering(
            "maximumLimit.contains=" + DEFAULT_MAXIMUM_LIMIT,
            "maximumLimit.contains=" + UPDATED_MAXIMUM_LIMIT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByMaximumLimitNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where maximumLimit does not contain
        defaultVendorTemDetailFiltering(
            "maximumLimit.doesNotContain=" + UPDATED_MAXIMUM_LIMIT,
            "maximumLimit.doesNotContain=" + DEFAULT_MAXIMUM_LIMIT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByCommentsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where comments equals to
        defaultVendorTemDetailFiltering(
            "comments.equals=" + DEFAULT_COMMENTS,
            "comments.equals=" + UPDATED_COMMENTS
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByCommentsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where comments in
        defaultVendorTemDetailFiltering(
            "comments.in=" + DEFAULT_COMMENTS + "," + UPDATED_COMMENTS,
            "comments.in=" + UPDATED_COMMENTS
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByCommentsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where comments is not null
        defaultVendorTemDetailFiltering(
            "comments.specified=true",
            "comments.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByCommentsContainsSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where comments contains
        defaultVendorTemDetailFiltering(
            "comments.contains=" + DEFAULT_COMMENTS,
            "comments.contains=" + UPDATED_COMMENTS
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByCommentsNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where comments does not contain
        defaultVendorTemDetailFiltering(
            "comments.doesNotContain=" + UPDATED_COMMENTS,
            "comments.doesNotContain=" + DEFAULT_COMMENTS
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByWarmupTimeIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where warmupTime equals to
        defaultVendorTemDetailFiltering(
            "warmupTime.equals=" + DEFAULT_WARMUP_TIME,
            "warmupTime.equals=" + UPDATED_WARMUP_TIME
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByWarmupTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where warmupTime in
        defaultVendorTemDetailFiltering(
            "warmupTime.in=" + DEFAULT_WARMUP_TIME + "," + UPDATED_WARMUP_TIME,
            "warmupTime.in=" + UPDATED_WARMUP_TIME
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByWarmupTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where warmupTime is not null
        defaultVendorTemDetailFiltering(
            "warmupTime.specified=true",
            "warmupTime.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByWarmupTimeContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where warmupTime contains
        defaultVendorTemDetailFiltering(
            "warmupTime.contains=" + DEFAULT_WARMUP_TIME,
            "warmupTime.contains=" + UPDATED_WARMUP_TIME
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByWarmupTimeNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where warmupTime does not contain
        defaultVendorTemDetailFiltering(
            "warmupTime.doesNotContain=" + UPDATED_WARMUP_TIME,
            "warmupTime.doesNotContain=" + DEFAULT_WARMUP_TIME
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByStorageUnitIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where storageUnit equals to
        defaultVendorTemDetailFiltering(
            "storageUnit.equals=" + DEFAULT_STORAGE_UNIT,
            "storageUnit.equals=" + UPDATED_STORAGE_UNIT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByStorageUnitIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where storageUnit in
        defaultVendorTemDetailFiltering(
            "storageUnit.in=" +
            DEFAULT_STORAGE_UNIT +
            "," +
            UPDATED_STORAGE_UNIT,
            "storageUnit.in=" + UPDATED_STORAGE_UNIT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByStorageUnitIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where storageUnit is not null
        defaultVendorTemDetailFiltering(
            "storageUnit.specified=true",
            "storageUnit.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByStorageUnitContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where storageUnit contains
        defaultVendorTemDetailFiltering(
            "storageUnit.contains=" + DEFAULT_STORAGE_UNIT,
            "storageUnit.contains=" + UPDATED_STORAGE_UNIT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByStorageUnitNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where storageUnit does not contain
        defaultVendorTemDetailFiltering(
            "storageUnit.doesNotContain=" + UPDATED_STORAGE_UNIT,
            "storageUnit.doesNotContain=" + DEFAULT_STORAGE_UNIT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsBySubStorageUnitIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where subStorageUnit equals to
        defaultVendorTemDetailFiltering(
            "subStorageUnit.equals=" + DEFAULT_SUB_STORAGE_UNIT,
            "subStorageUnit.equals=" + UPDATED_SUB_STORAGE_UNIT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsBySubStorageUnitIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where subStorageUnit in
        defaultVendorTemDetailFiltering(
            "subStorageUnit.in=" +
            DEFAULT_SUB_STORAGE_UNIT +
            "," +
            UPDATED_SUB_STORAGE_UNIT,
            "subStorageUnit.in=" + UPDATED_SUB_STORAGE_UNIT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsBySubStorageUnitIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where subStorageUnit is not null
        defaultVendorTemDetailFiltering(
            "subStorageUnit.specified=true",
            "subStorageUnit.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsBySubStorageUnitContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where subStorageUnit contains
        defaultVendorTemDetailFiltering(
            "subStorageUnit.contains=" + DEFAULT_SUB_STORAGE_UNIT,
            "subStorageUnit.contains=" + UPDATED_SUB_STORAGE_UNIT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsBySubStorageUnitNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where subStorageUnit does not contain
        defaultVendorTemDetailFiltering(
            "subStorageUnit.doesNotContain=" + UPDATED_SUB_STORAGE_UNIT,
            "subStorageUnit.doesNotContain=" + DEFAULT_SUB_STORAGE_UNIT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByLocationOverrideIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where locationOverride equals to
        defaultVendorTemDetailFiltering(
            "locationOverride.equals=" + DEFAULT_LOCATION_OVERRIDE,
            "locationOverride.equals=" + UPDATED_LOCATION_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByLocationOverrideIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where locationOverride in
        defaultVendorTemDetailFiltering(
            "locationOverride.in=" +
            DEFAULT_LOCATION_OVERRIDE +
            "," +
            UPDATED_LOCATION_OVERRIDE,
            "locationOverride.in=" + UPDATED_LOCATION_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByLocationOverrideIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where locationOverride is not null
        defaultVendorTemDetailFiltering(
            "locationOverride.specified=true",
            "locationOverride.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByLocationOverrideContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where locationOverride contains
        defaultVendorTemDetailFiltering(
            "locationOverride.contains=" + DEFAULT_LOCATION_OVERRIDE,
            "locationOverride.contains=" + UPDATED_LOCATION_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByLocationOverrideNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where locationOverride does not contain
        defaultVendorTemDetailFiltering(
            "locationOverride.doesNotContain=" + UPDATED_LOCATION_OVERRIDE,
            "locationOverride.doesNotContain=" + DEFAULT_LOCATION_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByExpirationDateIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where expirationDate equals to
        defaultVendorTemDetailFiltering(
            "expirationDate.equals=" + DEFAULT_EXPIRATION_DATE,
            "expirationDate.equals=" + UPDATED_EXPIRATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByExpirationDateIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where expirationDate in
        defaultVendorTemDetailFiltering(
            "expirationDate.in=" +
            DEFAULT_EXPIRATION_DATE +
            "," +
            UPDATED_EXPIRATION_DATE,
            "expirationDate.in=" + UPDATED_EXPIRATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByExpirationDateIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where expirationDate is not null
        defaultVendorTemDetailFiltering(
            "expirationDate.specified=true",
            "expirationDate.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByExpirationDateContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where expirationDate contains
        defaultVendorTemDetailFiltering(
            "expirationDate.contains=" + DEFAULT_EXPIRATION_DATE,
            "expirationDate.contains=" + UPDATED_EXPIRATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByExpirationDateNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where expirationDate does not contain
        defaultVendorTemDetailFiltering(
            "expirationDate.doesNotContain=" + UPDATED_EXPIRATION_DATE,
            "expirationDate.doesNotContain=" + DEFAULT_EXPIRATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByManufacturingDateIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where manufacturingDate equals to
        defaultVendorTemDetailFiltering(
            "manufacturingDate.equals=" + DEFAULT_MANUFACTURING_DATE,
            "manufacturingDate.equals=" + UPDATED_MANUFACTURING_DATE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByManufacturingDateIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where manufacturingDate in
        defaultVendorTemDetailFiltering(
            "manufacturingDate.in=" +
            DEFAULT_MANUFACTURING_DATE +
            "," +
            UPDATED_MANUFACTURING_DATE,
            "manufacturingDate.in=" + UPDATED_MANUFACTURING_DATE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByManufacturingDateIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where manufacturingDate is not null
        defaultVendorTemDetailFiltering(
            "manufacturingDate.specified=true",
            "manufacturingDate.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByManufacturingDateContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where manufacturingDate contains
        defaultVendorTemDetailFiltering(
            "manufacturingDate.contains=" + DEFAULT_MANUFACTURING_DATE,
            "manufacturingDate.contains=" + UPDATED_MANUFACTURING_DATE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByManufacturingDateNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where manufacturingDate does not contain
        defaultVendorTemDetailFiltering(
            "manufacturingDate.doesNotContain=" + UPDATED_MANUFACTURING_DATE,
            "manufacturingDate.doesNotContain=" + DEFAULT_MANUFACTURING_DATE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByPartClassIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where partClass equals to
        defaultVendorTemDetailFiltering(
            "partClass.equals=" + DEFAULT_PART_CLASS,
            "partClass.equals=" + UPDATED_PART_CLASS
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByPartClassIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where partClass in
        defaultVendorTemDetailFiltering(
            "partClass.in=" + DEFAULT_PART_CLASS + "," + UPDATED_PART_CLASS,
            "partClass.in=" + UPDATED_PART_CLASS
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByPartClassIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where partClass is not null
        defaultVendorTemDetailFiltering(
            "partClass.specified=true",
            "partClass.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByPartClassContainsSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where partClass contains
        defaultVendorTemDetailFiltering(
            "partClass.contains=" + DEFAULT_PART_CLASS,
            "partClass.contains=" + UPDATED_PART_CLASS
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByPartClassNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where partClass does not contain
        defaultVendorTemDetailFiltering(
            "partClass.doesNotContain=" + UPDATED_PART_CLASS,
            "partClass.doesNotContain=" + DEFAULT_PART_CLASS
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsBySapCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where sapCode equals to
        defaultVendorTemDetailFiltering(
            "sapCode.equals=" + DEFAULT_SAP_CODE,
            "sapCode.equals=" + UPDATED_SAP_CODE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsBySapCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where sapCode in
        defaultVendorTemDetailFiltering(
            "sapCode.in=" + DEFAULT_SAP_CODE + "," + UPDATED_SAP_CODE,
            "sapCode.in=" + UPDATED_SAP_CODE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsBySapCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where sapCode is not null
        defaultVendorTemDetailFiltering(
            "sapCode.specified=true",
            "sapCode.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsBySapCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where sapCode contains
        defaultVendorTemDetailFiltering(
            "sapCode.contains=" + DEFAULT_SAP_CODE,
            "sapCode.contains=" + UPDATED_SAP_CODE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsBySapCodeNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where sapCode does not contain
        defaultVendorTemDetailFiltering(
            "sapCode.doesNotContain=" + UPDATED_SAP_CODE,
            "sapCode.doesNotContain=" + DEFAULT_SAP_CODE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByVendorQrCodeIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where vendorQrCode equals to
        defaultVendorTemDetailFiltering(
            "vendorQrCode.equals=" + DEFAULT_VENDOR_QR_CODE,
            "vendorQrCode.equals=" + UPDATED_VENDOR_QR_CODE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByVendorQrCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where vendorQrCode in
        defaultVendorTemDetailFiltering(
            "vendorQrCode.in=" +
            DEFAULT_VENDOR_QR_CODE +
            "," +
            UPDATED_VENDOR_QR_CODE,
            "vendorQrCode.in=" + UPDATED_VENDOR_QR_CODE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByVendorQrCodeIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where vendorQrCode is not null
        defaultVendorTemDetailFiltering(
            "vendorQrCode.specified=true",
            "vendorQrCode.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByVendorQrCodeContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where vendorQrCode contains
        defaultVendorTemDetailFiltering(
            "vendorQrCode.contains=" + DEFAULT_VENDOR_QR_CODE,
            "vendorQrCode.contains=" + UPDATED_VENDOR_QR_CODE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByVendorQrCodeNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where vendorQrCode does not contain
        defaultVendorTemDetailFiltering(
            "vendorQrCode.doesNotContain=" + UPDATED_VENDOR_QR_CODE,
            "vendorQrCode.doesNotContain=" + DEFAULT_VENDOR_QR_CODE
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where status equals to
        defaultVendorTemDetailFiltering(
            "status.equals=" + DEFAULT_STATUS,
            "status.equals=" + UPDATED_STATUS
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where status in
        defaultVendorTemDetailFiltering(
            "status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS,
            "status.in=" + UPDATED_STATUS
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where status is not null
        defaultVendorTemDetailFiltering(
            "status.specified=true",
            "status.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where status contains
        defaultVendorTemDetailFiltering(
            "status.contains=" + DEFAULT_STATUS,
            "status.contains=" + UPDATED_STATUS
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where status does not contain
        defaultVendorTemDetailFiltering(
            "status.doesNotContain=" + UPDATED_STATUS,
            "status.doesNotContain=" + DEFAULT_STATUS
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByCreatedByIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where createdBy equals to
        defaultVendorTemDetailFiltering(
            "createdBy.equals=" + DEFAULT_CREATED_BY,
            "createdBy.equals=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where createdBy in
        defaultVendorTemDetailFiltering(
            "createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY,
            "createdBy.in=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where createdBy is not null
        defaultVendorTemDetailFiltering(
            "createdBy.specified=true",
            "createdBy.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where createdBy contains
        defaultVendorTemDetailFiltering(
            "createdBy.contains=" + DEFAULT_CREATED_BY,
            "createdBy.contains=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByCreatedByNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where createdBy does not contain
        defaultVendorTemDetailFiltering(
            "createdBy.doesNotContain=" + UPDATED_CREATED_BY,
            "createdBy.doesNotContain=" + DEFAULT_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByCreatedAtIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where createdAt equals to
        defaultVendorTemDetailFiltering(
            "createdAt.equals=" + DEFAULT_CREATED_AT,
            "createdAt.equals=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where createdAt in
        defaultVendorTemDetailFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where createdAt is not null
        defaultVendorTemDetailFiltering(
            "createdAt.specified=true",
            "createdAt.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByCreatedAtIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where createdAt is greater than or equal to
        defaultVendorTemDetailFiltering(
            "createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT,
            "createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByCreatedAtIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where createdAt is less than or equal to
        defaultVendorTemDetailFiltering(
            "createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT,
            "createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByCreatedAtIsLessThanSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where createdAt is less than
        defaultVendorTemDetailFiltering(
            "createdAt.lessThan=" + UPDATED_CREATED_AT,
            "createdAt.lessThan=" + DEFAULT_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByCreatedAtIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where createdAt is greater than
        defaultVendorTemDetailFiltering(
            "createdAt.greaterThan=" + SMALLER_CREATED_AT,
            "createdAt.greaterThan=" + DEFAULT_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUpdatedByIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where updatedBy equals to
        defaultVendorTemDetailFiltering(
            "updatedBy.equals=" + DEFAULT_UPDATED_BY,
            "updatedBy.equals=" + UPDATED_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUpdatedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where updatedBy in
        defaultVendorTemDetailFiltering(
            "updatedBy.in=" + DEFAULT_UPDATED_BY + "," + UPDATED_UPDATED_BY,
            "updatedBy.in=" + UPDATED_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUpdatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where updatedBy is not null
        defaultVendorTemDetailFiltering(
            "updatedBy.specified=true",
            "updatedBy.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUpdatedByContainsSomething() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where updatedBy contains
        defaultVendorTemDetailFiltering(
            "updatedBy.contains=" + DEFAULT_UPDATED_BY,
            "updatedBy.contains=" + UPDATED_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUpdatedByNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where updatedBy does not contain
        defaultVendorTemDetailFiltering(
            "updatedBy.doesNotContain=" + UPDATED_UPDATED_BY,
            "updatedBy.doesNotContain=" + DEFAULT_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUpdatedAtIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where updatedAt equals to
        defaultVendorTemDetailFiltering(
            "updatedAt.equals=" + DEFAULT_UPDATED_AT,
            "updatedAt.equals=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where updatedAt in
        defaultVendorTemDetailFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where updatedAt is not null
        defaultVendorTemDetailFiltering(
            "updatedAt.specified=true",
            "updatedAt.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUpdatedAtIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where updatedAt is greater than or equal to
        defaultVendorTemDetailFiltering(
            "updatedAt.greaterThanOrEqual=" + DEFAULT_UPDATED_AT,
            "updatedAt.greaterThanOrEqual=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUpdatedAtIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where updatedAt is less than or equal to
        defaultVendorTemDetailFiltering(
            "updatedAt.lessThanOrEqual=" + DEFAULT_UPDATED_AT,
            "updatedAt.lessThanOrEqual=" + SMALLER_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUpdatedAtIsLessThanSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where updatedAt is less than
        defaultVendorTemDetailFiltering(
            "updatedAt.lessThan=" + UPDATED_UPDATED_AT,
            "updatedAt.lessThan=" + DEFAULT_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByUpdatedAtIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        // Get all the vendorTemDetailList where updatedAt is greater than
        defaultVendorTemDetailFiltering(
            "updatedAt.greaterThan=" + SMALLER_UPDATED_AT,
            "updatedAt.greaterThan=" + DEFAULT_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllVendorTemDetailsByPoDetailIsEqualToSomething() throws Exception {
        PoDetail poDetail;
        if (TestUtil.findAll(em, PoDetail.class).isEmpty()) {
            vendorTemDetailRepository.saveAndFlush(vendorTemDetail);
            poDetail = PoDetailResourceIT.createEntity();
        } else {
            poDetail = TestUtil.findAll(em, PoDetail.class).get(0);
        }
        em.persist(poDetail);
        em.flush();
        vendorTemDetail.setPoDetail(poDetail);
        vendorTemDetailRepository.saveAndFlush(vendorTemDetail);
        Long poDetailId = poDetail.getId();
        // Get all the vendorTemDetailList where poDetail equals to poDetailId
        defaultVendorTemDetailShouldBeFound("poDetailId.equals=" + poDetailId);

        // Get all the vendorTemDetailList where poDetail equals to (poDetailId + 1)
        defaultVendorTemDetailShouldNotBeFound(
            "poDetailId.equals=" + (poDetailId + 1)
        );
    }

    private void defaultVendorTemDetailFiltering(
        String shouldBeFound,
        String shouldNotBeFound
    ) throws Exception {
        defaultVendorTemDetailShouldBeFound(shouldBeFound);
        defaultVendorTemDetailShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultVendorTemDetailShouldBeFound(String filter)
        throws Exception {
        restVendorTemDetailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(vendorTemDetail.getId().intValue())
                )
            )
            .andExpect(jsonPath("$.[*].reelId").value(hasItem(DEFAULT_REEL_ID)))
            .andExpect(
                jsonPath("$.[*].partNumber").value(hasItem(DEFAULT_PART_NUMBER))
            )
            .andExpect(jsonPath("$.[*].vendor").value(hasItem(DEFAULT_VENDOR)))
            .andExpect(jsonPath("$.[*].lot").value(hasItem(DEFAULT_LOT)))
            .andExpect(
                jsonPath("$.[*].userData1").value(hasItem(DEFAULT_USER_DATA_1))
            )
            .andExpect(
                jsonPath("$.[*].userData2").value(hasItem(DEFAULT_USER_DATA_2))
            )
            .andExpect(
                jsonPath("$.[*].userData3").value(hasItem(DEFAULT_USER_DATA_3))
            )
            .andExpect(
                jsonPath("$.[*].userData4").value(hasItem(DEFAULT_USER_DATA_4))
            )
            .andExpect(
                jsonPath("$.[*].userData5").value(hasItem(DEFAULT_USER_DATA_5))
            )
            .andExpect(
                jsonPath("$.[*].initialQuantity").value(
                    hasItem(DEFAULT_INITIAL_QUANTITY)
                )
            )
            .andExpect(
                jsonPath("$.[*].msdLevel").value(hasItem(DEFAULT_MSD_LEVEL))
            )
            .andExpect(
                jsonPath("$.[*].msdInitialFloorTime").value(
                    hasItem(DEFAULT_MSD_INITIAL_FLOOR_TIME)
                )
            )
            .andExpect(
                jsonPath("$.[*].msdBagSealDate").value(
                    hasItem(DEFAULT_MSD_BAG_SEAL_DATE)
                )
            )
            .andExpect(
                jsonPath("$.[*].marketUsage").value(
                    hasItem(DEFAULT_MARKET_USAGE)
                )
            )
            .andExpect(
                jsonPath("$.[*].quantityOverride").value(
                    hasItem(DEFAULT_QUANTITY_OVERRIDE)
                )
            )
            .andExpect(
                jsonPath("$.[*].shelfTime").value(hasItem(DEFAULT_SHELF_TIME))
            )
            .andExpect(
                jsonPath("$.[*].spMaterialName").value(
                    hasItem(DEFAULT_SP_MATERIAL_NAME)
                )
            )
            .andExpect(
                jsonPath("$.[*].warningLimit").value(
                    hasItem(DEFAULT_WARNING_LIMIT)
                )
            )
            .andExpect(
                jsonPath("$.[*].maximumLimit").value(
                    hasItem(DEFAULT_MAXIMUM_LIMIT)
                )
            )
            .andExpect(
                jsonPath("$.[*].comments").value(hasItem(DEFAULT_COMMENTS))
            )
            .andExpect(
                jsonPath("$.[*].warmupTime").value(hasItem(DEFAULT_WARMUP_TIME))
            )
            .andExpect(
                jsonPath("$.[*].storageUnit").value(
                    hasItem(DEFAULT_STORAGE_UNIT)
                )
            )
            .andExpect(
                jsonPath("$.[*].subStorageUnit").value(
                    hasItem(DEFAULT_SUB_STORAGE_UNIT)
                )
            )
            .andExpect(
                jsonPath("$.[*].locationOverride").value(
                    hasItem(DEFAULT_LOCATION_OVERRIDE)
                )
            )
            .andExpect(
                jsonPath("$.[*].expirationDate").value(
                    hasItem(DEFAULT_EXPIRATION_DATE)
                )
            )
            .andExpect(
                jsonPath("$.[*].manufacturingDate").value(
                    hasItem(DEFAULT_MANUFACTURING_DATE)
                )
            )
            .andExpect(
                jsonPath("$.[*].partClass").value(hasItem(DEFAULT_PART_CLASS))
            )
            .andExpect(
                jsonPath("$.[*].sapCode").value(hasItem(DEFAULT_SAP_CODE))
            )
            .andExpect(
                jsonPath("$.[*].vendorQrCode").value(
                    hasItem(DEFAULT_VENDOR_QR_CODE)
                )
            )
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(
                jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY))
            )
            .andExpect(
                jsonPath("$.[*].createdAt").value(
                    hasItem(sameInstant(DEFAULT_CREATED_AT))
                )
            )
            .andExpect(
                jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY))
            )
            .andExpect(
                jsonPath("$.[*].updatedAt").value(
                    hasItem(sameInstant(DEFAULT_UPDATED_AT))
                )
            );

        // Check, that the count call also returns 1
        restVendorTemDetailMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultVendorTemDetailShouldNotBeFound(String filter)
        throws Exception {
        restVendorTemDetailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restVendorTemDetailMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingVendorTemDetail() throws Exception {
        // Get the vendorTemDetail
        restVendorTemDetailMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVendorTemDetail() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the vendorTemDetail
        VendorTemDetail updatedVendorTemDetail = vendorTemDetailRepository
            .findById(vendorTemDetail.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedVendorTemDetail are not directly saved in db
        em.detach(updatedVendorTemDetail);
        updatedVendorTemDetail
            .reelId(UPDATED_REEL_ID)
            .partNumber(UPDATED_PART_NUMBER)
            .vendor(UPDATED_VENDOR)
            .lot(UPDATED_LOT)
            .userData1(UPDATED_USER_DATA_1)
            .userData2(UPDATED_USER_DATA_2)
            .userData3(UPDATED_USER_DATA_3)
            .userData4(UPDATED_USER_DATA_4)
            .userData5(UPDATED_USER_DATA_5)
            .initialQuantity(UPDATED_INITIAL_QUANTITY)
            .msdLevel(UPDATED_MSD_LEVEL)
            .msdInitialFloorTime(UPDATED_MSD_INITIAL_FLOOR_TIME)
            .msdBagSealDate(UPDATED_MSD_BAG_SEAL_DATE)
            .marketUsage(UPDATED_MARKET_USAGE)
            .quantityOverride(UPDATED_QUANTITY_OVERRIDE)
            .shelfTime(UPDATED_SHELF_TIME)
            .spMaterialName(UPDATED_SP_MATERIAL_NAME)
            .warningLimit(UPDATED_WARNING_LIMIT)
            .maximumLimit(UPDATED_MAXIMUM_LIMIT)
            .comments(UPDATED_COMMENTS)
            .warmupTime(UPDATED_WARMUP_TIME)
            .storageUnit(UPDATED_STORAGE_UNIT)
            .subStorageUnit(UPDATED_SUB_STORAGE_UNIT)
            .locationOverride(UPDATED_LOCATION_OVERRIDE)
            .expirationDate(UPDATED_EXPIRATION_DATE)
            .manufacturingDate(UPDATED_MANUFACTURING_DATE)
            .partClass(UPDATED_PART_CLASS)
            .sapCode(UPDATED_SAP_CODE)
            .vendorQrCode(UPDATED_VENDOR_QR_CODE)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT);
        VendorTemDetailDTO vendorTemDetailDTO = vendorTemDetailMapper.toDto(
            updatedVendorTemDetail
        );

        restVendorTemDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, vendorTemDetailDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(vendorTemDetailDTO))
            )
            .andExpect(status().isOk());

        // Validate the VendorTemDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVendorTemDetailToMatchAllProperties(
            updatedVendorTemDetail
        );
    }

    @Test
    @Transactional
    void putNonExistingVendorTemDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vendorTemDetail.setId(longCount.incrementAndGet());

        // Create the VendorTemDetail
        VendorTemDetailDTO vendorTemDetailDTO = vendorTemDetailMapper.toDto(
            vendorTemDetail
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVendorTemDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, vendorTemDetailDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(vendorTemDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VendorTemDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVendorTemDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vendorTemDetail.setId(longCount.incrementAndGet());

        // Create the VendorTemDetail
        VendorTemDetailDTO vendorTemDetailDTO = vendorTemDetailMapper.toDto(
            vendorTemDetail
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVendorTemDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(vendorTemDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VendorTemDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVendorTemDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vendorTemDetail.setId(longCount.incrementAndGet());

        // Create the VendorTemDetail
        VendorTemDetailDTO vendorTemDetailDTO = vendorTemDetailMapper.toDto(
            vendorTemDetail
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVendorTemDetailMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(vendorTemDetailDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the VendorTemDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVendorTemDetailWithPatch() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the vendorTemDetail using partial update
        VendorTemDetail partialUpdatedVendorTemDetail = new VendorTemDetail();
        partialUpdatedVendorTemDetail.setId(vendorTemDetail.getId());

        partialUpdatedVendorTemDetail
            .vendor(UPDATED_VENDOR)
            .userData1(UPDATED_USER_DATA_1)
            .userData3(UPDATED_USER_DATA_3)
            .userData4(UPDATED_USER_DATA_4)
            .userData5(UPDATED_USER_DATA_5)
            .msdLevel(UPDATED_MSD_LEVEL)
            .maximumLimit(UPDATED_MAXIMUM_LIMIT)
            .storageUnit(UPDATED_STORAGE_UNIT)
            .expirationDate(UPDATED_EXPIRATION_DATE)
            .manufacturingDate(UPDATED_MANUFACTURING_DATE)
            .partClass(UPDATED_PART_CLASS)
            .vendorQrCode(UPDATED_VENDOR_QR_CODE)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restVendorTemDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVendorTemDetail.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(partialUpdatedVendorTemDetail)
                    )
            )
            .andExpect(status().isOk());

        // Validate the VendorTemDetail in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVendorTemDetailUpdatableFieldsEquals(
            createUpdateProxyForBean(
                partialUpdatedVendorTemDetail,
                vendorTemDetail
            ),
            getPersistedVendorTemDetail(vendorTemDetail)
        );
    }

    @Test
    @Transactional
    void fullUpdateVendorTemDetailWithPatch() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the vendorTemDetail using partial update
        VendorTemDetail partialUpdatedVendorTemDetail = new VendorTemDetail();
        partialUpdatedVendorTemDetail.setId(vendorTemDetail.getId());

        partialUpdatedVendorTemDetail
            .reelId(UPDATED_REEL_ID)
            .partNumber(UPDATED_PART_NUMBER)
            .vendor(UPDATED_VENDOR)
            .lot(UPDATED_LOT)
            .userData1(UPDATED_USER_DATA_1)
            .userData2(UPDATED_USER_DATA_2)
            .userData3(UPDATED_USER_DATA_3)
            .userData4(UPDATED_USER_DATA_4)
            .userData5(UPDATED_USER_DATA_5)
            .initialQuantity(UPDATED_INITIAL_QUANTITY)
            .msdLevel(UPDATED_MSD_LEVEL)
            .msdInitialFloorTime(UPDATED_MSD_INITIAL_FLOOR_TIME)
            .msdBagSealDate(UPDATED_MSD_BAG_SEAL_DATE)
            .marketUsage(UPDATED_MARKET_USAGE)
            .quantityOverride(UPDATED_QUANTITY_OVERRIDE)
            .shelfTime(UPDATED_SHELF_TIME)
            .spMaterialName(UPDATED_SP_MATERIAL_NAME)
            .warningLimit(UPDATED_WARNING_LIMIT)
            .maximumLimit(UPDATED_MAXIMUM_LIMIT)
            .comments(UPDATED_COMMENTS)
            .warmupTime(UPDATED_WARMUP_TIME)
            .storageUnit(UPDATED_STORAGE_UNIT)
            .subStorageUnit(UPDATED_SUB_STORAGE_UNIT)
            .locationOverride(UPDATED_LOCATION_OVERRIDE)
            .expirationDate(UPDATED_EXPIRATION_DATE)
            .manufacturingDate(UPDATED_MANUFACTURING_DATE)
            .partClass(UPDATED_PART_CLASS)
            .sapCode(UPDATED_SAP_CODE)
            .vendorQrCode(UPDATED_VENDOR_QR_CODE)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT);

        restVendorTemDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVendorTemDetail.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(partialUpdatedVendorTemDetail)
                    )
            )
            .andExpect(status().isOk());

        // Validate the VendorTemDetail in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVendorTemDetailUpdatableFieldsEquals(
            partialUpdatedVendorTemDetail,
            getPersistedVendorTemDetail(partialUpdatedVendorTemDetail)
        );
    }

    @Test
    @Transactional
    void patchNonExistingVendorTemDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vendorTemDetail.setId(longCount.incrementAndGet());

        // Create the VendorTemDetail
        VendorTemDetailDTO vendorTemDetailDTO = vendorTemDetailMapper.toDto(
            vendorTemDetail
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVendorTemDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, vendorTemDetailDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(vendorTemDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VendorTemDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVendorTemDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vendorTemDetail.setId(longCount.incrementAndGet());

        // Create the VendorTemDetail
        VendorTemDetailDTO vendorTemDetailDTO = vendorTemDetailMapper.toDto(
            vendorTemDetail
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVendorTemDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(vendorTemDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VendorTemDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVendorTemDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vendorTemDetail.setId(longCount.incrementAndGet());

        // Create the VendorTemDetail
        VendorTemDetailDTO vendorTemDetailDTO = vendorTemDetailMapper.toDto(
            vendorTemDetail
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVendorTemDetailMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(vendorTemDetailDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the VendorTemDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVendorTemDetail() throws Exception {
        // Initialize the database
        insertedVendorTemDetail = vendorTemDetailRepository.saveAndFlush(
            vendorTemDetail
        );

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the vendorTemDetail
        restVendorTemDetailMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, vendorTemDetail.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return vendorTemDetailRepository.count();
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

    protected VendorTemDetail getPersistedVendorTemDetail(
        VendorTemDetail vendorTemDetail
    ) {
        return vendorTemDetailRepository
            .findById(vendorTemDetail.getId())
            .orElseThrow();
    }

    protected void assertPersistedVendorTemDetailToMatchAllProperties(
        VendorTemDetail expectedVendorTemDetail
    ) {
        assertVendorTemDetailAllPropertiesEquals(
            expectedVendorTemDetail,
            getPersistedVendorTemDetail(expectedVendorTemDetail)
        );
    }

    protected void assertPersistedVendorTemDetailToMatchUpdatableProperties(
        VendorTemDetail expectedVendorTemDetail
    ) {
        assertVendorTemDetailAllUpdatablePropertiesEquals(
            expectedVendorTemDetail,
            getPersistedVendorTemDetail(expectedVendorTemDetail)
        );
    }
}
