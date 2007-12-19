package org.csstudio.display.pvtable;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/** The main plugin class to be used in the desktop.
 * 
 *  @author Kay Kasemir
 */
public class Plugin extends AbstractCssUiPlugin
{
    /** The plug-in ID */
    final public static String ID = "org.csstudio.display.pvtable"; //$NON-NLS-1$

    /** File extension for config. files */
    final public static String FileExtension = "css-pvtable"; //$NON-NLS-1$
    
    /** Lazily initialized Log4j Logger */
    private static Logger log = null;
    
    /** The shared instance */
    private static Plugin plugin;
    
    /** The constructor. */
    public Plugin()
    {
        if (plugin != null)
            throw new IllegalStateException("Plugin is singleton"); //$NON-NLS-1$
        plugin = this;
    }

    /** @return Returns the shared instance. */
    public static Plugin getDefault()
    {
        return plugin;
    }
    
    /** @see AbstractCssUiPlugin */
    @Override
    public String getPluginId()
    {   return ID;  }
    
    /** @see AbstractCssUiPlugin */
    @Override
    protected void doStart(BundleContext context) throws Exception
    { /* NOP */ }

    /** @see AbstractCssUiPlugin */
    @Override
    protected void doStop(BundleContext context) throws Exception
    {
        plugin = null;
    }

    /** @return Log4j Logger */
    public static Logger getLogger()
    {
        if (log == null) // Also works with plugin==null during unit tests
            log = CentralLogger.getInstance().getLogger(plugin);
        return log;
    }

    /** @return Returns an image descriptor for the image file at the given plug-in
     *  relative path.
     *  @param path The path
     */
    public static ImageDescriptor getImageDescriptor(String path)
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
    }
}
