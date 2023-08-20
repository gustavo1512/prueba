package com.mycompany.myapp.repository;


import com.mycompany.myapp.domain.ReservarHabitacion;
import com.mycompany.myapp.repository.rowmapper.ClienteRowMapper;
import com.mycompany.myapp.repository.rowmapper.ColaboradorRowMapper;
import com.mycompany.myapp.repository.rowmapper.HabitacionRowMapper;
import com.mycompany.myapp.repository.rowmapper.PersonaRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the ReservarHabitacion entity.
 */
@SuppressWarnings("unused")
class ReservarHabitacionRepositoryInternalImpl
    extends SimpleR2dbcRepository<ReservarHabitacion, Long>
    implements ReservarHabitacionRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final HabitacionRowMapper habitacionMapper;
    private final ClienteRowMapper clienteMapper;
    private final ColaboradorRowMapper colaboradorMapper;
    private final PersonaRowMapper personaMapper;
    private final ReservarHabitacionRowMapper reservarhabitacionMapper;

    private static final Table entityTable = Table.aliased("reservar_habitacion", EntityManager.ENTITY_ALIAS);
    private static final Table habitacionTable = Table.aliased("habitacion", "habitacion");
    private static final Table clienteReservaHabitacionTable = Table.aliased("cliente", "clienteReservaHabitacion");
    private static final Table colaboradorReservaHabitacionTable = Table.aliased("colaborador", "colaboradorReservaHabitacion");
    private static final Table ocupantesTable = Table.aliased("persona", "ocupantes");

    public ReservarHabitacionRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        HabitacionRowMapper habitacionMapper,
        ClienteRowMapper clienteMapper,
        ColaboradorRowMapper colaboradorMapper,
        PersonaRowMapper personaMapper,
        ReservarHabitacionRowMapper reservarhabitacionMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(ReservarHabitacion.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.habitacionMapper = habitacionMapper;
        this.clienteMapper = clienteMapper;
        this.colaboradorMapper = colaboradorMapper;
        this.personaMapper = personaMapper;
        this.reservarhabitacionMapper = reservarhabitacionMapper;
    }

    @Override
    public Flux<ReservarHabitacion> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<ReservarHabitacion> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ReservarHabitacionSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(HabitacionSqlHelper.getColumns(habitacionTable, "habitacion"));
        columns.addAll(ClienteSqlHelper.getColumns(clienteReservaHabitacionTable, "clienteReservaHabitacion"));
        columns.addAll(ColaboradorSqlHelper.getColumns(colaboradorReservaHabitacionTable, "colaboradorReservaHabitacion"));
        columns.addAll(PersonaSqlHelper.getColumns(ocupantesTable, "ocupantes"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(habitacionTable)
            .on(Column.create("habitacion_id", entityTable))
            .equals(Column.create("id", habitacionTable))
            .leftOuterJoin(clienteReservaHabitacionTable)
            .on(Column.create("cliente_reserva_habitacion_id", entityTable))
            .equals(Column.create("id", clienteReservaHabitacionTable))
            .leftOuterJoin(colaboradorReservaHabitacionTable)
            .on(Column.create("colaborador_reserva_habitacion_id", entityTable))
            .equals(Column.create("id", colaboradorReservaHabitacionTable))
            .leftOuterJoin(ocupantesTable)
            .on(Column.create("ocupantes_id", entityTable))
            .equals(Column.create("id", ocupantesTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, ReservarHabitacion.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<ReservarHabitacion> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<ReservarHabitacion> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private ReservarHabitacion process(Row row, RowMetadata metadata) {
        ReservarHabitacion entity = reservarhabitacionMapper.apply(row, "e");
        entity.setHabitacion(habitacionMapper.apply(row, "habitacion"));
        entity.setClienteReservaHabitacion(clienteMapper.apply(row, "clienteReservaHabitacion"));
        entity.setColaboradorReservaHabitacion(colaboradorMapper.apply(row, "colaboradorReservaHabitacion"));
        entity.setOcupantes(personaMapper.apply(row, "ocupantes"));
        return entity;
    }

    @Override
    public <S extends ReservarHabitacion> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
