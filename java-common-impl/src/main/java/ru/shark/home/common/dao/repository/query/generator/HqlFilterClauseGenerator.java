package ru.shark.home.common.dao.repository.query.generator;

import ru.shark.home.common.dao.common.RequestFilter;
import org.springframework.stereotype.Component;
import ru.shark.home.common.dao.repository.query.parts.HqlFromQueryPart;
import ru.shark.home.common.enums.FieldType;
import ru.shark.home.common.enums.FilterOperation;

import java.text.MessageFormat;
import java.util.List;
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
public class HqlFilterClauseGenerator extends BaseHqlClauseGenerator implements ClauseGenerator {
    @Override
    public String generate(QueryClauseRequest request) {
        List<RequestFilter> filters = request.getRequestCriteria().getFilters();
        if (isEmpty(filters)) {
            return "";
        }

        return "(" + filters.stream().map(item -> filterToClause((HqlFromQueryPart) request.getParsedQuery().getFromPart(), item))
                .collect(Collectors.joining(" and ")) + ")";
    }

    /**
     * Преобразует переданный фильтр в строку условия.
     *
     * @param fromPart данные FROM части запроса
     * @param filter   фильтр для преобразования
     * @return строка условия
     */
    private String filterToClause(HqlFromQueryPart fromPart, RequestFilter filter) {
        switch (filter.getOperation()) {
            case EQ:
                return getEqClause(fromPart, filter);
            case LIKE:
                return getLikeClause(fromPart, filter);
            case NE:
                return getNeClause(fromPart, filter);
            case IN:
                return getClause(FILTER_IN_TPL, transformField(fromPart, filter.getField()), getFilterName(filter.getField()));
            case BETWEEN:
                return getBetweenClause(fromPart, filter);
            case LT:
                return getLtClause(fromPart, filter);
            case GT:
                return getGtClause(fromPart, filter);
            default:
                throw new UnsupportedOperationException(MessageFormat.format(UNKNOWN_FILTER_OPERATION, filter.getOperation().name()));
        }
    }

    /**
     * Преобразует фильтр в условие строгого соответствия.
     *
     * @param fromPart данные FROM части запроса
     * @param filter   фильтр для преобразования
     * @return строка условия
     */
    private String getEqClause(HqlFromQueryPart fromPart, RequestFilter filter) {
        switch (filter.getFieldType()) {
            case STRING:
            case ENUM:
                return getClause(FILTER_STRING_EQ_TPL, transformField(fromPart, filter.getField()), getFilterName(filter.getField()));
            case INTEGER:
            case DATE:
            case BOOL:
                return getClause(FILTER_NUMBER_EQ_TPL, transformField(fromPart, filter.getField()), getFilterName(filter.getField()));
            default:
                throw new UnsupportedOperationException(MessageFormat.format(UNSUPPORTED_FILTER_OPERATION, filter.getFieldType().name(),
                        FilterOperation.EQ.name()));
        }
    }

    /**
     * Преобразует фильтр для условия НЕ строгого соответствия. Поддерживает строковые поля.
     *
     * @param fromPart данные FROM части запроса
     * @param filter   фильтр для преобразования
     * @return строка условия
     */
    private String getLikeClause(HqlFromQueryPart fromPart, RequestFilter filter) {
        if (filter.getFieldType() == FieldType.STRING) {
            return MessageFormat.format(FILTER_STRING_LIKE_TPL, transformField(fromPart, filter.getField()), getFilterName(filter.getField()));
        }
        throw new UnsupportedOperationException(MessageFormat.format(UNSUPPORTED_FILTER_OPERATION, filter.getFieldType().name(),
                FilterOperation.LIKE.name()));
    }

    /**
     * Преобразует фильтр в условия "между".
     * Отличается от других необходимостью сформировать 2 параметра запроса с суффиксами _left и _right.
     *
     * @param fromPart данные FROM части запроса
     * @param filter   фильтр для преобразования
     * @return строка условия
     */
    protected String getBetweenClause(HqlFromQueryPart fromPart, RequestFilter filter) {
        switch (filter.getFieldType()) {
            case INTEGER:
            case DATE:
                return getClause(FILTER_BETWEEN_TPL, transformField(fromPart, filter.getField()), getFilterName(filter.getField()));
            default:
                throw new UnsupportedOperationException(MessageFormat.format(UNSUPPORTED_FILTER_OPERATION, filter.getFieldType().name(),
                        FilterOperation.BETWEEN.name()));
        }
    }

    /**
     * Преобразует фильтр в условие неравенства.
     *
     * @param fromPart данные FROM части запроса
     * @param filter   фильтр для преобразования
     * @return строка условия
     */
    protected String getNeClause(HqlFromQueryPart fromPart, RequestFilter filter) {
        switch (filter.getFieldType()) {
            case STRING:
            case ENUM:
                return getClause(FILTER_STRING_NE_TPL, transformField(fromPart, filter.getField()), getFilterName(filter.getField()));
            case INTEGER:
            case BOOL:
            case DATE:
                return getClause(FILTER_NUMBER_NE_TPL, transformField(fromPart, filter.getField()), getFilterName(filter.getField()));
            default:
                throw new UnsupportedOperationException(MessageFormat.format(UNSUPPORTED_FILTER_OPERATION, filter.getFieldType().name(),
                        FilterOperation.NE.name()));
        }
    }

    /**
     * Преобразует фильтр в условия "меньше чем".
     *
     * @param fromPart данные FROM части запроса
     * @param filter   фильтр для преобразования
     * @return строка условия
     */
    protected String getLtClause(HqlFromQueryPart fromPart, RequestFilter filter) {
        switch (filter.getFieldType()) {
            case INTEGER:
            case DATE:
                return getClause(FILTER_LT_TPL, transformField(fromPart, filter.getField()), getFilterName(filter.getField()));
            default:
                throw new UnsupportedOperationException(MessageFormat.format(UNSUPPORTED_FILTER_OPERATION, filter.getFieldType().name(),
                        FilterOperation.LT.name()));
        }
    }

    /**
     * Преобразует фильтр в условие "больше чем".
     *
     * @param fromPart данные FROM части запроса
     * @param filter   фильтр для преобразования
     * @return строка условия
     */
    protected String getGtClause(HqlFromQueryPart fromPart, RequestFilter filter) {
        switch (filter.getFieldType()) {
            case INTEGER:
            case DATE:
                return getClause(FILTER_GT_TPL, transformField(fromPart, filter.getField()), getFilterName(filter.getField()));
            default:
                throw new UnsupportedOperationException(MessageFormat.format(UNSUPPORTED_FILTER_OPERATION, filter.getFieldType().name(),
                        FilterOperation.GT.name()));
        }
    }

    private String getClause(String template, String field, String param) {
        return MessageFormat.format(template, field, param);
    }

    @Override
    public boolean canHandle(QueryClauseType type, boolean isNative) {
        return QueryClauseType.FILTER.equals(type) && !isNative;
    }
}
