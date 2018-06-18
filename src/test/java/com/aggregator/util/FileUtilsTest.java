package com.aggregator.util;

import com.aggregator.utils.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FileUtilsTest {

    @Test
    public void testStripExtension(){
        assertEquals("file", FileUtils.stripExtension("file.xml"));
    }

    @Test
    public void testStripExtensionIfNull(){
        assertNull(FileUtils.stripExtension(null));
    }

    @Test
    public void testStripExtensionIfNoExtention(){
        assertEquals("file",FileUtils.stripExtension("file"));
    }

    @Test
    public void testGetExtension() throws IOException {
        File file = File.createTempFile("temp", ".csv");
        assertEquals("csv", FileUtils.getExtension(file));
        file.delete();
    }

    @Test
    public void testGetExtensionIfLongPath() throws IOException {
        File file = File.createTempFile("temp", "folder2.folder1.csv");
        assertEquals("csv", FileUtils.getExtension(file));
        file.delete();
    }
}
