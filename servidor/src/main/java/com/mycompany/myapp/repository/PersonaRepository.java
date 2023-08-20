package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Persona;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Persona entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PersonaRepository extends ReactiveCrudRepository<Persona, Long>, PersonaRepositoryInternal {
    @Override
    <S extends Persona> Mono<S> save(S entity);

    @Override
    Flux<Persona> findAll();

    @Override
    Mono<Persona> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PersonaRepositoryInternal {
    <S extends Persona> Mono<S> save(S entity);

    Flux<Persona> findAllBy(Pageable pageable);

    Flux<Persona> findAll();

    Mono<Persona> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Persona> findAllBy(Pageable pageable, Criteria criteria);
}
