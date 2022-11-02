package ru.shark.home.common.dao.repository.query;

import org.hibernate.engine.query.spi.EntityGraphQueryHint;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory;
import org.hibernate.hql.spi.QueryTranslator;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.shark.home.common.QueryUtils.prepareTranslatorFactory;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HqlCriteriaQueryBuilderTest {
    private EntityManager em;

    @BeforeAll
    public void init() {
        em = mock(EntityManager.class);
        SessionImplementor sessionImplementor = mock(SessionImplementor.class);
        SessionFactoryImplementor sessionFactoryImplementor = mock(SessionFactoryImplementor.class);
        when(sessionImplementor.getFactory()).thenReturn(sessionFactoryImplementor);
        when(em.unwrap(eq(SessionImplementor.class))).thenReturn(sessionImplementor);
    }

    @Test
    public void buildWithSelectFrom() {
        // GIVEN
        HqlCriteriaQueryBuilder query = new HqlCriteriaQueryBuilder(em);
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
        HqlCriteriaQueryBuilder query = new HqlCriteriaQueryBuilder(em);
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
        ASTQueryTranslatorFactory factory = prepareTranslatorFactory("select s.* from table1 s where s.ud > 10 group by s.name");
        HqlCriteriaQueryBuilder query = new HqlCriteriaQueryBuilder(em);
        query.setQueryTranslatorFactory(factory);
        query.setSelectPart("select s");
        query.setFromPart("from Entity s");
        query.setWherePart("where s.id > 10");
        query.setGroupPart("group by s.name having count(s.id) > 2");
        String expected = "select s from Entity s where s.id > 10 group by s.name having count(s.id) > 2";
        String expectedCount = "select count(1) from (select s.* from table1 s where s.ud > 10 group by s.name) q";

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
        ASTQueryTranslatorFactory factory = prepareTranslatorFactory("select s.* from table1 s where s.ud > 10 group by s.name");
        HqlCriteriaQueryBuilder query = new HqlCriteriaQueryBuilder(em);
        query.setQueryTranslatorFactory(factory);
        query.setSelectPart("select s");
        query.setFromPart("from Entity s");
        query.setWherePart("where s.id > 10");
        query.setGroupPart("group by s.name having count(s.id) > 2");
        query.setOrderPart("order by s.name");
        String expected = "select s from Entity s where s.id > 10 group by s.name having count(s.id) > 2 order by s.name";
        String expectedCount = "select count(1) from (select s.* from table1 s where s.ud > 10 group by s.name) q";

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
        HqlCriteriaQueryBuilder query = new HqlCriteriaQueryBuilder(em);
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
        HqlCriteriaQueryBuilder query = new HqlCriteriaQueryBuilder(em, Arrays.asList("name", "description"));
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
    public void buildWithSelectFromAndSort() {
        // GIVEN
        HqlCriteriaQueryBuilder query = new HqlCriteriaQueryBuilder(em);
        query.setSelectPart("select s");
        query.setFromPart("from Entity s");
        String expected = "select s from Entity s " +
                "order by s.name asc, s.nameAsc asc, s.nameDesc desc";
        String expectedCount = "select count(1) from Entity s";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setSorts(Arrays.asList(
                new RequestSort("name", null),
                new RequestSort("nameAsc", "asc"),
                new RequestSort("nameDesc", "desc")));

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
        HqlCriteriaQueryBuilder query = new HqlCriteriaQueryBuilder(em, Arrays.asList("name", "description"));
        query.setSelectPart("select s");
        query.setFromPart("from Entity s");
        query.setOrderPart("order by s.id desc");
        String expected = "select s from Entity s " +
                "order by s.name asc, s.nameAsc asc, s.nameDesc desc";
        String expectedCount = "select count(1) from Entity s";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setSorts(Arrays.asList(
                new RequestSort("name", null),
                new RequestSort("nameAsc", "asc"),
                new RequestSort("nameDesc", "desc")));

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
        HqlCriteriaQueryBuilder query = new HqlCriteriaQueryBuilder(em, Arrays.asList("name", "description"));
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
    public void buildWithSelectFromAndFilterAndSearchAndOrderTransformed() {
        // GIVEN
        HqlCriteriaQueryBuilder query = new HqlCriteriaQueryBuilder(em, Arrays.asList("parent.name", "description"));
        query.setSelectPart("select s");
        query.setFromPart("from Entity s join s.parent p join p.subParent sp");
        String expected = "select s from Entity s join s.parent p join p.subParent sp " +
                "where (lower(p.name) = lower('test') or lower(s.description) = lower('test')) " +
                "and (lower(s.name) = lower(:name) and p.id = :parentId " +
                "and lower(sp.name) like '%' || lower(:parentSubparentName) || '%' " +
                "and lower(p.subParent2.name) like '%' || lower(:parentSubparent2Name) || '%') " +
                "order by s.description desc, p.name asc";
        String expectedCount = "select count(1) from Entity s join s.parent p join p.subParent sp " +
                "where (lower(p.name) = lower('test') or lower(s.description) = lower('test')) " +
                "and (lower(s.name) = lower(:name) and p.id = :parentId " +
                "and lower(sp.name) like '%' || lower(:parentSubparentName) || '%' " +
                "and lower(p.subParent2.name) like '%' || lower(:parentSubparent2Name) || '%')";
        RequestCriteria requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setFilters(Arrays.asList(
                new RequestFilter("name", FieldType.STRING, "=", "MaX"),
                new RequestFilter("parent.id", FieldType.INTEGER, "=", "1"),
                new RequestFilter("parent.subParent.name", FieldType.STRING, "~", ""),
                new RequestFilter("parent.subParent2.name", FieldType.STRING, "~", "")));
        requestCriteria.setSearch(new RequestSearch("test", true));
        requestCriteria.setSorts(Arrays.asList(new RequestSort("description", "DESC"),
                new RequestSort("parent.name", "ASC")));

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
        HqlCriteriaQueryBuilder query = new HqlCriteriaQueryBuilder(em, Arrays.asList("name", "description"));
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
        HqlCriteriaQueryBuilder query = new HqlCriteriaQueryBuilder(em, Arrays.asList("name", "description"));
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
        HqlCriteriaQueryBuilder query = new HqlCriteriaQueryBuilder(em, Arrays.asList("name", "description"));
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
