package org.csstudio.perspectives;

import java.io.File;
import java.io.IOException;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

@SuppressWarnings("restriction")  // This class uses internal e4 API.
public class PerspectiveLoader {

    @Inject
    @Preference(nodePath = "org.eclipse.ui.workbench")
    private IEclipsePreferences preferences;

    @Inject
    private IPerspectiveUtils perspectiveUtils;

    /**
     * Present a FileDialog to the user to select a .xmi file.  Load file into XML
     * string.  Put into preferences; this triggers the new perspective import mechanism
     * in Eclipse 4.5.2 which imports the perspective properly.
     */
    public void loadPerspectives() {
        File selectedFile = promptForXmiFile();
        if (selectedFile != null && selectedFile.isFile()) {
            ResourceSet rs = new ResourceSetImpl();
            URI uri = URI.createURI(Plugin.FILE_PREFIX + selectedFile.getPath());
            Resource res = rs.getResource(uri, true);
            EObject obj = res.getContents().get(0);
            if (obj instanceof MPerspective) {
                MPerspective p = (MPerspective) obj;
                try {
                    String perspAsString = perspectiveUtils.perspToString(p);
                    // The new perspective import and export mechanism will intercept
                    // this preference change and import the perspective for us.
                    preferences.put(p.getLabel() + Plugin.PERSPECTIVE_SUFFIX, perspAsString);
                } catch (IOException e) {
                    Plugin.getLogger().log(Level.WARNING, Messages.PerspectiveLoader_loadFailed, e);
                }
            } else {
                Plugin.getLogger().warning(NLS.bind(Messages.PerspectiveLoader_fileNotUnderstood, uri));
            }
        }
    }

    private File promptForXmiFile() {
        FileDialog chooser = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
        chooser.setText(Messages.PerspectiveLoader_selectFile);
        chooser.setFilterExtensions(new String[] {"*" + Plugin.XMI_EXTENSION});
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
