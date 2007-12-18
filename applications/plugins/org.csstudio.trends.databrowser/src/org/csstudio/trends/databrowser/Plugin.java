package org.csstudio.trends.databrowser;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/** Bundle activator registered in manifest.mf.
 *  @author Kay Kasemir
 */
public class Plugin extends AbstractCssUiPlugin
{
    public static final String ID = "org.csstudio.trends.databrowser"; //$NON-NLS-1$
    public static final String Version = "1.0"; //$NON-NLS-1$

    /** The suggested file extension for DataBrowser config files. */
    public static final String FileExtension = "css-plt"; //$NON-NLS-1$
    
    /** Lazily initialized Log4j Logger */
    private static Logger log = null;

    /** The shared instance. */
    private static Plugin plugin = null;

    /** The constructor. */
    public Plugin()
    {
        plugin = this;
    }

    /** @return The shared instance. */
    public static Plugin getDefault()
    {   return plugin;   }

    /** {@inheritDoc} */
    @Override
    public String getPluginId()
    {   return ID;  }
    
    /** {@inheritDoc} */
    @Override
    protected void doStart(final BundleContext context) throws Exception
    {
        // NOP
    }

    /** {@inheritDoc} */
    @Override
    protected void doStop(final BundleContext context) throws Exception
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
    
    /** Returns an image descriptor for the image file.
     *  <p>
     *  Usually, this is the image found via the the given plug-in
     *  relative path.
     *  But this implementation also supports a hack for testing:
     *  If no plugin is running, because for example this is an SWT-only
     *  test, the path is used as is, i.e. relative to the current
     *  directory.
     * 
     *  @param path the path
     *  @return the image descriptor
     */
    @SuppressWarnings("nls")
    public static ImageDescriptor getImageDescriptor(String path)
    {
        // If the plugin is running, get descriptor from the bundle
        if (plugin != null)
            return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
        // ... otherwise, this is an SWT-only test without the plugin:
        try
        {
            final Display display = Display.getCurrent();
            final Image img = new Image(display, path);        
            return ImageDescriptor.createFromImage(img);
        }
        catch (Exception ex)
        {
            getLogger().error("Cannot load image '" + path + "'", ex);
        }
        return null;
    }
}
