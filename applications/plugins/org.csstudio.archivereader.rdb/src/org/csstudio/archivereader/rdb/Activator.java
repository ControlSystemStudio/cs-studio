package org.csstudio.archivereader.rdb;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/** Plugin activator
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Activator extends Plugin
{
    /** Plugin ID defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.archivereader.rdb";
    
    private static Activator instance;

    /** {@inheritDoc} */
    @Override
    public void start(final BundleContext context) throws Exception
    {
        super.start(context);
        instance = this;
    }

    /** @return Singleton instance */
    public static Activator getInstance()
    {
        return instance;
    }
}
