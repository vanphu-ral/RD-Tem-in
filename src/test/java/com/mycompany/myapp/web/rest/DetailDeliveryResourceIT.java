package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.DetailDeliveryAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Delivery;
import com.mycompany.myapp.domain.DetailDelivery;
import com.mycompany.myapp.repository.DetailDeliveryRepository;
import com.mycompany.myapp.service.DetailDeliveryService;
import com.mycompany.myapp.service.dto.DetailDeliveryDTO;
import com.mycompany.myapp.service.mapper.DetailDeliveryMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link DetailDeliveryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DetailDeliveryResourceIT {

    private static final UUID DEFAULT_LINE_ID = UUID.randomUUID();
    private static final UUID UPDATED_LINE_ID = UUID.randomUUID();

    private static final String DEFAULT_PO_SMART = "AAAAAAAAAA";
    private static final String UPDATED_PO_SMART = "BBBBBBBBBB";

    private static final String DEFAULT_PO_ERP = "AAAAAAAAAA";
    private static final String UPDATED_PO_ERP = "BBBBBBBBBB";

    private static final String DEFAULT_PR_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PR_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_CONTRACT_NO = "AAAAAAAAAA";
    private static final String UPDATED_CONTRACT_NO = "BBBBBBBBBB";

    private static final String DEFAULT_LINE_ITEM_STT = "AAAAAAAAAA";
    private static final String UPDATED_LINE_ITEM_STT = "BBBBBBBBBB";

    private static final String DEFAULT_PO_LINE_ITEM = "AAAAAAAAAA";
    private static final String UPDATED_PO_LINE_ITEM = "BBBBBBBBBB";

    private static final String DEFAULT_ITEM_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ITEM_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_ITEM_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ITEM_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_WHS_CODE = "AAAAAAAAAA";
    private static final String UPDATED_WHS_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_UNIT = "AAAAAAAAAA";
    private static final String UPDATED_UNIT = "BBBBBBBBBB";

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final String DEFAULT_INVOICE_SMART_NO = "AAAAAAAAAA";
    private static final String UPDATED_INVOICE_SMART_NO = "BBBBBBBBBB";

    private static final String DEFAULT_INVOICE_NO = "AAAAAAAAAA";
    private static final String UPDATED_INVOICE_NO = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_INVOICE_DATE = LocalDate.ofEpochDay(
        0L
    );
    private static final LocalDate UPDATED_INVOICE_DATE = LocalDate.now(
        ZoneId.systemDefault()
    );

    private static final Integer DEFAULT_DELIVERY_TURN = 1;
    private static final Integer UPDATED_DELIVERY_TURN = 2;

    private static final String ENTITY_API_URL = "/api/detail-deliveries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DetailDeliveryRepository detailDeliveryRepository;

    @Mock
    private DetailDeliveryRepository detailDeliveryRepositoryMock;

    @Autowired
    private DetailDeliveryMapper detailDeliveryMapper;

    @Mock
    private DetailDeliveryService detailDeliveryServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDetailDeliveryMockMvc;

    private DetailDelivery detailDelivery;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DetailDelivery createEntity(EntityManager em) {
        DetailDelivery detailDelivery = new DetailDelivery()
            .lineId(DEFAULT_LINE_ID)
            .poSmart(DEFAULT_PO_SMART)
            .poErp(DEFAULT_PO_ERP)
            .prNumber(DEFAULT_PR_NUMBER)
            .contractNo(DEFAULT_CONTRACT_NO)
            .lineItemStt(DEFAULT_LINE_ITEM_STT)
            .poLineItem(DEFAULT_PO_LINE_ITEM)
            .itemCode(DEFAULT_ITEM_CODE)
            .itemName(DEFAULT_ITEM_NAME)
            .whsCode(DEFAULT_WHS_CODE)
            .unit(DEFAULT_UNIT)
            .quantity(DEFAULT_QUANTITY)
            .invoiceSmartNo(DEFAULT_INVOICE_SMART_NO)
            .invoiceNo(DEFAULT_INVOICE_NO)
            .invoiceDate(DEFAULT_INVOICE_DATE)
            .deliveryTurn(DEFAULT_DELIVERY_TURN);
        // Add required entity
        Delivery delivery;
        if (TestUtil.findAll(em, Delivery.class).isEmpty()) {
            delivery = DeliveryResourceIT.createEntity(em);
            em.persist(delivery);
            em.flush();
        } else {
            delivery = TestUtil.findAll(em, Delivery.class).get(0);
        }
        detailDelivery.setDelivery(delivery);
        return detailDelivery;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DetailDelivery createUpdatedEntity(EntityManager em) {
        DetailDelivery detailDelivery = new DetailDelivery()
            .lineId(UPDATED_LINE_ID)
            .poSmart(UPDATED_PO_SMART)
            .poErp(UPDATED_PO_ERP)
            .prNumber(UPDATED_PR_NUMBER)
            .contractNo(UPDATED_CONTRACT_NO)
            .lineItemStt(UPDATED_LINE_ITEM_STT)
            .poLineItem(UPDATED_PO_LINE_ITEM)
            .itemCode(UPDATED_ITEM_CODE)
            .itemName(UPDATED_ITEM_NAME)
            .whsCode(UPDATED_WHS_CODE)
            .unit(UPDATED_UNIT)
            .quantity(UPDATED_QUANTITY)
            .invoiceSmartNo(UPDATED_INVOICE_SMART_NO)
            .invoiceNo(UPDATED_INVOICE_NO)
            .invoiceDate(UPDATED_INVOICE_DATE)
            .deliveryTurn(UPDATED_DELIVERY_TURN);
        // Add required entity
        Delivery delivery;
        if (TestUtil.findAll(em, Delivery.class).isEmpty()) {
            delivery = DeliveryResourceIT.createUpdatedEntity(em);
            em.persist(delivery);
            em.flush();
        } else {
            delivery = TestUtil.findAll(em, Delivery.class).get(0);
        }
        detailDelivery.setDelivery(delivery);
        return detailDelivery;
    }

    @BeforeEach
    public void initTest() {
        detailDelivery = createEntity(em);
    }

    @Test
    @Transactional
    void createDetailDelivery() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DetailDelivery
        DetailDeliveryDTO detailDeliveryDTO = detailDeliveryMapper.toDto(
            detailDelivery
        );
        var returnedDetailDeliveryDTO = om.readValue(
            restDetailDeliveryMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(detailDeliveryDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DetailDeliveryDTO.class
        );

        // Validate the DetailDelivery in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDetailDelivery = detailDeliveryMapper.toEntity(
            returnedDetailDeliveryDTO
        );
        assertDetailDeliveryUpdatableFieldsEquals(
            returnedDetailDelivery,
            getPersistedDetailDelivery(returnedDetailDelivery)
        );
    }

    @Test
    @Transactional
    void createDetailDeliveryWithExistingId() throws Exception {
        // Create and save a detailDelivery first
        DetailDelivery existingDetailDelivery = createEntity(em);
        detailDeliveryRepository.saveAndFlush(existingDetailDelivery);

        // Try to create another detailDelivery with the same lineId
        DetailDelivery detailDeliveryWithSameId = createEntity(em);
        detailDeliveryWithSameId.setLineId(existingDetailDelivery.getLineId());
        DetailDeliveryDTO detailDeliveryDTO = detailDeliveryMapper.toDto(
            detailDeliveryWithSameId
        );

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDetailDeliveryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(detailDeliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DetailDelivery in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLineIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        detailDelivery.setLineId(null);

        // Create the DetailDelivery, which fails.
        DetailDeliveryDTO detailDeliveryDTO = detailDeliveryMapper.toDto(
            detailDelivery
        );

        restDetailDeliveryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(detailDeliveryDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDetailDeliveries() throws Exception {
        // Initialize the database
        detailDeliveryRepository.saveAndFlush(detailDelivery);

        // Get all the detailDeliveryList
        restDetailDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].lineId").value(
                    hasItem(detailDelivery.getLineId().toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].lineId").value(
                    hasItem(DEFAULT_LINE_ID.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].poSmart").value(hasItem(DEFAULT_PO_SMART))
            )
            .andExpect(jsonPath("$.[*].poErp").value(hasItem(DEFAULT_PO_ERP)))
            .andExpect(
                jsonPath("$.[*].prNumber").value(hasItem(DEFAULT_PR_NUMBER))
            )
            .andExpect(
                jsonPath("$.[*].contractNo").value(hasItem(DEFAULT_CONTRACT_NO))
            )
            .andExpect(
                jsonPath("$.[*].lineItemStt").value(
                    hasItem(DEFAULT_LINE_ITEM_STT)
                )
            )
            .andExpect(
                jsonPath("$.[*].poLineItem").value(
                    hasItem(DEFAULT_PO_LINE_ITEM)
                )
            )
            .andExpect(
                jsonPath("$.[*].itemCode").value(hasItem(DEFAULT_ITEM_CODE))
            )
            .andExpect(
                jsonPath("$.[*].itemName").value(hasItem(DEFAULT_ITEM_NAME))
            )
            .andExpect(
                jsonPath("$.[*].whsCode").value(hasItem(DEFAULT_WHS_CODE))
            )
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(
                jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY))
            )
            .andExpect(
                jsonPath("$.[*].invoiceSmartNo").value(
                    hasItem(DEFAULT_INVOICE_SMART_NO)
                )
            )
            .andExpect(
                jsonPath("$.[*].invoiceNo").value(hasItem(DEFAULT_INVOICE_NO))
            )
            .andExpect(
                jsonPath("$.[*].invoiceDate").value(
                    hasItem(DEFAULT_INVOICE_DATE.toString())
                )
            )
            .andExpect(
                jsonPath("$.[*].deliveryTurn").value(
                    hasItem(DEFAULT_DELIVERY_TURN)
                )
            );
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDetailDeliveriesWithEagerRelationshipsIsEnabled()
        throws Exception {
        when(
            detailDeliveryServiceMock.findAllWithEagerRelationships(any())
        ).thenReturn(new PageImpl(new ArrayList<>()));

        restDetailDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "?eagerload=true"))
            .andExpect(status().isOk());

        verify(
            detailDeliveryServiceMock,
            times(1)
        ).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDetailDeliveriesWithEagerRelationshipsIsNotEnabled()
        throws Exception {
        when(
            detailDeliveryServiceMock.findAllWithEagerRelationships(any())
        ).thenReturn(new PageImpl(new ArrayList<>()));

        restDetailDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "?eagerload=false"))
            .andExpect(status().isOk());
        verify(detailDeliveryRepositoryMock, times(1)).findAll(
            any(Pageable.class)
        );
    }

    @Test
    @Transactional
    void getDetailDelivery() throws Exception {
        // Initialize the database
        detailDeliveryRepository.saveAndFlush(detailDelivery);

        // Get the detailDelivery
        restDetailDeliveryMockMvc
            .perform(get(ENTITY_API_URL_ID, detailDelivery.getLineId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.lineId").value(
                    detailDelivery.getLineId().toString()
                )
            )
            .andExpect(jsonPath("$.lineId").value(DEFAULT_LINE_ID.toString()))
            .andExpect(jsonPath("$.poSmart").value(DEFAULT_PO_SMART))
            .andExpect(jsonPath("$.poErp").value(DEFAULT_PO_ERP))
            .andExpect(jsonPath("$.prNumber").value(DEFAULT_PR_NUMBER))
            .andExpect(jsonPath("$.contractNo").value(DEFAULT_CONTRACT_NO))
            .andExpect(jsonPath("$.lineItemStt").value(DEFAULT_LINE_ITEM_STT))
            .andExpect(jsonPath("$.poLineItem").value(DEFAULT_PO_LINE_ITEM))
            .andExpect(jsonPath("$.itemCode").value(DEFAULT_ITEM_CODE))
            .andExpect(jsonPath("$.itemName").value(DEFAULT_ITEM_NAME))
            .andExpect(jsonPath("$.whsCode").value(DEFAULT_WHS_CODE))
            .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(
                jsonPath("$.invoiceSmartNo").value(DEFAULT_INVOICE_SMART_NO)
            )
            .andExpect(jsonPath("$.invoiceNo").value(DEFAULT_INVOICE_NO))
            .andExpect(
                jsonPath("$.invoiceDate").value(DEFAULT_INVOICE_DATE.toString())
            )
            .andExpect(jsonPath("$.deliveryTurn").value(DEFAULT_DELIVERY_TURN));
    }

    @Test
    @Transactional
    void getNonExistingDetailDelivery() throws Exception {
        // Get the detailDelivery
        restDetailDeliveryMockMvc
            .perform(get(ENTITY_API_URL_ID, UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDetailDelivery() throws Exception {
        // Initialize the database
        detailDeliveryRepository.saveAndFlush(detailDelivery);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the detailDelivery
        DetailDelivery updatedDetailDelivery = detailDeliveryRepository
            .findById(detailDelivery.getLineId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedDetailDelivery are not directly saved in db
        em.detach(updatedDetailDelivery);
        updatedDetailDelivery
            .lineId(UPDATED_LINE_ID)
            .poSmart(UPDATED_PO_SMART)
            .poErp(UPDATED_PO_ERP)
            .prNumber(UPDATED_PR_NUMBER)
            .contractNo(UPDATED_CONTRACT_NO)
            .lineItemStt(UPDATED_LINE_ITEM_STT)
            .poLineItem(UPDATED_PO_LINE_ITEM)
            .itemCode(UPDATED_ITEM_CODE)
            .itemName(UPDATED_ITEM_NAME)
            .whsCode(UPDATED_WHS_CODE)
            .unit(UPDATED_UNIT)
            .quantity(UPDATED_QUANTITY)
            .invoiceSmartNo(UPDATED_INVOICE_SMART_NO)
            .invoiceNo(UPDATED_INVOICE_NO)
            .invoiceDate(UPDATED_INVOICE_DATE)
            .deliveryTurn(UPDATED_DELIVERY_TURN);
        DetailDeliveryDTO detailDeliveryDTO = detailDeliveryMapper.toDto(
            updatedDetailDelivery
        );

        restDetailDeliveryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, detailDeliveryDTO.getLineId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(detailDeliveryDTO))
            )
            .andExpect(status().isOk());

        // Validate the DetailDelivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDetailDeliveryToMatchAllProperties(
            updatedDetailDelivery
        );
    }

    @Test
    @Transactional
    void putNonExistingDetailDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        detailDelivery.setLineId(UUID.randomUUID());

        // Create the DetailDelivery
        DetailDeliveryDTO detailDeliveryDTO = detailDeliveryMapper.toDto(
            detailDelivery
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDetailDeliveryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, detailDeliveryDTO.getLineId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(detailDeliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DetailDelivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDetailDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        detailDelivery.setLineId(UUID.randomUUID());

        // Create the DetailDelivery
        DetailDeliveryDTO detailDeliveryDTO = detailDeliveryMapper.toDto(
            detailDelivery
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDetailDeliveryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(detailDeliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DetailDelivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDetailDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        detailDelivery.setLineId(UUID.randomUUID());

        // Create the DetailDelivery
        DetailDeliveryDTO detailDeliveryDTO = detailDeliveryMapper.toDto(
            detailDelivery
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDetailDeliveryMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(detailDeliveryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DetailDelivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDetailDeliveryWithPatch() throws Exception {
        // Initialize the database
        detailDeliveryRepository.saveAndFlush(detailDelivery);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the detailDelivery using partial update
        DetailDelivery partialUpdatedDetailDelivery = new DetailDelivery();
        partialUpdatedDetailDelivery.setLineId(detailDelivery.getLineId());

        partialUpdatedDetailDelivery
            .lineId(UPDATED_LINE_ID)
            .poErp(UPDATED_PO_ERP)
            .contractNo(UPDATED_CONTRACT_NO)
            .lineItemStt(UPDATED_LINE_ITEM_STT)
            .poLineItem(UPDATED_PO_LINE_ITEM)
            .itemCode(UPDATED_ITEM_CODE)
            .itemName(UPDATED_ITEM_NAME)
            .unit(UPDATED_UNIT)
            .deliveryTurn(UPDATED_DELIVERY_TURN);

        restDetailDeliveryMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    partialUpdatedDetailDelivery.getLineId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDetailDelivery))
            )
            .andExpect(status().isOk());

        // Validate the DetailDelivery in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDetailDeliveryUpdatableFieldsEquals(
            createUpdateProxyForBean(
                partialUpdatedDetailDelivery,
                detailDelivery
            ),
            getPersistedDetailDelivery(detailDelivery)
        );
    }

    @Test
    @Transactional
    void fullUpdateDetailDeliveryWithPatch() throws Exception {
        // Initialize the database
        detailDeliveryRepository.saveAndFlush(detailDelivery);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the detailDelivery using partial update
        DetailDelivery partialUpdatedDetailDelivery = new DetailDelivery();
        partialUpdatedDetailDelivery.setLineId(detailDelivery.getLineId());

        partialUpdatedDetailDelivery
            .lineId(UPDATED_LINE_ID)
            .poSmart(UPDATED_PO_SMART)
            .poErp(UPDATED_PO_ERP)
            .prNumber(UPDATED_PR_NUMBER)
            .contractNo(UPDATED_CONTRACT_NO)
            .lineItemStt(UPDATED_LINE_ITEM_STT)
            .poLineItem(UPDATED_PO_LINE_ITEM)
            .itemCode(UPDATED_ITEM_CODE)
            .itemName(UPDATED_ITEM_NAME)
            .whsCode(UPDATED_WHS_CODE)
            .unit(UPDATED_UNIT)
            .quantity(UPDATED_QUANTITY)
            .invoiceSmartNo(UPDATED_INVOICE_SMART_NO)
            .invoiceNo(UPDATED_INVOICE_NO)
            .invoiceDate(UPDATED_INVOICE_DATE)
            .deliveryTurn(UPDATED_DELIVERY_TURN);

        restDetailDeliveryMockMvc
            .perform(
                patch(
                    ENTITY_API_URL_ID,
                    partialUpdatedDetailDelivery.getLineId()
                )
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDetailDelivery))
            )
            .andExpect(status().isOk());

        // Validate the DetailDelivery in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDetailDeliveryUpdatableFieldsEquals(
            partialUpdatedDetailDelivery,
            getPersistedDetailDelivery(partialUpdatedDetailDelivery)
        );
    }

    @Test
    @Transactional
    void patchNonExistingDetailDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        detailDelivery.setLineId(UUID.randomUUID());

        // Create the DetailDelivery
        DetailDeliveryDTO detailDeliveryDTO = detailDeliveryMapper.toDto(
            detailDelivery
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDetailDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, detailDeliveryDTO.getLineId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(detailDeliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DetailDelivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDetailDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        detailDelivery.setLineId(UUID.randomUUID());

        // Create the DetailDelivery
        DetailDeliveryDTO detailDeliveryDTO = detailDeliveryMapper.toDto(
            detailDelivery
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDetailDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(detailDeliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DetailDelivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDetailDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        detailDelivery.setLineId(UUID.randomUUID());

        // Create the DetailDelivery
        DetailDeliveryDTO detailDeliveryDTO = detailDeliveryMapper.toDto(
            detailDelivery
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDetailDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(detailDeliveryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DetailDelivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDetailDelivery() throws Exception {
        // Initialize the database
        detailDeliveryRepository.saveAndFlush(detailDelivery);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the detailDelivery
        restDetailDeliveryMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, detailDelivery.getLineId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return detailDeliveryRepository.count();
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

    protected DetailDelivery getPersistedDetailDelivery(
        DetailDelivery detailDelivery
    ) {
        return detailDeliveryRepository
            .findById(detailDelivery.getLineId())
            .orElseThrow();
    }

    protected void assertPersistedDetailDeliveryToMatchAllProperties(
        DetailDelivery expectedDetailDelivery
    ) {
        assertDetailDeliveryAllPropertiesEquals(
            expectedDetailDelivery,
            getPersistedDetailDelivery(expectedDetailDelivery)
        );
    }

    protected void assertPersistedDetailDeliveryToMatchUpdatableProperties(
        DetailDelivery expectedDetailDelivery
    ) {
        assertDetailDeliveryAllUpdatablePropertiesEquals(
            expectedDetailDelivery,
            getPersistedDetailDelivery(expectedDetailDelivery)
        );
    }
}
