package org.csstudio.perspectives;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

/**
 * Default implementation of IFileUtils.
 */
public class FileUtils implements IFileUtils {

    private static final String FILE_PROTOCOL = "file";

    @Override
    public URI urlToEmfUri(URL url) {
        return URI.createURI(url.toString());
    }

    @Override
    public URI pathToEmfUri(Path file) {
        return URI.createFileURI(file.toString());
    }

    @Override
    public Path urlToPath(URL url) {
        if (!url.getProtocol().equals(FILE_PROTOCOL)) {
            throw new IllegalArgumentException("Only file URLs are supported.");
        }
        return Paths.get(url.getFile());
    }

    @Override
    public Path stringPathToPath(String path) throws IOException {
        try {
            URL directoryUrl = FileLocator.resolve(new URL(path));
            return Paths.get(directoryUrl.toURI());
        } catch (MalformedURLException | URISyntaxException e) {
            return Paths.get(path);
        }
    }

    @Override
    public String stringPathToUriFileString(String path) {
        return Paths.get(path).toUri().toString();
    }

    @Override
    public void createDirectory(Path directory) throws IOException {
        if (Files.isDirectory(directory)) {
            Files.createDirectories(directory);
        }
    }

    // Borrowed from https://docs.oracle.com/javase/7/docs/api/java/nio/file/DirectoryStream.html
    @Override
    public List<Path> listDirectory(Path directory, String fileExtension) throws IOException {
        List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*." + fileExtension)) {
            for (Path entry: stream) {
                result.add(entry);
            }
        } catch (DirectoryIteratorException ex) {
            // I/O error encounted during the iteration, the cause is an IOException
            throw ex.getCause();
        }
        return result;
    }

    @Override
    public Path promptForFile(Path startingDirectory, String fileExtension) {
        FileDialog chooser = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
        chooser.setText(Messages.FileUtils_selectFile);
        chooser.setFilterExtensions(new String[] {"*." + fileExtension});
        chooser.open();
        Path dirname = Paths.get(chooser.getFilterPath());
        String filename = chooser.getFileName();
        Path fullPath = null;
        if (filename != null) {
            fullPath = dirname.resolve(chooser.getFileName());
        }
        return fullPath;
    }

}
