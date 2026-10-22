package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.WarehouseTransferSessionAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.WarehouseTransferSession;
import com.mycompany.myapp.repository.WarehouseTransferSessionRepository;
import com.mycompany.myapp.service.dto.WarehouseTransferSessionDTO;
import com.mycompany.myapp.service.mapper.WarehouseTransferSessionMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link WarehouseTransferSessionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WarehouseTransferSessionResourceIT {

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(
        ChronoUnit.MILLIS
    );

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(
        ChronoUnit.MILLIS
    );

    private static final String DEFAULT_UPDATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_UPDATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_DELETED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DELETED_AT = Instant.now().truncatedTo(
        ChronoUnit.MILLIS
    );

    private static final String DEFAULT_DELETED_BY = "AAAAAAAAAA";
    private static final String UPDATED_DELETED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL =
        "/api/warehouse-transfer-sessions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WarehouseTransferSessionRepository warehouseTransferSessionRepository;

    @Autowired
    private WarehouseTransferSessionMapper warehouseTransferSessionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWarehouseTransferSessionMockMvc;

    private WarehouseTransferSession warehouseTransferSession;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WarehouseTransferSession createEntity(EntityManager em) {
        WarehouseTransferSession warehouseTransferSession =
            new WarehouseTransferSession()
                .status(DEFAULT_STATUS)
                .note(DEFAULT_NOTE)
                .createdAt(DEFAULT_CREATED_AT)
                .createdBy(DEFAULT_CREATED_BY)
                .updatedAt(DEFAULT_UPDATED_AT)
                .updatedBy(DEFAULT_UPDATED_BY)
                .deletedAt(DEFAULT_DELETED_AT)
                .deletedBy(DEFAULT_DELETED_BY);
        return warehouseTransferSession;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WarehouseTransferSession createUpdatedEntity(
        EntityManager em
    ) {
        WarehouseTransferSession warehouseTransferSession =
            new WarehouseTransferSession()
                .status(UPDATED_STATUS)
                .note(UPDATED_NOTE)
                .createdAt(UPDATED_CREATED_AT)
                .createdBy(UPDATED_CREATED_BY)
                .updatedAt(UPDATED_UPDATED_AT)
                .updatedBy(UPDATED_UPDATED_BY)
                .deletedAt(UPDATED_DELETED_AT)
                .deletedBy(UPDATED_DELETED_BY);
        return warehouseTransferSession;
    }

    @BeforeEach
    public void initTest() {
        warehouseTransferSession = createEntity(em);
    }

    @Test
    @Transactional
    void createWarehouseTransferSession() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the WarehouseTransferSession
        WarehouseTransferSessionDTO warehouseTransferSessionDTO =
            warehouseTransferSessionMapper.toDto(warehouseTransferSession);
        var returnedWarehouseTransferSessionDTO = om.readValue(
            restWarehouseTransferSessionMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            om.writeValueAsBytes(warehouseTransferSessionDTO)
                        )
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            WarehouseTransferSessionDTO.class
        );

        // Validate the WarehouseTransferSession in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWarehouseTransferSession =
            warehouseTransferSessionMapper.toEntity(
                returnedWarehouseTransferSessionDTO
            );
        assertWarehouseTransferSessionUpdatableFieldsEquals(
            returnedWarehouseTransferSession,
            getPersistedWarehouseTransferSession(
                returnedWarehouseTransferSession
            )
        );
    }

    @Test
    @Transactional
    void createWarehouseTransferSessionWithExistingId() throws Exception {
        // Create the WarehouseTransferSession with an existing ID
        warehouseTransferSession.setId(1L);
        WarehouseTransferSessionDTO warehouseTransferSessionDTO =
            warehouseTransferSessionMapper.toDto(warehouseTransferSession);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWarehouseTransferSessionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseTransferSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WarehouseTransferSession in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessions() throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList
        restWarehouseTransferSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(warehouseTransferSession.getId().intValue())
                )
            )
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(
                jsonPath("$.[*].createdAt").value(
                    hasItem(DEFAULT_CREATED_AT.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY))
            )
            .andExpect(
                jsonPath("$.[*].updatedAt").value(
                    hasItem(DEFAULT_UPDATED_AT.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY))
            )
            .andExpect(
                jsonPath("$.[*].deletedAt").value(
                    hasItem(DEFAULT_DELETED_AT.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY))
            );
    }

    @Test
    @Transactional
    void getWarehouseTransferSession() throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get the warehouseTransferSession
        restWarehouseTransferSessionMockMvc
            .perform(get(ENTITY_API_URL_ID, warehouseTransferSession.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.id").value(
                    warehouseTransferSession.getId().intValue()
                )
            )
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE))
            .andExpect(
                jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString())
            )
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(
                jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString())
            )
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
            .andExpect(
                jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString())
            )
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY));
    }

    @Test
    @Transactional
    void getWarehouseTransferSessionsByIdFiltering() throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        Long id = warehouseTransferSession.getId();

        defaultWarehouseTransferSessionFiltering(
            "id.equals=" + id,
            "id.notEquals=" + id
        );

        defaultWarehouseTransferSessionFiltering(
            "id.greaterThanOrEqual=" + id,
            "id.greaterThan=" + id
        );

        defaultWarehouseTransferSessionFiltering(
            "id.lessThanOrEqual=" + id,
            "id.lessThan=" + id
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByStatusIsEqualToSomething()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where status equals to
        defaultWarehouseTransferSessionFiltering(
            "status.equals=" + DEFAULT_STATUS,
            "status.equals=" + UPDATED_STATUS
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByStatusIsInShouldWork()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where status in
        defaultWarehouseTransferSessionFiltering(
            "status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS,
            "status.in=" + UPDATED_STATUS
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByStatusIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where status is not null
        defaultWarehouseTransferSessionFiltering(
            "status.specified=true",
            "status.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByStatusContainsSomething()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where status contains
        defaultWarehouseTransferSessionFiltering(
            "status.contains=" + DEFAULT_STATUS,
            "status.contains=" + UPDATED_STATUS
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByStatusNotContainsSomething()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where status does not contain
        defaultWarehouseTransferSessionFiltering(
            "status.doesNotContain=" + UPDATED_STATUS,
            "status.doesNotContain=" + DEFAULT_STATUS
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByNoteIsEqualToSomething()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where note equals to
        defaultWarehouseTransferSessionFiltering(
            "note.equals=" + DEFAULT_NOTE,
            "note.equals=" + UPDATED_NOTE
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByNoteIsInShouldWork()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where note in
        defaultWarehouseTransferSessionFiltering(
            "note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE,
            "note.in=" + UPDATED_NOTE
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByNoteIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where note is not null
        defaultWarehouseTransferSessionFiltering(
            "note.specified=true",
            "note.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByNoteContainsSomething()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where note contains
        defaultWarehouseTransferSessionFiltering(
            "note.contains=" + DEFAULT_NOTE,
            "note.contains=" + UPDATED_NOTE
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByNoteNotContainsSomething()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where note does not contain
        defaultWarehouseTransferSessionFiltering(
            "note.doesNotContain=" + UPDATED_NOTE,
            "note.doesNotContain=" + DEFAULT_NOTE
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByCreatedAtIsEqualToSomething()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where createdAt equals to
        defaultWarehouseTransferSessionFiltering(
            "createdAt.equals=" + DEFAULT_CREATED_AT,
            "createdAt.equals=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByCreatedAtIsInShouldWork()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where createdAt in
        defaultWarehouseTransferSessionFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByCreatedAtIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where createdAt is not null
        defaultWarehouseTransferSessionFiltering(
            "createdAt.specified=true",
            "createdAt.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByCreatedByIsEqualToSomething()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where createdBy equals to
        defaultWarehouseTransferSessionFiltering(
            "createdBy.equals=" + DEFAULT_CREATED_BY,
            "createdBy.equals=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByCreatedByIsInShouldWork()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where createdBy in
        defaultWarehouseTransferSessionFiltering(
            "createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY,
            "createdBy.in=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByCreatedByIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where createdBy is not null
        defaultWarehouseTransferSessionFiltering(
            "createdBy.specified=true",
            "createdBy.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByCreatedByContainsSomething()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where createdBy contains
        defaultWarehouseTransferSessionFiltering(
            "createdBy.contains=" + DEFAULT_CREATED_BY,
            "createdBy.contains=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByCreatedByNotContainsSomething()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where createdBy does not contain
        defaultWarehouseTransferSessionFiltering(
            "createdBy.doesNotContain=" + UPDATED_CREATED_BY,
            "createdBy.doesNotContain=" + DEFAULT_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByUpdatedAtIsEqualToSomething()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where updatedAt equals to
        defaultWarehouseTransferSessionFiltering(
            "updatedAt.equals=" + DEFAULT_UPDATED_AT,
            "updatedAt.equals=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByUpdatedAtIsInShouldWork()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where updatedAt in
        defaultWarehouseTransferSessionFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByUpdatedAtIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where updatedAt is not null
        defaultWarehouseTransferSessionFiltering(
            "updatedAt.specified=true",
            "updatedAt.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByUpdatedByIsEqualToSomething()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where updatedBy equals to
        defaultWarehouseTransferSessionFiltering(
            "updatedBy.equals=" + DEFAULT_UPDATED_BY,
            "updatedBy.equals=" + UPDATED_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByUpdatedByIsInShouldWork()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where updatedBy in
        defaultWarehouseTransferSessionFiltering(
            "updatedBy.in=" + DEFAULT_UPDATED_BY + "," + UPDATED_UPDATED_BY,
            "updatedBy.in=" + UPDATED_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByUpdatedByIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where updatedBy is not null
        defaultWarehouseTransferSessionFiltering(
            "updatedBy.specified=true",
            "updatedBy.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByUpdatedByContainsSomething()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where updatedBy contains
        defaultWarehouseTransferSessionFiltering(
            "updatedBy.contains=" + DEFAULT_UPDATED_BY,
            "updatedBy.contains=" + UPDATED_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByUpdatedByNotContainsSomething()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where updatedBy does not contain
        defaultWarehouseTransferSessionFiltering(
            "updatedBy.doesNotContain=" + UPDATED_UPDATED_BY,
            "updatedBy.doesNotContain=" + DEFAULT_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByDeletedAtIsEqualToSomething()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where deletedAt equals to
        defaultWarehouseTransferSessionFiltering(
            "deletedAt.equals=" + DEFAULT_DELETED_AT,
            "deletedAt.equals=" + UPDATED_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByDeletedAtIsInShouldWork()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where deletedAt in
        defaultWarehouseTransferSessionFiltering(
            "deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT,
            "deletedAt.in=" + UPDATED_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByDeletedAtIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where deletedAt is not null
        defaultWarehouseTransferSessionFiltering(
            "deletedAt.specified=true",
            "deletedAt.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByDeletedByIsEqualToSomething()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where deletedBy equals to
        defaultWarehouseTransferSessionFiltering(
            "deletedBy.equals=" + DEFAULT_DELETED_BY,
            "deletedBy.equals=" + UPDATED_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByDeletedByIsInShouldWork()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where deletedBy in
        defaultWarehouseTransferSessionFiltering(
            "deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY,
            "deletedBy.in=" + UPDATED_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByDeletedByIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where deletedBy is not null
        defaultWarehouseTransferSessionFiltering(
            "deletedBy.specified=true",
            "deletedBy.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByDeletedByContainsSomething()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where deletedBy contains
        defaultWarehouseTransferSessionFiltering(
            "deletedBy.contains=" + DEFAULT_DELETED_BY,
            "deletedBy.contains=" + UPDATED_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllWarehouseTransferSessionsByDeletedByNotContainsSomething()
        throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        // Get all the warehouseTransferSessionList where deletedBy does not contain
        defaultWarehouseTransferSessionFiltering(
            "deletedBy.doesNotContain=" + UPDATED_DELETED_BY,
            "deletedBy.doesNotContain=" + DEFAULT_DELETED_BY
        );
    }

    private void defaultWarehouseTransferSessionFiltering(
        String shouldBeFound,
        String shouldNotBeFound
    ) throws Exception {
        defaultWarehouseTransferSessionShouldBeFound(shouldBeFound);
        defaultWarehouseTransferSessionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWarehouseTransferSessionShouldBeFound(String filter)
        throws Exception {
        restWarehouseTransferSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(warehouseTransferSession.getId().intValue())
                )
            )
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(
                jsonPath("$.[*].createdAt").value(
                    hasItem(DEFAULT_CREATED_AT.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY))
            )
            .andExpect(
                jsonPath("$.[*].updatedAt").value(
                    hasItem(DEFAULT_UPDATED_AT.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY))
            )
            .andExpect(
                jsonPath("$.[*].deletedAt").value(
                    hasItem(DEFAULT_DELETED_AT.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY))
            );

        // Check, that the count call also returns 1
        restWarehouseTransferSessionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWarehouseTransferSessionShouldNotBeFound(String filter)
        throws Exception {
        restWarehouseTransferSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWarehouseTransferSessionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWarehouseTransferSession() throws Exception {
        // Get the warehouseTransferSession
        restWarehouseTransferSessionMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWarehouseTransferSession() throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the warehouseTransferSession
        WarehouseTransferSession updatedWarehouseTransferSession =
            warehouseTransferSessionRepository
                .findById(warehouseTransferSession.getId())
                .orElseThrow();
        // Disconnect from session so that the updates on updatedWarehouseTransferSession are not directly saved in db
        em.detach(updatedWarehouseTransferSession);
        updatedWarehouseTransferSession
            .status(UPDATED_STATUS)
            .note(UPDATED_NOTE)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        WarehouseTransferSessionDTO warehouseTransferSessionDTO =
            warehouseTransferSessionMapper.toDto(
                updatedWarehouseTransferSession
            );

        restWarehouseTransferSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, warehouseTransferSessionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseTransferSessionDTO))
            )
            .andExpect(status().isOk());

        // Validate the WarehouseTransferSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWarehouseTransferSessionToMatchAllProperties(
            updatedWarehouseTransferSession
        );
    }

    @Test
    @Transactional
    void putNonExistingWarehouseTransferSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseTransferSession.setId(longCount.incrementAndGet());

        // Create the WarehouseTransferSession
        WarehouseTransferSessionDTO warehouseTransferSessionDTO =
            warehouseTransferSessionMapper.toDto(warehouseTransferSession);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWarehouseTransferSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, warehouseTransferSessionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseTransferSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WarehouseTransferSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWarehouseTransferSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseTransferSession.setId(longCount.incrementAndGet());

        // Create the WarehouseTransferSession
        WarehouseTransferSessionDTO warehouseTransferSessionDTO =
            warehouseTransferSessionMapper.toDto(warehouseTransferSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseTransferSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseTransferSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WarehouseTransferSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWarehouseTransferSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseTransferSession.setId(longCount.incrementAndGet());

        // Create the WarehouseTransferSession
        WarehouseTransferSessionDTO warehouseTransferSessionDTO =
            warehouseTransferSessionMapper.toDto(warehouseTransferSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseTransferSessionMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseTransferSessionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the WarehouseTransferSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWarehouseTransferSessionWithPatch() throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the warehouseTransferSession using partial update
        WarehouseTransferSession partialUpdatedWarehouseTransferSession =
            new WarehouseTransferSession();
        partialUpdatedWarehouseTransferSession.setId(
            warehouseTransferSession.getId()
        );

        partialUpdatedWarehouseTransferSession
            .status(UPDATED_STATUS)
            .updatedAt(UPDATED_UPDATED_AT)
            .deletedAt(UPDATED_DELETED_AT);

        restWarehouseTransferSessionMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    partialUpdatedWarehouseTransferSession.getId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(
                            partialUpdatedWarehouseTransferSession
                        )
                    )
            )
            .andExpect(status().isOk());

        // Validate the WarehouseTransferSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWarehouseTransferSessionUpdatableFieldsEquals(
            createUpdateProxyForBean(
                partialUpdatedWarehouseTransferSession,
                warehouseTransferSession
            ),
            getPersistedWarehouseTransferSession(warehouseTransferSession)
        );
    }

    @Test
    @Transactional
    void fullUpdateWarehouseTransferSessionWithPatch() throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the warehouseTransferSession using partial update
        WarehouseTransferSession partialUpdatedWarehouseTransferSession =
            new WarehouseTransferSession();
        partialUpdatedWarehouseTransferSession.setId(
            warehouseTransferSession.getId()
        );

        partialUpdatedWarehouseTransferSession
            .status(UPDATED_STATUS)
            .note(UPDATED_NOTE)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restWarehouseTransferSessionMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    partialUpdatedWarehouseTransferSession.getId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(
                            partialUpdatedWarehouseTransferSession
                        )
                    )
            )
            .andExpect(status().isOk());

        // Validate the WarehouseTransferSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWarehouseTransferSessionUpdatableFieldsEquals(
            partialUpdatedWarehouseTransferSession,
            getPersistedWarehouseTransferSession(
                partialUpdatedWarehouseTransferSession
            )
        );
    }

    @Test
    @Transactional
    void patchNonExistingWarehouseTransferSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseTransferSession.setId(longCount.incrementAndGet());

        // Create the WarehouseTransferSession
        WarehouseTransferSessionDTO warehouseTransferSessionDTO =
            warehouseTransferSessionMapper.toDto(warehouseTransferSession);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWarehouseTransferSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, warehouseTransferSessionDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(warehouseTransferSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WarehouseTransferSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWarehouseTransferSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseTransferSession.setId(longCount.incrementAndGet());

        // Create the WarehouseTransferSession
        WarehouseTransferSessionDTO warehouseTransferSessionDTO =
            warehouseTransferSessionMapper.toDto(warehouseTransferSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseTransferSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(warehouseTransferSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WarehouseTransferSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWarehouseTransferSession()
        throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseTransferSession.setId(longCount.incrementAndGet());

        // Create the WarehouseTransferSession
        WarehouseTransferSessionDTO warehouseTransferSessionDTO =
            warehouseTransferSessionMapper.toDto(warehouseTransferSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseTransferSessionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(warehouseTransferSessionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the WarehouseTransferSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWarehouseTransferSession() throws Exception {
        // Initialize the database
        warehouseTransferSessionRepository.saveAndFlush(
            warehouseTransferSession
        );

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the warehouseTransferSession
        restWarehouseTransferSessionMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, warehouseTransferSession.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return warehouseTransferSessionRepository.count();
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

    protected WarehouseTransferSession getPersistedWarehouseTransferSession(
        WarehouseTransferSession warehouseTransferSession
    ) {
        return warehouseTransferSessionRepository
            .findById(warehouseTransferSession.getId())
            .orElseThrow();
    }

    protected void assertPersistedWarehouseTransferSessionToMatchAllProperties(
        WarehouseTransferSession expectedWarehouseTransferSession
    ) {
        assertWarehouseTransferSessionAllPropertiesEquals(
            expectedWarehouseTransferSession,
            getPersistedWarehouseTransferSession(
                expectedWarehouseTransferSession
            )
        );
    }

    protected void assertPersistedWarehouseTransferSessionToMatchUpdatableProperties(
        WarehouseTransferSession expectedWarehouseTransferSession
    ) {
        assertWarehouseTransferSessionAllUpdatablePropertiesEquals(
            expectedWarehouseTransferSession,
            getPersistedWarehouseTransferSession(
                expectedWarehouseTransferSession
            )
        );
    }
}
