package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.DetalleFactura;
import com.mycompany.myapp.repository.DetalleFacturaRepository;
import com.mycompany.myapp.repository.EntityManager;
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
 * Integration tests for the {@link DetalleFacturaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DetalleFacturaResourceIT {

    private static final Long DEFAULT_FECHA_EMITIDO = 1L;
    private static final Long UPDATED_FECHA_EMITIDO = 2L;

    private static final String ENTITY_API_URL = "/api/detalle-facturas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DetalleFacturaRepository detalleFacturaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private DetalleFactura detalleFactura;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DetalleFactura createEntity(EntityManager em) {
        DetalleFactura detalleFactura = new DetalleFactura().fechaEmitido(DEFAULT_FECHA_EMITIDO);
        return detalleFactura;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DetalleFactura createUpdatedEntity(EntityManager em) {
        DetalleFactura detalleFactura = new DetalleFactura().fechaEmitido(UPDATED_FECHA_EMITIDO);
        return detalleFactura;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(DetalleFactura.class).block();
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
        detalleFactura = createEntity(em);
    }

    @Test
    void createDetalleFactura() throws Exception {
        int databaseSizeBeforeCreate = detalleFacturaRepository.findAll().collectList().block().size();
        // Create the DetalleFactura
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(detalleFactura))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the DetalleFactura in the database
        List<DetalleFactura> detalleFacturaList = detalleFacturaRepository.findAll().collectList().block();
        assertThat(detalleFacturaList).hasSize(databaseSizeBeforeCreate + 1);
        DetalleFactura testDetalleFactura = detalleFacturaList.get(detalleFacturaList.size() - 1);
        assertThat(testDetalleFactura.getFechaEmitido()).isEqualTo(DEFAULT_FECHA_EMITIDO);
    }

    @Test
    void createDetalleFacturaWithExistingId() throws Exception {
        // Create the DetalleFactura with an existing ID
        detalleFactura.setId(1L);

        int databaseSizeBeforeCreate = detalleFacturaRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(detalleFactura))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DetalleFactura in the database
        List<DetalleFactura> detalleFacturaList = detalleFacturaRepository.findAll().collectList().block();
        assertThat(detalleFacturaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllDetalleFacturasAsStream() {
        // Initialize the database
        detalleFacturaRepository.save(detalleFactura).block();

        List<DetalleFactura> detalleFacturaList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(DetalleFactura.class)
            .getResponseBody()
            .filter(detalleFactura::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(detalleFacturaList).isNotNull();
        assertThat(detalleFacturaList).hasSize(1);
        DetalleFactura testDetalleFactura = detalleFacturaList.get(0);
        assertThat(testDetalleFactura.getFechaEmitido()).isEqualTo(DEFAULT_FECHA_EMITIDO);
    }

    @Test
    void getAllDetalleFacturas() {
        // Initialize the database
        detalleFacturaRepository.save(detalleFactura).block();

        // Get all the detalleFacturaList
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
            .value(hasItem(detalleFactura.getId().intValue()))
            .jsonPath("$.[*].fechaEmitido")
            .value(hasItem(DEFAULT_FECHA_EMITIDO.intValue()));
    }

    @Test
    void getDetalleFactura() {
        // Initialize the database
        detalleFacturaRepository.save(detalleFactura).block();

        // Get the detalleFactura
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, detalleFactura.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(detalleFactura.getId().intValue()))
            .jsonPath("$.fechaEmitido")
            .value(is(DEFAULT_FECHA_EMITIDO.intValue()));
    }

    @Test
    void getNonExistingDetalleFactura() {
        // Get the detalleFactura
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDetalleFactura() throws Exception {
        // Initialize the database
        detalleFacturaRepository.save(detalleFactura).block();

        int databaseSizeBeforeUpdate = detalleFacturaRepository.findAll().collectList().block().size();

        // Update the detalleFactura
        DetalleFactura updatedDetalleFactura = detalleFacturaRepository.findById(detalleFactura.getId()).block();
        updatedDetalleFactura.fechaEmitido(UPDATED_FECHA_EMITIDO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedDetalleFactura.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedDetalleFactura))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DetalleFactura in the database
        List<DetalleFactura> detalleFacturaList = detalleFacturaRepository.findAll().collectList().block();
        assertThat(detalleFacturaList).hasSize(databaseSizeBeforeUpdate);
        DetalleFactura testDetalleFactura = detalleFacturaList.get(detalleFacturaList.size() - 1);
        assertThat(testDetalleFactura.getFechaEmitido()).isEqualTo(UPDATED_FECHA_EMITIDO);
    }

    @Test
    void putNonExistingDetalleFactura() throws Exception {
        int databaseSizeBeforeUpdate = detalleFacturaRepository.findAll().collectList().block().size();
        detalleFactura.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, detalleFactura.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(detalleFactura))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DetalleFactura in the database
        List<DetalleFactura> detalleFacturaList = detalleFacturaRepository.findAll().collectList().block();
        assertThat(detalleFacturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDetalleFactura() throws Exception {
        int databaseSizeBeforeUpdate = detalleFacturaRepository.findAll().collectList().block().size();
        detalleFactura.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(detalleFactura))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DetalleFactura in the database
        List<DetalleFactura> detalleFacturaList = detalleFacturaRepository.findAll().collectList().block();
        assertThat(detalleFacturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDetalleFactura() throws Exception {
        int databaseSizeBeforeUpdate = detalleFacturaRepository.findAll().collectList().block().size();
        detalleFactura.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(detalleFactura))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the DetalleFactura in the database
        List<DetalleFactura> detalleFacturaList = detalleFacturaRepository.findAll().collectList().block();
        assertThat(detalleFacturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDetalleFacturaWithPatch() throws Exception {
        // Initialize the database
        detalleFacturaRepository.save(detalleFactura).block();

        int databaseSizeBeforeUpdate = detalleFacturaRepository.findAll().collectList().block().size();

        // Update the detalleFactura using partial update
        DetalleFactura partialUpdatedDetalleFactura = new DetalleFactura();
        partialUpdatedDetalleFactura.setId(detalleFactura.getId());

        partialUpdatedDetalleFactura.fechaEmitido(UPDATED_FECHA_EMITIDO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDetalleFactura.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDetalleFactura))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DetalleFactura in the database
        List<DetalleFactura> detalleFacturaList = detalleFacturaRepository.findAll().collectList().block();
        assertThat(detalleFacturaList).hasSize(databaseSizeBeforeUpdate);
        DetalleFactura testDetalleFactura = detalleFacturaList.get(detalleFacturaList.size() - 1);
        assertThat(testDetalleFactura.getFechaEmitido()).isEqualTo(UPDATED_FECHA_EMITIDO);
    }

    @Test
    void fullUpdateDetalleFacturaWithPatch() throws Exception {
        // Initialize the database
        detalleFacturaRepository.save(detalleFactura).block();

        int databaseSizeBeforeUpdate = detalleFacturaRepository.findAll().collectList().block().size();

        // Update the detalleFactura using partial update
        DetalleFactura partialUpdatedDetalleFactura = new DetalleFactura();
        partialUpdatedDetalleFactura.setId(detalleFactura.getId());

        partialUpdatedDetalleFactura.fechaEmitido(UPDATED_FECHA_EMITIDO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDetalleFactura.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDetalleFactura))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DetalleFactura in the database
        List<DetalleFactura> detalleFacturaList = detalleFacturaRepository.findAll().collectList().block();
        assertThat(detalleFacturaList).hasSize(databaseSizeBeforeUpdate);
        DetalleFactura testDetalleFactura = detalleFacturaList.get(detalleFacturaList.size() - 1);
        assertThat(testDetalleFactura.getFechaEmitido()).isEqualTo(UPDATED_FECHA_EMITIDO);
    }

    @Test
    void patchNonExistingDetalleFactura() throws Exception {
        int databaseSizeBeforeUpdate = detalleFacturaRepository.findAll().collectList().block().size();
        detalleFactura.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, detalleFactura.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(detalleFactura))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DetalleFactura in the database
        List<DetalleFactura> detalleFacturaList = detalleFacturaRepository.findAll().collectList().block();
        assertThat(detalleFacturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDetalleFactura() throws Exception {
        int databaseSizeBeforeUpdate = detalleFacturaRepository.findAll().collectList().block().size();
        detalleFactura.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(detalleFactura))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DetalleFactura in the database
        List<DetalleFactura> detalleFacturaList = detalleFacturaRepository.findAll().collectList().block();
        assertThat(detalleFacturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDetalleFactura() throws Exception {
        int databaseSizeBeforeUpdate = detalleFacturaRepository.findAll().collectList().block().size();
        detalleFactura.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(detalleFactura))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the DetalleFactura in the database
        List<DetalleFactura> detalleFacturaList = detalleFacturaRepository.findAll().collectList().block();
        assertThat(detalleFacturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDetalleFactura() {
        // Initialize the database
        detalleFacturaRepository.save(detalleFactura).block();

        int databaseSizeBeforeDelete = detalleFacturaRepository.findAll().collectList().block().size();

        // Delete the detalleFactura
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, detalleFactura.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<DetalleFactura> detalleFacturaList = detalleFacturaRepository.findAll().collectList().block();
        assertThat(detalleFacturaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
