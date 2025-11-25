package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.SerialBoxPalletMappingAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.SerialBoxPalletMapping;
import com.mycompany.myapp.repository.SerialBoxPalletMappingRepository;
import com.mycompany.myapp.service.dto.SerialBoxPalletMappingDTO;
import com.mycompany.myapp.service.mapper.SerialBoxPalletMappingMapper;
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
 * Integration tests for the {@link SerialBoxPalletMappingResource} REST
 * controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SerialBoxPalletMappingResourceIT {

    private static final String DEFAULT_SERIAL_BOX = "AAAAAAAAAA";
    private static final String UPDATED_SERIAL_BOX = "BBBBBBBBBB";

    private static final String DEFAULT_SERIAL_PALLET = "AAAAAAAAAA";
    private static final String UPDATED_SERIAL_PALLET = "BBBBBBBBBB";

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(
        ChronoUnit.MILLIS
    );

    private static final String DEFAULT_UPDATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_UPDATED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL =
        "/api/serial-box-pallet-mappings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SerialBoxPalletMappingRepository serialBoxPalletMappingRepository;

    @Autowired
    private SerialBoxPalletMappingMapper serialBoxPalletMappingMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSerialBoxPalletMappingMockMvc;

    private SerialBoxPalletMapping serialBoxPalletMapping;

    private SerialBoxPalletMapping insertedSerialBoxPalletMapping;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SerialBoxPalletMapping createEntity() {
        return new SerialBoxPalletMapping()
            .serialBox(DEFAULT_SERIAL_BOX)
            .serialPallet(DEFAULT_SERIAL_PALLET)
            .updatedAt(DEFAULT_UPDATED_AT)
            .updatedBy(DEFAULT_UPDATED_BY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SerialBoxPalletMapping createUpdatedEntity() {
        return new SerialBoxPalletMapping()
            .serialBox(UPDATED_SERIAL_BOX)
            .serialPallet(UPDATED_SERIAL_PALLET)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY);
    }

    @BeforeEach
    void initTest() {
        serialBoxPalletMapping = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSerialBoxPalletMapping != null) {
            serialBoxPalletMappingRepository.delete(
                insertedSerialBoxPalletMapping
            );
            insertedSerialBoxPalletMapping = null;
        }
    }

    @Test
    @Transactional
    void createSerialBoxPalletMapping() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SerialBoxPalletMapping
        SerialBoxPalletMappingDTO serialBoxPalletMappingDTO =
            serialBoxPalletMappingMapper.toDto(serialBoxPalletMapping);
        var returnedSerialBoxPalletMappingDTO = om.readValue(
            restSerialBoxPalletMappingMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            om.writeValueAsBytes(serialBoxPalletMappingDTO)
                        )
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SerialBoxPalletMappingDTO.class
        );

        // Validate the SerialBoxPalletMapping in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSerialBoxPalletMapping =
            serialBoxPalletMappingMapper.toEntity(
                returnedSerialBoxPalletMappingDTO
            );
        assertSerialBoxPalletMappingUpdatableFieldsEquals(
            returnedSerialBoxPalletMapping,
            getPersistedSerialBoxPalletMapping(returnedSerialBoxPalletMapping)
        );

        insertedSerialBoxPalletMapping = returnedSerialBoxPalletMapping;
    }

    @Test
    @Transactional
    void createSerialBoxPalletMappingWithExistingId() throws Exception {
        // Create the SerialBoxPalletMapping with an existing ID
        serialBoxPalletMapping.setId(1L);
        SerialBoxPalletMappingDTO serialBoxPalletMappingDTO =
            serialBoxPalletMappingMapper.toDto(serialBoxPalletMapping);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSerialBoxPalletMappingMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(serialBoxPalletMappingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SerialBoxPalletMapping in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSerialBoxPalletMappings() throws Exception {
        // Initialize the database
        insertedSerialBoxPalletMapping =
            serialBoxPalletMappingRepository.saveAndFlush(
                serialBoxPalletMapping
            );

        // Get all the serialBoxPalletMappingList
        restSerialBoxPalletMappingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(serialBoxPalletMapping.getId().intValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].serialBox").value(hasItem(DEFAULT_SERIAL_BOX))
            )
            .andExpect(
                jsonPath("$.[*].serialPallet").value(
                    hasItem(DEFAULT_SERIAL_PALLET)
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
    void getSerialBoxPalletMapping() throws Exception {
        // Initialize the database
        insertedSerialBoxPalletMapping =
            serialBoxPalletMappingRepository.saveAndFlush(
                serialBoxPalletMapping
            );

        // Get the serialBoxPalletMapping
        restSerialBoxPalletMappingMockMvc
            .perform(get(ENTITY_API_URL_ID, serialBoxPalletMapping.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.id").value(
                    serialBoxPalletMapping.getId().intValue()
                )
            )
            .andExpect(jsonPath("$.serialBox").value(DEFAULT_SERIAL_BOX))
            .andExpect(jsonPath("$.serialPallet").value(DEFAULT_SERIAL_PALLET))
            .andExpect(
                jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString())
            )
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY));
    }

    @Test
    @Transactional
    void getNonExistingSerialBoxPalletMapping() throws Exception {
        // Get the serialBoxPalletMapping
        restSerialBoxPalletMappingMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSerialBoxPalletMapping() throws Exception {
        // Initialize the database
        insertedSerialBoxPalletMapping =
            serialBoxPalletMappingRepository.saveAndFlush(
                serialBoxPalletMapping
            );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the serialBoxPalletMapping
        SerialBoxPalletMapping updatedSerialBoxPalletMapping =
            serialBoxPalletMappingRepository
                .findById(serialBoxPalletMapping.getId())
                .orElseThrow();
        // Disconnect from session so that the updates on updatedSerialBoxPalletMapping
        // are not directly saved in db
        em.detach(updatedSerialBoxPalletMapping);
        updatedSerialBoxPalletMapping
            .serialBox(UPDATED_SERIAL_BOX)
            .serialPallet(UPDATED_SERIAL_PALLET)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY);
        SerialBoxPalletMappingDTO serialBoxPalletMappingDTO =
            serialBoxPalletMappingMapper.toDto(updatedSerialBoxPalletMapping);

        restSerialBoxPalletMappingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, serialBoxPalletMappingDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(serialBoxPalletMappingDTO))
            )
            .andExpect(status().isOk());

        // Validate the SerialBoxPalletMapping in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSerialBoxPalletMappingToMatchAllProperties(
            updatedSerialBoxPalletMapping
        );
    }

    @Test
    @Transactional
    void putNonExistingSerialBoxPalletMapping() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serialBoxPalletMapping.setId(longCount.incrementAndGet());

        // Create the SerialBoxPalletMapping
        SerialBoxPalletMappingDTO serialBoxPalletMappingDTO =
            serialBoxPalletMappingMapper.toDto(serialBoxPalletMapping);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSerialBoxPalletMappingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, serialBoxPalletMappingDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(serialBoxPalletMappingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SerialBoxPalletMapping in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSerialBoxPalletMapping() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serialBoxPalletMapping.setId(longCount.incrementAndGet());

        // Create the SerialBoxPalletMapping
        SerialBoxPalletMappingDTO serialBoxPalletMappingDTO =
            serialBoxPalletMappingMapper.toDto(serialBoxPalletMapping);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSerialBoxPalletMappingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(serialBoxPalletMappingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SerialBoxPalletMapping in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSerialBoxPalletMapping() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serialBoxPalletMapping.setId(longCount.incrementAndGet());

        // Create the SerialBoxPalletMapping
        SerialBoxPalletMappingDTO serialBoxPalletMappingDTO =
            serialBoxPalletMappingMapper.toDto(serialBoxPalletMapping);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSerialBoxPalletMappingMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(serialBoxPalletMappingDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SerialBoxPalletMapping in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSerialBoxPalletMappingWithPatch() throws Exception {
        // Initialize the database
        insertedSerialBoxPalletMapping =
            serialBoxPalletMappingRepository.saveAndFlush(
                serialBoxPalletMapping
            );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the serialBoxPalletMapping using partial update
        SerialBoxPalletMapping partialUpdatedSerialBoxPalletMapping =
            new SerialBoxPalletMapping();
        partialUpdatedSerialBoxPalletMapping.setId(
            serialBoxPalletMapping.getId()
        );

        partialUpdatedSerialBoxPalletMapping
            .serialBox(UPDATED_SERIAL_BOX)
            .serialPallet(UPDATED_SERIAL_PALLET);

        restSerialBoxPalletMappingMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    partialUpdatedSerialBoxPalletMapping.getId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(
                            partialUpdatedSerialBoxPalletMapping
                        )
                    )
            )
            .andExpect(status().isOk());

        // Validate the SerialBoxPalletMapping in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSerialBoxPalletMappingUpdatableFieldsEquals(
            createUpdateProxyForBean(
                partialUpdatedSerialBoxPalletMapping,
                serialBoxPalletMapping
            ),
            getPersistedSerialBoxPalletMapping(serialBoxPalletMapping)
        );
    }

    @Test
    @Transactional
    void fullUpdateSerialBoxPalletMappingWithPatch() throws Exception {
        // Initialize the database
        insertedSerialBoxPalletMapping =
            serialBoxPalletMappingRepository.saveAndFlush(
                serialBoxPalletMapping
            );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the serialBoxPalletMapping using partial update
        SerialBoxPalletMapping partialUpdatedSerialBoxPalletMapping =
            new SerialBoxPalletMapping();
        partialUpdatedSerialBoxPalletMapping.setId(
            serialBoxPalletMapping.getId()
        );

        partialUpdatedSerialBoxPalletMapping
            .serialBox(UPDATED_SERIAL_BOX)
            .serialPallet(UPDATED_SERIAL_PALLET)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY);

        restSerialBoxPalletMappingMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    partialUpdatedSerialBoxPalletMapping.getId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(
                            partialUpdatedSerialBoxPalletMapping
                        )
                    )
            )
            .andExpect(status().isOk());

        // Validate the SerialBoxPalletMapping in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSerialBoxPalletMappingUpdatableFieldsEquals(
            partialUpdatedSerialBoxPalletMapping,
            getPersistedSerialBoxPalletMapping(
                partialUpdatedSerialBoxPalletMapping
            )
        );
    }

    @Test
    @Transactional
    void patchNonExistingSerialBoxPalletMapping() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serialBoxPalletMapping.setId(longCount.incrementAndGet());

        // Create the SerialBoxPalletMapping
        SerialBoxPalletMappingDTO serialBoxPalletMappingDTO =
            serialBoxPalletMappingMapper.toDto(serialBoxPalletMapping);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSerialBoxPalletMappingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, serialBoxPalletMappingDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(serialBoxPalletMappingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SerialBoxPalletMapping in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSerialBoxPalletMapping() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serialBoxPalletMapping.setId(longCount.incrementAndGet());

        // Create the SerialBoxPalletMapping
        SerialBoxPalletMappingDTO serialBoxPalletMappingDTO =
            serialBoxPalletMappingMapper.toDto(serialBoxPalletMapping);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSerialBoxPalletMappingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(serialBoxPalletMappingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SerialBoxPalletMapping in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSerialBoxPalletMapping() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serialBoxPalletMapping.setId(longCount.incrementAndGet());

        // Create the SerialBoxPalletMapping
        SerialBoxPalletMappingDTO serialBoxPalletMappingDTO =
            serialBoxPalletMappingMapper.toDto(serialBoxPalletMapping);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSerialBoxPalletMappingMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(serialBoxPalletMappingDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SerialBoxPalletMapping in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSerialBoxPalletMapping() throws Exception {
        // Initialize the database
        insertedSerialBoxPalletMapping =
            serialBoxPalletMappingRepository.saveAndFlush(
                serialBoxPalletMapping
            );

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the serialBoxPalletMapping
        restSerialBoxPalletMappingMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, serialBoxPalletMapping.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return serialBoxPalletMappingRepository.count();
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

    protected SerialBoxPalletMapping getPersistedSerialBoxPalletMapping(
        SerialBoxPalletMapping serialBoxPalletMapping
    ) {
        return serialBoxPalletMappingRepository
            .findById(serialBoxPalletMapping.getId())
            .orElseThrow();
    }

    protected void assertPersistedSerialBoxPalletMappingToMatchAllProperties(
        SerialBoxPalletMapping expectedSerialBoxPalletMapping
    ) {
        assertSerialBoxPalletMappingAllPropertiesEquals(
            expectedSerialBoxPalletMapping,
            getPersistedSerialBoxPalletMapping(expectedSerialBoxPalletMapping)
        );
    }

    protected void assertPersistedSerialBoxPalletMappingToMatchUpdatableProperties(
        SerialBoxPalletMapping expectedSerialBoxPalletMapping
    ) {
        assertSerialBoxPalletMappingAllUpdatablePropertiesEquals(
            expectedSerialBoxPalletMapping,
            getPersistedSerialBoxPalletMapping(expectedSerialBoxPalletMapping)
        );
    }
}
