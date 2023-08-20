package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Persona;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.PersonaRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link PersonaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PersonaResourceIT {

    private static final Instant DEFAULT_FECHA_NACIMIENTO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_NACIMIENTO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_TIPO = "AAAAAAAAAA";
    private static final String UPDATED_TIPO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/personas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Persona persona;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Persona createEntity(EntityManager em) {
        Persona persona = new Persona().fechaNacimiento(DEFAULT_FECHA_NACIMIENTO).tipo(DEFAULT_TIPO);
        return persona;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Persona createUpdatedEntity(EntityManager em) {
        Persona persona = new Persona().fechaNacimiento(UPDATED_FECHA_NACIMIENTO).tipo(UPDATED_TIPO);
        return persona;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Persona.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        persona = createEntity(em);
    }

    @Test
    void createPersona() throws Exception {
        int databaseSizeBeforeCreate = personaRepository.findAll().collectList().block().size();
        // Create the Persona
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(persona))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Persona in the database
        List<Persona> personaList = personaRepository.findAll().collectList().block();
        assertThat(personaList).hasSize(databaseSizeBeforeCreate + 1);
        Persona testPersona = personaList.get(personaList.size() - 1);
        assertThat(testPersona.getFechaNacimiento()).isEqualTo(DEFAULT_FECHA_NACIMIENTO);
        assertThat(testPersona.getTipo()).isEqualTo(DEFAULT_TIPO);
    }

    @Test
    void createPersonaWithExistingId() throws Exception {
        // Create the Persona with an existing ID
        persona.setId(1L);

        int databaseSizeBeforeCreate = personaRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(persona))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Persona in the database
        List<Persona> personaList = personaRepository.findAll().collectList().block();
        assertThat(personaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPersonasAsStream() {
        // Initialize the database
        personaRepository.save(persona).block();

        List<Persona> personaList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Persona.class)
            .getResponseBody()
            .filter(persona::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(personaList).isNotNull();
        assertThat(personaList).hasSize(1);
        Persona testPersona = personaList.get(0);
        assertThat(testPersona.getFechaNacimiento()).isEqualTo(DEFAULT_FECHA_NACIMIENTO);
        assertThat(testPersona.getTipo()).isEqualTo(DEFAULT_TIPO);
    }

    @Test
    void getAllPersonas() {
        // Initialize the database
        personaRepository.save(persona).block();

        // Get all the personaList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(persona.getId().intValue()))
            .jsonPath("$.[*].fechaNacimiento")
            .value(hasItem(DEFAULT_FECHA_NACIMIENTO.toString()))
            .jsonPath("$.[*].tipo")
            .value(hasItem(DEFAULT_TIPO));
    }

    @Test
    void getPersona() {
        // Initialize the database
        personaRepository.save(persona).block();

        // Get the persona
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, persona.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(persona.getId().intValue()))
            .jsonPath("$.fechaNacimiento")
            .value(is(DEFAULT_FECHA_NACIMIENTO.toString()))
            .jsonPath("$.tipo")
            .value(is(DEFAULT_TIPO));
    }

    @Test
    void getNonExistingPersona() {
        // Get the persona
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPersona() throws Exception {
        // Initialize the database
        personaRepository.save(persona).block();

        int databaseSizeBeforeUpdate = personaRepository.findAll().collectList().block().size();

        // Update the persona
        Persona updatedPersona = personaRepository.findById(persona.getId()).block();
        updatedPersona.fechaNacimiento(UPDATED_FECHA_NACIMIENTO).tipo(UPDATED_TIPO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPersona.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPersona))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Persona in the database
        List<Persona> personaList = personaRepository.findAll().collectList().block();
        assertThat(personaList).hasSize(databaseSizeBeforeUpdate);
        Persona testPersona = personaList.get(personaList.size() - 1);
        assertThat(testPersona.getFechaNacimiento()).isEqualTo(UPDATED_FECHA_NACIMIENTO);
        assertThat(testPersona.getTipo()).isEqualTo(UPDATED_TIPO);
    }

    @Test
    void putNonExistingPersona() throws Exception {
        int databaseSizeBeforeUpdate = personaRepository.findAll().collectList().block().size();
        persona.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, persona.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(persona))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Persona in the database
        List<Persona> personaList = personaRepository.findAll().collectList().block();
        assertThat(personaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPersona() throws Exception {
        int databaseSizeBeforeUpdate = personaRepository.findAll().collectList().block().size();
        persona.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(persona))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Persona in the database
        List<Persona> personaList = personaRepository.findAll().collectList().block();
        assertThat(personaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPersona() throws Exception {
        int databaseSizeBeforeUpdate = personaRepository.findAll().collectList().block().size();
        persona.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(persona))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Persona in the database
        List<Persona> personaList = personaRepository.findAll().collectList().block();
        assertThat(personaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePersonaWithPatch() throws Exception {
        // Initialize the database
        personaRepository.save(persona).block();

        int databaseSizeBeforeUpdate = personaRepository.findAll().collectList().block().size();

        // Update the persona using partial update
        Persona partialUpdatedPersona = new Persona();
        partialUpdatedPersona.setId(persona.getId());

        partialUpdatedPersona.tipo(UPDATED_TIPO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPersona.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPersona))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Persona in the database
        List<Persona> personaList = personaRepository.findAll().collectList().block();
        assertThat(personaList).hasSize(databaseSizeBeforeUpdate);
        Persona testPersona = personaList.get(personaList.size() - 1);
        assertThat(testPersona.getFechaNacimiento()).isEqualTo(DEFAULT_FECHA_NACIMIENTO);
        assertThat(testPersona.getTipo()).isEqualTo(UPDATED_TIPO);
    }

    @Test
    void fullUpdatePersonaWithPatch() throws Exception {
        // Initialize the database
        personaRepository.save(persona).block();

        int databaseSizeBeforeUpdate = personaRepository.findAll().collectList().block().size();

        // Update the persona using partial update
        Persona partialUpdatedPersona = new Persona();
        partialUpdatedPersona.setId(persona.getId());

        partialUpdatedPersona.fechaNacimiento(UPDATED_FECHA_NACIMIENTO).tipo(UPDATED_TIPO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPersona.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPersona))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Persona in the database
        List<Persona> personaList = personaRepository.findAll().collectList().block();
        assertThat(personaList).hasSize(databaseSizeBeforeUpdate);
        Persona testPersona = personaList.get(personaList.size() - 1);
        assertThat(testPersona.getFechaNacimiento()).isEqualTo(UPDATED_FECHA_NACIMIENTO);
        assertThat(testPersona.getTipo()).isEqualTo(UPDATED_TIPO);
    }

    @Test
    void patchNonExistingPersona() throws Exception {
        int databaseSizeBeforeUpdate = personaRepository.findAll().collectList().block().size();
        persona.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, persona.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(persona))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Persona in the database
        List<Persona> personaList = personaRepository.findAll().collectList().block();
        assertThat(personaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPersona() throws Exception {
        int databaseSizeBeforeUpdate = personaRepository.findAll().collectList().block().size();
        persona.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(persona))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Persona in the database
        List<Persona> personaList = personaRepository.findAll().collectList().block();
        assertThat(personaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPersona() throws Exception {
        int databaseSizeBeforeUpdate = personaRepository.findAll().collectList().block().size();
        persona.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(persona))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Persona in the database
        List<Persona> personaList = personaRepository.findAll().collectList().block();
        assertThat(personaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePersona() {
        // Initialize the database
        personaRepository.save(persona).block();

        int databaseSizeBeforeDelete = personaRepository.findAll().collectList().block().size();

        // Delete the persona
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, persona.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Persona> personaList = personaRepository.findAll().collectList().block();
        assertThat(personaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
