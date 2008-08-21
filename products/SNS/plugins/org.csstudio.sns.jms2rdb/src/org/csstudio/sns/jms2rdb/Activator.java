package org.csstudio.sns.jms2rdb;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/** Plugin activator
 *  @author Kay Kasemir
 *  reviewed by Katia Danilova 08/20/08
 */
@SuppressWarnings("nls")
public class Activator extends Plugin
{
    /** The plug-in ID defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.sns.jms2rdb";
    
    /** Singleton instance */
    private static Activator instance;

    /** {@inheritDoc} */
    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        instance = this;
    }

    /** {@inheritDoc} */
    @Override
    public void stop(BundleContext context) throws Exception
    {
        instance = null;
        super.stop(context);
    }

    /** @return Singleton instance */
    public static Activator getInstance()
    {
        return instance;
    }
}
