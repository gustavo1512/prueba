package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.ReservarEvento;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ReservarEvento}, with proper type conversions.
 */
@Service
public class ReservarEventoRowMapper implements BiFunction<Row, String, ReservarEvento> {

    private final ColumnConverter converter;

    public ReservarEventoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ReservarEvento} stored in the database.
     */
    @Override
    public ReservarEvento apply(Row row, String prefix) {
        ReservarEvento entity = new ReservarEvento();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFechaReservacion(converter.fromRow(row, prefix + "_fecha_reservacion", Instant.class));
        entity.setTotalReservacion(converter.fromRow(row, prefix + "_total_reservacion", Double.class));
        entity.setClienteReservaEventoId(converter.fromRow(row, prefix + "_cliente_reserva_evento_id", Long.class));
        entity.setColaboradorReservaEventoId(converter.fromRow(row, prefix + "_colaborador_reserva_evento_id", Long.class));
        return entity;
    }
}
