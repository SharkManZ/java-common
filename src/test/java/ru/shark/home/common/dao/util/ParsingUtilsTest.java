package ru.shark.home.common.dao.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParsingUtilsTest {

    @Test
    public void processBrackets() {
        // GIVEN
        String strSource = "some text ( second (close1) more text";

        // WHEN
        int result = ParsingUtils.processBrackets(strSource);

        // THEN
        Assertions.assertEquals(1, result);
    }

    @Test
    public void isInQuotas() {
        // GIVEN
        String sourceStrIn ="' some ''";
        String sourceStrNot ="' some '";

        // WHEN
        boolean resultIn = ParsingUtils.isInQuotas(false, sourceStrIn);
        boolean resultNot = ParsingUtils.isInQuotas(false, sourceStrNot);
        boolean resultAlreadyIn = ParsingUtils.isInQuotas(true, sourceStrIn);
        boolean resultAlreadyInNot = ParsingUtils.isInQuotas(true, sourceStrNot);

        // THEN
        Assertions.assertTrue(resultIn);
        Assertions.assertFalse(resultNot);

        Assertions.assertFalse(resultAlreadyIn);
        Assertions.assertTrue(resultAlreadyInNot);
    }
}
