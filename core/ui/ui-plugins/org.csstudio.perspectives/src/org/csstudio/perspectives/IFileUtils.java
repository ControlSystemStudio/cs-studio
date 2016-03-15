package org.csstudio.perspectives;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.widgets.Shell;

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
    public URI pathToEmfUri(Path path);

    /**
     * Convert a file url to a Path object.
     * @param url with scheme 'file:'; may not be null
     * @return Path object
     */
    public Path urlToPath(URL url);

    /**
     * Convert a string representing a path into a path object.
     * This may include an Eclipse 'platform:' uri.
     * @param path may be an Eclipse-style URI; may not be null
     * @return object representing the path
     * @throws IOException if some IO error occurs
     */
    public Path stringPathToPath(String path) throws IOException;

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
    public void createDirectory(Path directory) throws IOException;

    /**
     * Return a list of paths matching the file extension in the specified
     * directory.
     * @param directory must represent a directory; may not be null
     * @param fileExtension to match
     * @return list of matching files
     * @throws IOException if some IO error occurs
     */
    public List<Path> listDirectory(Path directory, String fileExtension) throws IOException;

    /**
     * Open a file chooser to select a file with the specified extension.
     * @param startingDirectory directory for the chooser to start at
     * @param fileExtension
     * @param parent used for centreing the dialog
     * @return selected file
     */
    public Path promptForFile(Path startingDirectory, String fileExtension, Shell parent);

    /**
     * Return true if the string represents a valid path.  This may include an
     * Eclipse 'platform:' URI
     * @param path
     * @return true if the string represents a valid path
     * @throws IOException if some IO error occurs
     */
    public boolean isDirectory(String path) throws IOException;

}
