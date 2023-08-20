package com.mycompany.myapp.repository;


import com.mycompany.myapp.domain.Evento;
import com.mycompany.myapp.repository.rowmapper.ColaboradorRowMapper;
import com.mycompany.myapp.repository.rowmapper.EventoRowMapper;
import com.mycompany.myapp.repository.rowmapper.TarifaRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Evento entity.
 */
@SuppressWarnings("unused")
class EventoRepositoryInternalImpl extends SimpleR2dbcRepository<Evento, Long> implements EventoRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TarifaRowMapper tarifaMapper;
    private final ColaboradorRowMapper colaboradorMapper;
    private final EventoRowMapper eventoMapper;

    private static final Table entityTable = Table.aliased("evento", EntityManager.ENTITY_ALIAS);
    private static final Table tarifaTable = Table.aliased("tarifa", "tarifa");
    private static final Table encargadoTable = Table.aliased("colaborador", "encargado");

    public EventoRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TarifaRowMapper tarifaMapper,
        ColaboradorRowMapper colaboradorMapper,
        EventoRowMapper eventoMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Evento.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.tarifaMapper = tarifaMapper;
        this.colaboradorMapper = colaboradorMapper;
        this.eventoMapper = eventoMapper;
    }

    @Override
    public Flux<Evento> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Evento> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = EventoSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(TarifaSqlHelper.getColumns(tarifaTable, "tarifa"));
        columns.addAll(ColaboradorSqlHelper.getColumns(encargadoTable, "encargado"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(tarifaTable)
            .on(Column.create("tarifa_id", entityTable))
            .equals(Column.create("id", tarifaTable))
            .leftOuterJoin(encargadoTable)
            .on(Column.create("encargado_id", entityTable))
            .equals(Column.create("id", encargadoTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Evento.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Evento> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Evento> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Evento process(Row row, RowMetadata metadata) {
        Evento entity = eventoMapper.apply(row, "e");
        entity.setTarifa(tarifaMapper.apply(row, "tarifa"));
        entity.setEncargado(colaboradorMapper.apply(row, "encargado"));
        return entity;
    }

    @Override
    public <S extends Evento> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
