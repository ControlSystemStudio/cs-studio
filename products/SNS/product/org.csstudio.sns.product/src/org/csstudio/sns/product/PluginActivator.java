package org.csstudio.sns.product;
import org.csstudio.platform.ResourceService;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/** Plugin lifecycle.
 *  @author Kay Kasemir
 */
public class PluginActivator extends AbstractUIPlugin
{
    final public static String ID = "org.csstudio.sns.product"; //$NON-NLS-1$
    private static PluginActivator instance;
    
    public PluginActivator()
    {
        instance = this;
    }

    /** {@inheritDoc} */
    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        // Assert that there is an open "CSS" project
        ResourceService.getInstance().createWorkspaceProject("CSS"); //$NON-NLS-1$
    }
    
    /** Log an info message. */
    public static void logInfo(String info)
    {
        log(IStatus.INFO, info, null);
    }

    /** Log something. */
    private static void log(final int severity,
                            final String message, final Throwable exception)
    {
        if (instance == null)
            System.out.println("LOG MESSAGE:" + message); //$NON-NLS-1$
        else
            instance.getLog().log(
                new Status(IStatus.INFO, ID, IStatus.OK, message, exception));
    }
}
