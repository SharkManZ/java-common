package ru.shark.home.common.services;

import ru.shark.home.common.dao.entity.BaseEntity;

public class LogicTestEntity extends BaseEntity {
    private Long id;
    private String name;
    private String type;
    private String mode;
    private Integer intField;
    private Long longField;
    private TestEnum testEnum;
    private LogicTestInnerEntity innerEntity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Integer getIntField() {
        return intField;
    }

    public void setIntField(Integer intField) {
        this.intField = intField;
    }

    public Long getLongField() {
        return longField;
    }

    public void setLongField(Long longField) {
        this.longField = longField;
    }

    public TestEnum getTestEnum() {
        return testEnum;
    }

    public void setTestEnum(TestEnum testEnum) {
        this.testEnum = testEnum;
    }

    public LogicTestInnerEntity getInnerEntity() {
        return innerEntity;
    }

    public void setInnerEntity(LogicTestInnerEntity innerEntity) {
        this.innerEntity = innerEntity;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
