package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.ScanPalletAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.ScanPallet;
import com.mycompany.myapp.repository.ScanPalletRepository;
import com.mycompany.myapp.service.dto.ScanPalletDTO;
import com.mycompany.myapp.service.mapper.ScanPalletMapper;
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
 * Integration tests for the {@link ScanPalletResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ScanPalletResourceIT {

    private static final String DEFAULT_SERIAL_PALLET = "AAAAAAAAAA";
    private static final String UPDATED_SERIAL_PALLET = "BBBBBBBBBB";

    private static final Long DEFAULT_BUSINESS_SOURCE_ID = 1L;
    private static final Long UPDATED_BUSINESS_SOURCE_ID = 2L;

    private static final String DEFAULT_BUSINESS_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_BUSINESS_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/scan-pallets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ScanPalletRepository scanPalletRepository;

    @Autowired
    private ScanPalletMapper scanPalletMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restScanPalletMockMvc;

    private ScanPallet scanPallet;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScanPallet createEntity(EntityManager em) {
        ScanPallet scanPallet = new ScanPallet()
            .serialPallet(DEFAULT_SERIAL_PALLET)
            .businessSourceId(DEFAULT_BUSINESS_SOURCE_ID)
            .businessType(DEFAULT_BUSINESS_TYPE);
        return scanPallet;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScanPallet createUpdatedEntity(EntityManager em) {
        ScanPallet scanPallet = new ScanPallet()
            .serialPallet(UPDATED_SERIAL_PALLET)
            .businessSourceId(UPDATED_BUSINESS_SOURCE_ID)
            .businessType(UPDATED_BUSINESS_TYPE);
        return scanPallet;
    }

    @BeforeEach
    public void initTest() {
        scanPallet = createEntity(em);
    }

    @Test
    @Transactional
    void createScanPallet() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ScanPallet
        ScanPalletDTO scanPalletDTO = scanPalletMapper.toDto(scanPallet);
        var returnedScanPalletDTO = om.readValue(
            restScanPalletMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(scanPalletDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ScanPalletDTO.class
        );

        // Validate the ScanPallet in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedScanPallet = scanPalletMapper.toEntity(
            returnedScanPalletDTO
        );
        assertScanPalletUpdatableFieldsEquals(
            returnedScanPallet,
            getPersistedScanPallet(returnedScanPallet)
        );
    }

    @Test
    @Transactional
    void createScanPalletWithExistingId() throws Exception {
        // Create the ScanPallet with an existing ID
        scanPallet.setId(1L);
        ScanPalletDTO scanPalletDTO = scanPalletMapper.toDto(scanPallet);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restScanPalletMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scanPalletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScanPallet in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllScanPallets() throws Exception {
        // Initialize the database
        scanPalletRepository.saveAndFlush(scanPallet);

        // Get all the scanPalletList
        restScanPalletMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(scanPallet.getId().intValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].serialPallet").value(
                    hasItem(DEFAULT_SERIAL_PALLET)
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
            );
    }

    @Test
    @Transactional
    void getScanPallet() throws Exception {
        // Initialize the database
        scanPalletRepository.saveAndFlush(scanPallet);

        // Get the scanPallet
        restScanPalletMockMvc
            .perform(get(ENTITY_API_URL_ID, scanPallet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(scanPallet.getId().intValue()))
            .andExpect(jsonPath("$.serialPallet").value(DEFAULT_SERIAL_PALLET))
            .andExpect(
                jsonPath("$.businessSourceId").value(
                    DEFAULT_BUSINESS_SOURCE_ID.intValue()
                )
            )
            .andExpect(jsonPath("$.businessType").value(DEFAULT_BUSINESS_TYPE));
    }

    @Test
    @Transactional
    void getNonExistingScanPallet() throws Exception {
        // Get the scanPallet
        restScanPalletMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingScanPallet() throws Exception {
        // Initialize the database
        scanPalletRepository.saveAndFlush(scanPallet);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scanPallet
        ScanPallet updatedScanPallet = scanPalletRepository
            .findById(scanPallet.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedScanPallet are not directly saved in db
        em.detach(updatedScanPallet);
        updatedScanPallet
            .serialPallet(UPDATED_SERIAL_PALLET)
            .businessSourceId(UPDATED_BUSINESS_SOURCE_ID)
            .businessType(UPDATED_BUSINESS_TYPE);
        ScanPalletDTO scanPalletDTO = scanPalletMapper.toDto(updatedScanPallet);

        restScanPalletMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scanPalletDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scanPalletDTO))
            )
            .andExpect(status().isOk());

        // Validate the ScanPallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedScanPalletToMatchAllProperties(updatedScanPallet);
    }

    @Test
    @Transactional
    void putNonExistingScanPallet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scanPallet.setId(longCount.incrementAndGet());

        // Create the ScanPallet
        ScanPalletDTO scanPalletDTO = scanPalletMapper.toDto(scanPallet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScanPalletMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scanPalletDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scanPalletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScanPallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchScanPallet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scanPallet.setId(longCount.incrementAndGet());

        // Create the ScanPallet
        ScanPalletDTO scanPalletDTO = scanPalletMapper.toDto(scanPallet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScanPalletMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scanPalletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScanPallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamScanPallet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scanPallet.setId(longCount.incrementAndGet());

        // Create the ScanPallet
        ScanPalletDTO scanPalletDTO = scanPalletMapper.toDto(scanPallet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScanPalletMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scanPalletDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ScanPallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateScanPalletWithPatch() throws Exception {
        // Initialize the database
        scanPalletRepository.saveAndFlush(scanPallet);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scanPallet using partial update
        ScanPallet partialUpdatedScanPallet = new ScanPallet();
        partialUpdatedScanPallet.setId(scanPallet.getId());

        partialUpdatedScanPallet
            .serialPallet(UPDATED_SERIAL_PALLET)
            .businessSourceId(UPDATED_BUSINESS_SOURCE_ID);

        restScanPalletMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScanPallet.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedScanPallet))
            )
            .andExpect(status().isOk());

        // Validate the ScanPallet in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertScanPalletUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedScanPallet, scanPallet),
            getPersistedScanPallet(scanPallet)
        );
    }

    @Test
    @Transactional
    void fullUpdateScanPalletWithPatch() throws Exception {
        // Initialize the database
        scanPalletRepository.saveAndFlush(scanPallet);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scanPallet using partial update
        ScanPallet partialUpdatedScanPallet = new ScanPallet();
        partialUpdatedScanPallet.setId(scanPallet.getId());

        partialUpdatedScanPallet
            .serialPallet(UPDATED_SERIAL_PALLET)
            .businessSourceId(UPDATED_BUSINESS_SOURCE_ID)
            .businessType(UPDATED_BUSINESS_TYPE);

        restScanPalletMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScanPallet.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedScanPallet))
            )
            .andExpect(status().isOk());

        // Validate the ScanPallet in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertScanPalletUpdatableFieldsEquals(
            partialUpdatedScanPallet,
            getPersistedScanPallet(partialUpdatedScanPallet)
        );
    }

    @Test
    @Transactional
    void patchNonExistingScanPallet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scanPallet.setId(longCount.incrementAndGet());

        // Create the ScanPallet
        ScanPalletDTO scanPalletDTO = scanPalletMapper.toDto(scanPallet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScanPalletMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, scanPalletDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scanPalletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScanPallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchScanPallet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scanPallet.setId(longCount.incrementAndGet());

        // Create the ScanPallet
        ScanPalletDTO scanPalletDTO = scanPalletMapper.toDto(scanPallet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScanPalletMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scanPalletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScanPallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamScanPallet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scanPallet.setId(longCount.incrementAndGet());

        // Create the ScanPallet
        ScanPalletDTO scanPalletDTO = scanPalletMapper.toDto(scanPallet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScanPalletMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scanPalletDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ScanPallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteScanPallet() throws Exception {
        // Initialize the database
        scanPalletRepository.saveAndFlush(scanPallet);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the scanPallet
        restScanPalletMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, scanPallet.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return scanPalletRepository.count();
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

    protected ScanPallet getPersistedScanPallet(ScanPallet scanPallet) {
        return scanPalletRepository.findById(scanPallet.getId()).orElseThrow();
    }

    protected void assertPersistedScanPalletToMatchAllProperties(
        ScanPallet expectedScanPallet
    ) {
        assertScanPalletAllPropertiesEquals(
            expectedScanPallet,
            getPersistedScanPallet(expectedScanPallet)
        );
    }

    protected void assertPersistedScanPalletToMatchUpdatableProperties(
        ScanPallet expectedScanPallet
    ) {
        assertScanPalletAllUpdatablePropertiesEquals(
            expectedScanPallet,
            getPersistedScanPallet(expectedScanPallet)
        );
    }
}
