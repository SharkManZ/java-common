package ru.shark.home.common.common;

/**
 * Константы ошибок.
 */
public class ErrorConstants {
    /**
     * Коды ошибок.
     */
    public static final String ERR_500 = "500";

    public static final String INVALID_FILTER_OPERATION = "Не поддерживаемая операция фильтрации \"{0}\"";
    public static final String UNSUPPORTED_FILTER_FIELD_TYPE = "Тип поля \"{0}\" не поддерживается в фильтрах";
    public static final String INVALID_NUMBER_FILTER_VALUE = "Передано не верное числовое значение фильтра {0}";
    public static final String UNKNOWN_FILTER_FIELD = "Поле \"{0}\" отсутствует в сущности";

    public static final String EMPTY_ENTITY = "Не передана сущность \"{0}\"";
    public static final String ENTITY_ALREADY_EXISTS = "Сущность \"{0}\" с ключом \"{1}\" уже существует";
    public static final String ENTITY_NOT_FOUND_BY_ID = "Сущность \"{0}\" с идентификатором {1} не найдена";
    public static final String ENTITY_EMPTY_FIELD = "Поле {0} сущности {1} не задано";
    public static final String ENTITY_FIELD_VALUE_LOWER = "Значение поля \"{0}\" должно быть больше {1}";

    public static final String JSON_PROCESS_ERROR = "Ошибка преобразования объекта в JSON";
    public static final String OBJECTS_TO_ZIP_ERROR = "Ошибка записи списка объектов в zip";

    public static final String QUERY_CLAUSE_GENERATOR_NOT_FOUND = "Не найден генератор для типа {0}";
    public static final String WRONG_DATE_FORMAT = "Невозможно преобразовать к дате строку: %s";
    public static final String FILTER_BETWEEN_MUST_CONTAIN_TWO_VALUES = "Фильтр по принципу \"от\" и \"до\" должен иметь 2 значения через " +
            Constants.FILTER_LIST_DELIMITER;
    public static final String FILTER_BETWEEN_NOT_SUPPORTED_OPERATION = "Поле {0} нельзя отфильтровать по принципу \"от\" и \"до\"";
    public static final String UNSUPPORTED_FILTER_OPERATION = "Не поддерживаемый тип поля {0} для операции {1}}";
    public static final String UNKNOWN_FILTER_OPERATION = "Не поддерживаемый оператор фильтра {0}";
}
