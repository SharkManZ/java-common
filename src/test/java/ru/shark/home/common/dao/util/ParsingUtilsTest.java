package ru.shark.home.common.dao.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParsingUtilsTest {

    @Test
    public void processBrackets() {
        // GIVEN
        int bracketLevel = 3;
        String strSource = "some text ( second (close1) more text";

        // WHEN
        int result = ParsingUtils.processBrackets(bracketLevel, strSource);

        // THEN
        Assertions.assertEquals(4, result);
    }
}
