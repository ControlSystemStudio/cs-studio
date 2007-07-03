package org.csstudio.trends.databrowser;

import org.csstudio.platform.ResourceService;
import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/** The main plugin class to be used in the desktop.
 *  @author Kay Kasemir
 */
public class Plugin extends AbstractCssUiPlugin
{
    public static final String ID = "org.csstudio.trends.databrowser"; //$NON-NLS-1$
    public static final String Version = "0.1"; //$NON-NLS-1$

    /** The suggested file extension for DataBrowser config files. */
    public static final String FileExtension = "css-plt"; //$NON-NLS-1$
    
    /** The shared instance. */
    private static Plugin plugin = null;

    /** The constructor. */
    public Plugin()
    {
        plugin = this;
    }

    /** Returns the shared instance. */
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
        // Assert that there is an open "CSS" project
    	ResourceService.getInstance().createWorkspaceProject("CSS"); //$NON-NLS-1$
    }

    /** {@inheritDoc} */
    @Override
    protected void doStop(final BundleContext context) throws Exception
    {
        plugin = null;
    }

    /** Add info message to the plugin log. */
    public static void logInfo(String message)
    {
        log(IStatus.INFO, message, null);
    }
    
    /** Add error message to the plugin log. */
    public static void logError(String message)
    {
        log(IStatus.ERROR, message, null);
    }

    /** Add an exception to the plugin log. */
    public static void logException(String message, Throwable e)
    {
        log(IStatus.ERROR, message, e);
    }

    /** Add a message to the log.
     *  @param type
     *  @param message
     *  @param e Exception or <code>null</code>
     */
    private static void log(int type, String message, Throwable ex)
    {
        if (plugin == null)
        {
            System.out.println(message);
            if (ex != null)
                ex.printStackTrace();
            return;
        }
        plugin.getLog().log(new Status(type, ID, IStatus.OK, message, ex));
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
            Display display = Display.getCurrent();
            Image img = new Image(display, path);        
            return ImageDescriptor.createFromImage(img, display);
        }
        catch (Exception e)
        {
            logException("Cannot load image '" + path + "'", e);
        }
        return null;
    }
}
