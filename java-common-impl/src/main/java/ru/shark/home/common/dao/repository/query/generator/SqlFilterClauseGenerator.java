package ru.shark.home.common.dao.repository.query.generator;

import ru.shark.home.common.dao.common.RequestFilter;
import ru.shark.home.common.enums.FieldType;
import org.springframework.stereotype.Component;
import ru.shark.home.common.enums.FilterOperation;

import java.text.MessageFormat;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;
import static ru.shark.home.common.common.ErrorConstants.UNKNOWN_FILTER_OPERATION;
import static ru.shark.home.common.common.ErrorConstants.UNSUPPORTED_FILTER_OPERATION;
import static ru.shark.home.common.dao.repository.query.generator.GeneratorConstants.FILTER_BETWEEN_TPL;
import static ru.shark.home.common.dao.repository.query.generator.GeneratorConstants.FILTER_GT_TPL;
import static ru.shark.home.common.dao.repository.query.generator.GeneratorConstants.FILTER_IN_TPL;
import static ru.shark.home.common.dao.repository.query.generator.GeneratorConstants.FILTER_LT_TPL;
import static ru.shark.home.common.dao.repository.query.generator.GeneratorConstants.FILTER_NUMBER_EQ_TPL;
import static ru.shark.home.common.dao.repository.query.generator.GeneratorConstants.FILTER_NUMBER_NE_TPL;
import static ru.shark.home.common.dao.repository.query.generator.GeneratorConstants.FILTER_STRING_EQ_TPL;
import static ru.shark.home.common.dao.repository.query.generator.GeneratorConstants.FILTER_STRING_LIKE_TPL;
import static ru.shark.home.common.dao.repository.query.generator.GeneratorConstants.FILTER_STRING_NE_TPL;
import static ru.shark.home.common.dao.util.QueryUtils.getFilterName;

@Component
public class SqlFilterClauseGenerator implements ClauseGenerator {
    @Override
    public String generate(QueryClauseRequest request) {
        if (isEmpty(request.getRequestCriteria().getFilters())) {
            return null;
        }

        return "(" + request.getRequestCriteria().getFilters().stream().map(this::filterToClause).collect(Collectors.joining(" and ")) + ")";
    }

    /**
     * Преобразует переданный фильтр в строку условия.
     *
     * @param filter фильтр для преобразования
     * @return строка условия
     */
    private String filterToClause(RequestFilter filter) {
        switch (filter.getOperation()) {
            case EQ:
                return getEqClause(filter);
            case LIKE:
                return getLikeClause(filter);
            case NE:
                return getNeClause(filter);
            case IN:
                return getClause(FILTER_IN_TPL, filter.getField());
            case BETWEEN:
                return getBetweenClause(filter);
            case LT:
                return getLtClause(filter);
            case GT:
                return getGtClause(filter);
            default:
                throw new UnsupportedOperationException(MessageFormat.format(UNKNOWN_FILTER_OPERATION, filter.getOperation().name()));
        }
    }

    /**
     * Преобразует фильтр в условие строгого соответствия.
     *
     * @param filter фильтр для преобразования
     * @return строка условия
     */
    protected String getEqClause(RequestFilter filter) {
        switch (filter.getFieldType()) {
            case STRING:
            case ENUM:
                return MessageFormat.format(FILTER_STRING_EQ_TPL, filter.getField(), getFilterName(filter.getField()));
            case INTEGER:
            case DATE:
            case BOOL:
                return MessageFormat.format(FILTER_NUMBER_EQ_TPL, filter.getField(), getFilterName(filter.getField()));
            default:
                throw new UnsupportedOperationException(MessageFormat.format(UNSUPPORTED_FILTER_OPERATION, filter.getFieldType().name(),
                        FilterOperation.EQ.name()));
        }
    }

    /**
     * Преобразует фильтр для условия НЕ строгого соответствия. Поддерживает строковые поля.
     *
     * @param filter фильтр для преобразования
     * @return строка условия
     */
    protected String getLikeClause(RequestFilter filter) {
        if (filter.getFieldType() == FieldType.STRING) {
            return MessageFormat.format(FILTER_STRING_LIKE_TPL, filter.getField(), getFilterName(filter.getField()));
        }
        throw new UnsupportedOperationException(MessageFormat.format(UNSUPPORTED_FILTER_OPERATION, filter.getFieldType().name(),
                FilterOperation.LIKE.name()));
    }

    /**
     * Преобразует фильтр в условие неравенства.
     *
     * @param filter фильтр для преобразования
     * @return строка условия
     */
    protected String getNeClause(RequestFilter filter) {
        switch (filter.getFieldType()) {
            case STRING:
            case ENUM:
                return getClause(FILTER_STRING_NE_TPL, filter.getField());
            case INTEGER:
            case BOOL:
            case DATE:
                return getClause(FILTER_NUMBER_NE_TPL, filter.getField());
            default:
                throw new UnsupportedOperationException(MessageFormat.format(UNSUPPORTED_FILTER_OPERATION, filter.getFieldType().name(),
                        FilterOperation.NE.name()));
        }
    }

    /**
     * Преобразует фильтр в условия "между".
     * Отличается от других необходимостью сформировать 2 параметра запроса с суффиксами _left и _right.
     *
     * @param filter фильтр для преобразования
     * @return строка условия
     */
    protected String getBetweenClause(RequestFilter filter) {
        switch (filter.getFieldType()) {
            case INTEGER:
            case DATE:
                return getClause(FILTER_BETWEEN_TPL, filter.getField());
            default:
                throw new UnsupportedOperationException(MessageFormat.format(UNSUPPORTED_FILTER_OPERATION, filter.getFieldType().name(),
                        FilterOperation.BETWEEN.name()));
        }
    }

    /**
     * Преобразует фильтр в условия "меньше чем".
     *
     * @param filter фильтр для преобразования
     * @return строка условия
     */
    protected String getLtClause(RequestFilter filter) {
        switch (filter.getFieldType()) {
            case INTEGER:
            case DATE:
                return getClause(FILTER_LT_TPL, filter.getField());
            default:
                throw new UnsupportedOperationException(MessageFormat.format(UNSUPPORTED_FILTER_OPERATION, filter.getFieldType().name(),
                        FilterOperation.LT.name()));
        }
    }

    /**
     * Преобразует фильтр в условие "больше чем".
     *
     * @param filter фильтр для преобразования
     * @return строка условия
     */
    protected String getGtClause(RequestFilter filter) {
        switch (filter.getFieldType()) {
            case INTEGER:
            case DATE:
                return getClause(FILTER_GT_TPL, filter.getField());
            default:
                throw new UnsupportedOperationException(MessageFormat.format(UNSUPPORTED_FILTER_OPERATION, filter.getFieldType().name(),
                        FilterOperation.GT.name()));
        }
    }

    private String getClause(String template, String field) {
        return MessageFormat.format(template, field);
    }

    @Override
    public boolean canHandle(QueryClauseType type, boolean isNative) {
        return QueryClauseType.FILTER.equals(type) && isNative;
    }
}
