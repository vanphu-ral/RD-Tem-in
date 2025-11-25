package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.GenTemConfigAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.GenTemConfig;
import com.mycompany.myapp.repository.GenTemConfigRepository;
import com.mycompany.myapp.service.dto.GenTemConfigDTO;
import com.mycompany.myapp.service.mapper.GenTemConfigMapper;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link GenTemConfigResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GenTemConfigResourceIT {

    private static final String DEFAULT_TP_NK = "AAAAAAAAAA";
    private static final String UPDATED_TP_NK = "BBBBBBBBBB";

    private static final String DEFAULT_RANK = "AAAAAAAAAA";
    private static final String UPDATED_RANK = "BBBBBBBBBB";

    private static final String DEFAULT_MFG = "AAAAAAAAAA";
    private static final String UPDATED_MFG = "BBBBBBBBBB";

    private static final Integer DEFAULT_QUANTITY_PER_BOX = 1;
    private static final Integer UPDATED_QUANTITY_PER_BOX = 2;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final Integer DEFAULT_NUM_BOX_PER_PALLET = 1;
    private static final Integer UPDATED_NUM_BOX_PER_PALLET = 2;

    private static final String DEFAULT_BRANCH = "AAAAAAAAAA";
    private static final String UPDATED_BRANCH = "BBBBBBBBBB";

    private static final String DEFAULT_GROUP_NAME = "AAAAAAAAAA";
    private static final String UPDATED_GROUP_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CUSTOMER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CUSTOMER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PO_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PO_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_DATE_CODE = "AAAAAAAAAA";
    private static final String UPDATED_DATE_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_ITEM_NO_SKU = "AAAAAAAAAA";
    private static final String UPDATED_ITEM_NO_SKU = "BBBBBBBBBB";

    private static final String DEFAULT_QDSX_NO = "AAAAAAAAAA";
    private static final String UPDATED_QDSX_NO = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_PRODUCTION_DATE =
        LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PRODUCTION_DATE = LocalDate.now(
        ZoneId.systemDefault()
    );

    private static final String DEFAULT_INSPECTOR_NAME = "AAAAAAAAAA";
    private static final String UPDATED_INSPECTOR_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_INSPECTION_RESULT = "AAAAAAAAAA";
    private static final String UPDATED_INSPECTION_RESULT = "BBBBBBBBBB";

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(
        ChronoUnit.MILLIS
    );

    private static final String DEFAULT_UPDATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_UPDATED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/gen-tem-configs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private GenTemConfigRepository genTemConfigRepository;

    @Autowired
    private GenTemConfigMapper genTemConfigMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGenTemConfigMockMvc;

    private GenTemConfig genTemConfig;

    private GenTemConfig insertedGenTemConfig;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GenTemConfig createEntity() {
        return new GenTemConfig()
            .tpNk(DEFAULT_TP_NK)
            .rank(DEFAULT_RANK)
            .mfg(DEFAULT_MFG)
            .quantityPerBox(DEFAULT_QUANTITY_PER_BOX)
            .note(DEFAULT_NOTE)
            .numBoxPerPallet(DEFAULT_NUM_BOX_PER_PALLET)
            .branch(DEFAULT_BRANCH)
            .groupName(DEFAULT_GROUP_NAME)
            .customerName(DEFAULT_CUSTOMER_NAME)
            .poNumber(DEFAULT_PO_NUMBER)
            .dateCode(DEFAULT_DATE_CODE)
            .itemNoSku(DEFAULT_ITEM_NO_SKU)
            .qdsxNo(DEFAULT_QDSX_NO)
            .productionDate(DEFAULT_PRODUCTION_DATE)
            .inspectorName(DEFAULT_INSPECTOR_NAME)
            .inspectionResult(DEFAULT_INSPECTION_RESULT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .updatedBy(DEFAULT_UPDATED_BY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GenTemConfig createUpdatedEntity() {
        return new GenTemConfig()
            .tpNk(UPDATED_TP_NK)
            .rank(UPDATED_RANK)
            .mfg(UPDATED_MFG)
            .quantityPerBox(UPDATED_QUANTITY_PER_BOX)
            .note(UPDATED_NOTE)
            .numBoxPerPallet(UPDATED_NUM_BOX_PER_PALLET)
            .branch(UPDATED_BRANCH)
            .groupName(UPDATED_GROUP_NAME)
            .customerName(UPDATED_CUSTOMER_NAME)
            .poNumber(UPDATED_PO_NUMBER)
            .dateCode(UPDATED_DATE_CODE)
            .itemNoSku(UPDATED_ITEM_NO_SKU)
            .qdsxNo(UPDATED_QDSX_NO)
            .productionDate(UPDATED_PRODUCTION_DATE)
            .inspectorName(UPDATED_INSPECTOR_NAME)
            .inspectionResult(UPDATED_INSPECTION_RESULT)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY);
    }

    @BeforeEach
    void initTest() {
        genTemConfig = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedGenTemConfig != null) {
            genTemConfigRepository.delete(insertedGenTemConfig);
            insertedGenTemConfig = null;
        }
    }

    @Test
    @Transactional
    void createGenTemConfig() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the GenTemConfig
        GenTemConfigDTO genTemConfigDTO = genTemConfigMapper.toDto(
            genTemConfig
        );
        var returnedGenTemConfigDTO = om.readValue(
            restGenTemConfigMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(genTemConfigDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            GenTemConfigDTO.class
        );

        // Validate the GenTemConfig in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedGenTemConfig = genTemConfigMapper.toEntity(
            returnedGenTemConfigDTO
        );
        assertGenTemConfigUpdatableFieldsEquals(
            returnedGenTemConfig,
            getPersistedGenTemConfig(returnedGenTemConfig)
        );

        insertedGenTemConfig = returnedGenTemConfig;
    }

    @Test
    @Transactional
    void createGenTemConfigWithExistingId() throws Exception {
        // Create the GenTemConfig with an existing ID
        genTemConfig.setId(1L);
        GenTemConfigDTO genTemConfigDTO = genTemConfigMapper.toDto(
            genTemConfig
        );

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGenTemConfigMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(genTemConfigDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GenTemConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllGenTemConfigs() throws Exception {
        // Initialize the database
        insertedGenTemConfig = genTemConfigRepository.saveAndFlush(
            genTemConfig
        );

        // Get all the genTemConfigList
        restGenTemConfigMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(genTemConfig.getId().intValue())
                )
            )
            .andExpect(jsonPath("$.[*].tpNk").value(hasItem(DEFAULT_TP_NK)))
            .andExpect(jsonPath("$.[*].rank").value(hasItem(DEFAULT_RANK)))
            .andExpect(jsonPath("$.[*].mfg").value(hasItem(DEFAULT_MFG)))
            .andExpect(
                jsonPath("$.[*].quantityPerBox").value(
                    hasItem(DEFAULT_QUANTITY_PER_BOX)
                )
            )
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(
                jsonPath("$.[*].numBoxPerPallet").value(
                    hasItem(DEFAULT_NUM_BOX_PER_PALLET)
                )
            )
            .andExpect(jsonPath("$.[*].branch").value(hasItem(DEFAULT_BRANCH)))
            .andExpect(
                jsonPath("$.[*].groupName").value(hasItem(DEFAULT_GROUP_NAME))
            )
            .andExpect(
                jsonPath("$.[*].customerName").value(
                    hasItem(DEFAULT_CUSTOMER_NAME)
                )
            )
            .andExpect(
                jsonPath("$.[*].poNumber").value(hasItem(DEFAULT_PO_NUMBER))
            )
            .andExpect(
                jsonPath("$.[*].dateCode").value(hasItem(DEFAULT_DATE_CODE))
            )
            .andExpect(
                jsonPath("$.[*].itemNoSku").value(hasItem(DEFAULT_ITEM_NO_SKU))
            )
            .andExpect(jsonPath("$.[*].qdsxNo").value(hasItem(DEFAULT_QDSX_NO)))
            .andExpect(
                jsonPath("$.[*].productionDate").value(
                    hasItem(DEFAULT_PRODUCTION_DATE.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].inspectorName").value(
                    hasItem(DEFAULT_INSPECTOR_NAME)
                )
            )
            .andExpect(
                jsonPath("$.[*].inspectionResult").value(
                    hasItem(DEFAULT_INSPECTION_RESULT)
                )
            )
            .andExpect(
                jsonPath("$.[*].updatedAt").value(
                    hasItem(DEFAULT_UPDATED_AT.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY))
            );
    }

    @Test
    @Transactional
    void getGenTemConfig() throws Exception {
        // Initialize the database
        insertedGenTemConfig = genTemConfigRepository.saveAndFlush(
            genTemConfig
        );

        // Get the genTemConfig
        restGenTemConfigMockMvc
            .perform(get(ENTITY_API_URL_ID, genTemConfig.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(genTemConfig.getId().intValue()))
            .andExpect(jsonPath("$.tpNk").value(DEFAULT_TP_NK))
            .andExpect(jsonPath("$.rank").value(DEFAULT_RANK))
            .andExpect(jsonPath("$.mfg").value(DEFAULT_MFG))
            .andExpect(
                jsonPath("$.quantityPerBox").value(DEFAULT_QUANTITY_PER_BOX)
            )
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE))
            .andExpect(
                jsonPath("$.numBoxPerPallet").value(DEFAULT_NUM_BOX_PER_PALLET)
            )
            .andExpect(jsonPath("$.branch").value(DEFAULT_BRANCH))
            .andExpect(jsonPath("$.groupName").value(DEFAULT_GROUP_NAME))
            .andExpect(jsonPath("$.customerName").value(DEFAULT_CUSTOMER_NAME))
            .andExpect(jsonPath("$.poNumber").value(DEFAULT_PO_NUMBER))
            .andExpect(jsonPath("$.dateCode").value(DEFAULT_DATE_CODE))
            .andExpect(jsonPath("$.itemNoSku").value(DEFAULT_ITEM_NO_SKU))
            .andExpect(jsonPath("$.qdsxNo").value(DEFAULT_QDSX_NO))
            .andExpect(
                jsonPath("$.productionDate").value(
                    DEFAULT_PRODUCTION_DATE.toString()
                )
            )
            .andExpect(
                jsonPath("$.inspectorName").value(DEFAULT_INSPECTOR_NAME)
            )
            .andExpect(
                jsonPath("$.inspectionResult").value(DEFAULT_INSPECTION_RESULT)
            )
            .andExpect(
                jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString())
            )
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY));
    }

    @Test
    @Transactional
    void getNonExistingGenTemConfig() throws Exception {
        // Get the genTemConfig
        restGenTemConfigMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingGenTemConfig() throws Exception {
        // Initialize the database
        insertedGenTemConfig = genTemConfigRepository.saveAndFlush(
            genTemConfig
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the genTemConfig
        GenTemConfig updatedGenTemConfig = genTemConfigRepository
            .findById(genTemConfig.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedGenTemConfig are not
        // directly saved in db
        em.detach(updatedGenTemConfig);
        updatedGenTemConfig
            .tpNk(UPDATED_TP_NK)
            .rank(UPDATED_RANK)
            .mfg(UPDATED_MFG)
            .quantityPerBox(UPDATED_QUANTITY_PER_BOX)
            .note(UPDATED_NOTE)
            .numBoxPerPallet(UPDATED_NUM_BOX_PER_PALLET)
            .branch(UPDATED_BRANCH)
            .groupName(UPDATED_GROUP_NAME)
            .customerName(UPDATED_CUSTOMER_NAME)
            .poNumber(UPDATED_PO_NUMBER)
            .dateCode(UPDATED_DATE_CODE)
            .itemNoSku(UPDATED_ITEM_NO_SKU)
            .qdsxNo(UPDATED_QDSX_NO)
            .productionDate(UPDATED_PRODUCTION_DATE)
            .inspectorName(UPDATED_INSPECTOR_NAME)
            .inspectionResult(UPDATED_INSPECTION_RESULT)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY);
        GenTemConfigDTO genTemConfigDTO = genTemConfigMapper.toDto(
            updatedGenTemConfig
        );

        restGenTemConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, genTemConfigDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(genTemConfigDTO))
            )
            .andExpect(status().isOk());

        // Validate the GenTemConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedGenTemConfigToMatchAllProperties(updatedGenTemConfig);
    }

    @Test
    @Transactional
    void putNonExistingGenTemConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        genTemConfig.setId(longCount.incrementAndGet());

        // Create the GenTemConfig
        GenTemConfigDTO genTemConfigDTO = genTemConfigMapper.toDto(
            genTemConfig
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGenTemConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, genTemConfigDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(genTemConfigDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GenTemConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGenTemConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        genTemConfig.setId(longCount.incrementAndGet());

        // Create the GenTemConfig
        GenTemConfigDTO genTemConfigDTO = genTemConfigMapper.toDto(
            genTemConfig
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenTemConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(genTemConfigDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GenTemConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGenTemConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        genTemConfig.setId(longCount.incrementAndGet());

        // Create the GenTemConfig
        GenTemConfigDTO genTemConfigDTO = genTemConfigMapper.toDto(
            genTemConfig
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenTemConfigMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(genTemConfigDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the GenTemConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGenTemConfigWithPatch() throws Exception {
        // Initialize the database
        insertedGenTemConfig = genTemConfigRepository.saveAndFlush(
            genTemConfig
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the genTemConfig using partial update
        GenTemConfig partialUpdatedGenTemConfig = new GenTemConfig();
        partialUpdatedGenTemConfig.setId(genTemConfig.getId());

        partialUpdatedGenTemConfig
            .tpNk(UPDATED_TP_NK)
            .rank(UPDATED_RANK)
            .quantityPerBox(UPDATED_QUANTITY_PER_BOX)
            .note(UPDATED_NOTE)
            .customerName(UPDATED_CUSTOMER_NAME)
            .poNumber(UPDATED_PO_NUMBER)
            .itemNoSku(UPDATED_ITEM_NO_SKU)
            .inspectorName(UPDATED_INSPECTOR_NAME)
            .inspectionResult(UPDATED_INSPECTION_RESULT)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY);

        restGenTemConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGenTemConfig.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGenTemConfig))
            )
            .andExpect(status().isOk());

        // Validate the GenTemConfig in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGenTemConfigUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedGenTemConfig, genTemConfig),
            getPersistedGenTemConfig(genTemConfig)
        );
    }

    @Test
    @Transactional
    void fullUpdateGenTemConfigWithPatch() throws Exception {
        // Initialize the database
        insertedGenTemConfig = genTemConfigRepository.saveAndFlush(
            genTemConfig
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the genTemConfig using partial update
        GenTemConfig partialUpdatedGenTemConfig = new GenTemConfig();
        partialUpdatedGenTemConfig.setId(genTemConfig.getId());

        partialUpdatedGenTemConfig
            .tpNk(UPDATED_TP_NK)
            .rank(UPDATED_RANK)
            .mfg(UPDATED_MFG)
            .quantityPerBox(UPDATED_QUANTITY_PER_BOX)
            .note(UPDATED_NOTE)
            .numBoxPerPallet(UPDATED_NUM_BOX_PER_PALLET)
            .branch(UPDATED_BRANCH)
            .groupName(UPDATED_GROUP_NAME)
            .customerName(UPDATED_CUSTOMER_NAME)
            .poNumber(UPDATED_PO_NUMBER)
            .dateCode(UPDATED_DATE_CODE)
            .itemNoSku(UPDATED_ITEM_NO_SKU)
            .qdsxNo(UPDATED_QDSX_NO)
            .productionDate(UPDATED_PRODUCTION_DATE)
            .inspectorName(UPDATED_INSPECTOR_NAME)
            .inspectionResult(UPDATED_INSPECTION_RESULT)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY);

        restGenTemConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGenTemConfig.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGenTemConfig))
            )
            .andExpect(status().isOk());

        // Validate the GenTemConfig in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGenTemConfigUpdatableFieldsEquals(
            partialUpdatedGenTemConfig,
            getPersistedGenTemConfig(partialUpdatedGenTemConfig)
        );
    }

    @Test
    @Transactional
    void patchNonExistingGenTemConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        genTemConfig.setId(longCount.incrementAndGet());

        // Create the GenTemConfig
        GenTemConfigDTO genTemConfigDTO = genTemConfigMapper.toDto(
            genTemConfig
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGenTemConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, genTemConfigDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(genTemConfigDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GenTemConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGenTemConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        genTemConfig.setId(longCount.incrementAndGet());

        // Create the GenTemConfig
        GenTemConfigDTO genTemConfigDTO = genTemConfigMapper.toDto(
            genTemConfig
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenTemConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(genTemConfigDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GenTemConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGenTemConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        genTemConfig.setId(longCount.incrementAndGet());

        // Create the GenTemConfig
        GenTemConfigDTO genTemConfigDTO = genTemConfigMapper.toDto(
            genTemConfig
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenTemConfigMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(genTemConfigDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the GenTemConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGenTemConfig() throws Exception {
        // Initialize the database
        insertedGenTemConfig = genTemConfigRepository.saveAndFlush(
            genTemConfig
        );

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the genTemConfig
        restGenTemConfigMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, genTemConfig.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return genTemConfigRepository.count();
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

    protected GenTemConfig getPersistedGenTemConfig(GenTemConfig genTemConfig) {
        return genTemConfigRepository
            .findById(genTemConfig.getId())
            .orElseThrow();
    }

    protected void assertPersistedGenTemConfigToMatchAllProperties(
        GenTemConfig expectedGenTemConfig
    ) {
        assertGenTemConfigAllPropertiesEquals(
            expectedGenTemConfig,
            getPersistedGenTemConfig(expectedGenTemConfig)
        );
    }

    protected void assertPersistedGenTemConfigToMatchUpdatableProperties(
        GenTemConfig expectedGenTemConfig
    ) {
        assertGenTemConfigAllUpdatablePropertiesEquals(
            expectedGenTemConfig,
            getPersistedGenTemConfig(expectedGenTemConfig)
        );
    }
}
