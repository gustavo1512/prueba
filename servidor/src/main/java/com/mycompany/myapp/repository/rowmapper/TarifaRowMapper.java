package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Tarifa;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Tarifa}, with proper type conversions.
 */
@Service
public class TarifaRowMapper implements BiFunction<Row, String, Tarifa> {

    private final ColumnConverter converter;

    public TarifaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Tarifa} stored in the database.
     */
    @Override
    public Tarifa apply(Row row, String prefix) {
        Tarifa entity = new Tarifa();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTipoTarifa(converter.fromRow(row, prefix + "_tipo_tarifa", String.class));
        entity.setTarifaAdulto(converter.fromRow(row, prefix + "_tarifa_adulto", Double.class));
        entity.setTarifaMenor(converter.fromRow(row, prefix + "_tarifa_menor", Double.class));
        return entity;
    }
}
