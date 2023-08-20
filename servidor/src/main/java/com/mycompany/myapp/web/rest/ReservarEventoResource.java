package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.ReservarEvento;
import com.mycompany.myapp.repository.ReservarEventoRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.ReservarEvento}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ReservarEventoResource {

    private final Logger log = LoggerFactory.getLogger(ReservarEventoResource.class);

    private static final String ENTITY_NAME = "reservarEvento";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReservarEventoRepository reservarEventoRepository;

    public ReservarEventoResource(ReservarEventoRepository reservarEventoRepository) {
        this.reservarEventoRepository = reservarEventoRepository;
    }

    /**
     * {@code POST  /reservar-eventos} : Create a new reservarEvento.
     *
     * @param reservarEvento the reservarEvento to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new reservarEvento, or with status {@code 400 (Bad Request)} if the reservarEvento has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/reservar-eventos")
    public Mono<ResponseEntity<ReservarEvento>> createReservarEvento(@RequestBody ReservarEvento reservarEvento) throws URISyntaxException {
        log.debug("REST request to save ReservarEvento : {}", reservarEvento);
        if (reservarEvento.getId() != null) {
            throw new BadRequestAlertException("A new reservarEvento cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return reservarEventoRepository
            .save(reservarEvento)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/reservar-eventos/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /reservar-eventos/:id} : Updates an existing reservarEvento.
     *
     * @param id the id of the reservarEvento to save.
     * @param reservarEvento the reservarEvento to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reservarEvento,
     * or with status {@code 400 (Bad Request)} if the reservarEvento is not valid,
     * or with status {@code 500 (Internal Server Error)} if the reservarEvento couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/reservar-eventos/{id}")
    public Mono<ResponseEntity<ReservarEvento>> updateReservarEvento(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ReservarEvento reservarEvento
    ) throws URISyntaxException {
        log.debug("REST request to update ReservarEvento : {}, {}", id, reservarEvento);
        if (reservarEvento.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reservarEvento.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return reservarEventoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return reservarEventoRepository
                    .save(reservarEvento)
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
     * {@code PATCH  /reservar-eventos/:id} : Partial updates given fields of an existing reservarEvento, field will ignore if it is null
     *
     * @param id the id of the reservarEvento to save.
     * @param reservarEvento the reservarEvento to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reservarEvento,
     * or with status {@code 400 (Bad Request)} if the reservarEvento is not valid,
     * or with status {@code 404 (Not Found)} if the reservarEvento is not found,
     * or with status {@code 500 (Internal Server Error)} if the reservarEvento couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/reservar-eventos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ReservarEvento>> partialUpdateReservarEvento(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ReservarEvento reservarEvento
    ) throws URISyntaxException {
        log.debug("REST request to partial update ReservarEvento partially : {}, {}", id, reservarEvento);
        if (reservarEvento.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reservarEvento.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return reservarEventoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ReservarEvento> result = reservarEventoRepository
                    .findById(reservarEvento.getId())
                    .map(existingReservarEvento -> {
                        if (reservarEvento.getFechaReservacion() != null) {
                            existingReservarEvento.setFechaReservacion(reservarEvento.getFechaReservacion());
                        }
                        if (reservarEvento.getTotalReservacion() != null) {
                            existingReservarEvento.setTotalReservacion(reservarEvento.getTotalReservacion());
                        }

                        return existingReservarEvento;
                    })
                    .flatMap(reservarEventoRepository::save);

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
     * {@code GET  /reservar-eventos} : get all the reservarEventos.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of reservarEventos in body.
     */
    @GetMapping(value = "/reservar-eventos", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<ReservarEvento>> getAllReservarEventos(@RequestParam(required = false) String filter) {
        if ("detallefactura-is-null".equals(filter)) {
            log.debug("REST request to get all ReservarEventos where detalleFactura is null");
            return reservarEventoRepository.findAllWhereDetalleFacturaIsNull().collectList();
        }
        log.debug("REST request to get all ReservarEventos");
        return reservarEventoRepository.findAll().collectList();
    }

    /**
     * {@code GET  /reservar-eventos} : get all the reservarEventos as a stream.
     * @return the {@link Flux} of reservarEventos.
     */
    @GetMapping(value = "/reservar-eventos", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ReservarEvento> getAllReservarEventosAsStream() {
        log.debug("REST request to get all ReservarEventos as a stream");
        return reservarEventoRepository.findAll();
    }

    /**
     * {@code GET  /reservar-eventos/:id} : get the "id" reservarEvento.
     *
     * @param id the id of the reservarEvento to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the reservarEvento, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/reservar-eventos/{id}")
    public Mono<ResponseEntity<ReservarEvento>> getReservarEvento(@PathVariable Long id) {
        log.debug("REST request to get ReservarEvento : {}", id);
        Mono<ReservarEvento> reservarEvento = reservarEventoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(reservarEvento);
    }

    /**
     * {@code DELETE  /reservar-eventos/:id} : delete the "id" reservarEvento.
     *
     * @param id the id of the reservarEvento to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/reservar-eventos/{id}")
    public Mono<ResponseEntity<Void>> deleteReservarEvento(@PathVariable Long id) {
        log.debug("REST request to delete ReservarEvento : {}", id);
        return reservarEventoRepository
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
