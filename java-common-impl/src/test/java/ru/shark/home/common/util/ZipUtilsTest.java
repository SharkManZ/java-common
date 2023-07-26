package ru.shark.home.common.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.shark.home.common.dto.FileDto;

import java.util.Arrays;
import java.util.List;

public class ZipUtilsTest {

    @Test
    public void objectsListToZip() {
        // GIVEN
        List<FileDto> files = Arrays.asList(new FileDto("file1", "data"),
                new FileDto("file2", "data"));

        // WHEN
        byte[] bytes = ZipUtils.objectsListToZip(files);

        // THEN
        Assertions.assertTrue(bytes.length > 0);
    }
}
