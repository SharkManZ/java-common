package ru.shark.home.common.dao.service;

import ru.shark.home.common.dao.common.RequestCriteria;
import ru.shark.home.common.dao.repository.query.CriteriaQueryBuilder;

import java.util.List;

/**
 * Интерфейс сервисов для преобразования переданных запросов с учетом критериев запроса.
 * Поддерживается добавление к запрос условий поиска по переданному набору полей (через ИЛИ).
 */
public interface QueryService {
    CriteriaQueryBuilder prepareNamedQuery(String name);

    CriteriaQueryBuilder prepareNamedQuery(String name, List<String> searchFields);

    CriteriaQueryBuilder prepareQuery(String query);

    CriteriaQueryBuilder prepareQuery(String query, List<String> searchFields);
}
