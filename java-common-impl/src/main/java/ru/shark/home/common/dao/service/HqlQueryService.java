package ru.shark.home.common.dao.service;

import ru.shark.home.common.dao.repository.query.ParamsQuery;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory;
import org.hibernate.hql.spi.QueryTranslator;
import org.springframework.stereotype.Component;
import ru.shark.home.common.dao.common.RequestCriteria;
import ru.shark.home.common.dao.repository.query.ParsedQuery;
import ru.shark.home.common.dao.repository.query.QueryPartType;
import ru.shark.home.common.dao.repository.query.generator.QueryClauseRequest;
import ru.shark.home.common.dao.repository.query.generator.QueryClauseType;
import ru.shark.home.common.dao.repository.query.parts.HqlFromQueryPart;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class HqlQueryService extends BaseQueryService implements QueryService {

    private EntityManager entityManager;
    private ASTQueryTranslatorFactory queryTranslatorFactory;
    private final Map<QueryPartType, BiConsumer<ParsedQuery, String>> partTypeConsumers = Map.of(
            QueryPartType.SELECT, ParsedQuery::setSelectPart,
            QueryPartType.FROM, (parsed, part) -> parsed.setFromPart(new HqlFromQueryPart(part)),
            QueryPartType.WHERE, ParsedQuery::setWherePart,
            QueryPartType.GROUP, ParsedQuery::setGroupPart,
            QueryPartType.ORDER, ParsedQuery::setOrderPart
    );


    public HqlQueryService() {
        queryTranslatorFactory = new ASTQueryTranslatorFactory();
    }

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
        String baseQuery = getBaseQuery(query, request, false);
        String order = queryClauseGenerator.generate(QueryClauseType.ORDER, false, request);
        if (isBlank(order)) {
            order = isBlank(query.getOrderPart()) ? "" : " " + query.getOrderPart().trim();
        } else {
            order = " order by " + order;
        }
        boolean isCountNative = !isBlank(query.getGroupPart());
        return new ParamsQuery(query.getSelectPart().trim() + " " + baseQuery + order,
                getCountString(isCountNative, query.getSelectPart(), baseQuery), isCountNative,
                combineParams(requestCriteria.getFilters(), params));
    }

    private String getCountString(boolean isCountNative, String selectPart, String baseQuery) {
        if (isCountNative) {
            SessionImplementor hibernateSession = entityManager.unwrap(SessionImplementor.class);
            QueryTranslator queryTranslator = queryTranslatorFactory.createQueryTranslator("",
                    selectPart.trim() + " " + baseQuery, Collections.EMPTY_MAP, hibernateSession.getFactory(),
                    null);
            queryTranslator.compile(Collections.EMPTY_MAP, false);
            return "select count(1) from (" + queryTranslator.getSQLString() + ") q";
        }
        return "select count(1) " + baseQuery;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    protected void setQueryTranslatorFactory(ASTQueryTranslatorFactory queryTranslatorFactory) {
        this.queryTranslatorFactory = queryTranslatorFactory;
    }
}