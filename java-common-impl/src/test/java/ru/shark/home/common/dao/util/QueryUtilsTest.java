package ru.shark.home.common.dao.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class QueryUtilsTest {

    @Test
    public void getFilterName() {
        // GIVEN
        String field1 = "field";
        String field2 = "some.field1";
        String field3 = "some.fieldId";

        // WHEN
        String filterName1 = QueryUtils.getFilterName(field1);
        String filterName2 = QueryUtils.getFilterName(field2);
        String filterName3 = QueryUtils.getFilterName(field3);

        // THEN
        Assertions.assertEquals("field", filterName1);
        Assertions.assertEquals("someField1", filterName2);
        Assertions.assertEquals("someFieldId", filterName3);
    }
}
