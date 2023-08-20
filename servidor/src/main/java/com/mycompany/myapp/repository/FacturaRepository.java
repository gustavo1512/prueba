package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Factura;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Factura entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FacturaRepository extends ReactiveCrudRepository<Factura, Long>, FacturaRepositoryInternal {
    @Query("SELECT * FROM factura entity WHERE entity.detalle_factura_id = :id")
    Flux<Factura> findByDetalleFactura(Long id);

    @Query("SELECT * FROM factura entity WHERE entity.detalle_factura_id IS NULL")
    Flux<Factura> findAllWhereDetalleFacturaIsNull();

    @Query("SELECT * FROM factura entity WHERE entity.cliente_factura_id = :id")
    Flux<Factura> findByClienteFactura(Long id);

    @Query("SELECT * FROM factura entity WHERE entity.cliente_factura_id IS NULL")
    Flux<Factura> findAllWhereClienteFacturaIsNull();

    @Query("SELECT * FROM factura entity WHERE entity.cliente_colaborador_id = :id")
    Flux<Factura> findByClienteColaborador(Long id);

    @Query("SELECT * FROM factura entity WHERE entity.cliente_colaborador_id IS NULL")
    Flux<Factura> findAllWhereClienteColaboradorIsNull();

    @Override
    <S extends Factura> Mono<S> save(S entity);

    @Override
    Flux<Factura> findAll();

    @Override
    Mono<Factura> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface FacturaRepositoryInternal {
    <S extends Factura> Mono<S> save(S entity);

    Flux<Factura> findAllBy(Pageable pageable);

    Flux<Factura> findAll();

    Mono<Factura> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Factura> findAllBy(Pageable pageable, Criteria criteria);
}
