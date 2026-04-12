package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.InboundWMSBoxAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.InboundWMSBox;
import com.mycompany.myapp.repository.partner3.InboundWMSBoxRepository;
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
 * Integration tests for the {@link InboundWMSBoxResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InboundWMSBoxResourceIT {

    private static final Integer DEFAULT_WAREHOUSE_NOTE_INFO_ID = 1;
    private static final Integer UPDATED_WAREHOUSE_NOTE_INFO_ID = 2;

    private static final String DEFAULT_SERIAL_BOX = "AAAAAAAAAA";
    private static final String UPDATED_SERIAL_BOX = "BBBBBBBBBB";

    private static final Boolean DEFAULT_WMS_SEND_STATUS = false;
    private static final Boolean UPDATED_WMS_SEND_STATUS = true;

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(
        ZoneId.systemDefault()
    ).withNano(0);

    private static final String ENTITY_API_URL = "/api/inbound-wms-boxes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InboundWMSBoxRepository inboundWMSBoxRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInboundWMSBoxMockMvc;

    private InboundWMSBox inboundWMSBox;

    private InboundWMSBox insertedInboundWMSBox;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InboundWMSBox createEntity() {
        return new InboundWMSBox()
            .warehouseNoteInfoId(DEFAULT_WAREHOUSE_NOTE_INFO_ID)
            .serialBox(DEFAULT_SERIAL_BOX)
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
    public static InboundWMSBox createUpdatedEntity() {
        return new InboundWMSBox()
            .warehouseNoteInfoId(UPDATED_WAREHOUSE_NOTE_INFO_ID)
            .serialBox(UPDATED_SERIAL_BOX)
            .wmsSendStatus(UPDATED_WMS_SEND_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        inboundWMSBox = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedInboundWMSBox != null) {
            inboundWMSBoxRepository.delete(insertedInboundWMSBox);
            insertedInboundWMSBox = null;
        }
    }

    @Test
    @Transactional
    void createInboundWMSBox() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the InboundWMSBox
        var returnedInboundWMSBox = om.readValue(
            restInboundWMSBoxMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(inboundWMSBox))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InboundWMSBox.class
        );

        // Validate the InboundWMSBox in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertInboundWMSBoxUpdatableFieldsEquals(
            returnedInboundWMSBox,
            getPersistedInboundWMSBox(returnedInboundWMSBox)
        );

        insertedInboundWMSBox = returnedInboundWMSBox;
    }

    @Test
    @Transactional
    void createInboundWMSBoxWithExistingId() throws Exception {
        // Create the InboundWMSBox with an existing ID
        inboundWMSBox.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInboundWMSBoxMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inboundWMSBox))
            )
            .andExpect(status().isBadRequest());

        // Validate the InboundWMSBox in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllInboundWMSBoxes() throws Exception {
        // Initialize the database
        insertedInboundWMSBox = inboundWMSBoxRepository.saveAndFlush(
            inboundWMSBox
        );

        // Get all the inboundWMSBoxList
        restInboundWMSBoxMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(
                    hasItem(inboundWMSBox.getId().intValue())
                )
            )
            .andExpect(
                jsonPath("$.[*].warehouseNoteInfoId").value(
                    hasItem(DEFAULT_WAREHOUSE_NOTE_INFO_ID)
                )
            )
            .andExpect(
                jsonPath("$.[*].serialBox").value(hasItem(DEFAULT_SERIAL_BOX))
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
    void getInboundWMSBox() throws Exception {
        // Initialize the database
        insertedInboundWMSBox = inboundWMSBoxRepository.saveAndFlush(
            inboundWMSBox
        );

        // Get the inboundWMSBox
        restInboundWMSBoxMockMvc
            .perform(get(ENTITY_API_URL_ID, inboundWMSBox.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(inboundWMSBox.getId().intValue()))
            .andExpect(
                jsonPath("$.warehouseNoteInfoId").value(
                    DEFAULT_WAREHOUSE_NOTE_INFO_ID
                )
            )
            .andExpect(jsonPath("$.serialBox").value(DEFAULT_SERIAL_BOX))
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
    void getNonExistingInboundWMSBox() throws Exception {
        // Get the inboundWMSBox
        restInboundWMSBoxMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInboundWMSBox() throws Exception {
        // Initialize the database
        insertedInboundWMSBox = inboundWMSBoxRepository.saveAndFlush(
            inboundWMSBox
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inboundWMSBox
        InboundWMSBox updatedInboundWMSBox = inboundWMSBoxRepository
            .findById(inboundWMSBox.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedInboundWMSBox are not directly saved in db
        em.detach(updatedInboundWMSBox);
        updatedInboundWMSBox
            .warehouseNoteInfoId(UPDATED_WAREHOUSE_NOTE_INFO_ID)
            .serialBox(UPDATED_SERIAL_BOX)
            .wmsSendStatus(UPDATED_WMS_SEND_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT);

        restInboundWMSBoxMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedInboundWMSBox.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedInboundWMSBox))
            )
            .andExpect(status().isOk());

        // Validate the InboundWMSBox in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInboundWMSBoxToMatchAllProperties(updatedInboundWMSBox);
    }

    @Test
    @Transactional
    void putNonExistingInboundWMSBox() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inboundWMSBox.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInboundWMSBoxMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inboundWMSBox.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inboundWMSBox))
            )
            .andExpect(status().isBadRequest());

        // Validate the InboundWMSBox in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInboundWMSBox() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inboundWMSBox.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInboundWMSBoxMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inboundWMSBox))
            )
            .andExpect(status().isBadRequest());

        // Validate the InboundWMSBox in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInboundWMSBox() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inboundWMSBox.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInboundWMSBoxMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inboundWMSBox))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InboundWMSBox in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInboundWMSBoxWithPatch() throws Exception {
        // Initialize the database
        insertedInboundWMSBox = inboundWMSBoxRepository.saveAndFlush(
            inboundWMSBox
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inboundWMSBox using partial update
        InboundWMSBox partialUpdatedInboundWMSBox = new InboundWMSBox();
        partialUpdatedInboundWMSBox.setId(inboundWMSBox.getId());

        partialUpdatedInboundWMSBox
            .serialBox(UPDATED_SERIAL_BOX)
            .wmsSendStatus(UPDATED_WMS_SEND_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT);

        restInboundWMSBoxMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInboundWMSBox.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInboundWMSBox))
            )
            .andExpect(status().isOk());

        // Validate the InboundWMSBox in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInboundWMSBoxUpdatableFieldsEquals(
            createUpdateProxyForBean(
                partialUpdatedInboundWMSBox,
                inboundWMSBox
            ),
            getPersistedInboundWMSBox(inboundWMSBox)
        );
    }

    @Test
    @Transactional
    void fullUpdateInboundWMSBoxWithPatch() throws Exception {
        // Initialize the database
        insertedInboundWMSBox = inboundWMSBoxRepository.saveAndFlush(
            inboundWMSBox
        );

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inboundWMSBox using partial update
        InboundWMSBox partialUpdatedInboundWMSBox = new InboundWMSBox();
        partialUpdatedInboundWMSBox.setId(inboundWMSBox.getId());

        partialUpdatedInboundWMSBox
            .warehouseNoteInfoId(UPDATED_WAREHOUSE_NOTE_INFO_ID)
            .serialBox(UPDATED_SERIAL_BOX)
            .wmsSendStatus(UPDATED_WMS_SEND_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT);

        restInboundWMSBoxMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInboundWMSBox.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInboundWMSBox))
            )
            .andExpect(status().isOk());

        // Validate the InboundWMSBox in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInboundWMSBoxUpdatableFieldsEquals(
            partialUpdatedInboundWMSBox,
            getPersistedInboundWMSBox(partialUpdatedInboundWMSBox)
        );
    }

    @Test
    @Transactional
    void patchNonExistingInboundWMSBox() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inboundWMSBox.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInboundWMSBoxMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, inboundWMSBox.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inboundWMSBox))
            )
            .andExpect(status().isBadRequest());

        // Validate the InboundWMSBox in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInboundWMSBox() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inboundWMSBox.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInboundWMSBoxMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inboundWMSBox))
            )
            .andExpect(status().isBadRequest());

        // Validate the InboundWMSBox in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInboundWMSBox() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inboundWMSBox.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInboundWMSBoxMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inboundWMSBox))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InboundWMSBox in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInboundWMSBox() throws Exception {
        // Initialize the database
        insertedInboundWMSBox = inboundWMSBoxRepository.saveAndFlush(
            inboundWMSBox
        );

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the inboundWMSBox
        restInboundWMSBoxMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, inboundWMSBox.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return inboundWMSBoxRepository.count();
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

    protected InboundWMSBox getPersistedInboundWMSBox(
        InboundWMSBox inboundWMSBox
    ) {
        return inboundWMSBoxRepository
            .findById(inboundWMSBox.getId())
            .orElseThrow();
    }

    protected void assertPersistedInboundWMSBoxToMatchAllProperties(
        InboundWMSBox expectedInboundWMSBox
    ) {
        assertInboundWMSBoxAllPropertiesEquals(
            expectedInboundWMSBox,
            getPersistedInboundWMSBox(expectedInboundWMSBox)
        );
    }

    protected void assertPersistedInboundWMSBoxToMatchUpdatableProperties(
        InboundWMSBox expectedInboundWMSBox
    ) {
        assertInboundWMSBoxAllUpdatablePropertiesEquals(
            expectedInboundWMSBox,
            getPersistedInboundWMSBox(expectedInboundWMSBox)
        );
    }
}
