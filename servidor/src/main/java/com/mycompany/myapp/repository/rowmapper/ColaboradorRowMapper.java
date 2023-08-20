package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Colaborador;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Colaborador}, with proper type conversions.
 */
@Service
public class ColaboradorRowMapper implements BiFunction<Row, String, Colaborador> {

    private final ColumnConverter converter;

    public ColaboradorRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Colaborador} stored in the database.
     */
    @Override
    public Colaborador apply(Row row, String prefix) {
        Colaborador entity = new Colaborador();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNombreColaborador(converter.fromRow(row, prefix + "_nombre_colaborador", String.class));
        entity.setNumTelefono(converter.fromRow(row, prefix + "_num_telefono", Long.class));
        entity.setCorreo(converter.fromRow(row, prefix + "_correo", String.class));
        entity.setTipoCargoColaboradorId(converter.fromRow(row, prefix + "_tipo_cargo_colaborador_id", Long.class));
        return entity;
    }
}
