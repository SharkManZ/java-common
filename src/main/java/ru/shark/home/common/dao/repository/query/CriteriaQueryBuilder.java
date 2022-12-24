package ru.shark.home.common.dao.repository.query;

import ru.shark.home.common.dao.common.RequestCriteria;

import java.util.Map;

/**
 * Интерфейс генерации запроса с учетом переданных критериев (поиск, фильтрация, сортировка)
 */
public interface CriteriaQueryBuilder {

    /**
     * Возвращает запрос с добавленными условиями
     *
     * @param requestCriteria условия.
     * @return результирующий запрос
     */
    ParamsQuery build(RequestCriteria requestCriteria);

    /**
     * Возвращает запрос с добавленными условиями.
     *
     * @param requestCriteria условия.
     * @return результирующий запрос
     */
    ParamsQuery build(RequestCriteria requestCriteria, Map<String, Object> params);
}
