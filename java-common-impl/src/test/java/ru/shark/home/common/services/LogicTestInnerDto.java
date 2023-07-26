package ru.shark.home.common.services;

import ru.shark.home.common.dao.common.EntityClass;

@EntityClass(clazz = LogicTestInnerEntity.class)
public class LogicTestInnerDto {
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
