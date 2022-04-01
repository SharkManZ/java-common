package ru.shark.home.common.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.shark.home.common.dao.common.RequestCriteria;
import ru.shark.home.common.dao.common.RequestFilter;
import ru.shark.home.common.dao.common.RequestSort;
import ru.shark.home.common.enums.FieldType;
import ru.shark.home.common.enums.FilterOperation;
import ru.shark.home.common.services.dto.Filter;
import ru.shark.home.common.services.dto.PageRequest;
import ru.shark.home.common.services.dto.Search;
import ru.shark.home.common.services.dto.Sort;
import ru.shark.home.common.util.BaseServiceTest;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.util.ObjectUtils.isEmpty;

public class BaseLogicServiceTest extends BaseServiceTest {
    private BaseLogicService baseLogicService;

    @BeforeAll
    public void init() {
        baseLogicService = new BaseLogicService();
    }

    @Test
    public void getCriteria() {
        // GIVEN
        PageRequest request = new PageRequest(0, 10);
        request.setFilters(Arrays.asList(new Filter("name", FilterOperation.EQ.getValue(), "A")));
        request.setSearch(new Search("str"));
        request.setSorts(Arrays.asList(new Sort("name", "ASC"),
                new Sort("mode", "DESC"),
                new Sort("type", null)));

        // WHEN
        RequestCriteria criteria = baseLogicService.getCriteria(request, LogicTestEntity.class);

        // THEN
        assertNotNull(criteria);
        assertEquals(request.getPage(), criteria.getPage());
        assertEquals(request.getSize(), criteria.getSize());
        assertEquals(request.getSearch().getValue(), criteria.getSearch().getValue());
        assertFalse(isEmpty(criteria.getFilters()));
        assertEquals(request.getFilters().size(), criteria.getFilters().size());
        Filter expectedFilter = request.getFilters().get(0);
        RequestFilter actualFilter = criteria.getFilters().get(0);
        assertEquals(expectedFilter.getField(), actualFilter.getField());
        assertEquals(expectedFilter.getOperator(), actualFilter.getOperation().getValue());
        assertEquals(expectedFilter.getValue(), actualFilter.getValue());
        assertFalse(isEmpty(criteria.getSorts()));
        assertEquals(request.getSorts().size(), criteria.getSorts().size());
        int idx = 0;
        for (Sort sort : request.getSorts()) {
            checkSort(sort, criteria.getSorts().get(idx));
            idx++;
        }
    }

    private void checkSort(Sort expected, RequestSort actual) {
        assertEquals(expected.getField(), actual.getField());
        if (isBlank(expected.getDirection())) {
            Assertions.assertNull(actual.getDirection());
        } else {
            assertEquals(expected.getDirection(), actual.getDirection().name());
        }
    }

    @Test
    public void getFieldType() {
        // GIVEN
        String stringField = "name";
        String intField = "intField";
        String longField = "longField";
        String enumField = "testEnum";

        // WHEN
        FieldType stringType = baseLogicService.getFieldType(LogicTestEntity.class, stringField);
        FieldType intType = baseLogicService.getFieldType(LogicTestEntity.class, intField);
        FieldType longType = baseLogicService.getFieldType(LogicTestEntity.class, longField);
        FieldType enumType = baseLogicService.getFieldType(LogicTestEntity.class, enumField);

        // THEN
        assertEquals(FieldType.STRING, stringType);
        assertEquals(FieldType.INTEGER, intType);
        assertEquals(FieldType.INTEGER, longType);
        assertEquals(FieldType.ENUM, enumType);
    }

    @Test
    public void findField() {
        // WHEN
        Field field = baseLogicService.findField(LogicTestDto.class, "innerDto.id");

        // THEN
        Assertions.assertNotNull(field);
        Assertions.assertEquals("id", field.getName());
    }

    @Test
    public void findFieldWithAnnotation() {
        // WHEN
        Field field = baseLogicService.findField(LogicTestInnerDto.class, "name");

        // THEN
        Assertions.assertNotNull(field);
        Assertions.assertEquals("name", field.getName());
    }
}
