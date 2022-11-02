package ru.shark.home.common.dao.service;

import org.hibernate.Session;
import org.junit.jupiter.api.*;
import ru.shark.home.common.dao.common.RequestCriteria;
import ru.shark.home.common.dao.repository.query.ParamsQuery;
import ru.shark.home.common.dao.repository.query.SqlCriteriaQueryBuilder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SqlQueryServiceTest {

    private SqlQueryService sqlQueryService;
    private Session session;

    @BeforeAll
    public void init() {
        session = mock(Session.class);
        sqlQueryService = new SqlQueryService();
        sqlQueryService.setEntityManager(session);
    }

    @BeforeEach
    public void initMethod() {
        reset(session);
    }

    @Test
    public void prepareQueryWithSimpleSelectFrom() {
        // GIVEN
        String query = "select  s.pre_id, s.pre_name, s.pre_name || '  ' || s.pre_description as fullName \n" +
                " from pre_set s";
        String expected = "select s.pre_id, s.pre_name, s.pre_name || '  ' || s.pre_description as fullName from pre_set s";
        String expectedCount = "select count(1) from pre_set s";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);

        // WHEN
        SqlCriteriaQueryBuilder result = sqlQueryService.prepareQuery(query);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void prepareQueryWithSimpleSelectFromWhere() {
        // GIVEN
        String query = "select  s.pre_id, s.pre_name \n" +
                " from pre_set s \n" +
                " where lower(s.pre_name) like '%' || lower(:name) || '%'";
        String expected = "select s.pre_id, s.pre_name from pre_set s where lower(s.pre_name) like '%' || lower(:name) || '%'";
        String expectedCount = "select count(1) from pre_set s where lower(s.pre_name) like '%' || lower(:name) || '%'";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);

        // WHEN
        SqlCriteriaQueryBuilder result = sqlQueryService.prepareQuery(query);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void prepareQueryWithSimpleSelectFromOrder() {
        // GIVEN
        String query = "select s.pre_id as id, s.pre_name as name \n" +
                " from pre_set s join pre_series se on se.pre_id = s.pre_series_id \n" +
                " order  by s.pre_id desc";
        String expected = "select s.pre_id as id, s.pre_name as name from pre_set s join pre_series se on se.pre_id = s.pre_series_id " +
                "order by s.pre_id desc";
        String expectedCount = "select count(1) from pre_set s join pre_series se on se.pre_id = s.pre_series_id";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);

        // WHEN
        SqlCriteriaQueryBuilder result = sqlQueryService.prepareQuery(query);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void prepareQueryWithSimpleSelectFromWhereOrder() {
        // GIVEN
        String query = "select s.pre_id as id, s.pre_name as name \n" +
                " from pre_set s join pre_series se on se.pre_id = s.pre_series_id \n" +
                " where lower(s.pre_name) like '%' || lower(:name) || '%' \n" +
                " order  by s.pre_id desc";
        String expected = "select s.pre_id as id, s.pre_name as name from pre_set s join pre_series se on se.pre_id = s.pre_series_id " +
                "where lower(s.pre_name) like '%' || lower(:name) || '%' " +
                "order by s.pre_id desc";
        String expectedCount = "select count(1) from pre_set s join pre_series se on se.pre_id = s.pre_series_id " +
                "where lower(s.pre_name) like '%' || lower(:name) || '%'";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);

        // WHEN
        SqlCriteriaQueryBuilder result = sqlQueryService.prepareQuery(query);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void prepareQueryWithSimpleSelectFromGroup() {
        // GIVEN
        String query = "select s.pre_id as id, s.pre_name as name \n" +
                " from pre_set s join pre_series se on se.pre_id = s.pre_series_id \n" +
                " group  by s.pre_id";
        String expected = "select s.pre_id as id, s.pre_name as name from pre_set s join pre_series se on se.pre_id = s.pre_series_id " +
                "group by s.pre_id";
        String expectedCount = "select count(1) from " +
                "(select s.pre_id as id, s.pre_name as name from pre_set s join pre_series se on se.pre_id = s.pre_series_id group by s.pre_id) q";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);

        // WHEN
        SqlCriteriaQueryBuilder result = sqlQueryService.prepareQuery(query);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());

    }

    @Test
    public void prepareQueryWithSimpleSelectFromWhereGroup() {
        // GIVEN
        String query = "select s.pre_id as id, s.pre_name as name \n" +
                " from pre_set s join pre_series se on se.pre_id = s.pre_series_id \n" +
                " where lower(s.pre_name) like '%' || lower(:name) || '%' \n" +
                " group  by s.pre_id";
        String expected = "select s.pre_id as id, s.pre_name as name from pre_set s join pre_series se on se.pre_id = s.pre_series_id " +
                "where lower(s.pre_name) like '%' || lower(:name) || '%' " +
                "group by s.pre_id";
        String expectedCount = "select count(1) from (" +
                "select s.pre_id as id, s.pre_name as name " +
                "from pre_set s join pre_series se on se.pre_id = s.pre_series_id " +
                "where lower(s.pre_name) like '%' || lower(:name) || '%' " +
                "group by s.pre_id" +
                ") q";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);

        // WHEN
        SqlCriteriaQueryBuilder result = sqlQueryService.prepareQuery(query);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void prepareQueryWithSimpleSelectFromWhereGroupOrder() {
        // GIVEN
        String query = "select s.pre_id as id, s.pre_name as name \n" +
                " from pre_set s join pre_series se on se.pre_id = s.pre_series_id \n" +
                " where lower(s.pre_name) like '%' || lower(:name) || '%' \n" +
                " group  by s.pre_id having count(1) > 2 " +
                "order by s.pre_id desc";
        String expected = "select s.pre_id as id, s.pre_name as name from pre_set s join pre_series se on se.pre_id = s.pre_series_id " +
                "where lower(s.pre_name) like '%' || lower(:name) || '%' " +
                "group by s.pre_id having count(1) > 2 " +
                "order by s.pre_id desc";
        String expectedCount = "select count(1) from (" +
                "select s.pre_id as id, s.pre_name as name " +
                "from pre_set s join pre_series se on se.pre_id = s.pre_series_id " +
                "where lower(s.pre_name) like '%' || lower(:name) || '%' " +
                "group by s.pre_id having count(1) > 2" +
                ") q";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);

        // WHEN
        SqlCriteriaQueryBuilder result = sqlQueryService.prepareQuery(query);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void prepareQueryWithWhereSubQuery() {
        // GIVEN
        String query = "select  s.pre_id as id, s.pre_name as name \n" +
                " from pre_set s join pre_series se on se.pre_id = s.pre_series_id \n" +
                " where lower(s.pre_name) like '%' || lower(:name) || '%' \n" +
                " and not exists (select 1 from pre_set s1 where s1.pre_id = s.pre_id) " +
                " order  by s.pre_id desc";
        String expected = "select s.pre_id as id, s.pre_name as name from pre_set s join pre_series se on se.pre_id = s.pre_series_id " +
                "where lower(s.pre_name) like '%' || lower(:name) || '%' " +
                "and not exists (select 1 from pre_set s1 where s1.pre_id = s.pre_id) " +
                "order by s.pre_id desc";
        String expectedCount = "select count(1) from pre_set s join pre_series se on se.pre_id = s.pre_series_id " +
                "where lower(s.pre_name) like '%' || lower(:name) || '%' " +
                "and not exists (select 1 from pre_set s1 where s1.pre_id = s.pre_id)";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);

        // WHEN
        SqlCriteriaQueryBuilder result = sqlQueryService.prepareQuery(query);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void prepareQueryWithSelectSubQuery() {
        // GIVEN
        String query = "select  s.pre_id as id, s.pre_name as name, " +
                "(select sum(t.pre_id) from pre_set2 t where t.pre_id != s.pre_id) as summ\n" +
                " from pre_set s join pre_series se on se.pre_id = s.pre_series_id\n" +
                " where lower(s.pre_name) like '%' || lower(:name) || '%' \n" +
                " and not exists (select 1 from pre_set s1 where s1.pre_id = s.pre_id)" +
                " order  by s.pre_id desc";
        String expected = "select s.pre_id as id, s.pre_name as name, " +
                "(select sum(t.pre_id) from pre_set2 t where t.pre_id != s.pre_id) as summ from pre_set s join pre_series se on se.pre_id = s.pre_series_id " +
                "where lower(s.pre_name) like '%' || lower(:name) || '%' " +
                "and not exists (select 1 from pre_set s1 where s1.pre_id = s.pre_id) " +
                "order by s.pre_id desc";
        String expectedCount = "select count(1) from pre_set s join pre_series se on se.pre_id = s.pre_series_id " +
                "where lower(s.pre_name) like '%' || lower(:name) || '%' " +
                "and not exists (select 1 from pre_set s1 where s1.pre_id = s.pre_id)";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);

        // WHEN
        SqlCriteriaQueryBuilder result = sqlQueryService.prepareQuery(query);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void prepareQueryWithSelectAndWhereSubQueryOrderGroup() {
        // GIVEN
        String query = "select  s.pre_id as id, s.pre_name as name, " +
                "(select sum(t.pre_id) from pre_set2 t where t.pre_id != s.pre_id) as summ \n" +
                " from pre_set s join pre_series se on se.pre_id = s.pre_series_id \n" +
                " where lower(s.pre_name) like '%' || lower(:name) || '%' \n" +
                " and not exists (select 1 from pre_set s1 where s1.pre_id = s.pre_id) " +
                " group  by s.pre_id having count(1) > 2 " +
                " order  by s.pre_id desc";
        String expected = "select s.pre_id as id, s.pre_name as name, " +
                "(select sum(t.pre_id) from pre_set2 t where t.pre_id != s.pre_id) as summ from pre_set s join pre_series se on se.pre_id = s.pre_series_id " +
                "where lower(s.pre_name) like '%' || lower(:name) || '%' " +
                "and not exists (select 1 from pre_set s1 where s1.pre_id = s.pre_id) " +
                "group by s.pre_id having count(1) > 2 " +
                "order by s.pre_id desc";
        String expectedCount = "select count(1) from (" +
                "select s.pre_id as id, s.pre_name as name, " +
                "(select sum(t.pre_id) from pre_set2 t where t.pre_id != s.pre_id) as summ " +
                "from pre_set s join pre_series se on se.pre_id = s.pre_series_id " +
                "where lower(s.pre_name) like '%' || lower(:name) || '%' " +
                "and not exists (select 1 from pre_set s1 where s1.pre_id = s.pre_id) " +
                "group by s.pre_id having count(1) > 2" +
                ") q";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);

        // WHEN
        SqlCriteriaQueryBuilder result = sqlQueryService.prepareQuery(query);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }

    @Test
    public void prepareSqlNamedQueryWithSelectAndSubSelectInFromWithUnionAndGroupAndOrder() {
        // GIVEN
        String query = "select max(id) as id, partColorId, userId, colorNumber, alternateColorNumber, number, alternateNumber,\n" +
                "            categoryName, partName,\n" +
                "            sum(coalesce(setsCount, 0)) + sum(coalesce(userCount, 0)) as userCount, max(coalesce(setsCount, 0)) as setsCount\n" +
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
                "            ) up\n" +
                "            group by partColorId, userId, colorNumber, alternateColorNumber, number, alternateNumber, categoryName, partName\n" +
                "            order by colorNumber, number";
        String expected = "select max(id) as id, partColorId, userId, colorNumber, alternateColorNumber, number, alternateNumber, " +
                "categoryName, partName, " +
                "sum(coalesce(setsCount, 0)) + sum(coalesce(userCount, 0)) as userCount, max(coalesce(setsCount, 0)) as setsCount " +
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
                ") up " +
                "group by partColorId, userId, colorNumber, alternateColorNumber, number, alternateNumber, categoryName, partName " +
                "order by colorNumber, number";
        String expectedCount = "select count(1) from (" +
                "select max(id) as id, partColorId, userId, colorNumber, alternateColorNumber, number, alternateNumber, " +
                "categoryName, partName, " +
                "sum(coalesce(setsCount, 0)) + sum(coalesce(userCount, 0)) as userCount, max(coalesce(setsCount, 0)) as setsCount " +
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
                ") up " +
                "group by partColorId, userId, colorNumber, alternateColorNumber, number, alternateNumber, categoryName, partName) q";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);

        // WHEN
        SqlCriteriaQueryBuilder result = sqlQueryService.prepareQuery(query);

        // THEN
        Assertions.assertNotNull(result);
        ParamsQuery paramsQuery = result.build(requestCriteria);
        Assertions.assertEquals(expected, paramsQuery.getQueryString());
        Assertions.assertEquals(expectedCount, paramsQuery.getCountQueryString());
    }
}
