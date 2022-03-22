package ru.shark.home.common.dao.service;

import org.hibernate.Session;
import org.springframework.stereotype.Component;
import ru.shark.home.common.dao.repository.query.HqlCriteriaQueryBuilder;
import ru.shark.home.common.dao.repository.query.QueryPartType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class HqlQueryService implements QueryService {

    private EntityManager entityManager;

    @Override
    public HqlCriteriaQueryBuilder prepareNamedQuery(String name) {
        return prepareQuery(((Session) entityManager).createNamedQuery(name).getQueryString(), null);
    }

    @Override
    public HqlCriteriaQueryBuilder prepareNamedQuery(String name, List<String> searchFields) {
        return prepareQuery(((Session) entityManager).createNamedQuery(name).getQueryString(), searchFields);
    }

    public HqlCriteriaQueryBuilder prepareQuery(String query) {
        return prepareQuery(query, null);
    }


    @Override
    public HqlCriteriaQueryBuilder prepareQuery(String query, List<String> searchFields) {
        String[] queryParts = query.replaceAll("\\n", "").split(" ");
        HqlCriteriaQueryBuilder hqlCriteriaQuery = new HqlCriteriaQueryBuilder(searchFields);
        int idx = 0;
        int lastCopyIdx = 0;
        QueryPartType currentPartType = QueryPartType.SELECT;
        int bracketLevel = 0;
        while (idx < queryParts.length) {
            String part = queryParts[idx];
            if ("from".equalsIgnoreCase(part) &&
                    QueryPartType.SELECT.equals(currentPartType) && bracketLevel == 0) {
                hqlCriteriaQuery.setSelectPart(String.join(" ", Arrays.copyOfRange(queryParts, 0, idx)));
                lastCopyIdx = idx;
                currentPartType = QueryPartType.FROM;
            } else if ("where".equalsIgnoreCase(part) &&
                    QueryPartType.FROM.equals(currentPartType) && bracketLevel == 0) {
                hqlCriteriaQuery.setFromPart(String.join(" ", Arrays.copyOfRange(queryParts, lastCopyIdx, idx)));
                lastCopyIdx = idx;
                currentPartType = QueryPartType.WHERE;
            } else if ("group".equalsIgnoreCase(part) &&
                    (QueryPartType.WHERE.equals(currentPartType) || QueryPartType.FROM.equals(currentPartType)) &&
                    bracketLevel == 0 && idx + 1 < queryParts.length &&
                    findQueryPartFromIndex(queryParts, idx + 1, "by") != -1) {
                if (QueryPartType.FROM.equals(currentPartType)) {
                    hqlCriteriaQuery.setFromPart(String.join(" ", Arrays.copyOfRange(queryParts, lastCopyIdx, idx)));
                } else if (QueryPartType.WHERE.equals(currentPartType)) {
                    hqlCriteriaQuery.setWherePart(String.join(" ", Arrays.copyOfRange(queryParts, lastCopyIdx, idx)));
                }
                lastCopyIdx = idx;
                idx = findQueryPartFromIndex(queryParts, idx + 1, "by");
                currentPartType = QueryPartType.GROUP;
            } else if ("order".equalsIgnoreCase(part) &&
                    (QueryPartType.WHERE.equals(currentPartType) || QueryPartType.FROM.equals(currentPartType) ||
                            QueryPartType.GROUP.equals(currentPartType)) &&
                    bracketLevel == 0 && idx + 1 < queryParts.length &&
                    findQueryPartFromIndex(queryParts, idx + 1, "by") != -1) {
                if (QueryPartType.FROM.equals(currentPartType)) {
                    hqlCriteriaQuery.setFromPart(String.join(" ", Arrays.copyOfRange(queryParts, lastCopyIdx, idx)));
                } else if (QueryPartType.WHERE.equals(currentPartType)) {
                    hqlCriteriaQuery.setWherePart(String.join(" ", Arrays.copyOfRange(queryParts, lastCopyIdx, idx)));
                } else if (QueryPartType.GROUP.equals(currentPartType)) {
                    hqlCriteriaQuery.setGroupPart(String.join(" ", Arrays.copyOfRange(queryParts, lastCopyIdx, idx)));
                }
                lastCopyIdx = idx;
                idx = findQueryPartFromIndex(queryParts, idx + 1, "by");
                currentPartType = QueryPartType.ORDER;
            } else if (part.contains("(") || part.contains(")")) {
                for (char let : part.toCharArray()) {
                    if ('(' == let) {
                        bracketLevel++;
                    } else if (')' == let) {
                        bracketLevel--;
                    }
                }
            }
            idx++;
        }

        String lastPart = String.join(" ", Arrays.copyOfRange(queryParts, lastCopyIdx, idx));
        switch (currentPartType) {
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

    private int findQueryPartFromIndex(String[] parts, int startIdx, String findPart) {
        boolean onlySpaces = true;
        for (int i = startIdx; i < parts.length; i++) {
            if (findPart.equalsIgnoreCase(parts[i])) {
                return onlySpaces ? i : -1;
            } else if (!isBlank(parts[i])) {
                onlySpaces = false;
            }
        }
        return -1;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}