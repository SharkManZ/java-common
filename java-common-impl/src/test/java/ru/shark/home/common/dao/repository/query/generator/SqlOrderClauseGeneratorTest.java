package ru.shark.home.common.dao.repository.query.generator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.shark.home.common.dao.common.RequestCriteria;
import ru.shark.home.common.dao.common.RequestSort;
import ru.shark.home.common.dao.repository.query.ParsedQuery;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SqlOrderClauseGeneratorTest {

    private SqlOrderClauseGenerator generator;

    @BeforeAll
    public void init() {
        generator = new SqlOrderClauseGenerator();
    }

    @Test
    public void generate() {
        // GIVEN
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setSorts(List.of(new RequestSort("field1", "asc"), new RequestSort("field2", "desc")));
        QueryClauseRequest request = new QueryClauseRequest(requestCriteria, new ParsedQuery(null, null));
        String expected = "field1 asc, field2 desc";

        // WHEN
        String clause = generator.generate(request);

        // THEN
        Assertions.assertEquals(expected, clause);
    }

    @Test
    public void generateWithoutSort() {
        // GIVEN
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        QueryClauseRequest request = new QueryClauseRequest(requestCriteria, new ParsedQuery(null, null));

        // WHEN
        String clause = generator.generate(request);

        // THEN
        Assertions.assertEquals("", clause);
    }

    @Test
    public void canHandle() {
        // WHEN
        boolean yes = generator.canHandle(QueryClauseType.ORDER, true);
        boolean noType = generator.canHandle(QueryClauseType.SEARCH, true);
        boolean noNative = generator.canHandle(QueryClauseType.ORDER, false);

        // THEN
        Assertions.assertTrue(yes);
        Assertions.assertFalse(noType);
        Assertions.assertFalse(noNative);
    }
}
