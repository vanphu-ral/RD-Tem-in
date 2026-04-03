package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.InboundWMSPalletAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.InboundWMSPallet;
import com.mycompany.myapp.repository.partner3.InboundWMSPalletRepository;
import com.mycompany.myapp.service.dto.InboundWMSPalletDTO;
import com.mycompany.myapp.service.mapper.InboundWMSPalletMapper;
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
 * Integration tests for the {@link InboundWMSPalletResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InboundWMSPalletResourceIT {

    private static final Integer DEFAULT_INBOUND_WMS_SESSION_ID = 1;
    private static final Integer UPDATED_INBOUND_WMS_SESSION_ID = 2;

    private static final Integer DEFAULT_WAREHOUSE_NOTE_INFO_ID = 1;
    private static final Integer UPDATED_WAREHOUSE_NOTE_INFO_ID = 2;

    private static final String DEFAULT_SERIAL_PALLET = "AAAAAAAAAA";
    private static final String UPDATED_SERIAL_PALLET = "BBBBBBBBBB";

    private static final String DEFAULT_WMS_SEND_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_WMS_SEND_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(
        ZoneId.systemDefault()
    ).withNano(0);

    private static final String ENTITY_API_URL = "/api/inbound-wms-pallets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InboundWMSPalletRepository inboundWMSPalletRepository;

    @Autowired
    private InboundWMSPalletMapper inboundWMSPalletMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInboundWMSPalletMockMvc;

    private InboundWMSPallet inboundWMSPallet;

    private InboundWMSPallet insertedInboundWMSPallet;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InboundWMSPallet createEntity() {
        return new InboundWMSPallet()
            .inboundWMSSessionId(DEFAULT_INBOUND_WMS_SESSION_ID)
            .warehouseNoteInfoId(DEFAULT_WAREHOUSE_NOTE_INFO_ID)
            .serialPallet(DEFAULT_SERIAL_PALLET)
            .wmsSendStatus(DEFAULT_WMS_SEND_STATUS)
            .createdBy(DEFAULT_CREATED_BY)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InboundWMSPallet createUpdatedEntity() {
        return new InboundWMSPallet()
            .inboundWMSSessionId(UPDATED_INBOUND_WMS_SESSION_ID)
            .warehouseNoteInfoId(UPDATED_WAREHOUSE_NOTE_INFO_ID)
            .serialPallet(UPDATED_SERIAL_PALLET)
            .wmsSendStatus(UPDATED_WMS_SEND_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        inboundWMSPallet = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedInboundWMSPallet != null) {
            inboundWMSPalletRepository.delete(insertedInboundWMSPallet);
            insertedInboundWMSPallet = null;
        }
    }

    @Test
    @Transactional
    void createInboundWMSPallet() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the InboundWMSPallet
        InboundWMSPalletDTO inboundWMSPalletDTO = inboundWMSPalletMapper.toDto(
            inboundWMSPallet
        );
        var returnedInboundWMSPalletDTO = om.readValue(
            restInboundWMSPalletMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(inboundWMSPalletDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InboundWMSPalletDTO.class
        );

        // Validate the InboundWMSPallet in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInboundWMSPallet = inboundWMSPalletMapper.toEntity(
            returnedInboundWMSPalletDTO
        );
        assertInboundWMSPalletUpdatableFieldsEquals(
            returnedInboundWMSPallet,
            getPersistedInboundWMSPallet(returnedInboundWMSPallet)
        );

        insertedInboundWMSPallet = returnedInboundWMSPallet;
    }

    @Test
    @Transactional
    void createInboundWMSPalletWithExistingId() throws Exception {
        // Create the InboundWMSPallet with an existing ID
        inboundWMSPallet.setId(1L);
        InboundWMSPalletDTO inboundWMSPalletDTO = inboundWMSPalletMapper.toDto(
            inboundWMSPallet
        );

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInboundWMSPalletMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inboundWMSPalletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InboundWMSPallet in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllInboundWMSPallets() throws Exception {
        // Initialize the database
        insertedInboundWMSPallet = inboundWMSPalletRepository.saveAndFlush(
            inboundWMSPallet
        );

        // Get all the inboundWMSPalletList
        restInboundWMSPalletMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(inboundWMSPallet.getId().intValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].inboundWMSSessionId").value(
                    hasItem(DEFAULT_INBOUND_WMS_SESSION_ID)
                )
            )
            .andExpect(
                jsonPath("$.[*].warehouseNoteInfoId").value(
                    hasItem(DEFAULT_WAREHOUSE_NOTE_INFO_ID)
                )
            )
            .andExpect(
                jsonPath("$.[*].serialPallet").value(
                    hasItem(DEFAULT_SERIAL_PALLET)
                )
            )
            .andExpect(
                jsonPath("$.[*].wmsSendStatus").value(
                    hasItem(DEFAULT_WMS_SEND_STATUS)
                )
            )
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
    void getInboundWMSPallet() throws Exception {
        // Initialize the database
        insertedInboundWMSPallet = inboundWMSPalletRepository.saveAndFlush(
            inboundWMSPallet
        );

        // Get the inboundWMSPallet
        restInboundWMSPalletMockMvc
            .perform(get(ENTITY_API_URL_ID, inboundWMSPallet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.id").value(inboundWMSPallet.getId().intValue())
            )
            .andExpect(
                jsonPath("$.inboundWMSSessionId").value(
                    DEFAULT_INBOUND_WMS_SESSION_ID
                )
            )
            .andExpect(
                jsonPath("$.warehouseNoteInfoId").value(
                    DEFAULT_WAREHOUSE_NOTE_INFO_ID
                )
            )
            .andExpect(jsonPath("$.serialPallet").value(DEFAULT_SERIAL_PALLET))
            .andExpect(
                jsonPath("$.wmsSendStatus").value(DEFAULT_WMS_SEND_STATUS)
            )
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(
                jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT))
            );
    }

    @Test
    @Transactional
    void getNonExistingInboundWMSPallet() throws Exception {
        // Get the inboundWMSPallet
        restInboundWMSPalletMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInboundWMSPallet() throws Exception {
        // Initialize the database
        insertedInboundWMSPallet = inboundWMSPalletRepository.saveAndFlush(
            inboundWMSPallet
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inboundWMSPallet
        InboundWMSPallet updatedInboundWMSPallet = inboundWMSPalletRepository
            .findById(inboundWMSPallet.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedInboundWMSPallet are not directly saved in db
        em.detach(updatedInboundWMSPallet);
        updatedInboundWMSPallet
            .inboundWMSSessionId(UPDATED_INBOUND_WMS_SESSION_ID)
            .warehouseNoteInfoId(UPDATED_WAREHOUSE_NOTE_INFO_ID)
            .serialPallet(UPDATED_SERIAL_PALLET)
            .wmsSendStatus(UPDATED_WMS_SEND_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT);
        InboundWMSPalletDTO inboundWMSPalletDTO = inboundWMSPalletMapper.toDto(
            updatedInboundWMSPallet
        );

        restInboundWMSPalletMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inboundWMSPalletDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inboundWMSPalletDTO))
            )
            .andExpect(status().isOk());

        // Validate the InboundWMSPallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInboundWMSPalletToMatchAllProperties(
            updatedInboundWMSPallet
        );
    }

    @Test
    @Transactional
    void putNonExistingInboundWMSPallet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inboundWMSPallet.setId(longCount.incrementAndGet());

        // Create the InboundWMSPallet
        InboundWMSPalletDTO inboundWMSPalletDTO = inboundWMSPalletMapper.toDto(
            inboundWMSPallet
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInboundWMSPalletMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inboundWMSPalletDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inboundWMSPalletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InboundWMSPallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInboundWMSPallet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inboundWMSPallet.setId(longCount.incrementAndGet());

        // Create the InboundWMSPallet
        InboundWMSPalletDTO inboundWMSPalletDTO = inboundWMSPalletMapper.toDto(
            inboundWMSPallet
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInboundWMSPalletMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inboundWMSPalletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InboundWMSPallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInboundWMSPallet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inboundWMSPallet.setId(longCount.incrementAndGet());

        // Create the InboundWMSPallet
        InboundWMSPalletDTO inboundWMSPalletDTO = inboundWMSPalletMapper.toDto(
            inboundWMSPallet
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInboundWMSPalletMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inboundWMSPalletDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InboundWMSPallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInboundWMSPalletWithPatch() throws Exception {
        // Initialize the database
        insertedInboundWMSPallet = inboundWMSPalletRepository.saveAndFlush(
            inboundWMSPallet
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inboundWMSPallet using partial update
        InboundWMSPallet partialUpdatedInboundWMSPallet =
            new InboundWMSPallet();
        partialUpdatedInboundWMSPallet.setId(inboundWMSPallet.getId());

        partialUpdatedInboundWMSPallet
            .warehouseNoteInfoId(UPDATED_WAREHOUSE_NOTE_INFO_ID)
            .serialPallet(UPDATED_SERIAL_PALLET)
            .wmsSendStatus(UPDATED_WMS_SEND_STATUS)
            .createdBy(UPDATED_CREATED_BY);

        restInboundWMSPalletMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInboundWMSPallet.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(partialUpdatedInboundWMSPallet)
                    )
            )
            .andExpect(status().isOk());

        // Validate the InboundWMSPallet in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInboundWMSPalletUpdatableFieldsEquals(
            createUpdateProxyForBean(
                partialUpdatedInboundWMSPallet,
                inboundWMSPallet
            ),
            getPersistedInboundWMSPallet(inboundWMSPallet)
        );
    }

    @Test
    @Transactional
    void fullUpdateInboundWMSPalletWithPatch() throws Exception {
        // Initialize the database
        insertedInboundWMSPallet = inboundWMSPalletRepository.saveAndFlush(
            inboundWMSPallet
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inboundWMSPallet using partial update
        InboundWMSPallet partialUpdatedInboundWMSPallet =
            new InboundWMSPallet();
        partialUpdatedInboundWMSPallet.setId(inboundWMSPallet.getId());

        partialUpdatedInboundWMSPallet
            .inboundWMSSessionId(UPDATED_INBOUND_WMS_SESSION_ID)
            .warehouseNoteInfoId(UPDATED_WAREHOUSE_NOTE_INFO_ID)
            .serialPallet(UPDATED_SERIAL_PALLET)
            .wmsSendStatus(UPDATED_WMS_SEND_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT);

        restInboundWMSPalletMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInboundWMSPallet.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(
                        om.writeValueAsBytes(partialUpdatedInboundWMSPallet)
                    )
            )
            .andExpect(status().isOk());

        // Validate the InboundWMSPallet in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInboundWMSPalletUpdatableFieldsEquals(
            partialUpdatedInboundWMSPallet,
            getPersistedInboundWMSPallet(partialUpdatedInboundWMSPallet)
        );
    }

    @Test
    @Transactional
    void patchNonExistingInboundWMSPallet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inboundWMSPallet.setId(longCount.incrementAndGet());

        // Create the InboundWMSPallet
        InboundWMSPalletDTO inboundWMSPalletDTO = inboundWMSPalletMapper.toDto(
            inboundWMSPallet
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInboundWMSPalletMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, inboundWMSPalletDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inboundWMSPalletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InboundWMSPallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInboundWMSPallet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inboundWMSPallet.setId(longCount.incrementAndGet());

        // Create the InboundWMSPallet
        InboundWMSPalletDTO inboundWMSPalletDTO = inboundWMSPalletMapper.toDto(
            inboundWMSPallet
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInboundWMSPalletMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inboundWMSPalletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InboundWMSPallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInboundWMSPallet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inboundWMSPallet.setId(longCount.incrementAndGet());

        // Create the InboundWMSPallet
        InboundWMSPalletDTO inboundWMSPalletDTO = inboundWMSPalletMapper.toDto(
            inboundWMSPallet
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInboundWMSPalletMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inboundWMSPalletDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InboundWMSPallet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInboundWMSPallet() throws Exception {
        // Initialize the database
        insertedInboundWMSPallet = inboundWMSPalletRepository.saveAndFlush(
            inboundWMSPallet
        );

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the inboundWMSPallet
        restInboundWMSPalletMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, inboundWMSPallet.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return inboundWMSPalletRepository.count();
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

    protected InboundWMSPallet getPersistedInboundWMSPallet(
        InboundWMSPallet inboundWMSPallet
    ) {
        return inboundWMSPalletRepository
            .findById(inboundWMSPallet.getId())
            .orElseThrow();
    }

    protected void assertPersistedInboundWMSPalletToMatchAllProperties(
        InboundWMSPallet expectedInboundWMSPallet
    ) {
        assertInboundWMSPalletAllPropertiesEquals(
            expectedInboundWMSPallet,
            getPersistedInboundWMSPallet(expectedInboundWMSPallet)
        );
    }

    protected void assertPersistedInboundWMSPalletToMatchUpdatableProperties(
        InboundWMSPallet expectedInboundWMSPallet
    ) {
        assertInboundWMSPalletAllUpdatablePropertiesEquals(
            expectedInboundWMSPallet,
            getPersistedInboundWMSPallet(expectedInboundWMSPallet)
        );
    }
}
