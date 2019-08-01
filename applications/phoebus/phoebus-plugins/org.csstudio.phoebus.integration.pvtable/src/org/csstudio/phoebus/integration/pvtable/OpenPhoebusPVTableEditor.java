package org.csstudio.phoebus.integration.pvtable;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.phoebus.integration.PhoebusLauncherService;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorLauncher;

/**
 * Open .sav and .pvs files using the phoebus pvtable applications
 * @author kunal
 *
 */
public class OpenPhoebusPVTableEditor implements IEditorLauncher {

    private static final Logger logger = Logger.getLogger(OpenPhoebusPVTableEditor.class.getName());
    static final String ID = "org.csstudio.phoebus.integration.pvtable.PvTableEditor";

    @Override
    public void open(IPath eclipseFile) {
        File file = eclipseFile.toFile();
        URI fileURI = file.toURI();
        try {
            URI phoebusResource = new URI(fileURI.getScheme(), fileURI.getAuthority(), fileURI.getPath(),
                    "app=" + Messages.Pvtable, fileURI.getFragment());
            PhoebusLauncherService.launchResource(phoebusResource.toString());
        } catch (URISyntaxException e) {
            logger.log(Level.WARNING, "Failed to open file with Phoebus", e);
            PhoebusLauncherService.launchResource(fileURI.toString());
        }
    }

}
