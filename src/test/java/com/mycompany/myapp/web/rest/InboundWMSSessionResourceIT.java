package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.InboundWMSSessionAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.InboundWMSSession;
import com.mycompany.myapp.repository.partner3.InboundWMSSessionRepository;
import com.mycompany.myapp.service.dto.InboundWMSSessionDTO;
import com.mycompany.myapp.service.mapper.InboundWMSSessionMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
 * Integration tests for the {@link InboundWMSSessionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InboundWMSSessionResourceIT {

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(
        ZoneId.systemDefault()
    ).withNano(0);

    private static final String ENTITY_API_URL = "/api/inbound-wms-sessions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InboundWMSSessionRepository inboundWMSSessionRepository;

    @Autowired
    private InboundWMSSessionMapper inboundWMSSessionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInboundWMSSessionMockMvc;

    private InboundWMSSession inboundWMSSession;

    private InboundWMSSession insertedInboundWMSSession;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InboundWMSSession createEntity() {
        return new InboundWMSSession()
            .status(DEFAULT_STATUS)
            .createdBy(DEFAULT_CREATED_BY)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InboundWMSSession createUpdatedEntity() {
        return new InboundWMSSession()
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        inboundWMSSession = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedInboundWMSSession != null) {
            inboundWMSSessionRepository.delete(insertedInboundWMSSession);
            insertedInboundWMSSession = null;
        }
    }

    @Test
    @Transactional
    void createInboundWMSSession() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the InboundWMSSession
        InboundWMSSessionDTO inboundWMSSessionDTO =
            inboundWMSSessionMapper.toDto(inboundWMSSession);
        var returnedInboundWMSSessionDTO = om.readValue(
            restInboundWMSSessionMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(inboundWMSSessionDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InboundWMSSessionDTO.class
        );

        // Validate the InboundWMSSession in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInboundWMSSession = inboundWMSSessionMapper.toEntity(
            returnedInboundWMSSessionDTO
        );
        assertInboundWMSSessionUpdatableFieldsEquals(
            returnedInboundWMSSession,
            getPersistedInboundWMSSession(returnedInboundWMSSession)
        );

        insertedInboundWMSSession = returnedInboundWMSSession;
    }

    @Test
    @Transactional
    void createInboundWMSSessionWithExistingId() throws Exception {
        // Create the InboundWMSSession with an existing ID
        inboundWMSSession.setId(1L);
        InboundWMSSessionDTO inboundWMSSessionDTO =
            inboundWMSSessionMapper.toDto(inboundWMSSession);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInboundWMSSessionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inboundWMSSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InboundWMSSession in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllInboundWMSSessions() throws Exception {
        // Initialize the database
        insertedInboundWMSSession = inboundWMSSessionRepository.saveAndFlush(
            inboundWMSSession
        );

        // Get all the inboundWMSSessionList
        restInboundWMSSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(inboundWMSSession.getId().intValue())
                )
            )
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(
                jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY))
            )
            .andExpect(
                jsonPath("$.[*].createdAt").value(
                    hasItem(sameInstant(DEFAULT_CREATED_AT))
                )
            );
    }

    @Test
    @Transactional
    void getInboundWMSSession() throws Exception {
        // Initialize the database
        insertedInboundWMSSession = inboundWMSSessionRepository.saveAndFlush(
            inboundWMSSession
        );

        // Get the inboundWMSSession
        restInboundWMSSessionMockMvc
            .perform(get(ENTITY_API_URL_ID, inboundWMSSession.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.id").value(inboundWMSSession.getId().intValue())
            )
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(
                jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT))
            );
    }

    @Test
    @Transactional
    void getNonExistingInboundWMSSession() throws Exception {
        // Get the inboundWMSSession
        restInboundWMSSessionMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInboundWMSSession() throws Exception {
        // Initialize the database
        insertedInboundWMSSession = inboundWMSSessionRepository.saveAndFlush(
            inboundWMSSession
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inboundWMSSession
        InboundWMSSession updatedInboundWMSSession = inboundWMSSessionRepository
            .findById(inboundWMSSession.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedInboundWMSSession are not directly saved in db
        em.detach(updatedInboundWMSSession);
        updatedInboundWMSSession
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT);
        InboundWMSSessionDTO inboundWMSSessionDTO =
            inboundWMSSessionMapper.toDto(updatedInboundWMSSession);

        restInboundWMSSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inboundWMSSessionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inboundWMSSessionDTO))
            )
            .andExpect(status().isOk());

        // Validate the InboundWMSSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInboundWMSSessionToMatchAllProperties(
            updatedInboundWMSSession
        );
    }

    @Test
    @Transactional
    void putNonExistingInboundWMSSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inboundWMSSession.setId(longCount.incrementAndGet());

        // Create the InboundWMSSession
        InboundWMSSessionDTO inboundWMSSessionDTO =
            inboundWMSSessionMapper.toDto(inboundWMSSession);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInboundWMSSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inboundWMSSessionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inboundWMSSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InboundWMSSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInboundWMSSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inboundWMSSession.setId(longCount.incrementAndGet());

        // Create the InboundWMSSession
        InboundWMSSessionDTO inboundWMSSessionDTO =
            inboundWMSSessionMapper.toDto(inboundWMSSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInboundWMSSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inboundWMSSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InboundWMSSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInboundWMSSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inboundWMSSession.setId(longCount.incrementAndGet());

        // Create the InboundWMSSession
        InboundWMSSessionDTO inboundWMSSessionDTO =
            inboundWMSSessionMapper.toDto(inboundWMSSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInboundWMSSessionMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inboundWMSSessionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InboundWMSSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInboundWMSSessionWithPatch() throws Exception {
        // Initialize the database
        insertedInboundWMSSession = inboundWMSSessionRepository.saveAndFlush(
            inboundWMSSession
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inboundWMSSession using partial update
        InboundWMSSession partialUpdatedInboundWMSSession =
            new InboundWMSSession();
        partialUpdatedInboundWMSSession.setId(inboundWMSSession.getId());

        partialUpdatedInboundWMSSession
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY);

        restInboundWMSSessionMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    partialUpdatedInboundWMSSession.getId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(partialUpdatedInboundWMSSession)
                    )
            )
            .andExpect(status().isOk());

        // Validate the InboundWMSSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInboundWMSSessionUpdatableFieldsEquals(
            createUpdateProxyForBean(
                partialUpdatedInboundWMSSession,
                inboundWMSSession
            ),
            getPersistedInboundWMSSession(inboundWMSSession)
        );
    }

    @Test
    @Transactional
    void fullUpdateInboundWMSSessionWithPatch() throws Exception {
        // Initialize the database
        insertedInboundWMSSession = inboundWMSSessionRepository.saveAndFlush(
            inboundWMSSession
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inboundWMSSession using partial update
        InboundWMSSession partialUpdatedInboundWMSSession =
            new InboundWMSSession();
        partialUpdatedInboundWMSSession.setId(inboundWMSSession.getId());

        partialUpdatedInboundWMSSession
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT);

        restInboundWMSSessionMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    partialUpdatedInboundWMSSession.getId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(partialUpdatedInboundWMSSession)
                    )
            )
            .andExpect(status().isOk());

        // Validate the InboundWMSSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInboundWMSSessionUpdatableFieldsEquals(
            partialUpdatedInboundWMSSession,
            getPersistedInboundWMSSession(partialUpdatedInboundWMSSession)
        );
    }

    @Test
    @Transactional
    void patchNonExistingInboundWMSSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inboundWMSSession.setId(longCount.incrementAndGet());

        // Create the InboundWMSSession
        InboundWMSSessionDTO inboundWMSSessionDTO =
            inboundWMSSessionMapper.toDto(inboundWMSSession);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInboundWMSSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, inboundWMSSessionDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inboundWMSSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InboundWMSSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInboundWMSSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inboundWMSSession.setId(longCount.incrementAndGet());

        // Create the InboundWMSSession
        InboundWMSSessionDTO inboundWMSSessionDTO =
            inboundWMSSessionMapper.toDto(inboundWMSSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInboundWMSSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inboundWMSSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InboundWMSSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInboundWMSSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inboundWMSSession.setId(longCount.incrementAndGet());

        // Create the InboundWMSSession
        InboundWMSSessionDTO inboundWMSSessionDTO =
            inboundWMSSessionMapper.toDto(inboundWMSSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInboundWMSSessionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inboundWMSSessionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InboundWMSSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInboundWMSSession() throws Exception {
        // Initialize the database
        insertedInboundWMSSession = inboundWMSSessionRepository.saveAndFlush(
            inboundWMSSession
        );

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the inboundWMSSession
        restInboundWMSSessionMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, inboundWMSSession.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return inboundWMSSessionRepository.count();
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

    protected InboundWMSSession getPersistedInboundWMSSession(
        InboundWMSSession inboundWMSSession
    ) {
        return inboundWMSSessionRepository
            .findById(inboundWMSSession.getId())
            .orElseThrow();
    }

    protected void assertPersistedInboundWMSSessionToMatchAllProperties(
        InboundWMSSession expectedInboundWMSSession
    ) {
        assertInboundWMSSessionAllPropertiesEquals(
            expectedInboundWMSSession,
            getPersistedInboundWMSSession(expectedInboundWMSSession)
        );
    }

    protected void assertPersistedInboundWMSSessionToMatchUpdatableProperties(
        InboundWMSSession expectedInboundWMSSession
    ) {
        assertInboundWMSSessionAllUpdatablePropertiesEquals(
            expectedInboundWMSSession,
            getPersistedInboundWMSSession(expectedInboundWMSSession)
        );
    }
}
