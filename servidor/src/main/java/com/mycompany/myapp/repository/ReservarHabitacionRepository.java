package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ReservarHabitacion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ReservarHabitacion entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReservarHabitacionRepository
    extends ReactiveCrudRepository<ReservarHabitacion, Long>, ReservarHabitacionRepositoryInternal {
    @Query("SELECT * FROM reservar_habitacion entity WHERE entity.habitacion_id = :id")
    Flux<ReservarHabitacion> findByHabitacion(Long id);

    @Query("SELECT * FROM reservar_habitacion entity WHERE entity.habitacion_id IS NULL")
    Flux<ReservarHabitacion> findAllWhereHabitacionIsNull();

    @Query("SELECT * FROM reservar_habitacion entity WHERE entity.cliente_reserva_habitacion_id = :id")
    Flux<ReservarHabitacion> findByClienteReservaHabitacion(Long id);

    @Query("SELECT * FROM reservar_habitacion entity WHERE entity.cliente_reserva_habitacion_id IS NULL")
    Flux<ReservarHabitacion> findAllWhereClienteReservaHabitacionIsNull();

    @Query("SELECT * FROM reservar_habitacion entity WHERE entity.colaborador_reserva_habitacion_id = :id")
    Flux<ReservarHabitacion> findByColaboradorReservaHabitacion(Long id);

    @Query("SELECT * FROM reservar_habitacion entity WHERE entity.colaborador_reserva_habitacion_id IS NULL")
    Flux<ReservarHabitacion> findAllWhereColaboradorReservaHabitacionIsNull();

    @Query("SELECT * FROM reservar_habitacion entity WHERE entity.ocupantes_id = :id")
    Flux<ReservarHabitacion> findByOcupantes(Long id);

    @Query("SELECT * FROM reservar_habitacion entity WHERE entity.ocupantes_id IS NULL")
    Flux<ReservarHabitacion> findAllWhereOcupantesIsNull();

    @Query("SELECT * FROM reservar_habitacion entity WHERE entity.id not in (select detalle_factura_id from detalle_factura)")
    Flux<ReservarHabitacion> findAllWhereDetalleFacturaIsNull();

    @Override
    <S extends ReservarHabitacion> Mono<S> save(S entity);

    @Override
    Flux<ReservarHabitacion> findAll();

    @Override
    Mono<ReservarHabitacion> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ReservarHabitacionRepositoryInternal {
    <S extends ReservarHabitacion> Mono<S> save(S entity);

    Flux<ReservarHabitacion> findAllBy(Pageable pageable);

    Flux<ReservarHabitacion> findAll();

    Mono<ReservarHabitacion> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ReservarHabitacion> findAllBy(Pageable pageable, Criteria criteria);
}
