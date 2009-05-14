package org.csstudio.sns.product;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/** Plugin activator
 *  @author Kay Kasemir
 */
public class Activator extends AbstractUIPlugin
{
    /** Plugin ID, defined in MANIFEST.MF */
    final public static String PLUGIN_ID = "org.csstudio.sns.product"; //$NON-NLS-1$
    
    /** Shared instances */
    private static Activator instance = null;
    
    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        instance = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception
    {
        instance = null;
        super.stop(context);
    }


    /** @return Activator instance or <code>null</code> when not running */
    public static Activator getInstance()
    {
        return instance;
    }
}
