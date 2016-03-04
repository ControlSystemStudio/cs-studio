package org.csstudio.perspectives;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.eclipse.emf.common.util.URI;

public interface IFileUtils {

    /**
     * Create an EMF URI from a URL.
     * @param url
     * @return uri
     */
    public URI urlToEmfUri(URL url);

    public URI fileToEmfUri(File file);

    /**
     * Convert a file url to a file object.
     * @param url file url
     * @return file object
     * @throws IllegalArgumentException if the URL has a protocol other than file
     */
    public File urlToFile(URL url);

    public File stringPathToFile(String path) throws IOException, URISyntaxException;

    /**
     * Convert a string representing a filesystem path into a string
     * representing a URI with scheme 'file:'.
     * A relative path will be converted into an absolute path.
     * @param path to convert
     * @return URI
     */
    public String stringPathToUriFileString(String path);

    public void createDirectory(File directory) throws IOException;

    public List<File> listDirectory(File directory, String fileExtension);

    public File promptForFile(File startingDirectory, String xmiExtension);

}
