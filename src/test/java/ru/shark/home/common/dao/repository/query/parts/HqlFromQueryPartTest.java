package ru.shark.home.common.dao.repository.query.parts;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HqlFromQueryPartTest {
    @Test
    public void constructorWithoutAlias() {
        // GIVEN
        String fromStr = " from  \n" +
                " SomeEntity";
        // WHEN
        HqlFromQueryPart part = new HqlFromQueryPart(fromStr);

        // THEN
        Assertions.assertNull(part.getMainTableAlias());
    }

    @Test
    public void constructorWithAlias() {
        // GIVEN
        String fromStr = " from  \n" +
                " SomeEntity s ";
        // WHEN
        HqlFromQueryPart part = new HqlFromQueryPart(fromStr);

        // THEN
        Assertions.assertEquals("s", part.getMainTableAlias());
    }

    @Test
    public void constructorWithJoins() {
        // GIVEN
        String fromStr = " from  \n" +
                " SomeEntity s join s.parent p";
        // WHEN
        HqlFromQueryPart part = new HqlFromQueryPart(fromStr);

        // THEN
        Assertions.assertEquals("s", part.getMainTableAlias());
    }

    @Test
    public void transformFieldChain() {
        // GIVEN
        String fromStr = " from \n" +
                " SomeEntity s join s.parent p join s.other o join p.topParent tp ";
        HqlFromQueryPart queryPart = new HqlFromQueryPart(fromStr);

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
        HqlFromQueryPart queryPart = new HqlFromQueryPart(fromStr);

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
        HqlFromQueryPart queryPart = new HqlFromQueryPart(fromStr);

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
        HqlFromQueryPart queryPart = new HqlFromQueryPart(fromStr);

        // WHEN
        String fieldChain = queryPart.transformFieldChain("parent.topParent.name");

        // THEN
        Assertions.assertEquals("p.topParent.name", fieldChain);
    }
}
