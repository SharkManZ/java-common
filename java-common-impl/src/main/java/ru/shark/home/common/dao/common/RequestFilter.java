package ru.shark.home.common.dao.common;

import ru.shark.home.common.enums.FieldType;
import ru.shark.home.common.enums.FilterOperation;

import java.text.MessageFormat;

import static ru.shark.home.common.common.ErrorConstants.INVALID_FILTER_OPERATION;

public class RequestFilter {
    private String field;
    private FilterOperation operation;
    private FieldType fieldType;
    private String value;

    public RequestFilter(String field, FieldType fieldType, String operation, String value) {
        this.field = field;
        this.fieldType = fieldType;
        this.operation = FilterOperation.byValue(operation);
        if (this.operation == null) {
            throw new IllegalArgumentException(MessageFormat.format(INVALID_FILTER_OPERATION, operation));
        }
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public FilterOperation getOperation() {
        return operation;
    }

    public void setOperation(FilterOperation operation) {
        this.operation = operation;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }
}
