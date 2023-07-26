package ru.shark.home.common.dao.repository.query.generator;

import ru.shark.home.common.dao.common.RequestCriteria;
import ru.shark.home.common.dao.repository.query.ParsedQuery;

public class QueryClauseRequest {

    private RequestCriteria requestCriteria;
    private ParsedQuery parsedQuery;

    public QueryClauseRequest(RequestCriteria requestCriteria, ParsedQuery parsedQuery) {
        this.requestCriteria = requestCriteria;
        this.parsedQuery = parsedQuery;
    }

    public RequestCriteria getRequestCriteria() {
        return requestCriteria;
    }

    public ParsedQuery getParsedQuery() {
        return parsedQuery;
    }
}
