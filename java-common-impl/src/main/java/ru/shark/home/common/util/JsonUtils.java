package ru.shark.home.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.shark.home.common.exception.CommonException;

import static ru.shark.home.common.common.ErrorConstants.JSON_PROCESS_ERROR;

public class JsonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String getJsonFromObject(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new CommonException(JSON_PROCESS_ERROR);
        }
    }
}
