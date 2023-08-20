package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Tarifa;
import com.mycompany.myapp.repository.TarifaRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Tarifa}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TarifaResource {

    private final Logger log = LoggerFactory.getLogger(TarifaResource.class);

    private static final String ENTITY_NAME = "tarifa";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TarifaRepository tarifaRepository;

    public TarifaResource(TarifaRepository tarifaRepository) {
        this.tarifaRepository = tarifaRepository;
    }

    /**
     * {@code POST  /tarifas} : Create a new tarifa.
     *
     * @param tarifa the tarifa to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tarifa, or with status {@code 400 (Bad Request)} if the tarifa has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tarifas")
    public Mono<ResponseEntity<Tarifa>> createTarifa(@RequestBody Tarifa tarifa) throws URISyntaxException {
        log.debug("REST request to save Tarifa : {}", tarifa);
        if (tarifa.getId() != null) {
            throw new BadRequestAlertException("A new tarifa cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return tarifaRepository
            .save(tarifa)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/tarifas/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /tarifas/:id} : Updates an existing tarifa.
     *
     * @param id the id of the tarifa to save.
     * @param tarifa the tarifa to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tarifa,
     * or with status {@code 400 (Bad Request)} if the tarifa is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tarifa couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tarifas/{id}")
    public Mono<ResponseEntity<Tarifa>> updateTarifa(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Tarifa tarifa
    ) throws URISyntaxException {
        log.debug("REST request to update Tarifa : {}, {}", id, tarifa);
        if (tarifa.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tarifa.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tarifaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return tarifaRepository
                    .save(tarifa)
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
     * {@code PATCH  /tarifas/:id} : Partial updates given fields of an existing tarifa, field will ignore if it is null
     *
     * @param id the id of the tarifa to save.
     * @param tarifa the tarifa to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tarifa,
     * or with status {@code 400 (Bad Request)} if the tarifa is not valid,
     * or with status {@code 404 (Not Found)} if the tarifa is not found,
     * or with status {@code 500 (Internal Server Error)} if the tarifa couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tarifas/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Tarifa>> partialUpdateTarifa(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Tarifa tarifa
    ) throws URISyntaxException {
        log.debug("REST request to partial update Tarifa partially : {}, {}", id, tarifa);
        if (tarifa.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tarifa.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tarifaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Tarifa> result = tarifaRepository
                    .findById(tarifa.getId())
                    .map(existingTarifa -> {
                        if (tarifa.getTipoTarifa() != null) {
                            existingTarifa.setTipoTarifa(tarifa.getTipoTarifa());
                        }
                        if (tarifa.getTarifaAdulto() != null) {
                            existingTarifa.setTarifaAdulto(tarifa.getTarifaAdulto());
                        }
                        if (tarifa.getTarifaMenor() != null) {
                            existingTarifa.setTarifaMenor(tarifa.getTarifaMenor());
                        }

                        return existingTarifa;
                    })
                    .flatMap(tarifaRepository::save);

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
     * {@code GET  /tarifas} : get all the tarifas.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tarifas in body.
     */
    @GetMapping(value = "/tarifas", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Tarifa>> getAllTarifas(@RequestParam(required = false) String filter) {
        if ("evento-is-null".equals(filter)) {
            log.debug("REST request to get all Tarifas where evento is null");
            return tarifaRepository.findAllWhereEventoIsNull().collectList();
        }

        if ("habitacion-is-null".equals(filter)) {
            log.debug("REST request to get all Tarifas where habitacion is null");
            return tarifaRepository.findAllWhereHabitacionIsNull().collectList();
        }
        log.debug("REST request to get all Tarifas");
        return tarifaRepository.findAll().collectList();
    }

    /**
     * {@code GET  /tarifas} : get all the tarifas as a stream.
     * @return the {@link Flux} of tarifas.
     */
    @GetMapping(value = "/tarifas", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Tarifa> getAllTarifasAsStream() {
        log.debug("REST request to get all Tarifas as a stream");
        return tarifaRepository.findAll();
    }

    /**
     * {@code GET  /tarifas/:id} : get the "id" tarifa.
     *
     * @param id the id of the tarifa to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tarifa, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tarifas/{id}")
    public Mono<ResponseEntity<Tarifa>> getTarifa(@PathVariable Long id) {
        log.debug("REST request to get Tarifa : {}", id);
        Mono<Tarifa> tarifa = tarifaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(tarifa);
    }

    /**
     * {@code DELETE  /tarifas/:id} : delete the "id" tarifa.
     *
     * @param id the id of the tarifa to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tarifas/{id}")
    public Mono<ResponseEntity<Void>> deleteTarifa(@PathVariable Long id) {
        log.debug("REST request to delete Tarifa : {}", id);
        return tarifaRepository
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
