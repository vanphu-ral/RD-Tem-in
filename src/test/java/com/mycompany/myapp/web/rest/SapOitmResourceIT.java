package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.SapOitmAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.partner4.SapOitm;
import com.mycompany.myapp.repository.partner4.SapOitmRepository;
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
 * Integration tests for the {@link SapOitmResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SapOitmResourceIT {

    private static final String DEFAULT_ITEM_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ITEM_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_ITEM_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ITEM_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ITMS_GRP_COD = "AAAAAAAAAA";
    private static final String UPDATED_ITMS_GRP_COD = "BBBBBBBBBB";

    private static final String DEFAULT_U_PART_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_U_PART_NUMBER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sap-oitms";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SapOitmRepository sapOitmRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSapOitmMockMvc;

    private SapOitm sapOitm;

    private SapOitm insertedSapOitm;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SapOitm createEntity() {
        return new SapOitm()
            .itemCode(DEFAULT_ITEM_CODE)
            .itemName(DEFAULT_ITEM_NAME)
            .itmsGrpCod(DEFAULT_ITMS_GRP_COD)
            .uPartNumber(DEFAULT_U_PART_NUMBER);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SapOitm createUpdatedEntity() {
        return new SapOitm()
            .itemCode(UPDATED_ITEM_CODE)
            .itemName(UPDATED_ITEM_NAME)
            .itmsGrpCod(UPDATED_ITMS_GRP_COD)
            .uPartNumber(UPDATED_U_PART_NUMBER);
    }

    @BeforeEach
    void initTest() {
        sapOitm = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSapOitm != null) {
            sapOitmRepository.delete(insertedSapOitm);
            insertedSapOitm = null;
        }
    }

    @Test
    @Transactional
    void createSapOitm() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SapOitm
        var returnedSapOitm = om.readValue(
            restSapOitmMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(sapOitm))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SapOitm.class
        );

        // Validate the SapOitm in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSapOitmUpdatableFieldsEquals(
            returnedSapOitm,
            getPersistedSapOitm(returnedSapOitm)
        );

        insertedSapOitm = returnedSapOitm;
    }

    @Test
    @Transactional
    void createSapOitmWithExistingId() throws Exception {
        // Create the SapOitm with an existing ID
        sapOitm.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSapOitmMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sapOitm))
            )
            .andExpect(status().isBadRequest());

        // Validate the SapOitm in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSapOitms() throws Exception {
        // Initialize the database
        insertedSapOitm = sapOitmRepository.saveAndFlush(sapOitm);

        // Get all the sapOitmList
        restSapOitmMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(hasItem(sapOitm.getId().intValue()))
            )
            .andExpect(
                jsonPath("$.[*].itemCode").value(hasItem(DEFAULT_ITEM_CODE))
            )
            .andExpect(
                jsonPath("$.[*].itemName").value(hasItem(DEFAULT_ITEM_NAME))
            )
            .andExpect(
                jsonPath("$.[*].itmsGrpCod").value(
                    hasItem(DEFAULT_ITMS_GRP_COD)
                )
            )
            .andExpect(
                jsonPath("$.[*].uPartNumber").value(
                    hasItem(DEFAULT_U_PART_NUMBER)
                )
            );
    }

    @Test
    @Transactional
    void getSapOitm() throws Exception {
        // Initialize the database
        insertedSapOitm = sapOitmRepository.saveAndFlush(sapOitm);

        // Get the sapOitm
        restSapOitmMockMvc
            .perform(get(ENTITY_API_URL_ID, sapOitm.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sapOitm.getId().intValue()))
            .andExpect(jsonPath("$.itemCode").value(DEFAULT_ITEM_CODE))
            .andExpect(jsonPath("$.itemName").value(DEFAULT_ITEM_NAME))
            .andExpect(jsonPath("$.itmsGrpCod").value(DEFAULT_ITMS_GRP_COD))
            .andExpect(jsonPath("$.uPartNumber").value(DEFAULT_U_PART_NUMBER));
    }

    @Test
    @Transactional
    void getNonExistingSapOitm() throws Exception {
        // Get the sapOitm
        restSapOitmMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSapOitm() throws Exception {
        // Initialize the database
        insertedSapOitm = sapOitmRepository.saveAndFlush(sapOitm);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sapOitm
        SapOitm updatedSapOitm = sapOitmRepository
            .findById(sapOitm.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedSapOitm are not directly saved in db
        em.detach(updatedSapOitm);
        updatedSapOitm
            .itemCode(UPDATED_ITEM_CODE)
            .itemName(UPDATED_ITEM_NAME)
            .itmsGrpCod(UPDATED_ITMS_GRP_COD)
            .uPartNumber(UPDATED_U_PART_NUMBER);

        restSapOitmMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSapOitm.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedSapOitm))
            )
            .andExpect(status().isOk());

        // Validate the SapOitm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSapOitmToMatchAllProperties(updatedSapOitm);
    }

    @Test
    @Transactional
    void putNonExistingSapOitm() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sapOitm.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSapOitmMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sapOitm.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sapOitm))
            )
            .andExpect(status().isBadRequest());

        // Validate the SapOitm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSapOitm() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sapOitm.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSapOitmMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sapOitm))
            )
            .andExpect(status().isBadRequest());

        // Validate the SapOitm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSapOitm() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sapOitm.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSapOitmMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sapOitm))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SapOitm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSapOitmWithPatch() throws Exception {
        // Initialize the database
        insertedSapOitm = sapOitmRepository.saveAndFlush(sapOitm);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sapOitm using partial update
        SapOitm partialUpdatedSapOitm = new SapOitm();
        partialUpdatedSapOitm.setId(sapOitm.getId());

        partialUpdatedSapOitm
            .itemName(UPDATED_ITEM_NAME)
            .uPartNumber(UPDATED_U_PART_NUMBER);

        restSapOitmMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSapOitm.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSapOitm))
            )
            .andExpect(status().isOk());

        // Validate the SapOitm in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSapOitmUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSapOitm, sapOitm),
            getPersistedSapOitm(sapOitm)
        );
    }

    @Test
    @Transactional
    void fullUpdateSapOitmWithPatch() throws Exception {
        // Initialize the database
        insertedSapOitm = sapOitmRepository.saveAndFlush(sapOitm);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sapOitm using partial update
        SapOitm partialUpdatedSapOitm = new SapOitm();
        partialUpdatedSapOitm.setId(sapOitm.getId());

        partialUpdatedSapOitm
            .itemCode(UPDATED_ITEM_CODE)
            .itemName(UPDATED_ITEM_NAME)
            .itmsGrpCod(UPDATED_ITMS_GRP_COD)
            .uPartNumber(UPDATED_U_PART_NUMBER);

        restSapOitmMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSapOitm.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSapOitm))
            )
            .andExpect(status().isOk());

        // Validate the SapOitm in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSapOitmUpdatableFieldsEquals(
            partialUpdatedSapOitm,
            getPersistedSapOitm(partialUpdatedSapOitm)
        );
    }

    @Test
    @Transactional
    void patchNonExistingSapOitm() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sapOitm.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSapOitmMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sapOitm.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sapOitm))
            )
            .andExpect(status().isBadRequest());

        // Validate the SapOitm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSapOitm() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sapOitm.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSapOitmMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sapOitm))
            )
            .andExpect(status().isBadRequest());

        // Validate the SapOitm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSapOitm() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sapOitm.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSapOitmMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sapOitm))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SapOitm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSapOitm() throws Exception {
        // Initialize the database
        insertedSapOitm = sapOitmRepository.saveAndFlush(sapOitm);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the sapOitm
        restSapOitmMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, sapOitm.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return sapOitmRepository.count();
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

    protected SapOitm getPersistedSapOitm(SapOitm sapOitm) {
        return sapOitmRepository.findById(sapOitm.getId()).orElseThrow();
    }

    protected void assertPersistedSapOitmToMatchAllProperties(
        SapOitm expectedSapOitm
    ) {
        assertSapOitmAllPropertiesEquals(
            expectedSapOitm,
            getPersistedSapOitm(expectedSapOitm)
        );
    }

    protected void assertPersistedSapOitmToMatchUpdatableProperties(
        SapOitm expectedSapOitm
    ) {
        assertSapOitmAllUpdatablePropertiesEquals(
            expectedSapOitm,
            getPersistedSapOitm(expectedSapOitm)
        );
    }
}
