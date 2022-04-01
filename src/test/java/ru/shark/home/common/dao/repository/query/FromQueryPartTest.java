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
        Assertions.assertNull(part.getMainTableAlias());
    }

    @Test
    public void constructorWithAlias() {
        // GIVEN
        String fromStr = " from  \n" +
                " SomeEntity s ";
        // WHEN
        FromQueryPart part = new FromQueryPart(fromStr);

        // THEN
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
        Assertions.assertEquals("s", part.getMainTableAlias());
    }

    @Test
    public void transformFieldChain() {
        // GIVEN
        String fromStr = " from \n" +
                " SomeEntity s join s.parent p join s.other o join p.topParent tp ";
        FromQueryPart queryPart = new FromQueryPart(fromStr);

        // WHEN
        String fieldChain = queryPart.transformFieldChain("parent.topParent.name");

        // THEN
        Assertions.assertEquals("tp.name", fieldChain);
    }

    @Test
    public void transformFieldChainWithSimpleField() {
        // GIVEN
        String fromStr = " from \n" +
                " SomeEntity s join s.parent p join s.other o join p.topParent tp ";
        FromQueryPart queryPart = new FromQueryPart(fromStr);

        // WHEN
        String fieldChain = queryPart.transformFieldChain("name");

        // THEN
        Assertions.assertEquals("name", fieldChain);
    }

    @Test
    public void transformFieldChainWithoutJoining() {
        // GIVEN
        String fromStr = " from \n" +
                " SomeEntity s";
        FromQueryPart queryPart = new FromQueryPart(fromStr);

        // WHEN
        String fieldChain = queryPart.transformFieldChain("parent.topParent.name");

        // THEN
        Assertions.assertEquals("parent.topParent.name", fieldChain);
    }

    @Test
    public void transformFieldChainWithPartlyChain() {
        // GIVEN
        String fromStr = " from \n" +
                " SomeEntity s join s.parent p join s.other o";
        FromQueryPart queryPart = new FromQueryPart(fromStr);

        // WHEN
        String fieldChain = queryPart.transformFieldChain("parent.topParent.name");

        // THEN
        Assertions.assertEquals("p.topParent.name", fieldChain);
    }
}
