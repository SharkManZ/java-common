package ru.shark.home.common.dao.repository.query;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Типы частей запроса.
 */
public enum QueryPartType {
    SELECT,
    FROM("SELECT"),
    WHERE("FROM"),
    GROUP("FROM", "WHERE"),
    ORDER("FROM", "WHERE", "GROUP");

    /**
     * Совместимые типы.
     */
    private Set<String> compatibleTypes;

    QueryPartType(String... compatibleTypes) {
        this.compatibleTypes = new HashSet<>(Arrays.asList(compatibleTypes));
    }

    /**
     * Проверка на совместимость переданного типа.
     */
    public boolean compatibleWith(String type) {
        return this.compatibleTypes.contains(type);
    }
}