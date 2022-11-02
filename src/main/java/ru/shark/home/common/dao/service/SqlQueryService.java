package ru.shark.home.common.dao.service;

import org.hibernate.Session;
import org.springframework.stereotype.Component;
import ru.shark.home.common.dao.repository.query.QueryParsingState;
import ru.shark.home.common.dao.repository.query.QueryPartType;
import ru.shark.home.common.dao.repository.query.SqlCriteriaQueryBuilder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Component
public class SqlQueryService implements QueryService {

    private EntityManager entityManager;

    @Override
    public SqlCriteriaQueryBuilder prepareNamedQuery(String name) {
        return prepareNamedQuery(name, null);
    }

    @Override
    public SqlCriteriaQueryBuilder prepareNamedQuery(String name, List<String> searchFields) {
        return prepareQuery(((Session) entityManager).getNamedNativeQuery(name).getQueryString(), searchFields);
    }

    @Override
    public SqlCriteriaQueryBuilder prepareQuery(String query) {
        return prepareQuery(query, null);
    }

    @Override
    public SqlCriteriaQueryBuilder prepareQuery(String query, List<String> searchFields) {
        SqlCriteriaQueryBuilder sqlCriteriaQueryBuilder = new SqlCriteriaQueryBuilder(entityManager, searchFields);
        QueryParsingState state = new QueryParsingState(query);
        while (state.hasNext()) {
            if (state.isPartStarted(QueryPartType.FROM)) {
                sqlCriteriaQueryBuilder.setSelectPart(state.changeCurrentPart(QueryPartType.FROM));

            } else if (state.isPartStarted(QueryPartType.WHERE)) {
                sqlCriteriaQueryBuilder.setFromPart(state.changeCurrentPart(QueryPartType.WHERE));

            } else if (state.isPartStarted(QueryPartType.GROUP) && state.hasMore() &&
                    state.findNextPartIdx("by") != -1) {
                QueryPartType lastType = state.getCurrentPartType();
                String previousPart = state.changeCurrentPart(QueryPartType.GROUP, "by");
                if (QueryPartType.FROM.equals(lastType)) {
                    sqlCriteriaQueryBuilder.setFromPart(previousPart);
                } else {
                    sqlCriteriaQueryBuilder.setWherePart(previousPart);
                }

            } else if (state.isPartStarted(QueryPartType.ORDER) && state.hasMore() &&
                    state.findNextPartIdx("by") != -1) {
                QueryPartType lastType = state.getCurrentPartType();
                String previousPart = state.changeCurrentPart(QueryPartType.ORDER, "by");
                if (QueryPartType.FROM.equals(lastType)) {
                    sqlCriteriaQueryBuilder.setFromPart(previousPart);
                } else if (QueryPartType.WHERE.equals(lastType)) {
                    sqlCriteriaQueryBuilder.setWherePart(previousPart);
                } else {
                    sqlCriteriaQueryBuilder.setGroupPart(previousPart);
                }

            } else {
                state.processBracket();
            }
            state.nextIdx();
        }

        String lastPart = state.getPreviousPart();
        switch (state.getCurrentPartType()) {
            case FROM:
                sqlCriteriaQueryBuilder.setFromPart(lastPart);
                break;
            case WHERE:
                sqlCriteriaQueryBuilder.setWherePart(lastPart);
                break;
            case GROUP:
                sqlCriteriaQueryBuilder.setGroupPart(lastPart);
                break;
            case ORDER:
                sqlCriteriaQueryBuilder.setOrderPart(lastPart);
                break;
        }

        return sqlCriteriaQueryBuilder;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
