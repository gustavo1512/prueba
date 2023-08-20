package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Factura;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Factura}, with proper type conversions.
 */
@Service
public class FacturaRowMapper implements BiFunction<Row, String, Factura> {

    private final ColumnConverter converter;

    public FacturaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Factura} stored in the database.
     */
    @Override
    public Factura apply(Row row, String prefix) {
        Factura entity = new Factura();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setMetodoPago(converter.fromRow(row, prefix + "_metodo_pago", String.class));
        entity.setSubtotal(converter.fromRow(row, prefix + "_subtotal", Double.class));
        entity.setMontoTotal(converter.fromRow(row, prefix + "_monto_total", Double.class));
        entity.setImpuesto(converter.fromRow(row, prefix + "_impuesto", Double.class));
        entity.setDetalleFacturaId(converter.fromRow(row, prefix + "_detalle_factura_id", Long.class));
        entity.setClienteFacturaId(converter.fromRow(row, prefix + "_cliente_factura_id", Long.class));
        entity.setClienteColaboradorId(converter.fromRow(row, prefix + "_cliente_colaborador_id", Long.class));
        return entity;
    }
}
