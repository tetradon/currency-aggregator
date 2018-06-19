package com.aggregator.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class FileUtils {

    private FileUtils() {
    }

    public static String stripExtension(final String str) {
        if (str == null) {
            return null;
        }
        int pos = str.lastIndexOf(".");
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    public static File findFileByName(final File folder,
                                      final String searchName) {
        File[] matchingFiles = folder
                .listFiles((dir, name) -> name.startsWith(searchName));
        return Objects.requireNonNull(matchingFiles)[0];
    }

    public static String getExtension(final File file) {
        String extension = "";

        int i = file.getPath().lastIndexOf('.');
        if (i > 0) {
            extension = file.getPath().substring(i + 1);
        }
        return extension;
    }

    public static void deleteContentOfFile(final File file) {
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
