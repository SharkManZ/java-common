package ru.shark.home.common.dao.util;

import org.apache.commons.text.CaseUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class QueryUtils {

    public static String getFilterName(String field) {
        return CaseUtils.toCamelCase(Arrays.stream(field.split("(?=\\p{Upper})")).collect(Collectors.joining(".")), false, '.');
    }
}
