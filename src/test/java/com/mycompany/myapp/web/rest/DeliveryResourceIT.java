package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.DeliveryAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Delivery;
import com.mycompany.myapp.repository.DeliveryRepository;
import com.mycompany.myapp.service.dto.DeliveryDTO;
import com.mycompany.myapp.service.mapper.DeliveryMapper;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.UUID;
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
 * Integration tests for the {@link DeliveryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DeliveryResourceIT {

    private static final UUID DEFAULT_DELIVERY_ID = UUID.randomUUID();
    private static final UUID UPDATED_DELIVERY_ID = UUID.randomUUID();

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_NOTICE_NO = "AAAAAAAAAA";
    private static final String UPDATED_NOTICE_NO = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_NOTICE_DATE = LocalDate.ofEpochDay(
        0L
    );
    private static final LocalDate UPDATED_NOTICE_DATE = LocalDate.now(
        ZoneId.systemDefault()
    );
    private static final LocalDate SMALLER_NOTICE_DATE = LocalDate.ofEpochDay(
        -1L
    );

    private static final String DEFAULT_SUPPLIER_CODE = "AAAAAAAAAA";
    private static final String UPDATED_SUPPLIER_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_SUPPLIER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SUPPLIER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FACTORY = "AAAAAAAAAA";
    private static final String UPDATED_FACTORY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_DATE_ARRIVAL_RANG_DONG =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime DATE_ARRIVAL_RANG_DONG =
        DEFAULT_DATE_ARRIVAL_RANG_DONG;
    private static final ZonedDateTime UPDATED_DATE_ARRIVAL_RANG_DONG =
        ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_DATE_ARRIVAL_RANG_DONG =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String DEFAULT_VEHICLE_NO = "AAAAAAAAAA";
    private static final String UPDATED_VEHICLE_NO = "BBBBBBBBBB";

    private static final Integer DEFAULT_NUMBER_OF_PO = 1;
    private static final Integer UPDATED_NUMBER_OF_PO = 2;
    private static final Integer SMALLER_NUMBER_OF_PO = 1 - 1;

    private static final ZonedDateTime DEFAULT_CREATED_AT =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(
        ZoneId.systemDefault()
    ).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_AT =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_DELETED_AT =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DELETED_AT = ZonedDateTime.now(
        ZoneId.systemDefault()
    ).withNano(0);
    private static final ZonedDateTime SMALLER_DELETED_AT =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String DEFAULT_DELETED_BY = "AAAAAAAAAA";
    private static final String UPDATED_DELETED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/deliveries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private DeliveryMapper deliveryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDeliveryMockMvc;

    private Delivery delivery;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Delivery createEntity(EntityManager em) {
        Delivery delivery = new Delivery()
            .deliveryId(DEFAULT_DELIVERY_ID)
            .type(DEFAULT_TYPE)
            .noticeNo(DEFAULT_NOTICE_NO)
            .noticeDate(DEFAULT_NOTICE_DATE)
            .supplierCode(DEFAULT_SUPPLIER_CODE)
            .supplierName(DEFAULT_SUPPLIER_NAME)
            .factory(DEFAULT_FACTORY)
            .dateArrivalRangDong(DEFAULT_DATE_ARRIVAL_RANG_DONG)
            .vehicleNo(DEFAULT_VEHICLE_NO)
            .numberOfPo(DEFAULT_NUMBER_OF_PO)
            .createdAt(DEFAULT_CREATED_AT)
            .createdBy(DEFAULT_CREATED_BY)
            .deletedAt(DEFAULT_DELETED_AT)
            .deletedBy(DEFAULT_DELETED_BY);
        return delivery;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Delivery createUpdatedEntity(EntityManager em) {
        Delivery delivery = new Delivery()
            .deliveryId(UPDATED_DELIVERY_ID)
            .type(UPDATED_TYPE)
            .noticeNo(UPDATED_NOTICE_NO)
            .noticeDate(UPDATED_NOTICE_DATE)
            .supplierCode(UPDATED_SUPPLIER_CODE)
            .supplierName(UPDATED_SUPPLIER_NAME)
            .factory(UPDATED_FACTORY)
            .dateArrivalRangDong(UPDATED_DATE_ARRIVAL_RANG_DONG)
            .vehicleNo(UPDATED_VEHICLE_NO)
            .numberOfPo(UPDATED_NUMBER_OF_PO)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        return delivery;
    }

    @BeforeEach
    public void initTest() {
        delivery = createEntity(em);
    }

    @Test
    @Transactional
    void createDelivery() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);
        var returnedDeliveryDTO = om.readValue(
            restDeliveryMockMvc
                .perform(
                    put(ENTITY_API_URL_ID, deliveryDTO.getDeliveryId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(deliveryDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DeliveryDTO.class
        );

        // Validate the Delivery in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDelivery = deliveryMapper.toEntity(returnedDeliveryDTO);
        assertDeliveryUpdatableFieldsEquals(
            returnedDelivery,
            getPersistedDelivery(returnedDelivery)
        );
    }

    @Test
    @Transactional
    void createDeliveryWithExistingId() throws Exception {
        // Create and save a delivery first
        Delivery existingDelivery = createEntity(em);
        deliveryRepository.saveAndFlush(existingDelivery);

        // Try to create another delivery with the same deliveryId
        Delivery deliveryWithSameId = createEntity(em);
        deliveryWithSameId.setDeliveryId(existingDelivery.getDeliveryId());
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(deliveryWithSameId);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDeliveryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate that no new delivery was added to the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDeliveryIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        delivery.setDeliveryId(null);

        // Create the Delivery, which fails.
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        restDeliveryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deliveryDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDeliveries() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList
        restDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(delivery.getDeliveryId().toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].deliveryId").value(
                    hasItem(DEFAULT_DELIVERY_ID.toString())
                )
            )
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(
                jsonPath("$.[*].noticeNo").value(hasItem(DEFAULT_NOTICE_NO))
            )
            .andExpect(
                jsonPath("$.[*].noticeDate").value(
                    hasItem(DEFAULT_NOTICE_DATE.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].supplierCode").value(
                    hasItem(DEFAULT_SUPPLIER_CODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].supplierName").value(
                    hasItem(DEFAULT_SUPPLIER_NAME)
                )
            )
            .andExpect(
                jsonPath("$.[*].factory").value(hasItem(DEFAULT_FACTORY))
            )
            .andExpect(
                jsonPath("$.[*].dateArrivalRangDong").value(
                    hasItem(DEFAULT_DATE_ARRIVAL_RANG_DONG.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].vehicleNo").value(hasItem(DEFAULT_VEHICLE_NO))
            )
            .andExpect(
                jsonPath("$.[*].numberOfPo").value(
                    hasItem(DEFAULT_NUMBER_OF_PO)
                )
            )
            .andExpect(
                jsonPath("$.[*].createdAt").value(
                    hasItem(sameInstant(DEFAULT_CREATED_AT))
                )
            )
            .andExpect(
                jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY))
            )
            .andExpect(
                jsonPath("$.[*].deletedAt").value(
                    hasItem(sameInstant(DEFAULT_DELETED_AT))
                )
            )
            .andExpect(
                jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY))
            );
    }

    @Test
    @Transactional
    void getDelivery() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get the delivery
        restDeliveryMockMvc
            .perform(get(ENTITY_API_URL_ID, delivery.getDeliveryId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.deliveryId").value(
                    delivery.getDeliveryId().toString()
                )
            )
            .andExpect(
                jsonPath("$.deliveryId").value(DEFAULT_DELIVERY_ID.toString())
            )
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.noticeNo").value(DEFAULT_NOTICE_NO))
            .andExpect(
                jsonPath("$.noticeDate").value(DEFAULT_NOTICE_DATE.toString())
            )
            .andExpect(jsonPath("$.supplierCode").value(DEFAULT_SUPPLIER_CODE))
            .andExpect(jsonPath("$.supplierName").value(DEFAULT_SUPPLIER_NAME))
            .andExpect(jsonPath("$.factory").value(DEFAULT_FACTORY))
            .andExpect(
                jsonPath("$.dateArrivalRangDong").value(
                    DATE_ARRIVAL_RANG_DONG.toString()
                )
            )
            .andExpect(jsonPath("$.vehicleNo").value(DEFAULT_VEHICLE_NO))
            .andExpect(jsonPath("$.numberOfPo").value(DEFAULT_NUMBER_OF_PO))
            .andExpect(
                jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT))
            )
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(
                jsonPath("$.deletedAt").value(sameInstant(DEFAULT_DELETED_AT))
            )
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY));
    }

    @Test
    @Transactional
    void getDeliveriesByIdFiltering() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        UUID id = delivery.getDeliveryId();

        defaultDeliveryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDeliveryFiltering(
            "id.greaterThanOrEqual=" + id,
            "id.greaterThan=" + id
        );

        defaultDeliveryFiltering(
            "id.lessThanOrEqual=" + id,
            "id.lessThan=" + id
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeliveryIdIsEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deliveryId equals to
        defaultDeliveryFiltering(
            "deliveryId.equals=" + DEFAULT_DELIVERY_ID,
            "deliveryId.equals=" + UPDATED_DELIVERY_ID
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeliveryIdIsInShouldWork() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deliveryId in
        defaultDeliveryFiltering(
            "deliveryId.in=" + DEFAULT_DELIVERY_ID + "," + UPDATED_DELIVERY_ID,
            "deliveryId.in=" + UPDATED_DELIVERY_ID
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeliveryIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deliveryId is not null
        defaultDeliveryFiltering(
            "deliveryId.specified=true",
            "deliveryId.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where type equals to
        defaultDeliveryFiltering(
            "type.equals=" + DEFAULT_TYPE,
            "type.equals=" + UPDATED_TYPE
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where type in
        defaultDeliveryFiltering(
            "type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE,
            "type.in=" + UPDATED_TYPE
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where type is not null
        defaultDeliveryFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllDeliveriesByTypeContainsSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where type contains
        defaultDeliveryFiltering(
            "type.contains=" + DEFAULT_TYPE,
            "type.contains=" + UPDATED_TYPE
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where type does not contain
        defaultDeliveryFiltering(
            "type.doesNotContain=" + UPDATED_TYPE,
            "type.doesNotContain=" + DEFAULT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByNoticeNoIsEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where noticeNo equals to
        defaultDeliveryFiltering(
            "noticeNo.equals=" + DEFAULT_NOTICE_NO,
            "noticeNo.equals=" + UPDATED_NOTICE_NO
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByNoticeNoIsInShouldWork() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where noticeNo in
        defaultDeliveryFiltering(
            "noticeNo.in=" + DEFAULT_NOTICE_NO + "," + UPDATED_NOTICE_NO,
            "noticeNo.in=" + UPDATED_NOTICE_NO
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByNoticeNoIsNullOrNotNull() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where noticeNo is not null
        defaultDeliveryFiltering(
            "noticeNo.specified=true",
            "noticeNo.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByNoticeNoContainsSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where noticeNo contains
        defaultDeliveryFiltering(
            "noticeNo.contains=" + DEFAULT_NOTICE_NO,
            "noticeNo.contains=" + UPDATED_NOTICE_NO
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByNoticeNoNotContainsSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where noticeNo does not contain
        defaultDeliveryFiltering(
            "noticeNo.doesNotContain=" + UPDATED_NOTICE_NO,
            "noticeNo.doesNotContain=" + DEFAULT_NOTICE_NO
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByNoticeDateIsEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where noticeDate equals to
        defaultDeliveryFiltering(
            "noticeDate.equals=" + DEFAULT_NOTICE_DATE,
            "noticeDate.equals=" + UPDATED_NOTICE_DATE
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByNoticeDateIsInShouldWork() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where noticeDate in
        defaultDeliveryFiltering(
            "noticeDate.in=" + DEFAULT_NOTICE_DATE + "," + UPDATED_NOTICE_DATE,
            "noticeDate.in=" + UPDATED_NOTICE_DATE
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByNoticeDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where noticeDate is not null
        defaultDeliveryFiltering(
            "noticeDate.specified=true",
            "noticeDate.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByNoticeDateIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where noticeDate is greater than or equal to
        defaultDeliveryFiltering(
            "noticeDate.greaterThanOrEqual=" + DEFAULT_NOTICE_DATE,
            "noticeDate.greaterThanOrEqual=" + UPDATED_NOTICE_DATE
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByNoticeDateIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where noticeDate is less than or equal to
        defaultDeliveryFiltering(
            "noticeDate.lessThanOrEqual=" + DEFAULT_NOTICE_DATE,
            "noticeDate.lessThanOrEqual=" + SMALLER_NOTICE_DATE
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByNoticeDateIsLessThanSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where noticeDate is less than
        defaultDeliveryFiltering(
            "noticeDate.lessThan=" + UPDATED_NOTICE_DATE,
            "noticeDate.lessThan=" + DEFAULT_NOTICE_DATE
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByNoticeDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where noticeDate is greater than
        defaultDeliveryFiltering(
            "noticeDate.greaterThan=" + SMALLER_NOTICE_DATE,
            "noticeDate.greaterThan=" + DEFAULT_NOTICE_DATE
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesBySupplierCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where supplierCode equals to
        defaultDeliveryFiltering(
            "supplierCode.equals=" + DEFAULT_SUPPLIER_CODE,
            "supplierCode.equals=" + UPDATED_SUPPLIER_CODE
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesBySupplierCodeIsInShouldWork() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where supplierCode in
        defaultDeliveryFiltering(
            "supplierCode.in=" +
            DEFAULT_SUPPLIER_CODE +
            "," +
            UPDATED_SUPPLIER_CODE,
            "supplierCode.in=" + UPDATED_SUPPLIER_CODE
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesBySupplierCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where supplierCode is not null
        defaultDeliveryFiltering(
            "supplierCode.specified=true",
            "supplierCode.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesBySupplierCodeContainsSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where supplierCode contains
        defaultDeliveryFiltering(
            "supplierCode.contains=" + DEFAULT_SUPPLIER_CODE,
            "supplierCode.contains=" + UPDATED_SUPPLIER_CODE
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesBySupplierCodeNotContainsSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where supplierCode does not contain
        defaultDeliveryFiltering(
            "supplierCode.doesNotContain=" + UPDATED_SUPPLIER_CODE,
            "supplierCode.doesNotContain=" + DEFAULT_SUPPLIER_CODE
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesBySupplierNameIsEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where supplierName equals to
        defaultDeliveryFiltering(
            "supplierName.equals=" + DEFAULT_SUPPLIER_NAME,
            "supplierName.equals=" + UPDATED_SUPPLIER_NAME
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesBySupplierNameIsInShouldWork() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where supplierName in
        defaultDeliveryFiltering(
            "supplierName.in=" +
            DEFAULT_SUPPLIER_NAME +
            "," +
            UPDATED_SUPPLIER_NAME,
            "supplierName.in=" + UPDATED_SUPPLIER_NAME
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesBySupplierNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where supplierName is not null
        defaultDeliveryFiltering(
            "supplierName.specified=true",
            "supplierName.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesBySupplierNameContainsSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where supplierName contains
        defaultDeliveryFiltering(
            "supplierName.contains=" + DEFAULT_SUPPLIER_NAME,
            "supplierName.contains=" + UPDATED_SUPPLIER_NAME
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesBySupplierNameNotContainsSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where supplierName does not contain
        defaultDeliveryFiltering(
            "supplierName.doesNotContain=" + UPDATED_SUPPLIER_NAME,
            "supplierName.doesNotContain=" + DEFAULT_SUPPLIER_NAME
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByFactoryIsEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where factory equals to
        defaultDeliveryFiltering(
            "factory.equals=" + DEFAULT_FACTORY,
            "factory.equals=" + UPDATED_FACTORY
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByFactoryIsInShouldWork() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where factory in
        defaultDeliveryFiltering(
            "factory.in=" + DEFAULT_FACTORY + "," + UPDATED_FACTORY,
            "factory.in=" + UPDATED_FACTORY
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByFactoryIsNullOrNotNull() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where factory is not null
        defaultDeliveryFiltering(
            "factory.specified=true",
            "factory.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByFactoryContainsSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where factory contains
        defaultDeliveryFiltering(
            "factory.contains=" + DEFAULT_FACTORY,
            "factory.contains=" + UPDATED_FACTORY
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByFactoryNotContainsSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where factory does not contain
        defaultDeliveryFiltering(
            "factory.doesNotContain=" + UPDATED_FACTORY,
            "factory.doesNotContain=" + DEFAULT_FACTORY
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByDateArrivalRangDongIsEqualToSomething()
        throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where dateArrivalRangDong equals to
        defaultDeliveryFiltering(
            "dateArrivalRangDong.equals=" + DEFAULT_DATE_ARRIVAL_RANG_DONG,
            "dateArrivalRangDong.equals=" + UPDATED_DATE_ARRIVAL_RANG_DONG
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByDateArrivalRangDongIsInShouldWork()
        throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where dateArrivalRangDong in
        defaultDeliveryFiltering(
            "dateArrivalRangDong.in=" +
            DEFAULT_DATE_ARRIVAL_RANG_DONG +
            "," +
            UPDATED_DATE_ARRIVAL_RANG_DONG,
            "dateArrivalRangDong.in=" + UPDATED_DATE_ARRIVAL_RANG_DONG
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByDateArrivalRangDongIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where dateArrivalRangDong is not null
        defaultDeliveryFiltering(
            "dateArrivalRangDong.specified=true",
            "dateArrivalRangDong.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByDateArrivalRangDongIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where dateArrivalRangDong is greater than or equal to
        defaultDeliveryFiltering(
            "dateArrivalRangDong.greaterThanOrEqual=" +
            DEFAULT_DATE_ARRIVAL_RANG_DONG,
            "dateArrivalRangDong.greaterThanOrEqual=" +
            UPDATED_DATE_ARRIVAL_RANG_DONG
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByDateArrivalRangDongIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where dateArrivalRangDong is less than or equal to
        defaultDeliveryFiltering(
            "dateArrivalRangDong.lessThanOrEqual=" +
            DEFAULT_DATE_ARRIVAL_RANG_DONG,
            "dateArrivalRangDong.lessThanOrEqual=" +
            SMALLER_DATE_ARRIVAL_RANG_DONG
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByDateArrivalRangDongIsLessThanSomething()
        throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where dateArrivalRangDong is less than
        defaultDeliveryFiltering(
            "dateArrivalRangDong.lessThan=" + UPDATED_DATE_ARRIVAL_RANG_DONG,
            "dateArrivalRangDong.lessThan=" + DEFAULT_DATE_ARRIVAL_RANG_DONG
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByDateArrivalRangDongIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where dateArrivalRangDong is greater than
        defaultDeliveryFiltering(
            "dateArrivalRangDong.greaterThan=" + SMALLER_DATE_ARRIVAL_RANG_DONG,
            "dateArrivalRangDong.greaterThan=" + DEFAULT_DATE_ARRIVAL_RANG_DONG
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByVehicleNoIsEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where vehicleNo equals to
        defaultDeliveryFiltering(
            "vehicleNo.equals=" + DEFAULT_VEHICLE_NO,
            "vehicleNo.equals=" + UPDATED_VEHICLE_NO
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByVehicleNoIsInShouldWork() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where vehicleNo in
        defaultDeliveryFiltering(
            "vehicleNo.in=" + DEFAULT_VEHICLE_NO + "," + UPDATED_VEHICLE_NO,
            "vehicleNo.in=" + UPDATED_VEHICLE_NO
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByVehicleNoIsNullOrNotNull() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where vehicleNo is not null
        defaultDeliveryFiltering(
            "vehicleNo.specified=true",
            "vehicleNo.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByVehicleNoContainsSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where vehicleNo contains
        defaultDeliveryFiltering(
            "vehicleNo.contains=" + DEFAULT_VEHICLE_NO,
            "vehicleNo.contains=" + UPDATED_VEHICLE_NO
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByVehicleNoNotContainsSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where vehicleNo does not contain
        defaultDeliveryFiltering(
            "vehicleNo.doesNotContain=" + UPDATED_VEHICLE_NO,
            "vehicleNo.doesNotContain=" + DEFAULT_VEHICLE_NO
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByNumberOfPoIsEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where numberOfPo equals to
        defaultDeliveryFiltering(
            "numberOfPo.equals=" + DEFAULT_NUMBER_OF_PO,
            "numberOfPo.equals=" + UPDATED_NUMBER_OF_PO
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByNumberOfPoIsInShouldWork() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where numberOfPo in
        defaultDeliveryFiltering(
            "numberOfPo.in=" +
            DEFAULT_NUMBER_OF_PO +
            "," +
            UPDATED_NUMBER_OF_PO,
            "numberOfPo.in=" + UPDATED_NUMBER_OF_PO
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByNumberOfPoIsNullOrNotNull() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where numberOfPo is not null
        defaultDeliveryFiltering(
            "numberOfPo.specified=true",
            "numberOfPo.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByNumberOfPoIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where numberOfPo is greater than or equal to
        defaultDeliveryFiltering(
            "numberOfPo.greaterThanOrEqual=" + DEFAULT_NUMBER_OF_PO,
            "numberOfPo.greaterThanOrEqual=" + UPDATED_NUMBER_OF_PO
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByNumberOfPoIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where numberOfPo is less than or equal to
        defaultDeliveryFiltering(
            "numberOfPo.lessThanOrEqual=" + DEFAULT_NUMBER_OF_PO,
            "numberOfPo.lessThanOrEqual=" + SMALLER_NUMBER_OF_PO
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByNumberOfPoIsLessThanSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where numberOfPo is less than
        defaultDeliveryFiltering(
            "numberOfPo.lessThan=" + UPDATED_NUMBER_OF_PO,
            "numberOfPo.lessThan=" + DEFAULT_NUMBER_OF_PO
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByNumberOfPoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where numberOfPo is greater than
        defaultDeliveryFiltering(
            "numberOfPo.greaterThan=" + SMALLER_NUMBER_OF_PO,
            "numberOfPo.greaterThan=" + DEFAULT_NUMBER_OF_PO
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where createdAt equals to
        defaultDeliveryFiltering(
            "createdAt.equals=" + DEFAULT_CREATED_AT,
            "createdAt.equals=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where createdAt in
        defaultDeliveryFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where createdAt is not null
        defaultDeliveryFiltering(
            "createdAt.specified=true",
            "createdAt.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByCreatedAtIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where createdAt is greater than or equal to
        defaultDeliveryFiltering(
            "createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT,
            "createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByCreatedAtIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where createdAt is less than or equal to
        defaultDeliveryFiltering(
            "createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT,
            "createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByCreatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where createdAt is less than
        defaultDeliveryFiltering(
            "createdAt.lessThan=" + UPDATED_CREATED_AT,
            "createdAt.lessThan=" + DEFAULT_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByCreatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where createdAt is greater than
        defaultDeliveryFiltering(
            "createdAt.greaterThan=" + SMALLER_CREATED_AT,
            "createdAt.greaterThan=" + DEFAULT_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where createdBy equals to
        defaultDeliveryFiltering(
            "createdBy.equals=" + DEFAULT_CREATED_BY,
            "createdBy.equals=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where createdBy in
        defaultDeliveryFiltering(
            "createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY,
            "createdBy.in=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where createdBy is not null
        defaultDeliveryFiltering(
            "createdBy.specified=true",
            "createdBy.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where createdBy contains
        defaultDeliveryFiltering(
            "createdBy.contains=" + DEFAULT_CREATED_BY,
            "createdBy.contains=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where createdBy does not contain
        defaultDeliveryFiltering(
            "createdBy.doesNotContain=" + UPDATED_CREATED_BY,
            "createdBy.doesNotContain=" + DEFAULT_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deletedAt equals to
        defaultDeliveryFiltering(
            "deletedAt.equals=" + DEFAULT_DELETED_AT,
            "deletedAt.equals=" + UPDATED_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deletedAt in
        defaultDeliveryFiltering(
            "deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT,
            "deletedAt.in=" + UPDATED_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deletedAt is not null
        defaultDeliveryFiltering(
            "deletedAt.specified=true",
            "deletedAt.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeletedAtIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deletedAt is greater than or equal to
        defaultDeliveryFiltering(
            "deletedAt.greaterThanOrEqual=" + DEFAULT_DELETED_AT,
            "deletedAt.greaterThanOrEqual=" + UPDATED_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeletedAtIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deletedAt is less than or equal to
        defaultDeliveryFiltering(
            "deletedAt.lessThanOrEqual=" + DEFAULT_DELETED_AT,
            "deletedAt.lessThanOrEqual=" + SMALLER_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeletedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deletedAt is less than
        defaultDeliveryFiltering(
            "deletedAt.lessThan=" + UPDATED_DELETED_AT,
            "deletedAt.lessThan=" + DEFAULT_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeletedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deletedAt is greater than
        defaultDeliveryFiltering(
            "deletedAt.greaterThan=" + SMALLER_DELETED_AT,
            "deletedAt.greaterThan=" + DEFAULT_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deletedBy equals to
        defaultDeliveryFiltering(
            "deletedBy.equals=" + DEFAULT_DELETED_BY,
            "deletedBy.equals=" + UPDATED_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deletedBy in
        defaultDeliveryFiltering(
            "deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY,
            "deletedBy.in=" + UPDATED_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deletedBy is not null
        defaultDeliveryFiltering(
            "deletedBy.specified=true",
            "deletedBy.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeletedByContainsSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deletedBy contains
        defaultDeliveryFiltering(
            "deletedBy.contains=" + DEFAULT_DELETED_BY,
            "deletedBy.contains=" + UPDATED_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllDeliveriesByDeletedByNotContainsSomething() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList where deletedBy does not contain
        defaultDeliveryFiltering(
            "deletedBy.doesNotContain=" + UPDATED_DELETED_BY,
            "deletedBy.doesNotContain=" + DEFAULT_DELETED_BY
        );
    }

    private void defaultDeliveryFiltering(
        String shouldBeFound,
        String shouldNotBeFound
    ) throws Exception {
        defaultDeliveryShouldBeFound(shouldBeFound);
        defaultDeliveryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDeliveryShouldBeFound(String filter) throws Exception {
        restDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].deliveryId").value(
                    hasItem(delivery.getDeliveryId().toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].deliveryId").value(
                    hasItem(DEFAULT_DELIVERY_ID.toString())
                )
            )
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(
                jsonPath("$.[*].noticeNo").value(hasItem(DEFAULT_NOTICE_NO))
            )
            .andExpect(
                jsonPath("$.[*].noticeDate").value(
                    hasItem(DEFAULT_NOTICE_DATE.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].supplierCode").value(
                    hasItem(DEFAULT_SUPPLIER_CODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].supplierName").value(
                    hasItem(DEFAULT_SUPPLIER_NAME)
                )
            )
            .andExpect(
                jsonPath("$.[*].factory").value(hasItem(DEFAULT_FACTORY))
            )
            .andExpect(
                jsonPath("$.[*].dateArrivalRangDong").value(
                    hasItem(DEFAULT_DATE_ARRIVAL_RANG_DONG.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].vehicleNo").value(hasItem(DEFAULT_VEHICLE_NO))
            )
            .andExpect(
                jsonPath("$.[*].numberOfPo").value(
                    hasItem(DEFAULT_NUMBER_OF_PO)
                )
            )
            .andExpect(
                jsonPath("$.[*].createdAt").value(
                    hasItem(sameInstant(DEFAULT_CREATED_AT))
                )
            )
            .andExpect(
                jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY))
            )
            .andExpect(
                jsonPath("$.[*].deletedAt").value(
                    hasItem(sameInstant(DEFAULT_DELETED_AT))
                )
            )
            .andExpect(
                jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY))
            );

        // Check, that the count call also returns 1
        restDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDeliveryShouldNotBeFound(String filter)
        throws Exception {
        restDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDelivery() throws Exception {
        // Get the delivery
        restDeliveryMockMvc
            .perform(get(ENTITY_API_URL_ID, UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDelivery() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the delivery
        Delivery updatedDelivery = deliveryRepository
            .findById(delivery.getDeliveryId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedDelivery are not directly saved in db
        em.detach(updatedDelivery);
        updatedDelivery
            .deliveryId(UPDATED_DELIVERY_ID)
            .type(UPDATED_TYPE)
            .noticeNo(UPDATED_NOTICE_NO)
            .noticeDate(UPDATED_NOTICE_DATE)
            .supplierCode(UPDATED_SUPPLIER_CODE)
            .supplierName(UPDATED_SUPPLIER_NAME)
            .factory(UPDATED_FACTORY)
            .dateArrivalRangDong(UPDATED_DATE_ARRIVAL_RANG_DONG)
            .vehicleNo(UPDATED_VEHICLE_NO)
            .numberOfPo(UPDATED_NUMBER_OF_PO)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(updatedDelivery);

        // Create partial update for PATCH request
        Delivery partialUpdatedDelivery = new Delivery();
        partialUpdatedDelivery.setDeliveryId(delivery.getDeliveryId());
        partialUpdatedDelivery
            .deliveryId(UPDATED_DELIVERY_ID)
            .type(UPDATED_TYPE)
            .noticeNo(UPDATED_NOTICE_NO)
            .noticeDate(UPDATED_NOTICE_DATE)
            .supplierCode(UPDATED_SUPPLIER_CODE)
            .supplierName(UPDATED_SUPPLIER_NAME)
            .factory(UPDATED_FACTORY)
            .dateArrivalRangDong(UPDATED_DATE_ARRIVAL_RANG_DONG)
            .vehicleNo(UPDATED_VEHICLE_NO)
            .numberOfPo(UPDATED_NUMBER_OF_PO)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDelivery.getDeliveryId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDelivery))
            )
            .andExpect(status().isOk());

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDeliveryToMatchAllProperties(updatedDelivery);
    }

    @Test
    @Transactional
    void putNonExistingDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        delivery.setDeliveryId(UUID.randomUUID());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeliveryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, deliveryDTO.getDeliveryId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        delivery.setDeliveryId(UUID.randomUUID());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, deliveryDTO.getDeliveryId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        delivery.setDeliveryId(UUID.randomUUID());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deliveryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDeliveryWithPatch() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the delivery using partial update
        Delivery partialUpdatedDelivery = new Delivery();
        partialUpdatedDelivery.setDeliveryId(delivery.getDeliveryId());

        partialUpdatedDelivery
            .deliveryId(UPDATED_DELIVERY_ID)
            .type(UPDATED_TYPE)
            .noticeNo(UPDATED_NOTICE_NO)
            .noticeDate(UPDATED_NOTICE_DATE)
            .supplierCode(UPDATED_SUPPLIER_CODE)
            .supplierName(UPDATED_SUPPLIER_NAME)
            .factory(UPDATED_FACTORY)
            .dateArrivalRangDong(UPDATED_DATE_ARRIVAL_RANG_DONG)
            .vehicleNo(UPDATED_VEHICLE_NO)
            .numberOfPo(UPDATED_NUMBER_OF_PO)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDelivery.getDeliveryId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDelivery))
            )
            .andExpect(status().isOk());

        // Validate the Delivery in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDeliveryUpdatableFieldsEquals(
            partialUpdatedDelivery,
            getPersistedDelivery(partialUpdatedDelivery)
        );
    }

    @Test
    @Transactional
    void patchNonExistingDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        delivery.setDeliveryId(UUID.randomUUID());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // Create partial update object for non-existing delivery
        Delivery partialUpdatedDelivery = new Delivery();
        partialUpdatedDelivery.setDeliveryId(UUID.randomUUID());
        partialUpdatedDelivery
            .deliveryId(UPDATED_DELIVERY_ID)
            .type(UPDATED_TYPE)
            .noticeNo(UPDATED_NOTICE_NO)
            .noticeDate(UPDATED_NOTICE_DATE)
            .supplierCode(UPDATED_SUPPLIER_CODE)
            .supplierName(UPDATED_SUPPLIER_NAME)
            .factory(UPDATED_FACTORY)
            .dateArrivalRangDong(UPDATED_DATE_ARRIVAL_RANG_DONG)
            .vehicleNo(UPDATED_VEHICLE_NO)
            .numberOfPo(UPDATED_NUMBER_OF_PO)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDelivery.getDeliveryId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDelivery))
            )
            .andExpect(status().isBadRequest());

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        delivery.setDeliveryId(UUID.randomUUID());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // Create partial update object with different ID
        Delivery partialUpdatedDelivery = new Delivery();
        partialUpdatedDelivery.setDeliveryId(UUID.randomUUID());
        partialUpdatedDelivery
            .deliveryId(UPDATED_DELIVERY_ID)
            .type(UPDATED_TYPE)
            .noticeNo(UPDATED_NOTICE_NO)
            .noticeDate(UPDATED_NOTICE_DATE)
            .supplierCode(UPDATED_SUPPLIER_CODE)
            .supplierName(UPDATED_SUPPLIER_NAME)
            .factory(UPDATED_FACTORY)
            .dateArrivalRangDong(UPDATED_DATE_ARRIVAL_RANG_DONG)
            .vehicleNo(UPDATED_VEHICLE_NO)
            .numberOfPo(UPDATED_NUMBER_OF_PO)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDelivery.getDeliveryId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(deliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        delivery.setDeliveryId(UUID.randomUUID());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(deliveryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDelivery() throws Exception {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the delivery
        restDeliveryMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, delivery.getDeliveryId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return deliveryRepository.count();
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

    protected Delivery getPersistedDelivery(Delivery delivery) {
        return deliveryRepository
            .findById(delivery.getDeliveryId())
            .orElseThrow();
    }

    protected void assertPersistedDeliveryToMatchAllProperties(
        Delivery expectedDelivery
    ) {
        assertDeliveryAllPropertiesEquals(
            expectedDelivery,
            getPersistedDelivery(expectedDelivery)
        );
    }

    protected void assertPersistedDeliveryToMatchUpdatableProperties(
        Delivery expectedDelivery
    ) {
        assertDeliveryAllUpdatablePropertiesEquals(
            expectedDelivery,
            getPersistedDelivery(expectedDelivery)
        );
    }
}
