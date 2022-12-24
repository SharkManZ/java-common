package ru.shark.home.common.dao.repository;

import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import ru.shark.home.common.dao.common.PageableList;
import ru.shark.home.common.dao.common.RequestCriteria;
import ru.shark.home.common.dao.common.RequestSort;
import ru.shark.home.common.dao.dto.Dto;
import ru.shark.home.common.dao.entity.BaseEntity;
import ru.shark.home.common.dao.repository.query.CriteriaQueryBuilder;
import ru.shark.home.common.dao.repository.query.ParamsQuery;
import ru.shark.home.common.dao.service.HqlQueryService;
import ru.shark.home.common.dao.service.QueryService;
import ru.shark.home.common.dao.service.SqlQueryService;
import ru.shark.home.common.dao.specification.SpecificationRequest;
import ru.shark.home.common.dao.util.SpecificationUtils;
import ru.shark.home.common.enums.SortDirection;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

public class JpaBaseRepository<E extends BaseEntity> extends SimpleJpaRepository<E, Long> implements BaseRepository<E> {
    private QueryService hqlQueryService;
    private QueryService sqlQueryService;
    private EntityManager em;

    public JpaBaseRepository(JpaEntityInformation<E, ?> entityInformation, EntityManager entityManager,
                             HqlQueryService hqlQueryService, SqlQueryService sqlQueryService) {
        super(entityInformation, entityManager);
        this.hqlQueryService = hqlQueryService;
        this.sqlQueryService = sqlQueryService;
        this.em = entityManager;
    }

    @Override
    public PageableList<E> getWithPagination(RequestCriteria request) {
        return getWithPagination(request, null);
    }

    @Override
    public PageableList<E> getWithPagination(RequestCriteria request, Specification searchSpecification) {
        return getWithPagination(request, searchSpecification, null);
    }

    @Override
    public PageableList<E> getWithPagination(RequestCriteria request, Specification searchSpecification, String... defaultSort) {
        Specification filterSpec = new SpecificationRequest(request);
        PageRequest pageRequest;
        Sort sort = getSortFromRequest(request.getSorts(), defaultSort);
        if (sort == null) {
            pageRequest = PageRequest.of(request.getPage(), request.getSize());
        } else {
            pageRequest = PageRequest.of(request.getPage(), request.getSize(), getSortFromRequest(request.getSorts(), defaultSort));
        }
        Page<E> all = findAll(SpecificationUtils.andSpecifications(filterSpec, searchSpecification), pageRequest);
        return new PageableList<>(all.getContent(), all.getTotalElements());
    }

    @Override
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

    @Override
    public <T extends Dto> PageableList<T> getNativeWithPagination(String queryName, RequestCriteria requestCriteria, Map<String, Object> params, List<String> searchFields,
                                                                   String resultSetMappingName) {
        return getNativeWithPagination(queryName, requestCriteria, params, searchFields, null, resultSetMappingName);
    }

    @Override
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

    private Sort getSortFromRequest(List<RequestSort> sortList, String... defaultSort) {
        if (isEmpty(sortList) && isEmpty(defaultSort)) {
            return null;
        }
        List<Sort.Order> orders = !isEmpty(sortList) ? getOrdersByRequestSortList(sortList) :
                getOrdersByDefaultSort(defaultSort);

        return Sort.by(orders);
    }

    private List<Sort.Order> getOrdersByRequestSortList(List<RequestSort> sortList) {
        return sortList.stream()
                .map(item -> (item.getDirection() == null || SortDirection.ASC.equals(item.getDirection())) ?
                        Sort.Order.asc(item.getField()) : Sort.Order.desc(item.getField()))
                .collect(Collectors.toList());
    }

    private List<Sort.Order> getOrdersByDefaultSort(String... defaultSort) {
        List<Sort.Order> list = new ArrayList<>();
        for (String sort : defaultSort) {
            String[] sortChain = sort.split(" ");
            if (sortChain.length == 1) {
                list.add(Sort.Order.asc(sort));
            } else {
                SortDirection direction = SortDirection.valueOf(sortChain[1]);
                list.add(SortDirection.ASC.equals(direction) ? Sort.Order.asc(sortChain[0]) :
                        Sort.Order.desc(sortChain[0]));
            }
        }

        return list;
    }
}
