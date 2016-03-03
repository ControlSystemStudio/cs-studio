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
    private URL simpleUrl;
    private String fileString = "/tmp/hello.txt";

    @Before
    public void setUp() throws Exception {
        fileUtils = new FileUtils();
        simpleUrl = new URL("file:" + fileString);
    }

    @Test(expected=NullPointerException.class)
    public void checkUrlToEmfUriThrowsNullPointerExceptionIfNullArgument() {
        fileUtils.urlToEmfUri(null);
    }

    @Test
    public void checkUrlToEmfUriHandlesSimpleFile() throws MalformedURLException {
        URI uri = fileUtils.urlToEmfUri(simpleUrl);
        assertEquals(uri.scheme(), "file");
        assertArrayEquals(uri.segments(), new String[] {"tmp", "hello.txt"});
        assertEquals(uri.fileExtension(), "txt");
    }

    @Test(expected=NullPointerException.class)
    public void checkUrlToFileThrowsNullPointerExceptionIfNullArgument() {
        fileUtils.urlToFile(null);
    }

    @Test
    public void checkUrlToFileHandlesSimpleFile() {
        File file = fileUtils.urlToFile(simpleUrl);
        assertEquals(file.getAbsolutePath(), fileString);
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
}
