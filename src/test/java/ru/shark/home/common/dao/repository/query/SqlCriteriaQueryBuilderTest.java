package ru.shark.home.common.dao.repository.query;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.shark.home.common.dao.common.RequestCriteria;
import ru.shark.home.common.dao.common.RequestFilter;
import ru.shark.home.common.dao.common.RequestSearch;
import ru.shark.home.common.dao.common.RequestSort;
import ru.shark.home.common.enums.FieldType;

import javax.persistence.EntityManager;
import java.util.Arrays;

import static org.mockito.Mockito.mock;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SqlCriteriaQueryBuilderTest {

    private EntityManager em;

    @BeforeAll
    public void init() {
        em = mock(EntityManager.class);
    }

    @Test
    public void buildWithSelectFrom() {
        // GIVEN
        SqlCriteriaQueryBuilder query = new SqlCriteriaQueryBuilder(em);
        query.setSelectPart("select s.c_id as id, s.c_name as name");
        query.setFromPart("from t_table1 s");
        String expected = "select s.c_id as id, s.c_name as name from t_table1 s";
        String expectedCount = "select count(1) from t_table1 s";

        // WHEN
        ParamsQuery build = query.build(new RequestCriteria(0, 10));

        // THEN
        Assertions.assertNotNull(build);
        Assertions.assertEquals(expected, build.getQueryString());
        Assertions.assertEquals(expectedCount, build.getCountQueryString());
    }

    @Test
    public void buildWithSelectFromWhere() {
        // GIVEN
        SqlCriteriaQueryBuilder query = new SqlCriteriaQueryBuilder(em);
        query.setSelectPart("select s.c_id as id, s.c_name as name");
        query.setFromPart("from t_table1 s");
        query.setWherePart("where s.c_id > 10");
        String expected = "select s.c_id as id, s.c_name as name from t_table1 s where s.c_id > 10";
        String expectedCount = "select count(1) from t_table1 s where s.c_id > 10";

        // WHEN
        ParamsQuery build = query.build(new RequestCriteria(0, 10));

        // THEN
        Assertions.assertNotNull(build);
        Assertions.assertEquals(expected, build.getQueryString());
        Assertions.assertEquals(expectedCount, build.getCountQueryString());
    }

    @Test
    public void buildWithSelectFromWhereGroupBy() {
        // GIVEN
        SqlCriteriaQueryBuilder query = new SqlCriteriaQueryBuilder(em);

        query.setSelectPart("select s.c_id as id, s.c_name as name");
        query.setFromPart("from t_table1 s");
        query.setWherePart("where s.c_id > 10");
        query.setGroupPart("group by s.c_name having count(s.c_id) > 2");
        String expected = "select s.c_id as id, s.c_name as name from t_table1 s where s.c_id > 10 " +
                "group by s.c_name having count(s.c_id) > 2";
        String expectedCount = "select count(1) from (select s.c_id as id, s.c_name as name from t_table1 s where s.c_id > 10 group by s.c_name having count(s.c_id) > 2) q";

        // WHEN
        ParamsQuery build = query.build(new RequestCriteria(0, 10));

        // THEN
        Assertions.assertNotNull(build);
        Assertions.assertEquals(expected, build.getQueryString());
        Assertions.assertEquals(expectedCount, build.getCountQueryString());
    }

    @Test
    public void buildWithSelectFromWhereGroupByOrderBy() {
        // GIVEN
        SqlCriteriaQueryBuilder query = new SqlCriteriaQueryBuilder(em);
        query.setSelectPart("select s.c_id as id, s.c_name as name");
        query.setFromPart("from t_table1 s");
        query.setWherePart("where s.c_id > 10");
        query.setGroupPart("group by s.c_name having count(s.c_id) > 2");
        query.setOrderPart("order by s.c_name");
        String expected = "select s.c_id as id, s.c_name as name from t_table1 s where s.c_id > 10 " +
                "group by s.c_name having count(s.c_id) > 2 order by s.c_name";
        String expectedCount = "select count(1) from (select s.c_id as id, s.c_name as name from t_table1 s where s.c_id > 10 group by s.c_name having count(s.c_id) > 2) q";

        // WHEN
        ParamsQuery build = query.build(new RequestCriteria(0, 10));

        // THEN
        Assertions.assertNotNull(build);
        Assertions.assertEquals(expected, build.getQueryString());
        Assertions.assertEquals(expectedCount, build.getCountQueryString());
    }

    @Test
    public void buildWithSelectFromOrderBy() {
        // GIVEN
        SqlCriteriaQueryBuilder query = new SqlCriteriaQueryBuilder(em);
        query.setSelectPart("select s.c_id as id, s.c_name as name");
        query.setFromPart("from t_table1 s");
        query.setOrderPart("order by s.c_name");
        String expected = "select s.c_id as id, s.c_name as name from t_table1 s order by s.c_name";
        String expectedCount = "select count(1) from t_table1 s";

        // WHEN
        ParamsQuery build = query.build(new RequestCriteria(0, 10));

        // THEN
        Assertions.assertNotNull(build);
        Assertions.assertEquals(expected, build.getQueryString());
        Assertions.assertEquals(expectedCount, build.getCountQueryString());
    }

    @Test
    public void buildWithSelectFromOrderByAndSearch() {
        // GIVEN
        SqlCriteriaQueryBuilder query = new SqlCriteriaQueryBuilder(em, Arrays.asList("s.c_name", "s.c_description"));
        query.setSelectPart("select s.c_id as id, s.c_name as name");
        query.setFromPart("from t_table1 s");
        query.setOrderPart("order by s.c_name");
        String expected = "select s.c_id as id, s.c_name as name from t_table1 s " +
                "where (lower(s.c_name) = lower('test') or lower(s.c_description) = lower('test')) order by s.c_name";
        String expectedCount = "select count(1) from t_table1 s " +
                "where (lower(s.c_name) = lower('test') or lower(s.c_description) = lower('test'))";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setSearch(new RequestSearch("test", true));

        // WHEN
        ParamsQuery build = query.build(requestCriteria);

        // THEN
        Assertions.assertNotNull(build);
        Assertions.assertEquals(expected, build.getQueryString());
        Assertions.assertEquals(expectedCount, build.getCountQueryString());
    }

    @Test
    public void buildWithSelectFromAndSort() {
        // GIVEN
        SqlCriteriaQueryBuilder query = new SqlCriteriaQueryBuilder(em);
        query.setSelectPart("select s.c_id as id, s.c_name as name");
        query.setFromPart("from t_table1 s");
        String expected = "select s.c_id as id, s.c_name as name from t_table1 s " +
                "order by s.c_name asc, s.c_nameAsc asc, s.c_nameDesc desc";
        String expectedCount = "select count(1) from t_table1 s";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setSorts(Arrays.asList(
                new RequestSort("s.c_name", null),
                new RequestSort("s.c_nameAsc", "asc"),
                new RequestSort("s.c_nameDesc", "desc")));

        // WHEN
        ParamsQuery build = query.build(requestCriteria);

        // THEN
        Assertions.assertNotNull(build);
        Assertions.assertEquals(expected, build.getQueryString());
        Assertions.assertEquals(expectedCount, build.getCountQueryString());
    }

    @Test
    public void buildWithSelectFromAndOrderAndDefaultOrder() {
        // GIVEN
        SqlCriteriaQueryBuilder query = new SqlCriteriaQueryBuilder(em, Arrays.asList("s.c_name", "s.c_description"));
        query.setSelectPart("select s.c_id as id, s.c_name as name");
        query.setFromPart("from t_table1 s");
        query.setOrderPart("order by s.c_id desc");
        String expected = "select s.c_id as id, s.c_name as name from t_table1 s " +
                "order by s.c_name asc, s.c_nameAsc asc, s.c_nameDesc desc";
        String expectedCount = "select count(1) from t_table1 s";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setSorts(Arrays.asList(
                new RequestSort("s.c_name", null),
                new RequestSort("s.c_nameAsc", "asc"),
                new RequestSort("s.c_nameDesc", "desc")));

        // WHEN
        ParamsQuery build = query.build(requestCriteria);

        // THEN
        Assertions.assertNotNull(build);
        Assertions.assertEquals(expected, build.getQueryString());
        Assertions.assertEquals(expectedCount, build.getCountQueryString());
    }

    @Test
    public void buildWithSelectFromAndFilter() {
        // GIVEN
        SqlCriteriaQueryBuilder query = new SqlCriteriaQueryBuilder(em, Arrays.asList("s.c_name", "s.c_description"));
        query.setSelectPart("select s.c_id as id, s.c_name as name");
        query.setFromPart("from t_table1 s join t_table2 s1 on s1.c_parent_id = s.c_id ");
        String expected = "select s.c_id as id, s.c_name as name from t_table1 s join t_table2 s1 on s1.c_parent_id = s.c_id " +
                "where (lower(s.c_name) = lower(:sC_name) and s1.c_id = :s1C_id " +
                "and lower(s.c_name1) like '%' || lower(:sC_name1) || '%')";
        String expectedCount = "select count(1) from t_table1 s join t_table2 s1 on s1.c_parent_id = s.c_id " +
                "where (lower(s.c_name) = lower(:sC_name) and s1.c_id = :s1C_id " +
                "and lower(s.c_name1) like '%' || lower(:sC_name1) || '%')";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setFilters(Arrays.asList(
                new RequestFilter("s.c_name", FieldType.STRING, "=", "MaX"),
                new RequestFilter("s1.c_id", FieldType.INTEGER, "=", "1"),
                new RequestFilter("s.c_name1", FieldType.STRING, "~", "")));

        // WHEN
        ParamsQuery build = query.build(requestCriteria);

        // THEN
        Assertions.assertNotNull(build);
        Assertions.assertEquals(expected, build.getQueryString());
        Assertions.assertEquals(expectedCount, build.getCountQueryString());
    }

    @Test
    public void buildWithSelectFromWhereAndFilter() {
        // GIVEN
        SqlCriteriaQueryBuilder query = new SqlCriteriaQueryBuilder(em, Arrays.asList("s.c_name", "s.c_description"));
        query.setSelectPart("select s.c_id as id, s.c_name as name");
        query.setFromPart("from t_table1 s join t_table2 s1 on s1.c_parent_id = s.c_id ");
        query.setWherePart("where s.c_id > 1");
        String expected = "select s.c_id as id, s.c_name as name from t_table1 s join t_table2 s1 on s1.c_parent_id = s.c_id " +
                "where s.c_id > 1 and (lower(s.c_name) = lower(:sC_name) and s1.c_id = :s1C_id " +
                "and lower(s.c_name1) like '%' || lower(:sC_name1) || '%')";
        String expectedCount = "select count(1) from t_table1 s join t_table2 s1 on s1.c_parent_id = s.c_id " +
                "where s.c_id > 1 and (lower(s.c_name) = lower(:sC_name) and s1.c_id = :s1C_id " +
                "and lower(s.c_name1) like '%' || lower(:sC_name1) || '%')";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setFilters(Arrays.asList(
                new RequestFilter("s.c_name", FieldType.STRING, "=", "MaX"),
                new RequestFilter("s1.c_id", FieldType.INTEGER, "=", "1"),
                new RequestFilter("s.c_name1", FieldType.STRING, "~", "")));

        // WHEN
        ParamsQuery build = query.build(requestCriteria);

        // THEN
        Assertions.assertNotNull(build);
        Assertions.assertEquals(expected, build.getQueryString());
        Assertions.assertEquals(expectedCount, build.getCountQueryString());
    }

    @Test
    public void buildWithSelectFromOrderByAndSearchLike() {
        // GIVEN
        SqlCriteriaQueryBuilder query = new SqlCriteriaQueryBuilder(em, Arrays.asList("s.c_name", "s.c_description"));
        query.setSelectPart("select s.c_id as id, s.c_name as name");
        query.setFromPart("from t_table1 s join t_table2 s1 on s1.c_parent_id = s.c_id ");
        query.setOrderPart("order by s.c_id desc");
        String expected = "select s.c_id as id, s.c_name as name from t_table1 s join t_table2 s1 on s1.c_parent_id = s.c_id " +
                "where (lower(s.c_name) like '%' || lower('test') || '%' or lower(s.c_description) like '%' || lower('test') || '%') " +
                "order by s.c_id desc";
        String expectedCount = "select count(1) from t_table1 s join t_table2 s1 on s1.c_parent_id = s.c_id " +
                "where (lower(s.c_name) like '%' || lower('test') || '%' or lower(s.c_description) like '%' || lower('test') || '%')";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setSearch(new RequestSearch("test", false));

        // WHEN
        ParamsQuery build = query.build(requestCriteria);

        // THEN
        Assertions.assertNotNull(build);
        Assertions.assertEquals(expected, build.getQueryString());
        Assertions.assertEquals(expectedCount, build.getCountQueryString());
    }
}
