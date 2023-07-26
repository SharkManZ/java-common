package ru.shark.home.common.dao.service;

import ru.shark.home.common.dao.repository.query.ParamsQuery;
import org.springframework.stereotype.Component;
import ru.shark.home.common.dao.common.RequestCriteria;
import ru.shark.home.common.dao.repository.query.ParsedQuery;
import ru.shark.home.common.dao.repository.query.QueryPartType;
import ru.shark.home.common.dao.repository.query.generator.QueryClauseRequest;
import ru.shark.home.common.dao.repository.query.generator.QueryClauseType;
import ru.shark.home.common.dao.repository.query.parts.SqlFromQueryPart;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class SqlQueryService extends BaseQueryService implements QueryService {


    private final Map<QueryPartType, BiConsumer<ParsedQuery, String>> partTypeConsumers = Map.of(
            QueryPartType.SELECT, ParsedQuery::setSelectPart,
            QueryPartType.FROM, (parsed, part) -> parsed.setFromPart(new SqlFromQueryPart(part)),
            QueryPartType.WHERE, ParsedQuery::setWherePart,
            QueryPartType.GROUP, ParsedQuery::setGroupPart,
            QueryPartType.ORDER, ParsedQuery::setOrderPart
    );

    @Override
    public ParsedQuery parseQuery(String query) {
        return parseQuery(query, null, null);
    }

    @Override
    public ParsedQuery parseQuery(String query, List<String> searchFields) {
        return parseQuery(query, searchFields, null);
    }

    @Override
    public ParsedQuery parseQuery(String query, List<String> searchFields, List<String> advancedSearchFields) {
        return parseQuery(query, searchFields, advancedSearchFields, partTypeConsumers);
    }

    @Override
    public ParamsQuery generateParamsQuery(ParsedQuery query, RequestCriteria requestCriteria) {
        return generateParamsQuery(query, requestCriteria, null);
    }

    @Override
    public ParamsQuery generateParamsQuery(ParsedQuery query, RequestCriteria requestCriteria, Map<String, Object> params) {
        QueryClauseRequest request = new QueryClauseRequest(requestCriteria, query);
        String baseQuery = getBaseQuery(query, request, true);
        String order = queryClauseGenerator.generate(QueryClauseType.ORDER, true, request);

        if (isBlank(order)) {
            order = isBlank(query.getOrderPart()) ? "" : " " + query.getOrderPart().trim();
        } else {
            order = " order by " + order;
        }

        return new ParamsQuery(query.getSelectPart().trim() + " " + baseQuery + order,
                isBlank(query.getGroupPart()) ? ("select count(1) " + baseQuery) :
                        "select count(1) from (" + query.getSelectPart().trim() + " " + baseQuery + ") q", true,
                combineParams(requestCriteria.getFilters(), params));
    }
}
