package ru.shark.home.common.services;

import ru.shark.home.common.dao.entity.BaseEntity;

public class LogicTestInnerEntity extends BaseEntity {
    private Long id;
    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
