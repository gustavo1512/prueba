package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Persona;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Persona}, with proper type conversions.
 */
@Service
public class PersonaRowMapper implements BiFunction<Row, String, Persona> {

    private final ColumnConverter converter;

    public PersonaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Persona} stored in the database.
     */
    @Override
    public Persona apply(Row row, String prefix) {
        Persona entity = new Persona();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFechaNacimiento(converter.fromRow(row, prefix + "_fecha_nacimiento", Instant.class));
        entity.setTipo(converter.fromRow(row, prefix + "_tipo", String.class));
        return entity;
    }
}
