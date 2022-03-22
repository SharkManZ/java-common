package ru.shark.home.common.dao.repository.query;

import java.util.Arrays;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Класс содержит состояние обработки текста hql запроса.
 * Передаваемый запрос разбивается на части через " " с удалением переносов строк.
 */
public class QueryParsingState {

    /**
     * Номер текущей части запроса.
     */
    private int currentIdx;

    /**
     * Номер части до которой текст запроса уже обработан.
     */
    private int lastCopyIdx;

    /**
     * Текущий уровень вложенности скобок.
     */
    private int bracketLevel;

    /**
     * Массив частей запроса.
     */
    private String[] parts;

    /**
     * Текущий установленный тип части запроса.
     */
    private QueryPartType currentPartType;

    /**
     * Состояние инициализирцется из переданного текста запроса.
     *
     * @param source текст запроса
     */
    public QueryParsingState(String source) {
        currentIdx = 0;
        lastCopyIdx = 0;
        bracketLevel = 0;
        parts = source.replaceAll("\\n", "").split(" ");
        currentPartType = QueryPartType.SELECT;
    }

    /**
     * Переход к следующей части запроса.
     */
    public void nextIdx() {
        currentIdx++;
    }

    /**
     * Возвращает признак нахождения на верхнем уровне запроса (не в скобках).
     */
    public boolean isTopLevel() {
        return bracketLevel == 0;
    }

    /**
     * Возвращает наличие следующей части.
     */
    public boolean hasNext() {
        return currentIdx < parts.length;
    }

    /**
     * Возвращает наличие следующей через 1 части.
     */
    public boolean hasMore() {
        return currentIdx + 1 < parts.length;
    }

    /**
     * Возвращает текущую часть.
     */
    public String getPart() {
        return parts[currentIdx];
    }

    /**
     * Содержит ли текущая часть запроса скобки.
     */
    public boolean isBracket() {
        return getPart().contains("(") || getPart().contains(")");
    }

    /**
     * Возвращает тип текущей части запроса.
     */
    public QueryPartType getCurrentPartType() {
        return currentPartType;
    }

    /**
     * Проверяет является ли текущая часть началом указанного типа части запроса.
     * Критерии:
     * 1. Верхний уровень запроса;
     * 2. Текущая часть соответствует переданному типу;
     * 3. Тип текущей части совместим с типом проверяемой.
     */
    public boolean isPartStarted(QueryPartType type) {
        return isTopLevel() && type.name().equalsIgnoreCase(getPart()) && type.compatibleWith(currentPartType.name());
    }

    /**
     * Объединяет и возвращает все части запроса с последнего места обработки по предыдущую часть.
     *
     * @return результат объединения
     */
    public String getPreviousPart() {
        return String.join(" ", Arrays.copyOfRange(parts, lastCopyIdx, currentIdx));
    }

    /**
     * Изменение текущего типа части запроса.
     * Объединяет и возвращает все части запроса с последнего места обработки по предыдущую часть.
     * Изменяет номер последней обработанной части на текущий, а также изменяет тип текущей части на переданный.
     *
     * @param type тип который надо сделать текущим
     * @return объединенная предыдущая часть.
     */
    public String changeCurrentPart(QueryPartType type) {
        return changeCurrentPart(type, null);
    }

    /**
     * Изменение текущего типа части запроса и изменение номера текущей части на следующий за переданной частью.
     * Объединяет и возвращает все части запроса с последнего места обработки по предыдущую часть.
     * Изменяет номер последней обработанной части на текущий, а также изменяет тип текущей части на переданный.
     *
     * @param type     тип который надо сделать текущим
     * @param nextPart значение части, которую нужно найти после текущей
     * @return объединенная предыдущая часть.
     */
    public String changeCurrentPart(QueryPartType type, String nextPart) {
        String previousPart = getPreviousPart();
        lastCopyIdx = currentIdx;
        currentPartType = type;
        if (!isBlank(nextPart)) {
            this.currentIdx = findNextPartIdx(nextPart);
        }
        return previousPart;
    }

    /**
     * Возвращает позицию части с указанным значением, следующую за текущей, если между ними только пробелы.
     * В противном случае возвращает -1.
     *
     * @return номер найденной части.
     */
    public int findNextPartIdx(String findPart) {
        boolean onlySpaces = true;
        for (int i = currentIdx + 1; i < parts.length; i++) {
            if (findPart.equalsIgnoreCase(parts[i])) {
                return onlySpaces ? i : -1;
            } else if (!isBlank(parts[i])) {
                onlySpaces = false;
            }
        }
        return -1;
    }

    /**
     * Обработка скобок содержащихся в текущей части.
     */
    public void processBracket() {
        for (char let : getPart().toCharArray()) {
            if ('(' == let) {
                bracketLevel++;
            } else if (')' == let) {
                bracketLevel--;
            }
        }
    }
}
