package ru.shark.home.common.dto;

public class FileDto {
    private String fileName;
    private Object data;

    public FileDto(String fileName, Object data) {
        this.fileName = fileName;
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
