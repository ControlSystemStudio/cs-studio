/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.utility.screenshot;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ScreenshotPlugin extends AbstractUIPlugin // implements IStartup
{

    private static final Logger LOG = Logger.getLogger(ScreenshotPlugin.class.getCanonicalName());

    /** The plug-in ID */
    public static final String PLUGIN_ID = "org.csstudio.utility.screenshot";

    /** The shared instance */
    private static ScreenshotPlugin plugin;

    /** */
    private final String NAME = "Screenshot";

    /** */
    private final String VERSION = " 0.6.0";

    /** */
    private IWorkbenchWindow window = null;

    /** */
    private MailEntry mailEntry = null;

    /** */
    private Display display = null;

    /**
     * The constructor
     */
    public ScreenshotPlugin() {
        plugin = this;
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static ScreenshotPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    public static String getInstalledFilePath(String filepath) {
        String path = null;

        URL pluginURL = getDefault().getBundle().getEntry(filepath);

        try {
            URL resolvedURL = FileLocator.resolve(pluginURL);

            path = resolvedURL.getPath();

            if(System.getProperty("os.name").toLowerCase().contains("windows")) {
                if(path.matches("/\\w:/.*")) {
                    path = path.substring(1);
                }
            }
        } catch (IOException ioe) {
            LOG.log(Level.SEVERE, " *** IOException *** : " + ioe);;
            path = null;
        }

        return path;
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        IWorkbench workbench = PlatformUI.getWorkbench();

        window = workbench.getActiveWorkbenchWindow(); //.getWorkbenchWindows()[0];

        display = window.getShell().getDisplay();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        plugin = null;
    }

    /*
    public void earlyStartup()
    {
        System.out.println(" earlyStartup()");
        IWorkbench workbench = PlatformUI.getWorkbench();

        window = workbench.getWorkbenchWindows()[0];

        display = window.getShell().getDisplay();
    }
    */

    public Display getDisplay() {
        return display;
    }

    public String getNameAndVersion() {
        return NAME + VERSION;
    }

    public MailEntry getMailEntry() {
        return mailEntry;
    }

    public void setMailEntry(MailEntry me) {
        mailEntry = me;
    }

    public boolean isMailEntryAvailable() {
        return (mailEntry != null);
    }

    public Shell getShell() {
        return window.getShell();
    }
}
