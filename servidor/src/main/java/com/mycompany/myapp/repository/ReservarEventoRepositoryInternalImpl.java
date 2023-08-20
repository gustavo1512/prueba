package com.mycompany.myapp.repository;


import com.mycompany.myapp.domain.ReservarEvento;
import com.mycompany.myapp.repository.rowmapper.ClienteRowMapper;
import com.mycompany.myapp.repository.rowmapper.ColaboradorRowMapper;
import com.mycompany.myapp.repository.rowmapper.ReservarEventoRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the ReservarEvento entity.
 */
@SuppressWarnings("unused")
class ReservarEventoRepositoryInternalImpl extends SimpleR2dbcRepository<ReservarEvento, Long> implements ReservarEventoRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ClienteRowMapper clienteMapper;
    private final ColaboradorRowMapper colaboradorMapper;
    private final ReservarEventoRowMapper reservareventoMapper;

    private static final Table entityTable = Table.aliased("reservar_evento", EntityManager.ENTITY_ALIAS);
    private static final Table clienteReservaEventoTable = Table.aliased("cliente", "clienteReservaEvento");
    private static final Table colaboradorReservaEventoTable = Table.aliased("colaborador", "colaboradorReservaEvento");

    public ReservarEventoRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ClienteRowMapper clienteMapper,
        ColaboradorRowMapper colaboradorMapper,
        ReservarEventoRowMapper reservareventoMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(ReservarEvento.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.clienteMapper = clienteMapper;
        this.colaboradorMapper = colaboradorMapper;
        this.reservareventoMapper = reservareventoMapper;
    }

    @Override
    public Flux<ReservarEvento> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<ReservarEvento> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ReservarEventoSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ClienteSqlHelper.getColumns(clienteReservaEventoTable, "clienteReservaEvento"));
        columns.addAll(ColaboradorSqlHelper.getColumns(colaboradorReservaEventoTable, "colaboradorReservaEvento"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(clienteReservaEventoTable)
            .on(Column.create("cliente_reserva_evento_id", entityTable))
            .equals(Column.create("id", clienteReservaEventoTable))
            .leftOuterJoin(colaboradorReservaEventoTable)
            .on(Column.create("colaborador_reserva_evento_id", entityTable))
            .equals(Column.create("id", colaboradorReservaEventoTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, ReservarEvento.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<ReservarEvento> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<ReservarEvento> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private ReservarEvento process(Row row, RowMetadata metadata) {
        ReservarEvento entity = reservareventoMapper.apply(row, "e");
        entity.setClienteReservaEvento(clienteMapper.apply(row, "clienteReservaEvento"));
        entity.setColaboradorReservaEvento(colaboradorMapper.apply(row, "colaboradorReservaEvento"));
        return entity;
    }

    @Override
    public <S extends ReservarEvento> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
