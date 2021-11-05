package ru.shark.home.common.dao.common;

import java.util.List;

/**
 * Обертка для пагинированного ответа.
 */
public class PageableList<T> {
    private List<T> data;
    private Long totalCount;

    public PageableList(List<T> data, Long totalCount) {
        this.data = data;
        this.totalCount = totalCount;
    }

    public List<T> getData() {
        return data;
    }

    public Long getTotalCount() {
        return totalCount;
    }
}
