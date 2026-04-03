package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.PoImportTemAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.PoImportTem;
import com.mycompany.myapp.repository.partner5.PoImportTemRepository;
import com.mycompany.myapp.service.dto.PoImportTemDTO;
import com.mycompany.myapp.service.mapper.PoImportTemMapper;
import java.time.Instant;
import java.time.LocalDate;
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
 * Integration tests for the {@link PoImportTemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PoImportTemResourceIT {

    private static final String DEFAULT_PO_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PO_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_VENDOR_CODE = "AAAAAAAAAA";
    private static final String UPDATED_VENDOR_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_VENDOR_NAME = "AAAAAAAAAA";
    private static final String UPDATED_VENDOR_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_ENTRY_DATE = LocalDate.ofEpochDay(
        0L
    );
    private static final LocalDate UPDATED_ENTRY_DATE = LocalDate.now(
        ZoneId.systemDefault()
    );
    private static final LocalDate SMALLER_ENTRY_DATE = LocalDate.ofEpochDay(
        -1L
    );

    private static final String DEFAULT_STORAGE_UNIT = "AAAAAAAAAA";
    private static final String UPDATED_STORAGE_UNIT = "BBBBBBBBBB";

    private static final Integer DEFAULT_QUANTITY_CONTAINER = 1;
    private static final Integer UPDATED_QUANTITY_CONTAINER = 2;
    private static final Integer SMALLER_QUANTITY_CONTAINER = 1 - 1;

    private static final Integer DEFAULT_TOTAL_QUANTITY = 1;
    private static final Integer UPDATED_TOTAL_QUANTITY = 2;
    private static final Integer SMALLER_TOTAL_QUANTITY = 1 - 1;

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

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

    private static final String DEFAULT_DELETED_BY = "AAAAAAAAAA";
    private static final String UPDATED_DELETED_BY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_DELETED_AT =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DELETED_AT = ZonedDateTime.now(
        ZoneId.systemDefault()
    ).withNano(0);
    private static final ZonedDateTime SMALLER_DELETED_AT =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/po-import-tems";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PoImportTemRepository poImportTemRepository;

    @Autowired
    private PoImportTemMapper poImportTemMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPoImportTemMockMvc;

    private PoImportTem poImportTem;

    private PoImportTem insertedPoImportTem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PoImportTem createEntity() {
        return new PoImportTem()
            .poNumber(DEFAULT_PO_NUMBER)
            .vendorCode(DEFAULT_VENDOR_CODE)
            .vendorName(DEFAULT_VENDOR_NAME)
            .entryDate(DEFAULT_ENTRY_DATE)
            .quantityContainer(DEFAULT_QUANTITY_CONTAINER)
            .totalQuantity(DEFAULT_TOTAL_QUANTITY)
            .status(DEFAULT_STATUS)
            .createdBy(DEFAULT_CREATED_BY)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedBy(DEFAULT_UPDATED_BY)
            .updatedAt(DEFAULT_UPDATED_AT)
            .deletedBy(DEFAULT_DELETED_BY)
            .deletedAt(DEFAULT_DELETED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PoImportTem createUpdatedEntity() {
        return new PoImportTem()
            .poNumber(UPDATED_PO_NUMBER)
            .vendorCode(UPDATED_VENDOR_CODE)
            .vendorName(UPDATED_VENDOR_NAME)
            .entryDate(UPDATED_ENTRY_DATE)
            .quantityContainer(UPDATED_QUANTITY_CONTAINER)
            .totalQuantity(UPDATED_TOTAL_QUANTITY)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .deletedBy(UPDATED_DELETED_BY)
            .deletedAt(UPDATED_DELETED_AT);
    }

    @BeforeEach
    void initTest() {
        poImportTem = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPoImportTem != null) {
            poImportTemRepository.delete(insertedPoImportTem);
            insertedPoImportTem = null;
        }
    }

    @Test
    @Transactional
    void createPoImportTem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PoImportTem
        PoImportTemDTO poImportTemDTO = poImportTemMapper.toDto(poImportTem);
        var returnedPoImportTemDTO = om.readValue(
            restPoImportTemMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(poImportTemDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PoImportTemDTO.class
        );

        // Validate the PoImportTem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPoImportTem = poImportTemMapper.toEntity(
            returnedPoImportTemDTO
        );
        assertPoImportTemUpdatableFieldsEquals(
            returnedPoImportTem,
            getPersistedPoImportTem(returnedPoImportTem)
        );

        insertedPoImportTem = returnedPoImportTem;
    }

    @Test
    @Transactional
    void createPoImportTemWithExistingId() throws Exception {
        // Create the PoImportTem with an existing ID
        poImportTem.setId(1L);
        PoImportTemDTO poImportTemDTO = poImportTemMapper.toDto(poImportTem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPoImportTemMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(poImportTemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PoImportTem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPoImportTems() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList
        restPoImportTemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(poImportTem.getId().intValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].poNumber").value(hasItem(DEFAULT_PO_NUMBER))
            )
            .andExpect(
                jsonPath("$.[*].vendorCode").value(hasItem(DEFAULT_VENDOR_CODE))
            )
            .andExpect(
                jsonPath("$.[*].vendorName").value(hasItem(DEFAULT_VENDOR_NAME))
            )
            .andExpect(
                jsonPath("$.[*].entryDate").value(
                    hasItem(DEFAULT_ENTRY_DATE.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].storageUnit").value(
                    hasItem(DEFAULT_STORAGE_UNIT)
                )
            )
            .andExpect(
                jsonPath("$.[*].quantityContainer").value(
                    hasItem(DEFAULT_QUANTITY_CONTAINER)
                )
            )
            .andExpect(
                jsonPath("$.[*].totalQuantity").value(
                    hasItem(DEFAULT_TOTAL_QUANTITY)
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
            )
            .andExpect(
                jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY))
            )
            .andExpect(
                jsonPath("$.[*].updatedAt").value(
                    hasItem(sameInstant(DEFAULT_UPDATED_AT))
                )
            )
            .andExpect(
                jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY))
            )
            .andExpect(
                jsonPath("$.[*].deletedAt").value(
                    hasItem(sameInstant(DEFAULT_DELETED_AT))
                )
            );
    }

    @Test
    @Transactional
    void getPoImportTem() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get the poImportTem
        restPoImportTemMockMvc
            .perform(get(ENTITY_API_URL_ID, poImportTem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(poImportTem.getId().intValue()))
            .andExpect(jsonPath("$.poNumber").value(DEFAULT_PO_NUMBER))
            .andExpect(jsonPath("$.vendorCode").value(DEFAULT_VENDOR_CODE))
            .andExpect(jsonPath("$.vendorName").value(DEFAULT_VENDOR_NAME))
            .andExpect(
                jsonPath("$.entryDate").value(DEFAULT_ENTRY_DATE.toString())
            )
            .andExpect(jsonPath("$.storageUnit").value(DEFAULT_STORAGE_UNIT))
            .andExpect(
                jsonPath("$.quantityContainer").value(
                    DEFAULT_QUANTITY_CONTAINER
                )
            )
            .andExpect(
                jsonPath("$.totalQuantity").value(DEFAULT_TOTAL_QUANTITY)
            )
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(
                jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT))
            )
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
            .andExpect(
                jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT))
            )
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY))
            .andExpect(
                jsonPath("$.deletedAt").value(sameInstant(DEFAULT_DELETED_AT))
            );
    }

    @Test
    @Transactional
    void getPoImportTemsByIdFiltering() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        Long id = poImportTem.getId();

        defaultPoImportTemFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPoImportTemFiltering(
            "id.greaterThanOrEqual=" + id,
            "id.greaterThan=" + id
        );

        defaultPoImportTemFiltering(
            "id.lessThanOrEqual=" + id,
            "id.lessThan=" + id
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByPoNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where poNumber equals to
        defaultPoImportTemFiltering(
            "poNumber.equals=" + DEFAULT_PO_NUMBER,
            "poNumber.equals=" + UPDATED_PO_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByPoNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where poNumber in
        defaultPoImportTemFiltering(
            "poNumber.in=" + DEFAULT_PO_NUMBER + "," + UPDATED_PO_NUMBER,
            "poNumber.in=" + UPDATED_PO_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByPoNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where poNumber is not null
        defaultPoImportTemFiltering(
            "poNumber.specified=true",
            "poNumber.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByPoNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where poNumber contains
        defaultPoImportTemFiltering(
            "poNumber.contains=" + DEFAULT_PO_NUMBER,
            "poNumber.contains=" + UPDATED_PO_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByPoNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where poNumber does not contain
        defaultPoImportTemFiltering(
            "poNumber.doesNotContain=" + UPDATED_PO_NUMBER,
            "poNumber.doesNotContain=" + DEFAULT_PO_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByVendorCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where vendorCode equals to
        defaultPoImportTemFiltering(
            "vendorCode.equals=" + DEFAULT_VENDOR_CODE,
            "vendorCode.equals=" + UPDATED_VENDOR_CODE
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByVendorCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where vendorCode in
        defaultPoImportTemFiltering(
            "vendorCode.in=" + DEFAULT_VENDOR_CODE + "," + UPDATED_VENDOR_CODE,
            "vendorCode.in=" + UPDATED_VENDOR_CODE
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByVendorCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where vendorCode is not null
        defaultPoImportTemFiltering(
            "vendorCode.specified=true",
            "vendorCode.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByVendorCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where vendorCode contains
        defaultPoImportTemFiltering(
            "vendorCode.contains=" + DEFAULT_VENDOR_CODE,
            "vendorCode.contains=" + UPDATED_VENDOR_CODE
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByVendorCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where vendorCode does not contain
        defaultPoImportTemFiltering(
            "vendorCode.doesNotContain=" + UPDATED_VENDOR_CODE,
            "vendorCode.doesNotContain=" + DEFAULT_VENDOR_CODE
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByVendorNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where vendorName equals to
        defaultPoImportTemFiltering(
            "vendorName.equals=" + DEFAULT_VENDOR_NAME,
            "vendorName.equals=" + UPDATED_VENDOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByVendorNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where vendorName in
        defaultPoImportTemFiltering(
            "vendorName.in=" + DEFAULT_VENDOR_NAME + "," + UPDATED_VENDOR_NAME,
            "vendorName.in=" + UPDATED_VENDOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByVendorNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where vendorName is not null
        defaultPoImportTemFiltering(
            "vendorName.specified=true",
            "vendorName.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByVendorNameContainsSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where vendorName contains
        defaultPoImportTemFiltering(
            "vendorName.contains=" + DEFAULT_VENDOR_NAME,
            "vendorName.contains=" + UPDATED_VENDOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByVendorNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where vendorName does not contain
        defaultPoImportTemFiltering(
            "vendorName.doesNotContain=" + UPDATED_VENDOR_NAME,
            "vendorName.doesNotContain=" + DEFAULT_VENDOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByEntryDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where entryDate equals to
        defaultPoImportTemFiltering(
            "entryDate.equals=" + DEFAULT_ENTRY_DATE,
            "entryDate.equals=" + UPDATED_ENTRY_DATE
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByEntryDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where entryDate in
        defaultPoImportTemFiltering(
            "entryDate.in=" + DEFAULT_ENTRY_DATE + "," + UPDATED_ENTRY_DATE,
            "entryDate.in=" + UPDATED_ENTRY_DATE
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByEntryDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where entryDate is not null
        defaultPoImportTemFiltering(
            "entryDate.specified=true",
            "entryDate.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByEntryDateIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where entryDate is greater than or equal to
        defaultPoImportTemFiltering(
            "entryDate.greaterThanOrEqual=" + DEFAULT_ENTRY_DATE,
            "entryDate.greaterThanOrEqual=" + UPDATED_ENTRY_DATE
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByEntryDateIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where entryDate is less than or equal to
        defaultPoImportTemFiltering(
            "entryDate.lessThanOrEqual=" + DEFAULT_ENTRY_DATE,
            "entryDate.lessThanOrEqual=" + SMALLER_ENTRY_DATE
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByEntryDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where entryDate is less than
        defaultPoImportTemFiltering(
            "entryDate.lessThan=" + UPDATED_ENTRY_DATE,
            "entryDate.lessThan=" + DEFAULT_ENTRY_DATE
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByEntryDateIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where entryDate is greater than
        defaultPoImportTemFiltering(
            "entryDate.greaterThan=" + SMALLER_ENTRY_DATE,
            "entryDate.greaterThan=" + DEFAULT_ENTRY_DATE
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByStorageUnitIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where storageUnit equals to
        defaultPoImportTemFiltering(
            "storageUnit.equals=" + DEFAULT_STORAGE_UNIT,
            "storageUnit.equals=" + UPDATED_STORAGE_UNIT
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByStorageUnitIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where storageUnit in
        defaultPoImportTemFiltering(
            "storageUnit.in=" +
            DEFAULT_STORAGE_UNIT +
            "," +
            UPDATED_STORAGE_UNIT,
            "storageUnit.in=" + UPDATED_STORAGE_UNIT
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByStorageUnitIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where storageUnit is not null
        defaultPoImportTemFiltering(
            "storageUnit.specified=true",
            "storageUnit.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByStorageUnitContainsSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where storageUnit contains
        defaultPoImportTemFiltering(
            "storageUnit.contains=" + DEFAULT_STORAGE_UNIT,
            "storageUnit.contains=" + UPDATED_STORAGE_UNIT
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByStorageUnitNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where storageUnit does not contain
        defaultPoImportTemFiltering(
            "storageUnit.doesNotContain=" + UPDATED_STORAGE_UNIT,
            "storageUnit.doesNotContain=" + DEFAULT_STORAGE_UNIT
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByQuantityContainerIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where quantityContainer equals to
        defaultPoImportTemFiltering(
            "quantityContainer.equals=" + DEFAULT_QUANTITY_CONTAINER,
            "quantityContainer.equals=" + UPDATED_QUANTITY_CONTAINER
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByQuantityContainerIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where quantityContainer in
        defaultPoImportTemFiltering(
            "quantityContainer.in=" +
            DEFAULT_QUANTITY_CONTAINER +
            "," +
            UPDATED_QUANTITY_CONTAINER,
            "quantityContainer.in=" + UPDATED_QUANTITY_CONTAINER
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByQuantityContainerIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where quantityContainer is not null
        defaultPoImportTemFiltering(
            "quantityContainer.specified=true",
            "quantityContainer.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByQuantityContainerIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where quantityContainer is greater than or equal
        // to
        defaultPoImportTemFiltering(
            "quantityContainer.greaterThanOrEqual=" +
            DEFAULT_QUANTITY_CONTAINER,
            "quantityContainer.greaterThanOrEqual=" + UPDATED_QUANTITY_CONTAINER
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByQuantityContainerIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where quantityContainer is less than or equal to
        defaultPoImportTemFiltering(
            "quantityContainer.lessThanOrEqual=" + DEFAULT_QUANTITY_CONTAINER,
            "quantityContainer.lessThanOrEqual=" + SMALLER_QUANTITY_CONTAINER
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByQuantityContainerIsLessThanSomething()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where quantityContainer is less than
        defaultPoImportTemFiltering(
            "quantityContainer.lessThan=" + UPDATED_QUANTITY_CONTAINER,
            "quantityContainer.lessThan=" + DEFAULT_QUANTITY_CONTAINER
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByQuantityContainerIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where quantityContainer is greater than
        defaultPoImportTemFiltering(
            "quantityContainer.greaterThan=" + SMALLER_QUANTITY_CONTAINER,
            "quantityContainer.greaterThan=" + DEFAULT_QUANTITY_CONTAINER
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByTotalQuantityIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where totalQuantity equals to
        defaultPoImportTemFiltering(
            "totalQuantity.equals=" + DEFAULT_TOTAL_QUANTITY,
            "totalQuantity.equals=" + UPDATED_TOTAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByTotalQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where totalQuantity in
        defaultPoImportTemFiltering(
            "totalQuantity.in=" +
            DEFAULT_TOTAL_QUANTITY +
            "," +
            UPDATED_TOTAL_QUANTITY,
            "totalQuantity.in=" + UPDATED_TOTAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByTotalQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where totalQuantity is not null
        defaultPoImportTemFiltering(
            "totalQuantity.specified=true",
            "totalQuantity.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByTotalQuantityIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where totalQuantity is greater than or equal to
        defaultPoImportTemFiltering(
            "totalQuantity.greaterThanOrEqual=" + DEFAULT_TOTAL_QUANTITY,
            "totalQuantity.greaterThanOrEqual=" + UPDATED_TOTAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByTotalQuantityIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where totalQuantity is less than or equal to
        defaultPoImportTemFiltering(
            "totalQuantity.lessThanOrEqual=" + DEFAULT_TOTAL_QUANTITY,
            "totalQuantity.lessThanOrEqual=" + SMALLER_TOTAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByTotalQuantityIsLessThanSomething()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where totalQuantity is less than
        defaultPoImportTemFiltering(
            "totalQuantity.lessThan=" + UPDATED_TOTAL_QUANTITY,
            "totalQuantity.lessThan=" + DEFAULT_TOTAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByTotalQuantityIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where totalQuantity is greater than
        defaultPoImportTemFiltering(
            "totalQuantity.greaterThan=" + SMALLER_TOTAL_QUANTITY,
            "totalQuantity.greaterThan=" + DEFAULT_TOTAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where status equals to
        defaultPoImportTemFiltering(
            "status.equals=" + DEFAULT_STATUS,
            "status.equals=" + UPDATED_STATUS
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where status in
        defaultPoImportTemFiltering(
            "status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS,
            "status.in=" + UPDATED_STATUS
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where status is not null
        defaultPoImportTemFiltering(
            "status.specified=true",
            "status.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where status contains
        defaultPoImportTemFiltering(
            "status.contains=" + DEFAULT_STATUS,
            "status.contains=" + UPDATED_STATUS
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where status does not contain
        defaultPoImportTemFiltering(
            "status.doesNotContain=" + UPDATED_STATUS,
            "status.doesNotContain=" + DEFAULT_STATUS
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where createdBy equals to
        defaultPoImportTemFiltering(
            "createdBy.equals=" + DEFAULT_CREATED_BY,
            "createdBy.equals=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where createdBy in
        defaultPoImportTemFiltering(
            "createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY,
            "createdBy.in=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where createdBy is not null
        defaultPoImportTemFiltering(
            "createdBy.specified=true",
            "createdBy.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where createdBy contains
        defaultPoImportTemFiltering(
            "createdBy.contains=" + DEFAULT_CREATED_BY,
            "createdBy.contains=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where createdBy does not contain
        defaultPoImportTemFiltering(
            "createdBy.doesNotContain=" + UPDATED_CREATED_BY,
            "createdBy.doesNotContain=" + DEFAULT_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where createdAt equals to
        defaultPoImportTemFiltering(
            "createdAt.equals=" + DEFAULT_CREATED_AT,
            "createdAt.equals=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where createdAt in
        defaultPoImportTemFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where createdAt is not null
        defaultPoImportTemFiltering(
            "createdAt.specified=true",
            "createdAt.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByCreatedAtIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where createdAt is greater than or equal to
        defaultPoImportTemFiltering(
            "createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT,
            "createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByCreatedAtIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where createdAt is less than or equal to
        defaultPoImportTemFiltering(
            "createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT,
            "createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByCreatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where createdAt is less than
        defaultPoImportTemFiltering(
            "createdAt.lessThan=" + UPDATED_CREATED_AT,
            "createdAt.lessThan=" + DEFAULT_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByCreatedAtIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where createdAt is greater than
        defaultPoImportTemFiltering(
            "createdAt.greaterThan=" + SMALLER_CREATED_AT,
            "createdAt.greaterThan=" + DEFAULT_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByUpdatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where updatedBy equals to
        defaultPoImportTemFiltering(
            "updatedBy.equals=" + DEFAULT_UPDATED_BY,
            "updatedBy.equals=" + UPDATED_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByUpdatedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where updatedBy in
        defaultPoImportTemFiltering(
            "updatedBy.in=" + DEFAULT_UPDATED_BY + "," + UPDATED_UPDATED_BY,
            "updatedBy.in=" + UPDATED_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByUpdatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where updatedBy is not null
        defaultPoImportTemFiltering(
            "updatedBy.specified=true",
            "updatedBy.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByUpdatedByContainsSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where updatedBy contains
        defaultPoImportTemFiltering(
            "updatedBy.contains=" + DEFAULT_UPDATED_BY,
            "updatedBy.contains=" + UPDATED_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByUpdatedByNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where updatedBy does not contain
        defaultPoImportTemFiltering(
            "updatedBy.doesNotContain=" + UPDATED_UPDATED_BY,
            "updatedBy.doesNotContain=" + DEFAULT_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where updatedAt equals to
        defaultPoImportTemFiltering(
            "updatedAt.equals=" + DEFAULT_UPDATED_AT,
            "updatedAt.equals=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where updatedAt in
        defaultPoImportTemFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where updatedAt is not null
        defaultPoImportTemFiltering(
            "updatedAt.specified=true",
            "updatedAt.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByUpdatedAtIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where updatedAt is greater than or equal to
        defaultPoImportTemFiltering(
            "updatedAt.greaterThanOrEqual=" + DEFAULT_UPDATED_AT,
            "updatedAt.greaterThanOrEqual=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByUpdatedAtIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where updatedAt is less than or equal to
        defaultPoImportTemFiltering(
            "updatedAt.lessThanOrEqual=" + DEFAULT_UPDATED_AT,
            "updatedAt.lessThanOrEqual=" + SMALLER_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByUpdatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where updatedAt is less than
        defaultPoImportTemFiltering(
            "updatedAt.lessThan=" + UPDATED_UPDATED_AT,
            "updatedAt.lessThan=" + DEFAULT_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByUpdatedAtIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where updatedAt is greater than
        defaultPoImportTemFiltering(
            "updatedAt.greaterThan=" + SMALLER_UPDATED_AT,
            "updatedAt.greaterThan=" + DEFAULT_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where deletedBy equals to
        defaultPoImportTemFiltering(
            "deletedBy.equals=" + DEFAULT_DELETED_BY,
            "deletedBy.equals=" + UPDATED_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where deletedBy in
        defaultPoImportTemFiltering(
            "deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY,
            "deletedBy.in=" + UPDATED_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where deletedBy is not null
        defaultPoImportTemFiltering(
            "deletedBy.specified=true",
            "deletedBy.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByDeletedByContainsSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where deletedBy contains
        defaultPoImportTemFiltering(
            "deletedBy.contains=" + DEFAULT_DELETED_BY,
            "deletedBy.contains=" + UPDATED_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByDeletedByNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where deletedBy does not contain
        defaultPoImportTemFiltering(
            "deletedBy.doesNotContain=" + UPDATED_DELETED_BY,
            "deletedBy.doesNotContain=" + DEFAULT_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where deletedAt equals to
        defaultPoImportTemFiltering(
            "deletedAt.equals=" + DEFAULT_DELETED_AT,
            "deletedAt.equals=" + UPDATED_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where deletedAt in
        defaultPoImportTemFiltering(
            "deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT,
            "deletedAt.in=" + UPDATED_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where deletedAt is not null
        defaultPoImportTemFiltering(
            "deletedAt.specified=true",
            "deletedAt.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByDeletedAtIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where deletedAt is greater than or equal to
        defaultPoImportTemFiltering(
            "deletedAt.greaterThanOrEqual=" + DEFAULT_DELETED_AT,
            "deletedAt.greaterThanOrEqual=" + UPDATED_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByDeletedAtIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where deletedAt is less than or equal to
        defaultPoImportTemFiltering(
            "deletedAt.lessThanOrEqual=" + DEFAULT_DELETED_AT,
            "deletedAt.lessThanOrEqual=" + SMALLER_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByDeletedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where deletedAt is less than
        defaultPoImportTemFiltering(
            "deletedAt.lessThan=" + UPDATED_DELETED_AT,
            "deletedAt.lessThan=" + DEFAULT_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllPoImportTemsByDeletedAtIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        // Get all the poImportTemList where deletedAt is greater than
        defaultPoImportTemFiltering(
            "deletedAt.greaterThan=" + SMALLER_DELETED_AT,
            "deletedAt.greaterThan=" + DEFAULT_DELETED_AT
        );
    }

    private void defaultPoImportTemFiltering(
        String shouldBeFound,
        String shouldNotBeFound
    ) throws Exception {
        defaultPoImportTemShouldBeFound(shouldBeFound);
        defaultPoImportTemShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPoImportTemShouldBeFound(String filter)
        throws Exception {
        restPoImportTemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(poImportTem.getId().intValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].poNumber").value(hasItem(DEFAULT_PO_NUMBER))
            )
            .andExpect(
                jsonPath("$.[*].vendorCode").value(hasItem(DEFAULT_VENDOR_CODE))
            )
            .andExpect(
                jsonPath("$.[*].vendorName").value(hasItem(DEFAULT_VENDOR_NAME))
            )
            .andExpect(
                jsonPath("$.[*].entryDate").value(
                    hasItem(DEFAULT_ENTRY_DATE.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].storageUnit").value(
                    hasItem(DEFAULT_STORAGE_UNIT)
                )
            )
            .andExpect(
                jsonPath("$.[*].quantityContainer").value(
                    hasItem(DEFAULT_QUANTITY_CONTAINER)
                )
            )
            .andExpect(
                jsonPath("$.[*].totalQuantity").value(
                    hasItem(DEFAULT_TOTAL_QUANTITY)
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
            )
            .andExpect(
                jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY))
            )
            .andExpect(
                jsonPath("$.[*].updatedAt").value(
                    hasItem(sameInstant(DEFAULT_UPDATED_AT))
                )
            )
            .andExpect(
                jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY))
            )
            .andExpect(
                jsonPath("$.[*].deletedAt").value(
                    hasItem(sameInstant(DEFAULT_DELETED_AT))
                )
            );

        // Check, that the count call also returns 1
        restPoImportTemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPoImportTemShouldNotBeFound(String filter)
        throws Exception {
        restPoImportTemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPoImportTemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPoImportTem() throws Exception {
        // Get the poImportTem
        restPoImportTemMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPoImportTem() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the poImportTem
        PoImportTem updatedPoImportTem = poImportTemRepository
            .findById(poImportTem.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedPoImportTem are not
        // directly saved in db
        em.detach(updatedPoImportTem);
        updatedPoImportTem
            .poNumber(UPDATED_PO_NUMBER)
            .vendorCode(UPDATED_VENDOR_CODE)
            .vendorName(UPDATED_VENDOR_NAME)
            .entryDate(UPDATED_ENTRY_DATE)
            .quantityContainer(UPDATED_QUANTITY_CONTAINER)
            .totalQuantity(UPDATED_TOTAL_QUANTITY)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .deletedBy(UPDATED_DELETED_BY)
            .deletedAt(UPDATED_DELETED_AT);
        PoImportTemDTO poImportTemDTO = poImportTemMapper.toDto(
            updatedPoImportTem
        );

        restPoImportTemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, poImportTemDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(poImportTemDTO))
            )
            .andExpect(status().isOk());

        // Validate the PoImportTem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPoImportTemToMatchAllProperties(updatedPoImportTem);
    }

    @Test
    @Transactional
    void putNonExistingPoImportTem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poImportTem.setId(longCount.incrementAndGet());

        // Create the PoImportTem
        PoImportTemDTO poImportTemDTO = poImportTemMapper.toDto(poImportTem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPoImportTemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, poImportTemDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(poImportTemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PoImportTem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPoImportTem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poImportTem.setId(longCount.incrementAndGet());

        // Create the PoImportTem
        PoImportTemDTO poImportTemDTO = poImportTemMapper.toDto(poImportTem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPoImportTemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(poImportTemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PoImportTem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPoImportTem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poImportTem.setId(longCount.incrementAndGet());

        // Create the PoImportTem
        PoImportTemDTO poImportTemDTO = poImportTemMapper.toDto(poImportTem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPoImportTemMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(poImportTemDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PoImportTem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePoImportTemWithPatch() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the poImportTem using partial update
        PoImportTem partialUpdatedPoImportTem = new PoImportTem();
        partialUpdatedPoImportTem.setId(poImportTem.getId());

        partialUpdatedPoImportTem
            .vendorCode(UPDATED_VENDOR_CODE)
            .entryDate(UPDATED_ENTRY_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .deletedBy(UPDATED_DELETED_BY)
            .deletedAt(UPDATED_DELETED_AT);

        restPoImportTemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPoImportTem.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPoImportTem))
            )
            .andExpect(status().isOk());

        // Validate the PoImportTem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPoImportTemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPoImportTem, poImportTem),
            getPersistedPoImportTem(poImportTem)
        );
    }

    @Test
    @Transactional
    void fullUpdatePoImportTemWithPatch() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the poImportTem using partial update
        PoImportTem partialUpdatedPoImportTem = new PoImportTem();
        partialUpdatedPoImportTem.setId(poImportTem.getId());

        partialUpdatedPoImportTem
            .poNumber(UPDATED_PO_NUMBER)
            .vendorCode(UPDATED_VENDOR_CODE)
            .vendorName(UPDATED_VENDOR_NAME)
            .entryDate(UPDATED_ENTRY_DATE)
            .quantityContainer(UPDATED_QUANTITY_CONTAINER)
            .totalQuantity(UPDATED_TOTAL_QUANTITY)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .deletedBy(UPDATED_DELETED_BY)
            .deletedAt(UPDATED_DELETED_AT);

        restPoImportTemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPoImportTem.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPoImportTem))
            )
            .andExpect(status().isOk());

        // Validate the PoImportTem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPoImportTemUpdatableFieldsEquals(
            partialUpdatedPoImportTem,
            getPersistedPoImportTem(partialUpdatedPoImportTem)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPoImportTem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poImportTem.setId(longCount.incrementAndGet());

        // Create the PoImportTem
        PoImportTemDTO poImportTemDTO = poImportTemMapper.toDto(poImportTem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPoImportTemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, poImportTemDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(poImportTemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PoImportTem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPoImportTem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poImportTem.setId(longCount.incrementAndGet());

        // Create the PoImportTem
        PoImportTemDTO poImportTemDTO = poImportTemMapper.toDto(poImportTem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPoImportTemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(poImportTemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PoImportTem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPoImportTem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poImportTem.setId(longCount.incrementAndGet());

        // Create the PoImportTem
        PoImportTemDTO poImportTemDTO = poImportTemMapper.toDto(poImportTem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPoImportTemMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(poImportTemDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PoImportTem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePoImportTem() throws Exception {
        // Initialize the database
        insertedPoImportTem = poImportTemRepository.saveAndFlush(poImportTem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the poImportTem
        restPoImportTemMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, poImportTem.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return poImportTemRepository.count();
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

    protected PoImportTem getPersistedPoImportTem(PoImportTem poImportTem) {
        return poImportTemRepository
            .findById(poImportTem.getId())
            .orElseThrow();
    }

    protected void assertPersistedPoImportTemToMatchAllProperties(
        PoImportTem expectedPoImportTem
    ) {
        assertPoImportTemAllPropertiesEquals(
            expectedPoImportTem,
            getPersistedPoImportTem(expectedPoImportTem)
        );
    }

    protected void assertPersistedPoImportTemToMatchUpdatableProperties(
        PoImportTem expectedPoImportTem
    ) {
        assertPoImportTemAllUpdatablePropertiesEquals(
            expectedPoImportTem,
            getPersistedPoImportTem(expectedPoImportTem)
        );
    }
}
