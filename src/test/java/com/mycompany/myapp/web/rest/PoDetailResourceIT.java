package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.PoDetailAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.ImportVendorTemTransactions;
import com.mycompany.myapp.domain.PoDetail;
import com.mycompany.myapp.repository.partner5.PoDetailRepository;
import com.mycompany.myapp.service.dto.PoDetailDTO;
import com.mycompany.myapp.service.mapper.PoDetailMapper;
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
 * Integration tests for the {@link PoDetailResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PoDetailResourceIT {

    private static final String DEFAULT_SAP_CODE = "AAAAAAAAAA";
    private static final String UPDATED_SAP_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_SAP_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SAP_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_QUANTITY_CONTAINER = 1;
    private static final Integer UPDATED_QUANTITY_CONTAINER = 2;
    private static final Integer SMALLER_QUANTITY_CONTAINER = 1 - 1;

    private static final Integer DEFAULT_TOTAL_QUANTITY = 1;
    private static final Integer UPDATED_TOTAL_QUANTITY = 2;
    private static final Integer SMALLER_TOTAL_QUANTITY = 1 - 1;

    private static final String DEFAULT_PART_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PART_NUMBER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/po-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PoDetailRepository poDetailRepository;

    @Autowired
    private PoDetailMapper poDetailMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPoDetailMockMvc;

    private PoDetail poDetail;

    private PoDetail insertedPoDetail;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PoDetail createEntity() {
        return new PoDetail()
            .sapCode(DEFAULT_SAP_CODE)
            .sapName(DEFAULT_SAP_NAME)
            .quantityContainer(DEFAULT_QUANTITY_CONTAINER)
            .totalQuantity(DEFAULT_TOTAL_QUANTITY)
            .partNumber(DEFAULT_PART_NUMBER);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PoDetail createUpdatedEntity() {
        return new PoDetail()
            .sapCode(UPDATED_SAP_CODE)
            .sapName(UPDATED_SAP_NAME)
            .quantityContainer(UPDATED_QUANTITY_CONTAINER)
            .totalQuantity(UPDATED_TOTAL_QUANTITY)
            .partNumber(UPDATED_PART_NUMBER);
    }

    @BeforeEach
    void initTest() {
        poDetail = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPoDetail != null) {
            poDetailRepository.delete(insertedPoDetail);
            insertedPoDetail = null;
        }
    }

    @Test
    @Transactional
    void createPoDetail() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PoDetail
        PoDetailDTO poDetailDTO = poDetailMapper.toDto(poDetail);
        var returnedPoDetailDTO = om.readValue(
            restPoDetailMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(poDetailDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PoDetailDTO.class
        );

        // Validate the PoDetail in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPoDetail = poDetailMapper.toEntity(returnedPoDetailDTO);
        assertPoDetailUpdatableFieldsEquals(
            returnedPoDetail,
            getPersistedPoDetail(returnedPoDetail)
        );

        insertedPoDetail = returnedPoDetail;
    }

    @Test
    @Transactional
    void createPoDetailWithExistingId() throws Exception {
        // Create the PoDetail with an existing ID
        poDetail.setId(1L);
        PoDetailDTO poDetailDTO = poDetailMapper.toDto(poDetail);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPoDetailMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(poDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PoDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPoDetails() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList
        restPoDetailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(hasItem(poDetail.getId().intValue()))
            )
            .andExpect(
                jsonPath("$.[*].sapCode").value(hasItem(DEFAULT_SAP_CODE))
            )
            .andExpect(
                jsonPath("$.[*].sapName").value(hasItem(DEFAULT_SAP_NAME))
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
            .andExpect(
                jsonPath("$.[*].partNumber").value(hasItem(DEFAULT_PART_NUMBER))
            );
    }

    @Test
    @Transactional
    void getPoDetail() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get the poDetail
        restPoDetailMockMvc
            .perform(get(ENTITY_API_URL_ID, poDetail.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(poDetail.getId().intValue()))
            .andExpect(jsonPath("$.sapCode").value(DEFAULT_SAP_CODE))
            .andExpect(jsonPath("$.sapName").value(DEFAULT_SAP_NAME))
            .andExpect(
                jsonPath("$.quantityContainer").value(
                    DEFAULT_QUANTITY_CONTAINER
                )
            )
            .andExpect(
                jsonPath("$.totalQuantity").value(DEFAULT_TOTAL_QUANTITY)
            )
            .andExpect(jsonPath("$.partNumber").value(DEFAULT_PART_NUMBER));
    }

    @Test
    @Transactional
    void getPoDetailsByIdFiltering() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        Long id = poDetail.getId();

        defaultPoDetailFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPoDetailFiltering(
            "id.greaterThanOrEqual=" + id,
            "id.greaterThan=" + id
        );

        defaultPoDetailFiltering(
            "id.lessThanOrEqual=" + id,
            "id.lessThan=" + id
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsBySapCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where sapCode equals to
        defaultPoDetailFiltering(
            "sapCode.equals=" + DEFAULT_SAP_CODE,
            "sapCode.equals=" + UPDATED_SAP_CODE
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsBySapCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where sapCode in
        defaultPoDetailFiltering(
            "sapCode.in=" + DEFAULT_SAP_CODE + "," + UPDATED_SAP_CODE,
            "sapCode.in=" + UPDATED_SAP_CODE
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsBySapCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where sapCode is not null
        defaultPoDetailFiltering(
            "sapCode.specified=true",
            "sapCode.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsBySapCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where sapCode contains
        defaultPoDetailFiltering(
            "sapCode.contains=" + DEFAULT_SAP_CODE,
            "sapCode.contains=" + UPDATED_SAP_CODE
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsBySapCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where sapCode does not contain
        defaultPoDetailFiltering(
            "sapCode.doesNotContain=" + UPDATED_SAP_CODE,
            "sapCode.doesNotContain=" + DEFAULT_SAP_CODE
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsBySapNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where sapName equals to
        defaultPoDetailFiltering(
            "sapName.equals=" + DEFAULT_SAP_NAME,
            "sapName.equals=" + UPDATED_SAP_NAME
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsBySapNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where sapName in
        defaultPoDetailFiltering(
            "sapName.in=" + DEFAULT_SAP_NAME + "," + UPDATED_SAP_NAME,
            "sapName.in=" + UPDATED_SAP_NAME
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsBySapNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where sapName is not null
        defaultPoDetailFiltering(
            "sapName.specified=true",
            "sapName.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsBySapNameContainsSomething() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where sapName contains
        defaultPoDetailFiltering(
            "sapName.contains=" + DEFAULT_SAP_NAME,
            "sapName.contains=" + UPDATED_SAP_NAME
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsBySapNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where sapName does not contain
        defaultPoDetailFiltering(
            "sapName.doesNotContain=" + UPDATED_SAP_NAME,
            "sapName.doesNotContain=" + DEFAULT_SAP_NAME
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsByQuantityContainerIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where quantityContainer equals to
        defaultPoDetailFiltering(
            "quantityContainer.equals=" + DEFAULT_QUANTITY_CONTAINER,
            "quantityContainer.equals=" + UPDATED_QUANTITY_CONTAINER
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsByQuantityContainerIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where quantityContainer in
        defaultPoDetailFiltering(
            "quantityContainer.in=" +
            DEFAULT_QUANTITY_CONTAINER +
            "," +
            UPDATED_QUANTITY_CONTAINER,
            "quantityContainer.in=" + UPDATED_QUANTITY_CONTAINER
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsByQuantityContainerIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where quantityContainer is not null
        defaultPoDetailFiltering(
            "quantityContainer.specified=true",
            "quantityContainer.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsByQuantityContainerIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where quantityContainer is greater than or equal to
        defaultPoDetailFiltering(
            "quantityContainer.greaterThanOrEqual=" +
            DEFAULT_QUANTITY_CONTAINER,
            "quantityContainer.greaterThanOrEqual=" + UPDATED_QUANTITY_CONTAINER
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsByQuantityContainerIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where quantityContainer is less than or equal to
        defaultPoDetailFiltering(
            "quantityContainer.lessThanOrEqual=" + DEFAULT_QUANTITY_CONTAINER,
            "quantityContainer.lessThanOrEqual=" + SMALLER_QUANTITY_CONTAINER
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsByQuantityContainerIsLessThanSomething()
        throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where quantityContainer is less than
        defaultPoDetailFiltering(
            "quantityContainer.lessThan=" + UPDATED_QUANTITY_CONTAINER,
            "quantityContainer.lessThan=" + DEFAULT_QUANTITY_CONTAINER
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsByQuantityContainerIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where quantityContainer is greater than
        defaultPoDetailFiltering(
            "quantityContainer.greaterThan=" + SMALLER_QUANTITY_CONTAINER,
            "quantityContainer.greaterThan=" + DEFAULT_QUANTITY_CONTAINER
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsByTotalQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where totalQuantity equals to
        defaultPoDetailFiltering(
            "totalQuantity.equals=" + DEFAULT_TOTAL_QUANTITY,
            "totalQuantity.equals=" + UPDATED_TOTAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsByTotalQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where totalQuantity in
        defaultPoDetailFiltering(
            "totalQuantity.in=" +
            DEFAULT_TOTAL_QUANTITY +
            "," +
            UPDATED_TOTAL_QUANTITY,
            "totalQuantity.in=" + UPDATED_TOTAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsByTotalQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where totalQuantity is not null
        defaultPoDetailFiltering(
            "totalQuantity.specified=true",
            "totalQuantity.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsByTotalQuantityIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where totalQuantity is greater than or equal to
        defaultPoDetailFiltering(
            "totalQuantity.greaterThanOrEqual=" + DEFAULT_TOTAL_QUANTITY,
            "totalQuantity.greaterThanOrEqual=" + UPDATED_TOTAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsByTotalQuantityIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where totalQuantity is less than or equal to
        defaultPoDetailFiltering(
            "totalQuantity.lessThanOrEqual=" + DEFAULT_TOTAL_QUANTITY,
            "totalQuantity.lessThanOrEqual=" + SMALLER_TOTAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsByTotalQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where totalQuantity is less than
        defaultPoDetailFiltering(
            "totalQuantity.lessThan=" + UPDATED_TOTAL_QUANTITY,
            "totalQuantity.lessThan=" + DEFAULT_TOTAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsByTotalQuantityIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where totalQuantity is greater than
        defaultPoDetailFiltering(
            "totalQuantity.greaterThan=" + SMALLER_TOTAL_QUANTITY,
            "totalQuantity.greaterThan=" + DEFAULT_TOTAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsByPartNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where partNumber equals to
        defaultPoDetailFiltering(
            "partNumber.equals=" + DEFAULT_PART_NUMBER,
            "partNumber.equals=" + UPDATED_PART_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsByPartNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where partNumber in
        defaultPoDetailFiltering(
            "partNumber.in=" + DEFAULT_PART_NUMBER + "," + UPDATED_PART_NUMBER,
            "partNumber.in=" + UPDATED_PART_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsByPartNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where partNumber is not null
        defaultPoDetailFiltering(
            "partNumber.specified=true",
            "partNumber.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsByPartNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where partNumber contains
        defaultPoDetailFiltering(
            "partNumber.contains=" + DEFAULT_PART_NUMBER,
            "partNumber.contains=" + UPDATED_PART_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsByPartNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        // Get all the poDetailList where partNumber does not contain
        defaultPoDetailFiltering(
            "partNumber.doesNotContain=" + UPDATED_PART_NUMBER,
            "partNumber.doesNotContain=" + DEFAULT_PART_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPoDetailsByImportVendorTemTransactionsIsEqualToSomething()
        throws Exception {
        ImportVendorTemTransactions importVendorTemTransactions;
        if (TestUtil.findAll(em, ImportVendorTemTransactions.class).isEmpty()) {
            poDetailRepository.saveAndFlush(poDetail);
            importVendorTemTransactions =
                ImportVendorTemTransactionsResourceIT.createEntity();
        } else {
            importVendorTemTransactions = TestUtil.findAll(
                em,
                ImportVendorTemTransactions.class
            ).get(0);
        }
        em.persist(importVendorTemTransactions);
        em.flush();
        poDetail.setImportVendorTemTransactions(importVendorTemTransactions);
        poDetailRepository.saveAndFlush(poDetail);
        Long importVendorTemTransactionsId =
            importVendorTemTransactions.getId();
        // Get all the poDetailList where importVendorTemTransactions equals to importVendorTemTransactionsId
        defaultPoDetailShouldBeFound(
            "importVendorTemTransactionsId.equals=" +
            importVendorTemTransactionsId
        );

        // Get all the poDetailList where importVendorTemTransactions equals to (importVendorTemTransactionsId + 1)
        defaultPoDetailShouldNotBeFound(
            "importVendorTemTransactionsId.equals=" +
            (importVendorTemTransactionsId + 1)
        );
    }

    private void defaultPoDetailFiltering(
        String shouldBeFound,
        String shouldNotBeFound
    ) throws Exception {
        defaultPoDetailShouldBeFound(shouldBeFound);
        defaultPoDetailShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPoDetailShouldBeFound(String filter) throws Exception {
        restPoDetailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(hasItem(poDetail.getId().intValue()))
            )
            .andExpect(
                jsonPath("$.[*].sapCode").value(hasItem(DEFAULT_SAP_CODE))
            )
            .andExpect(
                jsonPath("$.[*].sapName").value(hasItem(DEFAULT_SAP_NAME))
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
            .andExpect(
                jsonPath("$.[*].partNumber").value(hasItem(DEFAULT_PART_NUMBER))
            );

        // Check, that the count call also returns 1
        restPoDetailMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPoDetailShouldNotBeFound(String filter)
        throws Exception {
        restPoDetailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPoDetailMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPoDetail() throws Exception {
        // Get the poDetail
        restPoDetailMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPoDetail() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the poDetail
        PoDetail updatedPoDetail = poDetailRepository
            .findById(poDetail.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedPoDetail are not directly saved in db
        em.detach(updatedPoDetail);
        updatedPoDetail
            .sapCode(UPDATED_SAP_CODE)
            .sapName(UPDATED_SAP_NAME)
            .quantityContainer(UPDATED_QUANTITY_CONTAINER)
            .totalQuantity(UPDATED_TOTAL_QUANTITY)
            .partNumber(UPDATED_PART_NUMBER);
        PoDetailDTO poDetailDTO = poDetailMapper.toDto(updatedPoDetail);

        restPoDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, poDetailDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(poDetailDTO))
            )
            .andExpect(status().isOk());

        // Validate the PoDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPoDetailToMatchAllProperties(updatedPoDetail);
    }

    @Test
    @Transactional
    void putNonExistingPoDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poDetail.setId(longCount.incrementAndGet());

        // Create the PoDetail
        PoDetailDTO poDetailDTO = poDetailMapper.toDto(poDetail);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPoDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, poDetailDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(poDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PoDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPoDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poDetail.setId(longCount.incrementAndGet());

        // Create the PoDetail
        PoDetailDTO poDetailDTO = poDetailMapper.toDto(poDetail);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPoDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(poDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PoDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPoDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poDetail.setId(longCount.incrementAndGet());

        // Create the PoDetail
        PoDetailDTO poDetailDTO = poDetailMapper.toDto(poDetail);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPoDetailMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(poDetailDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PoDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePoDetailWithPatch() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the poDetail using partial update
        PoDetail partialUpdatedPoDetail = new PoDetail();
        partialUpdatedPoDetail.setId(poDetail.getId());

        partialUpdatedPoDetail.sapCode(UPDATED_SAP_CODE);

        restPoDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPoDetail.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPoDetail))
            )
            .andExpect(status().isOk());

        // Validate the PoDetail in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPoDetailUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPoDetail, poDetail),
            getPersistedPoDetail(poDetail)
        );
    }

    @Test
    @Transactional
    void fullUpdatePoDetailWithPatch() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the poDetail using partial update
        PoDetail partialUpdatedPoDetail = new PoDetail();
        partialUpdatedPoDetail.setId(poDetail.getId());

        partialUpdatedPoDetail
            .sapCode(UPDATED_SAP_CODE)
            .sapName(UPDATED_SAP_NAME)
            .quantityContainer(UPDATED_QUANTITY_CONTAINER)
            .totalQuantity(UPDATED_TOTAL_QUANTITY)
            .partNumber(UPDATED_PART_NUMBER);

        restPoDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPoDetail.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPoDetail))
            )
            .andExpect(status().isOk());

        // Validate the PoDetail in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPoDetailUpdatableFieldsEquals(
            partialUpdatedPoDetail,
            getPersistedPoDetail(partialUpdatedPoDetail)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPoDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poDetail.setId(longCount.incrementAndGet());

        // Create the PoDetail
        PoDetailDTO poDetailDTO = poDetailMapper.toDto(poDetail);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPoDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, poDetailDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(poDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PoDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPoDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poDetail.setId(longCount.incrementAndGet());

        // Create the PoDetail
        PoDetailDTO poDetailDTO = poDetailMapper.toDto(poDetail);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPoDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(poDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PoDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPoDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poDetail.setId(longCount.incrementAndGet());

        // Create the PoDetail
        PoDetailDTO poDetailDTO = poDetailMapper.toDto(poDetail);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPoDetailMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(poDetailDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PoDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePoDetail() throws Exception {
        // Initialize the database
        insertedPoDetail = poDetailRepository.saveAndFlush(poDetail);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the poDetail
        restPoDetailMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, poDetail.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return poDetailRepository.count();
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

    protected PoDetail getPersistedPoDetail(PoDetail poDetail) {
        return poDetailRepository.findById(poDetail.getId()).orElseThrow();
    }

    protected void assertPersistedPoDetailToMatchAllProperties(
        PoDetail expectedPoDetail
    ) {
        assertPoDetailAllPropertiesEquals(
            expectedPoDetail,
            getPersistedPoDetail(expectedPoDetail)
        );
    }

    protected void assertPersistedPoDetailToMatchUpdatableProperties(
        PoDetail expectedPoDetail
    ) {
        assertPoDetailAllUpdatablePropertiesEquals(
            expectedPoDetail,
            getPersistedPoDetail(expectedPoDetail)
        );
    }
}
