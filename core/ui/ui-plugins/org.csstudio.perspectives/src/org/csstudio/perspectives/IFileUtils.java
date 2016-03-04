package org.csstudio.perspectives;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.eclipse.emf.common.util.URI;

/**
 * Miscellaneous utilities for handling files.
 */
public interface IFileUtils {

    /**
     * Create an EMF URI from a URL.
     * @param url; may not be null
     * @return uri
     */
    public URI urlToEmfUri(URL url);

    /**
     * Create an EMF URI from a file object
     * @param file; may not be null
     * @return uri
     */
    public URI fileToEmfUri(File file);

    /**
     * Convert a file url to a file object.
     * @param url with scheme 'file:'; may not be null
     * @return file object
     */
    public File urlToFile(URL url);

    /**
     * Convert a string representing a path into a file object.
     * This may include an Eclipse 'platform:' uri.
     * @param path may be an Eclipse-style URI; may not be null
     * @return object representing the path
     * @throws IOException if some IO error occurs
     */
    public File stringPathToFile(String path) throws IOException;

    /**
     * Convert a string representing a filesystem path into a string
     * representing a URI with scheme 'file:'.
     * A relative path will be converted into an absolute path.
     * @param path to convert
     * @return URI
     */
    public String stringPathToUriFileString(String path);

    /**
     * Create directory and any required subdirectories.
     * @param directory may not be null
     * @throws IOException if some IO error occurs
     */
    public void createDirectory(File directory) throws IOException;

    /**
     * Return a list of files matching the file extension in the specified
     * directory.
     * @param directory must represent a directory; may not be null
     * @param fileExtension to match
     * @return list of matching files
     */
    public List<File> listDirectory(File directory, String fileExtension);

    /**
     * Open a file chooser to select a file with the specified extension.
     * @param startingDirectory directory for the chooser to start at
     * @param fileExtension
     * @return selected file
     */
    public File promptForFile(File startingDirectory, String fileExtension);

}
