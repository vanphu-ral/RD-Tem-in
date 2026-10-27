package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.SplitInventoryAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.SplitInventory;
import com.mycompany.myapp.repository.SplitInventoryRepository;
import com.mycompany.myapp.service.dto.SplitInventoryDTO;
import com.mycompany.myapp.service.mapper.SplitInventoryMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SplitInventoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SplitInventoryResourceIT {

    private static final Long DEFAULT_BUSINESS_SOURCE_ID = 1L;
    private static final Long UPDATED_BUSINESS_SOURCE_ID = 2L;
    private static final Long SMALLER_BUSINESS_SOURCE_ID = 1L - 1L;

    private static final String DEFAULT_BUSINESS_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_BUSINESS_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_REEL_ID = "AAAAAAAAAA";
    private static final String UPDATED_REEL_ID = "BBBBBBBBBB";

    private static final String DEFAULT_PARENT_MATERIAL_IDENTIFIER =
        "AAAAAAAAAA";
    private static final String UPDATED_PARENT_MATERIAL_IDENTIFIER =
        "BBBBBBBBBB";

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

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(
        ChronoUnit.MILLIS
    );

    private static final String DEFAULT_UPDATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_UPDATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(
        ChronoUnit.MILLIS
    );

    private static final Boolean DEFAULT_PRINT_STATUS = false;
    private static final Boolean UPDATED_PRINT_STATUS = true;

    private static final String ENTITY_API_URL = "/api/split-inventories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SplitInventoryRepository splitInventoryRepository;

    @Autowired
    private SplitInventoryMapper splitInventoryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSplitInventoryMockMvc;

    private SplitInventory splitInventory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SplitInventory createEntity(EntityManager em) {
        SplitInventory splitInventory = new SplitInventory()
            .businessSourceId(DEFAULT_BUSINESS_SOURCE_ID)
            .businessType(DEFAULT_BUSINESS_TYPE)
            .reelId(DEFAULT_REEL_ID)
            .parentMaterialIdentifier(DEFAULT_PARENT_MATERIAL_IDENTIFIER)
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
            .updatedAt(DEFAULT_UPDATED_AT)
            .printStatus(DEFAULT_PRINT_STATUS);
        return splitInventory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SplitInventory createUpdatedEntity(EntityManager em) {
        SplitInventory splitInventory = new SplitInventory()
            .businessSourceId(UPDATED_BUSINESS_SOURCE_ID)
            .businessType(UPDATED_BUSINESS_TYPE)
            .reelId(UPDATED_REEL_ID)
            .parentMaterialIdentifier(UPDATED_PARENT_MATERIAL_IDENTIFIER)
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
            .updatedAt(UPDATED_UPDATED_AT)
            .printStatus(UPDATED_PRINT_STATUS);
        return splitInventory;
    }

    @BeforeEach
    public void initTest() {
        splitInventory = createEntity(em);
    }

    @Test
    @Transactional
    void createSplitInventory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SplitInventory
        SplitInventoryDTO splitInventoryDTO = splitInventoryMapper.toDto(
            splitInventory
        );
        var returnedSplitInventoryDTO = om.readValue(
            restSplitInventoryMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(splitInventoryDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SplitInventoryDTO.class
        );

        // Validate the SplitInventory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSplitInventory = splitInventoryMapper.toEntity(
            returnedSplitInventoryDTO
        );
        assertSplitInventoryUpdatableFieldsEquals(
            returnedSplitInventory,
            getPersistedSplitInventory(returnedSplitInventory)
        );
    }

    @Test
    @Transactional
    void createSplitInventoryWithExistingId() throws Exception {
        // Create the SplitInventory with an existing ID
        splitInventory.setId(1L);
        SplitInventoryDTO splitInventoryDTO = splitInventoryMapper.toDto(
            splitInventory
        );

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSplitInventoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(splitInventoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SplitInventory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkVendorQrCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        splitInventory.setVendorQrCode(null);

        // Create the SplitInventory, which fails.
        SplitInventoryDTO splitInventoryDTO = splitInventoryMapper.toDto(
            splitInventory
        );

        restSplitInventoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(splitInventoryDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSplitInventories() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList
        restSplitInventoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(splitInventory.getId().intValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].businessSourceId").value(
                    hasItem(DEFAULT_BUSINESS_SOURCE_ID.intValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].businessType").value(
                    hasItem(DEFAULT_BUSINESS_TYPE)
                )
            )
            .andExpect(jsonPath("$.[*].reelId").value(hasItem(DEFAULT_REEL_ID)))
            .andExpect(
                jsonPath("$.[*].parentMaterialIdentifier").value(
                    hasItem(DEFAULT_PARENT_MATERIAL_IDENTIFIER)
                )
            )
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
                    hasItem(DEFAULT_CREATED_AT.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY))
            )
            .andExpect(
                jsonPath("$.[*].updatedAt").value(
                    hasItem(DEFAULT_UPDATED_AT.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].printStatus").value(
                    hasItem(DEFAULT_PRINT_STATUS.booleanValue())
                )
            );
    }

    @Test
    @Transactional
    void getSplitInventory() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get the splitInventory
        restSplitInventoryMockMvc
            .perform(get(ENTITY_API_URL_ID, splitInventory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.id").value(splitInventory.getId().intValue())
            )
            .andExpect(
                jsonPath("$.businessSourceId").value(
                    DEFAULT_BUSINESS_SOURCE_ID.intValue()
                )
            )
            .andExpect(jsonPath("$.businessType").value(DEFAULT_BUSINESS_TYPE))
            .andExpect(jsonPath("$.reelId").value(DEFAULT_REEL_ID))
            .andExpect(
                jsonPath("$.parentMaterialIdentifier").value(
                    DEFAULT_PARENT_MATERIAL_IDENTIFIER
                )
            )
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
                jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString())
            )
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
            .andExpect(
                jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString())
            )
            .andExpect(
                jsonPath("$.printStatus").value(
                    DEFAULT_PRINT_STATUS.booleanValue()
                )
            );
    }

    @Test
    @Transactional
    void getSplitInventoriesByIdFiltering() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        Long id = splitInventory.getId();

        defaultSplitInventoryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSplitInventoryFiltering(
            "id.greaterThanOrEqual=" + id,
            "id.greaterThan=" + id
        );

        defaultSplitInventoryFiltering(
            "id.lessThanOrEqual=" + id,
            "id.lessThan=" + id
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByBusinessSourceIdIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where businessSourceId equals to
        defaultSplitInventoryFiltering(
            "businessSourceId.equals=" + DEFAULT_BUSINESS_SOURCE_ID,
            "businessSourceId.equals=" + UPDATED_BUSINESS_SOURCE_ID
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByBusinessSourceIdIsInShouldWork()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where businessSourceId in
        defaultSplitInventoryFiltering(
            "businessSourceId.in=" +
            DEFAULT_BUSINESS_SOURCE_ID +
            "," +
            UPDATED_BUSINESS_SOURCE_ID,
            "businessSourceId.in=" + UPDATED_BUSINESS_SOURCE_ID
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByBusinessSourceIdIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where businessSourceId is not null
        defaultSplitInventoryFiltering(
            "businessSourceId.specified=true",
            "businessSourceId.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByBusinessSourceIdIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where businessSourceId is greater than or equal to
        defaultSplitInventoryFiltering(
            "businessSourceId.greaterThanOrEqual=" + DEFAULT_BUSINESS_SOURCE_ID,
            "businessSourceId.greaterThanOrEqual=" + UPDATED_BUSINESS_SOURCE_ID
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByBusinessSourceIdIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where businessSourceId is less than or equal to
        defaultSplitInventoryFiltering(
            "businessSourceId.lessThanOrEqual=" + DEFAULT_BUSINESS_SOURCE_ID,
            "businessSourceId.lessThanOrEqual=" + SMALLER_BUSINESS_SOURCE_ID
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByBusinessSourceIdIsLessThanSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where businessSourceId is less than
        defaultSplitInventoryFiltering(
            "businessSourceId.lessThan=" + UPDATED_BUSINESS_SOURCE_ID,
            "businessSourceId.lessThan=" + DEFAULT_BUSINESS_SOURCE_ID
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByBusinessSourceIdIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where businessSourceId is greater than
        defaultSplitInventoryFiltering(
            "businessSourceId.greaterThan=" + SMALLER_BUSINESS_SOURCE_ID,
            "businessSourceId.greaterThan=" + DEFAULT_BUSINESS_SOURCE_ID
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByBusinessTypeIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where businessType equals to
        defaultSplitInventoryFiltering(
            "businessType.equals=" + DEFAULT_BUSINESS_TYPE,
            "businessType.equals=" + UPDATED_BUSINESS_TYPE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByBusinessTypeIsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where businessType in
        defaultSplitInventoryFiltering(
            "businessType.in=" +
            DEFAULT_BUSINESS_TYPE +
            "," +
            UPDATED_BUSINESS_TYPE,
            "businessType.in=" + UPDATED_BUSINESS_TYPE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByBusinessTypeIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where businessType is not null
        defaultSplitInventoryFiltering(
            "businessType.specified=true",
            "businessType.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByBusinessTypeContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where businessType contains
        defaultSplitInventoryFiltering(
            "businessType.contains=" + DEFAULT_BUSINESS_TYPE,
            "businessType.contains=" + UPDATED_BUSINESS_TYPE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByBusinessTypeNotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where businessType does not contain
        defaultSplitInventoryFiltering(
            "businessType.doesNotContain=" + UPDATED_BUSINESS_TYPE,
            "businessType.doesNotContain=" + DEFAULT_BUSINESS_TYPE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByReelIdIsEqualToSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where reelId equals to
        defaultSplitInventoryFiltering(
            "reelId.equals=" + DEFAULT_REEL_ID,
            "reelId.equals=" + UPDATED_REEL_ID
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByReelIdIsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where reelId in
        defaultSplitInventoryFiltering(
            "reelId.in=" + DEFAULT_REEL_ID + "," + UPDATED_REEL_ID,
            "reelId.in=" + UPDATED_REEL_ID
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByReelIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where reelId is not null
        defaultSplitInventoryFiltering(
            "reelId.specified=true",
            "reelId.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByReelIdContainsSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where reelId contains
        defaultSplitInventoryFiltering(
            "reelId.contains=" + DEFAULT_REEL_ID,
            "reelId.contains=" + UPDATED_REEL_ID
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByReelIdNotContainsSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where reelId does not contain
        defaultSplitInventoryFiltering(
            "reelId.doesNotContain=" + UPDATED_REEL_ID,
            "reelId.doesNotContain=" + DEFAULT_REEL_ID
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByParentMaterialIdentifierIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where parentMaterialIdentifier equals to
        defaultSplitInventoryFiltering(
            "parentMaterialIdentifier.equals=" +
            DEFAULT_PARENT_MATERIAL_IDENTIFIER,
            "parentMaterialIdentifier.equals=" +
            UPDATED_PARENT_MATERIAL_IDENTIFIER
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByParentMaterialIdentifierIsInShouldWork()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where parentMaterialIdentifier in
        defaultSplitInventoryFiltering(
            "parentMaterialIdentifier.in=" +
            DEFAULT_PARENT_MATERIAL_IDENTIFIER +
            "," +
            UPDATED_PARENT_MATERIAL_IDENTIFIER,
            "parentMaterialIdentifier.in=" + UPDATED_PARENT_MATERIAL_IDENTIFIER
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByParentMaterialIdentifierIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where parentMaterialIdentifier is not null
        defaultSplitInventoryFiltering(
            "parentMaterialIdentifier.specified=true",
            "parentMaterialIdentifier.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByParentMaterialIdentifierContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where parentMaterialIdentifier contains
        defaultSplitInventoryFiltering(
            "parentMaterialIdentifier.contains=" +
            DEFAULT_PARENT_MATERIAL_IDENTIFIER,
            "parentMaterialIdentifier.contains=" +
            UPDATED_PARENT_MATERIAL_IDENTIFIER
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByParentMaterialIdentifierNotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where parentMaterialIdentifier does not contain
        defaultSplitInventoryFiltering(
            "parentMaterialIdentifier.doesNotContain=" +
            UPDATED_PARENT_MATERIAL_IDENTIFIER,
            "parentMaterialIdentifier.doesNotContain=" +
            DEFAULT_PARENT_MATERIAL_IDENTIFIER
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByPartNumberIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where partNumber equals to
        defaultSplitInventoryFiltering(
            "partNumber.equals=" + DEFAULT_PART_NUMBER,
            "partNumber.equals=" + UPDATED_PART_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByPartNumberIsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where partNumber in
        defaultSplitInventoryFiltering(
            "partNumber.in=" + DEFAULT_PART_NUMBER + "," + UPDATED_PART_NUMBER,
            "partNumber.in=" + UPDATED_PART_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByPartNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where partNumber is not null
        defaultSplitInventoryFiltering(
            "partNumber.specified=true",
            "partNumber.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByPartNumberContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where partNumber contains
        defaultSplitInventoryFiltering(
            "partNumber.contains=" + DEFAULT_PART_NUMBER,
            "partNumber.contains=" + UPDATED_PART_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByPartNumberNotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where partNumber does not contain
        defaultSplitInventoryFiltering(
            "partNumber.doesNotContain=" + UPDATED_PART_NUMBER,
            "partNumber.doesNotContain=" + DEFAULT_PART_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByVendorIsEqualToSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where vendor equals to
        defaultSplitInventoryFiltering(
            "vendor.equals=" + DEFAULT_VENDOR,
            "vendor.equals=" + UPDATED_VENDOR
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByVendorIsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where vendor in
        defaultSplitInventoryFiltering(
            "vendor.in=" + DEFAULT_VENDOR + "," + UPDATED_VENDOR,
            "vendor.in=" + UPDATED_VENDOR
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByVendorIsNullOrNotNull() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where vendor is not null
        defaultSplitInventoryFiltering(
            "vendor.specified=true",
            "vendor.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByVendorContainsSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where vendor contains
        defaultSplitInventoryFiltering(
            "vendor.contains=" + DEFAULT_VENDOR,
            "vendor.contains=" + UPDATED_VENDOR
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByVendorNotContainsSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where vendor does not contain
        defaultSplitInventoryFiltering(
            "vendor.doesNotContain=" + UPDATED_VENDOR,
            "vendor.doesNotContain=" + DEFAULT_VENDOR
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByLotIsEqualToSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where lot equals to
        defaultSplitInventoryFiltering(
            "lot.equals=" + DEFAULT_LOT,
            "lot.equals=" + UPDATED_LOT
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByLotIsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where lot in
        defaultSplitInventoryFiltering(
            "lot.in=" + DEFAULT_LOT + "," + UPDATED_LOT,
            "lot.in=" + UPDATED_LOT
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByLotIsNullOrNotNull() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where lot is not null
        defaultSplitInventoryFiltering(
            "lot.specified=true",
            "lot.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByLotContainsSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where lot contains
        defaultSplitInventoryFiltering(
            "lot.contains=" + DEFAULT_LOT,
            "lot.contains=" + UPDATED_LOT
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByLotNotContainsSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where lot does not contain
        defaultSplitInventoryFiltering(
            "lot.doesNotContain=" + UPDATED_LOT,
            "lot.doesNotContain=" + DEFAULT_LOT
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData1IsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData1 equals to
        defaultSplitInventoryFiltering(
            "userData1.equals=" + DEFAULT_USER_DATA_1,
            "userData1.equals=" + UPDATED_USER_DATA_1
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData1IsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData1 in
        defaultSplitInventoryFiltering(
            "userData1.in=" + DEFAULT_USER_DATA_1 + "," + UPDATED_USER_DATA_1,
            "userData1.in=" + UPDATED_USER_DATA_1
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData1IsNullOrNotNull() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData1 is not null
        defaultSplitInventoryFiltering(
            "userData1.specified=true",
            "userData1.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData1ContainsSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData1 contains
        defaultSplitInventoryFiltering(
            "userData1.contains=" + DEFAULT_USER_DATA_1,
            "userData1.contains=" + UPDATED_USER_DATA_1
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData1NotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData1 does not contain
        defaultSplitInventoryFiltering(
            "userData1.doesNotContain=" + UPDATED_USER_DATA_1,
            "userData1.doesNotContain=" + DEFAULT_USER_DATA_1
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData2IsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData2 equals to
        defaultSplitInventoryFiltering(
            "userData2.equals=" + DEFAULT_USER_DATA_2,
            "userData2.equals=" + UPDATED_USER_DATA_2
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData2IsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData2 in
        defaultSplitInventoryFiltering(
            "userData2.in=" + DEFAULT_USER_DATA_2 + "," + UPDATED_USER_DATA_2,
            "userData2.in=" + UPDATED_USER_DATA_2
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData2IsNullOrNotNull() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData2 is not null
        defaultSplitInventoryFiltering(
            "userData2.specified=true",
            "userData2.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData2ContainsSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData2 contains
        defaultSplitInventoryFiltering(
            "userData2.contains=" + DEFAULT_USER_DATA_2,
            "userData2.contains=" + UPDATED_USER_DATA_2
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData2NotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData2 does not contain
        defaultSplitInventoryFiltering(
            "userData2.doesNotContain=" + UPDATED_USER_DATA_2,
            "userData2.doesNotContain=" + DEFAULT_USER_DATA_2
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData3IsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData3 equals to
        defaultSplitInventoryFiltering(
            "userData3.equals=" + DEFAULT_USER_DATA_3,
            "userData3.equals=" + UPDATED_USER_DATA_3
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData3IsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData3 in
        defaultSplitInventoryFiltering(
            "userData3.in=" + DEFAULT_USER_DATA_3 + "," + UPDATED_USER_DATA_3,
            "userData3.in=" + UPDATED_USER_DATA_3
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData3IsNullOrNotNull() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData3 is not null
        defaultSplitInventoryFiltering(
            "userData3.specified=true",
            "userData3.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData3ContainsSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData3 contains
        defaultSplitInventoryFiltering(
            "userData3.contains=" + DEFAULT_USER_DATA_3,
            "userData3.contains=" + UPDATED_USER_DATA_3
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData3NotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData3 does not contain
        defaultSplitInventoryFiltering(
            "userData3.doesNotContain=" + UPDATED_USER_DATA_3,
            "userData3.doesNotContain=" + DEFAULT_USER_DATA_3
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData4IsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData4 equals to
        defaultSplitInventoryFiltering(
            "userData4.equals=" + DEFAULT_USER_DATA_4,
            "userData4.equals=" + UPDATED_USER_DATA_4
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData4IsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData4 in
        defaultSplitInventoryFiltering(
            "userData4.in=" + DEFAULT_USER_DATA_4 + "," + UPDATED_USER_DATA_4,
            "userData4.in=" + UPDATED_USER_DATA_4
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData4IsNullOrNotNull() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData4 is not null
        defaultSplitInventoryFiltering(
            "userData4.specified=true",
            "userData4.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData4ContainsSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData4 contains
        defaultSplitInventoryFiltering(
            "userData4.contains=" + DEFAULT_USER_DATA_4,
            "userData4.contains=" + UPDATED_USER_DATA_4
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData4NotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData4 does not contain
        defaultSplitInventoryFiltering(
            "userData4.doesNotContain=" + UPDATED_USER_DATA_4,
            "userData4.doesNotContain=" + DEFAULT_USER_DATA_4
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData5IsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData5 equals to
        defaultSplitInventoryFiltering(
            "userData5.equals=" + DEFAULT_USER_DATA_5,
            "userData5.equals=" + UPDATED_USER_DATA_5
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData5IsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData5 in
        defaultSplitInventoryFiltering(
            "userData5.in=" + DEFAULT_USER_DATA_5 + "," + UPDATED_USER_DATA_5,
            "userData5.in=" + UPDATED_USER_DATA_5
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData5IsNullOrNotNull() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData5 is not null
        defaultSplitInventoryFiltering(
            "userData5.specified=true",
            "userData5.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData5ContainsSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData5 contains
        defaultSplitInventoryFiltering(
            "userData5.contains=" + DEFAULT_USER_DATA_5,
            "userData5.contains=" + UPDATED_USER_DATA_5
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUserData5NotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where userData5 does not contain
        defaultSplitInventoryFiltering(
            "userData5.doesNotContain=" + UPDATED_USER_DATA_5,
            "userData5.doesNotContain=" + DEFAULT_USER_DATA_5
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByInitialQuantityIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where initialQuantity equals to
        defaultSplitInventoryFiltering(
            "initialQuantity.equals=" + DEFAULT_INITIAL_QUANTITY,
            "initialQuantity.equals=" + UPDATED_INITIAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByInitialQuantityIsInShouldWork()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where initialQuantity in
        defaultSplitInventoryFiltering(
            "initialQuantity.in=" +
            DEFAULT_INITIAL_QUANTITY +
            "," +
            UPDATED_INITIAL_QUANTITY,
            "initialQuantity.in=" + UPDATED_INITIAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByInitialQuantityIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where initialQuantity is not null
        defaultSplitInventoryFiltering(
            "initialQuantity.specified=true",
            "initialQuantity.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByInitialQuantityIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where initialQuantity is greater than or equal to
        defaultSplitInventoryFiltering(
            "initialQuantity.greaterThanOrEqual=" + DEFAULT_INITIAL_QUANTITY,
            "initialQuantity.greaterThanOrEqual=" + UPDATED_INITIAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByInitialQuantityIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where initialQuantity is less than or equal to
        defaultSplitInventoryFiltering(
            "initialQuantity.lessThanOrEqual=" + DEFAULT_INITIAL_QUANTITY,
            "initialQuantity.lessThanOrEqual=" + SMALLER_INITIAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByInitialQuantityIsLessThanSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where initialQuantity is less than
        defaultSplitInventoryFiltering(
            "initialQuantity.lessThan=" + UPDATED_INITIAL_QUANTITY,
            "initialQuantity.lessThan=" + DEFAULT_INITIAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByInitialQuantityIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where initialQuantity is greater than
        defaultSplitInventoryFiltering(
            "initialQuantity.greaterThan=" + SMALLER_INITIAL_QUANTITY,
            "initialQuantity.greaterThan=" + DEFAULT_INITIAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMsdLevelIsEqualToSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where msdLevel equals to
        defaultSplitInventoryFiltering(
            "msdLevel.equals=" + DEFAULT_MSD_LEVEL,
            "msdLevel.equals=" + UPDATED_MSD_LEVEL
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMsdLevelIsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where msdLevel in
        defaultSplitInventoryFiltering(
            "msdLevel.in=" + DEFAULT_MSD_LEVEL + "," + UPDATED_MSD_LEVEL,
            "msdLevel.in=" + UPDATED_MSD_LEVEL
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMsdLevelIsNullOrNotNull() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where msdLevel is not null
        defaultSplitInventoryFiltering(
            "msdLevel.specified=true",
            "msdLevel.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMsdLevelContainsSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where msdLevel contains
        defaultSplitInventoryFiltering(
            "msdLevel.contains=" + DEFAULT_MSD_LEVEL,
            "msdLevel.contains=" + UPDATED_MSD_LEVEL
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMsdLevelNotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where msdLevel does not contain
        defaultSplitInventoryFiltering(
            "msdLevel.doesNotContain=" + UPDATED_MSD_LEVEL,
            "msdLevel.doesNotContain=" + DEFAULT_MSD_LEVEL
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMsdInitialFloorTimeIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where msdInitialFloorTime equals to
        defaultSplitInventoryFiltering(
            "msdInitialFloorTime.equals=" + DEFAULT_MSD_INITIAL_FLOOR_TIME,
            "msdInitialFloorTime.equals=" + UPDATED_MSD_INITIAL_FLOOR_TIME
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMsdInitialFloorTimeIsInShouldWork()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where msdInitialFloorTime in
        defaultSplitInventoryFiltering(
            "msdInitialFloorTime.in=" +
            DEFAULT_MSD_INITIAL_FLOOR_TIME +
            "," +
            UPDATED_MSD_INITIAL_FLOOR_TIME,
            "msdInitialFloorTime.in=" + UPDATED_MSD_INITIAL_FLOOR_TIME
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMsdInitialFloorTimeIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where msdInitialFloorTime is not null
        defaultSplitInventoryFiltering(
            "msdInitialFloorTime.specified=true",
            "msdInitialFloorTime.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMsdInitialFloorTimeContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where msdInitialFloorTime contains
        defaultSplitInventoryFiltering(
            "msdInitialFloorTime.contains=" + DEFAULT_MSD_INITIAL_FLOOR_TIME,
            "msdInitialFloorTime.contains=" + UPDATED_MSD_INITIAL_FLOOR_TIME
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMsdInitialFloorTimeNotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where msdInitialFloorTime does not contain
        defaultSplitInventoryFiltering(
            "msdInitialFloorTime.doesNotContain=" +
            UPDATED_MSD_INITIAL_FLOOR_TIME,
            "msdInitialFloorTime.doesNotContain=" +
            DEFAULT_MSD_INITIAL_FLOOR_TIME
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMsdBagSealDateIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where msdBagSealDate equals to
        defaultSplitInventoryFiltering(
            "msdBagSealDate.equals=" + DEFAULT_MSD_BAG_SEAL_DATE,
            "msdBagSealDate.equals=" + UPDATED_MSD_BAG_SEAL_DATE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMsdBagSealDateIsInShouldWork()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where msdBagSealDate in
        defaultSplitInventoryFiltering(
            "msdBagSealDate.in=" +
            DEFAULT_MSD_BAG_SEAL_DATE +
            "," +
            UPDATED_MSD_BAG_SEAL_DATE,
            "msdBagSealDate.in=" + UPDATED_MSD_BAG_SEAL_DATE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMsdBagSealDateIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where msdBagSealDate is not null
        defaultSplitInventoryFiltering(
            "msdBagSealDate.specified=true",
            "msdBagSealDate.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMsdBagSealDateContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where msdBagSealDate contains
        defaultSplitInventoryFiltering(
            "msdBagSealDate.contains=" + DEFAULT_MSD_BAG_SEAL_DATE,
            "msdBagSealDate.contains=" + UPDATED_MSD_BAG_SEAL_DATE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMsdBagSealDateNotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where msdBagSealDate does not contain
        defaultSplitInventoryFiltering(
            "msdBagSealDate.doesNotContain=" + UPDATED_MSD_BAG_SEAL_DATE,
            "msdBagSealDate.doesNotContain=" + DEFAULT_MSD_BAG_SEAL_DATE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMarketUsageIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where marketUsage equals to
        defaultSplitInventoryFiltering(
            "marketUsage.equals=" + DEFAULT_MARKET_USAGE,
            "marketUsage.equals=" + UPDATED_MARKET_USAGE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMarketUsageIsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where marketUsage in
        defaultSplitInventoryFiltering(
            "marketUsage.in=" +
            DEFAULT_MARKET_USAGE +
            "," +
            UPDATED_MARKET_USAGE,
            "marketUsage.in=" + UPDATED_MARKET_USAGE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMarketUsageIsNullOrNotNull() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where marketUsage is not null
        defaultSplitInventoryFiltering(
            "marketUsage.specified=true",
            "marketUsage.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMarketUsageContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where marketUsage contains
        defaultSplitInventoryFiltering(
            "marketUsage.contains=" + DEFAULT_MARKET_USAGE,
            "marketUsage.contains=" + UPDATED_MARKET_USAGE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMarketUsageNotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where marketUsage does not contain
        defaultSplitInventoryFiltering(
            "marketUsage.doesNotContain=" + UPDATED_MARKET_USAGE,
            "marketUsage.doesNotContain=" + DEFAULT_MARKET_USAGE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByQuantityOverrideIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where quantityOverride equals to
        defaultSplitInventoryFiltering(
            "quantityOverride.equals=" + DEFAULT_QUANTITY_OVERRIDE,
            "quantityOverride.equals=" + UPDATED_QUANTITY_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByQuantityOverrideIsInShouldWork()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where quantityOverride in
        defaultSplitInventoryFiltering(
            "quantityOverride.in=" +
            DEFAULT_QUANTITY_OVERRIDE +
            "," +
            UPDATED_QUANTITY_OVERRIDE,
            "quantityOverride.in=" + UPDATED_QUANTITY_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByQuantityOverrideIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where quantityOverride is not null
        defaultSplitInventoryFiltering(
            "quantityOverride.specified=true",
            "quantityOverride.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByQuantityOverrideIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where quantityOverride is greater than or equal to
        defaultSplitInventoryFiltering(
            "quantityOverride.greaterThanOrEqual=" + DEFAULT_QUANTITY_OVERRIDE,
            "quantityOverride.greaterThanOrEqual=" + UPDATED_QUANTITY_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByQuantityOverrideIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where quantityOverride is less than or equal to
        defaultSplitInventoryFiltering(
            "quantityOverride.lessThanOrEqual=" + DEFAULT_QUANTITY_OVERRIDE,
            "quantityOverride.lessThanOrEqual=" + SMALLER_QUANTITY_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByQuantityOverrideIsLessThanSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where quantityOverride is less than
        defaultSplitInventoryFiltering(
            "quantityOverride.lessThan=" + UPDATED_QUANTITY_OVERRIDE,
            "quantityOverride.lessThan=" + DEFAULT_QUANTITY_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByQuantityOverrideIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where quantityOverride is greater than
        defaultSplitInventoryFiltering(
            "quantityOverride.greaterThan=" + SMALLER_QUANTITY_OVERRIDE,
            "quantityOverride.greaterThan=" + DEFAULT_QUANTITY_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByShelfTimeIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where shelfTime equals to
        defaultSplitInventoryFiltering(
            "shelfTime.equals=" + DEFAULT_SHELF_TIME,
            "shelfTime.equals=" + UPDATED_SHELF_TIME
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByShelfTimeIsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where shelfTime in
        defaultSplitInventoryFiltering(
            "shelfTime.in=" + DEFAULT_SHELF_TIME + "," + UPDATED_SHELF_TIME,
            "shelfTime.in=" + UPDATED_SHELF_TIME
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByShelfTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where shelfTime is not null
        defaultSplitInventoryFiltering(
            "shelfTime.specified=true",
            "shelfTime.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByShelfTimeContainsSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where shelfTime contains
        defaultSplitInventoryFiltering(
            "shelfTime.contains=" + DEFAULT_SHELF_TIME,
            "shelfTime.contains=" + UPDATED_SHELF_TIME
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByShelfTimeNotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where shelfTime does not contain
        defaultSplitInventoryFiltering(
            "shelfTime.doesNotContain=" + UPDATED_SHELF_TIME,
            "shelfTime.doesNotContain=" + DEFAULT_SHELF_TIME
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesBySpMaterialNameIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where spMaterialName equals to
        defaultSplitInventoryFiltering(
            "spMaterialName.equals=" + DEFAULT_SP_MATERIAL_NAME,
            "spMaterialName.equals=" + UPDATED_SP_MATERIAL_NAME
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesBySpMaterialNameIsInShouldWork()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where spMaterialName in
        defaultSplitInventoryFiltering(
            "spMaterialName.in=" +
            DEFAULT_SP_MATERIAL_NAME +
            "," +
            UPDATED_SP_MATERIAL_NAME,
            "spMaterialName.in=" + UPDATED_SP_MATERIAL_NAME
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesBySpMaterialNameIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where spMaterialName is not null
        defaultSplitInventoryFiltering(
            "spMaterialName.specified=true",
            "spMaterialName.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesBySpMaterialNameContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where spMaterialName contains
        defaultSplitInventoryFiltering(
            "spMaterialName.contains=" + DEFAULT_SP_MATERIAL_NAME,
            "spMaterialName.contains=" + UPDATED_SP_MATERIAL_NAME
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesBySpMaterialNameNotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where spMaterialName does not contain
        defaultSplitInventoryFiltering(
            "spMaterialName.doesNotContain=" + UPDATED_SP_MATERIAL_NAME,
            "spMaterialName.doesNotContain=" + DEFAULT_SP_MATERIAL_NAME
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByWarningLimitIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where warningLimit equals to
        defaultSplitInventoryFiltering(
            "warningLimit.equals=" + DEFAULT_WARNING_LIMIT,
            "warningLimit.equals=" + UPDATED_WARNING_LIMIT
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByWarningLimitIsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where warningLimit in
        defaultSplitInventoryFiltering(
            "warningLimit.in=" +
            DEFAULT_WARNING_LIMIT +
            "," +
            UPDATED_WARNING_LIMIT,
            "warningLimit.in=" + UPDATED_WARNING_LIMIT
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByWarningLimitIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where warningLimit is not null
        defaultSplitInventoryFiltering(
            "warningLimit.specified=true",
            "warningLimit.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByWarningLimitContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where warningLimit contains
        defaultSplitInventoryFiltering(
            "warningLimit.contains=" + DEFAULT_WARNING_LIMIT,
            "warningLimit.contains=" + UPDATED_WARNING_LIMIT
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByWarningLimitNotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where warningLimit does not contain
        defaultSplitInventoryFiltering(
            "warningLimit.doesNotContain=" + UPDATED_WARNING_LIMIT,
            "warningLimit.doesNotContain=" + DEFAULT_WARNING_LIMIT
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMaximumLimitIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where maximumLimit equals to
        defaultSplitInventoryFiltering(
            "maximumLimit.equals=" + DEFAULT_MAXIMUM_LIMIT,
            "maximumLimit.equals=" + UPDATED_MAXIMUM_LIMIT
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMaximumLimitIsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where maximumLimit in
        defaultSplitInventoryFiltering(
            "maximumLimit.in=" +
            DEFAULT_MAXIMUM_LIMIT +
            "," +
            UPDATED_MAXIMUM_LIMIT,
            "maximumLimit.in=" + UPDATED_MAXIMUM_LIMIT
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMaximumLimitIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where maximumLimit is not null
        defaultSplitInventoryFiltering(
            "maximumLimit.specified=true",
            "maximumLimit.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMaximumLimitContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where maximumLimit contains
        defaultSplitInventoryFiltering(
            "maximumLimit.contains=" + DEFAULT_MAXIMUM_LIMIT,
            "maximumLimit.contains=" + UPDATED_MAXIMUM_LIMIT
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByMaximumLimitNotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where maximumLimit does not contain
        defaultSplitInventoryFiltering(
            "maximumLimit.doesNotContain=" + UPDATED_MAXIMUM_LIMIT,
            "maximumLimit.doesNotContain=" + DEFAULT_MAXIMUM_LIMIT
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByCommentsIsEqualToSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where comments equals to
        defaultSplitInventoryFiltering(
            "comments.equals=" + DEFAULT_COMMENTS,
            "comments.equals=" + UPDATED_COMMENTS
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByCommentsIsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where comments in
        defaultSplitInventoryFiltering(
            "comments.in=" + DEFAULT_COMMENTS + "," + UPDATED_COMMENTS,
            "comments.in=" + UPDATED_COMMENTS
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByCommentsIsNullOrNotNull() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where comments is not null
        defaultSplitInventoryFiltering(
            "comments.specified=true",
            "comments.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByCommentsContainsSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where comments contains
        defaultSplitInventoryFiltering(
            "comments.contains=" + DEFAULT_COMMENTS,
            "comments.contains=" + UPDATED_COMMENTS
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByCommentsNotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where comments does not contain
        defaultSplitInventoryFiltering(
            "comments.doesNotContain=" + UPDATED_COMMENTS,
            "comments.doesNotContain=" + DEFAULT_COMMENTS
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByWarmupTimeIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where warmupTime equals to
        defaultSplitInventoryFiltering(
            "warmupTime.equals=" + DEFAULT_WARMUP_TIME,
            "warmupTime.equals=" + UPDATED_WARMUP_TIME
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByWarmupTimeIsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where warmupTime in
        defaultSplitInventoryFiltering(
            "warmupTime.in=" + DEFAULT_WARMUP_TIME + "," + UPDATED_WARMUP_TIME,
            "warmupTime.in=" + UPDATED_WARMUP_TIME
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByWarmupTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where warmupTime is not null
        defaultSplitInventoryFiltering(
            "warmupTime.specified=true",
            "warmupTime.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByWarmupTimeContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where warmupTime contains
        defaultSplitInventoryFiltering(
            "warmupTime.contains=" + DEFAULT_WARMUP_TIME,
            "warmupTime.contains=" + UPDATED_WARMUP_TIME
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByWarmupTimeNotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where warmupTime does not contain
        defaultSplitInventoryFiltering(
            "warmupTime.doesNotContain=" + UPDATED_WARMUP_TIME,
            "warmupTime.doesNotContain=" + DEFAULT_WARMUP_TIME
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByStorageUnitIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where storageUnit equals to
        defaultSplitInventoryFiltering(
            "storageUnit.equals=" + DEFAULT_STORAGE_UNIT,
            "storageUnit.equals=" + UPDATED_STORAGE_UNIT
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByStorageUnitIsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where storageUnit in
        defaultSplitInventoryFiltering(
            "storageUnit.in=" +
            DEFAULT_STORAGE_UNIT +
            "," +
            UPDATED_STORAGE_UNIT,
            "storageUnit.in=" + UPDATED_STORAGE_UNIT
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByStorageUnitIsNullOrNotNull() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where storageUnit is not null
        defaultSplitInventoryFiltering(
            "storageUnit.specified=true",
            "storageUnit.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByStorageUnitContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where storageUnit contains
        defaultSplitInventoryFiltering(
            "storageUnit.contains=" + DEFAULT_STORAGE_UNIT,
            "storageUnit.contains=" + UPDATED_STORAGE_UNIT
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByStorageUnitNotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where storageUnit does not contain
        defaultSplitInventoryFiltering(
            "storageUnit.doesNotContain=" + UPDATED_STORAGE_UNIT,
            "storageUnit.doesNotContain=" + DEFAULT_STORAGE_UNIT
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesBySubStorageUnitIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where subStorageUnit equals to
        defaultSplitInventoryFiltering(
            "subStorageUnit.equals=" + DEFAULT_SUB_STORAGE_UNIT,
            "subStorageUnit.equals=" + UPDATED_SUB_STORAGE_UNIT
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesBySubStorageUnitIsInShouldWork()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where subStorageUnit in
        defaultSplitInventoryFiltering(
            "subStorageUnit.in=" +
            DEFAULT_SUB_STORAGE_UNIT +
            "," +
            UPDATED_SUB_STORAGE_UNIT,
            "subStorageUnit.in=" + UPDATED_SUB_STORAGE_UNIT
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesBySubStorageUnitIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where subStorageUnit is not null
        defaultSplitInventoryFiltering(
            "subStorageUnit.specified=true",
            "subStorageUnit.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesBySubStorageUnitContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where subStorageUnit contains
        defaultSplitInventoryFiltering(
            "subStorageUnit.contains=" + DEFAULT_SUB_STORAGE_UNIT,
            "subStorageUnit.contains=" + UPDATED_SUB_STORAGE_UNIT
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesBySubStorageUnitNotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where subStorageUnit does not contain
        defaultSplitInventoryFiltering(
            "subStorageUnit.doesNotContain=" + UPDATED_SUB_STORAGE_UNIT,
            "subStorageUnit.doesNotContain=" + DEFAULT_SUB_STORAGE_UNIT
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByLocationOverrideIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where locationOverride equals to
        defaultSplitInventoryFiltering(
            "locationOverride.equals=" + DEFAULT_LOCATION_OVERRIDE,
            "locationOverride.equals=" + UPDATED_LOCATION_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByLocationOverrideIsInShouldWork()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where locationOverride in
        defaultSplitInventoryFiltering(
            "locationOverride.in=" +
            DEFAULT_LOCATION_OVERRIDE +
            "," +
            UPDATED_LOCATION_OVERRIDE,
            "locationOverride.in=" + UPDATED_LOCATION_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByLocationOverrideIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where locationOverride is not null
        defaultSplitInventoryFiltering(
            "locationOverride.specified=true",
            "locationOverride.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByLocationOverrideContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where locationOverride contains
        defaultSplitInventoryFiltering(
            "locationOverride.contains=" + DEFAULT_LOCATION_OVERRIDE,
            "locationOverride.contains=" + UPDATED_LOCATION_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByLocationOverrideNotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where locationOverride does not contain
        defaultSplitInventoryFiltering(
            "locationOverride.doesNotContain=" + UPDATED_LOCATION_OVERRIDE,
            "locationOverride.doesNotContain=" + DEFAULT_LOCATION_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByExpirationDateIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where expirationDate equals to
        defaultSplitInventoryFiltering(
            "expirationDate.equals=" + DEFAULT_EXPIRATION_DATE,
            "expirationDate.equals=" + UPDATED_EXPIRATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByExpirationDateIsInShouldWork()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where expirationDate in
        defaultSplitInventoryFiltering(
            "expirationDate.in=" +
            DEFAULT_EXPIRATION_DATE +
            "," +
            UPDATED_EXPIRATION_DATE,
            "expirationDate.in=" + UPDATED_EXPIRATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByExpirationDateIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where expirationDate is not null
        defaultSplitInventoryFiltering(
            "expirationDate.specified=true",
            "expirationDate.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByExpirationDateContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where expirationDate contains
        defaultSplitInventoryFiltering(
            "expirationDate.contains=" + DEFAULT_EXPIRATION_DATE,
            "expirationDate.contains=" + UPDATED_EXPIRATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByExpirationDateNotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where expirationDate does not contain
        defaultSplitInventoryFiltering(
            "expirationDate.doesNotContain=" + UPDATED_EXPIRATION_DATE,
            "expirationDate.doesNotContain=" + DEFAULT_EXPIRATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByManufacturingDateIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where manufacturingDate equals to
        defaultSplitInventoryFiltering(
            "manufacturingDate.equals=" + DEFAULT_MANUFACTURING_DATE,
            "manufacturingDate.equals=" + UPDATED_MANUFACTURING_DATE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByManufacturingDateIsInShouldWork()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where manufacturingDate in
        defaultSplitInventoryFiltering(
            "manufacturingDate.in=" +
            DEFAULT_MANUFACTURING_DATE +
            "," +
            UPDATED_MANUFACTURING_DATE,
            "manufacturingDate.in=" + UPDATED_MANUFACTURING_DATE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByManufacturingDateIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where manufacturingDate is not null
        defaultSplitInventoryFiltering(
            "manufacturingDate.specified=true",
            "manufacturingDate.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByManufacturingDateContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where manufacturingDate contains
        defaultSplitInventoryFiltering(
            "manufacturingDate.contains=" + DEFAULT_MANUFACTURING_DATE,
            "manufacturingDate.contains=" + UPDATED_MANUFACTURING_DATE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByManufacturingDateNotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where manufacturingDate does not contain
        defaultSplitInventoryFiltering(
            "manufacturingDate.doesNotContain=" + UPDATED_MANUFACTURING_DATE,
            "manufacturingDate.doesNotContain=" + DEFAULT_MANUFACTURING_DATE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByPartClassIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where partClass equals to
        defaultSplitInventoryFiltering(
            "partClass.equals=" + DEFAULT_PART_CLASS,
            "partClass.equals=" + UPDATED_PART_CLASS
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByPartClassIsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where partClass in
        defaultSplitInventoryFiltering(
            "partClass.in=" + DEFAULT_PART_CLASS + "," + UPDATED_PART_CLASS,
            "partClass.in=" + UPDATED_PART_CLASS
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByPartClassIsNullOrNotNull() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where partClass is not null
        defaultSplitInventoryFiltering(
            "partClass.specified=true",
            "partClass.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByPartClassContainsSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where partClass contains
        defaultSplitInventoryFiltering(
            "partClass.contains=" + DEFAULT_PART_CLASS,
            "partClass.contains=" + UPDATED_PART_CLASS
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByPartClassNotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where partClass does not contain
        defaultSplitInventoryFiltering(
            "partClass.doesNotContain=" + UPDATED_PART_CLASS,
            "partClass.doesNotContain=" + DEFAULT_PART_CLASS
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesBySapCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where sapCode equals to
        defaultSplitInventoryFiltering(
            "sapCode.equals=" + DEFAULT_SAP_CODE,
            "sapCode.equals=" + UPDATED_SAP_CODE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesBySapCodeIsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where sapCode in
        defaultSplitInventoryFiltering(
            "sapCode.in=" + DEFAULT_SAP_CODE + "," + UPDATED_SAP_CODE,
            "sapCode.in=" + UPDATED_SAP_CODE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesBySapCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where sapCode is not null
        defaultSplitInventoryFiltering(
            "sapCode.specified=true",
            "sapCode.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesBySapCodeContainsSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where sapCode contains
        defaultSplitInventoryFiltering(
            "sapCode.contains=" + DEFAULT_SAP_CODE,
            "sapCode.contains=" + UPDATED_SAP_CODE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesBySapCodeNotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where sapCode does not contain
        defaultSplitInventoryFiltering(
            "sapCode.doesNotContain=" + UPDATED_SAP_CODE,
            "sapCode.doesNotContain=" + DEFAULT_SAP_CODE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByVendorQrCodeIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where vendorQrCode equals to
        defaultSplitInventoryFiltering(
            "vendorQrCode.equals=" + DEFAULT_VENDOR_QR_CODE,
            "vendorQrCode.equals=" + UPDATED_VENDOR_QR_CODE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByVendorQrCodeIsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where vendorQrCode in
        defaultSplitInventoryFiltering(
            "vendorQrCode.in=" +
            DEFAULT_VENDOR_QR_CODE +
            "," +
            UPDATED_VENDOR_QR_CODE,
            "vendorQrCode.in=" + UPDATED_VENDOR_QR_CODE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByVendorQrCodeIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where vendorQrCode is not null
        defaultSplitInventoryFiltering(
            "vendorQrCode.specified=true",
            "vendorQrCode.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByVendorQrCodeContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where vendorQrCode contains
        defaultSplitInventoryFiltering(
            "vendorQrCode.contains=" + DEFAULT_VENDOR_QR_CODE,
            "vendorQrCode.contains=" + UPDATED_VENDOR_QR_CODE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByVendorQrCodeNotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where vendorQrCode does not contain
        defaultSplitInventoryFiltering(
            "vendorQrCode.doesNotContain=" + UPDATED_VENDOR_QR_CODE,
            "vendorQrCode.doesNotContain=" + DEFAULT_VENDOR_QR_CODE
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where status equals to
        defaultSplitInventoryFiltering(
            "status.equals=" + DEFAULT_STATUS,
            "status.equals=" + UPDATED_STATUS
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where status in
        defaultSplitInventoryFiltering(
            "status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS,
            "status.in=" + UPDATED_STATUS
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where status is not null
        defaultSplitInventoryFiltering(
            "status.specified=true",
            "status.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByStatusContainsSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where status contains
        defaultSplitInventoryFiltering(
            "status.contains=" + DEFAULT_STATUS,
            "status.contains=" + UPDATED_STATUS
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where status does not contain
        defaultSplitInventoryFiltering(
            "status.doesNotContain=" + UPDATED_STATUS,
            "status.doesNotContain=" + DEFAULT_STATUS
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByCreatedByIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where createdBy equals to
        defaultSplitInventoryFiltering(
            "createdBy.equals=" + DEFAULT_CREATED_BY,
            "createdBy.equals=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where createdBy in
        defaultSplitInventoryFiltering(
            "createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY,
            "createdBy.in=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where createdBy is not null
        defaultSplitInventoryFiltering(
            "createdBy.specified=true",
            "createdBy.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where createdBy contains
        defaultSplitInventoryFiltering(
            "createdBy.contains=" + DEFAULT_CREATED_BY,
            "createdBy.contains=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByCreatedByNotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where createdBy does not contain
        defaultSplitInventoryFiltering(
            "createdBy.doesNotContain=" + UPDATED_CREATED_BY,
            "createdBy.doesNotContain=" + DEFAULT_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByCreatedAtIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where createdAt equals to
        defaultSplitInventoryFiltering(
            "createdAt.equals=" + DEFAULT_CREATED_AT,
            "createdAt.equals=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where createdAt in
        defaultSplitInventoryFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where createdAt is not null
        defaultSplitInventoryFiltering(
            "createdAt.specified=true",
            "createdAt.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUpdatedByIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where updatedBy equals to
        defaultSplitInventoryFiltering(
            "updatedBy.equals=" + DEFAULT_UPDATED_BY,
            "updatedBy.equals=" + UPDATED_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUpdatedByIsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where updatedBy in
        defaultSplitInventoryFiltering(
            "updatedBy.in=" + DEFAULT_UPDATED_BY + "," + UPDATED_UPDATED_BY,
            "updatedBy.in=" + UPDATED_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUpdatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where updatedBy is not null
        defaultSplitInventoryFiltering(
            "updatedBy.specified=true",
            "updatedBy.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUpdatedByContainsSomething() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where updatedBy contains
        defaultSplitInventoryFiltering(
            "updatedBy.contains=" + DEFAULT_UPDATED_BY,
            "updatedBy.contains=" + UPDATED_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUpdatedByNotContainsSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where updatedBy does not contain
        defaultSplitInventoryFiltering(
            "updatedBy.doesNotContain=" + UPDATED_UPDATED_BY,
            "updatedBy.doesNotContain=" + DEFAULT_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUpdatedAtIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where updatedAt equals to
        defaultSplitInventoryFiltering(
            "updatedAt.equals=" + DEFAULT_UPDATED_AT,
            "updatedAt.equals=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where updatedAt in
        defaultSplitInventoryFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where updatedAt is not null
        defaultSplitInventoryFiltering(
            "updatedAt.specified=true",
            "updatedAt.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByPrintStatusIsEqualToSomething()
        throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where printStatus equals to
        defaultSplitInventoryFiltering(
            "printStatus.equals=" + DEFAULT_PRINT_STATUS,
            "printStatus.equals=" + UPDATED_PRINT_STATUS
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByPrintStatusIsInShouldWork() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where printStatus in
        defaultSplitInventoryFiltering(
            "printStatus.in=" +
            DEFAULT_PRINT_STATUS +
            "," +
            UPDATED_PRINT_STATUS,
            "printStatus.in=" + UPDATED_PRINT_STATUS
        );
    }

    @Test
    @Transactional
    void getAllSplitInventoriesByPrintStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        // Get all the splitInventoryList where printStatus is not null
        defaultSplitInventoryFiltering(
            "printStatus.specified=true",
            "printStatus.specified=false"
        );
    }

    private void defaultSplitInventoryFiltering(
        String shouldBeFound,
        String shouldNotBeFound
    ) throws Exception {
        defaultSplitInventoryShouldBeFound(shouldBeFound);
        defaultSplitInventoryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSplitInventoryShouldBeFound(String filter)
        throws Exception {
        restSplitInventoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(splitInventory.getId().intValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].businessSourceId").value(
                    hasItem(DEFAULT_BUSINESS_SOURCE_ID.intValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].businessType").value(
                    hasItem(DEFAULT_BUSINESS_TYPE)
                )
            )
            .andExpect(jsonPath("$.[*].reelId").value(hasItem(DEFAULT_REEL_ID)))
            .andExpect(
                jsonPath("$.[*].parentMaterialIdentifier").value(
                    hasItem(DEFAULT_PARENT_MATERIAL_IDENTIFIER)
                )
            )
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
                    hasItem(DEFAULT_CREATED_AT.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY))
            )
            .andExpect(
                jsonPath("$.[*].updatedAt").value(
                    hasItem(DEFAULT_UPDATED_AT.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].printStatus").value(
                    hasItem(DEFAULT_PRINT_STATUS.booleanValue())
                )
            );

        // Check, that the count call also returns 1
        restSplitInventoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSplitInventoryShouldNotBeFound(String filter)
        throws Exception {
        restSplitInventoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSplitInventoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSplitInventory() throws Exception {
        // Get the splitInventory
        restSplitInventoryMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSplitInventory() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the splitInventory
        SplitInventory updatedSplitInventory = splitInventoryRepository
            .findById(splitInventory.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedSplitInventory are not directly saved in db
        em.detach(updatedSplitInventory);
        updatedSplitInventory
            .businessSourceId(UPDATED_BUSINESS_SOURCE_ID)
            .businessType(UPDATED_BUSINESS_TYPE)
            .reelId(UPDATED_REEL_ID)
            .parentMaterialIdentifier(UPDATED_PARENT_MATERIAL_IDENTIFIER)
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
            .updatedAt(UPDATED_UPDATED_AT)
            .printStatus(UPDATED_PRINT_STATUS);
        SplitInventoryDTO splitInventoryDTO = splitInventoryMapper.toDto(
            updatedSplitInventory
        );

        restSplitInventoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, splitInventoryDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(splitInventoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the SplitInventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSplitInventoryToMatchAllProperties(
            updatedSplitInventory
        );
    }

    @Test
    @Transactional
    void putNonExistingSplitInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        splitInventory.setId(longCount.incrementAndGet());

        // Create the SplitInventory
        SplitInventoryDTO splitInventoryDTO = splitInventoryMapper.toDto(
            splitInventory
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSplitInventoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, splitInventoryDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(splitInventoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SplitInventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSplitInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        splitInventory.setId(longCount.incrementAndGet());

        // Create the SplitInventory
        SplitInventoryDTO splitInventoryDTO = splitInventoryMapper.toDto(
            splitInventory
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSplitInventoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(splitInventoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SplitInventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSplitInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        splitInventory.setId(longCount.incrementAndGet());

        // Create the SplitInventory
        SplitInventoryDTO splitInventoryDTO = splitInventoryMapper.toDto(
            splitInventory
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSplitInventoryMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(splitInventoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SplitInventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSplitInventoryWithPatch() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the splitInventory using partial update
        SplitInventory partialUpdatedSplitInventory = new SplitInventory();
        partialUpdatedSplitInventory.setId(splitInventory.getId());

        partialUpdatedSplitInventory
            .reelId(UPDATED_REEL_ID)
            .partNumber(UPDATED_PART_NUMBER)
            .lot(UPDATED_LOT)
            .userData1(UPDATED_USER_DATA_1)
            .userData2(UPDATED_USER_DATA_2)
            .initialQuantity(UPDATED_INITIAL_QUANTITY)
            .msdLevel(UPDATED_MSD_LEVEL)
            .msdInitialFloorTime(UPDATED_MSD_INITIAL_FLOOR_TIME)
            .msdBagSealDate(UPDATED_MSD_BAG_SEAL_DATE)
            .quantityOverride(UPDATED_QUANTITY_OVERRIDE)
            .shelfTime(UPDATED_SHELF_TIME)
            .maximumLimit(UPDATED_MAXIMUM_LIMIT)
            .comments(UPDATED_COMMENTS)
            .warmupTime(UPDATED_WARMUP_TIME)
            .storageUnit(UPDATED_STORAGE_UNIT)
            .locationOverride(UPDATED_LOCATION_OVERRIDE)
            .expirationDate(UPDATED_EXPIRATION_DATE)
            .vendorQrCode(UPDATED_VENDOR_QR_CODE)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT);

        restSplitInventoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSplitInventory.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSplitInventory))
            )
            .andExpect(status().isOk());

        // Validate the SplitInventory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSplitInventoryUpdatableFieldsEquals(
            createUpdateProxyForBean(
                partialUpdatedSplitInventory,
                splitInventory
            ),
            getPersistedSplitInventory(splitInventory)
        );
    }

    @Test
    @Transactional
    void fullUpdateSplitInventoryWithPatch() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the splitInventory using partial update
        SplitInventory partialUpdatedSplitInventory = new SplitInventory();
        partialUpdatedSplitInventory.setId(splitInventory.getId());

        partialUpdatedSplitInventory
            .businessSourceId(UPDATED_BUSINESS_SOURCE_ID)
            .businessType(UPDATED_BUSINESS_TYPE)
            .reelId(UPDATED_REEL_ID)
            .parentMaterialIdentifier(UPDATED_PARENT_MATERIAL_IDENTIFIER)
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
            .updatedAt(UPDATED_UPDATED_AT)
            .printStatus(UPDATED_PRINT_STATUS);

        restSplitInventoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSplitInventory.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSplitInventory))
            )
            .andExpect(status().isOk());

        // Validate the SplitInventory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSplitInventoryUpdatableFieldsEquals(
            partialUpdatedSplitInventory,
            getPersistedSplitInventory(partialUpdatedSplitInventory)
        );
    }

    @Test
    @Transactional
    void patchNonExistingSplitInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        splitInventory.setId(longCount.incrementAndGet());

        // Create the SplitInventory
        SplitInventoryDTO splitInventoryDTO = splitInventoryMapper.toDto(
            splitInventory
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSplitInventoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, splitInventoryDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(splitInventoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SplitInventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSplitInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        splitInventory.setId(longCount.incrementAndGet());

        // Create the SplitInventory
        SplitInventoryDTO splitInventoryDTO = splitInventoryMapper.toDto(
            splitInventory
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSplitInventoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(splitInventoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SplitInventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSplitInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        splitInventory.setId(longCount.incrementAndGet());

        // Create the SplitInventory
        SplitInventoryDTO splitInventoryDTO = splitInventoryMapper.toDto(
            splitInventory
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSplitInventoryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(splitInventoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SplitInventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSplitInventory() throws Exception {
        // Initialize the database
        splitInventoryRepository.saveAndFlush(splitInventory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the splitInventory
        restSplitInventoryMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, splitInventory.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return splitInventoryRepository.count();
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

    protected SplitInventory getPersistedSplitInventory(
        SplitInventory splitInventory
    ) {
        return splitInventoryRepository
            .findById(splitInventory.getId())
            .orElseThrow();
    }

    protected void assertPersistedSplitInventoryToMatchAllProperties(
        SplitInventory expectedSplitInventory
    ) {
        assertSplitInventoryAllPropertiesEquals(
            expectedSplitInventory,
            getPersistedSplitInventory(expectedSplitInventory)
        );
    }

    protected void assertPersistedSplitInventoryToMatchUpdatableProperties(
        SplitInventory expectedSplitInventory
    ) {
        assertSplitInventoryAllUpdatablePropertiesEquals(
            expectedSplitInventory,
            getPersistedSplitInventory(expectedSplitInventory)
        );
    }
}
