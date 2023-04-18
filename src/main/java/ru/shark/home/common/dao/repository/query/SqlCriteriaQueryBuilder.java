package ru.shark.home.common.dao.repository.query;

import org.apache.commons.text.CaseUtils;
import ru.shark.home.common.dao.common.RequestCriteria;
import ru.shark.home.common.dao.common.RequestFilter;
import ru.shark.home.common.dao.common.RequestSearch;
import ru.shark.home.common.dao.common.RequestSort;

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
public class SqlCriteriaQueryBuilder extends BaseCriteriaQueryBuilder implements CriteriaQueryBuilder {

    private String selectPart;
    private String fromPart;
    private String wherePart;
    private String groupPart;
    private String orderPart;
    private String searchClause;
    private List<String> searchFields;
    private List<String> advancedSearchFields;

    public SqlCriteriaQueryBuilder() {
        this.searchFields = null;
        this.advancedSearchFields = null;
    }

    public SqlCriteriaQueryBuilder(List<String> searchFields) {
        this.searchFields = searchFields;
        this.advancedSearchFields = null;
    }

    public SqlCriteriaQueryBuilder(List<String> searchFields, List<String> advancedSearchFields) {
        this.searchFields = searchFields;
        this.advancedSearchFields = advancedSearchFields;
    }

    public void setSelectPart(String selectPart) {
        this.selectPart = selectPart;
    }

    public void setFromPart(String fromPart) {
        this.fromPart = fromPart;
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

    private void prepareSearchClause(RequestSearch search) {
        if (!isBlank(searchClause)) {
            return;
        }
        if ((isEmpty(advancedSearchFields) && isEmpty(searchFields)) || search == null) {
            return;
        }
        if (!isEmpty(searchFields)) {
            String searchTemplate = search.isEquals() ? SIMPLE_SEARCH_LEFT + SEARCH_EQ_TPL : SIMPLE_SEARCH_LEFT + SEARCH_LIKE_TPL;
            searchClause = searchFields.stream()
                    .map(field -> MessageFormat.format(searchTemplate, field, search.getValue()))
                    .collect(Collectors.joining(" or "));
        }
        if (!isEmpty(advancedSearchFields)) {
            String searchTemplate = (search.isEquals() ? SEARCH_EQ_TPL : SEARCH_LIKE_TPL).replace("1", "0");
            String advancedSearchClause = advancedSearchFields.stream()
                    .map(item -> MessageFormat.format(item, MessageFormat.format(searchTemplate, search.getValue())))
                    .collect(Collectors.joining(" or "));
            if (isBlank(searchClause)) {
                searchClause = advancedSearchClause;
            } else {
                searchClause += " or " + advancedSearchClause;
            }
        }
    }

    private String prepareFilterClause(List<RequestFilter> filters) {
        if (isEmpty(filters)) {
            return null;
        }

        return filters.stream().map(this::filterToClause).collect(Collectors.joining(" and "));
    }

    private String prepareOrderClause(List<RequestSort> sorts) {
        if (isEmpty(sorts)) {
            return null;
        }

        return sorts.stream().map(this::sortToClause).collect(Collectors.joining(", "));
    }

    private String sortToClause(RequestSort sort) {
        return sort.getField() + (sort.getDirection() == null ? " asc" : " " + sort.getDirection().name().toLowerCase());
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
        switch (filter.getFieldType()) {
            case STRING:
                return MessageFormat.format(FILTER_STRING_EQ_TPL, filter.getField(), getFilterName(filter.getField()));
            case INTEGER:
                return MessageFormat.format(FILTER_NUMBER_EQ_TPL, filter.getField(), getFilterName(filter.getField()));
            default:
                throw new UnsupportedOperationException("Не поддерживаемый тип поля для операции EQ " +
                        filter.getFieldType().name());
        }
    }

    private String getLikeClause(RequestFilter filter) {
        switch (filter.getFieldType()) {
            case STRING:
                return MessageFormat.format(FILTER_STRING_LIKE_TPL, filter.getField(), getFilterName(filter.getField()));
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
        String order = prepareOrderClause(requestCriteria.getSorts());
        if (isBlank(order)) {
            order = isBlank(orderPart) ? "" : " " + orderPart.trim();
        } else {
            order = " order by " + order;
        }

        return new ParamsQuery(selectPart.trim() + " " + baseQuery + order,
                isBlank(groupPart) ? ("select count(1) " + baseQuery) :
                        "select count(1) from (" + selectPart.trim() + " " + baseQuery + ") q", true,
                combineParams(requestCriteria.getFilters(), params));
    }

    private String getBaseQuery(RequestCriteria requestCriteria) {
        prepareSearchClause(requestCriteria.getSearch());
        String filterClause = prepareFilterClause(requestCriteria.getFilters());

        StringBuilder sb = new StringBuilder()
                .append(fromPart)
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
