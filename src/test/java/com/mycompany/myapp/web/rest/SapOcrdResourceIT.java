package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.SapOcrdAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.SapOcrd;
import com.mycompany.myapp.repository.partner4.SapOcrdRepository;
import com.mycompany.myapp.service.dto.SapOcrdDTO;
import com.mycompany.myapp.service.mapper.SapOcrdMapper;
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
 * Integration tests for the {@link SapOcrdResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SapOcrdResourceIT {

    private static final String DEFAULT_CARD_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CARD_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_CARD_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_CARD_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_CARD_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CARD_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CARD_F_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CARD_F_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_GROUP_CODE = "AAAAAAAAAA";
    private static final String UPDATED_GROUP_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_CURRENCY = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY = "BBBBBBBBBB";

    private static final String DEFAULT_LIC_TRAD_NUM = "AAAAAAAAAA";
    private static final String UPDATED_LIC_TRAD_NUM = "BBBBBBBBBB";

    private static final String DEFAULT_DDD_ID = "AAAAAAAAAA";
    private static final String UPDATED_DDD_ID = "BBBBBBBBBB";

    private static final String DEFAULT_E_MAIL = "AAAAAAAAAA";
    private static final String UPDATED_E_MAIL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sap-ocrds";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SapOcrdRepository sapOcrdRepository;

    @Autowired
    private SapOcrdMapper sapOcrdMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSapOcrdMockMvc;

    private SapOcrd sapOcrd;

    private SapOcrd insertedSapOcrd;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SapOcrd createEntity() {
        return new SapOcrd()
            .cardCode(DEFAULT_CARD_CODE)
            .cardType(DEFAULT_CARD_TYPE)
            .cardName(DEFAULT_CARD_NAME)
            .cardFName(DEFAULT_CARD_F_NAME)
            .groupCode(DEFAULT_GROUP_CODE)
            .currency(DEFAULT_CURRENCY)
            .licTradNum(DEFAULT_LIC_TRAD_NUM)
            .dddId(DEFAULT_DDD_ID)
            .eMail(DEFAULT_E_MAIL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SapOcrd createUpdatedEntity() {
        return new SapOcrd()
            .cardCode(UPDATED_CARD_CODE)
            .cardType(UPDATED_CARD_TYPE)
            .cardName(UPDATED_CARD_NAME)
            .cardFName(UPDATED_CARD_F_NAME)
            .groupCode(UPDATED_GROUP_CODE)
            .currency(UPDATED_CURRENCY)
            .licTradNum(UPDATED_LIC_TRAD_NUM)
            .dddId(UPDATED_DDD_ID)
            .eMail(UPDATED_E_MAIL);
    }

    @BeforeEach
    void initTest() {
        sapOcrd = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSapOcrd != null) {
            sapOcrdRepository.delete(insertedSapOcrd);
            insertedSapOcrd = null;
        }
    }

    @Test
    @Transactional
    void createSapOcrd() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SapOcrd
        SapOcrdDTO sapOcrdDTO = sapOcrdMapper.toDto(sapOcrd);
        var returnedSapOcrdDTO = om.readValue(
            restSapOcrdMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(sapOcrdDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SapOcrdDTO.class
        );

        // Validate the SapOcrd in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSapOcrd = sapOcrdMapper.toEntity(returnedSapOcrdDTO);
        assertSapOcrdUpdatableFieldsEquals(
            returnedSapOcrd,
            getPersistedSapOcrd(returnedSapOcrd)
        );

        insertedSapOcrd = returnedSapOcrd;
    }

    @Test
    @Transactional
    void createSapOcrdWithExistingId() throws Exception {
        // Create the SapOcrd with an existing ID
        sapOcrd.setId(1L);
        SapOcrdDTO sapOcrdDTO = sapOcrdMapper.toDto(sapOcrd);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSapOcrdMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sapOcrdDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SapOcrd in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSapOcrds() throws Exception {
        // Initialize the database
        insertedSapOcrd = sapOcrdRepository.saveAndFlush(sapOcrd);

        // Get all the sapOcrdList
        restSapOcrdMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(hasItem(sapOcrd.getId().intValue()))
            )
            .andExpect(
                jsonPath("$.[*].cardCode").value(hasItem(DEFAULT_CARD_CODE))
            )
            .andExpect(
                jsonPath("$.[*].cardType").value(hasItem(DEFAULT_CARD_TYPE))
            )
            .andExpect(
                jsonPath("$.[*].cardName").value(hasItem(DEFAULT_CARD_NAME))
            )
            .andExpect(
                jsonPath("$.[*].cardFName").value(hasItem(DEFAULT_CARD_F_NAME))
            )
            .andExpect(
                jsonPath("$.[*].groupCode").value(hasItem(DEFAULT_GROUP_CODE))
            )
            .andExpect(
                jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY))
            )
            .andExpect(
                jsonPath("$.[*].licTradNum").value(
                    hasItem(DEFAULT_LIC_TRAD_NUM)
                )
            )
            .andExpect(jsonPath("$.[*].dddId").value(hasItem(DEFAULT_DDD_ID)))
            .andExpect(jsonPath("$.[*].eMail").value(hasItem(DEFAULT_E_MAIL)));
    }

    @Test
    @Transactional
    void getSapOcrd() throws Exception {
        // Initialize the database
        insertedSapOcrd = sapOcrdRepository.saveAndFlush(sapOcrd);

        // Get the sapOcrd
        restSapOcrdMockMvc
            .perform(get(ENTITY_API_URL_ID, sapOcrd.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sapOcrd.getId().intValue()))
            .andExpect(jsonPath("$.cardCode").value(DEFAULT_CARD_CODE))
            .andExpect(jsonPath("$.cardType").value(DEFAULT_CARD_TYPE))
            .andExpect(jsonPath("$.cardName").value(DEFAULT_CARD_NAME))
            .andExpect(jsonPath("$.cardFName").value(DEFAULT_CARD_F_NAME))
            .andExpect(jsonPath("$.groupCode").value(DEFAULT_GROUP_CODE))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY))
            .andExpect(jsonPath("$.licTradNum").value(DEFAULT_LIC_TRAD_NUM))
            .andExpect(jsonPath("$.dddId").value(DEFAULT_DDD_ID))
            .andExpect(jsonPath("$.eMail").value(DEFAULT_E_MAIL));
    }

    @Test
    @Transactional
    void getNonExistingSapOcrd() throws Exception {
        // Get the sapOcrd
        restSapOcrdMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSapOcrd() throws Exception {
        // Initialize the database
        insertedSapOcrd = sapOcrdRepository.saveAndFlush(sapOcrd);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sapOcrd
        SapOcrd updatedSapOcrd = sapOcrdRepository
            .findById(sapOcrd.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedSapOcrd are not directly saved in db
        em.detach(updatedSapOcrd);
        updatedSapOcrd
            .cardCode(UPDATED_CARD_CODE)
            .cardType(UPDATED_CARD_TYPE)
            .cardName(UPDATED_CARD_NAME)
            .cardFName(UPDATED_CARD_F_NAME)
            .groupCode(UPDATED_GROUP_CODE)
            .currency(UPDATED_CURRENCY)
            .licTradNum(UPDATED_LIC_TRAD_NUM)
            .dddId(UPDATED_DDD_ID)
            .eMail(UPDATED_E_MAIL);
        SapOcrdDTO sapOcrdDTO = sapOcrdMapper.toDto(updatedSapOcrd);

        restSapOcrdMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sapOcrdDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sapOcrdDTO))
            )
            .andExpect(status().isOk());

        // Validate the SapOcrd in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSapOcrdToMatchAllProperties(updatedSapOcrd);
    }

    @Test
    @Transactional
    void putNonExistingSapOcrd() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sapOcrd.setId(longCount.incrementAndGet());

        // Create the SapOcrd
        SapOcrdDTO sapOcrdDTO = sapOcrdMapper.toDto(sapOcrd);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSapOcrdMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sapOcrdDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sapOcrdDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SapOcrd in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSapOcrd() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sapOcrd.setId(longCount.incrementAndGet());

        // Create the SapOcrd
        SapOcrdDTO sapOcrdDTO = sapOcrdMapper.toDto(sapOcrd);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSapOcrdMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sapOcrdDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SapOcrd in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSapOcrd() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sapOcrd.setId(longCount.incrementAndGet());

        // Create the SapOcrd
        SapOcrdDTO sapOcrdDTO = sapOcrdMapper.toDto(sapOcrd);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSapOcrdMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sapOcrdDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SapOcrd in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSapOcrdWithPatch() throws Exception {
        // Initialize the database
        insertedSapOcrd = sapOcrdRepository.saveAndFlush(sapOcrd);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sapOcrd using partial update
        SapOcrd partialUpdatedSapOcrd = new SapOcrd();
        partialUpdatedSapOcrd.setId(sapOcrd.getId());

        partialUpdatedSapOcrd
            .cardType(UPDATED_CARD_TYPE)
            .cardName(UPDATED_CARD_NAME)
            .cardFName(UPDATED_CARD_F_NAME)
            .currency(UPDATED_CURRENCY);

        restSapOcrdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSapOcrd.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSapOcrd))
            )
            .andExpect(status().isOk());

        // Validate the SapOcrd in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSapOcrdUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSapOcrd, sapOcrd),
            getPersistedSapOcrd(sapOcrd)
        );
    }

    @Test
    @Transactional
    void fullUpdateSapOcrdWithPatch() throws Exception {
        // Initialize the database
        insertedSapOcrd = sapOcrdRepository.saveAndFlush(sapOcrd);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sapOcrd using partial update
        SapOcrd partialUpdatedSapOcrd = new SapOcrd();
        partialUpdatedSapOcrd.setId(sapOcrd.getId());

        partialUpdatedSapOcrd
            .cardCode(UPDATED_CARD_CODE)
            .cardType(UPDATED_CARD_TYPE)
            .cardName(UPDATED_CARD_NAME)
            .cardFName(UPDATED_CARD_F_NAME)
            .groupCode(UPDATED_GROUP_CODE)
            .currency(UPDATED_CURRENCY)
            .licTradNum(UPDATED_LIC_TRAD_NUM)
            .dddId(UPDATED_DDD_ID)
            .eMail(UPDATED_E_MAIL);

        restSapOcrdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSapOcrd.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSapOcrd))
            )
            .andExpect(status().isOk());

        // Validate the SapOcrd in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSapOcrdUpdatableFieldsEquals(
            partialUpdatedSapOcrd,
            getPersistedSapOcrd(partialUpdatedSapOcrd)
        );
    }

    @Test
    @Transactional
    void patchNonExistingSapOcrd() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sapOcrd.setId(longCount.incrementAndGet());

        // Create the SapOcrd
        SapOcrdDTO sapOcrdDTO = sapOcrdMapper.toDto(sapOcrd);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSapOcrdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sapOcrdDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sapOcrdDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SapOcrd in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSapOcrd() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sapOcrd.setId(longCount.incrementAndGet());

        // Create the SapOcrd
        SapOcrdDTO sapOcrdDTO = sapOcrdMapper.toDto(sapOcrd);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSapOcrdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sapOcrdDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SapOcrd in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSapOcrd() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sapOcrd.setId(longCount.incrementAndGet());

        // Create the SapOcrd
        SapOcrdDTO sapOcrdDTO = sapOcrdMapper.toDto(sapOcrd);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSapOcrdMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sapOcrdDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SapOcrd in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSapOcrd() throws Exception {
        // Initialize the database
        insertedSapOcrd = sapOcrdRepository.saveAndFlush(sapOcrd);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the sapOcrd
        restSapOcrdMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, sapOcrd.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return sapOcrdRepository.count();
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

    protected SapOcrd getPersistedSapOcrd(SapOcrd sapOcrd) {
        return sapOcrdRepository.findById(sapOcrd.getId()).orElseThrow();
    }

    protected void assertPersistedSapOcrdToMatchAllProperties(
        SapOcrd expectedSapOcrd
    ) {
        assertSapOcrdAllPropertiesEquals(
            expectedSapOcrd,
            getPersistedSapOcrd(expectedSapOcrd)
        );
    }

    protected void assertPersistedSapOcrdToMatchUpdatableProperties(
        SapOcrd expectedSapOcrd
    ) {
        assertSapOcrdAllUpdatablePropertiesEquals(
            expectedSapOcrd,
            getPersistedSapOcrd(expectedSapOcrd)
        );
    }
}
