package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Evento;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Evento entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventoRepository extends ReactiveCrudRepository<Evento, Long>, EventoRepositoryInternal {
    @Query("SELECT * FROM evento entity WHERE entity.tarifa_id = :id")
    Flux<Evento> findByTarifa(Long id);

    @Query("SELECT * FROM evento entity WHERE entity.tarifa_id IS NULL")
    Flux<Evento> findAllWhereTarifaIsNull();

    @Query("SELECT * FROM evento entity WHERE entity.encargado_id = :id")
    Flux<Evento> findByEncargado(Long id);

    @Query("SELECT * FROM evento entity WHERE entity.encargado_id IS NULL")
    Flux<Evento> findAllWhereEncargadoIsNull();

    @Override
    <S extends Evento> Mono<S> save(S entity);

    @Override
    Flux<Evento> findAll();

    @Override
    Mono<Evento> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EventoRepositoryInternal {
    <S extends Evento> Mono<S> save(S entity);

    Flux<Evento> findAllBy(Pageable pageable);

    Flux<Evento> findAll();

    Mono<Evento> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Evento> findAllBy(Pageable pageable, Criteria criteria);
}
