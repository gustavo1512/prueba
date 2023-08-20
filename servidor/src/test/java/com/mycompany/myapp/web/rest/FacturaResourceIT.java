package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Factura;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.FacturaRepository;
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
 * Integration tests for the {@link FacturaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class FacturaResourceIT {

    private static final String DEFAULT_METODO_PAGO = "AAAAAAAAAA";
    private static final String UPDATED_METODO_PAGO = "BBBBBBBBBB";

    private static final Double DEFAULT_SUBTOTAL = 1D;
    private static final Double UPDATED_SUBTOTAL = 2D;

    private static final Double DEFAULT_MONTO_TOTAL = 1D;
    private static final Double UPDATED_MONTO_TOTAL = 2D;

    private static final Double DEFAULT_IMPUESTO = 1D;
    private static final Double UPDATED_IMPUESTO = 2D;

    private static final String ENTITY_API_URL = "/api/facturas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Factura factura;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Factura createEntity(EntityManager em) {
        Factura factura = new Factura()
            .metodoPago(DEFAULT_METODO_PAGO)
            .subtotal(DEFAULT_SUBTOTAL)
            .montoTotal(DEFAULT_MONTO_TOTAL)
            .impuesto(DEFAULT_IMPUESTO);
        return factura;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Factura createUpdatedEntity(EntityManager em) {
        Factura factura = new Factura()
            .metodoPago(UPDATED_METODO_PAGO)
            .subtotal(UPDATED_SUBTOTAL)
            .montoTotal(UPDATED_MONTO_TOTAL)
            .impuesto(UPDATED_IMPUESTO);
        return factura;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Factura.class).block();
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
        factura = createEntity(em);
    }

    @Test
    void createFactura() throws Exception {
        int databaseSizeBeforeCreate = facturaRepository.findAll().collectList().block().size();
        // Create the Factura
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(factura))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll().collectList().block();
        assertThat(facturaList).hasSize(databaseSizeBeforeCreate + 1);
        Factura testFactura = facturaList.get(facturaList.size() - 1);
        assertThat(testFactura.getMetodoPago()).isEqualTo(DEFAULT_METODO_PAGO);
        assertThat(testFactura.getSubtotal()).isEqualTo(DEFAULT_SUBTOTAL);
        assertThat(testFactura.getMontoTotal()).isEqualTo(DEFAULT_MONTO_TOTAL);
        assertThat(testFactura.getImpuesto()).isEqualTo(DEFAULT_IMPUESTO);
    }

    @Test
    void createFacturaWithExistingId() throws Exception {
        // Create the Factura with an existing ID
        factura.setId(1L);

        int databaseSizeBeforeCreate = facturaRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(factura))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll().collectList().block();
        assertThat(facturaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllFacturasAsStream() {
        // Initialize the database
        facturaRepository.save(factura).block();

        List<Factura> facturaList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Factura.class)
            .getResponseBody()
            .filter(factura::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(facturaList).isNotNull();
        assertThat(facturaList).hasSize(1);
        Factura testFactura = facturaList.get(0);
        assertThat(testFactura.getMetodoPago()).isEqualTo(DEFAULT_METODO_PAGO);
        assertThat(testFactura.getSubtotal()).isEqualTo(DEFAULT_SUBTOTAL);
        assertThat(testFactura.getMontoTotal()).isEqualTo(DEFAULT_MONTO_TOTAL);
        assertThat(testFactura.getImpuesto()).isEqualTo(DEFAULT_IMPUESTO);
    }

    @Test
    void getAllFacturas() {
        // Initialize the database
        facturaRepository.save(factura).block();

        // Get all the facturaList
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
            .value(hasItem(factura.getId().intValue()))
            .jsonPath("$.[*].metodoPago")
            .value(hasItem(DEFAULT_METODO_PAGO))
            .jsonPath("$.[*].subtotal")
            .value(hasItem(DEFAULT_SUBTOTAL.doubleValue()))
            .jsonPath("$.[*].montoTotal")
            .value(hasItem(DEFAULT_MONTO_TOTAL.doubleValue()))
            .jsonPath("$.[*].impuesto")
            .value(hasItem(DEFAULT_IMPUESTO.doubleValue()));
    }

    @Test
    void getFactura() {
        // Initialize the database
        facturaRepository.save(factura).block();

        // Get the factura
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, factura.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(factura.getId().intValue()))
            .jsonPath("$.metodoPago")
            .value(is(DEFAULT_METODO_PAGO))
            .jsonPath("$.subtotal")
            .value(is(DEFAULT_SUBTOTAL.doubleValue()))
            .jsonPath("$.montoTotal")
            .value(is(DEFAULT_MONTO_TOTAL.doubleValue()))
            .jsonPath("$.impuesto")
            .value(is(DEFAULT_IMPUESTO.doubleValue()));
    }

    @Test
    void getNonExistingFactura() {
        // Get the factura
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingFactura() throws Exception {
        // Initialize the database
        facturaRepository.save(factura).block();

        int databaseSizeBeforeUpdate = facturaRepository.findAll().collectList().block().size();

        // Update the factura
        Factura updatedFactura = facturaRepository.findById(factura.getId()).block();
        updatedFactura
            .metodoPago(UPDATED_METODO_PAGO)
            .subtotal(UPDATED_SUBTOTAL)
            .montoTotal(UPDATED_MONTO_TOTAL)
            .impuesto(UPDATED_IMPUESTO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedFactura.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedFactura))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll().collectList().block();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
        Factura testFactura = facturaList.get(facturaList.size() - 1);
        assertThat(testFactura.getMetodoPago()).isEqualTo(UPDATED_METODO_PAGO);
        assertThat(testFactura.getSubtotal()).isEqualTo(UPDATED_SUBTOTAL);
        assertThat(testFactura.getMontoTotal()).isEqualTo(UPDATED_MONTO_TOTAL);
        assertThat(testFactura.getImpuesto()).isEqualTo(UPDATED_IMPUESTO);
    }

    @Test
    void putNonExistingFactura() throws Exception {
        int databaseSizeBeforeUpdate = facturaRepository.findAll().collectList().block().size();
        factura.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, factura.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(factura))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll().collectList().block();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchFactura() throws Exception {
        int databaseSizeBeforeUpdate = facturaRepository.findAll().collectList().block().size();
        factura.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(factura))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll().collectList().block();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamFactura() throws Exception {
        int databaseSizeBeforeUpdate = facturaRepository.findAll().collectList().block().size();
        factura.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(factura))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll().collectList().block();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateFacturaWithPatch() throws Exception {
        // Initialize the database
        facturaRepository.save(factura).block();

        int databaseSizeBeforeUpdate = facturaRepository.findAll().collectList().block().size();

        // Update the factura using partial update
        Factura partialUpdatedFactura = new Factura();
        partialUpdatedFactura.setId(factura.getId());

        partialUpdatedFactura.subtotal(UPDATED_SUBTOTAL).montoTotal(UPDATED_MONTO_TOTAL).impuesto(UPDATED_IMPUESTO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFactura.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFactura))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll().collectList().block();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
        Factura testFactura = facturaList.get(facturaList.size() - 1);
        assertThat(testFactura.getMetodoPago()).isEqualTo(DEFAULT_METODO_PAGO);
        assertThat(testFactura.getSubtotal()).isEqualTo(UPDATED_SUBTOTAL);
        assertThat(testFactura.getMontoTotal()).isEqualTo(UPDATED_MONTO_TOTAL);
        assertThat(testFactura.getImpuesto()).isEqualTo(UPDATED_IMPUESTO);
    }

    @Test
    void fullUpdateFacturaWithPatch() throws Exception {
        // Initialize the database
        facturaRepository.save(factura).block();

        int databaseSizeBeforeUpdate = facturaRepository.findAll().collectList().block().size();

        // Update the factura using partial update
        Factura partialUpdatedFactura = new Factura();
        partialUpdatedFactura.setId(factura.getId());

        partialUpdatedFactura
            .metodoPago(UPDATED_METODO_PAGO)
            .subtotal(UPDATED_SUBTOTAL)
            .montoTotal(UPDATED_MONTO_TOTAL)
            .impuesto(UPDATED_IMPUESTO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFactura.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFactura))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll().collectList().block();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
        Factura testFactura = facturaList.get(facturaList.size() - 1);
        assertThat(testFactura.getMetodoPago()).isEqualTo(UPDATED_METODO_PAGO);
        assertThat(testFactura.getSubtotal()).isEqualTo(UPDATED_SUBTOTAL);
        assertThat(testFactura.getMontoTotal()).isEqualTo(UPDATED_MONTO_TOTAL);
        assertThat(testFactura.getImpuesto()).isEqualTo(UPDATED_IMPUESTO);
    }

    @Test
    void patchNonExistingFactura() throws Exception {
        int databaseSizeBeforeUpdate = facturaRepository.findAll().collectList().block().size();
        factura.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, factura.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(factura))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll().collectList().block();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchFactura() throws Exception {
        int databaseSizeBeforeUpdate = facturaRepository.findAll().collectList().block().size();
        factura.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(factura))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll().collectList().block();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamFactura() throws Exception {
        int databaseSizeBeforeUpdate = facturaRepository.findAll().collectList().block().size();
        factura.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(factura))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll().collectList().block();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteFactura() {
        // Initialize the database
        facturaRepository.save(factura).block();

        int databaseSizeBeforeDelete = facturaRepository.findAll().collectList().block().size();

        // Delete the factura
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, factura.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Factura> facturaList = facturaRepository.findAll().collectList().block();
        assertThat(facturaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
