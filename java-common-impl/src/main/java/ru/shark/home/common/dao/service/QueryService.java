package ru.shark.home.common.dao.service;

import ru.shark.home.common.dao.repository.query.ParamsQuery;
import ru.shark.home.common.dao.common.RequestCriteria;
import ru.shark.home.common.dao.repository.query.ParsedQuery;

import java.util.List;
import java.util.Map;

/**
 * Интерфейс сервисов для преобразования переданных запросов с учетом критериев запроса.
 * Поддерживается добавление к запрос условий поиска по переданному набору полей (через ИЛИ).
 */
public interface QueryService {
    ParsedQuery parseQuery(String query);

    ParsedQuery parseQuery(String query, List<String> searchFields);

    ParsedQuery parseQuery(String query, List<String> searchFields, List<String> advancedSearchFields);

    ParamsQuery generateParamsQuery(ParsedQuery query, RequestCriteria requestCriteria);

    ParamsQuery generateParamsQuery(ParsedQuery query, RequestCriteria requestCriteria, Map<String, Object> params);
}
