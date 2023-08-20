package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Habitacion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Habitacion entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HabitacionRepository extends ReactiveCrudRepository<Habitacion, Long>, HabitacionRepositoryInternal {
    @Query("SELECT * FROM habitacion entity WHERE entity.tarifa_id = :id")
    Flux<Habitacion> findByTarifa(Long id);

    @Query("SELECT * FROM habitacion entity WHERE entity.tarifa_id IS NULL")
    Flux<Habitacion> findAllWhereTarifaIsNull();

    @Query("SELECT * FROM habitacion entity WHERE entity.id not in (select reservar_habitacion_id from reservar_habitacion)")
    Flux<Habitacion> findAllWhereReservarHabitacionIsNull();

    @Override
    <S extends Habitacion> Mono<S> save(S entity);

    @Override
    Flux<Habitacion> findAll();

    @Override
    Mono<Habitacion> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface HabitacionRepositoryInternal {
    <S extends Habitacion> Mono<S> save(S entity);

    Flux<Habitacion> findAllBy(Pageable pageable);

    Flux<Habitacion> findAll();

    Mono<Habitacion> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Habitacion> findAllBy(Pageable pageable, Criteria criteria);
}
