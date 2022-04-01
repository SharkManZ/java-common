package ru.shark.home.common.dao.repository.query;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Описание FROM части запроса. Содержит полный текст части и дополнительную информацию.
 */
public class FromQueryPart {
    private String value;
    private String mainTableAlias;
    private Map<String, String> tables;

    public FromQueryPart(String value) {
        this.value = value;
        String[] parts = value.replaceAll("[\\s]{2,}", " ").trim().split(" ");
        if (parts.length > 2) {
            mainTableAlias = parts[2];
        }
        tables = new LinkedHashMap<>();
        int idx = 0;
        while (idx < parts.length) {
            String part = parts[idx];
            if (part.equalsIgnoreCase("from")) {
                tables.put(parts[idx + 1], parts.length > idx + 2 ? parts[idx + 2] : null);
            } else if (part.contains(".")) {
                String[] split = part.split("\\.");
                tables.put(split[1], parts[idx + 1]);
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

    /**
     * Возвращает полное значение FROM части запроса.
     */
    public String getValue() {
        return value.trim();
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
            if (tables.containsKey(split[idx])) {
                lastFoundIdx = idx;
                lastAlias = tables.get(split[idx]);
            }
            idx++;
        }
        if (StringUtils.isBlank(lastAlias)) {
            return fieldChain;
        }
        return lastAlias + "." + String.join(".", Arrays.copyOfRange(split, lastFoundIdx + 1, split.length));
    }
}
