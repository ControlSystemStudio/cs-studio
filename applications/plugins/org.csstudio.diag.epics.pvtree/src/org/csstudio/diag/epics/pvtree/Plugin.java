package org.csstudio.diag.epics.pvtree;

import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Plugin class for EPICS PV Tree.
 *  @author Kay Kasemir
 */
public class Plugin extends AbstractUIPlugin
{
    /** The plug-in ID defined in MANIFEST.MF */
    public static final String ID = "org.csstudio.diag.epics.pvtree"; //$NON-NLS-1$

    /** The shared instance */
    private static Plugin plugin;

    /** The constructor. */
    public Plugin()
    {
        plugin = this;
    }
    
    /** @return Returns the shared instance. */
    public static Plugin getDefault()
    {
        return plugin;
    }
}
