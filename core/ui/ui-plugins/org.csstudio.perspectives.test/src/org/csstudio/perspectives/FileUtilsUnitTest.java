package org.csstudio.perspectives;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

public class FileUtilsUnitTest {

    private FileUtils fileUtils;
    private URL fileUrl;
    private URL httpUrl;
    private String fileString = "/tmp/hello.txt";

    @Before
    public void setUp() throws Exception {
        fileUtils = new FileUtils();
        fileUrl = new URL("file:" + fileString);
        httpUrl = new URL("http://www.google.com/maps");
    }

    @Test(expected=NullPointerException.class)
    public void checkUrlToEmfUriThrowsNullPointerExceptionIfNullArgument() {
        fileUtils.urlToEmfUri(null);
    }

    @Test
    public void checkUrlToEmfUriHandlesSimpleFile() throws MalformedURLException {
        URI uri = fileUtils.urlToEmfUri(fileUrl);
        assertEquals(uri.scheme(), "file");
        assertArrayEquals(uri.segments(), new String[] {"tmp", "hello.txt"});
        assertEquals(uri.fileExtension(), "txt");
    }

    @Test
    public void checkUrlToEmfUriHandlesHttpUrl() throws MalformedURLException {
        URI uri = fileUtils.urlToEmfUri(httpUrl);
        assertEquals(uri.authority(), "www.google.com");
        assertArrayEquals(uri.segments(), new String[] {"maps"});
    }

    @Test(expected=NullPointerException.class)
    public void checkFileToEmfUriThrowsNullPointerExceptionIfArgumentIsNull() {
        fileUtils.fileToEmfUri(null);
    }

    @Test(expected=NullPointerException.class)
    public void checkUrlToFileThrowsNullPointerExceptionIfNullArgument() {
        fileUtils.urlToFile(null);
    }

    @Test
    public void checkUrlToFileHandlesSimpleFile() {
        File file = fileUtils.urlToFile(fileUrl);
        assertEquals(file.getAbsolutePath(), fileString);
    }

    @Test(expected=IllegalArgumentException.class)
    public void urlToFileThrowsIllegalArgumentExceptionForHttpUrl() {
        System.out.println(fileUtils.urlToFile(httpUrl).getAbsolutePath());
    }

    @Test(expected=NullPointerException.class)
    public void checkStringPathToFileThrowsNullPointerExceptionIfArgumentIsNull() throws IOException, URISyntaxException {
        fileUtils.stringPathToFile(null);
    }

    @Test(expected=NullPointerException.class)
    public void checkStringPathToUriFileStringThrowsNullPointerExceptionIfArgumentIsNull() throws IOException, URISyntaxException {
        fileUtils.stringPathToUriFileString(null);
    }

    @Test
    public void checkStringPathToUriFileStringPrependsFile() throws IOException, URISyntaxException {
        String uri = fileUtils.stringPathToUriFileString(fileString);
        assertTrue(uri.startsWith("file"));
        assertTrue(uri.endsWith(fileString));
    }

    @Test
    public void checkStringPathToUriFileStringMakesAbsoluteFromRelativePath() throws IOException, URISyntaxException {
        String relativePath = "directory/file.txt";
        String uri = fileUtils.stringPathToUriFileString(relativePath);
        assertTrue(uri.startsWith("file"));
        assertTrue(uri.endsWith(relativePath));
        assertTrue(uri.contains(System.getProperty("user.dir")));
    }
}
