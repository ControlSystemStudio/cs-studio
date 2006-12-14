package org.csstudio.utility.pv;

import org.csstudio.platform.libs.epics.EpicsPlugin;
import org.csstudio.utility.pv.epics.EPICS_V3_PV;
import org.osgi.framework.BundleContext;

/** Plugin-activator for the PV.
 *  @author Kay Kasemir
 */
public class Plugin extends org.eclipse.core.runtime.Plugin
{
    /** Determine which EPICS CA context type to use from preferences.
     *  @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        EPICS_V3_PV.use_pure_java = EpicsPlugin.getDefault().usePureJava();
    }
}
