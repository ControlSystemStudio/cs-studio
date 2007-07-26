package org.csstudio.swt.chart;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Plugin activator.
 *  @author Kay Kasemir
 */
public class Activator extends Plugin
{
    final public static String ID = "org.csstudio.swt.chart"; //$NON-NLS-1$
    private static Activator plugin;
    
    /** Constructor */
    public Activator()
    {
        plugin = this;
    }
    
    /** @return The singleton instance. */
    static public Activator getDefault()
    {
        return plugin;
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
    public static void logException(String message, Throwable ex)
    {
        log(IStatus.ERROR, message, ex);
    }
  
    /** Add a message to the log.
     *  @param type
     *  @param message
     *  @param e Exception or <code>null</code>
     */
    private static void log(int type, String message, Throwable ex)
    {
        if (plugin == null)
            System.out.println(message);
        else
            plugin.getLog().log(new Status(type, ID, IStatus.OK, message, ex));
        if (ex != null)
            ex.printStackTrace();
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
        catch (Exception e)
        {
            logException("Cannot load image '" + path + "'", e);
        }
        return null;
    }
}
