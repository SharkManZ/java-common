package ru.shark.home.common.dao.repository.query;

import java.util.Map;

public class ParamsQuery {
    private String queryString;
    private String countQueryString;
    boolean isCountNative;
    private Map<String, Object> params;

    public ParamsQuery(String queryString, String countQueryString, boolean isCountNative, Map<String, Object> params) {
        this.queryString = queryString;
        this.countQueryString = countQueryString;
        this.isCountNative = isCountNative;
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

    public boolean isCountNative() {
        return isCountNative;
    }
}
