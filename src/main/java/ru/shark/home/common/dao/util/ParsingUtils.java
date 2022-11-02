package ru.shark.home.common.dao.util;

public class ParsingUtils {
    public static int processBrackets(int bracketLevel, String source) {
        int currentLevel = bracketLevel;
        for (char let : source.toCharArray()) {
            if ('(' == let) {
                currentLevel++;
            } else if (')' == let) {
                currentLevel--;
            }
        }
        return currentLevel;
    }

    public static boolean processQuotas(boolean isQuotaOpened, String source) {
        boolean localIsQuotaOpened = isQuotaOpened;
        for (char let : source.toCharArray()) {
            if ('\'' == let) {
                localIsQuotaOpened = !localIsQuotaOpened;
            }
        }
        return localIsQuotaOpened;
    }

    public static boolean containsBrackets(String source) {
        return source.contains("(") || source.contains(")");
    }
}
