package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.RdMaterialAttributesAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.RdMaterialAttributes;
import com.mycompany.myapp.repository.partner5.RdMaterialAttributesRepository;
import com.mycompany.myapp.service.dto.RdMaterialAttributesDTO;
import com.mycompany.myapp.service.mapper.RdMaterialAttributesMapper;
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
 * Integration tests for the {@link RdMaterialAttributesResource} REST
 * controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RdMaterialAttributesResourceIT {

    private static final String DEFAULT_ATTRIBUTES = "AAAAAAAAAA";
    private static final String UPDATED_ATTRIBUTES = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_ATTRIBUTES_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_ATTRIBUTES_TYPE = "BBBBBBBBBB";
    private static final String SMALLER_ATTRIBUTES_TYPE = "AAAAAAAAA";

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

    private static final String ENTITY_API_URL = "/api/rd-material-attributes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RdMaterialAttributesRepository rdMaterialAttributesRepository;

    @Autowired
    private RdMaterialAttributesMapper rdMaterialAttributesMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRdMaterialAttributesMockMvc;

    private RdMaterialAttributes rdMaterialAttributes;

    private RdMaterialAttributes insertedRdMaterialAttributes;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RdMaterialAttributes createEntity() {
        return new RdMaterialAttributes()
            .attributes(DEFAULT_ATTRIBUTES)
            .description(DEFAULT_DESCRIPTION)
            .attributesType(DEFAULT_ATTRIBUTES_TYPE)
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
    public static RdMaterialAttributes createUpdatedEntity() {
        return new RdMaterialAttributes()
            .attributes(UPDATED_ATTRIBUTES)
            .description(UPDATED_DESCRIPTION)
            .attributesType(UPDATED_ATTRIBUTES_TYPE)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        rdMaterialAttributes = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedRdMaterialAttributes != null) {
            rdMaterialAttributesRepository.delete(insertedRdMaterialAttributes);
            insertedRdMaterialAttributes = null;
        }
    }

    @Test
    @Transactional
    void createRdMaterialAttributes() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RdMaterialAttributes
        RdMaterialAttributesDTO rdMaterialAttributesDTO =
            rdMaterialAttributesMapper.toDto(rdMaterialAttributes);
        var returnedRdMaterialAttributesDTO = om.readValue(
            restRdMaterialAttributesMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(rdMaterialAttributesDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RdMaterialAttributesDTO.class
        );

        // Validate the RdMaterialAttributes in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRdMaterialAttributes = rdMaterialAttributesMapper.toEntity(
            returnedRdMaterialAttributesDTO
        );
        assertRdMaterialAttributesUpdatableFieldsEquals(
            returnedRdMaterialAttributes,
            getPersistedRdMaterialAttributes(returnedRdMaterialAttributes)
        );

        insertedRdMaterialAttributes = returnedRdMaterialAttributes;
    }

    @Test
    @Transactional
    void createRdMaterialAttributesWithExistingId() throws Exception {
        // Create the RdMaterialAttributes with an existing ID
        rdMaterialAttributes.setId(1L);
        RdMaterialAttributesDTO rdMaterialAttributesDTO =
            rdMaterialAttributesMapper.toDto(rdMaterialAttributes);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRdMaterialAttributesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(rdMaterialAttributesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RdMaterialAttributes in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributes() throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList
        restRdMaterialAttributesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(rdMaterialAttributes.getId().intValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].attributes").value(hasItem(DEFAULT_ATTRIBUTES))
            )
            .andExpect(
                jsonPath("$.[*].description").value(
                    hasItem(DEFAULT_DESCRIPTION)
                )
            )
            .andExpect(
                jsonPath("$.[*].attributesType").value(
                    hasItem(DEFAULT_ATTRIBUTES_TYPE)
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
    void getRdMaterialAttributes() throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get the rdMaterialAttributes
        restRdMaterialAttributesMockMvc
            .perform(get(ENTITY_API_URL_ID, rdMaterialAttributes.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.id").value(rdMaterialAttributes.getId().intValue())
            )
            .andExpect(jsonPath("$.attributes").value(DEFAULT_ATTRIBUTES))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(
                jsonPath("$.attributesType").value(DEFAULT_ATTRIBUTES_TYPE)
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
    void getRdMaterialAttributesByIdFiltering() throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        Long id = rdMaterialAttributes.getId();

        defaultRdMaterialAttributesFiltering(
            "id.equals=" + id,
            "id.notEquals=" + id
        );

        defaultRdMaterialAttributesFiltering(
            "id.greaterThanOrEqual=" + id,
            "id.greaterThan=" + id
        );

        defaultRdMaterialAttributesFiltering(
            "id.lessThanOrEqual=" + id,
            "id.lessThan=" + id
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByAttributesIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where attributes equals to
        defaultRdMaterialAttributesFiltering(
            "attributes.equals=" + DEFAULT_ATTRIBUTES,
            "attributes.equals=" + UPDATED_ATTRIBUTES
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByAttributesIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where attributes in
        defaultRdMaterialAttributesFiltering(
            "attributes.in=" + DEFAULT_ATTRIBUTES + "," + UPDATED_ATTRIBUTES,
            "attributes.in=" + UPDATED_ATTRIBUTES
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByAttributesIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where attributes is not null
        defaultRdMaterialAttributesFiltering(
            "attributes.specified=true",
            "attributes.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByAttributesContainsSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where attributes contains
        defaultRdMaterialAttributesFiltering(
            "attributes.contains=" + DEFAULT_ATTRIBUTES,
            "attributes.contains=" + UPDATED_ATTRIBUTES
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByAttributesNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where attributes does not contain
        defaultRdMaterialAttributesFiltering(
            "attributes.doesNotContain=" + UPDATED_ATTRIBUTES,
            "attributes.doesNotContain=" + DEFAULT_ATTRIBUTES
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByDescriptionIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where description equals to
        defaultRdMaterialAttributesFiltering(
            "description.equals=" + DEFAULT_DESCRIPTION,
            "description.equals=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByDescriptionIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where description in
        defaultRdMaterialAttributesFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByDescriptionIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where description is not null
        defaultRdMaterialAttributesFiltering(
            "description.specified=true",
            "description.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByDescriptionContainsSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where description contains
        defaultRdMaterialAttributesFiltering(
            "description.contains=" + DEFAULT_DESCRIPTION,
            "description.contains=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByDescriptionNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where description does not contain
        defaultRdMaterialAttributesFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByAttributesTypeIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where attributesType equals to
        defaultRdMaterialAttributesFiltering(
            "attributesType.equals=" + DEFAULT_ATTRIBUTES_TYPE,
            "attributesType.equals=" + UPDATED_ATTRIBUTES_TYPE
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByAttributesTypeIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where attributesType in
        defaultRdMaterialAttributesFiltering(
            "attributesType.in=" +
            DEFAULT_ATTRIBUTES_TYPE +
            "," +
            UPDATED_ATTRIBUTES_TYPE,
            "attributesType.in=" + UPDATED_ATTRIBUTES_TYPE
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByAttributesTypeIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where attributesType is not null
        defaultRdMaterialAttributesFiltering(
            "attributesType.specified=true",
            "attributesType.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByAttributesTypeIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where attributesType is greater than
        // or equal to
        defaultRdMaterialAttributesFiltering(
            "attributesType.greaterThanOrEqual=" + DEFAULT_ATTRIBUTES_TYPE,
            "attributesType.greaterThanOrEqual=" + UPDATED_ATTRIBUTES_TYPE
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByAttributesTypeIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where attributesType is less than or
        // equal to
        defaultRdMaterialAttributesFiltering(
            "attributesType.lessThanOrEqual=" + DEFAULT_ATTRIBUTES_TYPE,
            "attributesType.lessThanOrEqual=" + SMALLER_ATTRIBUTES_TYPE
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByAttributesTypeIsLessThanSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where attributesType is less than
        defaultRdMaterialAttributesFiltering(
            "attributesType.lessThan=" + UPDATED_ATTRIBUTES_TYPE,
            "attributesType.lessThan=" + DEFAULT_ATTRIBUTES_TYPE
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByAttributesTypeIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where attributesType is greater than
        defaultRdMaterialAttributesFiltering(
            "attributesType.greaterThan=" + SMALLER_ATTRIBUTES_TYPE,
            "attributesType.greaterThan=" + DEFAULT_ATTRIBUTES_TYPE
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByCreatedByIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where createdBy equals to
        defaultRdMaterialAttributesFiltering(
            "createdBy.equals=" + DEFAULT_CREATED_BY,
            "createdBy.equals=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByCreatedByIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where createdBy in
        defaultRdMaterialAttributesFiltering(
            "createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY,
            "createdBy.in=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByCreatedByIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where createdBy is not null
        defaultRdMaterialAttributesFiltering(
            "createdBy.specified=true",
            "createdBy.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByCreatedByContainsSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where createdBy contains
        defaultRdMaterialAttributesFiltering(
            "createdBy.contains=" + DEFAULT_CREATED_BY,
            "createdBy.contains=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByCreatedByNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where createdBy does not contain
        defaultRdMaterialAttributesFiltering(
            "createdBy.doesNotContain=" + UPDATED_CREATED_BY,
            "createdBy.doesNotContain=" + DEFAULT_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByCreatedAtIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where createdAt equals to
        defaultRdMaterialAttributesFiltering(
            "createdAt.equals=" + DEFAULT_CREATED_AT,
            "createdAt.equals=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByCreatedAtIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where createdAt in
        defaultRdMaterialAttributesFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByCreatedAtIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where createdAt is not null
        defaultRdMaterialAttributesFiltering(
            "createdAt.specified=true",
            "createdAt.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByCreatedAtIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where createdAt is greater than or equal
        // to
        defaultRdMaterialAttributesFiltering(
            "createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT,
            "createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByCreatedAtIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where createdAt is less than or equal to
        defaultRdMaterialAttributesFiltering(
            "createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT,
            "createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByCreatedAtIsLessThanSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where createdAt is less than
        defaultRdMaterialAttributesFiltering(
            "createdAt.lessThan=" + UPDATED_CREATED_AT,
            "createdAt.lessThan=" + DEFAULT_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByCreatedAtIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where createdAt is greater than
        defaultRdMaterialAttributesFiltering(
            "createdAt.greaterThan=" + SMALLER_CREATED_AT,
            "createdAt.greaterThan=" + DEFAULT_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByUpdatedByIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where updatedBy equals to
        defaultRdMaterialAttributesFiltering(
            "updatedBy.equals=" + DEFAULT_UPDATED_BY,
            "updatedBy.equals=" + UPDATED_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByUpdatedByIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where updatedBy in
        defaultRdMaterialAttributesFiltering(
            "updatedBy.in=" + DEFAULT_UPDATED_BY + "," + UPDATED_UPDATED_BY,
            "updatedBy.in=" + UPDATED_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByUpdatedByIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where updatedBy is not null
        defaultRdMaterialAttributesFiltering(
            "updatedBy.specified=true",
            "updatedBy.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByUpdatedByContainsSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where updatedBy contains
        defaultRdMaterialAttributesFiltering(
            "updatedBy.contains=" + DEFAULT_UPDATED_BY,
            "updatedBy.contains=" + UPDATED_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByUpdatedByNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where updatedBy does not contain
        defaultRdMaterialAttributesFiltering(
            "updatedBy.doesNotContain=" + UPDATED_UPDATED_BY,
            "updatedBy.doesNotContain=" + DEFAULT_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByUpdatedAtIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where updatedAt equals to
        defaultRdMaterialAttributesFiltering(
            "updatedAt.equals=" + DEFAULT_UPDATED_AT,
            "updatedAt.equals=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByUpdatedAtIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where updatedAt in
        defaultRdMaterialAttributesFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByUpdatedAtIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where updatedAt is not null
        defaultRdMaterialAttributesFiltering(
            "updatedAt.specified=true",
            "updatedAt.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByUpdatedAtIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where updatedAt is greater than or equal
        // to
        defaultRdMaterialAttributesFiltering(
            "updatedAt.greaterThanOrEqual=" + DEFAULT_UPDATED_AT,
            "updatedAt.greaterThanOrEqual=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByUpdatedAtIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where updatedAt is less than or equal to
        defaultRdMaterialAttributesFiltering(
            "updatedAt.lessThanOrEqual=" + DEFAULT_UPDATED_AT,
            "updatedAt.lessThanOrEqual=" + SMALLER_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByUpdatedAtIsLessThanSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where updatedAt is less than
        defaultRdMaterialAttributesFiltering(
            "updatedAt.lessThan=" + UPDATED_UPDATED_AT,
            "updatedAt.lessThan=" + DEFAULT_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllRdMaterialAttributesByUpdatedAtIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        // Get all the rdMaterialAttributesList where updatedAt is greater than
        defaultRdMaterialAttributesFiltering(
            "updatedAt.greaterThan=" + SMALLER_UPDATED_AT,
            "updatedAt.greaterThan=" + DEFAULT_UPDATED_AT
        );
    }

    private void defaultRdMaterialAttributesFiltering(
        String shouldBeFound,
        String shouldNotBeFound
    ) throws Exception {
        defaultRdMaterialAttributesShouldBeFound(shouldBeFound);
        defaultRdMaterialAttributesShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRdMaterialAttributesShouldBeFound(String filter)
        throws Exception {
        restRdMaterialAttributesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(rdMaterialAttributes.getId().intValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].attributes").value(hasItem(DEFAULT_ATTRIBUTES))
            )
            .andExpect(
                jsonPath("$.[*].description").value(
                    hasItem(DEFAULT_DESCRIPTION)
                )
            )
            .andExpect(
                jsonPath("$.[*].attributesType").value(
                    hasItem(DEFAULT_ATTRIBUTES_TYPE)
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
        restRdMaterialAttributesMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRdMaterialAttributesShouldNotBeFound(String filter)
        throws Exception {
        restRdMaterialAttributesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRdMaterialAttributesMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRdMaterialAttributes() throws Exception {
        // Get the rdMaterialAttributes
        restRdMaterialAttributesMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRdMaterialAttributes() throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rdMaterialAttributes
        RdMaterialAttributes updatedRdMaterialAttributes =
            rdMaterialAttributesRepository
                .findById(rdMaterialAttributes.getId())
                .orElseThrow();
        // Disconnect from session so that the updates on updatedRdMaterialAttributes
        // are not directly saved in db
        em.detach(updatedRdMaterialAttributes);
        updatedRdMaterialAttributes
            .attributes(UPDATED_ATTRIBUTES)
            .description(UPDATED_DESCRIPTION)
            .attributesType(UPDATED_ATTRIBUTES_TYPE)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT);
        RdMaterialAttributesDTO rdMaterialAttributesDTO =
            rdMaterialAttributesMapper.toDto(updatedRdMaterialAttributes);

        restRdMaterialAttributesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rdMaterialAttributesDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(rdMaterialAttributesDTO))
            )
            .andExpect(status().isOk());

        // Validate the RdMaterialAttributes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRdMaterialAttributesToMatchAllProperties(
            updatedRdMaterialAttributes
        );
    }

    @Test
    @Transactional
    void putNonExistingRdMaterialAttributes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rdMaterialAttributes.setId(longCount.incrementAndGet());

        // Create the RdMaterialAttributes
        RdMaterialAttributesDTO rdMaterialAttributesDTO =
            rdMaterialAttributesMapper.toDto(rdMaterialAttributes);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRdMaterialAttributesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rdMaterialAttributesDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(rdMaterialAttributesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RdMaterialAttributes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRdMaterialAttributes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rdMaterialAttributes.setId(longCount.incrementAndGet());

        // Create the RdMaterialAttributes
        RdMaterialAttributesDTO rdMaterialAttributesDTO =
            rdMaterialAttributesMapper.toDto(rdMaterialAttributes);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRdMaterialAttributesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(rdMaterialAttributesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RdMaterialAttributes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRdMaterialAttributes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rdMaterialAttributes.setId(longCount.incrementAndGet());

        // Create the RdMaterialAttributes
        RdMaterialAttributesDTO rdMaterialAttributesDTO =
            rdMaterialAttributesMapper.toDto(rdMaterialAttributes);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRdMaterialAttributesMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(rdMaterialAttributesDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RdMaterialAttributes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRdMaterialAttributesWithPatch() throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rdMaterialAttributes using partial update
        RdMaterialAttributes partialUpdatedRdMaterialAttributes =
            new RdMaterialAttributes();
        partialUpdatedRdMaterialAttributes.setId(rdMaterialAttributes.getId());

        partialUpdatedRdMaterialAttributes
            .attributes(UPDATED_ATTRIBUTES)
            .description(UPDATED_DESCRIPTION)
            .attributesType(UPDATED_ATTRIBUTES_TYPE)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT);

        restRdMaterialAttributesMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    partialUpdatedRdMaterialAttributes.getId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(partialUpdatedRdMaterialAttributes)
                    )
            )
            .andExpect(status().isOk());

        // Validate the RdMaterialAttributes in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRdMaterialAttributesUpdatableFieldsEquals(
            createUpdateProxyForBean(
                partialUpdatedRdMaterialAttributes,
                rdMaterialAttributes
            ),
            getPersistedRdMaterialAttributes(rdMaterialAttributes)
        );
    }

    @Test
    @Transactional
    void fullUpdateRdMaterialAttributesWithPatch() throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rdMaterialAttributes using partial update
        RdMaterialAttributes partialUpdatedRdMaterialAttributes =
            new RdMaterialAttributes();
        partialUpdatedRdMaterialAttributes.setId(rdMaterialAttributes.getId());

        partialUpdatedRdMaterialAttributes
            .attributes(UPDATED_ATTRIBUTES)
            .description(UPDATED_DESCRIPTION)
            .attributesType(UPDATED_ATTRIBUTES_TYPE)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT);

        restRdMaterialAttributesMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    partialUpdatedRdMaterialAttributes.getId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(partialUpdatedRdMaterialAttributes)
                    )
            )
            .andExpect(status().isOk());

        // Validate the RdMaterialAttributes in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRdMaterialAttributesUpdatableFieldsEquals(
            partialUpdatedRdMaterialAttributes,
            getPersistedRdMaterialAttributes(partialUpdatedRdMaterialAttributes)
        );
    }

    @Test
    @Transactional
    void patchNonExistingRdMaterialAttributes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rdMaterialAttributes.setId(longCount.incrementAndGet());

        // Create the RdMaterialAttributes
        RdMaterialAttributesDTO rdMaterialAttributesDTO =
            rdMaterialAttributesMapper.toDto(rdMaterialAttributes);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRdMaterialAttributesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, rdMaterialAttributesDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(rdMaterialAttributesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RdMaterialAttributes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRdMaterialAttributes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rdMaterialAttributes.setId(longCount.incrementAndGet());

        // Create the RdMaterialAttributes
        RdMaterialAttributesDTO rdMaterialAttributesDTO =
            rdMaterialAttributesMapper.toDto(rdMaterialAttributes);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRdMaterialAttributesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(rdMaterialAttributesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RdMaterialAttributes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRdMaterialAttributes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rdMaterialAttributes.setId(longCount.incrementAndGet());

        // Create the RdMaterialAttributes
        RdMaterialAttributesDTO rdMaterialAttributesDTO =
            rdMaterialAttributesMapper.toDto(rdMaterialAttributes);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRdMaterialAttributesMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(rdMaterialAttributesDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RdMaterialAttributes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRdMaterialAttributes() throws Exception {
        // Initialize the database
        insertedRdMaterialAttributes =
            rdMaterialAttributesRepository.saveAndFlush(rdMaterialAttributes);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the rdMaterialAttributes
        restRdMaterialAttributesMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, rdMaterialAttributes.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return rdMaterialAttributesRepository.count();
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

    protected RdMaterialAttributes getPersistedRdMaterialAttributes(
        RdMaterialAttributes rdMaterialAttributes
    ) {
        return rdMaterialAttributesRepository
            .findById(rdMaterialAttributes.getId())
            .orElseThrow();
    }

    protected void assertPersistedRdMaterialAttributesToMatchAllProperties(
        RdMaterialAttributes expectedRdMaterialAttributes
    ) {
        assertRdMaterialAttributesAllPropertiesEquals(
            expectedRdMaterialAttributes,
            getPersistedRdMaterialAttributes(expectedRdMaterialAttributes)
        );
    }

    protected void assertPersistedRdMaterialAttributesToMatchUpdatableProperties(
        RdMaterialAttributes expectedRdMaterialAttributes
    ) {
        assertRdMaterialAttributesAllUpdatablePropertiesEquals(
            expectedRdMaterialAttributes,
            getPersistedRdMaterialAttributes(expectedRdMaterialAttributes)
        );
    }
}
