package ru.shark.home.common.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import ru.shark.home.common.dao.common.PageableList;
import ru.shark.home.common.services.dto.response.BaseResponse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseServiceTest {
    protected void checkPagingResponse(BaseResponse response) {
        checkResponse(response);
        PageableList pageResponse = (PageableList) response.getBody();
        Assertions.assertNotNull(pageResponse.getData());
        Assertions.assertNotNull(pageResponse.getTotalCount());
    }

    protected void checkResponseWithBody(BaseResponse response) {
        checkResponse(response);
        Assertions.assertNotNull(response.getBody());
    }

    protected void checkResponse(BaseResponse response) {
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getError());
    }
}
