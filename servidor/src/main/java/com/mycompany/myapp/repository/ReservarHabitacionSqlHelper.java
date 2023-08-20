package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ReservarHabitacionSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("fecha_reserva", table, columnPrefix + "_fecha_reserva"));
        columns.add(Column.aliased("fecha_inicio", table, columnPrefix + "_fecha_inicio"));
        columns.add(Column.aliased("fecha_final", table, columnPrefix + "_fecha_final"));
        columns.add(Column.aliased("total_reservacion", table, columnPrefix + "_total_reservacion"));

        columns.add(Column.aliased("habitacion_id", table, columnPrefix + "_habitacion_id"));
        columns.add(Column.aliased("cliente_reserva_habitacion_id", table, columnPrefix + "_cliente_reserva_habitacion_id"));
        columns.add(Column.aliased("colaborador_reserva_habitacion_id", table, columnPrefix + "_colaborador_reserva_habitacion_id"));
        columns.add(Column.aliased("ocupantes_id", table, columnPrefix + "_ocupantes_id"));
        return columns;
    }
}
