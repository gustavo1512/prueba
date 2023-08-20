package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Evento;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Evento}, with proper type conversions.
 */
@Service
public class EventoRowMapper implements BiFunction<Row, String, Evento> {

    private final ColumnConverter converter;

    public EventoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Evento} stored in the database.
     */
    @Override
    public Evento apply(Row row, String prefix) {
        Evento entity = new Evento();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNombreEvento(converter.fromRow(row, prefix + "_nombre_evento", String.class));
        entity.setFechaHora(converter.fromRow(row, prefix + "_fecha_hora", Instant.class));
        entity.setCapacidadAdulto(converter.fromRow(row, prefix + "_capacidad_adulto", Long.class));
        entity.setCapacidadMenor(converter.fromRow(row, prefix + "_capacidad_menor", Long.class));
        entity.setTarifaId(converter.fromRow(row, prefix + "_tarifa_id", Long.class));
        entity.setEncargadoId(converter.fromRow(row, prefix + "_encargado_id", Long.class));
        return entity;
    }
}
