package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.ReservarHabitacion;
import com.mycompany.myapp.repository.ReservarHabitacionRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.ReservarHabitacion}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ReservarHabitacionResource {

    private final Logger log = LoggerFactory.getLogger(ReservarHabitacionResource.class);

    private static final String ENTITY_NAME = "reservarHabitacion";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReservarHabitacionRepository reservarHabitacionRepository;

    public ReservarHabitacionResource(ReservarHabitacionRepository reservarHabitacionRepository) {
        this.reservarHabitacionRepository = reservarHabitacionRepository;
    }

    /**
     * {@code POST  /reservar-habitacions} : Create a new reservarHabitacion.
     *
     * @param reservarHabitacion the reservarHabitacion to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new reservarHabitacion, or with status {@code 400 (Bad Request)} if the reservarHabitacion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/reservar-habitacions")
    public Mono<ResponseEntity<ReservarHabitacion>> createReservarHabitacion(@RequestBody ReservarHabitacion reservarHabitacion)
        throws URISyntaxException {
        log.debug("REST request to save ReservarHabitacion : {}", reservarHabitacion);
        if (reservarHabitacion.getId() != null) {
            throw new BadRequestAlertException("A new reservarHabitacion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return reservarHabitacionRepository
            .save(reservarHabitacion)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/reservar-habitacions/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /reservar-habitacions/:id} : Updates an existing reservarHabitacion.
     *
     * @param id the id of the reservarHabitacion to save.
     * @param reservarHabitacion the reservarHabitacion to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reservarHabitacion,
     * or with status {@code 400 (Bad Request)} if the reservarHabitacion is not valid,
     * or with status {@code 500 (Internal Server Error)} if the reservarHabitacion couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/reservar-habitacions/{id}")
    public Mono<ResponseEntity<ReservarHabitacion>> updateReservarHabitacion(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ReservarHabitacion reservarHabitacion
    ) throws URISyntaxException {
        log.debug("REST request to update ReservarHabitacion : {}, {}", id, reservarHabitacion);
        if (reservarHabitacion.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reservarHabitacion.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return reservarHabitacionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return reservarHabitacionRepository
                    .save(reservarHabitacion)
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
     * {@code PATCH  /reservar-habitacions/:id} : Partial updates given fields of an existing reservarHabitacion, field will ignore if it is null
     *
     * @param id the id of the reservarHabitacion to save.
     * @param reservarHabitacion the reservarHabitacion to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reservarHabitacion,
     * or with status {@code 400 (Bad Request)} if the reservarHabitacion is not valid,
     * or with status {@code 404 (Not Found)} if the reservarHabitacion is not found,
     * or with status {@code 500 (Internal Server Error)} if the reservarHabitacion couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/reservar-habitacions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ReservarHabitacion>> partialUpdateReservarHabitacion(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ReservarHabitacion reservarHabitacion
    ) throws URISyntaxException {
        log.debug("REST request to partial update ReservarHabitacion partially : {}, {}", id, reservarHabitacion);
        if (reservarHabitacion.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reservarHabitacion.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return reservarHabitacionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ReservarHabitacion> result = reservarHabitacionRepository
                    .findById(reservarHabitacion.getId())
                    .map(existingReservarHabitacion -> {
                        if (reservarHabitacion.getFechaReserva() != null) {
                            existingReservarHabitacion.setFechaReserva(reservarHabitacion.getFechaReserva());
                        }
                        if (reservarHabitacion.getFechaInicio() != null) {
                            existingReservarHabitacion.setFechaInicio(reservarHabitacion.getFechaInicio());
                        }
                        if (reservarHabitacion.getFechaFinal() != null) {
                            existingReservarHabitacion.setFechaFinal(reservarHabitacion.getFechaFinal());
                        }
                        if (reservarHabitacion.getTotalReservacion() != null) {
                            existingReservarHabitacion.setTotalReservacion(reservarHabitacion.getTotalReservacion());
                        }

                        return existingReservarHabitacion;
                    })
                    .flatMap(reservarHabitacionRepository::save);

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
     * {@code GET  /reservar-habitacions} : get all the reservarHabitacions.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of reservarHabitacions in body.
     */
    @GetMapping(value = "/reservar-habitacions", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<ReservarHabitacion>> getAllReservarHabitacions(@RequestParam(required = false) String filter) {
        if ("detallefactura-is-null".equals(filter)) {
            log.debug("REST request to get all ReservarHabitacions where detalleFactura is null");
            return reservarHabitacionRepository.findAllWhereDetalleFacturaIsNull().collectList();
        }
        log.debug("REST request to get all ReservarHabitacions");
        return reservarHabitacionRepository.findAll().collectList();
    }

    /**
     * {@code GET  /reservar-habitacions} : get all the reservarHabitacions as a stream.
     * @return the {@link Flux} of reservarHabitacions.
     */
    @GetMapping(value = "/reservar-habitacions", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ReservarHabitacion> getAllReservarHabitacionsAsStream() {
        log.debug("REST request to get all ReservarHabitacions as a stream");
        return reservarHabitacionRepository.findAll();
    }

    /**
     * {@code GET  /reservar-habitacions/:id} : get the "id" reservarHabitacion.
     *
     * @param id the id of the reservarHabitacion to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the reservarHabitacion, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/reservar-habitacions/{id}")
    public Mono<ResponseEntity<ReservarHabitacion>> getReservarHabitacion(@PathVariable Long id) {
        log.debug("REST request to get ReservarHabitacion : {}", id);
        Mono<ReservarHabitacion> reservarHabitacion = reservarHabitacionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(reservarHabitacion);
    }

    /**
     * {@code DELETE  /reservar-habitacions/:id} : delete the "id" reservarHabitacion.
     *
     * @param id the id of the reservarHabitacion to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/reservar-habitacions/{id}")
    public Mono<ResponseEntity<Void>> deleteReservarHabitacion(@PathVariable Long id) {
        log.debug("REST request to delete ReservarHabitacion : {}", id);
        return reservarHabitacionRepository
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
