package ru.shark.home.common.dao.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ReflectionUtils;
import ru.shark.home.common.dao.common.RequestCriteria;
import ru.shark.home.common.dao.common.RequestFilter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;
import static ru.shark.home.common.common.ErrorConstants.*;

public class SpecificationRequest implements Specification {
    private List<RequestFilter> filters;

    public SpecificationRequest(RequestCriteria requestCriteria) {
        this.filters = requestCriteria.getFilters();
    }

    @Override
    public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {

        if (isEmpty(filters)) {
            return null;
        }

        List<Predicate> predicates = filters.stream()
                .map(item -> buildPredicate(item, root, criteriaBuilder))
                .collect(Collectors.toList());

        return predicates.size() > 1 ? criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])) :
                predicates.get(0);
    }

    private Predicate buildPredicate(RequestFilter filter, Root root, CriteriaBuilder criteriaBuilder) {
        switch (filter.getOperation()) {
            case EQ:
                return buildEqualsPredicate(filter, root, criteriaBuilder);
            case LIKE:
                return buildLikePredicate(filter, root, criteriaBuilder);
            default:
                throw new IllegalArgumentException(MessageFormat.format(INVALID_FILTER_OPERATION,
                        filter.getOperation().getValue()));
        }
    }

    private Predicate buildEqualsPredicate(RequestFilter filter, Root root, CriteriaBuilder criteriaBuilder) {
        if (root.get(filter.getField()).getJavaType() == String.class) {
            return criteriaBuilder.equal(criteriaBuilder.lower(root.get(filter.getField())),
                    filter.getValue().toLowerCase());
        } else {
            return criteriaBuilder.equal(root.get(filter.getField()), getValue(filter, root));
        }
    }

    private Predicate buildLikePredicate(RequestFilter filter, Root root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.like(criteriaBuilder.lower(root.get(filter.getField())),
                "%" + filter.getValue().toLowerCase() + "%");
    }

    protected Object getValue(RequestFilter filter, Root root) {
        switch (filter.getFieldType()) {
            case STRING:
                return filter.getValue();
            case INTEGER:
                try {
                    return Long.parseLong(filter.getValue());
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException(MessageFormat.format(INVALID_NUMBER_FILTER_VALUE, filter.getValue()));
                }
            case ENUM:
                return getEnumValue(filter.getField(), root, filter.getValue());
            default:
                throw new IllegalArgumentException(MessageFormat.format(UNSUPPORTED_FILTER_FIELD_TYPE,
                        filter.getFieldType().name()));
        }
    }

    protected Object getEnumValue(String field, Root root, String source) {
        Class entityClass = root.getJavaType();
        Field filterField = ReflectionUtils.findField(entityClass, field);
        if (filterField == null) {
            throw new IllegalArgumentException(MessageFormat.format(UNKNOWN_FILTER_FIELD, field));
        }

        Class<? extends Enum> type = (Class<? extends Enum>) filterField.getType();
        return Enum.valueOf(type, source);
    }
}
