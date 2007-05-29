package org.csstudio.utility.pv;

import org.csstudio.platform.AbstractCssPlugin;
import org.csstudio.platform.libs.epics.EpicsPlugin;
import org.csstudio.utility.pv.epics.EPICS_V3_PV;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/** Plugin-activator for the PV.
 *  @author Kay Kasemir
 */
public class Plugin extends AbstractCssPlugin
{
	public final static String ID = "org.csstudio.utility.pv"; //$NON-NLS-1$
	
	@Override
    public String getPluginId()
	{   return ID; }

    /** @see AbstractCssPlugin */
    @SuppressWarnings("nls")
    @Override
    protected void doStart(BundleContext context) throws Exception
    {
        try
        {
            EPICS_V3_PV.use_pure_java = EpicsPlugin.getDefault().usePureJava();
            final String message = EPICS_V3_PV.use_pure_java ?
    		        				"Using pure java CAJ" : "Using JCA with JNI";
    		getLog().log(new Status(IStatus.INFO, ID, IStatus.OK, message, null));
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            getLog().log(new Status(IStatus.ERROR, ID, IStatus.OK,
                            "Cannot load EPICS_V3_PV", e));
        }
    }
    /** @see AbstractCssPlugin */
    @Override
    protected void doStop(BundleContext context) throws Exception
    {
        EPICS_V3_PV.use_pure_java = EpicsPlugin.getDefault().usePureJava();
    }
}
