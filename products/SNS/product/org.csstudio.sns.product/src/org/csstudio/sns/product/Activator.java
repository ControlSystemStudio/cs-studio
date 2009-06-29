package org.csstudio.sns.product;

import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Plugin activator
 *  @author Kay Kasemir
 */
public class Activator extends AbstractUIPlugin
{
    /** Plugin ID, defined in MANIFEST.MF */
    final public static String PLUGIN_ID = "org.csstudio.sns.product"; //$NON-NLS-1$

    // Used to get the name and version from bundle information, but
    // this and more is already available via Help/About/Configuration Details
//    @Override
//    public void start(BundleContext context) throws Exception
//    {
//        super.start(context);
//        
//        final Dictionary<String, String> headers = getBundle().getHeaders();
//        final String name = headers.get("Bundle-Name");
//        final String version = headers.get("Bundle-Version");
//    }
}
