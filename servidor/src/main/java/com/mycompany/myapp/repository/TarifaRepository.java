package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Tarifa;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Tarifa entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TarifaRepository extends ReactiveCrudRepository<Tarifa, Long>, TarifaRepositoryInternal {
    @Query("SELECT * FROM tarifa entity WHERE entity.id not in (select evento_id from evento)")
    Flux<Tarifa> findAllWhereEventoIsNull();

    @Query("SELECT * FROM tarifa entity WHERE entity.id not in (select habitacion_id from habitacion)")
    Flux<Tarifa> findAllWhereHabitacionIsNull();

    @Override
    <S extends Tarifa> Mono<S> save(S entity);

    @Override
    Flux<Tarifa> findAll();

    @Override
    Mono<Tarifa> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TarifaRepositoryInternal {
    <S extends Tarifa> Mono<S> save(S entity);

    Flux<Tarifa> findAllBy(Pageable pageable);

    Flux<Tarifa> findAll();

    Mono<Tarifa> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Tarifa> findAllBy(Pageable pageable, Criteria criteria);
}
