package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Habitacion;
import com.mycompany.myapp.repository.HabitacionRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Habitacion}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class HabitacionResource {

    private final Logger log = LoggerFactory.getLogger(HabitacionResource.class);

    private static final String ENTITY_NAME = "habitacion";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HabitacionRepository habitacionRepository;

    public HabitacionResource(HabitacionRepository habitacionRepository) {
        this.habitacionRepository = habitacionRepository;
    }

    /**
     * {@code POST  /habitacions} : Create a new habitacion.
     *
     * @param habitacion the habitacion to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new habitacion, or with status {@code 400 (Bad Request)} if the habitacion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/habitacions")
    public Mono<ResponseEntity<Habitacion>> createHabitacion(@RequestBody Habitacion habitacion) throws URISyntaxException {
        log.debug("REST request to save Habitacion : {}", habitacion);
        if (habitacion.getId() != null) {
            throw new BadRequestAlertException("A new habitacion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return habitacionRepository
            .save(habitacion)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/habitacions/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /habitacions/:id} : Updates an existing habitacion.
     *
     * @param id the id of the habitacion to save.
     * @param habitacion the habitacion to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated habitacion,
     * or with status {@code 400 (Bad Request)} if the habitacion is not valid,
     * or with status {@code 500 (Internal Server Error)} if the habitacion couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/habitacions/{id}")
    public Mono<ResponseEntity<Habitacion>> updateHabitacion(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Habitacion habitacion
    ) throws URISyntaxException {
        log.debug("REST request to update Habitacion : {}, {}", id, habitacion);
        if (habitacion.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, habitacion.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return habitacionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return habitacionRepository
                    .save(habitacion)
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
     * {@code PATCH  /habitacions/:id} : Partial updates given fields of an existing habitacion, field will ignore if it is null
     *
     * @param id the id of the habitacion to save.
     * @param habitacion the habitacion to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated habitacion,
     * or with status {@code 400 (Bad Request)} if the habitacion is not valid,
     * or with status {@code 404 (Not Found)} if the habitacion is not found,
     * or with status {@code 500 (Internal Server Error)} if the habitacion couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/habitacions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Habitacion>> partialUpdateHabitacion(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Habitacion habitacion
    ) throws URISyntaxException {
        log.debug("REST request to partial update Habitacion partially : {}, {}", id, habitacion);
        if (habitacion.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, habitacion.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return habitacionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Habitacion> result = habitacionRepository
                    .findById(habitacion.getId())
                    .map(existingHabitacion -> {
                        if (habitacion.getTipo() != null) {
                            existingHabitacion.setTipo(habitacion.getTipo());
                        }
                        if (habitacion.getCapacidadAdulto() != null) {
                            existingHabitacion.setCapacidadAdulto(habitacion.getCapacidadAdulto());
                        }
                        if (habitacion.getCapacidadMenor() != null) {
                            existingHabitacion.setCapacidadMenor(habitacion.getCapacidadMenor());
                        }
                        if (habitacion.getDisponible() != null) {
                            existingHabitacion.setDisponible(habitacion.getDisponible());
                        }

                        return existingHabitacion;
                    })
                    .flatMap(habitacionRepository::save);

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
     * {@code GET  /habitacions} : get all the habitacions.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of habitacions in body.
     */
    @GetMapping(value = "/habitacions", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Habitacion>> getAllHabitacions(@RequestParam(required = false) String filter) {
        if ("reservarhabitacion-is-null".equals(filter)) {
            log.debug("REST request to get all Habitacions where reservarHabitacion is null");
            return habitacionRepository.findAllWhereReservarHabitacionIsNull().collectList();
        }
        log.debug("REST request to get all Habitacions");
        return habitacionRepository.findAll().collectList();
    }

    /**
     * {@code GET  /habitacions} : get all the habitacions as a stream.
     * @return the {@link Flux} of habitacions.
     */
    @GetMapping(value = "/habitacions", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Habitacion> getAllHabitacionsAsStream() {
        log.debug("REST request to get all Habitacions as a stream");
        return habitacionRepository.findAll();
    }

    /**
     * {@code GET  /habitacions/:id} : get the "id" habitacion.
     *
     * @param id the id of the habitacion to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the habitacion, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/habitacions/{id}")
    public Mono<ResponseEntity<Habitacion>> getHabitacion(@PathVariable Long id) {
        log.debug("REST request to get Habitacion : {}", id);
        Mono<Habitacion> habitacion = habitacionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(habitacion);
    }

    /**
     * {@code DELETE  /habitacions/:id} : delete the "id" habitacion.
     *
     * @param id the id of the habitacion to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/habitacions/{id}")
    public Mono<ResponseEntity<Void>> deleteHabitacion(@PathVariable Long id) {
        log.debug("REST request to delete Habitacion : {}", id);
        return habitacionRepository
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
