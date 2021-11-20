package ru.shark.home.common.services;

import org.springframework.util.ReflectionUtils;
import ru.shark.home.common.dao.common.RequestCriteria;
import ru.shark.home.common.dao.common.RequestFilter;
import ru.shark.home.common.dao.common.RequestSort;
import ru.shark.home.common.enums.FieldType;
import ru.shark.home.common.services.dto.PageRequest;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;
import static ru.shark.home.common.common.ErrorConstants.UNSUPPORTED_FILTER_FIELD_TYPE;

public class BaseLogicService {

    protected RequestCriteria getCriteria(PageRequest request, Class dtoClass) {
        RequestCriteria criteria = new RequestCriteria(request.getPage(), request.getSize());
        criteria.setSearch(request.getSearch());
        if (!isEmpty(request.getFilters())) {
            criteria.setFilters(request.getFilters().stream()
                    .map(item -> new RequestFilter(item.getField(),
                            getFieldType(dtoClass, item.getField()), item.getOperator(), item.getValue()))
                    .collect(Collectors.toList()));
        }

        if (!isEmpty(request.getSorts())) {
            criteria.setSorts(request.getSorts().stream()
                    .map(item -> new RequestSort(item.getField(), item.getDirection()))
                    .collect(Collectors.toList()));
        }

        return criteria;
    }

    protected FieldType getFieldType(Class dtoClass, String fieldName) {
        Field field = findField(dtoClass, fieldName);

        if (field.isEnumConstant() || field.getType().isEnum()) {
            return FieldType.ENUM;
        }

        switch (field.getType().getSimpleName().toUpperCase()) {
            case "STRING":
                return FieldType.STRING;
            case "INTEGER":
            case "LONG":
                return FieldType.INTEGER;
            default:
                throw new IllegalArgumentException(MessageFormat.format(UNSUPPORTED_FILTER_FIELD_TYPE, field.getType().getSimpleName()));
        }
    }

    protected Field findField(Class<?> clazz, String fieldName) {
        if (clazz == null) {
            return null;
        }
        Field field = null;
        String[] split = fieldName.split("\\.");
        for (String element: split) {
            field = ReflectionUtils.findField(clazz, element);
            clazz = Optional.ofNullable(field).map(Field::getType).orElse(null);
        }

        return field;
    }
}
