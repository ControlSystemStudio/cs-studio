package org.csstudio.perspectives;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.osgi.util.NLS;

@SuppressWarnings("restriction")  // This class uses internal e4 API.
public class PerspectiveLoader {

    @Inject
    @Preference(nodePath = "org.eclipse.ui.workbench")
    private IEclipsePreferences preferences;

    @Inject
    private IPerspectiveUtils perspectiveUtils;

    @Inject
    private IFileUtils fileUtils;

    /**
     * Present a FileDialog to the user to select a .xmi file.  Load file into XML
     * string.  Put into preferences; this triggers the new perspective import mechanism
     * in Eclipse 4.5.2 which imports the perspective properly.
     */
    public void promptAndLoadPerspective() {
        Path selectedFile = promptForXmiFile();
        if (selectedFile != null && Files.isRegularFile(selectedFile)) {
            URI fileUri = fileUtils.pathToEmfUri(selectedFile);
            loadPerspective(fileUri);
        }
    }

    public void loadPerspective(URI fileUri) {
        ResourceSet rs = new ResourceSetImpl();
        Resource res = rs.getResource(fileUri, true);
        EObject obj = res.getContents().get(0);
        if (obj instanceof MPerspective) {
            MPerspective p = (MPerspective) obj;
            try {
                String perspAsString = perspectiveUtils.perspToString(p);
                // The new perspective import and export mechanism will intercept
                // this preference change and import the perspective for us.
                preferences.put(p.getLabel() + Plugin.PERSPECTIVE_SUFFIX, perspAsString);
                Plugin.getLogger().log(Level.INFO, NLS.bind(Messages.PerspectiveLoader_loadedPerspective, p.getLabel(), fileUri.toString()));
            } catch (IOException e) {
                Plugin.getLogger().log(Level.WARNING, Messages.PerspectiveLoader_loadFailed, e);
            }
        } else {
            Plugin.getLogger().warning(NLS.bind(Messages.PerspectiveLoader_fileNotUnderstood, fileUri));
        }
    }

    private Path promptForXmiFile() {
        return fileUtils.promptForFile(null, Plugin.XMI_EXTENSION);
    }

    public void loadFromDirectory(Path directory) throws IOException {
        List<Path> xmiFiles = fileUtils.listDirectory(directory, Plugin.XMI_EXTENSION);
        for (Path xmiFile : xmiFiles) {
            loadPerspective(URI.createFileURI(xmiFile.toString()));
        }
    }

}
