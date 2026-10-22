package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.OwtqWarehouseTransferSessionAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.OwtqWarehouseTransferSession;
import com.mycompany.myapp.repository.OwtqWarehouseTransferSessionRepository;
import com.mycompany.myapp.service.dto.OwtqWarehouseTransferSessionDTO;
import com.mycompany.myapp.service.mapper.OwtqWarehouseTransferSessionMapper;
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
 * Integration tests for the {@link OwtqWarehouseTransferSessionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OwtqWarehouseTransferSessionResourceIT {

    private static final Long DEFAULT_OWTQ_WAREHOUSE_TRANSFER_SESSION_ID = 1L;
    private static final Long UPDATED_OWTQ_WAREHOUSE_TRANSFER_SESSION_ID = 2L;

    private static final Long DEFAULT_OWTQ_DOC_ENTRY = 1L;
    private static final Long UPDATED_OWTQ_DOC_ENTRY = 2L;

    private static final String ENTITY_API_URL =
        "/api/owtq-warehouse-transfer-sessions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OwtqWarehouseTransferSessionRepository owtqWarehouseTransferSessionRepository;

    @Autowired
    private OwtqWarehouseTransferSessionMapper owtqWarehouseTransferSessionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOwtqWarehouseTransferSessionMockMvc;

    private OwtqWarehouseTransferSession owtqWarehouseTransferSession;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OwtqWarehouseTransferSession createEntity(EntityManager em) {
        OwtqWarehouseTransferSession owtqWarehouseTransferSession =
            new OwtqWarehouseTransferSession()
                .warehouseTransferSessionId(
                    DEFAULT_OWTQ_WAREHOUSE_TRANSFER_SESSION_ID
                )
                .owtqDocEntry(DEFAULT_OWTQ_DOC_ENTRY);
        return owtqWarehouseTransferSession;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OwtqWarehouseTransferSession createUpdatedEntity(
        EntityManager em
    ) {
        OwtqWarehouseTransferSession owtqWarehouseTransferSession =
            new OwtqWarehouseTransferSession()
                .warehouseTransferSessionId(
                    UPDATED_OWTQ_WAREHOUSE_TRANSFER_SESSION_ID
                )
                .owtqDocEntry(UPDATED_OWTQ_DOC_ENTRY);
        return owtqWarehouseTransferSession;
    }

    @BeforeEach
    public void initTest() {
        owtqWarehouseTransferSession = createEntity(em);
    }

    @Test
    @Transactional
    void createOwtqWarehouseTransferSession() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the OwtqWarehouseTransferSession
        OwtqWarehouseTransferSessionDTO owtqWarehouseTransferSessionDTO =
            owtqWarehouseTransferSessionMapper.toDto(
                owtqWarehouseTransferSession
            );
        var returnedOwtqWarehouseTransferSessionDTO = om.readValue(
            restOwtqWarehouseTransferSessionMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            om.writeValueAsBytes(
                                owtqWarehouseTransferSessionDTO
                            )
                        )
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            OwtqWarehouseTransferSessionDTO.class
        );

        // Validate the OwtqWarehouseTransferSession in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOwtqWarehouseTransferSession =
            owtqWarehouseTransferSessionMapper.toEntity(
                returnedOwtqWarehouseTransferSessionDTO
            );
        assertOwtqWarehouseTransferSessionUpdatableFieldsEquals(
            returnedOwtqWarehouseTransferSession,
            getPersistedOwtqWarehouseTransferSession(
                returnedOwtqWarehouseTransferSession
            )
        );
    }

    @Test
    @Transactional
    void createOwtqWarehouseTransferSessionWithExistingId() throws Exception {
        // Create the OwtqWarehouseTransferSession with an existing ID
        owtqWarehouseTransferSession.setId(1L);
        OwtqWarehouseTransferSessionDTO owtqWarehouseTransferSessionDTO =
            owtqWarehouseTransferSessionMapper.toDto(
                owtqWarehouseTransferSession
            );

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOwtqWarehouseTransferSessionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        om.writeValueAsBytes(owtqWarehouseTransferSessionDTO)
                    )
            )
            .andExpect(status().isBadRequest());

        // Validate the OwtqWarehouseTransferSession in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllOwtqWarehouseTransferSessions() throws Exception {
        // Initialize the database
        owtqWarehouseTransferSessionRepository.saveAndFlush(
            owtqWarehouseTransferSession
        );

        // Get all the owtqWarehouseTransferSessionList
        restOwtqWarehouseTransferSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(owtqWarehouseTransferSession.getId().intValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].warehouseTransferSessionId").value(
                    hasItem(
                        DEFAULT_OWTQ_WAREHOUSE_TRANSFER_SESSION_ID.intValue()
                    )
                )
            )
            .andExpect(
                jsonPath("$.[*].owtqDocEntry").value(
                    hasItem(DEFAULT_OWTQ_DOC_ENTRY.intValue())
                )
            );
    }

    @Test
    @Transactional
    void getOwtqWarehouseTransferSession() throws Exception {
        // Initialize the database
        owtqWarehouseTransferSessionRepository.saveAndFlush(
            owtqWarehouseTransferSession
        );

        // Get the owtqWarehouseTransferSession
        restOwtqWarehouseTransferSessionMockMvc
            .perform(
                get(ENTITY_API_URL_ID, owtqWarehouseTransferSession.getId())
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.id").value(
                    owtqWarehouseTransferSession.getId().intValue()
                )
            )
            .andExpect(
                jsonPath("$.warehouseTransferSessionId").value(
                    DEFAULT_OWTQ_WAREHOUSE_TRANSFER_SESSION_ID.intValue()
                )
            )
            .andExpect(
                jsonPath("$.owtqDocEntry").value(
                    DEFAULT_OWTQ_DOC_ENTRY.intValue()
                )
            );
    }

    @Test
    @Transactional
    void getNonExistingOwtqWarehouseTransferSession() throws Exception {
        // Get the owtqWarehouseTransferSession
        restOwtqWarehouseTransferSessionMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOwtqWarehouseTransferSession() throws Exception {
        // Initialize the database
        owtqWarehouseTransferSessionRepository.saveAndFlush(
            owtqWarehouseTransferSession
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the owtqWarehouseTransferSession
        OwtqWarehouseTransferSession updatedOwtqWarehouseTransferSession =
            owtqWarehouseTransferSessionRepository
                .findById(owtqWarehouseTransferSession.getId())
                .orElseThrow();
        // Disconnect from session so that the updates on updatedOwtqWarehouseTransferSession are not directly saved in db
        em.detach(updatedOwtqWarehouseTransferSession);
        updatedOwtqWarehouseTransferSession
            .warehouseTransferSessionId(
                UPDATED_OWTQ_WAREHOUSE_TRANSFER_SESSION_ID
            )
            .owtqDocEntry(UPDATED_OWTQ_DOC_ENTRY);
        OwtqWarehouseTransferSessionDTO owtqWarehouseTransferSessionDTO =
            owtqWarehouseTransferSessionMapper.toDto(
                updatedOwtqWarehouseTransferSession
            );

        restOwtqWarehouseTransferSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, owtqWarehouseTransferSessionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        om.writeValueAsBytes(owtqWarehouseTransferSessionDTO)
                    )
            )
            .andExpect(status().isOk());

        // Validate the OwtqWarehouseTransferSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOwtqWarehouseTransferSessionToMatchAllProperties(
            updatedOwtqWarehouseTransferSession
        );
    }

    @Test
    @Transactional
    void putNonExistingOwtqWarehouseTransferSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        owtqWarehouseTransferSession.setId(longCount.incrementAndGet());

        // Create the OwtqWarehouseTransferSession
        OwtqWarehouseTransferSessionDTO owtqWarehouseTransferSessionDTO =
            owtqWarehouseTransferSessionMapper.toDto(
                owtqWarehouseTransferSession
            );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOwtqWarehouseTransferSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, owtqWarehouseTransferSessionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        om.writeValueAsBytes(owtqWarehouseTransferSessionDTO)
                    )
            )
            .andExpect(status().isBadRequest());

        // Validate the OwtqWarehouseTransferSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOwtqWarehouseTransferSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        owtqWarehouseTransferSession.setId(longCount.incrementAndGet());

        // Create the OwtqWarehouseTransferSession
        OwtqWarehouseTransferSessionDTO owtqWarehouseTransferSessionDTO =
            owtqWarehouseTransferSessionMapper.toDto(
                owtqWarehouseTransferSession
            );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOwtqWarehouseTransferSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        om.writeValueAsBytes(owtqWarehouseTransferSessionDTO)
                    )
            )
            .andExpect(status().isBadRequest());

        // Validate the OwtqWarehouseTransferSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOwtqWarehouseTransferSession()
        throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        owtqWarehouseTransferSession.setId(longCount.incrementAndGet());

        // Create the OwtqWarehouseTransferSession
        OwtqWarehouseTransferSessionDTO owtqWarehouseTransferSessionDTO =
            owtqWarehouseTransferSessionMapper.toDto(
                owtqWarehouseTransferSession
            );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOwtqWarehouseTransferSessionMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        om.writeValueAsBytes(owtqWarehouseTransferSessionDTO)
                    )
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OwtqWarehouseTransferSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOwtqWarehouseTransferSessionWithPatch() throws Exception {
        // Initialize the database
        owtqWarehouseTransferSessionRepository.saveAndFlush(
            owtqWarehouseTransferSession
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the owtqWarehouseTransferSession using partial update
        OwtqWarehouseTransferSession partialUpdatedOwtqWarehouseTransferSession =
            new OwtqWarehouseTransferSession();
        partialUpdatedOwtqWarehouseTransferSession.setId(
            owtqWarehouseTransferSession.getId()
        );

        restOwtqWarehouseTransferSessionMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    partialUpdatedOwtqWarehouseTransferSession.getId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(
                            partialUpdatedOwtqWarehouseTransferSession
                        )
                    )
            )
            .andExpect(status().isOk());

        // Validate the OwtqWarehouseTransferSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOwtqWarehouseTransferSessionUpdatableFieldsEquals(
            createUpdateProxyForBean(
                partialUpdatedOwtqWarehouseTransferSession,
                owtqWarehouseTransferSession
            ),
            getPersistedOwtqWarehouseTransferSession(
                owtqWarehouseTransferSession
            )
        );
    }

    @Test
    @Transactional
    void fullUpdateOwtqWarehouseTransferSessionWithPatch() throws Exception {
        // Initialize the database
        owtqWarehouseTransferSessionRepository.saveAndFlush(
            owtqWarehouseTransferSession
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the owtqWarehouseTransferSession using partial update
        OwtqWarehouseTransferSession partialUpdatedOwtqWarehouseTransferSession =
            new OwtqWarehouseTransferSession();
        partialUpdatedOwtqWarehouseTransferSession.setId(
            owtqWarehouseTransferSession.getId()
        );

        partialUpdatedOwtqWarehouseTransferSession
            .warehouseTransferSessionId(
                UPDATED_OWTQ_WAREHOUSE_TRANSFER_SESSION_ID
            )
            .owtqDocEntry(UPDATED_OWTQ_DOC_ENTRY);

        restOwtqWarehouseTransferSessionMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    partialUpdatedOwtqWarehouseTransferSession.getId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(
                            partialUpdatedOwtqWarehouseTransferSession
                        )
                    )
            )
            .andExpect(status().isOk());

        // Validate the OwtqWarehouseTransferSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOwtqWarehouseTransferSessionUpdatableFieldsEquals(
            partialUpdatedOwtqWarehouseTransferSession,
            getPersistedOwtqWarehouseTransferSession(
                partialUpdatedOwtqWarehouseTransferSession
            )
        );
    }

    @Test
    @Transactional
    void patchNonExistingOwtqWarehouseTransferSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        owtqWarehouseTransferSession.setId(longCount.incrementAndGet());

        // Create the OwtqWarehouseTransferSession
        OwtqWarehouseTransferSessionDTO owtqWarehouseTransferSessionDTO =
            owtqWarehouseTransferSessionMapper.toDto(
                owtqWarehouseTransferSession
            );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOwtqWarehouseTransferSessionMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    owtqWarehouseTransferSessionDTO.getId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(owtqWarehouseTransferSessionDTO)
                    )
            )
            .andExpect(status().isBadRequest());

        // Validate the OwtqWarehouseTransferSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOwtqWarehouseTransferSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        owtqWarehouseTransferSession.setId(longCount.incrementAndGet());

        // Create the OwtqWarehouseTransferSession
        OwtqWarehouseTransferSessionDTO owtqWarehouseTransferSessionDTO =
            owtqWarehouseTransferSessionMapper.toDto(
                owtqWarehouseTransferSession
            );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOwtqWarehouseTransferSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(owtqWarehouseTransferSessionDTO)
                    )
            )
            .andExpect(status().isBadRequest());

        // Validate the OwtqWarehouseTransferSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOwtqWarehouseTransferSession()
        throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        owtqWarehouseTransferSession.setId(longCount.incrementAndGet());

        // Create the OwtqWarehouseTransferSession
        OwtqWarehouseTransferSessionDTO owtqWarehouseTransferSessionDTO =
            owtqWarehouseTransferSessionMapper.toDto(
                owtqWarehouseTransferSession
            );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOwtqWarehouseTransferSessionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(owtqWarehouseTransferSessionDTO)
                    )
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OwtqWarehouseTransferSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOwtqWarehouseTransferSession() throws Exception {
        // Initialize the database
        owtqWarehouseTransferSessionRepository.saveAndFlush(
            owtqWarehouseTransferSession
        );

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the owtqWarehouseTransferSession
        restOwtqWarehouseTransferSessionMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, owtqWarehouseTransferSession.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return owtqWarehouseTransferSessionRepository.count();
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

    protected OwtqWarehouseTransferSession getPersistedOwtqWarehouseTransferSession(
        OwtqWarehouseTransferSession owtqWarehouseTransferSession
    ) {
        return owtqWarehouseTransferSessionRepository
            .findById(owtqWarehouseTransferSession.getId())
            .orElseThrow();
    }

    protected void assertPersistedOwtqWarehouseTransferSessionToMatchAllProperties(
        OwtqWarehouseTransferSession expectedOwtqWarehouseTransferSession
    ) {
        assertOwtqWarehouseTransferSessionAllPropertiesEquals(
            expectedOwtqWarehouseTransferSession,
            getPersistedOwtqWarehouseTransferSession(
                expectedOwtqWarehouseTransferSession
            )
        );
    }

    protected void assertPersistedOwtqWarehouseTransferSessionToMatchUpdatableProperties(
        OwtqWarehouseTransferSession expectedOwtqWarehouseTransferSession
    ) {
        assertOwtqWarehouseTransferSessionAllUpdatablePropertiesEquals(
            expectedOwtqWarehouseTransferSession,
            getPersistedOwtqWarehouseTransferSession(
                expectedOwtqWarehouseTransferSession
            )
        );
    }
}
