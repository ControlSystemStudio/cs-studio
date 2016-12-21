package org.csstudio.diirt.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.logging.Logger;

import org.csstudio.diirt.util.core.preferences.DIIRTPreferences;
import org.csstudio.utility.product.IWorkbenchWindowAdvisorExtPoint;
import org.diirt.datasource.CompositeDataSource;
import org.diirt.datasource.CompositeDataSourceConfiguration;
import org.diirt.datasource.DataSource;
import org.diirt.datasource.PVManager;
import org.diirt.util.config.Configuration;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
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

    private static final String PLATFORM_URI_PREFIX = "platform:";

    private Logger log = Logger.getLogger(DiirtStartup.ID);

    @Override
    public void preWindowOpen ( ) {

        log.config("DIIRT: preWindowOpen");

        try {

//            {
//
//                final Location instanceLoc = Platform.getInstanceLocation();
//                final String defaultDiirtConfig = new URL(instanceLoc.getURL(),"diirt").toURI().getPath();
//
//                IPreferencesService prefs = Platform.getPreferencesService();
//                String diirtHome = getSubstitutedPath(prefs.getString("org.csstudio.diirt.util.preferences", "diirt.home", defaultDiirtConfig, null));
//                log.config(MessageFormat.format("Setting 'diirt.home' system property [{0}].", diirtHome.toString()));
//
//            }

            File diirtHome = Files.createTempDirectory("DIIRT").toFile();

            DIIRTPreferences.get().toFiles(diirtHome);

            log.config(MessageFormat.format("Setting 'diirt.home' system property [{0}].", diirtHome.toString()));
            System.setProperty("diirt.home", diirtHome.toString());

            // Configuration.
            log.config("Resetting the configuration folder");
            Configuration.reset();

            DataSource defaultDataSource = PVManager.getDefaultDataSource();

            if ( defaultDataSource instanceof CompositeDataSource ) {

                CompositeDataSource ds = (CompositeDataSource) defaultDataSource;

                try ( InputStream input = Configuration.getFileAsStream("datasources" + "/datasources.xml", ds, "datasources.default.xml") ) {

                    CompositeDataSourceConfiguration conf = new CompositeDataSourceConfiguration(input);

                    ds.setConfiguration(conf);
                    PVManager.setDefaultDataSource(ds);

                } catch ( Exception e ) {
                    log.severe(e.getMessage());
                }

            }

        } catch ( Exception e ) {
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
