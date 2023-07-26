package ru.shark.home.common.dao.repository.query.parts;

import java.util.HashSet;
import java.util.Set;

public class FromTableData {
    private String table;
    private String alias;
    private Set<String> columns;

    public FromTableData(String table, String alias) {
        this.table = table;
        this.alias = alias;
        this.columns = new HashSet<>();
    }

    public FromTableData(String alias, Set<String> columns) {
        this.alias = alias;
        this.columns = columns;
    }

    public boolean hasColumn(String column) {
        return columns.contains(column);
    }

    public String getAlias() {
        return alias;
    }
}
