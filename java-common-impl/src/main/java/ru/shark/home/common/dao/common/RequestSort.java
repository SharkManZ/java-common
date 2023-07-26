package ru.shark.home.common.dao.common;

import ru.shark.home.common.enums.SortDirection;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class RequestSort {
    private String field;
    private SortDirection direction;

    public RequestSort(String field, String direction) {
        this.field = field;
        if (!isBlank(direction)) {
            this.direction = SortDirection.valueOf(direction.toUpperCase());
        }
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public SortDirection getDirection() {
        return direction;
    }

    public void setDirection(SortDirection direction) {
        this.direction = direction;
    }
}
