package org.csstudio.trends.databrowser;

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
public class Plugin extends AbstractUIPlugin
{
    public static final String ID = "org.csstudio.trends.databrowser"; //$NON-NLS-1$
    public static final String Version = "0.1"; //$NON-NLS-1$
    // The shared instance.
    private static Plugin plugin = null;

    /** The constructor. */
    public Plugin()
    {
        plugin = this;
    }

    /** This method is called upon plug-in activation */
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
    }

    /** This method is called when the plug-in is stopped */
    public void stop(BundleContext context) throws Exception
    {
        super.stop(context);
        plugin = null;
    }

    /** Returns the shared instance. */
    public static Plugin getDefault()
    {
        return plugin;
    }
    
    /** Add error message to the plugin log. */
    public static void logError(String message)
    {
        getDefault().log(IStatus.ERROR, message, null);
    }

    /** Add an exception to the plugin log. */
    public static void logException(String message, Exception e)
    {
        getDefault().log(IStatus.ERROR, message, e);
    }

    /** Add a message to the log.
     * @param type
     * @param message
     */
    private void log(int type, String message, Exception e)
    {
        getLog().log(new Status(type,
                                getClass().getName(),
                                IStatus.OK,
                                message,
                                e));
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
            e.printStackTrace();
        }
        return null;
    }
}
