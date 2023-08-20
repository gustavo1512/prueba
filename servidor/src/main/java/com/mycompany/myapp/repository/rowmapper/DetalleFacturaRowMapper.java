package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.DetalleFactura;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link DetalleFactura}, with proper type conversions.
 */
@Service
public class DetalleFacturaRowMapper implements BiFunction<Row, String, DetalleFactura> {

    private final ColumnConverter converter;

    public DetalleFacturaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link DetalleFactura} stored in the database.
     */
    @Override
    public DetalleFactura apply(Row row, String prefix) {
        DetalleFactura entity = new DetalleFactura();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFechaEmitido(converter.fromRow(row, prefix + "_fecha_emitido", Long.class));
        entity.setHabitacionReservadaId(converter.fromRow(row, prefix + "_habitacion_reservada_id", Long.class));
        entity.setEventoReservadoId(converter.fromRow(row, prefix + "_evento_reservado_id", Long.class));
        return entity;
    }
}
