package ru.shark.home.common.services;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import ru.shark.home.common.dao.common.RequestCriteria;
import ru.shark.home.common.dao.common.RequestSearch;
import ru.shark.home.common.dao.repository.query.HqlCriteriaQueryBuilder;
import ru.shark.home.common.dao.repository.query.ParamsQuery;
import ru.shark.home.common.dao.service.HqlQueryService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HqlQueryServiceTest {

    private HqlQueryService hqlQueryService;
    private Session session;

    @BeforeAll
    public void init() {
        session = mock(Session.class);
        hqlQueryService = new HqlQueryService();
        hqlQueryService.setEntityManager(session);
    }

    @BeforeEach
    public void initMethod() {
        reset(session);
    }

    @Test
    public void prepareQueryWithSimpleSelectFrom() {
        // GIVEN
        String query = "select  s.id, s.name, s.name || '  ' || s.description as fullName \n" +
                " from SetEntity s";
        String expected = "select  s.id, s.name, s.name || '  ' || s.description as fullName from SetEntity s";
        String expectedCount = "select count(1) from SetEntity s";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);

        // WHEN
        HqlCriteriaQueryBuilder result = hqlQueryService.prepareQuery(query);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void prepareQueryWithSimpleSelectFromWhere() {
        // GIVEN
        String query = "select  s.id, s.name \n" +
                " from SetEntity s \n" +
                " where lower(s.name) like '%' || lower(:name) || '%'";
        String expected = "select  s.id, s.name from SetEntity s where lower(s.name) like '%' || lower(:name) || '%'";
        String expectedCount = "select count(1) from SetEntity s where lower(s.name) like '%' || lower(:name) || '%'";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);

        // WHEN
        HqlCriteriaQueryBuilder result = hqlQueryService.prepareQuery(query);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void prepareQueryWithSimpleSelectFromOrder() {
        // GIVEN
        String query = "select s.id as id, s.name as name \n" +
                " from SetEntity s join s.series se \n" +
                " order  by s.id desc";
        String expected = "select s.id as id, s.name as name from SetEntity s join s.series se " +
                "order  by s.id desc";
        String expectedCount = "select count(1) from SetEntity s join s.series se";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);

        // WHEN
        HqlCriteriaQueryBuilder result = hqlQueryService.prepareQuery(query);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void prepareQueryWithSimpleSelectFromWhereOrder() {
        // GIVEN
        String query = "select s.id as id, s.name as name \n" +
                " from SetEntity s join s.series se \n" +
                " where lower(s.name) like '%' || lower(:name) || '%' \n" +
                " order  by s.id desc";
        String expected = "select s.id as id, s.name as name from SetEntity s join s.series se " +
                "where lower(s.name) like '%' || lower(:name) || '%' " +
                "order  by s.id desc";
        String expectedCount = "select count(1) from SetEntity s join s.series se " +
                "where lower(s.name) like '%' || lower(:name) || '%'";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);

        // WHEN
        HqlCriteriaQueryBuilder result = hqlQueryService.prepareQuery(query);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void prepareQueryWithSimpleSelectFromGroup() {
        // GIVEN
        String query = "select s.id as id, s.name as name \n" +
                " from SetEntity s join s.series se \n" +
                " group  by s.id";
        String expected = "select s.id as id, s.name as name from SetEntity s join s.series se " +
                "group  by s.id";
        String expectedCount = "select count(1) from SetEntity s join s.series se " +
                "group  by s.id";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);

        // WHEN
        HqlCriteriaQueryBuilder result = hqlQueryService.prepareQuery(query);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());

    }

    @Test
    public void prepareQueryWithSimpleSelectFromWhereGroup() {
        // GIVEN
        String query = "select s.id as id, s.name as name \n" +
                " from SetEntity s join s.series se \n" +
                " where lower(s.name) like '%' || lower(:name) || '%' \n" +
                " group  by s.id";
        String expected = "select s.id as id, s.name as name from SetEntity s join s.series se " +
                "where lower(s.name) like '%' || lower(:name) || '%' " +
                "group  by s.id";
        String expectedCount = "select count(1) from SetEntity s join s.series se " +
                "where lower(s.name) like '%' || lower(:name) || '%' " +
                "group  by s.id";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);

        // WHEN
        HqlCriteriaQueryBuilder result = hqlQueryService.prepareQuery(query);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void prepareQueryWithSimpleSelectFromWhereGroupOrder() {
        // GIVEN
        String query = "select s.id as id, s.name as name \n" +
                " from SetEntity s join s.series se \n" +
                " where lower(s.name) like '%' || lower(:name) || '%' \n" +
                " group  by s.id having count(1) > 2 " +
                "order by s.id desc";
        String expected = "select s.id as id, s.name as name from SetEntity s join s.series se " +
                "where lower(s.name) like '%' || lower(:name) || '%' " +
                "group  by s.id having count(1) > 2 " +
                "order by s.id desc";
        String expectedCount = "select count(1) from SetEntity s join s.series se " +
                "where lower(s.name) like '%' || lower(:name) || '%' " +
                "group  by s.id having count(1) > 2";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);

        // WHEN
        HqlCriteriaQueryBuilder result = hqlQueryService.prepareQuery(query);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void prepareQueryWithWhereSubQuery() {
        // GIVEN
        String query = "select  new Map(s.id as id, s.name as name) \n" +
                " from SetEntity s join s.series se \n" +
                " where lower(s.name) like '%' || lower(:name) || '%' \n" +
                " and not exists (select 1 from SetEntity s1 where s1.id = s.id) " +
                " order  by s.id desc";
        String expected = "select  new Map(s.id as id, s.name as name) from SetEntity s join s.series se " +
                "where lower(s.name) like '%' || lower(:name) || '%' " +
                " and not exists (select 1 from SetEntity s1 where s1.id = s.id) " +
                "order  by s.id desc";
        String expectedCount = "select count(1) from SetEntity s join s.series se " +
                "where lower(s.name) like '%' || lower(:name) || '%' " +
                " and not exists (select 1 from SetEntity s1 where s1.id = s.id)";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);

        // WHEN
        HqlCriteriaQueryBuilder result = hqlQueryService.prepareQuery(query);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void prepareQueryWithSelectSubQuery() {
        // GIVEN
        String query = "select  new Map(s.id as id, s.name as name, " +
                "(select sum(d.id) from SomeTable t where t.id != s.id) as summ) \n" +
                " from SetEntity s join s.series se \n" +
                " where lower(s.name) like '%' || lower(:name) || '%' \n" +
                " and not exists (select 1 from SetEntity s1 where s1.id = s.id) " +
                " order  by s.id desc";
        String expected = "select  new Map(s.id as id, s.name as name, " +
                "(select sum(d.id) from SomeTable t where t.id != s.id) as summ) from SetEntity s join s.series se " +
                "where lower(s.name) like '%' || lower(:name) || '%' " +
                " and not exists (select 1 from SetEntity s1 where s1.id = s.id) " +
                "order  by s.id desc";
        String expectedCount = "select count(1) from SetEntity s join s.series se " +
                "where lower(s.name) like '%' || lower(:name) || '%' " +
                " and not exists (select 1 from SetEntity s1 where s1.id = s.id)";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);

        // WHEN
        HqlCriteriaQueryBuilder result = hqlQueryService.prepareQuery(query);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void prepareQueryWithSelectAndWhereSubQueryOrderGroup() {
        // GIVEN
        String query = "select  new Map(s.id as id, s.name as name, " +
                "(select sum(d.id) from SomeTable t where t.id != s.id) as summ) \n" +
                " from SetEntity s join s.series se \n" +
                " where lower(s.name) like '%' || lower(:name) || '%' \n" +
                " and not exists (select 1 from SetEntity s1 where s1.id = s.id) " +
                " group  by s.id having count(1) > 2 " +
                " order  by s.id desc";
        String expected = "select  new Map(s.id as id, s.name as name, " +
                "(select sum(d.id) from SomeTable t where t.id != s.id) as summ) from SetEntity s join s.series se " +
                "where lower(s.name) like '%' || lower(:name) || '%' " +
                " and not exists (select 1 from SetEntity s1 where s1.id = s.id) " +
                "group  by s.id having count(1) > 2 " +
                "order  by s.id desc";
        String expectedCount = "select count(1) from SetEntity s join s.series se " +
                "where lower(s.name) like '%' || lower(:name) || '%' " +
                " and not exists (select 1 from SetEntity s1 where s1.id = s.id) " +
                "group  by s.id having count(1) > 2";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);

        // WHEN
        HqlCriteriaQueryBuilder result = hqlQueryService.prepareQuery(query);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void prepareQueryWithoutWhereAndWithSearchEq() {
        // GIVEN
        String query = "select  new Map(s.id as id, s.name as name, " +
                "(select sum(d.id) from SomeTable t where t.id != s.id) as summ) \n" +
                " from SetEntity s join s.series se \n";
        String expected = "select  new Map(s.id as id, s.name as name, " +
                "(select sum(d.id) from SomeTable t where t.id != s.id) as summ) from SetEntity s join s.series se " +
                "where (lower(s.name) = lower('MaX') or lower(s.description) = lower('MaX'))";
        String expectedCount = "select count(1) from SetEntity s join s.series se " +
                "where (lower(s.name) = lower('MaX') or lower(s.description) = lower('MaX'))";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setSearch(new RequestSearch("MaX", true));
        List<String> searchFields = Arrays.asList("name", "description");

        // WHEN
        HqlCriteriaQueryBuilder result = hqlQueryService.prepareQuery(query, searchFields);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void prepareQueryWithoutWhereAndWithSearchLike() {
        // GIVEN
        String query = "select  new Map(s.id as id, s.name as name, " +
                "(select sum(d.id) from SomeTable t where t.id != s.id) as summ) \n" +
                " from SetEntity s join s.series se \n";
        String expected = "select  new Map(s.id as id, s.name as name, " +
                "(select sum(d.id) from SomeTable t where t.id != s.id) as summ) from SetEntity s join s.series se " +
                "where (lower(s.name) like '%' || lower('MaX') || '%' or lower(s.description) like '%' || lower('MaX') || '%')";
        String expectedCount = "select count(1) from SetEntity s join s.series se " +
                "where (lower(s.name) like '%' || lower('MaX') || '%' or lower(s.description) like '%' || lower('MaX') || '%')";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setSearch(new RequestSearch("MaX", false));
        List<String> searchFields = Arrays.asList("name", "description");

        // WHEN
        HqlCriteriaQueryBuilder result = hqlQueryService.prepareQuery(query, searchFields);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void prepareQueryWithWhereAndWithSearchLike() {
        // GIVEN
        String query = "select  new Map(s.id as id, s.name as name, " +
                "(select sum(d.id) from SomeTable t where t.id != s.id) as summ) \n" +
                " from SetEntity s join s.series se \n" +
                " where s.id > 10";
        String expected = "select  new Map(s.id as id, s.name as name, " +
                "(select sum(d.id) from SomeTable t where t.id != s.id) as summ) from SetEntity s join s.series se " +
                "where s.id > 10 and (lower(s.name) like '%' || lower('MaX') || '%' or lower(s.description) like '%' || lower('MaX') || '%')";
        String expectedCount = "select count(1) from SetEntity s join s.series se " +
                "where s.id > 10 and (lower(s.name) like '%' || lower('MaX') || '%' or lower(s.description) like '%' || lower('MaX') || '%')";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setSearch(new RequestSearch("MaX", false));
        List<String> searchFields = Arrays.asList("name", "description");

        // WHEN
        HqlCriteriaQueryBuilder result = hqlQueryService.prepareQuery(query, searchFields);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void prepareQueryWithoutAliasWithWhereAndWithSearchLike() {
        // GIVEN
        String query = "select  new Map(s.id as id, s.name as name, " +
                "(select sum(d.id) from SomeTable t where t.id != s.id) as summ) \n" +
                " from SetEntity\n" +
                " where id > 10";
        String expected = "select  new Map(s.id as id, s.name as name, " +
                "(select sum(d.id) from SomeTable t where t.id != s.id) as summ) from SetEntity " +
                "where id > 10 and (lower(name) like '%' || lower('MaX') || '%' or lower(description) like '%' || lower('MaX') || '%')";
        String expectedCount = "select count(1) from SetEntity " +
                "where id > 10 and (lower(name) like '%' || lower('MaX') || '%' or lower(description) like '%' || lower('MaX') || '%')";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setSearch(new RequestSearch("MaX", false));
        List<String> searchFields = Arrays.asList("name", "description");

        // WHEN
        HqlCriteriaQueryBuilder result = hqlQueryService.prepareQuery(query, searchFields);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void prepareNamedQueryWithSelectSubQuery() {
        // GIVEN
        String query = "select  new Map(s.id as id, s.name as name, " +
                "(select sum(d.id) from SomeTable t where t.id != s.id) as summ) \n" +
                " from SetEntity s join s.series se \n" +
                " where lower(s.name) like '%' || lower(:name) || '%' \n" +
                " and not exists (select 1 from SetEntity s1 where s1.id = s.id) " +
                " order  by s.id desc";
        String expected = "select  new Map(s.id as id, s.name as name, " +
                "(select sum(d.id) from SomeTable t where t.id != s.id) as summ) from SetEntity s join s.series se " +
                "where lower(s.name) like '%' || lower(:name) || '%' " +
                " and not exists (select 1 from SetEntity s1 where s1.id = s.id) " +
                "order  by s.id desc";
        String expectedCount = "select count(1) from SetEntity s join s.series se " +
                "where lower(s.name) like '%' || lower(:name) || '%' " +
                " and not exists (select 1 from SetEntity s1 where s1.id = s.id)";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        Query namedQuery = mock(Query.class);
        when(namedQuery.getQueryString()).thenReturn(query);
        when(session.getNamedQuery(eq("query1"))).thenReturn(namedQuery);

        // WHEN
        HqlCriteriaQueryBuilder result = hqlQueryService.prepareNamedQuery("query1");

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void prepareHqlNamedQueryWithoutAliasWithWhereAndWithSearchLike() {
        // GIVEN
        String query = "select  new Map(s.id as id, s.name as name, " +
                "(select sum(d.id) from SomeTable t where t.id != s.id) as summ) \n" +
                " from SetEntity\n" +
                " where id > 10";
        String expected = "select  new Map(s.id as id, s.name as name, " +
                "(select sum(d.id) from SomeTable t where t.id != s.id) as summ) from SetEntity " +
                "where id > 10 and (lower(name) like '%' || lower('MaX') || '%' or lower(description) like '%' || lower('MaX') || '%')";
        String expectedCount = "select count(1) from SetEntity " +
                "where id > 10 and (lower(name) like '%' || lower('MaX') || '%' or lower(description) like '%' || lower('MaX') || '%')";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setSearch(new RequestSearch("MaX", false));
        List<String> searchFields = Arrays.asList("name", "description");
        Query namedQuery = mock(Query.class);
        when(namedQuery.getQueryString()).thenReturn(query);
        when(session.getNamedQuery(eq("query1"))).thenReturn(namedQuery);

        // WHEN
        HqlCriteriaQueryBuilder result = hqlQueryService.prepareNamedQuery("query1", searchFields);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }
}
