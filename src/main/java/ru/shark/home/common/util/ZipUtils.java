package ru.shark.home.common.util;

import ru.shark.home.common.dto.FileDto;
import ru.shark.home.common.exception.CommonException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static ru.shark.home.common.common.ErrorConstants.OBJECTS_TO_ZIP_ERROR;

public class ZipUtils {
    private static final String ZIP_ORDER_FILE_NAME = "file_orders.txt";

    public static byte[] objectsListToZip(List<FileDto> files) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ZipOutputStream zip = new ZipOutputStream(bos)) {
            Iterator<FileDto> iterator = files.iterator();

            String fileNamesString = getFileNamesString(files);
            writeZipEntry(zip, ZIP_ORDER_FILE_NAME, fileNamesString.getBytes(StandardCharsets.UTF_8));
            while (iterator.hasNext()) {
                FileDto fileDto = iterator.next();
                byte[] fileData = JsonUtils.getJsonFromObject(fileDto).getBytes(StandardCharsets.UTF_8);
                writeZipEntry(zip, fileDto.getFileName(), fileData);
            }
            zip.finish();

            return bos.toByteArray();
        } catch (Exception e) {
            throw new CommonException(OBJECTS_TO_ZIP_ERROR);
        }
    }

    private static String getFileNamesString(List<FileDto> files) {
        return files.stream().map(file -> file.getFileName()).collect(Collectors.joining(System.lineSeparator()));
    }

    private static void writeZipEntry(ZipOutputStream zip, String fileName, byte[] content) throws IOException {
        ZipEntry zipEntry = new ZipEntry(fileName);
        zip.putNextEntry(zipEntry);
        zip.write(content, 0, content.length);
        zip.closeEntry();
    }
}
