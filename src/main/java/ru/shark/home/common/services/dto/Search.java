package ru.shark.home.common.services.dto;

public class Search {
    private String value;
    private boolean equals;

    public Search() {

    }

    public Search(String value) {
        this.value = value;
    }

    public Search(String value, boolean isEquals) {
        this.value = value;
        this.equals = isEquals;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isEquals() {
        return equals;
    }

    public void setEquals(boolean equals) {
        this.equals = equals;
    }
}
