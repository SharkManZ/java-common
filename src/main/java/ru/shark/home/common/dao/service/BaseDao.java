package ru.shark.home.common.dao.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.shark.home.common.dao.common.PageableList;
import ru.shark.home.common.dao.common.RequestCriteria;
import ru.shark.home.common.dao.dto.Dto;
import ru.shark.home.common.dao.entity.BaseEntity;
import ru.shark.home.common.dao.repository.query.CriteriaQueryBuilder;
import ru.shark.home.common.dao.repository.query.ParamsQuery;
import ru.shark.home.common.dao.util.ConverterUtil;
import ru.shark.home.common.services.dto.Filter;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.ObjectUtils.isEmpty;

/**
 * Базовый класс для сервисов доступа к данным.
 */
@Transactional(Transactional.TxType.REQUIRED)
public abstract class BaseDao<E extends BaseEntity> {

    private ConverterUtil converterUtil;
    private EntityManager em;
    private final Class<E> entityClass;
    private SqlQueryService sqlQueryService;
    private HqlQueryService hqlQueryService;

    protected BaseDao(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Сохранение сущности.
     *
     * @param entity сущность для сохранения
     * @return сохраненная сущность
     */
    public E save(E entity) {
        return em.merge(entity);
    }

    /**
     * Удаление сущности по идентификатору.
     */
    public void deleteById(Long id) {
        em.remove(findById(id));
    }

    /**
     * Удаление сущности.
     *
     * @param entity сущность для удаления
     */
    public void delete(E entity) {
        deleteById(entity.getId());
    }

    /**
     * Возвращает сущность по идентификатору
     *
     * @param id идентификатор
     * @return сущность
     */
    public E findById(Long id) {
        return em.find(entityClass, id);
    }

    /**
     * Возвращает все сущности.
     *
     * @return список сущностей
     */
    public List<E> findAll() {
        return em.createQuery("select t from " + entityClass.getSimpleName() + " t").getResultList();
    }

    /**
     * Возвращает класс сущности.
     */
    public Class<E> getEntityClass() {
        return entityClass;
    }

    /**
     * Возвращает фильтр из списка по ключу поля
     *
     * @param filters коллекция фильтров
     * @param field   фильтр
     * @return фильтр или null
     */
    protected Filter getFilterValueByField(List<Filter> filters, String field) {
        if (isEmpty(filters)) {
            return null;
        }

        return filters.stream().filter(item -> !isBlank(item.getField()) &&
                item.getField().equalsIgnoreCase(field)).findFirst().orElse(null);
    }

    public PageableList<E> getWithPagination(String queryName, RequestCriteria requestCriteria,
                                             Map<String, Object> params,
                                             List<String> searchFields) {
        CriteriaQueryBuilder criteriaQueryBuilder = hqlQueryService.prepareNamedQuery(queryName, searchFields);
        ParamsQuery query = criteriaQueryBuilder.build(requestCriteria, params);
        List<E> resultList = applyQueryParams(em.createQuery(query.getQueryString()),
                query.getParams())
                .setFirstResult(requestCriteria.getPage() * requestCriteria.getSize())
                .setMaxResults(requestCriteria.getSize())
                .getResultList();
        Query countQuery;
        if (query.isCountNative()) {
            countQuery = em.createNativeQuery(query.getCountQueryString());
        } else {
            countQuery = em.createQuery(query.getCountQueryString());
        }
        Long count = (Long) applyQueryParams(countQuery, query.getParams())
                .getSingleResult();
        return new PageableList<>(resultList, count);
    }

    public <T extends Dto> PageableList<T> getNativeWithPagination(String queryName, RequestCriteria requestCriteria, Map<String, Object> params, List<String> searchFields,
                                                                   String resultSetMappingName) {
        return getNativeWithPagination(queryName, requestCriteria, params, searchFields, null, resultSetMappingName);
    }

    public <T extends Dto> PageableList<T> getNativeWithPagination(String queryName, RequestCriteria requestCriteria, Map<String, Object> params, List<String> searchFields, List<String> advancedSearchFields, String resultSetMappingName) {
        CriteriaQueryBuilder criteriaQueryBuilder = sqlQueryService.prepareNamedQuery(queryName, searchFields, advancedSearchFields);
        ParamsQuery query = criteriaQueryBuilder.build(requestCriteria, params);
        List<T> resultList = applyQueryParams(em.createNativeQuery(query.getQueryString(), resultSetMappingName),
                query.getParams())
                .setFirstResult(requestCriteria.getPage() * requestCriteria.getSize())
                .setMaxResults(requestCriteria.getSize())
                .getResultList();
        Long count = ((BigInteger) applyQueryParams(em.createNativeQuery(query.getCountQueryString()), query.getParams())
                .getSingleResult()).longValue();
        return new PageableList<>(resultList, count);
    }

    private Query applyQueryParams(Query query, Map<String, Object> params) {
        if (isEmpty(params)) {
            return query;
        }
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query;
    }

    public ConverterUtil getConverterUtil() {
        return converterUtil;
    }

    @Autowired
    public void setEm(EntityManager em) {
        this.em = em;
    }

    @Autowired
    public void setConverterUtil(ConverterUtil converterUtil) {
        this.converterUtil = converterUtil;
    }

    @Autowired
    public void setSqlQueryService(SqlQueryService sqlQueryService) {
        this.sqlQueryService = sqlQueryService;
    }

    @Autowired
    public void setHqlQueryService(HqlQueryService hqlQueryService) {
        this.hqlQueryService = hqlQueryService;
    }
}
