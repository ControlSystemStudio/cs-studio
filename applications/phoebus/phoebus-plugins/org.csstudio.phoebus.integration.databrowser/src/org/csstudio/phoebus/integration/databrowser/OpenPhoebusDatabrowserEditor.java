package org.csstudio.phoebus.integration.databrowser;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.phoebus.integration.PhoebusLauncherService;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorLauncher;

/**
 * Action to open a plt file with the new databrowser editor.
 * @author kunal
 *
 */
public class OpenPhoebusDatabrowserEditor implements IEditorLauncher {

    private static final Logger logger = Logger.getLogger(OpenPhoebusDatabrowserEditor.class.getName());

    static final String ID = "org.csstudio.phoebus.integration.databrowser.OpenPhoebusDatabrowserEditor";
    static final String PHOEBUS_APP = "databrowser";

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
