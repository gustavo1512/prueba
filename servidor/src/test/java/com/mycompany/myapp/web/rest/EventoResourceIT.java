package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Evento;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.EventoRepository;
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
 * Integration tests for the {@link EventoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EventoResourceIT {

    private static final String DEFAULT_NOMBRE_EVENTO = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE_EVENTO = "BBBBBBBBBB";

    private static final Instant DEFAULT_FECHA_HORA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_HORA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_CAPACIDAD_ADULTO = 1L;
    private static final Long UPDATED_CAPACIDAD_ADULTO = 2L;

    private static final Long DEFAULT_CAPACIDAD_MENOR = 1L;
    private static final Long UPDATED_CAPACIDAD_MENOR = 2L;

    private static final String ENTITY_API_URL = "/api/eventos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Evento evento;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Evento createEntity(EntityManager em) {
        Evento evento = new Evento()
            .nombreEvento(DEFAULT_NOMBRE_EVENTO)
            .fechaHora(DEFAULT_FECHA_HORA)
            .capacidadAdulto(DEFAULT_CAPACIDAD_ADULTO)
            .capacidadMenor(DEFAULT_CAPACIDAD_MENOR);
        return evento;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Evento createUpdatedEntity(EntityManager em) {
        Evento evento = new Evento()
            .nombreEvento(UPDATED_NOMBRE_EVENTO)
            .fechaHora(UPDATED_FECHA_HORA)
            .capacidadAdulto(UPDATED_CAPACIDAD_ADULTO)
            .capacidadMenor(UPDATED_CAPACIDAD_MENOR);
        return evento;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Evento.class).block();
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
        evento = createEntity(em);
    }

    @Test
    void createEvento() throws Exception {
        int databaseSizeBeforeCreate = eventoRepository.findAll().collectList().block().size();
        // Create the Evento
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(evento))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Evento in the database
        List<Evento> eventoList = eventoRepository.findAll().collectList().block();
        assertThat(eventoList).hasSize(databaseSizeBeforeCreate + 1);
        Evento testEvento = eventoList.get(eventoList.size() - 1);
        assertThat(testEvento.getNombreEvento()).isEqualTo(DEFAULT_NOMBRE_EVENTO);
        assertThat(testEvento.getFechaHora()).isEqualTo(DEFAULT_FECHA_HORA);
        assertThat(testEvento.getCapacidadAdulto()).isEqualTo(DEFAULT_CAPACIDAD_ADULTO);
        assertThat(testEvento.getCapacidadMenor()).isEqualTo(DEFAULT_CAPACIDAD_MENOR);
    }

    @Test
    void createEventoWithExistingId() throws Exception {
        // Create the Evento with an existing ID
        evento.setId(1L);

        int databaseSizeBeforeCreate = eventoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(evento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Evento in the database
        List<Evento> eventoList = eventoRepository.findAll().collectList().block();
        assertThat(eventoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllEventosAsStream() {
        // Initialize the database
        eventoRepository.save(evento).block();

        List<Evento> eventoList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Evento.class)
            .getResponseBody()
            .filter(evento::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(eventoList).isNotNull();
        assertThat(eventoList).hasSize(1);
        Evento testEvento = eventoList.get(0);
        assertThat(testEvento.getNombreEvento()).isEqualTo(DEFAULT_NOMBRE_EVENTO);
        assertThat(testEvento.getFechaHora()).isEqualTo(DEFAULT_FECHA_HORA);
        assertThat(testEvento.getCapacidadAdulto()).isEqualTo(DEFAULT_CAPACIDAD_ADULTO);
        assertThat(testEvento.getCapacidadMenor()).isEqualTo(DEFAULT_CAPACIDAD_MENOR);
    }

    @Test
    void getAllEventos() {
        // Initialize the database
        eventoRepository.save(evento).block();

        // Get all the eventoList
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
            .value(hasItem(evento.getId().intValue()))
            .jsonPath("$.[*].nombreEvento")
            .value(hasItem(DEFAULT_NOMBRE_EVENTO))
            .jsonPath("$.[*].fechaHora")
            .value(hasItem(DEFAULT_FECHA_HORA.toString()))
            .jsonPath("$.[*].capacidadAdulto")
            .value(hasItem(DEFAULT_CAPACIDAD_ADULTO.intValue()))
            .jsonPath("$.[*].capacidadMenor")
            .value(hasItem(DEFAULT_CAPACIDAD_MENOR.intValue()));
    }

    @Test
    void getEvento() {
        // Initialize the database
        eventoRepository.save(evento).block();

        // Get the evento
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, evento.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(evento.getId().intValue()))
            .jsonPath("$.nombreEvento")
            .value(is(DEFAULT_NOMBRE_EVENTO))
            .jsonPath("$.fechaHora")
            .value(is(DEFAULT_FECHA_HORA.toString()))
            .jsonPath("$.capacidadAdulto")
            .value(is(DEFAULT_CAPACIDAD_ADULTO.intValue()))
            .jsonPath("$.capacidadMenor")
            .value(is(DEFAULT_CAPACIDAD_MENOR.intValue()));
    }

    @Test
    void getNonExistingEvento() {
        // Get the evento
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEvento() throws Exception {
        // Initialize the database
        eventoRepository.save(evento).block();

        int databaseSizeBeforeUpdate = eventoRepository.findAll().collectList().block().size();

        // Update the evento
        Evento updatedEvento = eventoRepository.findById(evento.getId()).block();
        updatedEvento
            .nombreEvento(UPDATED_NOMBRE_EVENTO)
            .fechaHora(UPDATED_FECHA_HORA)
            .capacidadAdulto(UPDATED_CAPACIDAD_ADULTO)
            .capacidadMenor(UPDATED_CAPACIDAD_MENOR);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedEvento.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedEvento))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Evento in the database
        List<Evento> eventoList = eventoRepository.findAll().collectList().block();
        assertThat(eventoList).hasSize(databaseSizeBeforeUpdate);
        Evento testEvento = eventoList.get(eventoList.size() - 1);
        assertThat(testEvento.getNombreEvento()).isEqualTo(UPDATED_NOMBRE_EVENTO);
        assertThat(testEvento.getFechaHora()).isEqualTo(UPDATED_FECHA_HORA);
        assertThat(testEvento.getCapacidadAdulto()).isEqualTo(UPDATED_CAPACIDAD_ADULTO);
        assertThat(testEvento.getCapacidadMenor()).isEqualTo(UPDATED_CAPACIDAD_MENOR);
    }

    @Test
    void putNonExistingEvento() throws Exception {
        int databaseSizeBeforeUpdate = eventoRepository.findAll().collectList().block().size();
        evento.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, evento.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(evento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Evento in the database
        List<Evento> eventoList = eventoRepository.findAll().collectList().block();
        assertThat(eventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEvento() throws Exception {
        int databaseSizeBeforeUpdate = eventoRepository.findAll().collectList().block().size();
        evento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(evento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Evento in the database
        List<Evento> eventoList = eventoRepository.findAll().collectList().block();
        assertThat(eventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEvento() throws Exception {
        int databaseSizeBeforeUpdate = eventoRepository.findAll().collectList().block().size();
        evento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(evento))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Evento in the database
        List<Evento> eventoList = eventoRepository.findAll().collectList().block();
        assertThat(eventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEventoWithPatch() throws Exception {
        // Initialize the database
        eventoRepository.save(evento).block();

        int databaseSizeBeforeUpdate = eventoRepository.findAll().collectList().block().size();

        // Update the evento using partial update
        Evento partialUpdatedEvento = new Evento();
        partialUpdatedEvento.setId(evento.getId());

        partialUpdatedEvento.nombreEvento(UPDATED_NOMBRE_EVENTO).fechaHora(UPDATED_FECHA_HORA);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEvento.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEvento))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Evento in the database
        List<Evento> eventoList = eventoRepository.findAll().collectList().block();
        assertThat(eventoList).hasSize(databaseSizeBeforeUpdate);
        Evento testEvento = eventoList.get(eventoList.size() - 1);
        assertThat(testEvento.getNombreEvento()).isEqualTo(UPDATED_NOMBRE_EVENTO);
        assertThat(testEvento.getFechaHora()).isEqualTo(UPDATED_FECHA_HORA);
        assertThat(testEvento.getCapacidadAdulto()).isEqualTo(DEFAULT_CAPACIDAD_ADULTO);
        assertThat(testEvento.getCapacidadMenor()).isEqualTo(DEFAULT_CAPACIDAD_MENOR);
    }

    @Test
    void fullUpdateEventoWithPatch() throws Exception {
        // Initialize the database
        eventoRepository.save(evento).block();

        int databaseSizeBeforeUpdate = eventoRepository.findAll().collectList().block().size();

        // Update the evento using partial update
        Evento partialUpdatedEvento = new Evento();
        partialUpdatedEvento.setId(evento.getId());

        partialUpdatedEvento
            .nombreEvento(UPDATED_NOMBRE_EVENTO)
            .fechaHora(UPDATED_FECHA_HORA)
            .capacidadAdulto(UPDATED_CAPACIDAD_ADULTO)
            .capacidadMenor(UPDATED_CAPACIDAD_MENOR);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEvento.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEvento))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Evento in the database
        List<Evento> eventoList = eventoRepository.findAll().collectList().block();
        assertThat(eventoList).hasSize(databaseSizeBeforeUpdate);
        Evento testEvento = eventoList.get(eventoList.size() - 1);
        assertThat(testEvento.getNombreEvento()).isEqualTo(UPDATED_NOMBRE_EVENTO);
        assertThat(testEvento.getFechaHora()).isEqualTo(UPDATED_FECHA_HORA);
        assertThat(testEvento.getCapacidadAdulto()).isEqualTo(UPDATED_CAPACIDAD_ADULTO);
        assertThat(testEvento.getCapacidadMenor()).isEqualTo(UPDATED_CAPACIDAD_MENOR);
    }

    @Test
    void patchNonExistingEvento() throws Exception {
        int databaseSizeBeforeUpdate = eventoRepository.findAll().collectList().block().size();
        evento.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, evento.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(evento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Evento in the database
        List<Evento> eventoList = eventoRepository.findAll().collectList().block();
        assertThat(eventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEvento() throws Exception {
        int databaseSizeBeforeUpdate = eventoRepository.findAll().collectList().block().size();
        evento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(evento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Evento in the database
        List<Evento> eventoList = eventoRepository.findAll().collectList().block();
        assertThat(eventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEvento() throws Exception {
        int databaseSizeBeforeUpdate = eventoRepository.findAll().collectList().block().size();
        evento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(evento))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Evento in the database
        List<Evento> eventoList = eventoRepository.findAll().collectList().block();
        assertThat(eventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEvento() {
        // Initialize the database
        eventoRepository.save(evento).block();

        int databaseSizeBeforeDelete = eventoRepository.findAll().collectList().block().size();

        // Delete the evento
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, evento.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Evento> eventoList = eventoRepository.findAll().collectList().block();
        assertThat(eventoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
