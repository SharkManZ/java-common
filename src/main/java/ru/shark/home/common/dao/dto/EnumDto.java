package ru.shark.home.common.dao.dto;

public class EnumDto {
    private String code;
    private String title;

    public EnumDto() {
        // empty constructor
    }

    public EnumDto(String code, String title) {
        this.code = code;
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
