package ru.shark.home.common.dao.service;

import org.hibernate.Session;
import org.springframework.stereotype.Component;
import ru.shark.home.common.dao.repository.query.QueryParser;
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
        return prepareNamedQuery(name, searchFields, null);
    }

    @Override
    public SqlCriteriaQueryBuilder prepareNamedQuery(String name, List<String> searchFields, List<String> advancedSearchFields) {
        return prepareQuery(((Session) entityManager).getNamedNativeQuery(name).getQueryString(), searchFields, advancedSearchFields);
    }

    @Override
    public SqlCriteriaQueryBuilder prepareQuery(String query) {
        return prepareQuery(query, null);
    }

    @Override
    public SqlCriteriaQueryBuilder prepareQuery(String query, List<String> searchFields) {
        return prepareQuery(query, searchFields, null);
    }

    @Override
    public SqlCriteriaQueryBuilder prepareQuery(String query, List<String> searchFields, List<String> advancedSearchFields) {
        SqlCriteriaQueryBuilder sqlCriteriaQueryBuilder = new SqlCriteriaQueryBuilder(searchFields, advancedSearchFields);
        QueryParser state = new QueryParser(query);
        while (state.hasNext()) {
            state.nextPart();
            if (state.isPartStarted(QueryPartType.FROM)) {
                sqlCriteriaQueryBuilder.setSelectPart(state.changeCurrentPart(QueryPartType.FROM));

            } else if (state.isPartStarted(QueryPartType.WHERE)) {
                sqlCriteriaQueryBuilder.setFromPart(state.changeCurrentPart(QueryPartType.WHERE));

            } else if (state.isPartStarted(QueryPartType.GROUP)) {
                QueryPartType lastType = state.getCurrentPartType();
                String previousPart = state.changeCurrentPart(QueryPartType.GROUP);
                if (QueryPartType.FROM.equals(lastType)) {
                    sqlCriteriaQueryBuilder.setFromPart(previousPart);
                } else {
                    sqlCriteriaQueryBuilder.setWherePart(previousPart);
                }
            } else if (state.isPartStarted(QueryPartType.ORDER)) {
                QueryPartType lastType = state.getCurrentPartType();
                String previousPart = state.changeCurrentPart(QueryPartType.ORDER);
                if (QueryPartType.FROM.equals(lastType)) {
                    sqlCriteriaQueryBuilder.setFromPart(previousPart);
                } else if (QueryPartType.WHERE.equals(lastType)) {
                    sqlCriteriaQueryBuilder.setWherePart(previousPart);
                } else {
                    sqlCriteriaQueryBuilder.setGroupPart(previousPart);
                }
            }
        }

        String lastPart = state.getLastPart();
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
