package ru.shark.home.common.dao.repository.query.parts;

import ru.shark.home.common.dao.util.ParsingUtils;

import javax.validation.ValidationException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SqlFromQueryPart extends FromQueryPart {
    public SqlFromQueryPart(String value) {
        super(value);
        // TODO теоретически делалось для работы с колонками из нескольких таблиц. Не нужно если обернуть в подзапрос.
//        String[] parts = value.replaceAll("[\\s]{2,}", " ").trim().split(" ");
//        int idx = 0;
//        int bracketLvl = 0;
//        int subQueryStartIdx = 0;
//        while (idx < parts.length) {
//            String part = parts[idx];
//            bracketLvl += ParsingUtils.processBrackets(part);
//            if (bracketLvl == 0) {
//                if (part.equalsIgnoreCase("from") && !ParsingUtils.containsBrackets(parts[idx + 1])) {
//                    addTableWithAlias(parts[idx + 1], parts.length > idx + 2 ? parts[idx + 2] : null);
//                } else if (part.equalsIgnoreCase("join") && !parts[idx + 1].contains("(")) {
//                    addTableWithAlias(parts[idx + 1], parts[idx + 2]);
//                } else if (part.contains(")") && subQueryStartIdx != 0 && idx < parts.length + 1) {
//                    addTableAliasWithColumns(parts[idx + 1],
//                            getColumnsFromSubQuery(Arrays.copyOfRange(parts, subQueryStartIdx, idx + 1)));
//                    subQueryStartIdx = 0;
//                }
//            } else if (bracketLvl == 1 && subQueryStartIdx == 0) {
//                subQueryStartIdx = idx;
//            }
//
//            idx++;
//        }
    }

    private Set<String> getColumnsFromSubQuery(String[] parts) {
        int idx = 0;
        int bracketLvl = 0;
        parts[0] = parts[0].replace("(", "");
        parts[parts.length - 1] = parts[parts.length - 1].replace(")", "");
        String selectPart = getSelectPart(parts);
        String[] commaParts = selectPart.split(",");
        Set<String> columns = new HashSet<>();
        while (idx < commaParts.length) {
            String part = commaParts[idx].trim();
            bracketLvl += ParsingUtils.processBrackets(part);
            if (bracketLvl == 0) {
                String[] spaceParts = part.split(" ");
                // простое поле вида t1.id
                columns.add(getColumnName(spaceParts[spaceParts.length-1]));
            }
            idx++;
        }
        return columns;
    }

    private String getColumnName(String source) {
        String[] dotParts = source.split("\\.");
        return dotParts[dotParts.length-1];
    }

    private String getSelectPart(String[] parts) {
        int idx = 0;
        int bracketLvl = 0;
        while (idx < parts.length) {
            String part = parts[idx];
            bracketLvl += ParsingUtils.processBrackets(part);
            if (bracketLvl == 0) {
                if (part.equalsIgnoreCase("from")) {
                    return String.join(" ", Arrays.copyOfRange(parts, 1, idx));
                }

            }
            idx++;
        }
        throw new ValidationException("Не удалось определить перечень колонок из переданного запроса");
    }
}
