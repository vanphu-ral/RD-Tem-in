package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.DeliveryNotificationAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.DeliveryNotification;
import com.mycompany.myapp.domain.PalletBoxMapping;
import com.mycompany.myapp.domain.PalletBoxMappingTestSamples;
import com.mycompany.myapp.domain.VendorLabelInfo;
import com.mycompany.myapp.domain.VendorLabelInfoTestSamples;
import com.mycompany.myapp.repository.DeliveryNotificationRepository;
import com.mycompany.myapp.repository.PalletBoxMappingRepository;
import com.mycompany.myapp.repository.VendorLabelInfoRepository;
import com.mycompany.myapp.service.dto.DeliveryNotificationDTO;
import com.mycompany.myapp.service.mapper.DeliveryNotificationMapper;
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
 * Integration tests for the {@link DeliveryNotificationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DeliveryNotificationResourceIT {

    private static final String DEFAULT_DELIVERY_NOTIFICATION_CODE =
        "AAAAAAAAAA";
    private static final String UPDATED_DELIVERY_NOTIFICATION_CODE =
        "BBBBBBBBBB";

    private static final String DEFAULT_SERIAL_BOX = "AAAAAAAAAA";
    private static final String UPDATED_SERIAL_BOX = "BBBBBBBBBB";

    private static final String DEFAULT_INVOICE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_INVOICE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_CONTRACT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CONTRACT_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_VENDOR_NAME = "AAAAAAAAAA";
    private static final String UPDATED_VENDOR_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CONT_NO = "AAAAAAAAAA";
    private static final String UPDATED_CONT_NO = "BBBBBBBBBB";

    private static final Instant DEFAULT_ENTRY_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ENTRY_DATE = Instant.now().truncatedTo(
        ChronoUnit.MILLIS
    );

    private static final Integer DEFAULT_NUMBER_OF_PO = 1;
    private static final Integer UPDATED_NUMBER_OF_PO = 2;
    private static final Integer SMALLER_NUMBER_OF_PO = 1 - 1;

    private static final Integer DEFAULT_NUMBER_OF_ITEM = 1;
    private static final Integer UPDATED_NUMBER_OF_ITEM = 2;
    private static final Integer SMALLER_NUMBER_OF_ITEM = 1 - 1;

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(
        ChronoUnit.MILLIS
    );

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_DELETED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DELETED_AT = Instant.now().truncatedTo(
        ChronoUnit.MILLIS
    );

    private static final String DEFAULT_DELETED_BY = "AAAAAAAAAA";
    private static final String UPDATED_DELETED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/delivery-notifications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final String DEFAULT_REEL_ID = "AAAAAAAAAA";
    private static final String DEFAULT_SERIAL_PALLET = "AAAAAAAAAA";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DeliveryNotificationRepository deliveryNotificationRepository;

    @Autowired
    private VendorLabelInfoRepository vendorLabelInfoRepository;

    @Autowired
    private PalletBoxMappingRepository palletBoxMappingRepository;

    @Autowired
    private DeliveryNotificationMapper deliveryNotificationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDeliveryNotificationMockMvc;

    private DeliveryNotification deliveryNotification;

    private DeliveryNotification insertedDeliveryNotification;

    private VendorLabelInfo insertedVendorLabelInfo;

    private PalletBoxMapping insertedPalletBoxMapping;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DeliveryNotification createEntity() {
        return new DeliveryNotification()
            .deliveryNotificationCode(DEFAULT_DELIVERY_NOTIFICATION_CODE)
            .invoiceNumber(DEFAULT_INVOICE_NUMBER)
            .contractCode(DEFAULT_CONTRACT_CODE)
            .vendorName(DEFAULT_VENDOR_NAME)
            .contNo(DEFAULT_CONT_NO)
            .entryDate(DEFAULT_ENTRY_DATE)
            .numberOfPo(DEFAULT_NUMBER_OF_PO)
            .numberOfItem(DEFAULT_NUMBER_OF_ITEM)
            .status(DEFAULT_STATUS)
            .createdAt(DEFAULT_CREATED_AT)
            .createdBy(DEFAULT_CREATED_BY)
            .deletedAt(DEFAULT_DELETED_AT)
            .deletedBy(DEFAULT_DELETED_BY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DeliveryNotification createUpdatedEntity() {
        return new DeliveryNotification()
            .deliveryNotificationCode(UPDATED_DELIVERY_NOTIFICATION_CODE)
            .invoiceNumber(UPDATED_INVOICE_NUMBER)
            .contractCode(UPDATED_CONTRACT_CODE)
            .vendorName(UPDATED_VENDOR_NAME)
            .contNo(UPDATED_CONT_NO)
            .entryDate(UPDATED_ENTRY_DATE)
            .numberOfPo(UPDATED_NUMBER_OF_PO)
            .numberOfItem(UPDATED_NUMBER_OF_ITEM)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
    }

    @BeforeEach
    void initTest() {
        deliveryNotification = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPalletBoxMapping != null) {
            palletBoxMappingRepository.deleteById(
                insertedPalletBoxMapping.getId()
            );
            insertedPalletBoxMapping = null;
        }
        if (insertedVendorLabelInfo != null) {
            vendorLabelInfoRepository.deleteById(
                insertedVendorLabelInfo.getId()
            );
            insertedVendorLabelInfo = null;
        }
        if (insertedDeliveryNotification != null) {
            deliveryNotificationRepository.delete(insertedDeliveryNotification);
            insertedDeliveryNotification = null;
        }
    }

    @Test
    @Transactional
    void createDeliveryNotification() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DeliveryNotification
        DeliveryNotificationDTO deliveryNotificationDTO =
            deliveryNotificationMapper.toDto(deliveryNotification);
        var returnedDeliveryNotificationDTO = om.readValue(
            restDeliveryNotificationMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(deliveryNotificationDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DeliveryNotificationDTO.class
        );

        // Validate the DeliveryNotification in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDeliveryNotification = deliveryNotificationMapper.toEntity(
            returnedDeliveryNotificationDTO
        );
        assertDeliveryNotificationUpdatableFieldsEquals(
            returnedDeliveryNotification,
            getPersistedDeliveryNotification(returnedDeliveryNotification)
        );

        insertedDeliveryNotification = returnedDeliveryNotification;
    }

    @Test
    @Transactional
    void createDeliveryNotificationWithExistingId() throws Exception {
        // Create the DeliveryNotification with an existing ID
        deliveryNotification.setId(1L);
        DeliveryNotificationDTO deliveryNotificationDTO =
            deliveryNotificationMapper.toDto(deliveryNotification);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDeliveryNotificationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deliveryNotificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DeliveryNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDeliveryNotifications() throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList
        restDeliveryNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(deliveryNotification.getId().intValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].deliveryNotificationCode").value(
                    hasItem(DEFAULT_DELIVERY_NOTIFICATION_CODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].serialBox").value(hasItem(DEFAULT_SERIAL_BOX))
            )
            .andExpect(
                jsonPath("$.[*].invoiceNumber").value(
                    hasItem(DEFAULT_INVOICE_NUMBER)
                )
            )
            .andExpect(
                jsonPath("$.[*].contractCode").value(
                    hasItem(DEFAULT_CONTRACT_CODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].vendorName").value(hasItem(DEFAULT_VENDOR_NAME))
            )
            .andExpect(jsonPath("$.[*].contNo").value(hasItem(DEFAULT_CONT_NO)))
            .andExpect(
                jsonPath("$.[*].entryDate").value(
                    hasItem(DEFAULT_ENTRY_DATE.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].numberOfPo").value(
                    hasItem(DEFAULT_NUMBER_OF_PO)
                )
            )
            .andExpect(
                jsonPath("$.[*].numberOfItem").value(
                    hasItem(DEFAULT_NUMBER_OF_ITEM)
                )
            )
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(
                jsonPath("$.[*].createdAt").value(
                    hasItem(DEFAULT_CREATED_AT.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY))
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
    void getDeliveryNotification() throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get the deliveryNotification
        restDeliveryNotificationMockMvc
            .perform(get(ENTITY_API_URL_ID, deliveryNotification.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.id").value(deliveryNotification.getId().intValue())
            )
            .andExpect(
                jsonPath("$.deliveryNotificationCode").value(
                    DEFAULT_DELIVERY_NOTIFICATION_CODE
                )
            )
            .andExpect(jsonPath("$.serialBox").value(DEFAULT_SERIAL_BOX))
            .andExpect(
                jsonPath("$.invoiceNumber").value(DEFAULT_INVOICE_NUMBER)
            )
            .andExpect(jsonPath("$.contractCode").value(DEFAULT_CONTRACT_CODE))
            .andExpect(jsonPath("$.vendorName").value(DEFAULT_VENDOR_NAME))
            .andExpect(jsonPath("$.contNo").value(DEFAULT_CONT_NO))
            .andExpect(
                jsonPath("$.entryDate").value(DEFAULT_ENTRY_DATE.toString())
            )
            .andExpect(jsonPath("$.numberOfPo").value(DEFAULT_NUMBER_OF_PO))
            .andExpect(jsonPath("$.numberOfItem").value(DEFAULT_NUMBER_OF_ITEM))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(
                jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString())
            )
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(
                jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString())
            )
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY));
    }

    @Test
    @Transactional
    void getDeliveryNotificationsByIdFiltering() throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        Long id = deliveryNotification.getId();

        defaultDeliveryNotificationFiltering(
            "id.equals=" + id,
            "id.notEquals=" + id
        );

        defaultDeliveryNotificationFiltering(
            "id.greaterThanOrEqual=" + id,
            "id.greaterThan=" + id
        );

        defaultDeliveryNotificationFiltering(
            "id.lessThanOrEqual=" + id,
            "id.lessThan=" + id
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByDeliveryNotificationCodeIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where deliveryNotificationCode equals to
        defaultDeliveryNotificationFiltering(
            "deliveryNotificationCode.equals=" +
            DEFAULT_DELIVERY_NOTIFICATION_CODE,
            "deliveryNotificationCode.equals=" +
            UPDATED_DELIVERY_NOTIFICATION_CODE
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByDeliveryNotificationCodeIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where deliveryNotificationCode in
        defaultDeliveryNotificationFiltering(
            "deliveryNotificationCode.in=" +
            DEFAULT_DELIVERY_NOTIFICATION_CODE +
            "," +
            UPDATED_DELIVERY_NOTIFICATION_CODE,
            "deliveryNotificationCode.in=" + UPDATED_DELIVERY_NOTIFICATION_CODE
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByDeliveryNotificationCodeIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where deliveryNotificationCode is not null
        defaultDeliveryNotificationFiltering(
            "deliveryNotificationCode.specified=true",
            "deliveryNotificationCode.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByDeliveryNotificationCodeContainsSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where deliveryNotificationCode contains
        defaultDeliveryNotificationFiltering(
            "deliveryNotificationCode.contains=" +
            DEFAULT_DELIVERY_NOTIFICATION_CODE,
            "deliveryNotificationCode.contains=" +
            UPDATED_DELIVERY_NOTIFICATION_CODE
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByDeliveryNotificationCodeNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where deliveryNotificationCode does not contain
        defaultDeliveryNotificationFiltering(
            "deliveryNotificationCode.doesNotContain=" +
            UPDATED_DELIVERY_NOTIFICATION_CODE,
            "deliveryNotificationCode.doesNotContain=" +
            DEFAULT_DELIVERY_NOTIFICATION_CODE
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsBySerialBoxIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where serialBox equals to
        defaultDeliveryNotificationFiltering(
            "serialBox.equals=" + DEFAULT_SERIAL_BOX,
            "serialBox.equals=" + UPDATED_SERIAL_BOX
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsBySerialBoxIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where serialBox in
        defaultDeliveryNotificationFiltering(
            "serialBox.in=" + DEFAULT_SERIAL_BOX + "," + UPDATED_SERIAL_BOX,
            "serialBox.in=" + UPDATED_SERIAL_BOX
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsBySerialBoxIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where serialBox is not null
        defaultDeliveryNotificationFiltering(
            "serialBox.specified=true",
            "serialBox.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsBySerialBoxContainsSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where serialBox contains
        defaultDeliveryNotificationFiltering(
            "serialBox.contains=" + DEFAULT_SERIAL_BOX,
            "serialBox.contains=" + UPDATED_SERIAL_BOX
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsBySerialBoxNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where serialBox does not contain
        defaultDeliveryNotificationFiltering(
            "serialBox.doesNotContain=" + UPDATED_SERIAL_BOX,
            "serialBox.doesNotContain=" + DEFAULT_SERIAL_BOX
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByInvoiceNumberIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where invoiceNumber equals to
        defaultDeliveryNotificationFiltering(
            "invoiceNumber.equals=" + DEFAULT_INVOICE_NUMBER,
            "invoiceNumber.equals=" + UPDATED_INVOICE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByInvoiceNumberIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where invoiceNumber in
        defaultDeliveryNotificationFiltering(
            "invoiceNumber.in=" +
            DEFAULT_INVOICE_NUMBER +
            "," +
            UPDATED_INVOICE_NUMBER,
            "invoiceNumber.in=" + UPDATED_INVOICE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByInvoiceNumberIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where invoiceNumber is not null
        defaultDeliveryNotificationFiltering(
            "invoiceNumber.specified=true",
            "invoiceNumber.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByInvoiceNumberContainsSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where invoiceNumber contains
        defaultDeliveryNotificationFiltering(
            "invoiceNumber.contains=" + DEFAULT_INVOICE_NUMBER,
            "invoiceNumber.contains=" + UPDATED_INVOICE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByInvoiceNumberNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where invoiceNumber does not contain
        defaultDeliveryNotificationFiltering(
            "invoiceNumber.doesNotContain=" + UPDATED_INVOICE_NUMBER,
            "invoiceNumber.doesNotContain=" + DEFAULT_INVOICE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByContractCodeIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where contractCode equals to
        defaultDeliveryNotificationFiltering(
            "contractCode.equals=" + DEFAULT_CONTRACT_CODE,
            "contractCode.equals=" + UPDATED_CONTRACT_CODE
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByContractCodeIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where contractCode in
        defaultDeliveryNotificationFiltering(
            "contractCode.in=" +
            DEFAULT_CONTRACT_CODE +
            "," +
            UPDATED_CONTRACT_CODE,
            "contractCode.in=" + UPDATED_CONTRACT_CODE
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByContractCodeIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where contractCode is not null
        defaultDeliveryNotificationFiltering(
            "contractCode.specified=true",
            "contractCode.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByContractCodeContainsSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where contractCode contains
        defaultDeliveryNotificationFiltering(
            "contractCode.contains=" + DEFAULT_CONTRACT_CODE,
            "contractCode.contains=" + UPDATED_CONTRACT_CODE
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByContractCodeNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where contractCode does not contain
        defaultDeliveryNotificationFiltering(
            "contractCode.doesNotContain=" + UPDATED_CONTRACT_CODE,
            "contractCode.doesNotContain=" + DEFAULT_CONTRACT_CODE
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByVendorNameIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where vendorName equals to
        defaultDeliveryNotificationFiltering(
            "vendorName.equals=" + DEFAULT_VENDOR_NAME,
            "vendorName.equals=" + UPDATED_VENDOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByVendorNameIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where vendorName in
        defaultDeliveryNotificationFiltering(
            "vendorName.in=" + DEFAULT_VENDOR_NAME + "," + UPDATED_VENDOR_NAME,
            "vendorName.in=" + UPDATED_VENDOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByVendorNameIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where vendorName is not null
        defaultDeliveryNotificationFiltering(
            "vendorName.specified=true",
            "vendorName.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByVendorNameContainsSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where vendorName contains
        defaultDeliveryNotificationFiltering(
            "vendorName.contains=" + DEFAULT_VENDOR_NAME,
            "vendorName.contains=" + UPDATED_VENDOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByVendorNameNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where vendorName does not contain
        defaultDeliveryNotificationFiltering(
            "vendorName.doesNotContain=" + UPDATED_VENDOR_NAME,
            "vendorName.doesNotContain=" + DEFAULT_VENDOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByContNoIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where contNo equals to
        defaultDeliveryNotificationFiltering(
            "contNo.equals=" + DEFAULT_CONT_NO,
            "contNo.equals=" + UPDATED_CONT_NO
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByContNoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where contNo in
        defaultDeliveryNotificationFiltering(
            "contNo.in=" + DEFAULT_CONT_NO + "," + UPDATED_CONT_NO,
            "contNo.in=" + UPDATED_CONT_NO
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByContNoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where contNo is not null
        defaultDeliveryNotificationFiltering(
            "contNo.specified=true",
            "contNo.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByContNoContainsSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where contNo contains
        defaultDeliveryNotificationFiltering(
            "contNo.contains=" + DEFAULT_CONT_NO,
            "contNo.contains=" + UPDATED_CONT_NO
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByContNoNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where contNo does not contain
        defaultDeliveryNotificationFiltering(
            "contNo.doesNotContain=" + UPDATED_CONT_NO,
            "contNo.doesNotContain=" + DEFAULT_CONT_NO
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByEntryDateIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where entryDate equals to
        defaultDeliveryNotificationFiltering(
            "entryDate.equals=" + DEFAULT_ENTRY_DATE,
            "entryDate.equals=" + UPDATED_ENTRY_DATE
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByEntryDateIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where entryDate in
        defaultDeliveryNotificationFiltering(
            "entryDate.in=" + DEFAULT_ENTRY_DATE + "," + UPDATED_ENTRY_DATE,
            "entryDate.in=" + UPDATED_ENTRY_DATE
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByEntryDateIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where entryDate is not null
        defaultDeliveryNotificationFiltering(
            "entryDate.specified=true",
            "entryDate.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByNumberOfPoIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where numberOfPo equals to
        defaultDeliveryNotificationFiltering(
            "numberOfPo.equals=" + DEFAULT_NUMBER_OF_PO,
            "numberOfPo.equals=" + UPDATED_NUMBER_OF_PO
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByNumberOfPoIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where numberOfPo in
        defaultDeliveryNotificationFiltering(
            "numberOfPo.in=" +
            DEFAULT_NUMBER_OF_PO +
            "," +
            UPDATED_NUMBER_OF_PO,
            "numberOfPo.in=" + UPDATED_NUMBER_OF_PO
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByNumberOfPoIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where numberOfPo is not null
        defaultDeliveryNotificationFiltering(
            "numberOfPo.specified=true",
            "numberOfPo.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByNumberOfPoIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where numberOfPo is greater than or equal to
        defaultDeliveryNotificationFiltering(
            "numberOfPo.greaterThanOrEqual=" + DEFAULT_NUMBER_OF_PO,
            "numberOfPo.greaterThanOrEqual=" + UPDATED_NUMBER_OF_PO
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByNumberOfPoIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where numberOfPo is less than or equal to
        defaultDeliveryNotificationFiltering(
            "numberOfPo.lessThanOrEqual=" + DEFAULT_NUMBER_OF_PO,
            "numberOfPo.lessThanOrEqual=" + SMALLER_NUMBER_OF_PO
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByNumberOfPoIsLessThanSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where numberOfPo is less than
        defaultDeliveryNotificationFiltering(
            "numberOfPo.lessThan=" + UPDATED_NUMBER_OF_PO,
            "numberOfPo.lessThan=" + DEFAULT_NUMBER_OF_PO
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByNumberOfPoIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where numberOfPo is greater than
        defaultDeliveryNotificationFiltering(
            "numberOfPo.greaterThan=" + SMALLER_NUMBER_OF_PO,
            "numberOfPo.greaterThan=" + DEFAULT_NUMBER_OF_PO
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByNumberOfItemIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where numberOfItem equals to
        defaultDeliveryNotificationFiltering(
            "numberOfItem.equals=" + DEFAULT_NUMBER_OF_ITEM,
            "numberOfItem.equals=" + UPDATED_NUMBER_OF_ITEM
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByNumberOfItemIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where numberOfItem in
        defaultDeliveryNotificationFiltering(
            "numberOfItem.in=" +
            DEFAULT_NUMBER_OF_ITEM +
            "," +
            UPDATED_NUMBER_OF_ITEM,
            "numberOfItem.in=" + UPDATED_NUMBER_OF_ITEM
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByNumberOfItemIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where numberOfItem is not null
        defaultDeliveryNotificationFiltering(
            "numberOfItem.specified=true",
            "numberOfItem.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByNumberOfItemIsGreaterThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where numberOfItem is greater than or equal to
        defaultDeliveryNotificationFiltering(
            "numberOfItem.greaterThanOrEqual=" + DEFAULT_NUMBER_OF_ITEM,
            "numberOfItem.greaterThanOrEqual=" + UPDATED_NUMBER_OF_ITEM
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByNumberOfItemIsLessThanOrEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where numberOfItem is less than or equal to
        defaultDeliveryNotificationFiltering(
            "numberOfItem.lessThanOrEqual=" + DEFAULT_NUMBER_OF_ITEM,
            "numberOfItem.lessThanOrEqual=" + SMALLER_NUMBER_OF_ITEM
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByNumberOfItemIsLessThanSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where numberOfItem is less than
        defaultDeliveryNotificationFiltering(
            "numberOfItem.lessThan=" + UPDATED_NUMBER_OF_ITEM,
            "numberOfItem.lessThan=" + DEFAULT_NUMBER_OF_ITEM
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByNumberOfItemIsGreaterThanSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where numberOfItem is greater than
        defaultDeliveryNotificationFiltering(
            "numberOfItem.greaterThan=" + SMALLER_NUMBER_OF_ITEM,
            "numberOfItem.greaterThan=" + DEFAULT_NUMBER_OF_ITEM
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByStatusIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where status equals to
        defaultDeliveryNotificationFiltering(
            "status.equals=" + DEFAULT_STATUS,
            "status.equals=" + UPDATED_STATUS
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where status in
        defaultDeliveryNotificationFiltering(
            "status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS,
            "status.in=" + UPDATED_STATUS
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where status is not null
        defaultDeliveryNotificationFiltering(
            "status.specified=true",
            "status.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByStatusContainsSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where status contains
        defaultDeliveryNotificationFiltering(
            "status.contains=" + DEFAULT_STATUS,
            "status.contains=" + UPDATED_STATUS
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByStatusNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where status does not contain
        defaultDeliveryNotificationFiltering(
            "status.doesNotContain=" + UPDATED_STATUS,
            "status.doesNotContain=" + DEFAULT_STATUS
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByCreateAtIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where createdAt equals to
        defaultDeliveryNotificationFiltering(
            "createdAt.equals=" + DEFAULT_CREATED_AT,
            "createdAt.equals=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByCreateAtIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where createdAt in
        defaultDeliveryNotificationFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByCreateAtIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where createdAt is not null
        defaultDeliveryNotificationFiltering(
            "createdAt.specified=true",
            "createdAt.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByCreateByIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where createdBy equals to
        defaultDeliveryNotificationFiltering(
            "createdBy.equals=" + DEFAULT_CREATED_BY,
            "createdBy.equals=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByCreateByIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where createdBy in
        defaultDeliveryNotificationFiltering(
            "createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY,
            "createdBy.in=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByCreateByIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where createdBy is not null
        defaultDeliveryNotificationFiltering(
            "createdBy.specified=true",
            "createdBy.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByCreateByContainsSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where createdBy contains
        defaultDeliveryNotificationFiltering(
            "createdBy.contains=" + DEFAULT_CREATED_BY,
            "createdBy.contains=" + UPDATED_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByCreateByNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where createdBy does not contain
        defaultDeliveryNotificationFiltering(
            "createdBy.doesNotContain=" + UPDATED_CREATED_BY,
            "createdBy.doesNotContain=" + DEFAULT_CREATED_BY
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByDeletedAtIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where deletedAt equals to
        defaultDeliveryNotificationFiltering(
            "deletedAt.equals=" + DEFAULT_DELETED_AT,
            "deletedAt.equals=" + UPDATED_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByDeletedAtIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where deletedAt in
        defaultDeliveryNotificationFiltering(
            "deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT,
            "deletedAt.in=" + UPDATED_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByDeletedAtIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where deletedAt is not null
        defaultDeliveryNotificationFiltering(
            "deletedAt.specified=true",
            "deletedAt.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByDeletedByIsEqualToSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where deletedBy equals to
        defaultDeliveryNotificationFiltering(
            "deletedBy.equals=" + DEFAULT_DELETED_BY,
            "deletedBy.equals=" + UPDATED_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByDeletedByIsInShouldWork()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where deletedBy in
        defaultDeliveryNotificationFiltering(
            "deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY,
            "deletedBy.in=" + UPDATED_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByDeletedByIsNullOrNotNull()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where deletedBy is not null
        defaultDeliveryNotificationFiltering(
            "deletedBy.specified=true",
            "deletedBy.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByDeletedByContainsSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where deletedBy contains
        defaultDeliveryNotificationFiltering(
            "deletedBy.contains=" + DEFAULT_DELETED_BY,
            "deletedBy.contains=" + UPDATED_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllDeliveryNotificationsByDeletedByNotContainsSomething()
        throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        // Get all the deliveryNotificationList where deletedBy does not contain
        defaultDeliveryNotificationFiltering(
            "deletedBy.doesNotContain=" + UPDATED_DELETED_BY,
            "deletedBy.doesNotContain=" + DEFAULT_DELETED_BY
        );
    }

    private void defaultDeliveryNotificationFiltering(
        String shouldBeFound,
        String shouldNotBeFound
    ) throws Exception {
        defaultDeliveryNotificationShouldBeFound(shouldBeFound);
        defaultDeliveryNotificationShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDeliveryNotificationShouldBeFound(String filter)
        throws Exception {
        restDeliveryNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(deliveryNotification.getId().intValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].deliveryNotificationCode").value(
                    hasItem(DEFAULT_DELIVERY_NOTIFICATION_CODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].serialBox").value(hasItem(DEFAULT_SERIAL_BOX))
            )
            .andExpect(
                jsonPath("$.[*].invoiceNumber").value(
                    hasItem(DEFAULT_INVOICE_NUMBER)
                )
            )
            .andExpect(
                jsonPath("$.[*].contractCode").value(
                    hasItem(DEFAULT_CONTRACT_CODE)
                )
            )
            .andExpect(
                jsonPath("$.[*].vendorName").value(hasItem(DEFAULT_VENDOR_NAME))
            )
            .andExpect(jsonPath("$.[*].contNo").value(hasItem(DEFAULT_CONT_NO)))
            .andExpect(
                jsonPath("$.[*].entryDate").value(
                    hasItem(DEFAULT_ENTRY_DATE.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].numberOfPo").value(
                    hasItem(DEFAULT_NUMBER_OF_PO)
                )
            )
            .andExpect(
                jsonPath("$.[*].numberOfItem").value(
                    hasItem(DEFAULT_NUMBER_OF_ITEM)
                )
            )
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(
                jsonPath("$.[*].createdAt").value(
                    hasItem(DEFAULT_CREATED_AT.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY))
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
        restDeliveryNotificationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDeliveryNotificationShouldNotBeFound(String filter)
        throws Exception {
        restDeliveryNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDeliveryNotificationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDeliveryNotification() throws Exception {
        // Get the deliveryNotification
        restDeliveryNotificationMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDeliveryNotification() throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the deliveryNotification
        DeliveryNotification updatedDeliveryNotification =
            deliveryNotificationRepository
                .findById(deliveryNotification.getId())
                .orElseThrow();
        // Disconnect from session so that the updates on updatedDeliveryNotification are not directly saved in db
        em.detach(updatedDeliveryNotification);
        updatedDeliveryNotification
            .deliveryNotificationCode(UPDATED_DELIVERY_NOTIFICATION_CODE)
            .invoiceNumber(UPDATED_INVOICE_NUMBER)
            .contractCode(UPDATED_CONTRACT_CODE)
            .vendorName(UPDATED_VENDOR_NAME)
            .contNo(UPDATED_CONT_NO)
            .entryDate(UPDATED_ENTRY_DATE)
            .numberOfPo(UPDATED_NUMBER_OF_PO)
            .numberOfItem(UPDATED_NUMBER_OF_ITEM)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        DeliveryNotificationDTO deliveryNotificationDTO =
            deliveryNotificationMapper.toDto(updatedDeliveryNotification);

        restDeliveryNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, deliveryNotificationDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deliveryNotificationDTO))
            )
            .andExpect(status().isOk());

        // Validate the DeliveryNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDeliveryNotificationToMatchAllProperties(
            updatedDeliveryNotification
        );
    }

    @Test
    @Transactional
    void putNonExistingDeliveryNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deliveryNotification.setId(longCount.incrementAndGet());

        // Create the DeliveryNotification
        DeliveryNotificationDTO deliveryNotificationDTO =
            deliveryNotificationMapper.toDto(deliveryNotification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeliveryNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, deliveryNotificationDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deliveryNotificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DeliveryNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDeliveryNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deliveryNotification.setId(longCount.incrementAndGet());

        // Create the DeliveryNotification
        DeliveryNotificationDTO deliveryNotificationDTO =
            deliveryNotificationMapper.toDto(deliveryNotification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deliveryNotificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DeliveryNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDeliveryNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deliveryNotification.setId(longCount.incrementAndGet());

        // Create the DeliveryNotification
        DeliveryNotificationDTO deliveryNotificationDTO =
            deliveryNotificationMapper.toDto(deliveryNotification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryNotificationMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deliveryNotificationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DeliveryNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDeliveryNotificationWithPatch() throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the deliveryNotification using partial update
        DeliveryNotification partialUpdatedDeliveryNotification =
            new DeliveryNotification();
        partialUpdatedDeliveryNotification.setId(deliveryNotification.getId());

        partialUpdatedDeliveryNotification
            .deliveryNotificationCode(UPDATED_DELIVERY_NOTIFICATION_CODE)
            .invoiceNumber(UPDATED_INVOICE_NUMBER)
            .vendorName(UPDATED_VENDOR_NAME)
            .deletedBy(UPDATED_DELETED_BY);

        restDeliveryNotificationMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    partialUpdatedDeliveryNotification.getId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(partialUpdatedDeliveryNotification)
                    )
            )
            .andExpect(status().isOk());

        // Validate the DeliveryNotification in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDeliveryNotificationUpdatableFieldsEquals(
            createUpdateProxyForBean(
                partialUpdatedDeliveryNotification,
                deliveryNotification
            ),
            getPersistedDeliveryNotification(deliveryNotification)
        );
    }

    @Test
    @Transactional
    void fullUpdateDeliveryNotificationWithPatch() throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the deliveryNotification using partial update
        DeliveryNotification partialUpdatedDeliveryNotification =
            new DeliveryNotification();
        partialUpdatedDeliveryNotification.setId(deliveryNotification.getId());

        partialUpdatedDeliveryNotification
            .deliveryNotificationCode(UPDATED_DELIVERY_NOTIFICATION_CODE)
            .invoiceNumber(UPDATED_INVOICE_NUMBER)
            .contractCode(UPDATED_CONTRACT_CODE)
            .vendorName(UPDATED_VENDOR_NAME)
            .contNo(UPDATED_CONT_NO)
            .entryDate(UPDATED_ENTRY_DATE)
            .numberOfPo(UPDATED_NUMBER_OF_PO)
            .numberOfItem(UPDATED_NUMBER_OF_ITEM)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restDeliveryNotificationMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    partialUpdatedDeliveryNotification.getId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(partialUpdatedDeliveryNotification)
                    )
            )
            .andExpect(status().isOk());

        // Validate the DeliveryNotification in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDeliveryNotificationUpdatableFieldsEquals(
            partialUpdatedDeliveryNotification,
            getPersistedDeliveryNotification(partialUpdatedDeliveryNotification)
        );
    }

    @Test
    @Transactional
    void patchNonExistingDeliveryNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deliveryNotification.setId(longCount.incrementAndGet());

        // Create the DeliveryNotification
        DeliveryNotificationDTO deliveryNotificationDTO =
            deliveryNotificationMapper.toDto(deliveryNotification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeliveryNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, deliveryNotificationDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(deliveryNotificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DeliveryNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDeliveryNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deliveryNotification.setId(longCount.incrementAndGet());

        // Create the DeliveryNotification
        DeliveryNotificationDTO deliveryNotificationDTO =
            deliveryNotificationMapper.toDto(deliveryNotification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(deliveryNotificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DeliveryNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDeliveryNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deliveryNotification.setId(longCount.incrementAndGet());

        // Create the DeliveryNotification
        DeliveryNotificationDTO deliveryNotificationDTO =
            deliveryNotificationMapper.toDto(deliveryNotification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(deliveryNotificationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DeliveryNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDeliveryNotification() throws Exception {
        // Initialize the database
        insertedDeliveryNotification =
            deliveryNotificationRepository.saveAndFlush(deliveryNotification);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the deliveryNotification
        restDeliveryNotificationMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, deliveryNotification.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return deliveryNotificationRepository.count();
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

    protected DeliveryNotification getPersistedDeliveryNotification(
        DeliveryNotification deliveryNotification
    ) {
        return deliveryNotificationRepository
            .findById(deliveryNotification.getId())
            .orElseThrow();
    }

    protected void assertPersistedDeliveryNotificationToMatchAllProperties(
        DeliveryNotification expectedDeliveryNotification
    ) {
        assertDeliveryNotificationAllPropertiesEquals(
            expectedDeliveryNotification,
            getPersistedDeliveryNotification(expectedDeliveryNotification)
        );
    }

    protected void assertPersistedDeliveryNotificationToMatchUpdatableProperties(
        DeliveryNotification expectedDeliveryNotification
    ) {
        assertDeliveryNotificationAllUpdatablePropertiesEquals(
            expectedDeliveryNotification,
            getPersistedDeliveryNotification(expectedDeliveryNotification)
        );
    }
}
