package org.csstudio.platform.workspace;

import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Plugin activator.
 *  @author Kay Kasemir
 */
public class Activator extends AbstractUIPlugin
{
    final public static String ID = "org.csstudio.platform.workspace";  //$NON-NLS-1$
    private static Activator plugin = null;
    
    public Activator()
    {
        plugin = this;
    }
    
    public static Activator getInstance()
    {
        return plugin;
    }
}
