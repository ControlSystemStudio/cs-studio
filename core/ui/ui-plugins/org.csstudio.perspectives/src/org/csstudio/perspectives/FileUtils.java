package org.csstudio.perspectives;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

public class FileUtils implements IFileUtils {

    private static final String FILE_PROTOCOL = "file";

    @Override
    public URI urlToEmfUri(URL url) {
        return URI.createURI(url.toString());
    }

    @Override
    public URI fileToEmfUri(File file) {
        return URI.createFileURI(file.getPath());
    }

    @Override
    public File urlToFile(URL url) {
        if (!url.getProtocol().equals(FILE_PROTOCOL)) {
            throw new IllegalArgumentException("Only file URLs are supported.");
        }
        return new File(url.getFile());
    }

    @Override
    public File stringPathToFile(String path) throws IOException, URISyntaxException {
        try {
            URL directoryUrl = FileLocator.resolve(new URL(path));
            return new File(directoryUrl.toURI());
        } catch (MalformedURLException e) {
            return new File(path);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.perspectives.IFileUtils#stringPathToUriFileString(java.lang.String)
     */
    @Override
    public String stringPathToUriFileString(String path) {
        return Paths.get(path).toUri().toString();
    }

    @Override
    public void createDirectory(File directory) throws IOException {
        if (directory.isDirectory()) {
            Files.createDirectories(directory.toPath());
        }
    }

    @Override
    public List<File> listDirectory(File directory, String fileExtension) {
        List<File> contents = new ArrayList<File>();
        FilenameFilter filter = (File dir, String name) -> name.endsWith(fileExtension);
        String[] files = directory.list(filter);
        if (contents != null) {
            for (String f : files) {
                contents.add(new File(directory, f));
            }
        }
        return contents;
    }

    @Override
    public File promptForFile(File startingDirectory, String fileExtension) {
        FileDialog chooser = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
        chooser.setText(Messages.FileUtils_selectFile);
        chooser.setFilterExtensions(new String[] {"*." + fileExtension});
        chooser.open();
        File dirname = new File(chooser.getFilterPath());
        String filename = chooser.getFileName();
        File fullPath = null;
        if (filename != null) {
            fullPath = new File(dirname, chooser.getFileName());
        }
        return fullPath;
    }

}
