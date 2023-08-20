package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.DetalleFactura;
import com.mycompany.myapp.repository.DetalleFacturaRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.DetalleFactura}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class DetalleFacturaResource {

    private final Logger log = LoggerFactory.getLogger(DetalleFacturaResource.class);

    private static final String ENTITY_NAME = "detalleFactura";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DetalleFacturaRepository detalleFacturaRepository;

    public DetalleFacturaResource(DetalleFacturaRepository detalleFacturaRepository) {
        this.detalleFacturaRepository = detalleFacturaRepository;
    }

    /**
     * {@code POST  /detalle-facturas} : Create a new detalleFactura.
     *
     * @param detalleFactura the detalleFactura to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new detalleFactura, or with status {@code 400 (Bad Request)} if the detalleFactura has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/detalle-facturas")
    public Mono<ResponseEntity<DetalleFactura>> createDetalleFactura(@RequestBody DetalleFactura detalleFactura) throws URISyntaxException {
        log.debug("REST request to save DetalleFactura : {}", detalleFactura);
        if (detalleFactura.getId() != null) {
            throw new BadRequestAlertException("A new detalleFactura cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return detalleFacturaRepository
            .save(detalleFactura)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/detalle-facturas/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /detalle-facturas/:id} : Updates an existing detalleFactura.
     *
     * @param id the id of the detalleFactura to save.
     * @param detalleFactura the detalleFactura to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated detalleFactura,
     * or with status {@code 400 (Bad Request)} if the detalleFactura is not valid,
     * or with status {@code 500 (Internal Server Error)} if the detalleFactura couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/detalle-facturas/{id}")
    public Mono<ResponseEntity<DetalleFactura>> updateDetalleFactura(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DetalleFactura detalleFactura
    ) throws URISyntaxException {
        log.debug("REST request to update DetalleFactura : {}, {}", id, detalleFactura);
        if (detalleFactura.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, detalleFactura.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return detalleFacturaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return detalleFacturaRepository
                    .save(detalleFactura)
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
     * {@code PATCH  /detalle-facturas/:id} : Partial updates given fields of an existing detalleFactura, field will ignore if it is null
     *
     * @param id the id of the detalleFactura to save.
     * @param detalleFactura the detalleFactura to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated detalleFactura,
     * or with status {@code 400 (Bad Request)} if the detalleFactura is not valid,
     * or with status {@code 404 (Not Found)} if the detalleFactura is not found,
     * or with status {@code 500 (Internal Server Error)} if the detalleFactura couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/detalle-facturas/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<DetalleFactura>> partialUpdateDetalleFactura(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DetalleFactura detalleFactura
    ) throws URISyntaxException {
        log.debug("REST request to partial update DetalleFactura partially : {}, {}", id, detalleFactura);
        if (detalleFactura.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, detalleFactura.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return detalleFacturaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<DetalleFactura> result = detalleFacturaRepository
                    .findById(detalleFactura.getId())
                    .map(existingDetalleFactura -> {
                        if (detalleFactura.getFechaEmitido() != null) {
                            existingDetalleFactura.setFechaEmitido(detalleFactura.getFechaEmitido());
                        }

                        return existingDetalleFactura;
                    })
                    .flatMap(detalleFacturaRepository::save);

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
     * {@code GET  /detalle-facturas} : get all the detalleFacturas.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of detalleFacturas in body.
     */
    @GetMapping(value = "/detalle-facturas", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<DetalleFactura>> getAllDetalleFacturas() {
        log.debug("REST request to get all DetalleFacturas");
        return detalleFacturaRepository.findAll().collectList();
    }

    /**
     * {@code GET  /detalle-facturas} : get all the detalleFacturas as a stream.
     * @return the {@link Flux} of detalleFacturas.
     */
    @GetMapping(value = "/detalle-facturas", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<DetalleFactura> getAllDetalleFacturasAsStream() {
        log.debug("REST request to get all DetalleFacturas as a stream");
        return detalleFacturaRepository.findAll();
    }

    /**
     * {@code GET  /detalle-facturas/:id} : get the "id" detalleFactura.
     *
     * @param id the id of the detalleFactura to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the detalleFactura, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/detalle-facturas/{id}")
    public Mono<ResponseEntity<DetalleFactura>> getDetalleFactura(@PathVariable Long id) {
        log.debug("REST request to get DetalleFactura : {}", id);
        Mono<DetalleFactura> detalleFactura = detalleFacturaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(detalleFactura);
    }

    /**
     * {@code DELETE  /detalle-facturas/:id} : delete the "id" detalleFactura.
     *
     * @param id the id of the detalleFactura to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/detalle-facturas/{id}")
    public Mono<ResponseEntity<Void>> deleteDetalleFactura(@PathVariable Long id) {
        log.debug("REST request to delete DetalleFactura : {}", id);
        return detalleFacturaRepository
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
