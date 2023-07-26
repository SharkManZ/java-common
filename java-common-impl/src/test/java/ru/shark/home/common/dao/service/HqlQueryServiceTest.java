package ru.shark.home.common.dao.service;

import ru.shark.home.common.QueryUtils;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory;
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
import ru.shark.home.common.dao.repository.query.generator.HqlFilterClauseGenerator;
import ru.shark.home.common.dao.repository.query.generator.HqlOrderClauseGenerator;
import ru.shark.home.common.dao.repository.query.generator.HqlSearchClauseGenerator;
import ru.shark.home.common.dao.repository.query.generator.QueryClauseGenerator;
import ru.shark.home.common.enums.FieldType;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static ru.shark.home.common.dao.repository.query.generator.GeneratorConstants.FILTER_STRING_EQ_TPL;
import static ru.shark.home.common.dao.repository.query.generator.GeneratorConstants.SEARCH_EQ_TPL;
import static ru.shark.home.common.dao.repository.query.generator.GeneratorConstants.SEARCH_LIKE_TPL;
import static ru.shark.home.common.dao.repository.query.generator.GeneratorConstants.SIMPLE_SEARCH_LEFT;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HqlQueryServiceTest {

    private HqlQueryService hqlQueryService;
    private Session session;

    @BeforeAll
    public void init() {
        session = mock(Session.class);
        hqlQueryService = new HqlQueryService();
        hqlQueryService.setEntityManager(session);
        QueryClauseGenerator generator = new QueryClauseGenerator();
        generator.setGenerators(List.of(new HqlSearchClauseGenerator(), new HqlFilterClauseGenerator(), new HqlOrderClauseGenerator()));
        hqlQueryService.setQueryClauseGenerator(generator);
    }

    @BeforeEach
    public void initMethod() {
        reset(session);
        SessionImplementor sessionImplementor = mock(SessionImplementor.class);
        SessionFactoryImplementor sessionFactoryImplementor = mock(SessionFactoryImplementor.class);
        when(sessionImplementor.getFactory()).thenReturn(sessionFactoryImplementor);
        when(session.unwrap(eq(SessionImplementor.class))).thenReturn(sessionImplementor);
        hqlQueryService.setQueryTranslatorFactory(new ASTQueryTranslatorFactory());
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
        ParsedQuery parsedQuery = hqlQueryService.parseQuery(query, searchFields, advancedSearchFields);

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
        ParsedQuery parsedQuery = hqlQueryService.parseQuery(query);

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
        ParsedQuery parsedQuery = hqlQueryService.parseQuery(query);

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
        ParsedQuery parsedQuery = hqlQueryService.parseQuery(query);

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
        ParsedQuery parsedQuery = hqlQueryService.parseQuery(query);

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
        ParsedQuery parsedQuery = hqlQueryService.parseQuery(query);

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
        ParsedQuery parsedQuery = hqlQueryService.parseQuery(query);

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
        ParsedQuery parsedQuery = hqlQueryService.parseQuery(query);

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
        ParsedQuery parsedQuery = hqlQueryService.parseQuery(query);
        RequestCriteria request = new RequestCriteria(0, 10);
        List<String> expectedParts = expectedParts();
        String expected = String.join(" ", expectedParts);
        hqlQueryService.setQueryTranslatorFactory(QueryUtils.prepareTranslatorFactory(expected));
        String expectedCount = "select count(1) from (" + expected + ") q";

        // WHEN
        ParamsQuery paramsQuery = hqlQueryService.generateParamsQuery(parsedQuery, request);

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
        ParsedQuery parsedQuery = hqlQueryService.parseQuery(query);
        RequestCriteria request = new RequestCriteria(0, 10);
        List<String> expectedParts = expectedParts();
        String expected = expectedParts.get(0) + " " + expectedParts.get(1) + " " + expectedParts.get(2) + " " + expectedParts.get(4);
        String expectedCount = "select count(1) " + expectedParts.get(1) + " " + expectedParts.get(2);

        // WHEN
        ParamsQuery paramsQuery = hqlQueryService.generateParamsQuery(parsedQuery, request);

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
        ParsedQuery parsedQuery = hqlQueryService.parseQuery(query);
        RequestCriteria request = new RequestCriteria(0, 10);
        request.setSorts(List.of(new RequestSort("name", "DESC"), new RequestSort("id", "ASC")));
        List<String> expectedParts = expectedParts();
        String expected = expectedParts.get(0) + " " + expectedParts.get(1) + " " + expectedParts.get(2) + " " + expectedParts.get(3) + " " +
                "order by s.name desc, s.id asc";
        hqlQueryService.setQueryTranslatorFactory(QueryUtils.prepareTranslatorFactory(expected));
        String expectedCount = "select count(1) from (" + expected + ") q";
        Map<String, Object> params = Map.of("id", 1L);

        // WHEN
        ParamsQuery paramsQuery = hqlQueryService.generateParamsQuery(parsedQuery, request, params);

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
        ParsedQuery parsedQuery = hqlQueryService.parseQuery(query, List.of("name"));
        RequestCriteria request = new RequestCriteria(0, 10);
        request.setSearch(new RequestSearch("val", false));
        request.setSorts(List.of(new RequestSort("name", "DESC"), new RequestSort("id", "ASC")));
        List<String> expectedParts = expectedParts();
        String expected = expectedParts.get(0) + " " + expectedParts.get(1) + " " + expectedParts.get(2) + " " +
                "and (" + MessageFormat.format(SIMPLE_SEARCH_LEFT + SEARCH_LIKE_TPL, "s.name", "val") + ") " +
                expectedParts.get(3) + " " +
                "order by s.name desc, s.id asc";
        hqlQueryService.setQueryTranslatorFactory(QueryUtils.prepareTranslatorFactory(expected));
        String expectedCount = "select count(1) from (" + expected + ") q";
        Map<String, Object> params = Map.of("id", 1L);

        // WHEN
        ParamsQuery paramsQuery = hqlQueryService.generateParamsQuery(parsedQuery, request, params);

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
        ParsedQuery parsedQuery = hqlQueryService.parseQuery(query, List.of("name"));
        RequestCriteria request = new RequestCriteria(0, 10);
        request.setSearch(new RequestSearch("val", false));
        request.setSorts(List.of(new RequestSort("name", "DESC"), new RequestSort("id", "ASC")));
        List<String> expectedParts = expectedParts();
        String expected = expectedParts.get(0) + " " + expectedParts.get(1) + " " +
                "where (" + MessageFormat.format(SIMPLE_SEARCH_LEFT + SEARCH_LIKE_TPL, "s.name", "val") + ") " +
                expectedParts.get(3) + " " +
                "order by s.name desc, s.id asc";
        hqlQueryService.setQueryTranslatorFactory(QueryUtils.prepareTranslatorFactory(expected));
        String expectedCount = "select count(1) from (" + expected + ") q";
        Map<String, Object> params = Map.of("id", 1L);

        // WHEN
        ParamsQuery paramsQuery = hqlQueryService.generateParamsQuery(parsedQuery, request, params);

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
        ParsedQuery parsedQuery = hqlQueryService.parseQuery(query, List.of("name"));
        RequestCriteria request = new RequestCriteria(0, 10);
        request.setSorts(List.of(new RequestSort("name", "DESC"), new RequestSort("id", "ASC")));
        request.setFilters(List.of(new RequestFilter("name", FieldType.STRING, "=", "val")));
        List<String> expectedParts = expectedParts();
        String expected = expectedParts.get(0) + " " + expectedParts.get(1) + " " + expectedParts.get(2) + " " +
                "and (" + MessageFormat.format(FILTER_STRING_EQ_TPL, "s.name", "name") + ") " +
                expectedParts.get(3) + " " +
                "order by s.name desc, s.id asc";
        hqlQueryService.setQueryTranslatorFactory(QueryUtils.prepareTranslatorFactory(expected));
        String expectedCount = "select count(1) from (" + expected + ") q";
        Map<String, Object> params = Map.of("id", 1L, "filter_name", "val");

        // WHEN
        ParamsQuery paramsQuery = hqlQueryService.generateParamsQuery(parsedQuery, request, params);

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
        ParsedQuery parsedQuery = hqlQueryService.parseQuery(query, List.of("name"));
        RequestCriteria request = new RequestCriteria(0, 10);
        request.setSorts(List.of(new RequestSort("name", "DESC"), new RequestSort("id", "ASC")));
        request.setFilters(List.of(new RequestFilter("name", FieldType.STRING, "=", "val")));
        List<String> expectedParts = expectedParts();
        String expected = expectedParts.get(0) + " " + expectedParts.get(1) + " " +
                "where (" + MessageFormat.format(FILTER_STRING_EQ_TPL, "s.name", "name") + ") " +
                expectedParts.get(3) + " " +
                "order by s.name desc, s.id asc";
        hqlQueryService.setQueryTranslatorFactory(QueryUtils.prepareTranslatorFactory(expected));
        String expectedCount = "select count(1) from (" + expected + ") q";
        Map<String, Object> params = Map.of("id", 1L, "filter_name", "val");

        // WHEN
        ParamsQuery paramsQuery = hqlQueryService.generateParamsQuery(parsedQuery, request, params);

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
        ParsedQuery parsedQuery = hqlQueryService.parseQuery(query, List.of("name"));
        RequestCriteria request = new RequestCriteria(0, 10);
        request.setSorts(List.of(new RequestSort("name", "DESC"), new RequestSort("id", "ASC")));
        request.setFilters(List.of(new RequestFilter("name", FieldType.STRING, "=", "val")));
        request.setSearch(new RequestSearch("val", true));
        List<String> expectedParts = expectedParts();
        String expected = expectedParts.get(0) + " " + expectedParts.get(1) + " " +
                "where (" + MessageFormat.format(SIMPLE_SEARCH_LEFT + SEARCH_EQ_TPL, "s.name", "val") + ") " +
                "and (" + MessageFormat.format(FILTER_STRING_EQ_TPL, "s.name", "name") + ") " +
                expectedParts.get(3) + " " +
                "order by s.name desc, s.id asc";
        hqlQueryService.setQueryTranslatorFactory(QueryUtils.prepareTranslatorFactory(expected));
        String expectedCount = "select count(1) from (" + expected + ") q";
        Map<String, Object> params = Map.of("id", 1L, "filter_name", "val");

        // WHEN
        ParamsQuery paramsQuery = hqlQueryService.generateParamsQuery(parsedQuery, request, params);

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
        Map<String, Object> result = hqlQueryService.combineParams(filters, params);

        // THEN
        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertTrue(result.get("filter_field1") instanceof String);
        Assertions.assertTrue(result.get("filter_field2") instanceof Long);
        Assertions.assertTrue(result.get("version") instanceof String);

    }

    private List<String> testQueryParts() {
        return List.of("select  new Map(s.id as id, s.name as name) \n",
                " from SetEntity s join s.series se \n",
                " where lower(s.name) like '%' || lower(:name) || '%' \n" +
                        " and not exists (select 1 from SetEntity s1 where s1.id = s.id) ",
                " group by s.id, s.name ",
                " order  by s.id desc");
    }

    private List<String> expectedParts() {
        return List.of("select new Map(s.id as id, s.name as name)",
                "from SetEntity s join s.series se",
                "where lower(s.name) like '%' || lower(:name) || '%'" +
                        " and not exists (select 1 from SetEntity s1 where s1.id = s.id)",
                "group by s.id, s.name",
                "order by s.id desc");
    }
}
