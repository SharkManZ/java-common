package ru.shark.home.common.dao.repository.query;

public class BaseCriteriaQueryBuilder {
    protected static final String SIMPLE_SEARCH_LEFT = "lower({0}) ";
    protected static final String SEARCH_EQ_TPL = "= lower(''{1}'')";
    protected static final String SEARCH_LIKE_TPL = "like ''%'' || lower(''{1}'') || ''%''";
    protected static final String FILTER_STRING_EQ_TPL = "lower({0}) = lower(:{1})";
    protected static final String FILTER_NUMBER_EQ_TPL = "{0} = :{1}";
    protected static final String FILTER_STRING_LIKE_TPL = "lower({0}) like ''%'' || lower(:{1}) || ''%''";
}
