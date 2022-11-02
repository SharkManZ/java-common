package ru.shark.home.common.dao.repository.query.parts;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Описание FROM части запроса. Содержит полный текст части и дополнительную информацию.
 */
public class HqlFromQueryPart extends FromQueryPart {

    private String mainTableAlias;

    public HqlFromQueryPart(String value) {
        super(value);
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
}
