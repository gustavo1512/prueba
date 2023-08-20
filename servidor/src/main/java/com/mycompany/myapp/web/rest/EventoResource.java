package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Evento;
import com.mycompany.myapp.repository.EventoRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Evento}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class EventoResource {

    private final Logger log = LoggerFactory.getLogger(EventoResource.class);

    private static final String ENTITY_NAME = "evento";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EventoRepository eventoRepository;

    public EventoResource(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    /**
     * {@code POST  /eventos} : Create a new evento.
     *
     * @param evento the evento to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new evento, or with status {@code 400 (Bad Request)} if the evento has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/eventos")
    public Mono<ResponseEntity<Evento>> createEvento(@RequestBody Evento evento) throws URISyntaxException {
        log.debug("REST request to save Evento : {}", evento);
        if (evento.getId() != null) {
            throw new BadRequestAlertException("A new evento cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return eventoRepository
            .save(evento)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/eventos/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /eventos/:id} : Updates an existing evento.
     *
     * @param id the id of the evento to save.
     * @param evento the evento to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated evento,
     * or with status {@code 400 (Bad Request)} if the evento is not valid,
     * or with status {@code 500 (Internal Server Error)} if the evento couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/eventos/{id}")
    public Mono<ResponseEntity<Evento>> updateEvento(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Evento evento
    ) throws URISyntaxException {
        log.debug("REST request to update Evento : {}, {}", id, evento);
        if (evento.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, evento.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return eventoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return eventoRepository
                    .save(evento)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /eventos/:id} : Partial updates given fields of an existing evento, field will ignore if it is null
     *
     * @param id the id of the evento to save.
     * @param evento the evento to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated evento,
     * or with status {@code 400 (Bad Request)} if the evento is not valid,
     * or with status {@code 404 (Not Found)} if the evento is not found,
     * or with status {@code 500 (Internal Server Error)} if the evento couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/eventos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Evento>> partialUpdateEvento(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Evento evento
    ) throws URISyntaxException {
        log.debug("REST request to partial update Evento partially : {}, {}", id, evento);
        if (evento.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, evento.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return eventoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Evento> result = eventoRepository
                    .findById(evento.getId())
                    .map(existingEvento -> {
                        if (evento.getNombreEvento() != null) {
                            existingEvento.setNombreEvento(evento.getNombreEvento());
                        }
                        if (evento.getFechaHora() != null) {
                            existingEvento.setFechaHora(evento.getFechaHora());
                        }
                        if (evento.getCapacidadAdulto() != null) {
                            existingEvento.setCapacidadAdulto(evento.getCapacidadAdulto());
                        }
                        if (evento.getCapacidadMenor() != null) {
                            existingEvento.setCapacidadMenor(evento.getCapacidadMenor());
                        }

                        return existingEvento;
                    })
                    .flatMap(eventoRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /eventos} : get all the eventos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of eventos in body.
     */
    @GetMapping(value = "/eventos", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Evento>> getAllEventos() {
        log.debug("REST request to get all Eventos");
        return eventoRepository.findAll().collectList();
    }

    /**
     * {@code GET  /eventos} : get all the eventos as a stream.
     * @return the {@link Flux} of eventos.
     */
    @GetMapping(value = "/eventos", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Evento> getAllEventosAsStream() {
        log.debug("REST request to get all Eventos as a stream");
        return eventoRepository.findAll();
    }

    /**
     * {@code GET  /eventos/:id} : get the "id" evento.
     *
     * @param id the id of the evento to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the evento, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/eventos/{id}")
    public Mono<ResponseEntity<Evento>> getEvento(@PathVariable Long id) {
        log.debug("REST request to get Evento : {}", id);
        Mono<Evento> evento = eventoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(evento);
    }

    /**
     * {@code DELETE  /eventos/:id} : delete the "id" evento.
     *
     * @param id the id of the evento to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/eventos/{id}")
    public Mono<ResponseEntity<Void>> deleteEvento(@PathVariable Long id) {
        log.debug("REST request to delete Evento : {}", id);
        return eventoRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
