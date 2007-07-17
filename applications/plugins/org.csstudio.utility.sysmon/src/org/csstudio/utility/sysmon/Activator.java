package org.csstudio.utility.sysmon;

import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Plugin activator.
 *  @author Kay Kasemir
 */
public class Activator extends AbstractUIPlugin
{
    private static Activator instance;
    
    /** Constructor */
    public Activator()
    {
        instance = this;
    }
    
    /** @return The singleton instance. */
    static public Activator getDefault()
    {
        return instance;
    }
    
}
