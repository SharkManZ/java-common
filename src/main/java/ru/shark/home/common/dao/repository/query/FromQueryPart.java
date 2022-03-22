package ru.shark.home.common.dao.repository.query;

/**
 * Описание FROM части запроса. Содержит полный текст части и дополнительную информацию.
 */
public class FromQueryPart {
    private String value;
    private String mainTable;
    private String mainTableAlias;

    public FromQueryPart(String value) {
        this.value = value;
        String[] parts = value.replaceAll("[\\s]{2,}", " ").trim().split(" ");
        mainTable = parts[1];
        if (parts.length > 2) {
            mainTableAlias = parts[2];
        }
    }

    /**
     * Возвращает название главной таблицы запроса.
     */
    public String getMainTable() {
        return mainTable;
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
}
