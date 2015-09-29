package org.csstudio.diirt.util;

import java.util.logging.Logger;

import org.diirt.datasource.DataSource;
import org.diirt.datasource.PVManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.prefs.Preferences;
import org.osgi.service.prefs.PreferencesService;

public class Activator implements BundleActivator {

    public static final String ID = "org.csstudio.diirt.util";

    private static final Logger log = Logger.getLogger(ID);

    @Override
    public void start(BundleContext context) throws Exception {
        log.config("Starting diirt.util");
        ServiceReference<PreferencesService> osgiPreferenceServiceRef = context.getServiceReference(PreferencesService.class);
        PreferencesService t = context.getService(osgiPreferenceServiceRef);
        log.config("Getting the osgi pref service : " + t.getUserPreferences(ID).get("diirt.home", "default"));
        
        Preferences preferences = InstanceScope.INSTANCE.getNode(ID);
        log.config("Getting the osgi preferefence : " + preferences.get("diirt.home", "default"));
        
        IPreferencesService prefs = Platform.getPreferencesService();
        log.config("Starting diirt.util ");
        
        String diirtHome;
        try {
            diirtHome = prefs.getString(ID, "diirt.home", "default", null);
            log.config("Setting diirt.home to pref:" + diirtHome);
            System.setProperty("diirt.home", diirtHome);
        } catch (Exception e) {
            log.severe(e.getMessage());
            e.printStackTrace();
        }
//        DataSource datasource = PVManager.getDefaultDataSource();
//        log.warning("ACTIVATOR: getting default source" + datasource.toString());
    }

    @Override
    public void stop(BundleContext context) throws Exception {

    }

}
