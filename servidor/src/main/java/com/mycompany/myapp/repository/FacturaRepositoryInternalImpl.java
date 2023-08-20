package com.mycompany.myapp.repository;


import com.mycompany.myapp.domain.Factura;
import com.mycompany.myapp.repository.rowmapper.ClienteRowMapper;
import com.mycompany.myapp.repository.rowmapper.ColaboradorRowMapper;
import com.mycompany.myapp.repository.rowmapper.DetalleFacturaRowMapper;
import com.mycompany.myapp.repository.rowmapper.FacturaRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Factura entity.
 */
@SuppressWarnings("unused")
class FacturaRepositoryInternalImpl extends SimpleR2dbcRepository<Factura, Long> implements FacturaRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final DetalleFacturaRowMapper detallefacturaMapper;
    private final ClienteRowMapper clienteMapper;
    private final ColaboradorRowMapper colaboradorMapper;
    private final FacturaRowMapper facturaMapper;

    private static final Table entityTable = Table.aliased("factura", EntityManager.ENTITY_ALIAS);
    private static final Table detalleFacturaTable = Table.aliased("detalle_factura", "detalleFactura");
    private static final Table clienteFacturaTable = Table.aliased("cliente", "clienteFactura");
    private static final Table clienteColaboradorTable = Table.aliased("colaborador", "clienteColaborador");

    public FacturaRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        DetalleFacturaRowMapper detallefacturaMapper,
        ClienteRowMapper clienteMapper,
        ColaboradorRowMapper colaboradorMapper,
        FacturaRowMapper facturaMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Factura.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.detallefacturaMapper = detallefacturaMapper;
        this.clienteMapper = clienteMapper;
        this.colaboradorMapper = colaboradorMapper;
        this.facturaMapper = facturaMapper;
    }

    @Override
    public Flux<Factura> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Factura> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = FacturaSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(DetalleFacturaSqlHelper.getColumns(detalleFacturaTable, "detalleFactura"));
        columns.addAll(ClienteSqlHelper.getColumns(clienteFacturaTable, "clienteFactura"));
        columns.addAll(ColaboradorSqlHelper.getColumns(clienteColaboradorTable, "clienteColaborador"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(detalleFacturaTable)
            .on(Column.create("detalle_factura_id", entityTable))
            .equals(Column.create("id", detalleFacturaTable))
            .leftOuterJoin(clienteFacturaTable)
            .on(Column.create("cliente_factura_id", entityTable))
            .equals(Column.create("id", clienteFacturaTable))
            .leftOuterJoin(clienteColaboradorTable)
            .on(Column.create("cliente_colaborador_id", entityTable))
            .equals(Column.create("id", clienteColaboradorTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Factura.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Factura> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Factura> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Factura process(Row row, RowMetadata metadata) {
        Factura entity = facturaMapper.apply(row, "e");
        entity.setDetalleFactura(detallefacturaMapper.apply(row, "detalleFactura"));
        entity.setClienteFactura(clienteMapper.apply(row, "clienteFactura"));
        entity.setClienteColaborador(colaboradorMapper.apply(row, "clienteColaborador"));
        return entity;
    }

    @Override
    public <S extends Factura> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
