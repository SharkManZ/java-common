package ru.shark.home.common.dao.specification;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.shark.home.common.dao.common.RequestCriteria;
import ru.shark.home.common.dao.common.RequestFilter;
import ru.shark.home.common.enums.FieldType;
import ru.shark.home.common.enums.FilterOperation;
import ru.shark.home.common.services.LogicTestEntity;
import ru.shark.home.common.services.TestEnum;

import javax.persistence.criteria.Root;
import java.text.MessageFormat;
import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.shark.home.common.common.ErrorConstants.INVALID_NUMBER_FILTER_VALUE;
import static ru.shark.home.common.common.ErrorConstants.UNKNOWN_FILTER_FIELD;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SpecificationRequestTest {
    private SpecificationRequest specificationRequest;
    private RequestCriteria requestCriteria;

    @BeforeAll
    public void init() {
        requestCriteria = new RequestCriteria(0, 10);
        requestCriteria.setFilters(Arrays.asList(new RequestFilter("name", FieldType.STRING,
                        FilterOperation.EQ.getValue(), "Bow"),
                new RequestFilter("pAtk", FieldType.INTEGER,
                        FilterOperation.EQ.getValue(), "123"),
                new RequestFilter("type", FieldType.ENUM,
                        FilterOperation.EQ.getValue(), "BOW"),
                new RequestFilter("innerEntity.id", FieldType.INTEGER,
                        FilterOperation.EQ.getValue(), "1")));
        specificationRequest = new SpecificationRequest(requestCriteria);
    }

    @Test
    public void getEnumValue() {
        // GIVEN
        Root root = mock(Root.class);
        when(root.getJavaType()).thenReturn(LogicTestEntity.class);

        // WHEN
        TestEnum enumValue = (TestEnum) specificationRequest.getEnumValue("testEnum", root, "V1");

        // THEN
        Assertions.assertNotNull(enumValue);
        Assertions.assertEquals(TestEnum.V1, enumValue);
    }

    @Test
    public void getEnumValueWithUnknownField() {
        // GIVEN
        Root root = mock(Root.class);
        when(root.getJavaType()).thenReturn(LogicTestEntity.class);
        String expectedException = MessageFormat.format(UNKNOWN_FILTER_FIELD, "field");

        // WHEN
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                specificationRequest.getEnumValue("field", root, "V1"));

        // THEN
        Assertions.assertEquals(expectedException, exception.getMessage());
    }

    @Test
    public void getValueWithString() {
        // GIVEN
        RequestFilter requestFilter = new RequestFilter("name", FieldType.STRING,
                FilterOperation.EQ.getValue(), "Bow");
        Root root = mock(Root.class);

        // WHEN
        Object value = specificationRequest.getValue(requestFilter, root);

        // THEN
        Assertions.assertNotNull(value);
        Assertions.assertEquals(value, "Bow");
    }

    @Test
    public void getValueWithInteger() {
        // GIVEN
        RequestFilter requestFilter = new RequestFilter("pAtk", FieldType.INTEGER,
                FilterOperation.EQ.getValue(), "123");
        Root root = mock(Root.class);

        // WHEN
        Object value = specificationRequest.getValue(requestFilter, root);

        // THEN
        Assertions.assertNotNull(value);
        Assertions.assertEquals(value, 123L);
    }

    @Test
    public void getValueWithIntegerInvalid() {
        // GIVEN
        RequestFilter requestFilter = new RequestFilter("pAtk", FieldType.INTEGER,
                FilterOperation.EQ.getValue(), "1aaa23");
        Root root = mock(Root.class);
        String expectedException = MessageFormat.format(INVALID_NUMBER_FILTER_VALUE, requestFilter.getValue());

        // WHEN
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> specificationRequest.getValue(requestFilter, root));

        // THEN
        Assertions.assertEquals(expectedException, exception.getMessage());
    }

    @Test
    public void getValueWithEnum() {
        // GIVEN
        RequestFilter requestFilter = new RequestFilter("testEnum", FieldType.ENUM,
                FilterOperation.EQ.getValue(), "V1");
        Root root = mock(Root.class);
        when(root.getJavaType()).thenReturn(LogicTestEntity.class);

        // WHEN
        Object value = specificationRequest.getValue(requestFilter, root);

        // THEN
        Assertions.assertNotNull(value);
        Assertions.assertEquals(value, TestEnum.V1);
    }
}
