package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.AttributesTypeAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.AttributesType;
import com.mycompany.myapp.repository.partner5.AttributesTypeRepository;
import com.mycompany.myapp.service.dto.AttributesTypeDTO;
import com.mycompany.myapp.service.mapper.AttributesTypeMapper;
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
 * Integration tests for the {@link AttributesTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AttributesTypeResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/attributes-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AttributesTypeRepository attributesTypeRepository;

    @Autowired
    private AttributesTypeMapper attributesTypeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAttributesTypeMockMvc;

    private AttributesType attributesType;

    private AttributesType insertedAttributesType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AttributesType createEntity() {
        return new AttributesType().description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AttributesType createUpdatedEntity() {
        return new AttributesType().description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    void initTest() {
        attributesType = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAttributesType != null) {
            attributesTypeRepository.delete(insertedAttributesType);
            insertedAttributesType = null;
        }
    }

    @Test
    @Transactional
    void createAttributesType() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AttributesType
        AttributesTypeDTO attributesTypeDTO = attributesTypeMapper.toDto(
            attributesType
        );
        var returnedAttributesTypeDTO = om.readValue(
            restAttributesTypeMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(attributesTypeDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AttributesTypeDTO.class
        );

        // Validate the AttributesType in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAttributesType = attributesTypeMapper.toEntity(
            returnedAttributesTypeDTO
        );
        assertAttributesTypeUpdatableFieldsEquals(
            returnedAttributesType,
            getPersistedAttributesType(returnedAttributesType)
        );

        insertedAttributesType = returnedAttributesType;
    }

    @Test
    @Transactional
    void createAttributesTypeWithExistingId() throws Exception {
        // Create the AttributesType with an existing ID
        attributesType.setId(1L);
        AttributesTypeDTO attributesTypeDTO = attributesTypeMapper.toDto(
            attributesType
        );

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAttributesTypeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(attributesTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AttributesType in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAttributesTypes() throws Exception {
        // Initialize the database
        insertedAttributesType = attributesTypeRepository.saveAndFlush(
            attributesType
        );

        // Get all the attributesTypeList
        restAttributesTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(attributesType.getId().intValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].description").value(
                    hasItem(DEFAULT_DESCRIPTION)
                )
            );
    }

    @Test
    @Transactional
    void getAttributesType() throws Exception {
        // Initialize the database
        insertedAttributesType = attributesTypeRepository.saveAndFlush(
            attributesType
        );

        // Get the attributesType
        restAttributesTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, attributesType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.id").value(attributesType.getId().intValue())
            )
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getAttributesTypesByIdFiltering() throws Exception {
        // Initialize the database
        insertedAttributesType = attributesTypeRepository.saveAndFlush(
            attributesType
        );

        Long id = attributesType.getId();

        defaultAttributesTypeFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAttributesTypeFiltering(
            "id.greaterThanOrEqual=" + id,
            "id.greaterThan=" + id
        );

        defaultAttributesTypeFiltering(
            "id.lessThanOrEqual=" + id,
            "id.lessThan=" + id
        );
    }

    @Test
    @Transactional
    void getAllAttributesTypesByDescriptionIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedAttributesType = attributesTypeRepository.saveAndFlush(
            attributesType
        );

        // Get all the attributesTypeList where description equals to
        defaultAttributesTypeFiltering(
            "description.equals=" + DEFAULT_DESCRIPTION,
            "description.equals=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllAttributesTypesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAttributesType = attributesTypeRepository.saveAndFlush(
            attributesType
        );

        // Get all the attributesTypeList where description in
        defaultAttributesTypeFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllAttributesTypesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAttributesType = attributesTypeRepository.saveAndFlush(
            attributesType
        );

        // Get all the attributesTypeList where description is not null
        defaultAttributesTypeFiltering(
            "description.specified=true",
            "description.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllAttributesTypesByDescriptionContainsSomething()
        throws Exception {
        // Initialize the database
        insertedAttributesType = attributesTypeRepository.saveAndFlush(
            attributesType
        );

        // Get all the attributesTypeList where description contains
        defaultAttributesTypeFiltering(
            "description.contains=" + DEFAULT_DESCRIPTION,
            "description.contains=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllAttributesTypesByDescriptionNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedAttributesType = attributesTypeRepository.saveAndFlush(
            attributesType
        );

        // Get all the attributesTypeList where description does not contain
        defaultAttributesTypeFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    private void defaultAttributesTypeFiltering(
        String shouldBeFound,
        String shouldNotBeFound
    ) throws Exception {
        defaultAttributesTypeShouldBeFound(shouldBeFound);
        defaultAttributesTypeShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAttributesTypeShouldBeFound(String filter)
        throws Exception {
        restAttributesTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(attributesType.getId().intValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].description").value(
                    hasItem(DEFAULT_DESCRIPTION)
                )
            );

        // Check, that the count call also returns 1
        restAttributesTypeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAttributesTypeShouldNotBeFound(String filter)
        throws Exception {
        restAttributesTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAttributesTypeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAttributesType() throws Exception {
        // Get the attributesType
        restAttributesTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAttributesType() throws Exception {
        // Initialize the database
        insertedAttributesType = attributesTypeRepository.saveAndFlush(
            attributesType
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the attributesType
        AttributesType updatedAttributesType = attributesTypeRepository
            .findById(attributesType.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedAttributesType are not directly saved in db
        em.detach(updatedAttributesType);
        updatedAttributesType.description(UPDATED_DESCRIPTION);
        AttributesTypeDTO attributesTypeDTO = attributesTypeMapper.toDto(
            updatedAttributesType
        );

        restAttributesTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, attributesTypeDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(attributesTypeDTO))
            )
            .andExpect(status().isOk());

        // Validate the AttributesType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAttributesTypeToMatchAllProperties(
            updatedAttributesType
        );
    }

    @Test
    @Transactional
    void putNonExistingAttributesType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attributesType.setId(longCount.incrementAndGet());

        // Create the AttributesType
        AttributesTypeDTO attributesTypeDTO = attributesTypeMapper.toDto(
            attributesType
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAttributesTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, attributesTypeDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(attributesTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AttributesType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAttributesType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attributesType.setId(longCount.incrementAndGet());

        // Create the AttributesType
        AttributesTypeDTO attributesTypeDTO = attributesTypeMapper.toDto(
            attributesType
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttributesTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(attributesTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AttributesType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAttributesType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attributesType.setId(longCount.incrementAndGet());

        // Create the AttributesType
        AttributesTypeDTO attributesTypeDTO = attributesTypeMapper.toDto(
            attributesType
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttributesTypeMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(attributesTypeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AttributesType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAttributesTypeWithPatch() throws Exception {
        // Initialize the database
        insertedAttributesType = attributesTypeRepository.saveAndFlush(
            attributesType
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the attributesType using partial update
        AttributesType partialUpdatedAttributesType = new AttributesType();
        partialUpdatedAttributesType.setId(attributesType.getId());

        partialUpdatedAttributesType.description(UPDATED_DESCRIPTION);

        restAttributesTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAttributesType.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAttributesType))
            )
            .andExpect(status().isOk());

        // Validate the AttributesType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAttributesTypeUpdatableFieldsEquals(
            createUpdateProxyForBean(
                partialUpdatedAttributesType,
                attributesType
            ),
            getPersistedAttributesType(attributesType)
        );
    }

    @Test
    @Transactional
    void fullUpdateAttributesTypeWithPatch() throws Exception {
        // Initialize the database
        insertedAttributesType = attributesTypeRepository.saveAndFlush(
            attributesType
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the attributesType using partial update
        AttributesType partialUpdatedAttributesType = new AttributesType();
        partialUpdatedAttributesType.setId(attributesType.getId());

        partialUpdatedAttributesType.description(UPDATED_DESCRIPTION);

        restAttributesTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAttributesType.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAttributesType))
            )
            .andExpect(status().isOk());

        // Validate the AttributesType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAttributesTypeUpdatableFieldsEquals(
            partialUpdatedAttributesType,
            getPersistedAttributesType(partialUpdatedAttributesType)
        );
    }

    @Test
    @Transactional
    void patchNonExistingAttributesType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attributesType.setId(longCount.incrementAndGet());

        // Create the AttributesType
        AttributesTypeDTO attributesTypeDTO = attributesTypeMapper.toDto(
            attributesType
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAttributesTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, attributesTypeDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(attributesTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AttributesType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAttributesType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attributesType.setId(longCount.incrementAndGet());

        // Create the AttributesType
        AttributesTypeDTO attributesTypeDTO = attributesTypeMapper.toDto(
            attributesType
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttributesTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(attributesTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AttributesType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAttributesType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attributesType.setId(longCount.incrementAndGet());

        // Create the AttributesType
        AttributesTypeDTO attributesTypeDTO = attributesTypeMapper.toDto(
            attributesType
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttributesTypeMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(attributesTypeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AttributesType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAttributesType() throws Exception {
        // Initialize the database
        insertedAttributesType = attributesTypeRepository.saveAndFlush(
            attributesType
        );

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the attributesType
        restAttributesTypeMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, attributesType.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return attributesTypeRepository.count();
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

    protected AttributesType getPersistedAttributesType(
        AttributesType attributesType
    ) {
        return attributesTypeRepository
            .findById(attributesType.getId())
            .orElseThrow();
    }

    protected void assertPersistedAttributesTypeToMatchAllProperties(
        AttributesType expectedAttributesType
    ) {
        assertAttributesTypeAllPropertiesEquals(
            expectedAttributesType,
            getPersistedAttributesType(expectedAttributesType)
        );
    }

    protected void assertPersistedAttributesTypeToMatchUpdatableProperties(
        AttributesType expectedAttributesType
    ) {
        assertAttributesTypeAllUpdatablePropertiesEquals(
            expectedAttributesType,
            getPersistedAttributesType(expectedAttributesType)
        );
    }
}
