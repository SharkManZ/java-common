package ru.shark.home.common.dao.util;

import ru.shark.home.common.services.LogicTestDto;
import ru.shark.home.common.services.LogicTestEntity;
import ru.shark.home.common.services.TestEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.util.ObjectUtils.isEmpty;

@SpringBootTest
public class ConverterUtilTest {

    @Autowired
    private ConverterUtil converterUtil;

    @Test
    public void entityToDto() {
        // GIVEN
        LogicTestEntity entity = prepareEntity(1L, "Bop");

        // WHEN
        LogicTestDto dto = converterUtil.entityToDto(entity, LogicTestDto.class);

        // THEN
        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getTestEnum(), dto.getTestEnum());
        assertEquals(entity.getIntField(), dto.getIntField());
    }

    @Test
    public void dtoToEntity() {
        // GIVEN
        LogicTestDto dto = prepareDto(1L, "Bop");

        // WHEN
        LogicTestEntity entity = converterUtil.dtoToEntity(dto, LogicTestEntity.class);

        // THEN
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getTestEnum(), entity.getTestEnum());
        assertEquals(dto.getIntField(), entity.getIntField());
    }

    @Test
    public void entityListToDtoList() {
        // GIVEN
        List<LogicTestEntity> list = Arrays.asList(prepareEntity(1L, "Bop"),
                prepareEntity(2L, "Emic"));

        // WHEN
        List<LogicTestDto> result = converterUtil.entityListToDtoList(list, LogicTestDto.class);

        // THEN
        Assertions.assertFalse(isEmpty(result));
        AtomicInteger idx = new AtomicInteger(0);
        for (LogicTestDto dto : result) {
            LogicTestEntity entity = list.get(idx.getAndIncrement());

            assertNotNull(dto);
            assertNotNull(entity);
            assertEquals(entity.getId(), dto.getId());
            assertEquals(entity.getName(), dto.getName());
            assertEquals(entity.getTestEnum(), dto.getTestEnum());
            assertEquals(entity.getIntField(), dto.getIntField());
        }
    }

    @Test
    public void dtoListToEntityList() {
        // GIVEN
        List<LogicTestDto> list = Arrays.asList(prepareDto(1L, "Bop"),
                prepareDto(2L, "Emic"));

        // WHEN
        List<LogicTestEntity> result = converterUtil.dtoListToEntityList(list, LogicTestEntity.class);

        // THEN
        Assertions.assertFalse(isEmpty(result));
        AtomicInteger idx = new AtomicInteger(0);
        for (LogicTestEntity entity : result) {
            LogicTestDto dto = list.get(idx.getAndIncrement());

            assertNotNull(entity);
            assertNotNull(dto);
            assertEquals(dto.getId(), entity.getId());
            assertEquals(dto.getName(), entity.getName());
            assertEquals(dto.getTestEnum(), entity.getTestEnum());
            assertEquals(dto.getIntField(), entity.getIntField());
        }
    }

    private LogicTestEntity prepareEntity(Long id, String name) {
        LogicTestEntity weaponEntity = new LogicTestEntity();
        weaponEntity.setId(id);
        weaponEntity.setName(name);
        weaponEntity.setTestEnum(TestEnum.V1);
        weaponEntity.setIntField(100);

        return weaponEntity;
    }

    private LogicTestDto prepareDto(Long id, String name) {
        LogicTestDto dto = new LogicTestDto();
        dto.setId(id);
        dto.setName(name);
        dto.setTestEnum(TestEnum.V1);
        dto.setIntField(100);

        return dto;
    }
}
