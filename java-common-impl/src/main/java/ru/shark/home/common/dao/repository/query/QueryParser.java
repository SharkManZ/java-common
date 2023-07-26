package ru.shark.home.common.dao.repository.query;

import ru.shark.home.common.dao.util.ParsingUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Парсер текста запроса.
 * Разбирает запрос на элементы разделенные через " " с удалением переносов строк и повторяющихся пробелов (если они не внутри ковычек).
 * Содержит методы для обхода разобранного запроса и хранит состояния обхода.
 * Используется для получения из исходного текста запроса составных частей (SELECT..., FROM... и т.д.).
 */
public class QueryParser {

    /**
     * Номер текущего обрабатываемого элемента запроса.
     */
    private int currentIdx;

    /**
     * Номер элемента, до которой текст запроса уже обработан.
     */
    private int lastCopyIdx;

    /**
     * Текущий уровень вложенности скобок.
     */
    private int bracketLevel;

    /**
     * Элементы запроса.
     */
    private String[] parts;

    /**
     * Текущий тип части запроса.
     */
    private QueryPartType currentPartType;

    /**
     * Инициализация значений и разбор переданного текста запроса на элементы.
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
     * Переход к следующему элементу запроса.
     * Если элемент существует, проверяем скобки.
     */
    public void nextPart() {
        currentIdx++;
        if (hasNext()) {
            processBracket();
        }
    }

    /**
     * Возвращает признак нахождения на верхнем уровне запроса (не в скобках).
     *
     * @return признак верхнего уровня
     */
    public boolean isTopLevel() {
        return bracketLevel == 0;
    }

    /**
     * Возвращает наличие следующего элемента запроса.
     *
     * @return признак наличия
     */
    public boolean hasNext() {
        return currentIdx + 1 != parts.length;
    }

    /**
     * Возвращает текущий элемент.
     *
     * @return значение элемента
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
     * Возвращает часть текста запроса, состоящую из элементов начиная с последнего скопированного ранее до текущего.
     *
     * @return элементы запроса объединенные через " "
     */
    public String getPreviousPart() {
        return String.join(" ", Arrays.copyOfRange(parts, lastCopyIdx, currentIdx));
    }

    /**
     * Возврашает часть текста запроса, состоящую из элементов начиная с последнего скопированного ранее до последнего.
     *
     * @return элементы запроса объединенные через " "
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
     * Обработка скобок содержащихся в текущем элементе запроса.
     */
    private void processBracket() {
        if (getPart().contains("(") || getPart().contains(")")) {
            bracketLevel += ParsingUtils.processBrackets(getPart());
        }
    }

    /**
     * Возвращает массив элементов из переданного текста запроса.
     * Удаляем переносы строк и пробелы (кроме тех что в одиночных кавычках).
     *
     * @param source текст запроса
     * @return массив элементов
     */
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
