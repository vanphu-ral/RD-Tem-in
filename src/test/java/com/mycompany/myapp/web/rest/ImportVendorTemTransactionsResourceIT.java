package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.ImportVendorTemTransactionsAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.ImportVendorTemTransactions;
import com.mycompany.myapp.repository.partner5.ImportVendorTemTransactionsRepository;
import com.mycompany.myapp.service.dto.ImportVendorTemTransactionsDTO;
import com.mycompany.myapp.service.mapper.ImportVendorTemTransactionsMapper;
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
 * Integration tests for the {@link ImportVendorTemTransactionsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ImportVendorTemTransactionsResourceIT {

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

    private static final String DEFAULT_IMPORT_TEM_PROFILE = "AAAAAAAAAA";
    private static final String UPDATED_IMPORT_TEM_PROFILE = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL =
        "/api/import-vendor-tem-transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ImportVendorTemTransactionsRepository importVendorTemTransactionsRepository;

    @Autowired
    private ImportVendorTemTransactionsMapper importVendorTemTransactionsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restImportVendorTemTransactionsMockMvc;

    private ImportVendorTemTransactions importVendorTemTransactions;

    private ImportVendorTemTransactions insertedImportVendorTemTransactions;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ImportVendorTemTransactions createEntity() {
        return new ImportVendorTemTransactions()
            .poNumber(DEFAULT_PO_NUMBER)
            .vendorCode(DEFAULT_VENDOR_CODE)
            .vendorName(DEFAULT_VENDOR_NAME)
            .entryDate(DEFAULT_ENTRY_DATE)
            .storageUnit(DEFAULT_STORAGE_UNIT)
            .importTemProfile(DEFAULT_IMPORT_TEM_PROFILE)
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
    public static ImportVendorTemTransactions createUpdatedEntity() {
        return new ImportVendorTemTransactions()
            .poNumber(UPDATED_PO_NUMBER)
            .vendorCode(UPDATED_VENDOR_CODE)
            .vendorName(UPDATED_VENDOR_NAME)
            .entryDate(UPDATED_ENTRY_DATE)
            .storageUnit(UPDATED_STORAGE_UNIT)
            .importTemProfile(UPDATED_IMPORT_TEM_PROFILE)
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
        importVendorTemTransactions = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedImportVendorTemTransactions != null) {
            importVendorTemTransactionsRepository.delete(
                insertedImportVendorTemTransactions
            );
            insertedImportVendorTemTransactions = null;
        }
    }

    @Test
    @Transactional
    void createImportVendorTemTransactions() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ImportVendorTemTransactions
        ImportVendorTemTransactionsDTO importVendorTemTransactionsDTO =
            importVendorTemTransactionsMapper.toDto(
                importVendorTemTransactions
            );
        var returnedImportVendorTemTransactionsDTO = om.readValue(
            restImportVendorTemTransactionsMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            om.writeValueAsBytes(importVendorTemTransactionsDTO)
                        )
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ImportVendorTemTransactionsDTO.class
        );

        // Validate the ImportVendorTemTransactions in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedImportVendorTemTransactions =
            importVendorTemTransactionsMapper.toEntity(
                returnedImportVendorTemTransactionsDTO
            );
        assertImportVendorTemTransactionsUpdatableFieldsEquals(
            returnedImportVendorTemTransactions,
            getPersistedImportVendorTemTransactions(
                returnedImportVendorTemTransactions
            )
        );

        insertedImportVendorTemTransactions =
            returnedImportVendorTemTransactions;
    }

    @Test
    @Transactional
    void createImportVendorTemTransactionsWithExistingId() throws Exception {
        // Create the ImportVendorTemTransactions with an existing ID
        importVendorTemTransactions.setId(1L);
        ImportVendorTemTransactionsDTO importVendorTemTransactionsDTO =
            importVendorTemTransactionsMapper.toDto(
                importVendorTemTransactions
            );

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restImportVendorTemTransactionsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        om.writeValueAsBytes(importVendorTemTransactionsDTO)
                    )
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportVendorTemTransactions in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactions() throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList
        restImportVendorTemTransactionsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(importVendorTemTransactions.getId().intValue())
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
                jsonPath("$.[*].importTemProfile").value(
                    hasItem(DEFAULT_IMPORT_TEM_PROFILE)
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
    void getImportVendorTemTransactions() throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get the importVendorTemTransactions
        restImportVendorTemTransactionsMockMvc
            .perform(
                get(ENTITY_API_URL_ID, importVendorTemTransactions.getId())
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.id").value(
                    importVendorTemTransactions.getId().intValue()
                )
            )
            .andExpect(jsonPath("$.poNumber").value(DEFAULT_PO_NUMBER))
            .andExpect(jsonPath("$.vendorCode").value(DEFAULT_VENDOR_CODE))
            .andExpect(jsonPath("$.vendorName").value(DEFAULT_VENDOR_NAME))
            .andExpect(
                jsonPath("$.entryDate").value(DEFAULT_ENTRY_DATE.toString())
            )
            .andExpect(jsonPath("$.storageUnit").value(DEFAULT_STORAGE_UNIT))
            .andExpect(
                jsonPath("$.importTemProfile").value(DEFAULT_IMPORT_TEM_PROFILE)
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
    void getImportVendorTemTransactionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        Long id = importVendorTemTransactions.getId();

        defaultImportVendorTemTransactionsFiltering(
            "id.equals=" + id,
            "id.notEquals=" + id
        );

        defaultImportVendorTemTransactionsFiltering(
            "id.greaterThanOrEqual=" + id,
            "id.greaterThan=" + id
        );

        defaultImportVendorTemTransactionsFiltering(
            "id.lessThanOrEqual=" + id,
            "id.lessThan=" + id
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByPoNumberIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where poNumber equals to
        defaultImportVendorTemTransactionsFiltering(
            "poNumber.equals=" + DEFAULT_PO_NUMBER,
            "poNumber.equals=" + UPDATED_PO_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByPoNumberIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where poNumber in
        defaultImportVendorTemTransactionsFiltering(
            "poNumber.in=" + DEFAULT_PO_NUMBER + "," + UPDATED_PO_NUMBER,
            "poNumber.in=" + UPDATED_PO_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByPoNumberIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where poNumber is not null
        defaultImportVendorTemTransactionsFiltering(
            "poNumber.specified=true",
            "poNumber.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByPoNumberContainsSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where poNumber contains
        defaultImportVendorTemTransactionsFiltering(
            "poNumber.contains=" + DEFAULT_PO_NUMBER,
            "poNumber.contains=" + UPDATED_PO_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByPoNumberNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where poNumber does not contain
        defaultImportVendorTemTransactionsFiltering(
            "poNumber.doesNotContain=" + UPDATED_PO_NUMBER,
            "poNumber.doesNotContain=" + DEFAULT_PO_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByVendorCodeIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where vendorCode equals to
        defaultImportVendorTemTransactionsFiltering(
            "vendorCode.equals=" + DEFAULT_VENDOR_CODE,
            "vendorCode.equals=" + UPDATED_VENDOR_CODE
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByVendorCodeIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where vendorCode in
        defaultImportVendorTemTransactionsFiltering(
            "vendorCode.in=" + DEFAULT_VENDOR_CODE + "," + UPDATED_VENDOR_CODE,
            "vendorCode.in=" + UPDATED_VENDOR_CODE
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByVendorCodeIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where vendorCode is not null
        defaultImportVendorTemTransactionsFiltering(
            "vendorCode.specified=true",
            "vendorCode.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByVendorCodeContainsSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where vendorCode contains
        defaultImportVendorTemTransactionsFiltering(
            "vendorCode.contains=" + DEFAULT_VENDOR_CODE,
            "vendorCode.contains=" + UPDATED_VENDOR_CODE
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByVendorCodeNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where vendorCode does not contain
        defaultImportVendorTemTransactionsFiltering(
            "vendorCode.doesNotContain=" + UPDATED_VENDOR_CODE,
            "vendorCode.doesNotContain=" + DEFAULT_VENDOR_CODE
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByVendorNameIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where vendorName equals to
        defaultImportVendorTemTransactionsFiltering(
            "vendorName.equals=" + DEFAULT_VENDOR_NAME,
            "vendorName.equals=" + UPDATED_VENDOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByVendorNameIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where vendorName in
        defaultImportVendorTemTransactionsFiltering(
            "vendorName.in=" + DEFAULT_VENDOR_NAME + "," + UPDATED_VENDOR_NAME,
            "vendorName.in=" + UPDATED_VENDOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByVendorNameIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where vendorName is not null
        defaultImportVendorTemTransactionsFiltering(
            "vendorName.specified=true",
            "vendorName.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByVendorNameContainsSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where vendorName contains
        defaultImportVendorTemTransactionsFiltering(
            "vendorName.contains=" + DEFAULT_VENDOR_NAME,
            "vendorName.contains=" + UPDATED_VENDOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByVendorNameNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where vendorName does not contain
        defaultImportVendorTemTransactionsFiltering(
            "vendorName.doesNotContain=" + UPDATED_VENDOR_NAME,
            "vendorName.doesNotContain=" + DEFAULT_VENDOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByEntryDateIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where entryDate equals to
        defaultImportVendorTemTransactionsFiltering(
            "entryDate.equals=" + DEFAULT_ENTRY_DATE,
            "entryDate.equals=" + UPDATED_ENTRY_DATE
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByEntryDateIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where entryDate in
        defaultImportVendorTemTransactionsFiltering(
            "entryDate.in=" + DEFAULT_ENTRY_DATE + "," + UPDATED_ENTRY_DATE,
            "entryDate.in=" + UPDATED_ENTRY_DATE
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByEntryDateIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where entryDate is not null
        defaultImportVendorTemTransactionsFiltering(
            "entryDate.specified=true",
            "entryDate.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByEntryDateIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where entryDate is greater than or equal to
        defaultImportVendorTemTransactionsFiltering(
            "entryDate.greaterThanOrEqual=" + DEFAULT_ENTRY_DATE,
            "entryDate.greaterThanOrEqual=" + UPDATED_ENTRY_DATE
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByEntryDateIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where entryDate is less than or equal to
        defaultImportVendorTemTransactionsFiltering(
            "entryDate.lessThanOrEqual=" + DEFAULT_ENTRY_DATE,
            "entryDate.lessThanOrEqual=" + SMALLER_ENTRY_DATE
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByEntryDateIsLessThanSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where entryDate is less than
        defaultImportVendorTemTransactionsFiltering(
            "entryDate.lessThan=" + UPDATED_ENTRY_DATE,
            "entryDate.lessThan=" + DEFAULT_ENTRY_DATE
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByEntryDateIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where entryDate is greater than
        defaultImportVendorTemTransactionsFiltering(
            "entryDate.greaterThan=" + SMALLER_ENTRY_DATE,
            "entryDate.greaterThan=" + DEFAULT_ENTRY_DATE
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByStorageUnitIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where storageUnit equals to
        defaultImportVendorTemTransactionsFiltering(
            "storageUnit.equals=" + DEFAULT_STORAGE_UNIT,
            "storageUnit.equals=" + UPDATED_STORAGE_UNIT
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByStorageUnitIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where storageUnit in
        defaultImportVendorTemTransactionsFiltering(
            "storageUnit.in=" +
            DEFAULT_STORAGE_UNIT +
            "," +
            UPDATED_STORAGE_UNIT,
            "storageUnit.in=" + UPDATED_STORAGE_UNIT
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByStorageUnitIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where storageUnit is not null
        defaultImportVendorTemTransactionsFiltering(
            "storageUnit.specified=true",
            "storageUnit.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByStorageUnitContainsSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where storageUnit contains
        defaultImportVendorTemTransactionsFiltering(
            "storageUnit.contains=" + DEFAULT_STORAGE_UNIT,
            "storageUnit.contains=" + UPDATED_STORAGE_UNIT
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByStorageUnitNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where storageUnit does not contain
        defaultImportVendorTemTransactionsFiltering(
            "storageUnit.doesNotContain=" + UPDATED_STORAGE_UNIT,
            "storageUnit.doesNotContain=" + DEFAULT_STORAGE_UNIT
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByImportTemProfileIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where importTemProfile equals to
        defaultImportVendorTemTransactionsFiltering(
            "importTemProfile.equals=" + DEFAULT_IMPORT_TEM_PROFILE,
            "importTemProfile.equals=" + UPDATED_IMPORT_TEM_PROFILE
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByImportTemProfileIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where importTemProfile in
        defaultImportVendorTemTransactionsFiltering(
            "importTemProfile.in=" +
            DEFAULT_IMPORT_TEM_PROFILE +
            "," +
            UPDATED_IMPORT_TEM_PROFILE,
            "importTemProfile.in=" + UPDATED_IMPORT_TEM_PROFILE
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByImportTemProfileIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where importTemProfile is not null
        defaultImportVendorTemTransactionsFiltering(
            "importTemProfile.specified=true",
            "importTemProfile.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByImportTemProfileContainsSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where importTemProfile contains
        defaultImportVendorTemTransactionsFiltering(
            "importTemProfile.contains=" + DEFAULT_IMPORT_TEM_PROFILE,
            "importTemProfile.contains=" + UPDATED_IMPORT_TEM_PROFILE
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByImportTemProfileNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where importTemProfile does not contain
        defaultImportVendorTemTransactionsFiltering(
            "importTemProfile.doesNotContain=" + UPDATED_IMPORT_TEM_PROFILE,
            "importTemProfile.doesNotContain=" + DEFAULT_IMPORT_TEM_PROFILE
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByStatusIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where status equals to
        defaultImportVendorTemTransactionsFiltering(
            "status.equals=" + DEFAULT_STATUS,
            "status.equals=" + UPDATED_STATUS
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByStatusIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where status in
        defaultImportVendorTemTransactionsFiltering(
            "status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS,
            "status.in=" + UPDATED_STATUS
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByStatusIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where status is not null
        defaultImportVendorTemTransactionsFiltering(
            "status.specified=true",
            "status.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByStatusContainsSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where status contains
        defaultImportVendorTemTransactionsFiltering(
            "status.contains=" + DEFAULT_STATUS,
            "status.contains=" + UPDATED_STATUS
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByStatusNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where status does not contain
        defaultImportVendorTemTransactionsFiltering(
            "status.doesNotContain=" + UPDATED_STATUS,
            "status.doesNotContain=" + DEFAULT_STATUS
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByCreatedByIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where createdBy equals to
        defaultImportVendorTemTransactionsFiltering(
            "createdBy.equals=" + DEFAULT_CREATED_BY,
            "createdBy.equals=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByCreatedByIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where createdBy in
        defaultImportVendorTemTransactionsFiltering(
            "createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY,
            "createdBy.in=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByCreatedByIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where createdBy is not null
        defaultImportVendorTemTransactionsFiltering(
            "createdBy.specified=true",
            "createdBy.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByCreatedByContainsSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where createdBy contains
        defaultImportVendorTemTransactionsFiltering(
            "createdBy.contains=" + DEFAULT_CREATED_BY,
            "createdBy.contains=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByCreatedByNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where createdBy does not contain
        defaultImportVendorTemTransactionsFiltering(
            "createdBy.doesNotContain=" + UPDATED_CREATED_BY,
            "createdBy.doesNotContain=" + DEFAULT_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByCreatedAtIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where createdAt equals to
        defaultImportVendorTemTransactionsFiltering(
            "createdAt.equals=" + DEFAULT_CREATED_AT,
            "createdAt.equals=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByCreatedAtIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where createdAt in
        defaultImportVendorTemTransactionsFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByCreatedAtIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where createdAt is not null
        defaultImportVendorTemTransactionsFiltering(
            "createdAt.specified=true",
            "createdAt.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByCreatedAtIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where createdAt is greater than or equal to
        defaultImportVendorTemTransactionsFiltering(
            "createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT,
            "createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByCreatedAtIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where createdAt is less than or equal to
        defaultImportVendorTemTransactionsFiltering(
            "createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT,
            "createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByCreatedAtIsLessThanSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where createdAt is less than
        defaultImportVendorTemTransactionsFiltering(
            "createdAt.lessThan=" + UPDATED_CREATED_AT,
            "createdAt.lessThan=" + DEFAULT_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByCreatedAtIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where createdAt is greater than
        defaultImportVendorTemTransactionsFiltering(
            "createdAt.greaterThan=" + SMALLER_CREATED_AT,
            "createdAt.greaterThan=" + DEFAULT_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByUpdatedByIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where updatedBy equals to
        defaultImportVendorTemTransactionsFiltering(
            "updatedBy.equals=" + DEFAULT_UPDATED_BY,
            "updatedBy.equals=" + UPDATED_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByUpdatedByIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where updatedBy in
        defaultImportVendorTemTransactionsFiltering(
            "updatedBy.in=" + DEFAULT_UPDATED_BY + "," + UPDATED_UPDATED_BY,
            "updatedBy.in=" + UPDATED_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByUpdatedByIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where updatedBy is not null
        defaultImportVendorTemTransactionsFiltering(
            "updatedBy.specified=true",
            "updatedBy.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByUpdatedByContainsSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where updatedBy contains
        defaultImportVendorTemTransactionsFiltering(
            "updatedBy.contains=" + DEFAULT_UPDATED_BY,
            "updatedBy.contains=" + UPDATED_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByUpdatedByNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where updatedBy does not contain
        defaultImportVendorTemTransactionsFiltering(
            "updatedBy.doesNotContain=" + UPDATED_UPDATED_BY,
            "updatedBy.doesNotContain=" + DEFAULT_UPDATED_BY
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByUpdatedAtIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where updatedAt equals to
        defaultImportVendorTemTransactionsFiltering(
            "updatedAt.equals=" + DEFAULT_UPDATED_AT,
            "updatedAt.equals=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByUpdatedAtIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where updatedAt in
        defaultImportVendorTemTransactionsFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByUpdatedAtIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where updatedAt is not null
        defaultImportVendorTemTransactionsFiltering(
            "updatedAt.specified=true",
            "updatedAt.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByUpdatedAtIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where updatedAt is greater than or equal to
        defaultImportVendorTemTransactionsFiltering(
            "updatedAt.greaterThanOrEqual=" + DEFAULT_UPDATED_AT,
            "updatedAt.greaterThanOrEqual=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByUpdatedAtIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where updatedAt is less than or equal to
        defaultImportVendorTemTransactionsFiltering(
            "updatedAt.lessThanOrEqual=" + DEFAULT_UPDATED_AT,
            "updatedAt.lessThanOrEqual=" + SMALLER_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByUpdatedAtIsLessThanSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where updatedAt is less than
        defaultImportVendorTemTransactionsFiltering(
            "updatedAt.lessThan=" + UPDATED_UPDATED_AT,
            "updatedAt.lessThan=" + DEFAULT_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByUpdatedAtIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where updatedAt is greater than
        defaultImportVendorTemTransactionsFiltering(
            "updatedAt.greaterThan=" + SMALLER_UPDATED_AT,
            "updatedAt.greaterThan=" + DEFAULT_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByDeletedByIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where deletedBy equals to
        defaultImportVendorTemTransactionsFiltering(
            "deletedBy.equals=" + DEFAULT_DELETED_BY,
            "deletedBy.equals=" + UPDATED_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByDeletedByIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where deletedBy in
        defaultImportVendorTemTransactionsFiltering(
            "deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY,
            "deletedBy.in=" + UPDATED_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByDeletedByIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where deletedBy is not null
        defaultImportVendorTemTransactionsFiltering(
            "deletedBy.specified=true",
            "deletedBy.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByDeletedByContainsSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where deletedBy contains
        defaultImportVendorTemTransactionsFiltering(
            "deletedBy.contains=" + DEFAULT_DELETED_BY,
            "deletedBy.contains=" + UPDATED_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByDeletedByNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where deletedBy does not contain
        defaultImportVendorTemTransactionsFiltering(
            "deletedBy.doesNotContain=" + UPDATED_DELETED_BY,
            "deletedBy.doesNotContain=" + DEFAULT_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByDeletedAtIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where deletedAt equals to
        defaultImportVendorTemTransactionsFiltering(
            "deletedAt.equals=" + DEFAULT_DELETED_AT,
            "deletedAt.equals=" + UPDATED_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByDeletedAtIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where deletedAt in
        defaultImportVendorTemTransactionsFiltering(
            "deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT,
            "deletedAt.in=" + UPDATED_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByDeletedAtIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where deletedAt is not null
        defaultImportVendorTemTransactionsFiltering(
            "deletedAt.specified=true",
            "deletedAt.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByDeletedAtIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where deletedAt is greater than or equal to
        defaultImportVendorTemTransactionsFiltering(
            "deletedAt.greaterThanOrEqual=" + DEFAULT_DELETED_AT,
            "deletedAt.greaterThanOrEqual=" + UPDATED_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByDeletedAtIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where deletedAt is less than or equal to
        defaultImportVendorTemTransactionsFiltering(
            "deletedAt.lessThanOrEqual=" + DEFAULT_DELETED_AT,
            "deletedAt.lessThanOrEqual=" + SMALLER_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByDeletedAtIsLessThanSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where deletedAt is less than
        defaultImportVendorTemTransactionsFiltering(
            "deletedAt.lessThan=" + UPDATED_DELETED_AT,
            "deletedAt.lessThan=" + DEFAULT_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllImportVendorTemTransactionsByDeletedAtIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        // Get all the importVendorTemTransactionsList where deletedAt is greater than
        defaultImportVendorTemTransactionsFiltering(
            "deletedAt.greaterThan=" + SMALLER_DELETED_AT,
            "deletedAt.greaterThan=" + DEFAULT_DELETED_AT
        );
    }

    private void defaultImportVendorTemTransactionsFiltering(
        String shouldBeFound,
        String shouldNotBeFound
    ) throws Exception {
        defaultImportVendorTemTransactionsShouldBeFound(shouldBeFound);
        defaultImportVendorTemTransactionsShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultImportVendorTemTransactionsShouldBeFound(String filter)
        throws Exception {
        restImportVendorTemTransactionsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(importVendorTemTransactions.getId().intValue())
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
                jsonPath("$.[*].importTemProfile").value(
                    hasItem(DEFAULT_IMPORT_TEM_PROFILE)
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
        restImportVendorTemTransactionsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultImportVendorTemTransactionsShouldNotBeFound(
        String filter
    ) throws Exception {
        restImportVendorTemTransactionsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restImportVendorTemTransactionsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingImportVendorTemTransactions() throws Exception {
        // Get the importVendorTemTransactions
        restImportVendorTemTransactionsMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingImportVendorTemTransactions() throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the importVendorTemTransactions
        ImportVendorTemTransactions updatedImportVendorTemTransactions =
            importVendorTemTransactionsRepository
                .findById(importVendorTemTransactions.getId())
                .orElseThrow();
        // Disconnect from session so that the updates on updatedImportVendorTemTransactions are not directly saved in db
        em.detach(updatedImportVendorTemTransactions);
        updatedImportVendorTemTransactions
            .poNumber(UPDATED_PO_NUMBER)
            .vendorCode(UPDATED_VENDOR_CODE)
            .vendorName(UPDATED_VENDOR_NAME)
            .entryDate(UPDATED_ENTRY_DATE)
            .storageUnit(UPDATED_STORAGE_UNIT)
            .importTemProfile(UPDATED_IMPORT_TEM_PROFILE)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .deletedBy(UPDATED_DELETED_BY)
            .deletedAt(UPDATED_DELETED_AT);
        ImportVendorTemTransactionsDTO importVendorTemTransactionsDTO =
            importVendorTemTransactionsMapper.toDto(
                updatedImportVendorTemTransactions
            );

        restImportVendorTemTransactionsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, importVendorTemTransactionsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        om.writeValueAsBytes(importVendorTemTransactionsDTO)
                    )
            )
            .andExpect(status().isOk());

        // Validate the ImportVendorTemTransactions in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedImportVendorTemTransactionsToMatchAllProperties(
            updatedImportVendorTemTransactions
        );
    }

    @Test
    @Transactional
    void putNonExistingImportVendorTemTransactions() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importVendorTemTransactions.setId(longCount.incrementAndGet());

        // Create the ImportVendorTemTransactions
        ImportVendorTemTransactionsDTO importVendorTemTransactionsDTO =
            importVendorTemTransactionsMapper.toDto(
                importVendorTemTransactions
            );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImportVendorTemTransactionsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, importVendorTemTransactionsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        om.writeValueAsBytes(importVendorTemTransactionsDTO)
                    )
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportVendorTemTransactions in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchImportVendorTemTransactions() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importVendorTemTransactions.setId(longCount.incrementAndGet());

        // Create the ImportVendorTemTransactions
        ImportVendorTemTransactionsDTO importVendorTemTransactionsDTO =
            importVendorTemTransactionsMapper.toDto(
                importVendorTemTransactions
            );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportVendorTemTransactionsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        om.writeValueAsBytes(importVendorTemTransactionsDTO)
                    )
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportVendorTemTransactions in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamImportVendorTemTransactions()
        throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importVendorTemTransactions.setId(longCount.incrementAndGet());

        // Create the ImportVendorTemTransactions
        ImportVendorTemTransactionsDTO importVendorTemTransactionsDTO =
            importVendorTemTransactionsMapper.toDto(
                importVendorTemTransactions
            );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportVendorTemTransactionsMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        om.writeValueAsBytes(importVendorTemTransactionsDTO)
                    )
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ImportVendorTemTransactions in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateImportVendorTemTransactionsWithPatch() throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the importVendorTemTransactions using partial update
        ImportVendorTemTransactions partialUpdatedImportVendorTemTransactions =
            new ImportVendorTemTransactions();
        partialUpdatedImportVendorTemTransactions.setId(
            importVendorTemTransactions.getId()
        );

        partialUpdatedImportVendorTemTransactions
            .poNumber(UPDATED_PO_NUMBER)
            .vendorCode(UPDATED_VENDOR_CODE)
            .storageUnit(UPDATED_STORAGE_UNIT)
            .importTemProfile(UPDATED_IMPORT_TEM_PROFILE)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restImportVendorTemTransactionsMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    partialUpdatedImportVendorTemTransactions.getId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(
                            partialUpdatedImportVendorTemTransactions
                        )
                    )
            )
            .andExpect(status().isOk());

        // Validate the ImportVendorTemTransactions in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertImportVendorTemTransactionsUpdatableFieldsEquals(
            createUpdateProxyForBean(
                partialUpdatedImportVendorTemTransactions,
                importVendorTemTransactions
            ),
            getPersistedImportVendorTemTransactions(importVendorTemTransactions)
        );
    }

    @Test
    @Transactional
    void fullUpdateImportVendorTemTransactionsWithPatch() throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the importVendorTemTransactions using partial update
        ImportVendorTemTransactions partialUpdatedImportVendorTemTransactions =
            new ImportVendorTemTransactions();
        partialUpdatedImportVendorTemTransactions.setId(
            importVendorTemTransactions.getId()
        );

        partialUpdatedImportVendorTemTransactions
            .poNumber(UPDATED_PO_NUMBER)
            .vendorCode(UPDATED_VENDOR_CODE)
            .vendorName(UPDATED_VENDOR_NAME)
            .entryDate(UPDATED_ENTRY_DATE)
            .storageUnit(UPDATED_STORAGE_UNIT)
            .importTemProfile(UPDATED_IMPORT_TEM_PROFILE)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .deletedBy(UPDATED_DELETED_BY)
            .deletedAt(UPDATED_DELETED_AT);

        restImportVendorTemTransactionsMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    partialUpdatedImportVendorTemTransactions.getId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(
                            partialUpdatedImportVendorTemTransactions
                        )
                    )
            )
            .andExpect(status().isOk());

        // Validate the ImportVendorTemTransactions in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertImportVendorTemTransactionsUpdatableFieldsEquals(
            partialUpdatedImportVendorTemTransactions,
            getPersistedImportVendorTemTransactions(
                partialUpdatedImportVendorTemTransactions
            )
        );
    }

    @Test
    @Transactional
    void patchNonExistingImportVendorTemTransactions() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importVendorTemTransactions.setId(longCount.incrementAndGet());

        // Create the ImportVendorTemTransactions
        ImportVendorTemTransactionsDTO importVendorTemTransactionsDTO =
            importVendorTemTransactionsMapper.toDto(
                importVendorTemTransactions
            );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImportVendorTemTransactionsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, importVendorTemTransactionsDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(importVendorTemTransactionsDTO)
                    )
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportVendorTemTransactions in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchImportVendorTemTransactions() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importVendorTemTransactions.setId(longCount.incrementAndGet());

        // Create the ImportVendorTemTransactions
        ImportVendorTemTransactionsDTO importVendorTemTransactionsDTO =
            importVendorTemTransactionsMapper.toDto(
                importVendorTemTransactions
            );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportVendorTemTransactionsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(importVendorTemTransactionsDTO)
                    )
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportVendorTemTransactions in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamImportVendorTemTransactions()
        throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importVendorTemTransactions.setId(longCount.incrementAndGet());

        // Create the ImportVendorTemTransactions
        ImportVendorTemTransactionsDTO importVendorTemTransactionsDTO =
            importVendorTemTransactionsMapper.toDto(
                importVendorTemTransactions
            );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportVendorTemTransactionsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(importVendorTemTransactionsDTO)
                    )
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ImportVendorTemTransactions in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteImportVendorTemTransactions() throws Exception {
        // Initialize the database
        insertedImportVendorTemTransactions =
            importVendorTemTransactionsRepository.saveAndFlush(
                importVendorTemTransactions
            );

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the importVendorTemTransactions
        restImportVendorTemTransactionsMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, importVendorTemTransactions.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return importVendorTemTransactionsRepository.count();
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

    protected ImportVendorTemTransactions getPersistedImportVendorTemTransactions(
        ImportVendorTemTransactions importVendorTemTransactions
    ) {
        return importVendorTemTransactionsRepository
            .findById(importVendorTemTransactions.getId())
            .orElseThrow();
    }

    protected void assertPersistedImportVendorTemTransactionsToMatchAllProperties(
        ImportVendorTemTransactions expectedImportVendorTemTransactions
    ) {
        assertImportVendorTemTransactionsAllPropertiesEquals(
            expectedImportVendorTemTransactions,
            getPersistedImportVendorTemTransactions(
                expectedImportVendorTemTransactions
            )
        );
    }

    protected void assertPersistedImportVendorTemTransactionsToMatchUpdatableProperties(
        ImportVendorTemTransactions expectedImportVendorTemTransactions
    ) {
        assertImportVendorTemTransactionsAllUpdatablePropertiesEquals(
            expectedImportVendorTemTransactions,
            getPersistedImportVendorTemTransactions(
                expectedImportVendorTemTransactions
            )
        );
    }
}
