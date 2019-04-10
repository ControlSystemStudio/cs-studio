package org.csstudio.phoebus.integration.actions;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.phoebus.integration.PhoebusLauncherService;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorLauncher;

public class OPIEditor implements IEditorLauncher {

    private static final Logger logger = Logger.getLogger(OPIEditor.class.getName());

    static final String ID = "org.csstudio.phoebus.integration.actions.OPIEditor";
    static final String PHOEBUS_APP = "display_editor";

    @Override
    public void open(IPath eclipseFile) {
        File file = eclipseFile.toFile();
        URI fileURI = file.toURI();
        try {
            URI phoebusResource = new URI(fileURI.getScheme(), fileURI.getAuthority(), fileURI.getPath(),
                    "app=" + PHOEBUS_APP, fileURI.getFragment());
            PhoebusLauncherService.launchResource(phoebusResource.toString());
        } catch (URISyntaxException e) {
            logger.log(Level.WARNING, "Failed to open file with Phoebus", e);
            PhoebusLauncherService.launchResource(fileURI.toString());
        }
    }

}
