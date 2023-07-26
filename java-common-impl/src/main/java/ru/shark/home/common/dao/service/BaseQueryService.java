package ru.shark.home.common.dao.service;

import ru.shark.home.common.common.Constants;
import ru.shark.home.common.common.ErrorConstants;
import ru.shark.home.common.dao.common.RequestFilter;
import ru.shark.home.common.dao.repository.query.ParsedQuery;
import ru.shark.home.common.dao.repository.query.QueryParser;
import ru.shark.home.common.dao.repository.query.QueryPartType;
import ru.shark.home.common.dao.repository.query.generator.QueryClauseGenerator;
import ru.shark.home.common.dao.repository.query.generator.QueryClauseRequest;
import ru.shark.home.common.dao.repository.query.generator.QueryClauseType;
import ru.shark.home.common.enums.FieldType;
import ru.shark.home.common.enums.FilterOperation;
import ru.shark.home.common.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.ObjectUtils.isEmpty;

public abstract class BaseQueryService {
    protected QueryClauseGenerator queryClauseGenerator;

    public ParsedQuery parseQuery(String query, List<String> searchFields, List<String> advancedSearchFields,
                                  Map<QueryPartType, BiConsumer<ParsedQuery, String>> partTypeConsumers) {
        ParsedQuery parsedQuery = new ParsedQuery(searchFields, advancedSearchFields);
        QueryParser state = new QueryParser(query);
        List<QueryPartType> parsingTypes = Stream.of(QueryPartType.values()).filter(item -> !QueryPartType.SELECT.equals(item)).collect(Collectors.toList());

        while (state.hasNext()) {
            state.nextPart();
            Optional<QueryPartType> starterPartType = parsingTypes.stream().filter(state::isPartStarted)
                    .findAny();
            starterPartType.ifPresent(type -> partTypeConsumers.get(state.getCurrentPartType()).accept(parsedQuery, state.changeCurrentPart(type)));
        }
        partTypeConsumers.get(state.getCurrentPartType()).accept(parsedQuery, state.getLastPart());
        return parsedQuery;
    }

    /**
     * Возвращает основную часть запроса (начиная с FROM и заканчивая GROUP BY) одинаковую для запроса данных и количества строк результата.
     * Добавляет к исходному запросу условия фильтрации и поиска, сгенерированные по переданным критериям.
     *
     * @param query   разобранный запрос
     * @param request параметры для генератора выражений поиска и фильтрации
     * @return собранная базовая часть запроса
     */
    protected String getBaseQuery(ParsedQuery query, QueryClauseRequest request, boolean isNative) {
        String searchClause = queryClauseGenerator.generate(QueryClauseType.SEARCH, isNative, request);
        String filterClause = queryClauseGenerator.generate(QueryClauseType.FILTER, isNative, request);

        StringBuilder sb = new StringBuilder()
                .append(query.getFromPart().getValue())
                .append(isBlank(query.getWherePart()) ? "" : " " + query.getWherePart().trim());
        if (!isBlank(searchClause) || !isBlank(filterClause)) {
            if (isBlank(query.getWherePart())) {
                sb.append(" where ");
            } else {
                sb.append(" and ");
            }

            if (!isBlank(searchClause)) {
                sb.append(searchClause);
            }
            if (!isBlank(filterClause)) {
                sb.append((!isBlank(searchClause) ? " and " : ""))
                        .append(filterClause);
            }
        }
        sb.append(isBlank(query.getGroupPart()) ? "" : " " + query.getGroupPart().trim());
        return sb.toString();
    }

    /**
     * Объединяет базовые параметры запроса с фильтрами, преобразуемыми в параметры.
     *
     * @param filters    фильтры
     * @param baseParams базовые параметры
     * @return итоговая карта параметров запроса
     */
    protected Map<String, Object> combineParams(List<RequestFilter> filters, Map<String, Object> baseParams) {
        if (isEmpty(filters) && isEmpty(baseParams)) {
            return Collections.emptyMap();
        }
        Map<String, Object> params = new HashMap<>();
        params.putAll(Optional.ofNullable(baseParams).orElse(Collections.emptyMap()));
        params.putAll(Optional.ofNullable(filters).orElse(Collections.emptyList())
                .stream()
                .filter(filter -> !FilterOperation.BETWEEN.equals(filter.getOperation()) && !isBlank(filter.getValue()))
                .collect(Collectors.toMap(filter -> "filter_" + filter.getField(), this::prepareFilterValue)));
        params.putAll(prepareBetweenFilterValues(Optional.ofNullable(filters).orElse(Collections.emptyList())
                .stream()
                .filter(filter -> FilterOperation.BETWEEN.equals(filter.getOperation()) && !isBlank(filter.getValue()))
                .collect(Collectors.toList())));

        return params;
    }

    /**
     * Приведение значение фильтра к нужному типу. Используется для фильтров с оператором отличным от BETWEEN.
     * Преобразует единичные и списочные значения.
     *
     * @param filter фильтр для обработки
     * @return значение приведенное к нужному типу
     */
    private Object prepareFilterValue(RequestFilter filter) {
        if (FilterOperation.IN.equals(filter.getOperation())) {
            return Stream.of(filter.getValue().split(Constants.FILTER_LIST_DELIMITER))
                    .map(value -> prepareSingleFilterValue(filter))
                    .collect(Collectors.toList());
        } else {
            return prepareSingleFilterValue(filter);
        }
    }

    /**
     * Формирование карты преобразованных параметров по переданным фильтрам.
     * Используется для фильтров с оператором BETWEEN.
     * Преобразует переданные фильтры и на каждый формирует 2 параметра с суффиксами _right и _left.
     *
     * @param filters фильтры для обработки
     * @return карта преобразованных параметров
     */
    private Map<String, Object> prepareBetweenFilterValues(List<RequestFilter> filters) {
        if (isEmpty(filters)) {
            return Collections.emptyMap();
        }
        Map<String, Object> params = new HashMap<>();
        for (RequestFilter filter : filters) {
            String[] values = filter.getValue().split(Constants.FILTER_LIST_DELIMITER);
            if (values.length != 2) {
                throw new IllegalArgumentException(ErrorConstants.FILTER_BETWEEN_MUST_CONTAIN_TWO_VALUES);
            }
            switch (filter.getFieldType()) {
                case INTEGER:
                    params.put("filter_" + filter.getField() + "_left", Long.parseLong(values[0]));
                    params.put("filter_" + filter.getField() + "_right", Long.parseLong(values[1]));
                    break;
                case DATE:
                    params.put("filter_" + filter.getField() + "_left", DateUtils.parseDate(values[0]));
                    params.put("filter_" + filter.getField() + "_right", DateUtils.parseDate(values[1]));
                    break;
                default:
                    throw new UnsupportedOperationException(MessageFormat.format(ErrorConstants.FILTER_BETWEEN_NOT_SUPPORTED_OPERATION,
                            filter.getFieldType().name()));
            }
        }
        return params;
    }

    /**
     * Приводит строковое значение для фильтра к нужному типу, в зависимости от типа поля.
     * Для перечислений предполагается хранение их в БД в виде строки.
     *
     * @param filter данные фильтра
     * @return приведенное значение
     */
    private Object prepareSingleFilterValue(RequestFilter filter) {
        switch (filter.getFieldType()) {
            case STRING:
            case ENUM:
                return filter.getValue();
            case INTEGER:
                return Long.parseLong(filter.getValue());
            case BOOL:
                return Boolean.parseBoolean(filter.getValue());
            case DATE:
                return DateUtils.parseDate(filter.getValue());
            default:
                throw new UnsupportedOperationException("Не поддерживаемый тип поля " + filter.getFieldType().name());
        }
    }

    @Autowired
    public void setQueryClauseGenerator(QueryClauseGenerator queryClauseGenerator) {
        this.queryClauseGenerator = queryClauseGenerator;
    }
}
