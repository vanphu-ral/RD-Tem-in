package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.PalletBoxMappingAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.PalletBoxMapping;
import com.mycompany.myapp.repository.PalletBoxMappingRepository;
import com.mycompany.myapp.service.dto.PalletBoxMappingDTO;
import com.mycompany.myapp.service.mapper.PalletBoxMappingMapper;
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
 * Integration tests for the {@link PalletBoxMappingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PalletBoxMappingResourceIT {

  private static final String DEFAULT_SERIAL_PALLET = "AAAAAAAAAA";
  private static final String UPDATED_SERIAL_PALLET = "BBBBBBBBBB";

  private static final Instant DEFAULT_CREATE_AT = Instant.ofEpochMilli(0L);
  private static final Instant UPDATED_CREATE_AT = Instant.now()
    .truncatedTo(ChronoUnit.MILLIS);

  private static final String DEFAULT_CREATE_BY = "AAAAAAAAAA";
  private static final String UPDATED_CREATE_BY = "BBBBBBBBBB";

  private static final String ENTITY_API_URL = "/api/pallet-box-mappings";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

  private static Random random = new Random();
  private static AtomicLong longCount = new AtomicLong(
    random.nextInt() + (2 * Integer.MAX_VALUE)
  );

  @Autowired
  private ObjectMapper om;

  @Autowired
  private PalletBoxMappingRepository palletBoxMappingRepository;

  @Autowired
  private PalletBoxMappingMapper palletBoxMappingMapper;

  @Autowired
  private EntityManager em;

  @Autowired
  private MockMvc restPalletBoxMappingMockMvc;

  private PalletBoxMapping palletBoxMapping;

  private PalletBoxMapping insertedPalletBoxMapping;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static PalletBoxMapping createEntity() {
    return new PalletBoxMapping()
      .serialPallet(DEFAULT_SERIAL_PALLET)
      .createAt(DEFAULT_CREATE_AT)
      .createBy(DEFAULT_CREATE_BY);
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static PalletBoxMapping createUpdatedEntity() {
    return new PalletBoxMapping()
      .serialPallet(UPDATED_SERIAL_PALLET)
      .createAt(UPDATED_CREATE_AT)
      .createBy(UPDATED_CREATE_BY);
  }

  @BeforeEach
  void initTest() {
    palletBoxMapping = createEntity();
  }

  @AfterEach
  void cleanup() {
    if (insertedPalletBoxMapping != null) {
      palletBoxMappingRepository.delete(insertedPalletBoxMapping);
      insertedPalletBoxMapping = null;
    }
  }

  @Test
  @Transactional
  void createPalletBoxMapping() throws Exception {
    long databaseSizeBeforeCreate = getRepositoryCount();
    // Create the PalletBoxMapping
    PalletBoxMappingDTO palletBoxMappingDTO = palletBoxMappingMapper.toDto(
      palletBoxMapping
    );
    var returnedPalletBoxMappingDTO = om.readValue(
      restPalletBoxMappingMockMvc
        .perform(
          post(ENTITY_API_URL)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsBytes(palletBoxMappingDTO))
        )
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString(),
      PalletBoxMappingDTO.class
    );

    // Validate the PalletBoxMapping in the database
    assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
    var returnedPalletBoxMapping = palletBoxMappingMapper.toEntity(
      returnedPalletBoxMappingDTO
    );
    assertPalletBoxMappingUpdatableFieldsEquals(
      returnedPalletBoxMapping,
      getPersistedPalletBoxMapping(returnedPalletBoxMapping)
    );

    insertedPalletBoxMapping = returnedPalletBoxMapping;
  }

  @Test
  @Transactional
  void createPalletBoxMappingWithExistingId() throws Exception {
    // Create the PalletBoxMapping with an existing ID
    palletBoxMapping.setId(1L);
    PalletBoxMappingDTO palletBoxMappingDTO = palletBoxMappingMapper.toDto(
      palletBoxMapping
    );

    long databaseSizeBeforeCreate = getRepositoryCount();

    // An entity with an existing ID cannot be created, so this API call must fail
    restPalletBoxMappingMockMvc
      .perform(
        post(ENTITY_API_URL)
          .with(csrf())
          .contentType(MediaType.APPLICATION_JSON)
          .content(om.writeValueAsBytes(palletBoxMappingDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the PalletBoxMapping in the database
    assertSameRepositoryCount(databaseSizeBeforeCreate);
  }

  @Test
  @Transactional
  void getAllPalletBoxMappings() throws Exception {
    // Initialize the database
    insertedPalletBoxMapping = palletBoxMappingRepository.saveAndFlush(
      palletBoxMapping
    );

    // Get all the palletBoxMappingList
    restPalletBoxMappingMockMvc
      .perform(get(ENTITY_API_URL + "?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(
        jsonPath("$.[*].id").value(hasItem(palletBoxMapping.getId().intValue()))
      )
      .andExpect(
        jsonPath("$.[*].serialPallet").value(hasItem(DEFAULT_SERIAL_PALLET))
      )
      .andExpect(
        jsonPath("$.[*].createAt").value(hasItem(DEFAULT_CREATE_AT.toString()))
      )
      .andExpect(jsonPath("$.[*].createBy").value(hasItem(DEFAULT_CREATE_BY)));
  }

  @Test
  @Transactional
  void getPalletBoxMapping() throws Exception {
    // Initialize the database
    insertedPalletBoxMapping = palletBoxMappingRepository.saveAndFlush(
      palletBoxMapping
    );

    // Get the palletBoxMapping
    restPalletBoxMappingMockMvc
      .perform(get(ENTITY_API_URL_ID, palletBoxMapping.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").value(palletBoxMapping.getId().intValue()))
      .andExpect(jsonPath("$.serialPallet").value(DEFAULT_SERIAL_PALLET))
      .andExpect(jsonPath("$.createAt").value(DEFAULT_CREATE_AT.toString()))
      .andExpect(jsonPath("$.createBy").value(DEFAULT_CREATE_BY));
  }

  @Test
  @Transactional
  void getNonExistingPalletBoxMapping() throws Exception {
    // Get the palletBoxMapping
    restPalletBoxMappingMockMvc
      .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
      .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  void putExistingPalletBoxMapping() throws Exception {
    // Initialize the database
    insertedPalletBoxMapping = palletBoxMappingRepository.saveAndFlush(
      palletBoxMapping
    );

    long databaseSizeBeforeUpdate = getRepositoryCount();

    // Update the palletBoxMapping
    PalletBoxMapping updatedPalletBoxMapping = palletBoxMappingRepository
      .findById(palletBoxMapping.getId())
      .orElseThrow();
    // Disconnect from session so that the updates on updatedPalletBoxMapping are not directly saved in db
    em.detach(updatedPalletBoxMapping);
    updatedPalletBoxMapping
      .serialPallet(UPDATED_SERIAL_PALLET)
      .createAt(UPDATED_CREATE_AT)
      .createBy(UPDATED_CREATE_BY);
    PalletBoxMappingDTO palletBoxMappingDTO = palletBoxMappingMapper.toDto(
      updatedPalletBoxMapping
    );

    restPalletBoxMappingMockMvc
      .perform(
        put(ENTITY_API_URL_ID, palletBoxMappingDTO.getId())
          .with(csrf())
          .contentType(MediaType.APPLICATION_JSON)
          .content(om.writeValueAsBytes(palletBoxMappingDTO))
      )
      .andExpect(status().isOk());

    // Validate the PalletBoxMapping in the database
    assertSameRepositoryCount(databaseSizeBeforeUpdate);
    assertPersistedPalletBoxMappingToMatchAllProperties(
      updatedPalletBoxMapping
    );
  }

  @Test
  @Transactional
  void putNonExistingPalletBoxMapping() throws Exception {
    long databaseSizeBeforeUpdate = getRepositoryCount();
    palletBoxMapping.setId(longCount.incrementAndGet());

    // Create the PalletBoxMapping
    PalletBoxMappingDTO palletBoxMappingDTO = palletBoxMappingMapper.toDto(
      palletBoxMapping
    );

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restPalletBoxMappingMockMvc
      .perform(
        put(ENTITY_API_URL_ID, palletBoxMappingDTO.getId())
          .with(csrf())
          .contentType(MediaType.APPLICATION_JSON)
          .content(om.writeValueAsBytes(palletBoxMappingDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the PalletBoxMapping in the database
    assertSameRepositoryCount(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void putWithIdMismatchPalletBoxMapping() throws Exception {
    long databaseSizeBeforeUpdate = getRepositoryCount();
    palletBoxMapping.setId(longCount.incrementAndGet());

    // Create the PalletBoxMapping
    PalletBoxMappingDTO palletBoxMappingDTO = palletBoxMappingMapper.toDto(
      palletBoxMapping
    );

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPalletBoxMappingMockMvc
      .perform(
        put(ENTITY_API_URL_ID, longCount.incrementAndGet())
          .with(csrf())
          .contentType(MediaType.APPLICATION_JSON)
          .content(om.writeValueAsBytes(palletBoxMappingDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the PalletBoxMapping in the database
    assertSameRepositoryCount(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void putWithMissingIdPathParamPalletBoxMapping() throws Exception {
    long databaseSizeBeforeUpdate = getRepositoryCount();
    palletBoxMapping.setId(longCount.incrementAndGet());

    // Create the PalletBoxMapping
    PalletBoxMappingDTO palletBoxMappingDTO = palletBoxMappingMapper.toDto(
      palletBoxMapping
    );

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPalletBoxMappingMockMvc
      .perform(
        put(ENTITY_API_URL)
          .with(csrf())
          .contentType(MediaType.APPLICATION_JSON)
          .content(om.writeValueAsBytes(palletBoxMappingDTO))
      )
      .andExpect(status().isMethodNotAllowed());

    // Validate the PalletBoxMapping in the database
    assertSameRepositoryCount(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void partialUpdatePalletBoxMappingWithPatch() throws Exception {
    // Initialize the database
    insertedPalletBoxMapping = palletBoxMappingRepository.saveAndFlush(
      palletBoxMapping
    );

    long databaseSizeBeforeUpdate = getRepositoryCount();

    // Update the palletBoxMapping using partial update
    PalletBoxMapping partialUpdatedPalletBoxMapping = new PalletBoxMapping();
    partialUpdatedPalletBoxMapping.setId(palletBoxMapping.getId());

    partialUpdatedPalletBoxMapping.createAt(UPDATED_CREATE_AT);

    restPalletBoxMappingMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedPalletBoxMapping.getId())
          .with(csrf())
          .contentType("application/merge-patch+json")
          .content(om.writeValueAsBytes(partialUpdatedPalletBoxMapping))
      )
      .andExpect(status().isOk());

    // Validate the PalletBoxMapping in the database

    assertSameRepositoryCount(databaseSizeBeforeUpdate);
    assertPalletBoxMappingUpdatableFieldsEquals(
      createUpdateProxyForBean(
        partialUpdatedPalletBoxMapping,
        palletBoxMapping
      ),
      getPersistedPalletBoxMapping(palletBoxMapping)
    );
  }

  @Test
  @Transactional
  void fullUpdatePalletBoxMappingWithPatch() throws Exception {
    // Initialize the database
    insertedPalletBoxMapping = palletBoxMappingRepository.saveAndFlush(
      palletBoxMapping
    );

    long databaseSizeBeforeUpdate = getRepositoryCount();

    // Update the palletBoxMapping using partial update
    PalletBoxMapping partialUpdatedPalletBoxMapping = new PalletBoxMapping();
    partialUpdatedPalletBoxMapping.setId(palletBoxMapping.getId());

    partialUpdatedPalletBoxMapping
      .serialPallet(UPDATED_SERIAL_PALLET)
      .createAt(UPDATED_CREATE_AT)
      .createBy(UPDATED_CREATE_BY);

    restPalletBoxMappingMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedPalletBoxMapping.getId())
          .with(csrf())
          .contentType("application/merge-patch+json")
          .content(om.writeValueAsBytes(partialUpdatedPalletBoxMapping))
      )
      .andExpect(status().isOk());

    // Validate the PalletBoxMapping in the database

    assertSameRepositoryCount(databaseSizeBeforeUpdate);
    assertPalletBoxMappingUpdatableFieldsEquals(
      partialUpdatedPalletBoxMapping,
      getPersistedPalletBoxMapping(partialUpdatedPalletBoxMapping)
    );
  }

  @Test
  @Transactional
  void patchNonExistingPalletBoxMapping() throws Exception {
    long databaseSizeBeforeUpdate = getRepositoryCount();
    palletBoxMapping.setId(longCount.incrementAndGet());

    // Create the PalletBoxMapping
    PalletBoxMappingDTO palletBoxMappingDTO = palletBoxMappingMapper.toDto(
      palletBoxMapping
    );

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restPalletBoxMappingMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, palletBoxMappingDTO.getId())
          .with(csrf())
          .contentType("application/merge-patch+json")
          .content(om.writeValueAsBytes(palletBoxMappingDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the PalletBoxMapping in the database
    assertSameRepositoryCount(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void patchWithIdMismatchPalletBoxMapping() throws Exception {
    long databaseSizeBeforeUpdate = getRepositoryCount();
    palletBoxMapping.setId(longCount.incrementAndGet());

    // Create the PalletBoxMapping
    PalletBoxMappingDTO palletBoxMappingDTO = palletBoxMappingMapper.toDto(
      palletBoxMapping
    );

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPalletBoxMappingMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
          .with(csrf())
          .contentType("application/merge-patch+json")
          .content(om.writeValueAsBytes(palletBoxMappingDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the PalletBoxMapping in the database
    assertSameRepositoryCount(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void patchWithMissingIdPathParamPalletBoxMapping() throws Exception {
    long databaseSizeBeforeUpdate = getRepositoryCount();
    palletBoxMapping.setId(longCount.incrementAndGet());

    // Create the PalletBoxMapping
    PalletBoxMappingDTO palletBoxMappingDTO = palletBoxMappingMapper.toDto(
      palletBoxMapping
    );

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPalletBoxMappingMockMvc
      .perform(
        patch(ENTITY_API_URL)
          .with(csrf())
          .contentType("application/merge-patch+json")
          .content(om.writeValueAsBytes(palletBoxMappingDTO))
      )
      .andExpect(status().isMethodNotAllowed());

    // Validate the PalletBoxMapping in the database
    assertSameRepositoryCount(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void deletePalletBoxMapping() throws Exception {
    // Initialize the database
    insertedPalletBoxMapping = palletBoxMappingRepository.saveAndFlush(
      palletBoxMapping
    );

    long databaseSizeBeforeDelete = getRepositoryCount();

    // Delete the palletBoxMapping
    restPalletBoxMappingMockMvc
      .perform(
        delete(ENTITY_API_URL_ID, palletBoxMapping.getId())
          .with(csrf())
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNoContent());

    // Validate the database contains one less item
    assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
  }

  protected long getRepositoryCount() {
    return palletBoxMappingRepository.count();
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

  protected PalletBoxMapping getPersistedPalletBoxMapping(
    PalletBoxMapping palletBoxMapping
  ) {
    return palletBoxMappingRepository
      .findById(palletBoxMapping.getId())
      .orElseThrow();
  }

  protected void assertPersistedPalletBoxMappingToMatchAllProperties(
    PalletBoxMapping expectedPalletBoxMapping
  ) {
    assertPalletBoxMappingAllPropertiesEquals(
      expectedPalletBoxMapping,
      getPersistedPalletBoxMapping(expectedPalletBoxMapping)
    );
  }

  protected void assertPersistedPalletBoxMappingToMatchUpdatableProperties(
    PalletBoxMapping expectedPalletBoxMapping
  ) {
    assertPalletBoxMappingAllUpdatablePropertiesEquals(
      expectedPalletBoxMapping,
      getPersistedPalletBoxMapping(expectedPalletBoxMapping)
    );
  }
}
