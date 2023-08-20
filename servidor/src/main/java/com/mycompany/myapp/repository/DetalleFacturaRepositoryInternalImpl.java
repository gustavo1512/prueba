package com.mycompany.myapp.repository;


import com.mycompany.myapp.domain.DetalleFactura;
import com.mycompany.myapp.repository.rowmapper.DetalleFacturaRowMapper;
import com.mycompany.myapp.repository.rowmapper.ReservarEventoRowMapper;
import com.mycompany.myapp.repository.rowmapper.ReservarHabitacionRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the DetalleFactura entity.
 */
@SuppressWarnings("unused")
class DetalleFacturaRepositoryInternalImpl extends SimpleR2dbcRepository<DetalleFactura, Long> implements DetalleFacturaRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ReservarHabitacionRowMapper reservarhabitacionMapper;
    private final ReservarEventoRowMapper reservareventoMapper;
    private final DetalleFacturaRowMapper detallefacturaMapper;

    private static final Table entityTable = Table.aliased("detalle_factura", EntityManager.ENTITY_ALIAS);
    private static final Table habitacionReservadaTable = Table.aliased("reservar_habitacion", "habitacionReservada");
    private static final Table eventoReservadoTable = Table.aliased("reservar_evento", "eventoReservado");

    public DetalleFacturaRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ReservarHabitacionRowMapper reservarhabitacionMapper,
        ReservarEventoRowMapper reservareventoMapper,
        DetalleFacturaRowMapper detallefacturaMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(DetalleFactura.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.reservarhabitacionMapper = reservarhabitacionMapper;
        this.reservareventoMapper = reservareventoMapper;
        this.detallefacturaMapper = detallefacturaMapper;
    }

    @Override
    public Flux<DetalleFactura> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<DetalleFactura> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = DetalleFacturaSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ReservarHabitacionSqlHelper.getColumns(habitacionReservadaTable, "habitacionReservada"));
        columns.addAll(ReservarEventoSqlHelper.getColumns(eventoReservadoTable, "eventoReservado"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(habitacionReservadaTable)
            .on(Column.create("habitacion_reservada_id", entityTable))
            .equals(Column.create("id", habitacionReservadaTable))
            .leftOuterJoin(eventoReservadoTable)
            .on(Column.create("evento_reservado_id", entityTable))
            .equals(Column.create("id", eventoReservadoTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, DetalleFactura.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<DetalleFactura> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<DetalleFactura> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private DetalleFactura process(Row row, RowMetadata metadata) {
        DetalleFactura entity = detallefacturaMapper.apply(row, "e");
        entity.setHabitacionReservada(reservarhabitacionMapper.apply(row, "habitacionReservada"));
        entity.setEventoReservado(reservareventoMapper.apply(row, "eventoReservado"));
        return entity;
    }

    @Override
    public <S extends DetalleFactura> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
