package ru.shark.home.common.dao.repository.query.generator;

public interface ClauseGenerator {

    String generate(QueryClauseRequest request);

    boolean canHandle(QueryClauseType type, boolean isNative);
}
