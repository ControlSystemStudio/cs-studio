package org.csstudio.diirt.util;

import java.net.URL;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    public static final String ID = "org.csstudio.diirt.util";

    private static final Logger log = Logger.getLogger(ID);

    @Override
    public void start(BundleContext context) throws Exception {
        
        final Location instanceLoc = Platform.getInstanceLocation();
        final String defaultDiirtConfig = new URL(instanceLoc.getURL(), "diirt").toString();
        
        log.info("Starting diirt.util");        
        IPreferencesService prefs = Platform.getPreferencesService();
        String diirtHome;
        try {
            diirtHome = prefs.getString(ID, "diirt.home", defaultDiirtConfig, null);
            log.config("Setting Diirt configuration folder to :" + diirtHome);
            System.setProperty("diirt.home", diirtHome);
        } catch (Exception e) {
            log.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {

    }

}
