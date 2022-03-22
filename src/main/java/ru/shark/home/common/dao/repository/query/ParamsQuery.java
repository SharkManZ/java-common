package ru.shark.home.common.dao.repository.query;

import java.util.Map;

public class ParamsQuery {
    private String queryString;
    private String countQueryString;
    private Map<String, Object> params;

    public ParamsQuery(String queryString, String countQueryString, Map<String, Object> params) {
        this.queryString = queryString;
        this.countQueryString = countQueryString;
        this.params = params;
    }

    public String getQueryString() {
        return queryString;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public String getCountQueryString() {
        return countQueryString;
    }
}
