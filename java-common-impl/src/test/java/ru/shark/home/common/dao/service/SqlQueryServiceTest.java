package ru.shark.home.common.dao.service;

import org.hibernate.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.shark.home.common.dao.common.RequestCriteria;
import ru.shark.home.common.dao.common.RequestFilter;
import ru.shark.home.common.dao.common.RequestSearch;
import ru.shark.home.common.dao.common.RequestSort;
import ru.shark.home.common.dao.repository.query.ParamsQuery;
import ru.shark.home.common.dao.repository.query.ParsedQuery;
import ru.shark.home.common.dao.repository.query.generator.QueryClauseGenerator;
import ru.shark.home.common.dao.repository.query.generator.SqlFilterClauseGenerator;
import ru.shark.home.common.dao.repository.query.generator.SqlOrderClauseGenerator;
import ru.shark.home.common.dao.repository.query.generator.SqlSearchClauseGenerator;
import ru.shark.home.common.enums.FieldType;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static ru.shark.home.common.dao.repository.query.generator.GeneratorConstants.FILTER_STRING_EQ_TPL;
import static ru.shark.home.common.dao.repository.query.generator.GeneratorConstants.SEARCH_EQ_TPL;
import static ru.shark.home.common.dao.repository.query.generator.GeneratorConstants.SEARCH_LIKE_TPL;
import static ru.shark.home.common.dao.repository.query.generator.GeneratorConstants.SIMPLE_SEARCH_LEFT;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SqlQueryServiceTest {

    private SqlQueryService sqlQueryService;
    private Session session;

    @BeforeAll
    public void init() {
        session = mock(Session.class);
        sqlQueryService = new SqlQueryService();
        QueryClauseGenerator generator = new QueryClauseGenerator();
        generator.setGenerators(List.of(new SqlSearchClauseGenerator(), new SqlFilterClauseGenerator(), new SqlOrderClauseGenerator()));
        sqlQueryService.setQueryClauseGenerator(generator);

    }

    @BeforeEach
    public void initMethod() {
        reset(session);
    }

    @Test
    public void prepareQueryWithFrom() {
        // GIVEN
        List<String> parts = testQueryParts();
        String query = parts.get(0) + " " + parts.get(1);
        List<String> expected = expectedParts();
        List<String> searchFields = List.of("id", "name");
        List<String> advancedSearchFields = List.of("some");

        // WHEN
        ParsedQuery parsedQuery = sqlQueryService.parseQuery(query, searchFields, advancedSearchFields);

        // THEN
        Assertions.assertNotNull(parsedQuery);
        Assertions.assertEquals(expected.get(0), parsedQuery.getSelectPart());
        Assertions.assertEquals(expected.get(1), parsedQuery.getFromPart().getValue());
        Assertions.assertNull(parsedQuery.getWherePart());
        Assertions.assertNull(parsedQuery.getGroupPart());
        Assertions.assertNull(parsedQuery.getOrderPart());
        Assertions.assertEquals(searchFields, parsedQuery.getSearchFields());
        Assertions.assertEquals(advancedSearchFields, parsedQuery.getAdvancedSearchFields());
    }

    @Test
    public void prepareQueryWithFromWhere() {
        // GIVEN
        List<String> parts = testQueryParts();
        String query = parts.get(0) + " " + parts.get(1) + " " + parts.get(2);
        List<String> expected = expectedParts();

        // WHEN
        ParsedQuery parsedQuery = sqlQueryService.parseQuery(query);

        // THEN
        Assertions.assertNotNull(parsedQuery);
        Assertions.assertEquals(expected.get(0), parsedQuery.getSelectPart());
        Assertions.assertEquals(expected.get(1), parsedQuery.getFromPart().getValue());
        Assertions.assertEquals(expected.get(2), parsedQuery.getWherePart());
        Assertions.assertNull(parsedQuery.getGroupPart());
        Assertions.assertNull(parsedQuery.getOrderPart());
    }

    @Test
    public void prepareQueryWithFromGroup() {
        // GIVEN
        List<String> parts = testQueryParts();
        String query = parts.get(0) + " " + parts.get(1) + " " + parts.get(3);
        List<String> expected = expectedParts();

        // WHEN
        ParsedQuery parsedQuery = sqlQueryService.parseQuery(query);

        // THEN
        Assertions.assertNotNull(parsedQuery);
        Assertions.assertEquals(expected.get(0), parsedQuery.getSelectPart());
        Assertions.assertEquals(expected.get(1), parsedQuery.getFromPart().getValue());
        Assertions.assertEquals(expected.get(3), parsedQuery.getGroupPart());
        Assertions.assertNull(parsedQuery.getWherePart());
        Assertions.assertNull(parsedQuery.getOrderPart());
    }

    @Test
    public void prepareQueryWithFromWhereGroup() {
        // GIVEN
        List<String> parts = testQueryParts();
        String query = parts.get(0) + " " + parts.get(1) + " " + parts.get(2) + " " + parts.get(3);
        List<String> expected = expectedParts();

        // WHEN
        ParsedQuery parsedQuery = sqlQueryService.parseQuery(query);

        // THEN
        Assertions.assertNotNull(parsedQuery);
        Assertions.assertEquals(expected.get(0), parsedQuery.getSelectPart());
        Assertions.assertEquals(expected.get(1), parsedQuery.getFromPart().getValue());
        Assertions.assertEquals(expected.get(2), parsedQuery.getWherePart());
        Assertions.assertEquals(expected.get(3), parsedQuery.getGroupPart());
        Assertions.assertNull(parsedQuery.getOrderPart());
    }

    @Test
    public void prepareQueryWithFromOrder() {
        // GIVEN
        List<String> parts = testQueryParts();
        String query = parts.get(0) + " " + parts.get(1) + " " + parts.get(4);
        List<String> expected = expectedParts();

        // WHEN
        ParsedQuery parsedQuery = sqlQueryService.parseQuery(query);

        // THEN
        Assertions.assertNotNull(parsedQuery);
        Assertions.assertEquals(expected.get(0), parsedQuery.getSelectPart());
        Assertions.assertEquals(expected.get(1), parsedQuery.getFromPart().getValue());
        Assertions.assertEquals(expected.get(4), parsedQuery.getOrderPart());
        Assertions.assertNull(parsedQuery.getWherePart());
        Assertions.assertNull(parsedQuery.getGroupPart());
    }

    @Test
    public void prepareQueryWithFromWhereOrder() {
        // GIVEN
        List<String> parts = testQueryParts();
        String query = parts.get(0) + " " + parts.get(1) + " " + parts.get(2) + parts.get(4);
        List<String> expected = expectedParts();

        // WHEN
        ParsedQuery parsedQuery = sqlQueryService.parseQuery(query);

        // THEN
        Assertions.assertNotNull(parsedQuery);
        Assertions.assertEquals(expected.get(0), parsedQuery.getSelectPart());
        Assertions.assertEquals(expected.get(1), parsedQuery.getFromPart().getValue());
        Assertions.assertEquals(expected.get(4), parsedQuery.getOrderPart());
        Assertions.assertEquals(expected.get(2), parsedQuery.getWherePart());
        Assertions.assertNull(parsedQuery.getGroupPart());
    }

    @Test
    public void prepareQueryWithFromGroupOrder() {
        // GIVEN
        List<String> parts = testQueryParts();
        String query = parts.get(0) + " " + parts.get(1) + " " + parts.get(3) + parts.get(4);
        List<String> expected = expectedParts();

        // WHEN
        ParsedQuery parsedQuery = sqlQueryService.parseQuery(query);

        // THEN
        Assertions.assertNotNull(parsedQuery);
        Assertions.assertEquals(expected.get(0), parsedQuery.getSelectPart());
        Assertions.assertEquals(expected.get(1), parsedQuery.getFromPart().getValue());
        Assertions.assertEquals(expected.get(4), parsedQuery.getOrderPart());
        Assertions.assertEquals(expected.get(3), parsedQuery.getGroupPart());
        Assertions.assertNull(parsedQuery.getWherePart());
    }

    @Test
    public void prepareQueryWithFromWhereGroupOrder() {
        // GIVEN
        List<String> parts = testQueryParts();
        String query = parts.get(0) + " " + parts.get(1) + " " + parts.get(2) + parts.get(3) + parts.get(4);
        List<String> expected = expectedParts();

        // WHEN
        ParsedQuery parsedQuery = sqlQueryService.parseQuery(query);

        // THEN
        Assertions.assertNotNull(parsedQuery);
        Assertions.assertEquals(expected.get(0), parsedQuery.getSelectPart());
        Assertions.assertEquals(expected.get(1), parsedQuery.getFromPart().getValue());
        Assertions.assertEquals(expected.get(4), parsedQuery.getOrderPart());
        Assertions.assertEquals(expected.get(3), parsedQuery.getGroupPart());
        Assertions.assertEquals(expected.get(2), parsedQuery.getWherePart());
    }

    @Test
    public void generateParamsQueryWithAllPartsAndWithoutCriteria() {
        // GIVEN
        List<String> parts = testQueryParts();
        String query = String.join(" ", parts);
        ParsedQuery parsedQuery = sqlQueryService.parseQuery(query);
        RequestCriteria request = new RequestCriteria(0, 10);
        List<String> expectedParts = expectedParts();
        String expected = String.join(" ", expectedParts);
        String expectedCount = "select count(1) from (" + expectedParts.get(0) + " " + expectedParts.get(1) + " " + expectedParts.get(2) + " " + expectedParts.get(3) + ") q";

        // WHEN
        ParamsQuery paramsQuery = sqlQueryService.generateParamsQuery(parsedQuery, request);

        // THEN
        Assertions.assertNotNull(paramsQuery);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void generateParamsQueryWithoutGroupPartAndWithoutCriteria() {
        // GIVEN
        List<String> parts = testQueryParts();
        String query = parts.get(0) + " " + parts.get(1) + " " + parts.get(2) + " " + parts.get(4);
        ParsedQuery parsedQuery = sqlQueryService.parseQuery(query);
        RequestCriteria request = new RequestCriteria(0, 10);
        List<String> expectedParts = expectedParts();
        String expected = expectedParts.get(0) + " " + expectedParts.get(1) + " " + expectedParts.get(2) + " " + expectedParts.get(4);
        String expectedCount = "select count(1) " + expectedParts.get(1) + " " + expectedParts.get(2);

        // WHEN
        ParamsQuery paramsQuery = sqlQueryService.generateParamsQuery(parsedQuery, request);

        // THEN
        Assertions.assertNotNull(paramsQuery);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void generateParamsQueryWithAllPartsAndOrder() {
        // GIVEN
        List<String> parts = testQueryParts();
        String query = String.join(" ", parts);
        ParsedQuery parsedQuery = sqlQueryService.parseQuery(query);
        RequestCriteria request = new RequestCriteria(0, 10);
        request.setSorts(List.of(new RequestSort("name", "DESC"), new RequestSort("id", "ASC")));
        List<String> expectedParts = expectedParts();
        String expectedNoOrder = expectedParts.get(0) + " " + expectedParts.get(1) + " " + expectedParts.get(2) + " " + expectedParts.get(3);
        String expected = expectedNoOrder + " order by name desc, id asc";
        String expectedCount = "select count(1) from (" + expectedNoOrder + ") q";
        Map<String, Object> params = Map.of("id", 1L);

        // WHEN
        ParamsQuery paramsQuery = sqlQueryService.generateParamsQuery(parsedQuery, request, params);

        // THEN
        Assertions.assertNotNull(paramsQuery);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
        Assertions.assertEquals(params, paramsQuery.getParams());
    }

    @Test
    public void generateParamsQueryWithAllPartsAndSearchAndOrder() {
        // GIVEN
        List<String> parts = testQueryParts();
        String query = String.join(" ", parts);
        ParsedQuery parsedQuery = sqlQueryService.parseQuery(query, List.of("name"));
        RequestCriteria request = new RequestCriteria(0, 10);
        request.setSearch(new RequestSearch("val", false));
        request.setSorts(List.of(new RequestSort("name", "DESC"), new RequestSort("id", "ASC")));
        List<String> expectedParts = expectedParts();
        String expectedNoOrder = expectedParts.get(0) + " " + expectedParts.get(1) + " " + expectedParts.get(2) + " " +
                "and (" + MessageFormat.format(SIMPLE_SEARCH_LEFT + SEARCH_LIKE_TPL, "name", "val") + ") " +
                expectedParts.get(3);
        String expected = expectedNoOrder + " order by name desc, id asc";
        String expectedCount = "select count(1) from (" + expectedNoOrder + ") q";
        Map<String, Object> params = Map.of("id", 1L);

        // WHEN
        ParamsQuery paramsQuery = sqlQueryService.generateParamsQuery(parsedQuery, request, params);

        // THEN
        Assertions.assertNotNull(paramsQuery);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
        Assertions.assertEquals(params, paramsQuery.getParams());
    }

    @Test
    public void generateParamsQueryWithoutWherePartAndSearchAndOrder() {
        // GIVEN
        List<String> parts = testQueryParts();
        String query = parts.get(0) + " " + parts.get(1) + " " + parts.get(3) + " " + parts.get(4);
        ParsedQuery parsedQuery = sqlQueryService.parseQuery(query, List.of("name"));
        RequestCriteria request = new RequestCriteria(0, 10);
        request.setSearch(new RequestSearch("val", false));
        request.setSorts(List.of(new RequestSort("name", "DESC"), new RequestSort("id", "ASC")));
        List<String> expectedParts = expectedParts();
        String expectedNoOrder = expectedParts.get(0) + " " + expectedParts.get(1) + " " +
                "where (" + MessageFormat.format(SIMPLE_SEARCH_LEFT + SEARCH_LIKE_TPL, "name", "val") + ") " +
                expectedParts.get(3);
        String expected = expectedNoOrder + " order by name desc, id asc";
        String expectedCount = "select count(1) from (" + expectedNoOrder + ") q";
        Map<String, Object> params = Map.of("id", 1L);

        // WHEN
        ParamsQuery paramsQuery = sqlQueryService.generateParamsQuery(parsedQuery, request, params);

        // THEN
        Assertions.assertNotNull(paramsQuery);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
        Assertions.assertEquals(params, paramsQuery.getParams());
    }

    @Test
    public void generateParamsQueryWithAllPartsAndFilterAndOrder() {
        // GIVEN
        List<String> parts = testQueryParts();
        String query = String.join(" ", parts);
        ParsedQuery parsedQuery = sqlQueryService.parseQuery(query, List.of("name"));
        RequestCriteria request = new RequestCriteria(0, 10);
        request.setSorts(List.of(new RequestSort("name", "DESC"), new RequestSort("id", "ASC")));
        request.setFilters(List.of(new RequestFilter("name", FieldType.STRING, "=", "val")));
        List<String> expectedParts = expectedParts();
        String expectedMoOrder = expectedParts.get(0) + " " + expectedParts.get(1) + " " + expectedParts.get(2) + " " +
                "and (" + MessageFormat.format(FILTER_STRING_EQ_TPL, "name", "name") + ") " +
                expectedParts.get(3);
        String expected = expectedMoOrder + " order by name desc, id asc";
        String expectedCount = "select count(1) from (" + expectedMoOrder + ") q";
        Map<String, Object> params = Map.of("id", 1L, "filter_name", "val");

        // WHEN
        ParamsQuery paramsQuery = sqlQueryService.generateParamsQuery(parsedQuery, request, params);

        // THEN
        Assertions.assertNotNull(paramsQuery);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
        Assertions.assertEquals(params, paramsQuery.getParams());
    }

    @Test
    public void generateParamsQueryWithoutWherePartAndFilterAndOrder() {
        // GIVEN
        List<String> parts = testQueryParts();
        String query = parts.get(0) + " " + parts.get(1) + " " + parts.get(3) + " " + parts.get(4);
        ParsedQuery parsedQuery = sqlQueryService.parseQuery(query, List.of("name"));
        RequestCriteria request = new RequestCriteria(0, 10);
        request.setSorts(List.of(new RequestSort("name", "DESC"), new RequestSort("id", "ASC")));
        request.setFilters(List.of(new RequestFilter("name", FieldType.STRING, "=", "val")));
        List<String> expectedParts = expectedParts();
        String expectedNoOrder = expectedParts.get(0) + " " + expectedParts.get(1) + " " +
                "where (" + MessageFormat.format(FILTER_STRING_EQ_TPL, "name", "name") + ") " +
                expectedParts.get(3);
        String expected = expectedNoOrder + " order by name desc, id asc";
        String expectedCount = "select count(1) from (" + expectedNoOrder + ") q";
        Map<String, Object> params = Map.of("id", 1L, "filter_name", "val");

        // WHEN
        ParamsQuery paramsQuery = sqlQueryService.generateParamsQuery(parsedQuery, request, params);

        // THEN
        Assertions.assertNotNull(paramsQuery);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
        Assertions.assertEquals(params, paramsQuery.getParams());
    }

    @Test
    public void generateParamsQueryWithoutWherePartAndFilterAndSearchAndOrder() {
        // GIVEN
        List<String> parts = testQueryParts();
        String query = parts.get(0) + " " + parts.get(1) + " " + parts.get(3) + " " + parts.get(4);
        ParsedQuery parsedQuery = sqlQueryService.parseQuery(query, List.of("name"));
        RequestCriteria request = new RequestCriteria(0, 10);
        request.setSorts(List.of(new RequestSort("name", "DESC"), new RequestSort("id", "ASC")));
        request.setFilters(List.of(new RequestFilter("name", FieldType.STRING, "=", "val")));
        request.setSearch(new RequestSearch("val", true));
        List<String> expectedParts = expectedParts();
        String expectedNoOrder = expectedParts.get(0) + " " + expectedParts.get(1) + " " +
                "where (" + MessageFormat.format(SIMPLE_SEARCH_LEFT + SEARCH_EQ_TPL, "name", "val") + ") " +
                "and (" + MessageFormat.format(FILTER_STRING_EQ_TPL, "name", "name") + ") " +
                expectedParts.get(3);
        String expected = expectedNoOrder + " order by name desc, id asc";
        String expectedCount = "select count(1) from (" + expectedNoOrder + ") q";
        Map<String, Object> params = Map.of("id", 1L, "filter_name", "val");

        // WHEN
        ParamsQuery paramsQuery = sqlQueryService.generateParamsQuery(parsedQuery, request, params);

        // THEN
        Assertions.assertNotNull(paramsQuery);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
        Assertions.assertEquals(params, paramsQuery.getParams());
    }

    @Test
    public void combineParams() {
        // GIVEN
        List<RequestFilter> filters = List.of(new RequestFilter("field1", FieldType.STRING, "=", "val"),
                new RequestFilter("field2", FieldType.INTEGER, "=", "1"));
        Map<String, Object> params = Map.of("version", "1.0");

        // WHEN
        Map<String, Object> result = sqlQueryService.combineParams(filters, params);

        // THEN
        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertTrue(result.get("filter_field1") instanceof String);
        Assertions.assertTrue(result.get("filter_field2") instanceof Long);
        Assertions.assertTrue(result.get("version") instanceof String);

    }

    private List<String> testQueryParts() {
        return List.of("select max(id) as id, partColorId, userId, colorNumber, alternateColorNumber, number, alternateNumber,\n" +
                        "            categoryName, partName,\n" +
                        "            sum(coalesce(setsCount, 0)) + sum(coalesce(userCount, 0)) as userCount, max(coalesce(setsCount, 0)) as setsCount\n",
                "            from (\n" +
                        "                select null as id, lpc.lego_id as partColorId, lus.lego_user_id as userId, lpc.lego_number as colorNumber,\n" +
                        "                    lpc.lego_alternate_number as alternateColorNumber, lp.lego_number as number,\n" +
                        "                    lp.lego_alternate_number as alternateNumber,\n" +
                        "                    lpcc.lego_name as categoryName, lp.lego_name as partName, null as userCount, sum(lsp.lego_count) as setsCount\n" +
                        "                from lego_user_sets lus\n" +
                        "                    join lego_set_part lsp on lsp.lego_set_id = lus.lego_set_id\n" +
                        "                    join lego_part_color lpc on lpc.lego_id = lsp.lego_part_color_id\n" +
                        "                    join lego_part lp on lp.lego_id = lpc.lego_part_id\n" +
                        "                    join lego_part_category lpcc on lpcc.lego_id = lp.lego_part_category_id\n" +
                        "                    join lego_color lc on lc.lego_id = lpc.lego_color_id\n" +
                        "                where lus.lego_user_id = :userId\n" +
                        "                group by lus.lego_user_id, lpc.lego_id, lpc.lego_number, lpc.lego_alternate_number, lp.lego_number,\n" +
                        "                    lp.lego_alternate_number, lpcc.lego_name, lp.lego_name\n" +
                        "                union all\n" +
                        "                select lup.lego_id as id, lup.lego_part_color_id as partColorId, lup.lego_user_id as userId, lpc.lego_number as colorNumber,\n" +
                        "                    lpc.lego_alternate_number as alternateColorNumber, lp.lego_number as number,\n" +
                        "                    lp.lego_alternate_number as alternateNumber,\n" +
                        "                    lpcc.lego_name as categoryName, lp.lego_name as partName, lup.lego_count as userCount, null as setsCount\n" +
                        "                from lego_user_parts lup\n" +
                        "                    join lego_part_color lpc on lpc.lego_id = lup.lego_part_color_id\n" +
                        "                    join lego_part lp on lp.lego_id = lpc.lego_part_id\n" +
                        "                    join lego_part_category lpcc on lpcc.lego_id = lp.lego_part_category_id\n" +
                        "                where lup.lego_user_id = :userId\n" +
                        "            ) up\n",
                " where up.partColorId in (1,2,3) \n",
                "            group by partColorId, userId, colorNumber, alternateColorNumber, number, alternateNumber, categoryName, partName\n",
                "            order by colorNumber, number");
    }

    private List<String> expectedParts() {
        return List.of("select max(id) as id, partColorId, userId, colorNumber, alternateColorNumber, number, alternateNumber, " +
                        "categoryName, partName, " +
                        "sum(coalesce(setsCount, 0)) + sum(coalesce(userCount, 0)) as userCount, max(coalesce(setsCount, 0)) as setsCount",
                "from ( " +
                        "select null as id, lpc.lego_id as partColorId, lus.lego_user_id as userId, lpc.lego_number as colorNumber, " +
                        "lpc.lego_alternate_number as alternateColorNumber, lp.lego_number as number, " +
                        "lp.lego_alternate_number as alternateNumber, " +
                        "lpcc.lego_name as categoryName, lp.lego_name as partName, null as userCount, sum(lsp.lego_count) as setsCount " +
                        "from lego_user_sets lus " +
                        "join lego_set_part lsp on lsp.lego_set_id = lus.lego_set_id " +
                        "join lego_part_color lpc on lpc.lego_id = lsp.lego_part_color_id " +
                        "join lego_part lp on lp.lego_id = lpc.lego_part_id " +
                        "join lego_part_category lpcc on lpcc.lego_id = lp.lego_part_category_id " +
                        "join lego_color lc on lc.lego_id = lpc.lego_color_id " +
                        "where lus.lego_user_id = :userId " +
                        "group by lus.lego_user_id, lpc.lego_id, lpc.lego_number, lpc.lego_alternate_number, lp.lego_number, " +
                        "lp.lego_alternate_number, lpcc.lego_name, lp.lego_name " +
                        "union all " +
                        "select lup.lego_id as id, lup.lego_part_color_id as partColorId, lup.lego_user_id as userId, lpc.lego_number as colorNumber, " +
                        "lpc.lego_alternate_number as alternateColorNumber, lp.lego_number as number, " +
                        "lp.lego_alternate_number as alternateNumber, " +
                        "lpcc.lego_name as categoryName, lp.lego_name as partName, lup.lego_count as userCount, null as setsCount " +
                        "from lego_user_parts lup " +
                        "join lego_part_color lpc on lpc.lego_id = lup.lego_part_color_id " +
                        "join lego_part lp on lp.lego_id = lpc.lego_part_id " +
                        "join lego_part_category lpcc on lpcc.lego_id = lp.lego_part_category_id " +
                        "where lup.lego_user_id = :userId " +
                        ") up",
                "where up.partColorId in (1,2,3)",
                "group by partColorId, userId, colorNumber, alternateColorNumber, number, alternateNumber, categoryName, partName",
                "order by colorNumber, number");
    }
}
