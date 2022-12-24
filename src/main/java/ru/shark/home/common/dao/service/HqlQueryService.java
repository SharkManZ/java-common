package ru.shark.home.common.dao.service;

import org.hibernate.Session;
import org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory;
import org.springframework.stereotype.Component;
import ru.shark.home.common.dao.repository.query.CriteriaQueryBuilder;
import ru.shark.home.common.dao.repository.query.HqlCriteriaQueryBuilder;
import ru.shark.home.common.dao.repository.query.QueryParsingState;
import ru.shark.home.common.dao.repository.query.QueryPartType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Component
public class HqlQueryService implements QueryService {

    private EntityManager entityManager;
    private ASTQueryTranslatorFactory queryTranslatorFactory;

    @Override
    public HqlCriteriaQueryBuilder prepareNamedQuery(String name) {
        return prepareNamedQuery(name, null);
    }

    @Override
    public HqlCriteriaQueryBuilder prepareNamedQuery(String name, List<String> searchFields) {
        return prepareNamedQuery(name, searchFields, null);
    }

    @Override
    public HqlCriteriaQueryBuilder prepareNamedQuery(String name, List<String> searchFields, List<String> advancedSearchFields) {
        return prepareQuery(((Session) entityManager).getNamedQuery(name).getQueryString(), searchFields);
    }

    public HqlCriteriaQueryBuilder prepareQuery(String query) {
        return prepareQuery(query, null);
    }


    @Override
    public HqlCriteriaQueryBuilder prepareQuery(String query, List<String> searchFields) {
        return prepareQuery(query, searchFields, null);
    }

    @Override
    public HqlCriteriaQueryBuilder prepareQuery(String query, List<String> searchFields, List<String> advancedSearchFields) {
        HqlCriteriaQueryBuilder hqlCriteriaQuery = new HqlCriteriaQueryBuilder(entityManager, searchFields, advancedSearchFields);
        if (queryTranslatorFactory != null) {
            hqlCriteriaQuery.setQueryTranslatorFactory(queryTranslatorFactory);
        }
        QueryParsingState state = new QueryParsingState(query);
        while (state.hasNext()) {
            if (state.isPartStarted(QueryPartType.FROM)) {
                hqlCriteriaQuery.setSelectPart(state.changeCurrentPart(QueryPartType.FROM));

            } else if (state.isPartStarted(QueryPartType.WHERE)) {
                hqlCriteriaQuery.setFromPart(state.changeCurrentPart(QueryPartType.WHERE));

            } else if (state.isPartStarted(QueryPartType.GROUP) && state.hasMore() &&
                    state.findNextPartIdx("by") != -1) {
                QueryPartType lastType = state.getCurrentPartType();
                String previousPart = state.changeCurrentPart(QueryPartType.GROUP, "by");
                if (QueryPartType.FROM.equals(lastType)) {
                    hqlCriteriaQuery.setFromPart(previousPart);
                } else {
                    hqlCriteriaQuery.setWherePart(previousPart);
                }

            } else if (state.isPartStarted(QueryPartType.ORDER) && state.hasMore() &&
                    state.findNextPartIdx("by") != -1) {
                QueryPartType lastType = state.getCurrentPartType();
                String previousPart = state.changeCurrentPart(QueryPartType.ORDER, "by");
                if (QueryPartType.FROM.equals(lastType)) {
                    hqlCriteriaQuery.setFromPart(previousPart);
                } else if (QueryPartType.WHERE.equals(lastType)) {
                    hqlCriteriaQuery.setWherePart(previousPart);
                } else {
                    hqlCriteriaQuery.setGroupPart(previousPart);
                }

            } else {
                state.processBracket();
            }
            state.nextIdx();
        }

        String lastPart = state.getPreviousPart();
        switch (state.getCurrentPartType()) {
            case FROM:
                hqlCriteriaQuery.setFromPart(lastPart);
                break;
            case WHERE:
                hqlCriteriaQuery.setWherePart(lastPart);
                break;
            case GROUP:
                hqlCriteriaQuery.setGroupPart(lastPart);
                break;
            case ORDER:
                hqlCriteriaQuery.setOrderPart(lastPart);
                break;
        }

        return hqlCriteriaQuery;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    protected void setQueryTranslatorFactory(ASTQueryTranslatorFactory queryTranslatorFactory) {
        this.queryTranslatorFactory = queryTranslatorFactory;
    }
}