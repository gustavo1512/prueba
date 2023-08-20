package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ReservarEvento;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ReservarEvento entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReservarEventoRepository extends ReactiveCrudRepository<ReservarEvento, Long>, ReservarEventoRepositoryInternal {
    @Query("SELECT * FROM reservar_evento entity WHERE entity.cliente_reserva_evento_id = :id")
    Flux<ReservarEvento> findByClienteReservaEvento(Long id);

    @Query("SELECT * FROM reservar_evento entity WHERE entity.cliente_reserva_evento_id IS NULL")
    Flux<ReservarEvento> findAllWhereClienteReservaEventoIsNull();

    @Query("SELECT * FROM reservar_evento entity WHERE entity.colaborador_reserva_evento_id = :id")
    Flux<ReservarEvento> findByColaboradorReservaEvento(Long id);

    @Query("SELECT * FROM reservar_evento entity WHERE entity.colaborador_reserva_evento_id IS NULL")
    Flux<ReservarEvento> findAllWhereColaboradorReservaEventoIsNull();

    @Query("SELECT * FROM reservar_evento entity WHERE entity.id not in (select detalle_factura_id from detalle_factura)")
    Flux<ReservarEvento> findAllWhereDetalleFacturaIsNull();

    @Override
    <S extends ReservarEvento> Mono<S> save(S entity);

    @Override
    Flux<ReservarEvento> findAll();

    @Override
    Mono<ReservarEvento> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ReservarEventoRepositoryInternal {
    <S extends ReservarEvento> Mono<S> save(S entity);

    Flux<ReservarEvento> findAllBy(Pageable pageable);

    Flux<ReservarEvento> findAll();

    Mono<ReservarEvento> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ReservarEvento> findAllBy(Pageable pageable, Criteria criteria);
}
