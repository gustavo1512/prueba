package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Habitacion;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.HabitacionRepository;
import java.time.Duration;
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
 * Integration tests for the {@link HabitacionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class HabitacionResourceIT {

    private static final String DEFAULT_TIPO = "AAAAAAAAAA";
    private static final String UPDATED_TIPO = "BBBBBBBBBB";

    private static final Long DEFAULT_CAPACIDAD_ADULTO = 1L;
    private static final Long UPDATED_CAPACIDAD_ADULTO = 2L;

    private static final Long DEFAULT_CAPACIDAD_MENOR = 1L;
    private static final Long UPDATED_CAPACIDAD_MENOR = 2L;

    private static final Boolean DEFAULT_DISPONIBLE = false;
    private static final Boolean UPDATED_DISPONIBLE = true;

    private static final String ENTITY_API_URL = "/api/habitacions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Habitacion habitacion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Habitacion createEntity(EntityManager em) {
        Habitacion habitacion = new Habitacion()
            .tipo(DEFAULT_TIPO)
            .capacidadAdulto(DEFAULT_CAPACIDAD_ADULTO)
            .capacidadMenor(DEFAULT_CAPACIDAD_MENOR)
            .disponible(DEFAULT_DISPONIBLE);
        return habitacion;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Habitacion createUpdatedEntity(EntityManager em) {
        Habitacion habitacion = new Habitacion()
            .tipo(UPDATED_TIPO)
            .capacidadAdulto(UPDATED_CAPACIDAD_ADULTO)
            .capacidadMenor(UPDATED_CAPACIDAD_MENOR)
            .disponible(UPDATED_DISPONIBLE);
        return habitacion;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Habitacion.class).block();
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
        habitacion = createEntity(em);
    }

    @Test
    void createHabitacion() throws Exception {
        int databaseSizeBeforeCreate = habitacionRepository.findAll().collectList().block().size();
        // Create the Habitacion
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(habitacion))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Habitacion in the database
        List<Habitacion> habitacionList = habitacionRepository.findAll().collectList().block();
        assertThat(habitacionList).hasSize(databaseSizeBeforeCreate + 1);
        Habitacion testHabitacion = habitacionList.get(habitacionList.size() - 1);
        assertThat(testHabitacion.getTipo()).isEqualTo(DEFAULT_TIPO);
        assertThat(testHabitacion.getCapacidadAdulto()).isEqualTo(DEFAULT_CAPACIDAD_ADULTO);
        assertThat(testHabitacion.getCapacidadMenor()).isEqualTo(DEFAULT_CAPACIDAD_MENOR);
        assertThat(testHabitacion.getDisponible()).isEqualTo(DEFAULT_DISPONIBLE);
    }

    @Test
    void createHabitacionWithExistingId() throws Exception {
        // Create the Habitacion with an existing ID
        habitacion.setId(1L);

        int databaseSizeBeforeCreate = habitacionRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(habitacion))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Habitacion in the database
        List<Habitacion> habitacionList = habitacionRepository.findAll().collectList().block();
        assertThat(habitacionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllHabitacionsAsStream() {
        // Initialize the database
        habitacionRepository.save(habitacion).block();

        List<Habitacion> habitacionList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Habitacion.class)
            .getResponseBody()
            .filter(habitacion::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(habitacionList).isNotNull();
        assertThat(habitacionList).hasSize(1);
        Habitacion testHabitacion = habitacionList.get(0);
        assertThat(testHabitacion.getTipo()).isEqualTo(DEFAULT_TIPO);
        assertThat(testHabitacion.getCapacidadAdulto()).isEqualTo(DEFAULT_CAPACIDAD_ADULTO);
        assertThat(testHabitacion.getCapacidadMenor()).isEqualTo(DEFAULT_CAPACIDAD_MENOR);
        assertThat(testHabitacion.getDisponible()).isEqualTo(DEFAULT_DISPONIBLE);
    }

    @Test
    void getAllHabitacions() {
        // Initialize the database
        habitacionRepository.save(habitacion).block();

        // Get all the habitacionList
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
            .value(hasItem(habitacion.getId().intValue()))
            .jsonPath("$.[*].tipo")
            .value(hasItem(DEFAULT_TIPO))
            .jsonPath("$.[*].capacidadAdulto")
            .value(hasItem(DEFAULT_CAPACIDAD_ADULTO.intValue()))
            .jsonPath("$.[*].capacidadMenor")
            .value(hasItem(DEFAULT_CAPACIDAD_MENOR.intValue()))
            .jsonPath("$.[*].disponible")
            .value(hasItem(DEFAULT_DISPONIBLE.booleanValue()));
    }

    @Test
    void getHabitacion() {
        // Initialize the database
        habitacionRepository.save(habitacion).block();

        // Get the habitacion
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, habitacion.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(habitacion.getId().intValue()))
            .jsonPath("$.tipo")
            .value(is(DEFAULT_TIPO))
            .jsonPath("$.capacidadAdulto")
            .value(is(DEFAULT_CAPACIDAD_ADULTO.intValue()))
            .jsonPath("$.capacidadMenor")
            .value(is(DEFAULT_CAPACIDAD_MENOR.intValue()))
            .jsonPath("$.disponible")
            .value(is(DEFAULT_DISPONIBLE.booleanValue()));
    }

    @Test
    void getNonExistingHabitacion() {
        // Get the habitacion
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingHabitacion() throws Exception {
        // Initialize the database
        habitacionRepository.save(habitacion).block();

        int databaseSizeBeforeUpdate = habitacionRepository.findAll().collectList().block().size();

        // Update the habitacion
        Habitacion updatedHabitacion = habitacionRepository.findById(habitacion.getId()).block();
        updatedHabitacion
            .tipo(UPDATED_TIPO)
            .capacidadAdulto(UPDATED_CAPACIDAD_ADULTO)
            .capacidadMenor(UPDATED_CAPACIDAD_MENOR)
            .disponible(UPDATED_DISPONIBLE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedHabitacion.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedHabitacion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Habitacion in the database
        List<Habitacion> habitacionList = habitacionRepository.findAll().collectList().block();
        assertThat(habitacionList).hasSize(databaseSizeBeforeUpdate);
        Habitacion testHabitacion = habitacionList.get(habitacionList.size() - 1);
        assertThat(testHabitacion.getTipo()).isEqualTo(UPDATED_TIPO);
        assertThat(testHabitacion.getCapacidadAdulto()).isEqualTo(UPDATED_CAPACIDAD_ADULTO);
        assertThat(testHabitacion.getCapacidadMenor()).isEqualTo(UPDATED_CAPACIDAD_MENOR);
        assertThat(testHabitacion.getDisponible()).isEqualTo(UPDATED_DISPONIBLE);
    }

    @Test
    void putNonExistingHabitacion() throws Exception {
        int databaseSizeBeforeUpdate = habitacionRepository.findAll().collectList().block().size();
        habitacion.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, habitacion.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(habitacion))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Habitacion in the database
        List<Habitacion> habitacionList = habitacionRepository.findAll().collectList().block();
        assertThat(habitacionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchHabitacion() throws Exception {
        int databaseSizeBeforeUpdate = habitacionRepository.findAll().collectList().block().size();
        habitacion.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(habitacion))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Habitacion in the database
        List<Habitacion> habitacionList = habitacionRepository.findAll().collectList().block();
        assertThat(habitacionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamHabitacion() throws Exception {
        int databaseSizeBeforeUpdate = habitacionRepository.findAll().collectList().block().size();
        habitacion.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(habitacion))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Habitacion in the database
        List<Habitacion> habitacionList = habitacionRepository.findAll().collectList().block();
        assertThat(habitacionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateHabitacionWithPatch() throws Exception {
        // Initialize the database
        habitacionRepository.save(habitacion).block();

        int databaseSizeBeforeUpdate = habitacionRepository.findAll().collectList().block().size();

        // Update the habitacion using partial update
        Habitacion partialUpdatedHabitacion = new Habitacion();
        partialUpdatedHabitacion.setId(habitacion.getId());

        partialUpdatedHabitacion.tipo(UPDATED_TIPO).capacidadAdulto(UPDATED_CAPACIDAD_ADULTO).disponible(UPDATED_DISPONIBLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedHabitacion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedHabitacion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Habitacion in the database
        List<Habitacion> habitacionList = habitacionRepository.findAll().collectList().block();
        assertThat(habitacionList).hasSize(databaseSizeBeforeUpdate);
        Habitacion testHabitacion = habitacionList.get(habitacionList.size() - 1);
        assertThat(testHabitacion.getTipo()).isEqualTo(UPDATED_TIPO);
        assertThat(testHabitacion.getCapacidadAdulto()).isEqualTo(UPDATED_CAPACIDAD_ADULTO);
        assertThat(testHabitacion.getCapacidadMenor()).isEqualTo(DEFAULT_CAPACIDAD_MENOR);
        assertThat(testHabitacion.getDisponible()).isEqualTo(UPDATED_DISPONIBLE);
    }

    @Test
    void fullUpdateHabitacionWithPatch() throws Exception {
        // Initialize the database
        habitacionRepository.save(habitacion).block();

        int databaseSizeBeforeUpdate = habitacionRepository.findAll().collectList().block().size();

        // Update the habitacion using partial update
        Habitacion partialUpdatedHabitacion = new Habitacion();
        partialUpdatedHabitacion.setId(habitacion.getId());

        partialUpdatedHabitacion
            .tipo(UPDATED_TIPO)
            .capacidadAdulto(UPDATED_CAPACIDAD_ADULTO)
            .capacidadMenor(UPDATED_CAPACIDAD_MENOR)
            .disponible(UPDATED_DISPONIBLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedHabitacion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedHabitacion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Habitacion in the database
        List<Habitacion> habitacionList = habitacionRepository.findAll().collectList().block();
        assertThat(habitacionList).hasSize(databaseSizeBeforeUpdate);
        Habitacion testHabitacion = habitacionList.get(habitacionList.size() - 1);
        assertThat(testHabitacion.getTipo()).isEqualTo(UPDATED_TIPO);
        assertThat(testHabitacion.getCapacidadAdulto()).isEqualTo(UPDATED_CAPACIDAD_ADULTO);
        assertThat(testHabitacion.getCapacidadMenor()).isEqualTo(UPDATED_CAPACIDAD_MENOR);
        assertThat(testHabitacion.getDisponible()).isEqualTo(UPDATED_DISPONIBLE);
    }

    @Test
    void patchNonExistingHabitacion() throws Exception {
        int databaseSizeBeforeUpdate = habitacionRepository.findAll().collectList().block().size();
        habitacion.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, habitacion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(habitacion))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Habitacion in the database
        List<Habitacion> habitacionList = habitacionRepository.findAll().collectList().block();
        assertThat(habitacionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchHabitacion() throws Exception {
        int databaseSizeBeforeUpdate = habitacionRepository.findAll().collectList().block().size();
        habitacion.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(habitacion))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Habitacion in the database
        List<Habitacion> habitacionList = habitacionRepository.findAll().collectList().block();
        assertThat(habitacionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamHabitacion() throws Exception {
        int databaseSizeBeforeUpdate = habitacionRepository.findAll().collectList().block().size();
        habitacion.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(habitacion))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Habitacion in the database
        List<Habitacion> habitacionList = habitacionRepository.findAll().collectList().block();
        assertThat(habitacionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteHabitacion() {
        // Initialize the database
        habitacionRepository.save(habitacion).block();

        int databaseSizeBeforeDelete = habitacionRepository.findAll().collectList().block().size();

        // Delete the habitacion
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, habitacion.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Habitacion> habitacionList = habitacionRepository.findAll().collectList().block();
        assertThat(habitacionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
