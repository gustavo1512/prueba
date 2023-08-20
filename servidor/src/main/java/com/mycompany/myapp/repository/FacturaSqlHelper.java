package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class FacturaSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("metodo_pago", table, columnPrefix + "_metodo_pago"));
        columns.add(Column.aliased("subtotal", table, columnPrefix + "_subtotal"));
        columns.add(Column.aliased("monto_total", table, columnPrefix + "_monto_total"));
        columns.add(Column.aliased("impuesto", table, columnPrefix + "_impuesto"));

        columns.add(Column.aliased("detalle_factura_id", table, columnPrefix + "_detalle_factura_id"));
        columns.add(Column.aliased("cliente_factura_id", table, columnPrefix + "_cliente_factura_id"));
        columns.add(Column.aliased("cliente_colaborador_id", table, columnPrefix + "_cliente_colaborador_id"));
        return columns;
    }
}
