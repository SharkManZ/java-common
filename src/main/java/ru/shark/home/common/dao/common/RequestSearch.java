package ru.shark.home.common.dao.common;

public class RequestSearch {
    private String value;
    private boolean isEquals;

    public RequestSearch(String value, boolean isEquals) {
        this.value = value;
        this.isEquals = isEquals;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isEquals() {
        return isEquals;
    }

    public void setEquals(boolean equals) {
        isEquals = equals;
    }
}
