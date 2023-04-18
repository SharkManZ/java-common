package ru.shark.home.common.dao.repository.query;

import ru.shark.home.common.dao.util.ParsingUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static ru.shark.home.common.dao.util.ParsingUtils.processBrackets;

/**
 * Класс содержит состояние обработки текста запроса.
 * Передаваемый запрос разбивается на части через " " с удалением переносов строк.
 */
public class QueryParser {

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
    public QueryParser(String source) {
        currentIdx = -1;
        lastCopyIdx = 0;
        bracketLevel = 0;
        parts = getParts(source);
        currentPartType = QueryPartType.SELECT;
    }

    /**
     * Переход к следующей части запроса.
     * Увеличение индекса части и процессинг скобок.
     */
    public void nextPart() {
        currentIdx++;
        if (hasNext()) {
            processBracket();
        }
    }

    /**
     * Возвращает признак нахождения на верхнем уровне запроса (не в скобках).
     */
    public boolean isTopLevel() {
        return bracketLevel == 0;
    }

    /**
     * Возвращает признак следующей части запроса.
     */
    public boolean hasNext() {
        return currentIdx + 1 != parts.length;
    }

    /**
     * Возвращает текущую часть.
     */
    public String getPart() {
        return parts[currentIdx];
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
     * Объединяет и возвращает все части запроса с последнего места обработки до последнего элемента.
     *
     * @return результат объединения
     */
    public String getLastPart() {
        return String.join(" ", Arrays.copyOfRange(parts, lastCopyIdx, parts.length));
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
        String previousPart = getPreviousPart();
        lastCopyIdx = currentIdx;
        currentPartType = type;
        return previousPart;
    }

    /**
     * Обработка скобок содержащихся в текущей части.
     */
    private void processBracket() {
        if (getPart().contains("(") || getPart().contains(")")) {
            bracketLevel += processBrackets(getPart());
        }
    }

    private String[] getParts(String source) {
        String[] parts = source.replaceAll("\\n", "").split(" ");
        boolean isQuotaOpened = false;
        int idx = 0;
        List<String> partList = new ArrayList<>();
        while (idx < parts.length) {
            String part = parts[idx];
            isQuotaOpened = ParsingUtils.isInQuotas(isQuotaOpened, part);
            if (!isQuotaOpened && isBlank(part)) {
                idx++;
                continue;
            }
            partList.add(part.trim());
            idx++;
        }
        return partList.stream().toArray(String[]::new);
    }
}
