package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.ReservarHabitacion;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.ReservarHabitacionRepository;
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
 * Integration tests for the {@link ReservarHabitacionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ReservarHabitacionResourceIT {

    private static final Instant DEFAULT_FECHA_RESERVA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_RESERVA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_FECHA_INICIO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_INICIO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_FECHA_FINAL = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_FINAL = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Double DEFAULT_TOTAL_RESERVACION = 1D;
    private static final Double UPDATED_TOTAL_RESERVACION = 2D;

    private static final String ENTITY_API_URL = "/api/reservar-habitacions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ReservarHabitacionRepository reservarHabitacionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ReservarHabitacion reservarHabitacion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReservarHabitacion createEntity(EntityManager em) {
        ReservarHabitacion reservarHabitacion = new ReservarHabitacion()
            .fechaReserva(DEFAULT_FECHA_RESERVA)
            .fechaInicio(DEFAULT_FECHA_INICIO)
            .fechaFinal(DEFAULT_FECHA_FINAL)
            .totalReservacion(DEFAULT_TOTAL_RESERVACION);
        return reservarHabitacion;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReservarHabitacion createUpdatedEntity(EntityManager em) {
        ReservarHabitacion reservarHabitacion = new ReservarHabitacion()
            .fechaReserva(UPDATED_FECHA_RESERVA)
            .fechaInicio(UPDATED_FECHA_INICIO)
            .fechaFinal(UPDATED_FECHA_FINAL)
            .totalReservacion(UPDATED_TOTAL_RESERVACION);
        return reservarHabitacion;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ReservarHabitacion.class).block();
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
        reservarHabitacion = createEntity(em);
    }

    @Test
    void createReservarHabitacion() throws Exception {
        int databaseSizeBeforeCreate = reservarHabitacionRepository.findAll().collectList().block().size();
        // Create the ReservarHabitacion
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservarHabitacion))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ReservarHabitacion in the database
        List<ReservarHabitacion> reservarHabitacionList = reservarHabitacionRepository.findAll().collectList().block();
        assertThat(reservarHabitacionList).hasSize(databaseSizeBeforeCreate + 1);
        ReservarHabitacion testReservarHabitacion = reservarHabitacionList.get(reservarHabitacionList.size() - 1);
        assertThat(testReservarHabitacion.getFechaReserva()).isEqualTo(DEFAULT_FECHA_RESERVA);
        assertThat(testReservarHabitacion.getFechaInicio()).isEqualTo(DEFAULT_FECHA_INICIO);
        assertThat(testReservarHabitacion.getFechaFinal()).isEqualTo(DEFAULT_FECHA_FINAL);
        assertThat(testReservarHabitacion.getTotalReservacion()).isEqualTo(DEFAULT_TOTAL_RESERVACION);
    }

    @Test
    void createReservarHabitacionWithExistingId() throws Exception {
        // Create the ReservarHabitacion with an existing ID
        reservarHabitacion.setId(1L);

        int databaseSizeBeforeCreate = reservarHabitacionRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservarHabitacion))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ReservarHabitacion in the database
        List<ReservarHabitacion> reservarHabitacionList = reservarHabitacionRepository.findAll().collectList().block();
        assertThat(reservarHabitacionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllReservarHabitacionsAsStream() {
        // Initialize the database
        reservarHabitacionRepository.save(reservarHabitacion).block();

        List<ReservarHabitacion> reservarHabitacionList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ReservarHabitacion.class)
            .getResponseBody()
            .filter(reservarHabitacion::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(reservarHabitacionList).isNotNull();
        assertThat(reservarHabitacionList).hasSize(1);
        ReservarHabitacion testReservarHabitacion = reservarHabitacionList.get(0);
        assertThat(testReservarHabitacion.getFechaReserva()).isEqualTo(DEFAULT_FECHA_RESERVA);
        assertThat(testReservarHabitacion.getFechaInicio()).isEqualTo(DEFAULT_FECHA_INICIO);
        assertThat(testReservarHabitacion.getFechaFinal()).isEqualTo(DEFAULT_FECHA_FINAL);
        assertThat(testReservarHabitacion.getTotalReservacion()).isEqualTo(DEFAULT_TOTAL_RESERVACION);
    }

    @Test
    void getAllReservarHabitacions() {
        // Initialize the database
        reservarHabitacionRepository.save(reservarHabitacion).block();

        // Get all the reservarHabitacionList
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
            .value(hasItem(reservarHabitacion.getId().intValue()))
            .jsonPath("$.[*].fechaReserva")
            .value(hasItem(DEFAULT_FECHA_RESERVA.toString()))
            .jsonPath("$.[*].fechaInicio")
            .value(hasItem(DEFAULT_FECHA_INICIO.toString()))
            .jsonPath("$.[*].fechaFinal")
            .value(hasItem(DEFAULT_FECHA_FINAL.toString()))
            .jsonPath("$.[*].totalReservacion")
            .value(hasItem(DEFAULT_TOTAL_RESERVACION.doubleValue()));
    }

    @Test
    void getReservarHabitacion() {
        // Initialize the database
        reservarHabitacionRepository.save(reservarHabitacion).block();

        // Get the reservarHabitacion
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, reservarHabitacion.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(reservarHabitacion.getId().intValue()))
            .jsonPath("$.fechaReserva")
            .value(is(DEFAULT_FECHA_RESERVA.toString()))
            .jsonPath("$.fechaInicio")
            .value(is(DEFAULT_FECHA_INICIO.toString()))
            .jsonPath("$.fechaFinal")
            .value(is(DEFAULT_FECHA_FINAL.toString()))
            .jsonPath("$.totalReservacion")
            .value(is(DEFAULT_TOTAL_RESERVACION.doubleValue()));
    }

    @Test
    void getNonExistingReservarHabitacion() {
        // Get the reservarHabitacion
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingReservarHabitacion() throws Exception {
        // Initialize the database
        reservarHabitacionRepository.save(reservarHabitacion).block();

        int databaseSizeBeforeUpdate = reservarHabitacionRepository.findAll().collectList().block().size();

        // Update the reservarHabitacion
        ReservarHabitacion updatedReservarHabitacion = reservarHabitacionRepository.findById(reservarHabitacion.getId()).block();
        updatedReservarHabitacion
            .fechaReserva(UPDATED_FECHA_RESERVA)
            .fechaInicio(UPDATED_FECHA_INICIO)
            .fechaFinal(UPDATED_FECHA_FINAL)
            .totalReservacion(UPDATED_TOTAL_RESERVACION);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedReservarHabitacion.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedReservarHabitacion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ReservarHabitacion in the database
        List<ReservarHabitacion> reservarHabitacionList = reservarHabitacionRepository.findAll().collectList().block();
        assertThat(reservarHabitacionList).hasSize(databaseSizeBeforeUpdate);
        ReservarHabitacion testReservarHabitacion = reservarHabitacionList.get(reservarHabitacionList.size() - 1);
        assertThat(testReservarHabitacion.getFechaReserva()).isEqualTo(UPDATED_FECHA_RESERVA);
        assertThat(testReservarHabitacion.getFechaInicio()).isEqualTo(UPDATED_FECHA_INICIO);
        assertThat(testReservarHabitacion.getFechaFinal()).isEqualTo(UPDATED_FECHA_FINAL);
        assertThat(testReservarHabitacion.getTotalReservacion()).isEqualTo(UPDATED_TOTAL_RESERVACION);
    }

    @Test
    void putNonExistingReservarHabitacion() throws Exception {
        int databaseSizeBeforeUpdate = reservarHabitacionRepository.findAll().collectList().block().size();
        reservarHabitacion.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, reservarHabitacion.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservarHabitacion))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ReservarHabitacion in the database
        List<ReservarHabitacion> reservarHabitacionList = reservarHabitacionRepository.findAll().collectList().block();
        assertThat(reservarHabitacionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchReservarHabitacion() throws Exception {
        int databaseSizeBeforeUpdate = reservarHabitacionRepository.findAll().collectList().block().size();
        reservarHabitacion.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservarHabitacion))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ReservarHabitacion in the database
        List<ReservarHabitacion> reservarHabitacionList = reservarHabitacionRepository.findAll().collectList().block();
        assertThat(reservarHabitacionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamReservarHabitacion() throws Exception {
        int databaseSizeBeforeUpdate = reservarHabitacionRepository.findAll().collectList().block().size();
        reservarHabitacion.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservarHabitacion))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ReservarHabitacion in the database
        List<ReservarHabitacion> reservarHabitacionList = reservarHabitacionRepository.findAll().collectList().block();
        assertThat(reservarHabitacionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateReservarHabitacionWithPatch() throws Exception {
        // Initialize the database
        reservarHabitacionRepository.save(reservarHabitacion).block();

        int databaseSizeBeforeUpdate = reservarHabitacionRepository.findAll().collectList().block().size();

        // Update the reservarHabitacion using partial update
        ReservarHabitacion partialUpdatedReservarHabitacion = new ReservarHabitacion();
        partialUpdatedReservarHabitacion.setId(reservarHabitacion.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedReservarHabitacion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedReservarHabitacion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ReservarHabitacion in the database
        List<ReservarHabitacion> reservarHabitacionList = reservarHabitacionRepository.findAll().collectList().block();
        assertThat(reservarHabitacionList).hasSize(databaseSizeBeforeUpdate);
        ReservarHabitacion testReservarHabitacion = reservarHabitacionList.get(reservarHabitacionList.size() - 1);
        assertThat(testReservarHabitacion.getFechaReserva()).isEqualTo(DEFAULT_FECHA_RESERVA);
        assertThat(testReservarHabitacion.getFechaInicio()).isEqualTo(DEFAULT_FECHA_INICIO);
        assertThat(testReservarHabitacion.getFechaFinal()).isEqualTo(DEFAULT_FECHA_FINAL);
        assertThat(testReservarHabitacion.getTotalReservacion()).isEqualTo(DEFAULT_TOTAL_RESERVACION);
    }

    @Test
    void fullUpdateReservarHabitacionWithPatch() throws Exception {
        // Initialize the database
        reservarHabitacionRepository.save(reservarHabitacion).block();

        int databaseSizeBeforeUpdate = reservarHabitacionRepository.findAll().collectList().block().size();

        // Update the reservarHabitacion using partial update
        ReservarHabitacion partialUpdatedReservarHabitacion = new ReservarHabitacion();
        partialUpdatedReservarHabitacion.setId(reservarHabitacion.getId());

        partialUpdatedReservarHabitacion
            .fechaReserva(UPDATED_FECHA_RESERVA)
            .fechaInicio(UPDATED_FECHA_INICIO)
            .fechaFinal(UPDATED_FECHA_FINAL)
            .totalReservacion(UPDATED_TOTAL_RESERVACION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedReservarHabitacion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedReservarHabitacion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ReservarHabitacion in the database
        List<ReservarHabitacion> reservarHabitacionList = reservarHabitacionRepository.findAll().collectList().block();
        assertThat(reservarHabitacionList).hasSize(databaseSizeBeforeUpdate);
        ReservarHabitacion testReservarHabitacion = reservarHabitacionList.get(reservarHabitacionList.size() - 1);
        assertThat(testReservarHabitacion.getFechaReserva()).isEqualTo(UPDATED_FECHA_RESERVA);
        assertThat(testReservarHabitacion.getFechaInicio()).isEqualTo(UPDATED_FECHA_INICIO);
        assertThat(testReservarHabitacion.getFechaFinal()).isEqualTo(UPDATED_FECHA_FINAL);
        assertThat(testReservarHabitacion.getTotalReservacion()).isEqualTo(UPDATED_TOTAL_RESERVACION);
    }

    @Test
    void patchNonExistingReservarHabitacion() throws Exception {
        int databaseSizeBeforeUpdate = reservarHabitacionRepository.findAll().collectList().block().size();
        reservarHabitacion.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, reservarHabitacion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservarHabitacion))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ReservarHabitacion in the database
        List<ReservarHabitacion> reservarHabitacionList = reservarHabitacionRepository.findAll().collectList().block();
        assertThat(reservarHabitacionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchReservarHabitacion() throws Exception {
        int databaseSizeBeforeUpdate = reservarHabitacionRepository.findAll().collectList().block().size();
        reservarHabitacion.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservarHabitacion))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ReservarHabitacion in the database
        List<ReservarHabitacion> reservarHabitacionList = reservarHabitacionRepository.findAll().collectList().block();
        assertThat(reservarHabitacionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamReservarHabitacion() throws Exception {
        int databaseSizeBeforeUpdate = reservarHabitacionRepository.findAll().collectList().block().size();
        reservarHabitacion.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservarHabitacion))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ReservarHabitacion in the database
        List<ReservarHabitacion> reservarHabitacionList = reservarHabitacionRepository.findAll().collectList().block();
        assertThat(reservarHabitacionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteReservarHabitacion() {
        // Initialize the database
        reservarHabitacionRepository.save(reservarHabitacion).block();

        int databaseSizeBeforeDelete = reservarHabitacionRepository.findAll().collectList().block().size();

        // Delete the reservarHabitacion
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, reservarHabitacion.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ReservarHabitacion> reservarHabitacionList = reservarHabitacionRepository.findAll().collectList().block();
        assertThat(reservarHabitacionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
