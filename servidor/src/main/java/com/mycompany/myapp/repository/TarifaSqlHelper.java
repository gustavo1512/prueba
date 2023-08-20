package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class TarifaSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("tipo_tarifa", table, columnPrefix + "_tipo_tarifa"));
        columns.add(Column.aliased("tarifa_adulto", table, columnPrefix + "_tarifa_adulto"));
        columns.add(Column.aliased("tarifa_menor", table, columnPrefix + "_tarifa_menor"));

        return columns;
    }
}
