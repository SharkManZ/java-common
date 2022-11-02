package ru.shark.home.common.dao.repository.query.parts;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class SqlFromQueryPartTest {
    @Test
    public void constructor() {
        // GIVEN
        String fromStr = "from lego_user_sets lus\n" +
                "                        join lego_set_part lsp on lsp.lego_set_id  = lus.lego_set_id\n" +
                "                        join lego_part_color lpc on lpc.lego_id  = lsp.lego_part_color_id\n" +
                "                        left join (select lup.lego_part_color_id, lup.lego_count from lego_user_parts lup) up on up.lego_part_color_id = lpc.lego_id \n" +
                "                        join lego_part lp on lp.lego_id  = lpc.lego_part_id\n" +
                "                        join lego_part_category lpcc on lpcc.lego_id = lp.lego_part_category_id\n" +
                "                        join lego_color lc on lc.lego_id  = lpc.lego_color_id\n";
        // WHEN
        SqlFromQueryPart part = new SqlFromQueryPart(fromStr);

        // THEN
        Assertions.assertNotNull(part);
        Assertions.assertEquals("lus", part.getAliasByTable("lego_user_sets"));
        Assertions.assertEquals("lsp", part.getAliasByTable("lego_set_part"));
        Assertions.assertEquals("lpc", part.getAliasByTable("lego_part_color"));
        Assertions.assertEquals("lp", part.getAliasByTable("lego_part"));
        Assertions.assertEquals("lpcc", part.getAliasByTable("lego_part_category"));
        Assertions.assertEquals("lc", part.getAliasByTable("lego_color"));
    }

    @Test
    public void getColumnsFromSubQuery() {
        // GIVEN
        String querySimpleColumn = "from (select t.id, t.name from table t) a";
        String queryAliasColumn = "from (select t.id as id2, t.name from table t) a";
        String queryAliasFunctionColumn = "from (select t.id as id2, t.name, sum(t.val) as summ, " +
                "case when t.val when 1 then 0 else 1 end as val from table t) a";
        String queryAliasFunctionCommaColumn = "from (select t.id as id2, t.name, sum(coalesce(t.val,0)) as summ2 from table t) a";

        // WHEN
        SqlFromQueryPart part = new SqlFromQueryPart(querySimpleColumn);
        SqlFromQueryPart partAlias = new SqlFromQueryPart(queryAliasColumn);
        SqlFromQueryPart partAliasFunction = new SqlFromQueryPart(queryAliasFunctionColumn);
        SqlFromQueryPart partAliasFunctionComma = new SqlFromQueryPart(queryAliasFunctionCommaColumn);

        // THEN
        checkSubQueryPart(part, Sets.newHashSet("id", "name"));
        checkSubQueryPart(partAlias, Sets.newHashSet("id2", "name"));
        checkSubQueryPart(partAliasFunction, Sets.newHashSet("id2", "name", "summ", "val"));
        checkSubQueryPart(partAliasFunctionComma, Sets.newHashSet("id2", "name", "summ2"));
    }

    private void checkSubQueryPart(SqlFromQueryPart part, Set<String> columns) {
        Assertions.assertNotNull(part);
        for (String column : columns) {
            Assertions.assertEquals("a", part.getAliasByColumn(column));
        }
    }

    @Test
    public void constructorWithSubSelectSingleColumn() {
        // GIVEN
        String fromStr = "from (select lus.lego_id from lego_user_sets lus\n" +
                "                        join lego_set_part lsp on lsp.lego_set_id  = lus.lego_set_id\n" +
                "                        join lego_part_color lpc on lpc.lego_id  = lsp.lego_part_color_id\n" +
                "                        left join (select lup.lego_part_color_id, lup.lego_count from lego_user_parts lup) up on up.lego_part_color_id = lpc.lego_id \n" +
                "                        join lego_part lp on lp.lego_id  = lpc.lego_part_id\n" +
                "                        join lego_part_category lpcc on lpcc.lego_id = lp.lego_part_category_id\n" +
                "                        join lego_color lc on lc.lego_id  = lpc.lego_color_id) up\n";
        // WHEN
        SqlFromQueryPart part = new SqlFromQueryPart(fromStr);

        // THEN
        Assertions.assertNotNull(part);
        Assertions.assertEquals("up", part.getAliasByColumn("lego_id"));
    }
}
