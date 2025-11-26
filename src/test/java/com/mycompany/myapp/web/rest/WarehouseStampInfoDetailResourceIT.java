package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.WarehouseStampInfoDetailAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.WarehouseStampInfoDetail;
import com.mycompany.myapp.repository.WarehouseStampInfoDetailRepository;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDetailDTO;
import com.mycompany.myapp.service.mapper.WarehouseStampInfoDetailMapper;
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
 * Integration tests for the {@link WarehouseStampInfoDetailResource} REST
 * controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WarehouseStampInfoDetailResourceIT {

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

    private static final Long DEFAULT_LENH_SAN_XUAT_ID = 1L;
    private static final Long UPDATED_LENH_SAN_XUAT_ID = 2L;

    private static final String DEFAULT_TRANG_THAI = "AAAAAAAAAA";
    private static final String UPDATED_TRANG_THAI = "BBBBBBBBBB";

    private static final Integer DEFAULT_CHECKED = 1;
    private static final Integer UPDATED_CHECKED = 2;

    private static final String ENTITY_API_URL =
        "/api/warehouse-note-info-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WarehouseStampInfoDetailRepository warehouseStampInfoDetailRepository;

    @Autowired
    private WarehouseStampInfoDetailMapper warehouseStampInfoDetailMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWarehouseStampInfoDetailMockMvc;

    private WarehouseStampInfoDetail warehouseStampInfoDetail;

    private WarehouseStampInfoDetail insertedWarehouseStampInfoDetail;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WarehouseStampInfoDetail createEntity() {
        return new WarehouseStampInfoDetail()
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
            .lenhSanXuatId(DEFAULT_LENH_SAN_XUAT_ID)
            .trangThai(DEFAULT_TRANG_THAI)
            .checked(DEFAULT_CHECKED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WarehouseStampInfoDetail createUpdatedEntity() {
        return new WarehouseStampInfoDetail()
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
            .lenhSanXuatId(UPDATED_LENH_SAN_XUAT_ID)
            .trangThai(UPDATED_TRANG_THAI)
            .checked(UPDATED_CHECKED);
    }

    @BeforeEach
    void initTest() {
        warehouseStampInfoDetail = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedWarehouseStampInfoDetail != null) {
            warehouseStampInfoDetailRepository.delete(
                insertedWarehouseStampInfoDetail
            );
            insertedWarehouseStampInfoDetail = null;
        }
    }

    @Test
    @Transactional
    void createWarehouseStampInfoDetail() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the WarehouseStampInfoDetail
        WarehouseStampInfoDetailDTO warehouseStampInfoDetailDTO =
            warehouseStampInfoDetailMapper.toDto(warehouseStampInfoDetail);
        var returnedWarehouseStampInfoDetailDTO = om.readValue(
            restWarehouseStampInfoDetailMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            om.writeValueAsBytes(warehouseStampInfoDetailDTO)
                        )
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            WarehouseStampInfoDetailDTO.class
        );

        // Validate the WarehouseStampInfoDetail in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWarehouseStampInfoDetail =
            warehouseStampInfoDetailMapper.toEntity(
                returnedWarehouseStampInfoDetailDTO
            );
        assertWarehouseStampInfoDetailUpdatableFieldsEquals(
            returnedWarehouseStampInfoDetail,
            getPersistedWarehouseStampInfoDetail(
                returnedWarehouseStampInfoDetail
            )
        );

        insertedWarehouseStampInfoDetail = returnedWarehouseStampInfoDetail;
    }

    @Test
    @Transactional
    void createWarehouseStampInfoDetailWithExistingId() throws Exception {
        // Create the WarehouseStampInfoDetail with an existing ID
        warehouseStampInfoDetail.setId(1L);
        WarehouseStampInfoDetailDTO warehouseStampInfoDetailDTO =
            warehouseStampInfoDetailMapper.toDto(warehouseStampInfoDetail);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWarehouseStampInfoDetailMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseStampInfoDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WarehouseStampInfoDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllWarehouseStampInfoDetails() throws Exception {
        // Initialize the database
        insertedWarehouseStampInfoDetail =
            warehouseStampInfoDetailRepository.saveAndFlush(
                warehouseStampInfoDetail
            );

        // Get all the warehouseStampInfoDetailList
        restWarehouseStampInfoDetailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(warehouseStampInfoDetail.getId().intValue())
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
                jsonPath("$.[*].lenhSanXuatId").value(
                    hasItem(DEFAULT_LENH_SAN_XUAT_ID.intValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].trangThai").value(hasItem(DEFAULT_TRANG_THAI))
            )
            .andExpect(
                jsonPath("$.[*].checked").value(hasItem(DEFAULT_CHECKED))
            );
    }

    @Test
    @Transactional
    void getWarehouseStampInfoDetail() throws Exception {
        // Initialize the database
        insertedWarehouseStampInfoDetail =
            warehouseStampInfoDetailRepository.saveAndFlush(
                warehouseStampInfoDetail
            );

        // Get the warehouseStampInfoDetail
        restWarehouseStampInfoDetailMockMvc
            .perform(get(ENTITY_API_URL_ID, warehouseStampInfoDetail.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.id").value(
                    warehouseStampInfoDetail.getId().intValue()
                )
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
            .andExpect(
                jsonPath("$.lenhSanXuatId").value(
                    DEFAULT_LENH_SAN_XUAT_ID.intValue()
                )
            )
            .andExpect(jsonPath("$.trangThai").value(DEFAULT_TRANG_THAI))
            .andExpect(jsonPath("$.checked").value(DEFAULT_CHECKED));
    }

    @Test
    @Transactional
    void getNonExistingWarehouseStampInfoDetail() throws Exception {
        // Get the warehouseStampInfoDetail
        restWarehouseStampInfoDetailMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWarehouseStampInfoDetail() throws Exception {
        // Initialize the database
        insertedWarehouseStampInfoDetail =
            warehouseStampInfoDetailRepository.saveAndFlush(
                warehouseStampInfoDetail
            );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the warehouseStampInfoDetail
        WarehouseStampInfoDetail updatedWarehouseStampInfoDetail =
            warehouseStampInfoDetailRepository
                .findById(warehouseStampInfoDetail.getId())
                .orElseThrow();
        // Disconnect from session so that the updates on
        // updatedWarehouseStampInfoDetail are not directly saved in db
        em.detach(updatedWarehouseStampInfoDetail);
        updatedWarehouseStampInfoDetail
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
            .lenhSanXuatId(UPDATED_LENH_SAN_XUAT_ID)
            .trangThai(UPDATED_TRANG_THAI)
            .checked(UPDATED_CHECKED);
        WarehouseStampInfoDetailDTO warehouseStampInfoDetailDTO =
            warehouseStampInfoDetailMapper.toDto(
                updatedWarehouseStampInfoDetail
            );

        restWarehouseStampInfoDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, warehouseStampInfoDetailDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseStampInfoDetailDTO))
            )
            .andExpect(status().isOk());

        // Validate the WarehouseStampInfoDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWarehouseStampInfoDetailToMatchAllProperties(
            updatedWarehouseStampInfoDetail
        );
    }

    @Test
    @Transactional
    void putNonExistingWarehouseStampInfoDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseStampInfoDetail.setId(longCount.incrementAndGet());

        // Create the WarehouseStampInfoDetail
        WarehouseStampInfoDetailDTO warehouseStampInfoDetailDTO =
            warehouseStampInfoDetailMapper.toDto(warehouseStampInfoDetail);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWarehouseStampInfoDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, warehouseStampInfoDetailDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseStampInfoDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WarehouseStampInfoDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWarehouseStampInfoDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseStampInfoDetail.setId(longCount.incrementAndGet());

        // Create the WarehouseStampInfoDetail
        WarehouseStampInfoDetailDTO warehouseStampInfoDetailDTO =
            warehouseStampInfoDetailMapper.toDto(warehouseStampInfoDetail);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseStampInfoDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseStampInfoDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WarehouseStampInfoDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWarehouseStampInfoDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseStampInfoDetail.setId(longCount.incrementAndGet());

        // Create the WarehouseStampInfoDetail
        WarehouseStampInfoDetailDTO warehouseStampInfoDetailDTO =
            warehouseStampInfoDetailMapper.toDto(warehouseStampInfoDetail);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseStampInfoDetailMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseStampInfoDetailDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the WarehouseStampInfoDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWarehouseStampInfoDetailWithPatch() throws Exception {
        // Initialize the database
        insertedWarehouseStampInfoDetail =
            warehouseStampInfoDetailRepository.saveAndFlush(
                warehouseStampInfoDetail
            );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the warehouseStampInfoDetail using partial update
        WarehouseStampInfoDetail partialUpdatedWarehouseStampInfoDetail =
            new WarehouseStampInfoDetail();
        partialUpdatedWarehouseStampInfoDetail.setId(
            warehouseStampInfoDetail.getId()
        );

        partialUpdatedWarehouseStampInfoDetail
            .partNumber(UPDATED_PART_NUMBER)
            .lot(UPDATED_LOT)
            .userData1(UPDATED_USER_DATA_1)
            .userData3(UPDATED_USER_DATA_3)
            .msdInitialFloorTime(UPDATED_MSD_INITIAL_FLOOR_TIME)
            .spMaterialName(UPDATED_SP_MATERIAL_NAME)
            .maximumLimit(UPDATED_MAXIMUM_LIMIT)
            .comments(UPDATED_COMMENTS)
            .warmupTime(UPDATED_WARMUP_TIME)
            .expirationDate(UPDATED_EXPIRATION_DATE)
            .partClass(UPDATED_PART_CLASS)
            .sapCode(UPDATED_SAP_CODE)
            .lenhSanXuatId(UPDATED_LENH_SAN_XUAT_ID)
            .trangThai(UPDATED_TRANG_THAI)
            .checked(UPDATED_CHECKED);

        restWarehouseStampInfoDetailMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    partialUpdatedWarehouseStampInfoDetail.getId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(
                            partialUpdatedWarehouseStampInfoDetail
                        )
                    )
            )
            .andExpect(status().isOk());

        // Validate the WarehouseStampInfoDetail in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWarehouseStampInfoDetailUpdatableFieldsEquals(
            createUpdateProxyForBean(
                partialUpdatedWarehouseStampInfoDetail,
                warehouseStampInfoDetail
            ),
            getPersistedWarehouseStampInfoDetail(warehouseStampInfoDetail)
        );
    }

    @Test
    @Transactional
    void fullUpdateWarehouseStampInfoDetailWithPatch() throws Exception {
        // Initialize the database
        insertedWarehouseStampInfoDetail =
            warehouseStampInfoDetailRepository.saveAndFlush(
                warehouseStampInfoDetail
            );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the warehouseStampInfoDetail using partial update
        WarehouseStampInfoDetail partialUpdatedWarehouseStampInfoDetail =
            new WarehouseStampInfoDetail();
        partialUpdatedWarehouseStampInfoDetail.setId(
            warehouseStampInfoDetail.getId()
        );

        partialUpdatedWarehouseStampInfoDetail
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
            .lenhSanXuatId(UPDATED_LENH_SAN_XUAT_ID)
            .trangThai(UPDATED_TRANG_THAI)
            .checked(UPDATED_CHECKED);

        restWarehouseStampInfoDetailMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    partialUpdatedWarehouseStampInfoDetail.getId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(
                            partialUpdatedWarehouseStampInfoDetail
                        )
                    )
            )
            .andExpect(status().isOk());

        // Validate the WarehouseStampInfoDetail in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWarehouseStampInfoDetailUpdatableFieldsEquals(
            partialUpdatedWarehouseStampInfoDetail,
            getPersistedWarehouseStampInfoDetail(
                partialUpdatedWarehouseStampInfoDetail
            )
        );
    }

    @Test
    @Transactional
    void patchNonExistingWarehouseStampInfoDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseStampInfoDetail.setId(longCount.incrementAndGet());

        // Create the WarehouseStampInfoDetail
        WarehouseStampInfoDetailDTO warehouseStampInfoDetailDTO =
            warehouseStampInfoDetailMapper.toDto(warehouseStampInfoDetail);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWarehouseStampInfoDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, warehouseStampInfoDetailDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(warehouseStampInfoDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WarehouseStampInfoDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWarehouseStampInfoDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseStampInfoDetail.setId(longCount.incrementAndGet());

        // Create the WarehouseStampInfoDetail
        WarehouseStampInfoDetailDTO warehouseStampInfoDetailDTO =
            warehouseStampInfoDetailMapper.toDto(warehouseStampInfoDetail);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseStampInfoDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(warehouseStampInfoDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WarehouseStampInfoDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWarehouseStampInfoDetail()
        throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseStampInfoDetail.setId(longCount.incrementAndGet());

        // Create the WarehouseStampInfoDetail
        WarehouseStampInfoDetailDTO warehouseStampInfoDetailDTO =
            warehouseStampInfoDetailMapper.toDto(warehouseStampInfoDetail);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseStampInfoDetailMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(warehouseStampInfoDetailDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the WarehouseStampInfoDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWarehouseStampInfoDetail() throws Exception {
        // Initialize the database
        insertedWarehouseStampInfoDetail =
            warehouseStampInfoDetailRepository.saveAndFlush(
                warehouseStampInfoDetail
            );

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the warehouseStampInfoDetail
        restWarehouseStampInfoDetailMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, warehouseStampInfoDetail.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return warehouseStampInfoDetailRepository.count();
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

    protected WarehouseStampInfoDetail getPersistedWarehouseStampInfoDetail(
        WarehouseStampInfoDetail warehouseStampInfoDetail
    ) {
        return warehouseStampInfoDetailRepository
            .findById(warehouseStampInfoDetail.getId())
            .orElseThrow();
    }

    protected void assertPersistedWarehouseStampInfoDetailToMatchAllProperties(
        WarehouseStampInfoDetail expectedWarehouseStampInfoDetail
    ) {
        assertWarehouseStampInfoDetailAllPropertiesEquals(
            expectedWarehouseStampInfoDetail,
            getPersistedWarehouseStampInfoDetail(
                expectedWarehouseStampInfoDetail
            )
        );
    }

    protected void assertPersistedWarehouseStampInfoDetailToMatchUpdatableProperties(
        WarehouseStampInfoDetail expectedWarehouseStampInfoDetail
    ) {
        assertWarehouseStampInfoDetailAllUpdatablePropertiesEquals(
            expectedWarehouseStampInfoDetail,
            getPersistedWarehouseStampInfoDetail(
                expectedWarehouseStampInfoDetail
            )
        );
    }
}
