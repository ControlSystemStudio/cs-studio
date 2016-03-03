package org.csstudio.perspectives;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.eclipse.emf.common.util.URI;

public interface IFileUtils {

    public URI urlToEmfUri(URL url);

    public URI fileToEmfUri(File file);

    public File urlToFile(URL url);

    public File stringPathToFile(String path) throws MalformedURLException, IOException, URISyntaxException;

    public String stringPathToUriFileString(String path);

    public void createDirectory(File directory) throws IOException;

    public List<File> listDirectory(File directory, String fileExtension);

    public File promptForFile(File startingDirectory, String xmiExtension);

}
