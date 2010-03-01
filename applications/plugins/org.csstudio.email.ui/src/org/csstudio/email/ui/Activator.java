package org.csstudio.email.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Plugin Activator
 *  @author Kay Kasemir
 *  TODO Externalize Strings
 *  TODO Preferences
 *  TODO Preference GUI
 */
@SuppressWarnings("nls")
public class Activator extends AbstractUIPlugin
{
    /** Plugin ID defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.email.ui";
    
    /** @return Returns an image descriptor for the image file at the given plug-in
     *  relative path.
     *  @param path The path
     */
    public static ImageDescriptor getImageDescriptor(final String path)
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
    }
}
