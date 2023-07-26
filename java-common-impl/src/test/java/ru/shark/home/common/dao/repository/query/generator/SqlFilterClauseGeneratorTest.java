package ru.shark.home.common.dao.repository.query.generator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.shark.home.common.enums.FieldType;
import ru.shark.home.common.enums.FilterOperation;

import java.util.List;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SqlFilterClauseGeneratorTest {

    private SqlFilterClauseGenerator generator;
    private Map<FilterOperation, List<FieldType>> operationAvailableTypes;

    @BeforeAll
    public void init() {
        generator = new SqlFilterClauseGenerator();
        operationAvailableTypes = Map.of(
                FilterOperation.LIKE, List.of(FieldType.STRING),
                FilterOperation.EQ, List.of(FieldType.STRING, FieldType.INTEGER, FieldType.BOOL, FieldType.DATE, FieldType.ENUM),
                FilterOperation.NE, List.of(FieldType.STRING, FieldType.INTEGER, FieldType.BOOL, FieldType.DATE, FieldType.ENUM),
                FilterOperation.IN, List.of(FieldType.STRING, FieldType.INTEGER, FieldType.BOOL, FieldType.DATE, FieldType.ENUM),
                FilterOperation.BETWEEN, List.of(FieldType.INTEGER, FieldType.DATE),
                FilterOperation.LT, List.of(FieldType.INTEGER, FieldType.DATE),
                FilterOperation.GT, List.of(FieldType.INTEGER, FieldType.DATE)
        );
    }

    @Test
    public void canHandle() {
        // WHEN
        boolean yes = generator.canHandle(QueryClauseType.FILTER, true);
        boolean noType = generator.canHandle(QueryClauseType.SEARCH, true);
        boolean noNative = generator.canHandle(QueryClauseType.FILTER, false);

        // THEN
        Assertions.assertTrue(yes);
        Assertions.assertFalse(noType);
        Assertions.assertFalse(noNative);
    }
}
