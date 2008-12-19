package org.csstudio.apputil.ui;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Plugin Activator
 *  @author Kay Kasemir
 */
public class Activator extends AbstractUIPlugin
{
    /** Plug-in ID defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.apputil.ui"; //$NON-NLS-1$

    private static Activator instance;

    public Activator()
    {
        instance = this;
    }
    
    public static Activator getInstance()
    {
        return instance;
    }
    
    /** @return Returns an image descriptor for the image file at the given plug-in
     *  relative path.
     *  @param path The path
     */
    public static ImageDescriptor getImageDescriptor(final String path)
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
    }
}
