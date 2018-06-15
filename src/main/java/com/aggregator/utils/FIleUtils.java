package com.aggregator.utils;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Objects;

public class FIleUtils {
    public static String stripExtension(String str) {
        if (str == null) return null;
        int pos = str.lastIndexOf(".");
        if (pos == -1) return str;
        return str.substring(0, pos);
    }

    public static File findFileByName(ServletContext servletContext, String searchName) {
        File f = new File(servletContext.getRealPath("/WEB-INF/rates/"));
        File[] matchingFiles = f.listFiles((dir, name) -> name.startsWith(searchName));
        return Objects.requireNonNull(matchingFiles)[0];
    }

    public static String getExtension(File file) {
        String extension = "";

        int i = file.getPath().lastIndexOf('.');
        if (i > 0) {
            extension = file.getPath().substring(i + 1);
        }
        return extension;
    }

    public static void deleteContentOfFile(File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.print("");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
