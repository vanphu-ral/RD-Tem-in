package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.PalletMngtAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.PalletMngt;
import com.mycompany.myapp.repository.PalletMngtRepository;
import com.mycompany.myapp.service.dto.PalletMngtDTO;
import com.mycompany.myapp.service.mapper.PalletMngtMapper;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
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
 * Integration tests for the {@link PalletMngtResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PalletMngtResourceIT {

  private static final String DEFAULT_SERIAL_PALLET = "AAAAAAAAAA";
  private static final String UPDATED_SERIAL_PALLET = "BBBBBBBBBB";

  private static final String DEFAULT_LOCATION_NAME = "AAAAAAAAAA";
  private static final String UPDATED_LOCATION_NAME = "BBBBBBBBBB";

  private static final Integer DEFAULT_NUMBER_OF_BOX = 1;
  private static final Integer UPDATED_NUMBER_OF_BOX = 2;

  private static final Integer DEFAULT_TOTAL_QUANTITY = 1;
  private static final Integer UPDATED_TOTAL_QUANTITY = 2;

  private static final String DEFAULT_STATUS = "AAAAAAAAAA";
  private static final String UPDATED_STATUS = "BBBBBBBBBB";

  private static final String DEFAULT_NOTE = "AAAAAAAAAA";
  private static final String UPDATED_NOTE = "BBBBBBBBBB";

  private static final Instant DEFAULT_CREATE_AT = Instant.ofEpochMilli(0L);
  private static final Instant UPDATED_CREATE_AT = Instant.now()
    .truncatedTo(ChronoUnit.MILLIS);

  private static final String DEFAULT_CREATE_BY = "AAAAAAAAAA";
  private static final String UPDATED_CREATE_BY = "BBBBBBBBBB";

  private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
  private static final Instant UPDATED_UPDATED_AT = Instant.now()
    .truncatedTo(ChronoUnit.MILLIS);

  private static final String DEFAULT_UPDATED_BY = "AAAAAAAAAA";
  private static final String UPDATED_UPDATED_BY = "BBBBBBBBBB";

  private static final String ENTITY_API_URL = "/api/pallet-mngts";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

  private static Random random = new Random();
  private static AtomicLong longCount = new AtomicLong(
    random.nextInt() + (2 * Integer.MAX_VALUE)
  );

  @Autowired
  private ObjectMapper om;

  @Autowired
  private PalletMngtRepository palletMngtRepository;

  @Autowired
  private PalletMngtMapper palletMngtMapper;

  @Autowired
  private EntityManager em;

  @Autowired
  private MockMvc restPalletMngtMockMvc;

  private PalletMngt palletMngt;

  private PalletMngt insertedPalletMngt;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static PalletMngt createEntity() {
    return new PalletMngt()
      .serialPallet(DEFAULT_SERIAL_PALLET)
      .locationName(DEFAULT_LOCATION_NAME)
      .numberOfBox(DEFAULT_NUMBER_OF_BOX)
      .totalQuantity(DEFAULT_TOTAL_QUANTITY)
      .status(DEFAULT_STATUS)
      .note(DEFAULT_NOTE)
      .createAt(DEFAULT_CREATE_AT)
      .createBy(DEFAULT_CREATE_BY)
      .updatedAt(DEFAULT_UPDATED_AT)
      .updatedBy(DEFAULT_UPDATED_BY);
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static PalletMngt createUpdatedEntity() {
    return new PalletMngt()
      .serialPallet(UPDATED_SERIAL_PALLET)
      .locationName(UPDATED_LOCATION_NAME)
      .numberOfBox(UPDATED_NUMBER_OF_BOX)
      .totalQuantity(UPDATED_TOTAL_QUANTITY)
      .status(UPDATED_STATUS)
      .note(UPDATED_NOTE)
      .createAt(UPDATED_CREATE_AT)
      .createBy(UPDATED_CREATE_BY)
      .updatedAt(UPDATED_UPDATED_AT)
      .updatedBy(UPDATED_UPDATED_BY);
  }

  @BeforeEach
  void initTest() {
    palletMngt = createEntity();
  }

  @AfterEach
  void cleanup() {
    if (insertedPalletMngt != null) {
      palletMngtRepository.delete(insertedPalletMngt);
      insertedPalletMngt = null;
    }
  }

  @Test
  @Transactional
  void createPalletMngt() throws Exception {
    long databaseSizeBeforeCreate = getRepositoryCount();
    // Create the PalletMngt
    PalletMngtDTO palletMngtDTO = palletMngtMapper.toDto(palletMngt);
    var returnedPalletMngtDTO = om.readValue(
      restPalletMngtMockMvc
        .perform(
          post(ENTITY_API_URL)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsBytes(palletMngtDTO))
        )
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString(),
      PalletMngtDTO.class
    );

    // Validate the PalletMngt in the database
    assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
    var returnedPalletMngt = palletMngtMapper.toEntity(returnedPalletMngtDTO);
    assertPalletMngtUpdatableFieldsEquals(
      returnedPalletMngt,
      getPersistedPalletMngt(returnedPalletMngt)
    );

    insertedPalletMngt = returnedPalletMngt;
  }

  @Test
  @Transactional
  void createPalletMngtWithExistingId() throws Exception {
    // Create the PalletMngt with an existing ID
    palletMngt.setId(1L);
    PalletMngtDTO palletMngtDTO = palletMngtMapper.toDto(palletMngt);

    long databaseSizeBeforeCreate = getRepositoryCount();

    // An entity with an existing ID cannot be created, so this API call must fail
    restPalletMngtMockMvc
      .perform(
        post(ENTITY_API_URL)
          .with(csrf())
          .contentType(MediaType.APPLICATION_JSON)
          .content(om.writeValueAsBytes(palletMngtDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the PalletMngt in the database
    assertSameRepositoryCount(databaseSizeBeforeCreate);
  }

  @Test
  @Transactional
  void getAllPalletMngts() throws Exception {
    // Initialize the database
    insertedPalletMngt = palletMngtRepository.saveAndFlush(palletMngt);

    // Get all the palletMngtList
    restPalletMngtMockMvc
      .perform(get(ENTITY_API_URL + "?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(
        jsonPath("$.[*].id").value(hasItem(palletMngt.getId().intValue()))
      )
      .andExpect(
        jsonPath("$.[*].serialPallet").value(hasItem(DEFAULT_SERIAL_PALLET))
      )
      .andExpect(
        jsonPath("$.[*].locationName").value(hasItem(DEFAULT_LOCATION_NAME))
      )
      .andExpect(
        jsonPath("$.[*].numberOfBox").value(hasItem(DEFAULT_NUMBER_OF_BOX))
      )
      .andExpect(
        jsonPath("$.[*].totalQuantity").value(hasItem(DEFAULT_TOTAL_QUANTITY))
      )
      .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
      .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
      .andExpect(
        jsonPath("$.[*].createAt").value(hasItem(DEFAULT_CREATE_AT.toString()))
      )
      .andExpect(jsonPath("$.[*].createBy").value(hasItem(DEFAULT_CREATE_BY)))
      .andExpect(
        jsonPath("$.[*].updatedAt").value(
          hasItem(DEFAULT_UPDATED_AT.toString())
        )
      )
      .andExpect(
        jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY))
      );
  }

  @Test
  @Transactional
  void getPalletMngt() throws Exception {
    // Initialize the database
    insertedPalletMngt = palletMngtRepository.saveAndFlush(palletMngt);

    // Get the palletMngt
    restPalletMngtMockMvc
      .perform(get(ENTITY_API_URL_ID, palletMngt.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").value(palletMngt.getId().intValue()))
      .andExpect(jsonPath("$.serialPallet").value(DEFAULT_SERIAL_PALLET))
      .andExpect(jsonPath("$.locationName").value(DEFAULT_LOCATION_NAME))
      .andExpect(jsonPath("$.numberOfBox").value(DEFAULT_NUMBER_OF_BOX))
      .andExpect(jsonPath("$.totalQuantity").value(DEFAULT_TOTAL_QUANTITY))
      .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
      .andExpect(jsonPath("$.note").value(DEFAULT_NOTE))
      .andExpect(jsonPath("$.createAt").value(DEFAULT_CREATE_AT.toString()))
      .andExpect(jsonPath("$.createBy").value(DEFAULT_CREATE_BY))
      .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
      .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY));
  }

  @Test
  @Transactional
  void getNonExistingPalletMngt() throws Exception {
    // Get the palletMngt
    restPalletMngtMockMvc
      .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
      .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  void putExistingPalletMngt() throws Exception {
    // Initialize the database
    insertedPalletMngt = palletMngtRepository.saveAndFlush(palletMngt);

    long databaseSizeBeforeUpdate = getRepositoryCount();

    // Update the palletMngt
    PalletMngt updatedPalletMngt = palletMngtRepository
      .findById(palletMngt.getId())
      .orElseThrow();
    // Disconnect from session so that the updates on updatedPalletMngt are not directly saved in db
    em.detach(updatedPalletMngt);
    updatedPalletMngt
      .serialPallet(UPDATED_SERIAL_PALLET)
      .locationName(UPDATED_LOCATION_NAME)
      .numberOfBox(UPDATED_NUMBER_OF_BOX)
      .totalQuantity(UPDATED_TOTAL_QUANTITY)
      .status(UPDATED_STATUS)
      .note(UPDATED_NOTE)
      .createAt(UPDATED_CREATE_AT)
      .createBy(UPDATED_CREATE_BY)
      .updatedAt(UPDATED_UPDATED_AT)
      .updatedBy(UPDATED_UPDATED_BY);
    PalletMngtDTO palletMngtDTO = palletMngtMapper.toDto(updatedPalletMngt);

    restPalletMngtMockMvc
      .perform(
        put(ENTITY_API_URL_ID, palletMngtDTO.getId())
          .with(csrf())
          .contentType(MediaType.APPLICATION_JSON)
          .content(om.writeValueAsBytes(palletMngtDTO))
      )
      .andExpect(status().isOk());

    // Validate the PalletMngt in the database
    assertSameRepositoryCount(databaseSizeBeforeUpdate);
    assertPersistedPalletMngtToMatchAllProperties(updatedPalletMngt);
  }

  @Test
  @Transactional
  void putNonExistingPalletMngt() throws Exception {
    long databaseSizeBeforeUpdate = getRepositoryCount();
    palletMngt.setId(longCount.incrementAndGet());

    // Create the PalletMngt
    PalletMngtDTO palletMngtDTO = palletMngtMapper.toDto(palletMngt);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restPalletMngtMockMvc
      .perform(
        put(ENTITY_API_URL_ID, palletMngtDTO.getId())
          .with(csrf())
          .contentType(MediaType.APPLICATION_JSON)
          .content(om.writeValueAsBytes(palletMngtDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the PalletMngt in the database
    assertSameRepositoryCount(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void putWithIdMismatchPalletMngt() throws Exception {
    long databaseSizeBeforeUpdate = getRepositoryCount();
    palletMngt.setId(longCount.incrementAndGet());

    // Create the PalletMngt
    PalletMngtDTO palletMngtDTO = palletMngtMapper.toDto(palletMngt);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPalletMngtMockMvc
      .perform(
        put(ENTITY_API_URL_ID, longCount.incrementAndGet())
          .with(csrf())
          .contentType(MediaType.APPLICATION_JSON)
          .content(om.writeValueAsBytes(palletMngtDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the PalletMngt in the database
    assertSameRepositoryCount(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void putWithMissingIdPathParamPalletMngt() throws Exception {
    long databaseSizeBeforeUpdate = getRepositoryCount();
    palletMngt.setId(longCount.incrementAndGet());

    // Create the PalletMngt
    PalletMngtDTO palletMngtDTO = palletMngtMapper.toDto(palletMngt);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPalletMngtMockMvc
      .perform(
        put(ENTITY_API_URL)
          .with(csrf())
          .contentType(MediaType.APPLICATION_JSON)
          .content(om.writeValueAsBytes(palletMngtDTO))
      )
      .andExpect(status().isMethodNotAllowed());

    // Validate the PalletMngt in the database
    assertSameRepositoryCount(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void partialUpdatePalletMngtWithPatch() throws Exception {
    // Initialize the database
    insertedPalletMngt = palletMngtRepository.saveAndFlush(palletMngt);

    long databaseSizeBeforeUpdate = getRepositoryCount();

    // Update the palletMngt using partial update
    PalletMngt partialUpdatedPalletMngt = new PalletMngt();
    partialUpdatedPalletMngt.setId(palletMngt.getId());

    partialUpdatedPalletMngt
      .note(UPDATED_NOTE)
      .updatedAt(UPDATED_UPDATED_AT)
      .updatedBy(UPDATED_UPDATED_BY);

    restPalletMngtMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedPalletMngt.getId())
          .with(csrf())
          .contentType("application/merge-patch+json")
          .content(om.writeValueAsBytes(partialUpdatedPalletMngt))
      )
      .andExpect(status().isOk());

    // Validate the PalletMngt in the database

    assertSameRepositoryCount(databaseSizeBeforeUpdate);
    assertPalletMngtUpdatableFieldsEquals(
      createUpdateProxyForBean(partialUpdatedPalletMngt, palletMngt),
      getPersistedPalletMngt(palletMngt)
    );
  }

  @Test
  @Transactional
  void fullUpdatePalletMngtWithPatch() throws Exception {
    // Initialize the database
    insertedPalletMngt = palletMngtRepository.saveAndFlush(palletMngt);

    long databaseSizeBeforeUpdate = getRepositoryCount();

    // Update the palletMngt using partial update
    PalletMngt partialUpdatedPalletMngt = new PalletMngt();
    partialUpdatedPalletMngt.setId(palletMngt.getId());

    partialUpdatedPalletMngt
      .serialPallet(UPDATED_SERIAL_PALLET)
      .locationName(UPDATED_LOCATION_NAME)
      .numberOfBox(UPDATED_NUMBER_OF_BOX)
      .totalQuantity(UPDATED_TOTAL_QUANTITY)
      .status(UPDATED_STATUS)
      .note(UPDATED_NOTE)
      .createAt(UPDATED_CREATE_AT)
      .createBy(UPDATED_CREATE_BY)
      .updatedAt(UPDATED_UPDATED_AT)
      .updatedBy(UPDATED_UPDATED_BY);

    restPalletMngtMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedPalletMngt.getId())
          .with(csrf())
          .contentType("application/merge-patch+json")
          .content(om.writeValueAsBytes(partialUpdatedPalletMngt))
      )
      .andExpect(status().isOk());

    // Validate the PalletMngt in the database

    assertSameRepositoryCount(databaseSizeBeforeUpdate);
    assertPalletMngtUpdatableFieldsEquals(
      partialUpdatedPalletMngt,
      getPersistedPalletMngt(partialUpdatedPalletMngt)
    );
  }

  @Test
  @Transactional
  void patchNonExistingPalletMngt() throws Exception {
    long databaseSizeBeforeUpdate = getRepositoryCount();
    palletMngt.setId(longCount.incrementAndGet());

    // Create the PalletMngt
    PalletMngtDTO palletMngtDTO = palletMngtMapper.toDto(palletMngt);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restPalletMngtMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, palletMngtDTO.getId())
          .with(csrf())
          .contentType("application/merge-patch+json")
          .content(om.writeValueAsBytes(palletMngtDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the PalletMngt in the database
    assertSameRepositoryCount(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void patchWithIdMismatchPalletMngt() throws Exception {
    long databaseSizeBeforeUpdate = getRepositoryCount();
    palletMngt.setId(longCount.incrementAndGet());

    // Create the PalletMngt
    PalletMngtDTO palletMngtDTO = palletMngtMapper.toDto(palletMngt);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPalletMngtMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
          .with(csrf())
          .contentType("application/merge-patch+json")
          .content(om.writeValueAsBytes(palletMngtDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the PalletMngt in the database
    assertSameRepositoryCount(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void patchWithMissingIdPathParamPalletMngt() throws Exception {
    long databaseSizeBeforeUpdate = getRepositoryCount();
    palletMngt.setId(longCount.incrementAndGet());

    // Create the PalletMngt
    PalletMngtDTO palletMngtDTO = palletMngtMapper.toDto(palletMngt);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPalletMngtMockMvc
      .perform(
        patch(ENTITY_API_URL)
          .with(csrf())
          .contentType("application/merge-patch+json")
          .content(om.writeValueAsBytes(palletMngtDTO))
      )
      .andExpect(status().isMethodNotAllowed());

    // Validate the PalletMngt in the database
    assertSameRepositoryCount(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void deletePalletMngt() throws Exception {
    // Initialize the database
    insertedPalletMngt = palletMngtRepository.saveAndFlush(palletMngt);

    long databaseSizeBeforeDelete = getRepositoryCount();

    // Delete the palletMngt
    restPalletMngtMockMvc
      .perform(
        delete(ENTITY_API_URL_ID, palletMngt.getId())
          .with(csrf())
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNoContent());

    // Validate the database contains one less item
    assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
  }

  protected long getRepositoryCount() {
    return palletMngtRepository.count();
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

  protected PalletMngt getPersistedPalletMngt(PalletMngt palletMngt) {
    return palletMngtRepository.findById(palletMngt.getId()).orElseThrow();
  }

  protected void assertPersistedPalletMngtToMatchAllProperties(
    PalletMngt expectedPalletMngt
  ) {
    assertPalletMngtAllPropertiesEquals(
      expectedPalletMngt,
      getPersistedPalletMngt(expectedPalletMngt)
    );
  }

  protected void assertPersistedPalletMngtToMatchUpdatableProperties(
    PalletMngt expectedPalletMngt
  ) {
    assertPalletMngtAllUpdatablePropertiesEquals(
      expectedPalletMngt,
      getPersistedPalletMngt(expectedPalletMngt)
    );
  }
}
