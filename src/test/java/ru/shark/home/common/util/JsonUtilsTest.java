package ru.shark.home.common.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.shark.home.common.services.LogicTestDto;

public class JsonUtilsTest {

    @Test
    public void getJsonFromObject() {
        // GIVEN
        LogicTestDto dto = new LogicTestDto();
        dto.setMode("Mode");

        // WHEN
        String jsonFromObject = JsonUtils.getJsonFromObject(dto);

        // THEN
        Assertions.assertNotNull(jsonFromObject);
    }
}
