package ru.shark.home.common.dao.repository.query;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FromQueryPartTest {
    @Test
    public void constructorWithoutAlias() {
        // GIVEN
        String fromStr = " from  \n" +
                " SomeEntity";
        // WHEN
        FromQueryPart part = new FromQueryPart(fromStr);

        // THEN
        Assertions.assertEquals("SomeEntity", part.getMainTable());
    }

    @Test
    public void constructorWithAlias() {
        // GIVEN
        String fromStr = " from  \n" +
                " SomeEntity s ";
        // WHEN
        FromQueryPart part = new FromQueryPart(fromStr);

        // THEN
        Assertions.assertEquals("SomeEntity", part.getMainTable());
        Assertions.assertEquals("s", part.getMainTableAlias());
    }
    @Test
    public void constructorWithJoins() {
        // GIVEN
        String fromStr = " from  \n" +
                " SomeEntity s join s.parent p";
        // WHEN
        FromQueryPart part = new FromQueryPart(fromStr);

        // THEN
        Assertions.assertEquals("SomeEntity", part.getMainTable());
        Assertions.assertEquals("s", part.getMainTableAlias());
    }

}
