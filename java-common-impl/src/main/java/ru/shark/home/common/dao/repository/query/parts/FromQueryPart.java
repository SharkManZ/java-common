package ru.shark.home.common.dao.repository.query.parts;

public abstract class FromQueryPart {
    private String value;


    public FromQueryPart(String value) {
        this.value = value;
    }

    /**
     * Возвращает полное значение FROM части запроса.
     */
    public String getValue() {
        return value.trim();
    }
}
