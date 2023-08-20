package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class DetalleFacturaSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("fecha_emitido", table, columnPrefix + "_fecha_emitido"));

        columns.add(Column.aliased("habitacion_reservada_id", table, columnPrefix + "_habitacion_reservada_id"));
        columns.add(Column.aliased("evento_reservado_id", table, columnPrefix + "_evento_reservado_id"));
        return columns;
    }
}
