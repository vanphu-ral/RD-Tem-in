package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.TemIdentificationScenarioAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.TemIdentificationScenario;
import com.mycompany.myapp.repository.partner5.TemIdentificationScenarioRepository;
import com.mycompany.myapp.service.dto.TemIdentificationScenarioDTO;
import com.mycompany.myapp.service.mapper.TemIdentificationScenarioMapper;
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
 * Integration tests for the {@link TemIdentificationScenarioResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TemIdentificationScenarioResourceIT {

    private static final String DEFAULT_VENDOR_CODE = "AAAAAAAAAA";
    private static final String UPDATED_VENDOR_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_VENDOR_NAME = "AAAAAAAAAA";
    private static final String UPDATED_VENDOR_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_MAPPING_CONFIG = "AAAAAAAAAA";
    private static final String UPDATED_MAPPING_CONFIG = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(
        ZoneId.systemDefault()
    ).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_AT =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String DEFAULT_UPDATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_UPDATED_BY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_UPDATED_AT =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_AT = ZonedDateTime.now(
        ZoneId.systemDefault()
    ).withNano(0);
    private static final ZonedDateTime SMALLER_UPDATED_AT =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL =
        "/api/tem-identification-scenarios";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TemIdentificationScenarioRepository temIdentificationScenarioRepository;

    @Autowired
    private TemIdentificationScenarioMapper temIdentificationScenarioMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTemIdentificationScenarioMockMvc;

    private TemIdentificationScenario temIdentificationScenario;

    private TemIdentificationScenario insertedTemIdentificationScenario;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TemIdentificationScenario createEntity() {
        return new TemIdentificationScenario()
            .vendorCode(DEFAULT_VENDOR_CODE)
            .vendorName(DEFAULT_VENDOR_NAME)
            .mappingConfig(DEFAULT_MAPPING_CONFIG)
            .createdBy(DEFAULT_CREATED_BY)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedBy(DEFAULT_UPDATED_BY)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TemIdentificationScenario createUpdatedEntity() {
        return new TemIdentificationScenario()
            .vendorCode(UPDATED_VENDOR_CODE)
            .vendorName(UPDATED_VENDOR_NAME)
            .mappingConfig(UPDATED_MAPPING_CONFIG)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        temIdentificationScenario = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTemIdentificationScenario != null) {
            temIdentificationScenarioRepository.delete(
                insertedTemIdentificationScenario
            );
            insertedTemIdentificationScenario = null;
        }
    }

    @Test
    @Transactional
    void createTemIdentificationScenario() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TemIdentificationScenario
        TemIdentificationScenarioDTO temIdentificationScenarioDTO =
            temIdentificationScenarioMapper.toDto(temIdentificationScenario);
        var returnedTemIdentificationScenarioDTO = om.readValue(
            restTemIdentificationScenarioMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            om.writeValueAsBytes(temIdentificationScenarioDTO)
                        )
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TemIdentificationScenarioDTO.class
        );

        // Validate the TemIdentificationScenario in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTemIdentificationScenario =
            temIdentificationScenarioMapper.toEntity(
                returnedTemIdentificationScenarioDTO
            );
        assertTemIdentificationScenarioUpdatableFieldsEquals(
            returnedTemIdentificationScenario,
            getPersistedTemIdentificationScenario(
                returnedTemIdentificationScenario
            )
        );

        insertedTemIdentificationScenario = returnedTemIdentificationScenario;
    }

    @Test
    @Transactional
    void createTemIdentificationScenarioWithExistingId() throws Exception {
        // Create the TemIdentificationScenario with an existing ID
        temIdentificationScenario.setId(1L);
        TemIdentificationScenarioDTO temIdentificationScenarioDTO =
            temIdentificationScenarioMapper.toDto(temIdentificationScenario);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTemIdentificationScenarioMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(temIdentificationScenarioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TemIdentificationScenario in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenarios() throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList
        restTemIdentificationScenarioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(temIdentificationScenario.getId().intValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].vendorCode").value(hasItem(DEFAULT_VENDOR_CODE))
            )
            .andExpect(
                jsonPath("$.[*].vendorName").value(hasItem(DEFAULT_VENDOR_NAME))
            )
            .andExpect(
                jsonPath("$.[*].mappingConfig").value(
                    hasItem(DEFAULT_MAPPING_CONFIG)
                )
            )
            .andExpect(
                jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY))
            )
            .andExpect(
                jsonPath("$.[*].createdAt").value(
                    hasItem(sameInstant(DEFAULT_CREATED_AT))
                )
            )
            .andExpect(
                jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY))
            )
            .andExpect(
                jsonPath("$.[*].updatedAt").value(
                    hasItem(sameInstant(DEFAULT_UPDATED_AT))
                )
            );
    }

    @Test
    @Transactional
    void getTemIdentificationScenario() throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get the temIdentificationScenario
        restTemIdentificationScenarioMockMvc
            .perform(get(ENTITY_API_URL_ID, temIdentificationScenario.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.id").value(
                    temIdentificationScenario.getId().intValue()
                )
            )
            .andExpect(jsonPath("$.vendorCode").value(DEFAULT_VENDOR_CODE))
            .andExpect(jsonPath("$.vendorName").value(DEFAULT_VENDOR_NAME))
            .andExpect(
                jsonPath("$.mappingConfig").value(DEFAULT_MAPPING_CONFIG)
            )
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(
                jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT))
            )
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
            .andExpect(
                jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT))
            );
    }

    @Test
    @Transactional
    void getTemIdentificationScenariosByIdFiltering() throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        Long id = temIdentificationScenario.getId();

        defaultTemIdentificationScenarioFiltering(
            "id.equals=" + id,
            "id.notEquals=" + id
        );

        defaultTemIdentificationScenarioFiltering(
            "id.greaterThanOrEqual=" + id,
            "id.greaterThan=" + id
        );

        defaultTemIdentificationScenarioFiltering(
            "id.lessThanOrEqual=" + id,
            "id.lessThan=" + id
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByVendorCodeIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where vendorCode equals to
        defaultTemIdentificationScenarioFiltering(
            "vendorCode.equals=" + DEFAULT_VENDOR_CODE,
            "vendorCode.equals=" + UPDATED_VENDOR_CODE
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByVendorCodeIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where vendorCode in
        defaultTemIdentificationScenarioFiltering(
            "vendorCode.in=" + DEFAULT_VENDOR_CODE + "," + UPDATED_VENDOR_CODE,
            "vendorCode.in=" + UPDATED_VENDOR_CODE
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByVendorCodeIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where vendorCode is not null
        defaultTemIdentificationScenarioFiltering(
            "vendorCode.specified=true",
            "vendorCode.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByVendorCodeContainsSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where vendorCode contains
        defaultTemIdentificationScenarioFiltering(
            "vendorCode.contains=" + DEFAULT_VENDOR_CODE,
            "vendorCode.contains=" + UPDATED_VENDOR_CODE
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByVendorCodeNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where vendorCode does not contain
        defaultTemIdentificationScenarioFiltering(
            "vendorCode.doesNotContain=" + UPDATED_VENDOR_CODE,
            "vendorCode.doesNotContain=" + DEFAULT_VENDOR_CODE
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByVendorNameIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where vendorName equals to
        defaultTemIdentificationScenarioFiltering(
            "vendorName.equals=" + DEFAULT_VENDOR_NAME,
            "vendorName.equals=" + UPDATED_VENDOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByVendorNameIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where vendorName in
        defaultTemIdentificationScenarioFiltering(
            "vendorName.in=" + DEFAULT_VENDOR_NAME + "," + UPDATED_VENDOR_NAME,
            "vendorName.in=" + UPDATED_VENDOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByVendorNameIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where vendorName is not null
        defaultTemIdentificationScenarioFiltering(
            "vendorName.specified=true",
            "vendorName.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByVendorNameContainsSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where vendorName contains
        defaultTemIdentificationScenarioFiltering(
            "vendorName.contains=" + DEFAULT_VENDOR_NAME,
            "vendorName.contains=" + UPDATED_VENDOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByVendorNameNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where vendorName does not contain
        defaultTemIdentificationScenarioFiltering(
            "vendorName.doesNotContain=" + UPDATED_VENDOR_NAME,
            "vendorName.doesNotContain=" + DEFAULT_VENDOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByMappingConfigIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where mappingConfig equals to
        defaultTemIdentificationScenarioFiltering(
            "mappingConfig.equals=" + DEFAULT_MAPPING_CONFIG,
            "mappingConfig.equals=" + UPDATED_MAPPING_CONFIG
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByMappingConfigIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where mappingConfig in
        defaultTemIdentificationScenarioFiltering(
            "mappingConfig.in=" +
            DEFAULT_MAPPING_CONFIG +
            "," +
            UPDATED_MAPPING_CONFIG,
            "mappingConfig.in=" + UPDATED_MAPPING_CONFIG
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByMappingConfigIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where mappingConfig is not null
        defaultTemIdentificationScenarioFiltering(
            "mappingConfig.specified=true",
            "mappingConfig.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByMappingConfigContainsSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where mappingConfig contains
        defaultTemIdentificationScenarioFiltering(
            "mappingConfig.contains=" + DEFAULT_MAPPING_CONFIG,
            "mappingConfig.contains=" + UPDATED_MAPPING_CONFIG
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByMappingConfigNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where mappingConfig does not contain
        defaultTemIdentificationScenarioFiltering(
            "mappingConfig.doesNotContain=" + UPDATED_MAPPING_CONFIG,
            "mappingConfig.doesNotContain=" + DEFAULT_MAPPING_CONFIG
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByCreatedByIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where createdBy equals to
        defaultTemIdentificationScenarioFiltering(
            "createdBy.equals=" + DEFAULT_CREATED_BY,
            "createdBy.equals=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByCreatedByIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where createdBy in
        defaultTemIdentificationScenarioFiltering(
            "createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY,
            "createdBy.in=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByCreatedByIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where createdBy is not null
        defaultTemIdentificationScenarioFiltering(
            "createdBy.specified=true",
            "createdBy.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByCreatedByContainsSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where createdBy contains
        defaultTemIdentificationScenarioFiltering(
            "createdBy.contains=" + DEFAULT_CREATED_BY,
            "createdBy.contains=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByCreatedByNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where createdBy does not contain
        defaultTemIdentificationScenarioFiltering(
            "createdBy.doesNotContain=" + UPDATED_CREATED_BY,
            "createdBy.doesNotContain=" + DEFAULT_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByCreatedAtIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where createdAt equals to
        defaultTemIdentificationScenarioFiltering(
            "createdAt.equals=" + DEFAULT_CREATED_AT,
            "createdAt.equals=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByCreatedAtIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where createdAt in
        defaultTemIdentificationScenarioFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByCreatedAtIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where createdAt is not null
        defaultTemIdentificationScenarioFiltering(
            "createdAt.specified=true",
            "createdAt.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByCreatedAtIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where createdAt is greater than or equal to
        defaultTemIdentificationScenarioFiltering(
            "createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT,
            "createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByCreatedAtIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where createdAt is less than or equal to
        defaultTemIdentificationScenarioFiltering(
            "createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT,
            "createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByCreatedAtIsLessThanSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where createdAt is less than
        defaultTemIdentificationScenarioFiltering(
            "createdAt.lessThan=" + UPDATED_CREATED_AT,
            "createdAt.lessThan=" + DEFAULT_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByCreatedAtIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where createdAt is greater than
        defaultTemIdentificationScenarioFiltering(
            "createdAt.greaterThan=" + SMALLER_CREATED_AT,
            "createdAt.greaterThan=" + DEFAULT_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByUpdatedByIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where updatedBy equals to
        defaultTemIdentificationScenarioFiltering(
            "updatedBy.equals=" + DEFAULT_UPDATED_BY,
            "updatedBy.equals=" + UPDATED_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByUpdatedByIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where updatedBy in
        defaultTemIdentificationScenarioFiltering(
            "updatedBy.in=" + DEFAULT_UPDATED_BY + "," + UPDATED_UPDATED_BY,
            "updatedBy.in=" + UPDATED_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByUpdatedByIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where updatedBy is not null
        defaultTemIdentificationScenarioFiltering(
            "updatedBy.specified=true",
            "updatedBy.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByUpdatedByContainsSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where updatedBy contains
        defaultTemIdentificationScenarioFiltering(
            "updatedBy.contains=" + DEFAULT_UPDATED_BY,
            "updatedBy.contains=" + UPDATED_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByUpdatedByNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where updatedBy does not contain
        defaultTemIdentificationScenarioFiltering(
            "updatedBy.doesNotContain=" + UPDATED_UPDATED_BY,
            "updatedBy.doesNotContain=" + DEFAULT_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByUpdatedAtIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where updatedAt equals to
        defaultTemIdentificationScenarioFiltering(
            "updatedAt.equals=" + DEFAULT_UPDATED_AT,
            "updatedAt.equals=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByUpdatedAtIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where updatedAt in
        defaultTemIdentificationScenarioFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByUpdatedAtIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where updatedAt is not null
        defaultTemIdentificationScenarioFiltering(
            "updatedAt.specified=true",
            "updatedAt.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByUpdatedAtIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where updatedAt is greater than or equal to
        defaultTemIdentificationScenarioFiltering(
            "updatedAt.greaterThanOrEqual=" + DEFAULT_UPDATED_AT,
            "updatedAt.greaterThanOrEqual=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByUpdatedAtIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where updatedAt is less than or equal to
        defaultTemIdentificationScenarioFiltering(
            "updatedAt.lessThanOrEqual=" + DEFAULT_UPDATED_AT,
            "updatedAt.lessThanOrEqual=" + SMALLER_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByUpdatedAtIsLessThanSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where updatedAt is less than
        defaultTemIdentificationScenarioFiltering(
            "updatedAt.lessThan=" + UPDATED_UPDATED_AT,
            "updatedAt.lessThan=" + DEFAULT_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllTemIdentificationScenariosByUpdatedAtIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        // Get all the temIdentificationScenarioList where updatedAt is greater than
        defaultTemIdentificationScenarioFiltering(
            "updatedAt.greaterThan=" + SMALLER_UPDATED_AT,
            "updatedAt.greaterThan=" + DEFAULT_UPDATED_AT
        );
    }

    private void defaultTemIdentificationScenarioFiltering(
        String shouldBeFound,
        String shouldNotBeFound
    ) throws Exception {
        defaultTemIdentificationScenarioShouldBeFound(shouldBeFound);
        defaultTemIdentificationScenarioShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTemIdentificationScenarioShouldBeFound(String filter)
        throws Exception {
        restTemIdentificationScenarioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(temIdentificationScenario.getId().intValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].vendorCode").value(hasItem(DEFAULT_VENDOR_CODE))
            )
            .andExpect(
                jsonPath("$.[*].vendorName").value(hasItem(DEFAULT_VENDOR_NAME))
            )
            .andExpect(
                jsonPath("$.[*].mappingConfig").value(
                    hasItem(DEFAULT_MAPPING_CONFIG)
                )
            )
            .andExpect(
                jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY))
            )
            .andExpect(
                jsonPath("$.[*].createdAt").value(
                    hasItem(sameInstant(DEFAULT_CREATED_AT))
                )
            )
            .andExpect(
                jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY))
            )
            .andExpect(
                jsonPath("$.[*].updatedAt").value(
                    hasItem(sameInstant(DEFAULT_UPDATED_AT))
                )
            );

        // Check, that the count call also returns 1
        restTemIdentificationScenarioMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTemIdentificationScenarioShouldNotBeFound(String filter)
        throws Exception {
        restTemIdentificationScenarioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTemIdentificationScenarioMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTemIdentificationScenario() throws Exception {
        // Get the temIdentificationScenario
        restTemIdentificationScenarioMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTemIdentificationScenario() throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the temIdentificationScenario
        TemIdentificationScenario updatedTemIdentificationScenario =
            temIdentificationScenarioRepository
                .findById(temIdentificationScenario.getId())
                .orElseThrow();
        // Disconnect from session so that the updates on updatedTemIdentificationScenario are not directly saved in db
        em.detach(updatedTemIdentificationScenario);
        updatedTemIdentificationScenario
            .vendorCode(UPDATED_VENDOR_CODE)
            .vendorName(UPDATED_VENDOR_NAME)
            .mappingConfig(UPDATED_MAPPING_CONFIG)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT);
        TemIdentificationScenarioDTO temIdentificationScenarioDTO =
            temIdentificationScenarioMapper.toDto(
                updatedTemIdentificationScenario
            );

        restTemIdentificationScenarioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, temIdentificationScenarioDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(temIdentificationScenarioDTO))
            )
            .andExpect(status().isOk());

        // Validate the TemIdentificationScenario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTemIdentificationScenarioToMatchAllProperties(
            updatedTemIdentificationScenario
        );
    }

    @Test
    @Transactional
    void putNonExistingTemIdentificationScenario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        temIdentificationScenario.setId(longCount.incrementAndGet());

        // Create the TemIdentificationScenario
        TemIdentificationScenarioDTO temIdentificationScenarioDTO =
            temIdentificationScenarioMapper.toDto(temIdentificationScenario);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTemIdentificationScenarioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, temIdentificationScenarioDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(temIdentificationScenarioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TemIdentificationScenario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTemIdentificationScenario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        temIdentificationScenario.setId(longCount.incrementAndGet());

        // Create the TemIdentificationScenario
        TemIdentificationScenarioDTO temIdentificationScenarioDTO =
            temIdentificationScenarioMapper.toDto(temIdentificationScenario);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemIdentificationScenarioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(temIdentificationScenarioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TemIdentificationScenario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTemIdentificationScenario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        temIdentificationScenario.setId(longCount.incrementAndGet());

        // Create the TemIdentificationScenario
        TemIdentificationScenarioDTO temIdentificationScenarioDTO =
            temIdentificationScenarioMapper.toDto(temIdentificationScenario);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemIdentificationScenarioMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(temIdentificationScenarioDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TemIdentificationScenario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTemIdentificationScenarioWithPatch() throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the temIdentificationScenario using partial update
        TemIdentificationScenario partialUpdatedTemIdentificationScenario =
            new TemIdentificationScenario();
        partialUpdatedTemIdentificationScenario.setId(
            temIdentificationScenario.getId()
        );

        partialUpdatedTemIdentificationScenario.createdBy(UPDATED_CREATED_BY);

        restTemIdentificationScenarioMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    partialUpdatedTemIdentificationScenario.getId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(
                            partialUpdatedTemIdentificationScenario
                        )
                    )
            )
            .andExpect(status().isOk());

        // Validate the TemIdentificationScenario in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTemIdentificationScenarioUpdatableFieldsEquals(
            createUpdateProxyForBean(
                partialUpdatedTemIdentificationScenario,
                temIdentificationScenario
            ),
            getPersistedTemIdentificationScenario(temIdentificationScenario)
        );
    }

    @Test
    @Transactional
    void fullUpdateTemIdentificationScenarioWithPatch() throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the temIdentificationScenario using partial update
        TemIdentificationScenario partialUpdatedTemIdentificationScenario =
            new TemIdentificationScenario();
        partialUpdatedTemIdentificationScenario.setId(
            temIdentificationScenario.getId()
        );

        partialUpdatedTemIdentificationScenario
            .vendorCode(UPDATED_VENDOR_CODE)
            .vendorName(UPDATED_VENDOR_NAME)
            .mappingConfig(UPDATED_MAPPING_CONFIG)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT);

        restTemIdentificationScenarioMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    partialUpdatedTemIdentificationScenario.getId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(
                            partialUpdatedTemIdentificationScenario
                        )
                    )
            )
            .andExpect(status().isOk());

        // Validate the TemIdentificationScenario in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTemIdentificationScenarioUpdatableFieldsEquals(
            partialUpdatedTemIdentificationScenario,
            getPersistedTemIdentificationScenario(
                partialUpdatedTemIdentificationScenario
            )
        );
    }

    @Test
    @Transactional
    void patchNonExistingTemIdentificationScenario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        temIdentificationScenario.setId(longCount.incrementAndGet());

        // Create the TemIdentificationScenario
        TemIdentificationScenarioDTO temIdentificationScenarioDTO =
            temIdentificationScenarioMapper.toDto(temIdentificationScenario);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTemIdentificationScenarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, temIdentificationScenarioDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(temIdentificationScenarioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TemIdentificationScenario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTemIdentificationScenario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        temIdentificationScenario.setId(longCount.incrementAndGet());

        // Create the TemIdentificationScenario
        TemIdentificationScenarioDTO temIdentificationScenarioDTO =
            temIdentificationScenarioMapper.toDto(temIdentificationScenario);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemIdentificationScenarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(temIdentificationScenarioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TemIdentificationScenario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTemIdentificationScenario()
        throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        temIdentificationScenario.setId(longCount.incrementAndGet());

        // Create the TemIdentificationScenario
        TemIdentificationScenarioDTO temIdentificationScenarioDTO =
            temIdentificationScenarioMapper.toDto(temIdentificationScenario);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemIdentificationScenarioMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(temIdentificationScenarioDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TemIdentificationScenario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTemIdentificationScenario() throws Exception {
        // Initialize the database
        insertedTemIdentificationScenario =
            temIdentificationScenarioRepository.saveAndFlush(
                temIdentificationScenario
            );

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the temIdentificationScenario
        restTemIdentificationScenarioMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, temIdentificationScenario.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return temIdentificationScenarioRepository.count();
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

    protected TemIdentificationScenario getPersistedTemIdentificationScenario(
        TemIdentificationScenario temIdentificationScenario
    ) {
        return temIdentificationScenarioRepository
            .findById(temIdentificationScenario.getId())
            .orElseThrow();
    }

    protected void assertPersistedTemIdentificationScenarioToMatchAllProperties(
        TemIdentificationScenario expectedTemIdentificationScenario
    ) {
        assertTemIdentificationScenarioAllPropertiesEquals(
            expectedTemIdentificationScenario,
            getPersistedTemIdentificationScenario(
                expectedTemIdentificationScenario
            )
        );
    }

    protected void assertPersistedTemIdentificationScenarioToMatchUpdatableProperties(
        TemIdentificationScenario expectedTemIdentificationScenario
    ) {
        assertTemIdentificationScenarioAllUpdatablePropertiesEquals(
            expectedTemIdentificationScenario,
            getPersistedTemIdentificationScenario(
                expectedTemIdentificationScenario
            )
        );
    }
}
