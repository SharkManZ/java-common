package ru.shark.home.common.dao.repository.query.parts;

import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public abstract class FromQueryPart {
    private String value;
    /**
     * Карта, где ключ - таблица, значение - информация о таблице
     */
    private Map<String, FromTableData> tables;
    /**
     * Карта, где ключ - алиас, значение - информация о таблице
     */
    private Map<String, FromTableData> aliasColumns;

    public FromQueryPart(String value) {
        this.value = value;
        tables = new LinkedHashMap<>();
        aliasColumns = new HashMap<>();
    }

    /**
     * Возвращает полное значение FROM части запроса.
     */
    public String getValue() {
        return value.trim();
    }

    /**
     * Добавляет привязку алиаса таблицы и ее доступных колонок.
     *
     * @param alias   алиас
     * @param columns коллекция колонок
     */
    protected void addTableAliasWithColumns(String alias, Set<String> columns) {
        aliasColumns.put(alias, new FromTableData(alias, columns));
    }

    /**
     * Добавляет таблицу и ее алиас.
     *
     * @param table таблица
     * @param alias алиас
     */
    protected void addTableWithAlias(String table, String alias) {
        tables.put(table, new FromTableData(table, alias));
    }

    /**
     * Проверяет наличие алиаса по таблице
     *
     * @param table таблица
     * @return признак наличия алиаса
     */
    protected boolean hasAliasByTable(String table) {
        return tables.containsKey(table);
    }

    /**
     * Возвращает алиас по таблице.
     *
     * @param table таблица
     * @return алиас
     */
    protected String getAliasByTable(String table) {
        return tables.get(table).getAlias();
    }

    /**
     * Возвращает алиас по колонке. Ищет первый алиас в перечне колонок которого есть указанная колонке и возвращает его.
     * Если алиас не найден бросает исключение
     *
     * @param column колонка
     * @return алиас
     */
    protected String getAliasByColumn(String column) {
        return aliasColumns.entrySet().stream().filter(item -> item.getValue().hasColumn(column))
                .map(Map.Entry::getKey)
                .findFirst().orElseThrow(() -> new ValidationException("Не найден алиас в запросе по колонке: " + column));
    }
}
