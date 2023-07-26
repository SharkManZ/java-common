package ru.shark.home.common.dao.repository.query.parts;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ValidationException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Описание FROM части запроса. Содержит полный текст части и дополнительную информацию.
 */
public class HqlFromQueryPart extends FromQueryPart {
    /**
     * Карта, где ключ - таблица, значение - информация о таблице
     */
    private Map<String, FromTableData> tables;
    /**
     * Карта, где ключ - алиас, значение - информация о таблице
     */
    private Map<String, FromTableData> aliasColumns;
    private String mainTableAlias;

    public HqlFromQueryPart(String value) {
        super(value);
        tables = new LinkedHashMap<>();
        aliasColumns = new HashMap<>();
        String[] parts = value.replaceAll("[\\s]{2,}", " ").trim().split(" ");
        if (parts.length > 2) {
            mainTableAlias = parts[2];
        }
        int idx = 0;
        while (idx < parts.length) {
            String part = parts[idx];
            if (part.equalsIgnoreCase("from")) {
                addTableWithAlias(parts[idx + 1], parts.length > idx + 2 ? parts[idx + 2] : null);
            } else if (part.contains(".")) {
                String[] split = part.split("\\.");
                addTableWithAlias(split[1], parts[idx + 1]);
            }
            idx++;
        }
    }

    /**
     * Возвращает алиас главной таблицы запроса.
     */
    public String getMainTableAlias() {
        return mainTableAlias;
    }

    public String transformFieldChain(String fieldChain) {
        if (StringUtils.isBlank(fieldChain)) {
            return fieldChain;
        }
        String[] split = fieldChain.split("\\.");
        if (split.length == 1) {
            return fieldChain;
        }
        int idx = 0;
        int lastFoundIdx = 0;
        String lastAlias = null;
        while (idx < split.length) {
            if (hasAliasByTable(split[idx])) {
                lastFoundIdx = idx;
                lastAlias = getAliasByTable(split[idx]);
            }
            idx++;
        }
        if (StringUtils.isBlank(lastAlias)) {
            return fieldChain;
        }
        return lastAlias + "." + String.join(".", Arrays.copyOfRange(split, lastFoundIdx + 1, split.length));
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
