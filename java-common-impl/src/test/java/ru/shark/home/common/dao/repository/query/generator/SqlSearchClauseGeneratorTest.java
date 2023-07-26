package ru.shark.home.common.dao.repository.query.generator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.shark.home.common.dao.common.RequestCriteria;
import ru.shark.home.common.dao.common.RequestSearch;
import ru.shark.home.common.dao.repository.query.ParsedQuery;

import java.text.MessageFormat;
import java.util.List;

import static ru.shark.home.common.dao.repository.query.generator.GeneratorConstants.SEARCH_EQ_TPL;
import static ru.shark.home.common.dao.repository.query.generator.GeneratorConstants.SEARCH_LIKE_TPL;
import static ru.shark.home.common.dao.repository.query.generator.GeneratorConstants.SIMPLE_SEARCH_LEFT;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SqlSearchClauseGeneratorTest {

    private SqlSearchClauseGenerator generator;

    @BeforeAll
    public void init() {
        generator = new SqlSearchClauseGenerator();
    }

    @Test
    public void generateWithEqualsAndSearch() {
        // GIVEN
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setSearch(new RequestSearch("val", true));
        QueryClauseRequest request = new QueryClauseRequest(requestCriteria, new ParsedQuery(List.of("field1", "field2"), null));
        String expected = "(" + MessageFormat.format(SIMPLE_SEARCH_LEFT + SEARCH_EQ_TPL, "field1", requestCriteria.getSearch().getValue()) + " or " +
                MessageFormat.format(SIMPLE_SEARCH_LEFT + SEARCH_EQ_TPL, "field2", requestCriteria.getSearch().getValue()) + ")";

        // WHEN
        String clause = generator.generate(request);

        // THEN
        Assertions.assertEquals(expected, clause);
    }

    @Test
    public void generateWithNotEqualsAndSearch() {
        // GIVEN
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setSearch(new RequestSearch("val", false));
        QueryClauseRequest request = new QueryClauseRequest(requestCriteria, new ParsedQuery(List.of("field1", "field2"), null));
        String expected = "(" + MessageFormat.format(SIMPLE_SEARCH_LEFT + SEARCH_LIKE_TPL, "field1", requestCriteria.getSearch().getValue()) + " or " +
                MessageFormat.format(SIMPLE_SEARCH_LEFT + SEARCH_LIKE_TPL, "field2", requestCriteria.getSearch().getValue()) + ")";

        // WHEN
        String clause = generator.generate(request);

        // THEN
        Assertions.assertEquals(expected, clause);
    }

    @Test
    public void generateWithEqualsAndAdvancedSearch() {
        // GIVEN
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setSearch(new RequestSearch("val", true));
        QueryClauseRequest request = new QueryClauseRequest(requestCriteria, new ParsedQuery(null, List.of("some clause1 {0}", "some clause2 {0}")));
        String expected = "(some clause1 " + MessageFormat.format(SEARCH_EQ_TPL.replace("1", "0"), requestCriteria.getSearch().getValue()) +
                " or some clause2 " +
                MessageFormat.format(SEARCH_EQ_TPL.replace("1", "0"), requestCriteria.getSearch().getValue()) + ")";

        // WHEN
        String clause = generator.generate(request);

        // THEN
        Assertions.assertEquals(expected, clause);
    }

    @Test
    public void generateWithNotEqualsAndAdvancedSearch() {
        // GIVEN
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setSearch(new RequestSearch("val", false));
        QueryClauseRequest request = new QueryClauseRequest(requestCriteria, new ParsedQuery(null, List.of("some clause1 {0}", "some clause2 {0}")));
        String expected = "(some clause1 " + MessageFormat.format(SEARCH_LIKE_TPL.replace("1", "0"), requestCriteria.getSearch().getValue()) +
                " or some clause2 " +
                MessageFormat.format(SEARCH_LIKE_TPL.replace("1", "0"), requestCriteria.getSearch().getValue()) + ")";

        // WHEN
        String clause = generator.generate(request);

        // THEN
        Assertions.assertEquals(expected, clause);
    }

    @Test
    public void generateWithNotEqualsAndBoth() {
        // GIVEN
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setSearch(new RequestSearch("val", false));
        QueryClauseRequest request = new QueryClauseRequest(requestCriteria, new ParsedQuery(List.of("field1", "field2"), List.of("some clause1 {0}", "some clause2 {0}")));
        String expected = "(" +
                MessageFormat.format(SIMPLE_SEARCH_LEFT + SEARCH_LIKE_TPL, "field1", requestCriteria.getSearch().getValue()) + " or " +
                MessageFormat.format(SIMPLE_SEARCH_LEFT + SEARCH_LIKE_TPL, "field2", requestCriteria.getSearch().getValue()) + " or " +
                "some clause1 " + MessageFormat.format(SEARCH_LIKE_TPL.replace("1", "0"), requestCriteria.getSearch().getValue()) +
                " or some clause2 " +
                MessageFormat.format(SEARCH_LIKE_TPL.replace("1", "0"), requestCriteria.getSearch().getValue()) + ")";

        // WHEN
        String clause = generator.generate(request);

        // THEN
        Assertions.assertEquals(expected, clause);
    }

    @Test
    public void generateWithoutSearch() {
        // GIVEN
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setSearch(new RequestSearch("val", true));
        QueryClauseRequest request = new QueryClauseRequest(requestCriteria, new ParsedQuery(null, null));

        // WHEN
        String clause = generator.generate(request);

        // THEN
        Assertions.assertEquals("", clause);
    }

    @Test
    public void canHandle() {
        // WHEN
        boolean yes = generator.canHandle(QueryClauseType.SEARCH, true);
        boolean noType = generator.canHandle(QueryClauseType.ORDER, true);
        boolean noNative = generator.canHandle(QueryClauseType.SEARCH, false);

        // THEN
        Assertions.assertTrue(yes);
        Assertions.assertFalse(noType);
        Assertions.assertFalse(noNative);
    }
}
