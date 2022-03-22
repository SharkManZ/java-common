package ru.shark.home.common.dao.repository.query;

import org.apache.commons.text.CaseUtils;
import ru.shark.home.common.dao.common.RequestCriteria;
import ru.shark.home.common.dao.common.RequestFilter;
import ru.shark.home.common.dao.common.RequestSearch;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.ObjectUtils.isEmpty;

/**
 * Имплементация дополненного критериями выборки запроса для Hql запросов.
 */
public class HqlCriteriaQueryBuilder implements CriteriaQueryBuilder {
    private static final String SEARCH_EQ_TPL = "lower({0}) = lower(''{1}'')";
    private static final String SEARCH_LIKE_TPL = "lower({0}) like ''%'' || lower(''{1}'') || ''%''";
    private static final String FILTER_STRING_EQ_TPL = "lower({0}) = lower(:{1})";
    private static final String FILTER_NUMBER_EQ_TPL = "{0} = :{1}";
    private static final String FILTER_STRING_LIKE_TPL = "lower({0}) like ''%'' || lower(:{1}) || ''%''";

    private String selectPart;
    private FromQueryPart fromPart;
    private String wherePart;
    private String groupPart;
    private String orderPart;
    private String searchClause;
    private List<String> searchFields;

    public HqlCriteriaQueryBuilder() {
        // empty constructor
    }

    public HqlCriteriaQueryBuilder(List<String> searchFields) {
        this.searchFields = searchFields;
    }

    public void setSelectPart(String selectPart) {
        this.selectPart = selectPart;
    }

    public void setFromPart(String fromPart) {
        this.fromPart = new FromQueryPart(fromPart);
    }

    public void setWherePart(String wherePart) {
        this.wherePart = wherePart;
    }

    public void setGroupPart(String groupPart) {
        this.groupPart = groupPart;
    }

    public void setOrderPart(String orderPart) {
        this.orderPart = orderPart;
    }

    private void prepareSearchClause(List<String> searchFields, RequestSearch search) {
        if (!isBlank(searchClause)) {
            return;
        }
        if (isEmpty(searchFields) || search == null) {
            return;
        }
        String prefix = isBlank(fromPart.getMainTableAlias()) ? "" : fromPart.getMainTableAlias() + ".";
        String searchTemplate = search.isEquals() ? SEARCH_EQ_TPL : SEARCH_LIKE_TPL;
        searchClause = searchFields.stream()
                .map(field -> MessageFormat.format(searchTemplate, prefix + field, search.getValue()))
                .collect(Collectors.joining(" or "));
    }

    private String prepareFilterClause(List<RequestFilter> filters) {
        if (isEmpty(filters)) {
            return null;
        }

        return filters.stream().map(this::filterToClause).collect(Collectors.joining(" and "));
    }

    private String filterToClause(RequestFilter filter) {
        switch (filter.getOperation()) {
            case EQ:
                return getEqClause(filter);
            case LIKE:
                return getLikeClause(filter);
            default:
                throw new UnsupportedOperationException("Не поддерживаемый оператор фильтра " +
                        filter.getOperation().name());
        }
    }

    private String getEqClause(RequestFilter filter) {
        String prefix = isBlank(fromPart.getMainTableAlias()) ? "" : fromPart.getMainTableAlias() + ".";
        switch (filter.getFieldType()) {
            case STRING:
                return MessageFormat.format(FILTER_STRING_EQ_TPL, prefix + filter.getField(), getFilterName(filter.getField()));
            case INTEGER:
                return MessageFormat.format(FILTER_NUMBER_EQ_TPL, prefix + filter.getField(), getFilterName(filter.getField()));
            default:
                throw new UnsupportedOperationException("Не поддерживаемый тип поля для операции EQ " +
                        filter.getFieldType().name());
        }
    }

    private String getLikeClause(RequestFilter filter) {
        String prefix = isBlank(fromPart.getMainTableAlias()) ? "" : fromPart.getMainTableAlias() + ".";
        switch (filter.getFieldType()) {
            case STRING:
                return MessageFormat.format(FILTER_STRING_LIKE_TPL, prefix + filter.getField(), getFilterName(filter.getField()));
            default:
                throw new UnsupportedOperationException("Не поддерживаемый тип поля для операции LIKE " +
                        filter.getFieldType().name());
        }
    }

    private String getFilterName(String field) {
        return CaseUtils.toCamelCase(field, false, '.');
    }

    private Map<String, Object> combineParams(List<RequestFilter> filters, Map<String, Object> baseParams) {
        if (isEmpty(filters) && isEmpty(baseParams)) {
            return Collections.emptyMap();
        }
        Map<String, Object> params = new HashMap<>();
        if (baseParams != null) {
            params.putAll(baseParams);
        }
        if (filters != null) {
            params.putAll(filters.stream()
                    .collect(Collectors.toMap(filter -> getFilterName(filter.getField()), filter -> prepareFilterValue(filter))));
        }

        return params;
    }

    private Object prepareFilterValue(RequestFilter filter) {
        switch (filter.getFieldType()) {
            case STRING:
                return filter.getValue();
            case INTEGER:
                return Long.parseLong(filter.getValue());
            default:
                throw new UnsupportedOperationException("Не поддерживаемый тип поля " + filter.getFieldType().name());
        }
    }

    @Override
    public ParamsQuery build(RequestCriteria requestCriteria) {
        return build(requestCriteria, null);
    }

    @Override
    public ParamsQuery build(RequestCriteria requestCriteria, Map<String, Object> params) {
        String baseQuery = getBaseQuery(requestCriteria);
        return new ParamsQuery(selectPart.trim() + " " + baseQuery +
                (isBlank(orderPart) ? "" : " " + orderPart.trim()),
                "select count(1) " + baseQuery,
                combineParams(requestCriteria.getFilters(), params));
    }

    private String getBaseQuery(RequestCriteria requestCriteria) {
        prepareSearchClause(searchFields, requestCriteria.getSearch());
        String filterClause = prepareFilterClause(requestCriteria.getFilters());

        StringBuilder sb = new StringBuilder()
                .append(fromPart.getValue())
                .append(isBlank(wherePart) ? "" : " " + wherePart.trim());
        if (searchClause != null || filterClause != null) {
            if (isBlank(wherePart)) {
                sb.append(" where ");
            } else {
                sb.append(" and ");
            }

            if (searchClause != null) {
                sb.append("(" + searchClause + ")");
            }
            if (filterClause != null) {
                sb.append((searchClause != null ? " and " : "") + "(" + filterClause + ")");
            }
        }
        sb.append(isBlank(groupPart) ? "" : " " + groupPart.trim());
        return sb.toString();
    }
}
