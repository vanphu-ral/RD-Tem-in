package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.WarehouseTransferDetailAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.WarehouseTransferDetail;
import com.mycompany.myapp.repository.WarehouseTransferDetailRepository;
import com.mycompany.myapp.service.dto.WarehouseTransferDetailDTO;
import com.mycompany.myapp.service.mapper.WarehouseTransferDetailMapper;
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
 * Integration tests for the {@link WarehouseTransferDetailResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WarehouseTransferDetailResourceIT {

    private static final Long DEFAULT_LINE_NUM = 1L;
    private static final Long UPDATED_LINE_NUM = 2L;

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

    private static final Boolean DEFAULT_PANA_SEND_STATUS = false;
    private static final Boolean UPDATED_PANA_SEND_STATUS = true;

    private static final Boolean DEFAULT_SAP_SEND_STATUS = false;
    private static final Boolean UPDATED_SAP_SEND_STATUS = true;

    private static final String ENTITY_API_URL =
        "/api/warehouse-transfer-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WarehouseTransferDetailRepository warehouseTransferDetailRepository;

    @Autowired
    private WarehouseTransferDetailMapper warehouseTransferDetailMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWarehouseTransferDetailMockMvc;

    private WarehouseTransferDetail warehouseTransferDetail;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WarehouseTransferDetail createEntity(EntityManager em) {
        WarehouseTransferDetail warehouseTransferDetail =
            new WarehouseTransferDetail()
                .lineNum(DEFAULT_LINE_NUM)
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
                .updatedAt(DEFAULT_UPDATED_AT)
                .panaSendStatus(DEFAULT_PANA_SEND_STATUS)
                .sapSendStatus(DEFAULT_SAP_SEND_STATUS);
        return warehouseTransferDetail;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WarehouseTransferDetail createUpdatedEntity(
        EntityManager em
    ) {
        WarehouseTransferDetail warehouseTransferDetail =
            new WarehouseTransferDetail()
                .lineNum(UPDATED_LINE_NUM)
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
                .updatedAt(UPDATED_UPDATED_AT)
                .panaSendStatus(UPDATED_PANA_SEND_STATUS)
                .sapSendStatus(UPDATED_SAP_SEND_STATUS);
        return warehouseTransferDetail;
    }

    @BeforeEach
    public void initTest() {
        warehouseTransferDetail = createEntity(em);
    }

    @Test
    @Transactional
    void createWarehouseTransferDetail() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the WarehouseTransferDetail
        WarehouseTransferDetailDTO warehouseTransferDetailDTO =
            warehouseTransferDetailMapper.toDto(warehouseTransferDetail);
        var returnedWarehouseTransferDetailDTO = om.readValue(
            restWarehouseTransferDetailMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            om.writeValueAsBytes(warehouseTransferDetailDTO)
                        )
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            WarehouseTransferDetailDTO.class
        );

        // Validate the WarehouseTransferDetail in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWarehouseTransferDetail =
            warehouseTransferDetailMapper.toEntity(
                returnedWarehouseTransferDetailDTO
            );
        assertWarehouseTransferDetailUpdatableFieldsEquals(
            returnedWarehouseTransferDetail,
            getPersistedWarehouseTransferDetail(returnedWarehouseTransferDetail)
        );
    }

    @Test
    @Transactional
    void createWarehouseTransferDetailWithExistingId() throws Exception {
        // Create the WarehouseTransferDetail with an existing ID
        warehouseTransferDetail.setId(1L);
        WarehouseTransferDetailDTO warehouseTransferDetailDTO =
            warehouseTransferDetailMapper.toDto(warehouseTransferDetail);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWarehouseTransferDetailMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseTransferDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WarehouseTransferDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllWarehouseTransferDetails() throws Exception {
        // Initialize the database
        warehouseTransferDetailRepository.saveAndFlush(warehouseTransferDetail);

        // Get all the warehouseTransferDetailList
        restWarehouseTransferDetailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(warehouseTransferDetail.getId().intValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].lineNum").value(
                    hasItem(DEFAULT_LINE_NUM.intValue())
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
                    hasItem(DEFAULT_VENDOR_QR_CODE.toString())
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
                jsonPath("$.[*].panaSendStatus").value(
                    hasItem(DEFAULT_PANA_SEND_STATUS.booleanValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].sapSendStatus").value(
                    hasItem(DEFAULT_SAP_SEND_STATUS.booleanValue())
                )
            );
    }

    @Test
    @Transactional
    void getWarehouseTransferDetail() throws Exception {
        // Initialize the database
        warehouseTransferDetailRepository.saveAndFlush(warehouseTransferDetail);

        // Get the warehouseTransferDetail
        restWarehouseTransferDetailMockMvc
            .perform(get(ENTITY_API_URL_ID, warehouseTransferDetail.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.id").value(
                    warehouseTransferDetail.getId().intValue()
                )
            )
            .andExpect(jsonPath("$.lineNum").value(DEFAULT_LINE_NUM.intValue()))
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
            .andExpect(
                jsonPath("$.vendorQrCode").value(
                    DEFAULT_VENDOR_QR_CODE.toString()
                )
            )
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
                jsonPath("$.panaSendStatus").value(
                    DEFAULT_PANA_SEND_STATUS.booleanValue()
                )
            )
            .andExpect(
                jsonPath("$.sapSendStatus").value(
                    DEFAULT_SAP_SEND_STATUS.booleanValue()
                )
            );
    }

    @Test
    @Transactional
    void getNonExistingWarehouseTransferDetail() throws Exception {
        // Get the warehouseTransferDetail
        restWarehouseTransferDetailMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWarehouseTransferDetail() throws Exception {
        // Initialize the database
        warehouseTransferDetailRepository.saveAndFlush(warehouseTransferDetail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the warehouseTransferDetail
        WarehouseTransferDetail updatedWarehouseTransferDetail =
            warehouseTransferDetailRepository
                .findById(warehouseTransferDetail.getId())
                .orElseThrow();
        // Disconnect from session so that the updates on updatedWarehouseTransferDetail are not directly saved in db
        em.detach(updatedWarehouseTransferDetail);
        updatedWarehouseTransferDetail
            .lineNum(UPDATED_LINE_NUM)
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
            .updatedAt(UPDATED_UPDATED_AT)
            .panaSendStatus(UPDATED_PANA_SEND_STATUS)
            .sapSendStatus(UPDATED_SAP_SEND_STATUS);
        WarehouseTransferDetailDTO warehouseTransferDetailDTO =
            warehouseTransferDetailMapper.toDto(updatedWarehouseTransferDetail);

        restWarehouseTransferDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, warehouseTransferDetailDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseTransferDetailDTO))
            )
            .andExpect(status().isOk());

        // Validate the WarehouseTransferDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWarehouseTransferDetailToMatchAllProperties(
            updatedWarehouseTransferDetail
        );
    }

    @Test
    @Transactional
    void putNonExistingWarehouseTransferDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseTransferDetail.setId(longCount.incrementAndGet());

        // Create the WarehouseTransferDetail
        WarehouseTransferDetailDTO warehouseTransferDetailDTO =
            warehouseTransferDetailMapper.toDto(warehouseTransferDetail);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWarehouseTransferDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, warehouseTransferDetailDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseTransferDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WarehouseTransferDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWarehouseTransferDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseTransferDetail.setId(longCount.incrementAndGet());

        // Create the WarehouseTransferDetail
        WarehouseTransferDetailDTO warehouseTransferDetailDTO =
            warehouseTransferDetailMapper.toDto(warehouseTransferDetail);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseTransferDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseTransferDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WarehouseTransferDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWarehouseTransferDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseTransferDetail.setId(longCount.incrementAndGet());

        // Create the WarehouseTransferDetail
        WarehouseTransferDetailDTO warehouseTransferDetailDTO =
            warehouseTransferDetailMapper.toDto(warehouseTransferDetail);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseTransferDetailMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseTransferDetailDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the WarehouseTransferDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWarehouseTransferDetailWithPatch() throws Exception {
        // Initialize the database
        warehouseTransferDetailRepository.saveAndFlush(warehouseTransferDetail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the warehouseTransferDetail using partial update
        WarehouseTransferDetail partialUpdatedWarehouseTransferDetail =
            new WarehouseTransferDetail();
        partialUpdatedWarehouseTransferDetail.setId(
            warehouseTransferDetail.getId()
        );

        partialUpdatedWarehouseTransferDetail
            .lineNum(UPDATED_LINE_NUM)
            .reelId(UPDATED_REEL_ID)
            .vendor(UPDATED_VENDOR)
            .lot(UPDATED_LOT)
            .userData2(UPDATED_USER_DATA_2)
            .userData4(UPDATED_USER_DATA_4)
            .initialQuantity(UPDATED_INITIAL_QUANTITY)
            .msdInitialFloorTime(UPDATED_MSD_INITIAL_FLOOR_TIME)
            .marketUsage(UPDATED_MARKET_USAGE)
            .shelfTime(UPDATED_SHELF_TIME)
            .maximumLimit(UPDATED_MAXIMUM_LIMIT)
            .warmupTime(UPDATED_WARMUP_TIME)
            .expirationDate(UPDATED_EXPIRATION_DATE)
            .vendorQrCode(UPDATED_VENDOR_QR_CODE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .panaSendStatus(UPDATED_PANA_SEND_STATUS)
            .sapSendStatus(UPDATED_SAP_SEND_STATUS);

        restWarehouseTransferDetailMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    partialUpdatedWarehouseTransferDetail.getId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(
                            partialUpdatedWarehouseTransferDetail
                        )
                    )
            )
            .andExpect(status().isOk());

        // Validate the WarehouseTransferDetail in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWarehouseTransferDetailUpdatableFieldsEquals(
            createUpdateProxyForBean(
                partialUpdatedWarehouseTransferDetail,
                warehouseTransferDetail
            ),
            getPersistedWarehouseTransferDetail(warehouseTransferDetail)
        );
    }

    @Test
    @Transactional
    void fullUpdateWarehouseTransferDetailWithPatch() throws Exception {
        // Initialize the database
        warehouseTransferDetailRepository.saveAndFlush(warehouseTransferDetail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the warehouseTransferDetail using partial update
        WarehouseTransferDetail partialUpdatedWarehouseTransferDetail =
            new WarehouseTransferDetail();
        partialUpdatedWarehouseTransferDetail.setId(
            warehouseTransferDetail.getId()
        );

        partialUpdatedWarehouseTransferDetail
            .lineNum(UPDATED_LINE_NUM)
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
            .updatedAt(UPDATED_UPDATED_AT)
            .panaSendStatus(UPDATED_PANA_SEND_STATUS)
            .sapSendStatus(UPDATED_SAP_SEND_STATUS);

        restWarehouseTransferDetailMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    partialUpdatedWarehouseTransferDetail.getId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(
                            partialUpdatedWarehouseTransferDetail
                        )
                    )
            )
            .andExpect(status().isOk());

        // Validate the WarehouseTransferDetail in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWarehouseTransferDetailUpdatableFieldsEquals(
            partialUpdatedWarehouseTransferDetail,
            getPersistedWarehouseTransferDetail(
                partialUpdatedWarehouseTransferDetail
            )
        );
    }

    @Test
    @Transactional
    void patchNonExistingWarehouseTransferDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseTransferDetail.setId(longCount.incrementAndGet());

        // Create the WarehouseTransferDetail
        WarehouseTransferDetailDTO warehouseTransferDetailDTO =
            warehouseTransferDetailMapper.toDto(warehouseTransferDetail);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWarehouseTransferDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, warehouseTransferDetailDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(warehouseTransferDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WarehouseTransferDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWarehouseTransferDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseTransferDetail.setId(longCount.incrementAndGet());

        // Create the WarehouseTransferDetail
        WarehouseTransferDetailDTO warehouseTransferDetailDTO =
            warehouseTransferDetailMapper.toDto(warehouseTransferDetail);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseTransferDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(warehouseTransferDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WarehouseTransferDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWarehouseTransferDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseTransferDetail.setId(longCount.incrementAndGet());

        // Create the WarehouseTransferDetail
        WarehouseTransferDetailDTO warehouseTransferDetailDTO =
            warehouseTransferDetailMapper.toDto(warehouseTransferDetail);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseTransferDetailMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(warehouseTransferDetailDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the WarehouseTransferDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWarehouseTransferDetail() throws Exception {
        // Initialize the database
        warehouseTransferDetailRepository.saveAndFlush(warehouseTransferDetail);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the warehouseTransferDetail
        restWarehouseTransferDetailMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, warehouseTransferDetail.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return warehouseTransferDetailRepository.count();
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

    protected WarehouseTransferDetail getPersistedWarehouseTransferDetail(
        WarehouseTransferDetail warehouseTransferDetail
    ) {
        return warehouseTransferDetailRepository
            .findById(warehouseTransferDetail.getId())
            .orElseThrow();
    }

    protected void assertPersistedWarehouseTransferDetailToMatchAllProperties(
        WarehouseTransferDetail expectedWarehouseTransferDetail
    ) {
        assertWarehouseTransferDetailAllPropertiesEquals(
            expectedWarehouseTransferDetail,
            getPersistedWarehouseTransferDetail(expectedWarehouseTransferDetail)
        );
    }

    protected void assertPersistedWarehouseTransferDetailToMatchUpdatableProperties(
        WarehouseTransferDetail expectedWarehouseTransferDetail
    ) {
        assertWarehouseTransferDetailAllUpdatablePropertiesEquals(
            expectedWarehouseTransferDetail,
            getPersistedWarehouseTransferDetail(expectedWarehouseTransferDetail)
        );
    }
}
