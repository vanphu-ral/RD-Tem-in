package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.WarehouseStampInfoAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.WarehouseNoteInfo;
import com.mycompany.myapp.repository.WarehouseStampInfoRepository;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDTO;
import com.mycompany.myapp.service.mapper.WarehouseStampInfoMapper;
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
 * Integration tests for the {@link WarehouseStampInfoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WarehouseStampInfoResourceIT {

    private static final String DEFAULT_MA_LENH_SAN_XUAT = "AAAAAAAAAA";
    private static final String UPDATED_MA_LENH_SAN_XUAT = "BBBBBBBBBB";

    private static final String DEFAULT_SAP_CODE = "AAAAAAAAAA";
    private static final String UPDATED_SAP_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_SAP_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SAP_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_WORK_ORDER_CODE = "AAAAAAAAAA";
    private static final String UPDATED_WORK_ORDER_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_VERSION = "BBBBBBBBBB";

    private static final String DEFAULT_STORAGE_CODE = "AAAAAAAAAA";
    private static final String UPDATED_STORAGE_CODE = "BBBBBBBBBB";

    private static final Integer DEFAULT_TOTAL_QUANTITY = 1;
    private static final Integer UPDATED_TOTAL_QUANTITY = 2;

    private static final String DEFAULT_CREATE_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATE_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_ENTRY_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ENTRY_TIME = Instant.now().truncatedTo(
        ChronoUnit.MILLIS
    );

    private static final String DEFAULT_TRANG_THAI = "AAAAAAAAAA";
    private static final String UPDATED_TRANG_THAI = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_TIME_UPDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIME_UPDATE =
        Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_GROUP_NAME = "AAAAAAAAAA";
    private static final String UPDATED_GROUP_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENT_2 = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT_2 = "BBBBBBBBBB";

    private static final String DEFAULT_APPROVER_BY = "AAAAAAAAAA";
    private static final String UPDATED_APPROVER_BY = "BBBBBBBBBB";

    private static final String DEFAULT_BRANCH = "AAAAAAAAAA";
    private static final String UPDATED_BRANCH = "BBBBBBBBBB";

    private static final String DEFAULT_PRODUCT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_PRODUCT_TYPE = "BBBBBBBBBB";

    private static final Instant DEFAULT_DELETED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DELETED_AT = Instant.now().truncatedTo(
        ChronoUnit.MILLIS
    );

    private static final String DEFAULT_DELETED_BY = "AAAAAAAAAA";
    private static final String UPDATED_DELETED_BY = "BBBBBBBBBB";

    private static final Integer DEFAULT_DESTINATION_WAREHOUSE = 1;
    private static final Integer UPDATED_DESTINATION_WAREHOUSE = 2;

    private static final String ENTITY_API_URL = "/api/warehouse-note-infos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WarehouseStampInfoRepository warehouseStampInfoRepository;

    @Autowired
    private WarehouseStampInfoMapper warehouseStampInfoMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWarehouseStampInfoMockMvc;

    private WarehouseNoteInfo warehouseStampInfo;

    private WarehouseNoteInfo insertedWarehouseStampInfo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WarehouseNoteInfo createEntity() {
        return new WarehouseNoteInfo()
            .maLenhSanXuat(DEFAULT_MA_LENH_SAN_XUAT)
            .sapCode(DEFAULT_SAP_CODE)
            .sapName(DEFAULT_SAP_NAME)
            .workOrderCode(DEFAULT_WORK_ORDER_CODE)
            .version(DEFAULT_VERSION)
            .storageCode(DEFAULT_STORAGE_CODE)
            .totalQuantity(DEFAULT_TOTAL_QUANTITY)
            .createBy(DEFAULT_CREATE_BY)
            .entryTime(DEFAULT_ENTRY_TIME)
            .trangThai(DEFAULT_TRANG_THAI)
            .comment(DEFAULT_COMMENT)
            .timeUpdate(DEFAULT_TIME_UPDATE)
            .groupName(DEFAULT_GROUP_NAME)
            .comment2(DEFAULT_COMMENT_2)
            .approverBy(DEFAULT_APPROVER_BY)
            .branch(DEFAULT_BRANCH)
            .productType(DEFAULT_PRODUCT_TYPE)
            .deletedAt(DEFAULT_DELETED_AT)
            .deletedBy(DEFAULT_DELETED_BY)
            .destinationWarehouse(DEFAULT_DESTINATION_WAREHOUSE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WarehouseNoteInfo createUpdatedEntity() {
        return new WarehouseNoteInfo()
            .maLenhSanXuat(UPDATED_MA_LENH_SAN_XUAT)
            .sapCode(UPDATED_SAP_CODE)
            .sapName(UPDATED_SAP_NAME)
            .workOrderCode(UPDATED_WORK_ORDER_CODE)
            .version(UPDATED_VERSION)
            .storageCode(UPDATED_STORAGE_CODE)
            .totalQuantity(UPDATED_TOTAL_QUANTITY)
            .createBy(UPDATED_CREATE_BY)
            .entryTime(UPDATED_ENTRY_TIME)
            .trangThai(UPDATED_TRANG_THAI)
            .comment(UPDATED_COMMENT)
            .timeUpdate(UPDATED_TIME_UPDATE)
            .groupName(UPDATED_GROUP_NAME)
            .comment2(UPDATED_COMMENT_2)
            .approverBy(UPDATED_APPROVER_BY)
            .branch(UPDATED_BRANCH)
            .productType(UPDATED_PRODUCT_TYPE)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY)
            .destinationWarehouse(UPDATED_DESTINATION_WAREHOUSE);
    }

    @BeforeEach
    void initTest() {
        warehouseStampInfo = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedWarehouseStampInfo != null) {
            warehouseStampInfoRepository.delete(insertedWarehouseStampInfo);
            insertedWarehouseStampInfo = null;
        }
    }

    @Test
    @Transactional
    void createWarehouseStampInfo() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the WarehouseNoteInfo
        WarehouseStampInfoDTO warehouseStampInfoDTO =
            warehouseStampInfoMapper.toDto(warehouseStampInfo);
        var returnedWarehouseStampInfoDTO = om.readValue(
            restWarehouseStampInfoMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(warehouseStampInfoDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            WarehouseStampInfoDTO.class
        );

        // Validate the WarehouseNoteInfo in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWarehouseStampInfo = warehouseStampInfoMapper.toEntity(
            returnedWarehouseStampInfoDTO
        );
        assertWarehouseStampInfoUpdatableFieldsEquals(
            returnedWarehouseStampInfo,
            getPersistedWarehouseStampInfo(returnedWarehouseStampInfo)
        );

        insertedWarehouseStampInfo = returnedWarehouseStampInfo;
    }

    @Test
    @Transactional
    void createWarehouseStampInfoWithExistingId() throws Exception {
        // Create the WarehouseNoteInfo with an existing ID
        warehouseStampInfo.setId(1L);
        WarehouseStampInfoDTO warehouseStampInfoDTO =
            warehouseStampInfoMapper.toDto(warehouseStampInfo);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWarehouseStampInfoMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseStampInfoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WarehouseNoteInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllWarehouseStampInfos() throws Exception {
        // Initialize the database
        insertedWarehouseStampInfo = warehouseStampInfoRepository.saveAndFlush(
            warehouseStampInfo
        );

        // Get all the warehouseStampInfoList
        restWarehouseStampInfoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(warehouseStampInfo.getId().intValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].maLenhSanXuat").value(
                    hasItem(DEFAULT_MA_LENH_SAN_XUAT)
                )
            )
            .andExpect(
                jsonPath("$.[*].sapCode").value(hasItem(DEFAULT_SAP_CODE))
            )
            .andExpect(
                jsonPath("$.[*].sapName").value(hasItem(DEFAULT_SAP_NAME))
            )
            .andExpect(
                jsonPath("$.[*].workOrderCode").value(
                    hasItem(DEFAULT_WORK_ORDER_CODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION))
            )
            .andExpect(
                jsonPath("$.[*].storageCode").value(
                    hasItem(DEFAULT_STORAGE_CODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].totalQuantity").value(
                    hasItem(DEFAULT_TOTAL_QUANTITY)
                )
            )
            .andExpect(
                jsonPath("$.[*].createBy").value(hasItem(DEFAULT_CREATE_BY))
            )
            .andExpect(
                jsonPath("$.[*].entryTime").value(
                    hasItem(DEFAULT_ENTRY_TIME.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].trangThai").value(hasItem(DEFAULT_TRANG_THAI))
            )
            .andExpect(
                jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT))
            )
            .andExpect(
                jsonPath("$.[*].timeUpdate").value(
                    hasItem(DEFAULT_TIME_UPDATE.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].groupName").value(hasItem(DEFAULT_GROUP_NAME))
            )
            .andExpect(
                jsonPath("$.[*].comment2").value(hasItem(DEFAULT_COMMENT_2))
            )
            .andExpect(
                jsonPath("$.[*].approverBy").value(hasItem(DEFAULT_APPROVER_BY))
            )
            .andExpect(jsonPath("$.[*].branch").value(hasItem(DEFAULT_BRANCH)))
            .andExpect(
                jsonPath("$.[*].productType").value(
                    hasItem(DEFAULT_PRODUCT_TYPE)
                )
            )
            .andExpect(
                jsonPath("$.[*].deletedAt").value(
                    hasItem(DEFAULT_DELETED_AT.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY))
            )
            .andExpect(
                jsonPath("$.[*].destinationWarehouse").value(
                    hasItem(DEFAULT_DESTINATION_WAREHOUSE)
                )
            );
    }

    @Test
    @Transactional
    void getWarehouseStampInfo() throws Exception {
        // Initialize the database
        insertedWarehouseStampInfo = warehouseStampInfoRepository.saveAndFlush(
            warehouseStampInfo
        );

        // Get the warehouseStampInfo
        restWarehouseStampInfoMockMvc
            .perform(get(ENTITY_API_URL_ID, warehouseStampInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.id").value(warehouseStampInfo.getId().intValue())
            )
            .andExpect(
                jsonPath("$.maLenhSanXuat").value(DEFAULT_MA_LENH_SAN_XUAT)
            )
            .andExpect(jsonPath("$.sapCode").value(DEFAULT_SAP_CODE))
            .andExpect(jsonPath("$.sapName").value(DEFAULT_SAP_NAME))
            .andExpect(
                jsonPath("$.workOrderCode").value(DEFAULT_WORK_ORDER_CODE)
            )
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.storageCode").value(DEFAULT_STORAGE_CODE))
            .andExpect(
                jsonPath("$.totalQuantity").value(DEFAULT_TOTAL_QUANTITY)
            )
            .andExpect(jsonPath("$.createBy").value(DEFAULT_CREATE_BY))
            .andExpect(
                jsonPath("$.entryTime").value(DEFAULT_ENTRY_TIME.toString())
            )
            .andExpect(jsonPath("$.trangThai").value(DEFAULT_TRANG_THAI))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT))
            .andExpect(
                jsonPath("$.timeUpdate").value(DEFAULT_TIME_UPDATE.toString())
            )
            .andExpect(jsonPath("$.groupName").value(DEFAULT_GROUP_NAME))
            .andExpect(jsonPath("$.comment2").value(DEFAULT_COMMENT_2))
            .andExpect(jsonPath("$.approverBy").value(DEFAULT_APPROVER_BY))
            .andExpect(jsonPath("$.branch").value(DEFAULT_BRANCH))
            .andExpect(jsonPath("$.productType").value(DEFAULT_PRODUCT_TYPE))
            .andExpect(
                jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString())
            )
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY))
            .andExpect(
                jsonPath("$.destinationWarehouse").value(
                    DEFAULT_DESTINATION_WAREHOUSE
                )
            );
    }

    @Test
    @Transactional
    void getNonExistingWarehouseStampInfo() throws Exception {
        // Get the warehouseStampInfo
        restWarehouseStampInfoMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWarehouseStampInfo() throws Exception {
        // Initialize the database
        insertedWarehouseStampInfo = warehouseStampInfoRepository.saveAndFlush(
            warehouseStampInfo
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the warehouseStampInfo
        WarehouseNoteInfo updatedWarehouseStampInfo =
            warehouseStampInfoRepository
                .findById(warehouseStampInfo.getId())
                .orElseThrow();
        // Disconnect from session so that the updates on updatedWarehouseStampInfo are
        // not directly saved in db
        em.detach(updatedWarehouseStampInfo);
        updatedWarehouseStampInfo
            .maLenhSanXuat(UPDATED_MA_LENH_SAN_XUAT)
            .sapCode(UPDATED_SAP_CODE)
            .sapName(UPDATED_SAP_NAME)
            .workOrderCode(UPDATED_WORK_ORDER_CODE)
            .version(UPDATED_VERSION)
            .storageCode(UPDATED_STORAGE_CODE)
            .totalQuantity(UPDATED_TOTAL_QUANTITY)
            .createBy(UPDATED_CREATE_BY)
            .entryTime(UPDATED_ENTRY_TIME)
            .trangThai(UPDATED_TRANG_THAI)
            .comment(UPDATED_COMMENT)
            .timeUpdate(UPDATED_TIME_UPDATE)
            .groupName(UPDATED_GROUP_NAME)
            .comment2(UPDATED_COMMENT_2)
            .approverBy(UPDATED_APPROVER_BY)
            .branch(UPDATED_BRANCH)
            .productType(UPDATED_PRODUCT_TYPE)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY)
            .destinationWarehouse(UPDATED_DESTINATION_WAREHOUSE);
        WarehouseStampInfoDTO warehouseStampInfoDTO =
            warehouseStampInfoMapper.toDto(updatedWarehouseStampInfo);

        restWarehouseStampInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, warehouseStampInfoDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseStampInfoDTO))
            )
            .andExpect(status().isOk());

        // Validate the WarehouseNoteInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWarehouseStampInfoToMatchAllProperties(
            updatedWarehouseStampInfo
        );
    }

    @Test
    @Transactional
    void putNonExistingWarehouseStampInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseStampInfo.setId(longCount.incrementAndGet());

        // Create the WarehouseNoteInfo
        WarehouseStampInfoDTO warehouseStampInfoDTO =
            warehouseStampInfoMapper.toDto(warehouseStampInfo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWarehouseStampInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, warehouseStampInfoDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseStampInfoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WarehouseNoteInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWarehouseStampInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseStampInfo.setId(longCount.incrementAndGet());

        // Create the WarehouseNoteInfo
        WarehouseStampInfoDTO warehouseStampInfoDTO =
            warehouseStampInfoMapper.toDto(warehouseStampInfo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseStampInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseStampInfoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WarehouseNoteInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWarehouseStampInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseStampInfo.setId(longCount.incrementAndGet());

        // Create the WarehouseNoteInfo
        WarehouseStampInfoDTO warehouseStampInfoDTO =
            warehouseStampInfoMapper.toDto(warehouseStampInfo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseStampInfoMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseStampInfoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the WarehouseNoteInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWarehouseStampInfoWithPatch() throws Exception {
        // Initialize the database
        insertedWarehouseStampInfo = warehouseStampInfoRepository.saveAndFlush(
            warehouseStampInfo
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the warehouseStampInfo using partial update
        WarehouseNoteInfo partialUpdatedWarehouseStampInfo =
            new WarehouseNoteInfo();
        partialUpdatedWarehouseStampInfo.setId(warehouseStampInfo.getId());

        partialUpdatedWarehouseStampInfo
            .maLenhSanXuat(UPDATED_MA_LENH_SAN_XUAT)
            .sapCode(UPDATED_SAP_CODE)
            .version(UPDATED_VERSION)
            .storageCode(UPDATED_STORAGE_CODE)
            .entryTime(UPDATED_ENTRY_TIME)
            .comment(UPDATED_COMMENT)
            .timeUpdate(UPDATED_TIME_UPDATE)
            .groupName(UPDATED_GROUP_NAME)
            .approverBy(UPDATED_APPROVER_BY)
            .branch(UPDATED_BRANCH)
            .productType(UPDATED_PRODUCT_TYPE);

        restWarehouseStampInfoMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    partialUpdatedWarehouseStampInfo.getId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(partialUpdatedWarehouseStampInfo)
                    )
            )
            .andExpect(status().isOk());

        // Validate the WarehouseNoteInfo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWarehouseStampInfoUpdatableFieldsEquals(
            createUpdateProxyForBean(
                partialUpdatedWarehouseStampInfo,
                warehouseStampInfo
            ),
            getPersistedWarehouseStampInfo(warehouseStampInfo)
        );
    }

    @Test
    @Transactional
    void fullUpdateWarehouseStampInfoWithPatch() throws Exception {
        // Initialize the database
        insertedWarehouseStampInfo = warehouseStampInfoRepository.saveAndFlush(
            warehouseStampInfo
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the warehouseStampInfo using partial update
        WarehouseNoteInfo partialUpdatedWarehouseStampInfo =
            new WarehouseNoteInfo();
        partialUpdatedWarehouseStampInfo.setId(warehouseStampInfo.getId());

        partialUpdatedWarehouseStampInfo
            .maLenhSanXuat(UPDATED_MA_LENH_SAN_XUAT)
            .sapCode(UPDATED_SAP_CODE)
            .sapName(UPDATED_SAP_NAME)
            .workOrderCode(UPDATED_WORK_ORDER_CODE)
            .version(UPDATED_VERSION)
            .storageCode(UPDATED_STORAGE_CODE)
            .totalQuantity(UPDATED_TOTAL_QUANTITY)
            .createBy(UPDATED_CREATE_BY)
            .entryTime(UPDATED_ENTRY_TIME)
            .trangThai(UPDATED_TRANG_THAI)
            .comment(UPDATED_COMMENT)
            .timeUpdate(UPDATED_TIME_UPDATE)
            .groupName(UPDATED_GROUP_NAME)
            .comment2(UPDATED_COMMENT_2)
            .approverBy(UPDATED_APPROVER_BY)
            .branch(UPDATED_BRANCH)
            .productType(UPDATED_PRODUCT_TYPE)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY)
            .destinationWarehouse(UPDATED_DESTINATION_WAREHOUSE);

        restWarehouseStampInfoMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    partialUpdatedWarehouseStampInfo.getId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(partialUpdatedWarehouseStampInfo)
                    )
            )
            .andExpect(status().isOk());

        // Validate the WarehouseNoteInfo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWarehouseStampInfoUpdatableFieldsEquals(
            partialUpdatedWarehouseStampInfo,
            getPersistedWarehouseStampInfo(partialUpdatedWarehouseStampInfo)
        );
    }

    @Test
    @Transactional
    void patchNonExistingWarehouseStampInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseStampInfo.setId(longCount.incrementAndGet());

        // Create the WarehouseNoteInfo
        WarehouseStampInfoDTO warehouseStampInfoDTO =
            warehouseStampInfoMapper.toDto(warehouseStampInfo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWarehouseStampInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, warehouseStampInfoDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(warehouseStampInfoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WarehouseNoteInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWarehouseStampInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseStampInfo.setId(longCount.incrementAndGet());

        // Create the WarehouseNoteInfo
        WarehouseStampInfoDTO warehouseStampInfoDTO =
            warehouseStampInfoMapper.toDto(warehouseStampInfo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseStampInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(warehouseStampInfoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WarehouseNoteInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWarehouseStampInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseStampInfo.setId(longCount.incrementAndGet());

        // Create the WarehouseNoteInfo
        WarehouseStampInfoDTO warehouseStampInfoDTO =
            warehouseStampInfoMapper.toDto(warehouseStampInfo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseStampInfoMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(warehouseStampInfoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the WarehouseNoteInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWarehouseStampInfo() throws Exception {
        // Initialize the database
        insertedWarehouseStampInfo = warehouseStampInfoRepository.saveAndFlush(
            warehouseStampInfo
        );

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the warehouseStampInfo
        restWarehouseStampInfoMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, warehouseStampInfo.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return warehouseStampInfoRepository.count();
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

    protected WarehouseNoteInfo getPersistedWarehouseStampInfo(
        WarehouseNoteInfo warehouseStampInfo
    ) {
        return warehouseStampInfoRepository
            .findById(warehouseStampInfo.getId())
            .orElseThrow();
    }

    protected void assertPersistedWarehouseStampInfoToMatchAllProperties(
        WarehouseNoteInfo expectedWarehouseStampInfo
    ) {
        assertWarehouseStampInfoAllPropertiesEquals(
            expectedWarehouseStampInfo,
            getPersistedWarehouseStampInfo(expectedWarehouseStampInfo)
        );
    }

    protected void assertPersistedWarehouseStampInfoToMatchUpdatableProperties(
        WarehouseNoteInfo expectedWarehouseStampInfo
    ) {
        assertWarehouseStampInfoAllUpdatablePropertiesEquals(
            expectedWarehouseStampInfo,
            getPersistedWarehouseStampInfo(expectedWarehouseStampInfo)
        );
    }
}
