package ru.shark.home.common.dao.repository.query.generator;

public class GeneratorConstants {
    public static final String SIMPLE_SEARCH_LEFT = "lower({0}) ";
    public static final String SEARCH_EQ_TPL = "= lower(''{1}'')";
    public static final String SEARCH_LIKE_TPL = "like ''%'' || lower(''{1}'') || ''%''";

    public static final String FILTER_STRING_EQ_TPL = "lower({0}) = lower(:filter_{1})";
    public static final String FILTER_NUMBER_EQ_TPL = "{0} = :filter_{1}";
    public static final String FILTER_STRING_NE_TPL = "lower({0}) <> lower(:filter_{0})";
    public static final String FILTER_NUMBER_NE_TPL = "{0} <> :filter_{0}";
    public static final String FILTER_IN_TPL = "{0} in (:filter_{0})";
    public static final String FILTER_STRING_LIKE_TPL = "lower({0}) like ''%'' || lower(:filter_{1}) || ''%''";
    public static final String FILTER_BETWEEN_TPL = "{0} between :filter_{0}_left and :filter_{0}_right";
    public static final String FILTER_LT_TPL = "{0} < :filter_{0}";
    public static final String FILTER_GT_TPL = "{0} > :filter_{0}";
}
