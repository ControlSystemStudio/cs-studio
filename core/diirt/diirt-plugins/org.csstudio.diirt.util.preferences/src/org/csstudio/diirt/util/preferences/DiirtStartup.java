package org.csstudio.diirt.util.preferences;

import java.net.URL;
import java.util.logging.Logger;

import org.csstudio.utility.product.IWorkbenchWindowAdvisorExtPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;

/**
 * This class ensures that the diirt system.home is set from the eclipse
 * diirt.home preference before the diirt datasources/formulas/services are used.
 * 
 * @author Kunal Shroff
 *
 */
public class DiirtStartup implements IWorkbenchWindowAdvisorExtPoint {

    private Logger log = Logger.getLogger(DiirtStartup.ID);

    @Override
    public void preWindowOpen() {
        log.fine("DIIRT: preWindowOpen");
        try {
            final Location instanceLoc = Platform.getInstanceLocation();
            final String defaultDiirtConfig = new URL(instanceLoc.getURL(),"diirt").toURI().getPath();

            IPreferencesService prefs = Platform.getPreferencesService();
            String diirtHome = prefs.getString("org.csstudio.diirt.util.preferences", "diirt.home", defaultDiirtConfig, null);
            log.config("Setting Diirt configuration folder to :" + diirtHome);
            System.setProperty("diirt.home", diirtHome);
        } catch (Exception e) {
            log.severe(e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public boolean preWindowShellClose() {
        log.fine("DIIRT: preWindowShellClose");
        return true;
    }

    @Override
    public void postWindowRestore() throws WorkbenchException {
        log.fine("DIIRT: postWindowRestore");
    }

    @Override
    public void postWindowCreate() {
        log.fine("DIIRT: postWindowCreate");
    }

    @Override
    public void postWindowOpen() {
        log.fine("DIIRT: postWindowOpen");
    }

    @Override
    public void postWindowClose() {
        log.fine("DIIRT: postWindowClose");
    }

    @Override
    public IStatus saveState(IMemento memento) {
        return null;
    }

    @Override
    public IStatus restoreState(IMemento memento) {
        return null;
    }

}
