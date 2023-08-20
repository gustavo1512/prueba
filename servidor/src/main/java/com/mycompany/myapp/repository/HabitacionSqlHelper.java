package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class HabitacionSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("tipo", table, columnPrefix + "_tipo"));
        columns.add(Column.aliased("capacidad_adulto", table, columnPrefix + "_capacidad_adulto"));
        columns.add(Column.aliased("capacidad_menor", table, columnPrefix + "_capacidad_menor"));
        columns.add(Column.aliased("disponible", table, columnPrefix + "_disponible"));

        columns.add(Column.aliased("tarifa_id", table, columnPrefix + "_tarifa_id"));
        return columns;
    }
}
