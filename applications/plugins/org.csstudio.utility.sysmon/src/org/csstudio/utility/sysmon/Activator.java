package org.csstudio.utility.sysmon;

import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Plugin activator.
 *  @author Kay Kasemir
 */
public class Activator extends AbstractUIPlugin
{
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
}
