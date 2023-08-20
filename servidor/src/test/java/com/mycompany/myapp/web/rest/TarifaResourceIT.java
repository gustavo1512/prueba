package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Tarifa;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.TarifaRepository;
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
 * Integration tests for the {@link TarifaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TarifaResourceIT {

    private static final String DEFAULT_TIPO_TARIFA = "AAAAAAAAAA";
    private static final String UPDATED_TIPO_TARIFA = "BBBBBBBBBB";

    private static final Double DEFAULT_TARIFA_ADULTO = 1D;
    private static final Double UPDATED_TARIFA_ADULTO = 2D;

    private static final Double DEFAULT_TARIFA_MENOR = 1D;
    private static final Double UPDATED_TARIFA_MENOR = 2D;

    private static final String ENTITY_API_URL = "/api/tarifas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TarifaRepository tarifaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Tarifa tarifa;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tarifa createEntity(EntityManager em) {
        Tarifa tarifa = new Tarifa().tipoTarifa(DEFAULT_TIPO_TARIFA).tarifaAdulto(DEFAULT_TARIFA_ADULTO).tarifaMenor(DEFAULT_TARIFA_MENOR);
        return tarifa;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tarifa createUpdatedEntity(EntityManager em) {
        Tarifa tarifa = new Tarifa().tipoTarifa(UPDATED_TIPO_TARIFA).tarifaAdulto(UPDATED_TARIFA_ADULTO).tarifaMenor(UPDATED_TARIFA_MENOR);
        return tarifa;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Tarifa.class).block();
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
        tarifa = createEntity(em);
    }

    @Test
    void createTarifa() throws Exception {
        int databaseSizeBeforeCreate = tarifaRepository.findAll().collectList().block().size();
        // Create the Tarifa
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tarifa))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Tarifa in the database
        List<Tarifa> tarifaList = tarifaRepository.findAll().collectList().block();
        assertThat(tarifaList).hasSize(databaseSizeBeforeCreate + 1);
        Tarifa testTarifa = tarifaList.get(tarifaList.size() - 1);
        assertThat(testTarifa.getTipoTarifa()).isEqualTo(DEFAULT_TIPO_TARIFA);
        assertThat(testTarifa.getTarifaAdulto()).isEqualTo(DEFAULT_TARIFA_ADULTO);
        assertThat(testTarifa.getTarifaMenor()).isEqualTo(DEFAULT_TARIFA_MENOR);
    }

    @Test
    void createTarifaWithExistingId() throws Exception {
        // Create the Tarifa with an existing ID
        tarifa.setId(1L);

        int databaseSizeBeforeCreate = tarifaRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tarifa))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tarifa in the database
        List<Tarifa> tarifaList = tarifaRepository.findAll().collectList().block();
        assertThat(tarifaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllTarifasAsStream() {
        // Initialize the database
        tarifaRepository.save(tarifa).block();

        List<Tarifa> tarifaList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Tarifa.class)
            .getResponseBody()
            .filter(tarifa::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(tarifaList).isNotNull();
        assertThat(tarifaList).hasSize(1);
        Tarifa testTarifa = tarifaList.get(0);
        assertThat(testTarifa.getTipoTarifa()).isEqualTo(DEFAULT_TIPO_TARIFA);
        assertThat(testTarifa.getTarifaAdulto()).isEqualTo(DEFAULT_TARIFA_ADULTO);
        assertThat(testTarifa.getTarifaMenor()).isEqualTo(DEFAULT_TARIFA_MENOR);
    }

    @Test
    void getAllTarifas() {
        // Initialize the database
        tarifaRepository.save(tarifa).block();

        // Get all the tarifaList
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
            .value(hasItem(tarifa.getId().intValue()))
            .jsonPath("$.[*].tipoTarifa")
            .value(hasItem(DEFAULT_TIPO_TARIFA))
            .jsonPath("$.[*].tarifaAdulto")
            .value(hasItem(DEFAULT_TARIFA_ADULTO.doubleValue()))
            .jsonPath("$.[*].tarifaMenor")
            .value(hasItem(DEFAULT_TARIFA_MENOR.doubleValue()));
    }

    @Test
    void getTarifa() {
        // Initialize the database
        tarifaRepository.save(tarifa).block();

        // Get the tarifa
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, tarifa.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(tarifa.getId().intValue()))
            .jsonPath("$.tipoTarifa")
            .value(is(DEFAULT_TIPO_TARIFA))
            .jsonPath("$.tarifaAdulto")
            .value(is(DEFAULT_TARIFA_ADULTO.doubleValue()))
            .jsonPath("$.tarifaMenor")
            .value(is(DEFAULT_TARIFA_MENOR.doubleValue()));
    }

    @Test
    void getNonExistingTarifa() {
        // Get the tarifa
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTarifa() throws Exception {
        // Initialize the database
        tarifaRepository.save(tarifa).block();

        int databaseSizeBeforeUpdate = tarifaRepository.findAll().collectList().block().size();

        // Update the tarifa
        Tarifa updatedTarifa = tarifaRepository.findById(tarifa.getId()).block();
        updatedTarifa.tipoTarifa(UPDATED_TIPO_TARIFA).tarifaAdulto(UPDATED_TARIFA_ADULTO).tarifaMenor(UPDATED_TARIFA_MENOR);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTarifa.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedTarifa))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tarifa in the database
        List<Tarifa> tarifaList = tarifaRepository.findAll().collectList().block();
        assertThat(tarifaList).hasSize(databaseSizeBeforeUpdate);
        Tarifa testTarifa = tarifaList.get(tarifaList.size() - 1);
        assertThat(testTarifa.getTipoTarifa()).isEqualTo(UPDATED_TIPO_TARIFA);
        assertThat(testTarifa.getTarifaAdulto()).isEqualTo(UPDATED_TARIFA_ADULTO);
        assertThat(testTarifa.getTarifaMenor()).isEqualTo(UPDATED_TARIFA_MENOR);
    }

    @Test
    void putNonExistingTarifa() throws Exception {
        int databaseSizeBeforeUpdate = tarifaRepository.findAll().collectList().block().size();
        tarifa.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tarifa.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tarifa))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tarifa in the database
        List<Tarifa> tarifaList = tarifaRepository.findAll().collectList().block();
        assertThat(tarifaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTarifa() throws Exception {
        int databaseSizeBeforeUpdate = tarifaRepository.findAll().collectList().block().size();
        tarifa.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tarifa))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tarifa in the database
        List<Tarifa> tarifaList = tarifaRepository.findAll().collectList().block();
        assertThat(tarifaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTarifa() throws Exception {
        int databaseSizeBeforeUpdate = tarifaRepository.findAll().collectList().block().size();
        tarifa.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tarifa))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Tarifa in the database
        List<Tarifa> tarifaList = tarifaRepository.findAll().collectList().block();
        assertThat(tarifaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTarifaWithPatch() throws Exception {
        // Initialize the database
        tarifaRepository.save(tarifa).block();

        int databaseSizeBeforeUpdate = tarifaRepository.findAll().collectList().block().size();

        // Update the tarifa using partial update
        Tarifa partialUpdatedTarifa = new Tarifa();
        partialUpdatedTarifa.setId(tarifa.getId());

        partialUpdatedTarifa.tipoTarifa(UPDATED_TIPO_TARIFA).tarifaAdulto(UPDATED_TARIFA_ADULTO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTarifa.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTarifa))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tarifa in the database
        List<Tarifa> tarifaList = tarifaRepository.findAll().collectList().block();
        assertThat(tarifaList).hasSize(databaseSizeBeforeUpdate);
        Tarifa testTarifa = tarifaList.get(tarifaList.size() - 1);
        assertThat(testTarifa.getTipoTarifa()).isEqualTo(UPDATED_TIPO_TARIFA);
        assertThat(testTarifa.getTarifaAdulto()).isEqualTo(UPDATED_TARIFA_ADULTO);
        assertThat(testTarifa.getTarifaMenor()).isEqualTo(DEFAULT_TARIFA_MENOR);
    }

    @Test
    void fullUpdateTarifaWithPatch() throws Exception {
        // Initialize the database
        tarifaRepository.save(tarifa).block();

        int databaseSizeBeforeUpdate = tarifaRepository.findAll().collectList().block().size();

        // Update the tarifa using partial update
        Tarifa partialUpdatedTarifa = new Tarifa();
        partialUpdatedTarifa.setId(tarifa.getId());

        partialUpdatedTarifa.tipoTarifa(UPDATED_TIPO_TARIFA).tarifaAdulto(UPDATED_TARIFA_ADULTO).tarifaMenor(UPDATED_TARIFA_MENOR);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTarifa.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTarifa))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tarifa in the database
        List<Tarifa> tarifaList = tarifaRepository.findAll().collectList().block();
        assertThat(tarifaList).hasSize(databaseSizeBeforeUpdate);
        Tarifa testTarifa = tarifaList.get(tarifaList.size() - 1);
        assertThat(testTarifa.getTipoTarifa()).isEqualTo(UPDATED_TIPO_TARIFA);
        assertThat(testTarifa.getTarifaAdulto()).isEqualTo(UPDATED_TARIFA_ADULTO);
        assertThat(testTarifa.getTarifaMenor()).isEqualTo(UPDATED_TARIFA_MENOR);
    }

    @Test
    void patchNonExistingTarifa() throws Exception {
        int databaseSizeBeforeUpdate = tarifaRepository.findAll().collectList().block().size();
        tarifa.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, tarifa.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tarifa))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tarifa in the database
        List<Tarifa> tarifaList = tarifaRepository.findAll().collectList().block();
        assertThat(tarifaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTarifa() throws Exception {
        int databaseSizeBeforeUpdate = tarifaRepository.findAll().collectList().block().size();
        tarifa.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tarifa))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tarifa in the database
        List<Tarifa> tarifaList = tarifaRepository.findAll().collectList().block();
        assertThat(tarifaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTarifa() throws Exception {
        int databaseSizeBeforeUpdate = tarifaRepository.findAll().collectList().block().size();
        tarifa.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tarifa))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Tarifa in the database
        List<Tarifa> tarifaList = tarifaRepository.findAll().collectList().block();
        assertThat(tarifaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTarifa() {
        // Initialize the database
        tarifaRepository.save(tarifa).block();

        int databaseSizeBeforeDelete = tarifaRepository.findAll().collectList().block().size();

        // Delete the tarifa
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, tarifa.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Tarifa> tarifaList = tarifaRepository.findAll().collectList().block();
        assertThat(tarifaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
