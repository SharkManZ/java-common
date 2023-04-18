package ru.shark.home.common.dao.util;

import org.apache.commons.lang3.StringUtils;

public class ParsingUtils {
    public static int processBrackets(String source) {
        return StringUtils.countMatches(source, '(') - StringUtils.countMatches(source, ')');
    }

    public static boolean isInQuotas(boolean currentIn, String source) {
        boolean inQuotas = StringUtils.countMatches(source, '\'') % 2 != 0;
       return (currentIn && !inQuotas) || (!currentIn && inQuotas);
    }

    public static boolean containsBrackets(String source) {
        return source.contains("(") || source.contains(")");
    }
}
