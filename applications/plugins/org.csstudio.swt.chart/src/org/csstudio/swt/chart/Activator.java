package org.csstudio.swt.chart;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Plugin activator.
 *  @author Kay Kasemir
 */
public class Activator extends Plugin
{
    /** Plugin ID, defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.swt.chart"; //$NON-NLS-1$
    
    private static Activator plugin;
    
    /** Lazily initialized Log4j Logger */
    private static Logger log = null;

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
        catch (Exception e)
        {
            getLogger().error("Cannot load image '" + path + "'", e);
        }
        return null;
    }
}
