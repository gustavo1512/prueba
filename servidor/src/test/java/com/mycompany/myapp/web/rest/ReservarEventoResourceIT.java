package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.ReservarEvento;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.ReservarEventoRepository;
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
 * Integration tests for the {@link ReservarEventoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ReservarEventoResourceIT {

    private static final Instant DEFAULT_FECHA_RESERVACION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_RESERVACION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Double DEFAULT_TOTAL_RESERVACION = 1D;
    private static final Double UPDATED_TOTAL_RESERVACION = 2D;

    private static final String ENTITY_API_URL = "/api/reservar-eventos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ReservarEventoRepository reservarEventoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ReservarEvento reservarEvento;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReservarEvento createEntity(EntityManager em) {
        ReservarEvento reservarEvento = new ReservarEvento()
            .fechaReservacion(DEFAULT_FECHA_RESERVACION)
            .totalReservacion(DEFAULT_TOTAL_RESERVACION);
        return reservarEvento;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReservarEvento createUpdatedEntity(EntityManager em) {
        ReservarEvento reservarEvento = new ReservarEvento()
            .fechaReservacion(UPDATED_FECHA_RESERVACION)
            .totalReservacion(UPDATED_TOTAL_RESERVACION);
        return reservarEvento;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ReservarEvento.class).block();
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
        reservarEvento = createEntity(em);
    }

    @Test
    void createReservarEvento() throws Exception {
        int databaseSizeBeforeCreate = reservarEventoRepository.findAll().collectList().block().size();
        // Create the ReservarEvento
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservarEvento))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ReservarEvento in the database
        List<ReservarEvento> reservarEventoList = reservarEventoRepository.findAll().collectList().block();
        assertThat(reservarEventoList).hasSize(databaseSizeBeforeCreate + 1);
        ReservarEvento testReservarEvento = reservarEventoList.get(reservarEventoList.size() - 1);
        assertThat(testReservarEvento.getFechaReservacion()).isEqualTo(DEFAULT_FECHA_RESERVACION);
        assertThat(testReservarEvento.getTotalReservacion()).isEqualTo(DEFAULT_TOTAL_RESERVACION);
    }

    @Test
    void createReservarEventoWithExistingId() throws Exception {
        // Create the ReservarEvento with an existing ID
        reservarEvento.setId(1L);

        int databaseSizeBeforeCreate = reservarEventoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservarEvento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ReservarEvento in the database
        List<ReservarEvento> reservarEventoList = reservarEventoRepository.findAll().collectList().block();
        assertThat(reservarEventoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllReservarEventosAsStream() {
        // Initialize the database
        reservarEventoRepository.save(reservarEvento).block();

        List<ReservarEvento> reservarEventoList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ReservarEvento.class)
            .getResponseBody()
            .filter(reservarEvento::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(reservarEventoList).isNotNull();
        assertThat(reservarEventoList).hasSize(1);
        ReservarEvento testReservarEvento = reservarEventoList.get(0);
        assertThat(testReservarEvento.getFechaReservacion()).isEqualTo(DEFAULT_FECHA_RESERVACION);
        assertThat(testReservarEvento.getTotalReservacion()).isEqualTo(DEFAULT_TOTAL_RESERVACION);
    }

    @Test
    void getAllReservarEventos() {
        // Initialize the database
        reservarEventoRepository.save(reservarEvento).block();

        // Get all the reservarEventoList
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
            .value(hasItem(reservarEvento.getId().intValue()))
            .jsonPath("$.[*].fechaReservacion")
            .value(hasItem(DEFAULT_FECHA_RESERVACION.toString()))
            .jsonPath("$.[*].totalReservacion")
            .value(hasItem(DEFAULT_TOTAL_RESERVACION.doubleValue()));
    }

    @Test
    void getReservarEvento() {
        // Initialize the database
        reservarEventoRepository.save(reservarEvento).block();

        // Get the reservarEvento
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, reservarEvento.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(reservarEvento.getId().intValue()))
            .jsonPath("$.fechaReservacion")
            .value(is(DEFAULT_FECHA_RESERVACION.toString()))
            .jsonPath("$.totalReservacion")
            .value(is(DEFAULT_TOTAL_RESERVACION.doubleValue()));
    }

    @Test
    void getNonExistingReservarEvento() {
        // Get the reservarEvento
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingReservarEvento() throws Exception {
        // Initialize the database
        reservarEventoRepository.save(reservarEvento).block();

        int databaseSizeBeforeUpdate = reservarEventoRepository.findAll().collectList().block().size();

        // Update the reservarEvento
        ReservarEvento updatedReservarEvento = reservarEventoRepository.findById(reservarEvento.getId()).block();
        updatedReservarEvento.fechaReservacion(UPDATED_FECHA_RESERVACION).totalReservacion(UPDATED_TOTAL_RESERVACION);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedReservarEvento.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedReservarEvento))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ReservarEvento in the database
        List<ReservarEvento> reservarEventoList = reservarEventoRepository.findAll().collectList().block();
        assertThat(reservarEventoList).hasSize(databaseSizeBeforeUpdate);
        ReservarEvento testReservarEvento = reservarEventoList.get(reservarEventoList.size() - 1);
        assertThat(testReservarEvento.getFechaReservacion()).isEqualTo(UPDATED_FECHA_RESERVACION);
        assertThat(testReservarEvento.getTotalReservacion()).isEqualTo(UPDATED_TOTAL_RESERVACION);
    }

    @Test
    void putNonExistingReservarEvento() throws Exception {
        int databaseSizeBeforeUpdate = reservarEventoRepository.findAll().collectList().block().size();
        reservarEvento.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, reservarEvento.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservarEvento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ReservarEvento in the database
        List<ReservarEvento> reservarEventoList = reservarEventoRepository.findAll().collectList().block();
        assertThat(reservarEventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchReservarEvento() throws Exception {
        int databaseSizeBeforeUpdate = reservarEventoRepository.findAll().collectList().block().size();
        reservarEvento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservarEvento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ReservarEvento in the database
        List<ReservarEvento> reservarEventoList = reservarEventoRepository.findAll().collectList().block();
        assertThat(reservarEventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamReservarEvento() throws Exception {
        int databaseSizeBeforeUpdate = reservarEventoRepository.findAll().collectList().block().size();
        reservarEvento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservarEvento))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ReservarEvento in the database
        List<ReservarEvento> reservarEventoList = reservarEventoRepository.findAll().collectList().block();
        assertThat(reservarEventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateReservarEventoWithPatch() throws Exception {
        // Initialize the database
        reservarEventoRepository.save(reservarEvento).block();

        int databaseSizeBeforeUpdate = reservarEventoRepository.findAll().collectList().block().size();

        // Update the reservarEvento using partial update
        ReservarEvento partialUpdatedReservarEvento = new ReservarEvento();
        partialUpdatedReservarEvento.setId(reservarEvento.getId());

        partialUpdatedReservarEvento.fechaReservacion(UPDATED_FECHA_RESERVACION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedReservarEvento.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedReservarEvento))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ReservarEvento in the database
        List<ReservarEvento> reservarEventoList = reservarEventoRepository.findAll().collectList().block();
        assertThat(reservarEventoList).hasSize(databaseSizeBeforeUpdate);
        ReservarEvento testReservarEvento = reservarEventoList.get(reservarEventoList.size() - 1);
        assertThat(testReservarEvento.getFechaReservacion()).isEqualTo(UPDATED_FECHA_RESERVACION);
        assertThat(testReservarEvento.getTotalReservacion()).isEqualTo(DEFAULT_TOTAL_RESERVACION);
    }

    @Test
    void fullUpdateReservarEventoWithPatch() throws Exception {
        // Initialize the database
        reservarEventoRepository.save(reservarEvento).block();

        int databaseSizeBeforeUpdate = reservarEventoRepository.findAll().collectList().block().size();

        // Update the reservarEvento using partial update
        ReservarEvento partialUpdatedReservarEvento = new ReservarEvento();
        partialUpdatedReservarEvento.setId(reservarEvento.getId());

        partialUpdatedReservarEvento.fechaReservacion(UPDATED_FECHA_RESERVACION).totalReservacion(UPDATED_TOTAL_RESERVACION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedReservarEvento.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedReservarEvento))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ReservarEvento in the database
        List<ReservarEvento> reservarEventoList = reservarEventoRepository.findAll().collectList().block();
        assertThat(reservarEventoList).hasSize(databaseSizeBeforeUpdate);
        ReservarEvento testReservarEvento = reservarEventoList.get(reservarEventoList.size() - 1);
        assertThat(testReservarEvento.getFechaReservacion()).isEqualTo(UPDATED_FECHA_RESERVACION);
        assertThat(testReservarEvento.getTotalReservacion()).isEqualTo(UPDATED_TOTAL_RESERVACION);
    }

    @Test
    void patchNonExistingReservarEvento() throws Exception {
        int databaseSizeBeforeUpdate = reservarEventoRepository.findAll().collectList().block().size();
        reservarEvento.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, reservarEvento.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservarEvento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ReservarEvento in the database
        List<ReservarEvento> reservarEventoList = reservarEventoRepository.findAll().collectList().block();
        assertThat(reservarEventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchReservarEvento() throws Exception {
        int databaseSizeBeforeUpdate = reservarEventoRepository.findAll().collectList().block().size();
        reservarEvento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservarEvento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ReservarEvento in the database
        List<ReservarEvento> reservarEventoList = reservarEventoRepository.findAll().collectList().block();
        assertThat(reservarEventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamReservarEvento() throws Exception {
        int databaseSizeBeforeUpdate = reservarEventoRepository.findAll().collectList().block().size();
        reservarEvento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservarEvento))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ReservarEvento in the database
        List<ReservarEvento> reservarEventoList = reservarEventoRepository.findAll().collectList().block();
        assertThat(reservarEventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteReservarEvento() {
        // Initialize the database
        reservarEventoRepository.save(reservarEvento).block();

        int databaseSizeBeforeDelete = reservarEventoRepository.findAll().collectList().block().size();

        // Delete the reservarEvento
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, reservarEvento.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ReservarEvento> reservarEventoList = reservarEventoRepository.findAll().collectList().block();
        assertThat(reservarEventoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
