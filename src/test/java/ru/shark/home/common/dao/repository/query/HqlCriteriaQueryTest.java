package ru.shark.home.common.dao.repository.query;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.shark.home.common.dao.common.RequestCriteria;
import ru.shark.home.common.dao.common.RequestFilter;
import ru.shark.home.common.dao.common.RequestSearch;
import ru.shark.home.common.enums.FieldType;

import java.util.Arrays;

public class HqlCriteriaQueryTest {

    @Test
    public void buildWithSelectFrom() {
        // GIVEN
        HqlCriteriaQueryBuilder query = new HqlCriteriaQueryBuilder();
        query.setSelectPart("select s");
        query.setFromPart("from Entity s");
        String expected = "select s from Entity s";
        String expectedCount = "select count(1) from Entity s";

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
        HqlCriteriaQueryBuilder query = new HqlCriteriaQueryBuilder();
        query.setSelectPart("select s");
        query.setFromPart("from Entity s");
        query.setWherePart("where s.id > 10");
        String expected = "select s from Entity s where s.id > 10";
        String expectedCount = "select count(1) from Entity s where s.id > 10";

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
        HqlCriteriaQueryBuilder query = new HqlCriteriaQueryBuilder();
        query.setSelectPart("select s");
        query.setFromPart("from Entity s");
        query.setWherePart("where s.id > 10");
        query.setGroupPart("group by s.name having count(s.id) > 2");
        String expected = "select s from Entity s where s.id > 10 group by s.name having count(s.id) > 2";
        String expectedCount = "select count(1) from Entity s where s.id > 10 group by s.name having count(s.id) > 2";

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
        HqlCriteriaQueryBuilder query = new HqlCriteriaQueryBuilder();
        query.setSelectPart("select s");
        query.setFromPart("from Entity s");
        query.setWherePart("where s.id > 10");
        query.setGroupPart("group by s.name having count(s.id) > 2");
        query.setOrderPart("order by s.name");
        String expected = "select s from Entity s where s.id > 10 group by s.name having count(s.id) > 2 order by s.name";
        String expectedCount = "select count(1) from Entity s where s.id > 10 group by s.name having count(s.id) > 2";

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
        HqlCriteriaQueryBuilder query = new HqlCriteriaQueryBuilder();
        query.setSelectPart("select s");
        query.setFromPart("from Entity s");
        query.setOrderPart("order by s.name");
        String expected = "select s from Entity s order by s.name";
        String expectedCount = "select count(1) from Entity s";

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
        HqlCriteriaQueryBuilder query = new HqlCriteriaQueryBuilder(Arrays.asList("name", "description"));
        query.setSelectPart("select s");
        query.setFromPart("from Entity s");
        query.setOrderPart("order by s.name");
        String expected = "select s from Entity s " +
                "where (lower(s.name) = lower('test') or lower(s.description) = lower('test')) order by s.name";
        String expectedCount = "select count(1) from Entity s " +
                "where (lower(s.name) = lower('test') or lower(s.description) = lower('test'))";
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
    public void buildWithSelectFromAndFilter() {
        // GIVEN
        HqlCriteriaQueryBuilder query = new HqlCriteriaQueryBuilder(Arrays.asList("name", "description"));
        query.setSelectPart("select s");
        query.setFromPart("from Entity s");
        String expected = "select s from Entity s " +
                "where (lower(s.name) = lower(:name) and s.subEntity.id = :subentityId " +
                "and lower(s.name1) like '%' || lower(:name1) || '%')";
        String expectedCount = "select count(1) from Entity s " +
                "where (lower(s.name) = lower(:name) and s.subEntity.id = :subentityId " +
                "and lower(s.name1) like '%' || lower(:name1) || '%')";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setFilters(Arrays.asList(
                new RequestFilter("name", FieldType.STRING, "=", "MaX"),
                new RequestFilter("subEntity.id", FieldType.INTEGER, "=", "1"),
                new RequestFilter("name1", FieldType.STRING, "~", "")));

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
        HqlCriteriaQueryBuilder query = new HqlCriteriaQueryBuilder(Arrays.asList("name", "description"));
        query.setSelectPart("select s");
        query.setFromPart("from Entity s");
        query.setWherePart("where s.id > 1");
        String expected = "select s from Entity s " +
                "where s.id > 1 and (lower(s.name) = lower(:name) and s.subEntity.id = :subentityId " +
                "and lower(s.name1) like '%' || lower(:name1) || '%')";
        String expectedCount = "select count(1) from Entity s " +
                "where s.id > 1 and (lower(s.name) = lower(:name) and s.subEntity.id = :subentityId " +
                "and lower(s.name1) like '%' || lower(:name1) || '%')";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setFilters(Arrays.asList(
                new RequestFilter("name", FieldType.STRING, "=", "MaX"),
                new RequestFilter("subEntity.id", FieldType.INTEGER, "=", "1"),
                new RequestFilter("name1", FieldType.STRING, "~", "")));

        // WHEN
        ParamsQuery build = query.build(requestCriteria);

        // THEN
        Assertions.assertNotNull(build);
        Assertions.assertEquals(expected, build.getQueryString());
        Assertions.assertEquals(expectedCount, build.getCountQueryString());
    }

    @Test
    public void buildWithSelectFromWhereAndFilterAndSearch() {
        // GIVEN
        HqlCriteriaQueryBuilder query = new HqlCriteriaQueryBuilder(Arrays.asList("name", "description"));
        query.setSelectPart("select s");
        query.setFromPart("from Entity s");
        query.setWherePart("where s.id > 1");
        String expected = "select s from Entity s " +
                "where s.id > 1 and " +
                "(lower(s.name) like '%' || lower('test') || '%' or lower(s.description) like '%' || lower('test') || '%') " +
                "and (lower(s.name) = lower(:name) and s.subEntity.id = :subentityId " +
                "and lower(s.name1) like '%' || lower(:name1) || '%')";
        String expectedCount = "select count(1) from Entity s " +
                "where s.id > 1 and " +
                "(lower(s.name) like '%' || lower('test') || '%' or lower(s.description) like '%' || lower('test') || '%') " +
                "and (lower(s.name) = lower(:name) and s.subEntity.id = :subentityId " +
                "and lower(s.name1) like '%' || lower(:name1) || '%')";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setFilters(Arrays.asList(
                new RequestFilter("name", FieldType.STRING, "=", "MaX"),
                new RequestFilter("subEntity.id", FieldType.INTEGER, "=", "1"),
                new RequestFilter("name1", FieldType.STRING, "~", "")));
        requestCriteria.setSearch(new RequestSearch("test", false));

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
        HqlCriteriaQueryBuilder query = new HqlCriteriaQueryBuilder(Arrays.asList("name", "description"));
        query.setSelectPart("select s");
        query.setFromPart("from Entity s");
        query.setOrderPart("order by s.name");
        String expected = "select s from Entity s " +
                "where (lower(s.name) like '%' || lower('test') || '%' or lower(s.description) like '%' || lower('test') || '%') order by s.name";
        String expectedCount = "select count(1) from Entity s " +
                "where (lower(s.name) like '%' || lower('test') || '%' or lower(s.description) like '%' || lower('test') || '%')";
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
