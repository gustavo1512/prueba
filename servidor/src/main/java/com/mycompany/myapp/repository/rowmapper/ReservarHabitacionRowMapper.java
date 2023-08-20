package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.ReservarHabitacion;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ReservarHabitacion}, with proper type conversions.
 */
@Service
public class ReservarHabitacionRowMapper implements BiFunction<Row, String, ReservarHabitacion> {

    private final ColumnConverter converter;

    public ReservarHabitacionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ReservarHabitacion} stored in the database.
     */
    @Override
    public ReservarHabitacion apply(Row row, String prefix) {
        ReservarHabitacion entity = new ReservarHabitacion();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFechaReserva(converter.fromRow(row, prefix + "_fecha_reserva", Instant.class));
        entity.setFechaInicio(converter.fromRow(row, prefix + "_fecha_inicio", Instant.class));
        entity.setFechaFinal(converter.fromRow(row, prefix + "_fecha_final", Instant.class));
        entity.setTotalReservacion(converter.fromRow(row, prefix + "_total_reservacion", Double.class));
        entity.setHabitacionId(converter.fromRow(row, prefix + "_habitacion_id", Long.class));
        entity.setClienteReservaHabitacionId(converter.fromRow(row, prefix + "_cliente_reserva_habitacion_id", Long.class));
        entity.setColaboradorReservaHabitacionId(converter.fromRow(row, prefix + "_colaborador_reserva_habitacion_id", Long.class));
        entity.setOcupantesId(converter.fromRow(row, prefix + "_ocupantes_id", Long.class));
        return entity;
    }
}
