package org.csstudio.diirt.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    public static final String ID = "org.csstudio.diirt.util";

    private static final String PLATFORM_URI_PREFIX = "platform:";

    private static final Logger log = Logger.getLogger(ID);

    @Override
    public void start(BundleContext context) throws Exception {
        log.info("Starting diirt.util");
        setDiirtHome();
    }

    @Override
    public void stop(BundleContext context) throws Exception {

    }

    public void setDiirtHome(){
        try {
            final Location instanceLoc = Platform.getInstanceLocation();
            final String defaultDiirtConfig = new URL(instanceLoc.getURL(),"diirt").toURI().getPath();

            IPreferencesService prefs = Platform.getPreferencesService();
            String diirtHome = getSubstitutedPath(prefs.getString("org.csstudio.diirt.util.preferences", "diirt.home", defaultDiirtConfig, null));
            log.config("Setting Diirt configuration folder to :" + diirtHome);
            System.setProperty("diirt.home", diirtHome);
        } catch (Exception e) {
            log.severe(e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * handles the platform urls
     *
     * @param path
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    static String getSubstitutedPath(String path) throws MalformedURLException, IOException {
        if(path != null && !path.isEmpty()) {
            if(path.startsWith(PLATFORM_URI_PREFIX)) {
                return FileLocator.resolve(new URL(path)).getPath().toString();
            } else {
                return path;
            }
        } else {
            return "root";
        }
    }
}
