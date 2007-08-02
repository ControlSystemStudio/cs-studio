package org.csstudio.sns.product;

import org.csstudio.platform.ResourceService;
import org.csstudio.platform.ui.workspace.WorkspaceSwitchHelper;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/** This class controls all aspects of the application's execution.
 * 
 *  TODO: When closing the app with Prop-Q on OS X,
 *        it just quits w/o performing the Plugin.stop() calls etc.,
 *        so no pres are saved.
 *  
 *        Tried to revert back to IPlatformRunnable, same problem.
 *  
 *  @author Kay Kasemir
 */
public class Application
 implements IApplication
{
    private Location workspace_location = null;

    /** @see IApplication#start */
    public Object start(final IApplicationContext context) throws Exception
    {
        PluginActivator.logInfo("CSS Application started"); //$NON-NLS-1$
        final Display display = PlatformUI.createDisplay();
        try
        {
            final Object workspace_query_result = getWorkspace();
            if (workspace_query_result != null)
                return workspace_query_result;
            return runApp(context, display);
        }
        catch (Throwable ex)
        {
            PluginActivator.logException("SNS Application Error", ex); //$NON-NLS-1$
        }
        finally
        {
            if (workspace_location != null && workspace_location.isSet())
                workspace_location.release();
            display.dispose();
        }
        return IApplication.EXIT_OK;
    }

    /** @see IApplication#stop */
    public void stop()
    {
        System.out.println("Application.stop..."); //$NON-NLS-1$
        final IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench == null)
            return;
        final Display display = workbench.getDisplay();
        display.syncExec(new Runnable()
        {
            public void run()
            {
                if (!display.isDisposed())
                    workbench.close();
            }
        });
    }

    /** Run application with display */
    private Object runApp(final IApplicationContext context, final Display display)
    {
        PluginActivator.logInfo("CSS Application Running"); //$NON-NLS-1$
        
        // Assert that there is an open "CSS" project.
        // Without that, an existing 'CSS' might show up,
        // but a 'new Folder' action would run into
        // 'project not open' error...
        ResourceService.getInstance().createWorkspaceProject("CSS"); //$NON-NLS-1$
        // Is this necessary?
        if (context != null)
            context.applicationRunning();
        final int returnCode = PlatformUI.createAndRunWorkbench(display,
                        new ApplicationWorkbenchAdvisor());
        if (returnCode == PlatformUI.RETURN_RESTART)
        {   // Something called IWorkbench.restart().
            // Is this supposed to be a RESTART or RELAUNCH?
            final Integer exit_code =
                Integer.getInteger(RelaunchConstants.PROP_EXIT_CODE);
            if (IApplication.EXIT_RELAUNCH.equals(exit_code))
            {   // RELAUCH with new command line
                PluginActivator.logInfo("RELAUNCH, command line:"); //$NON-NLS-1$
                PluginActivator.logInfo(
                     System.getProperty(RelaunchConstants.PROP_EXIT_DATA));
                return IApplication.EXIT_RELAUNCH;
            }
            // RESTART without changes
            return IApplication.EXIT_RESTART;
        }
        // Plain exit from IWorkbench.close(), or exception
        return IApplication.EXIT_OK;
    }
    
    /** Determine the workspace via dialog.
     *  <p>
     *  Sets the workspace_location and locks it.
     *  
     *  @return {@link NullPointerException} if all is OK, otherwise one
     *          of the restart or exit codes.
     *  @throws Exception on error
     */
    @SuppressWarnings("nls")
    private Object getWorkspace() throws Exception
    {
        while (true)
        {
            // See also: Eclipse help, Platform Plug-in Devel Guide,
            //           Reference, other ref info,
            //           - Runtime options
            //           - Multi-user install
            
            // Query for workpace
            final String workspace =
                WorkspaceSwitchHelper.promptForWorkspace(null, true);
            // Nothing selected
            if (workspace == null)
            {
                PluginActivator.logInfo("CSS Application Canceled");
                return IApplication.EXIT_OK;
            }
            
            // Does this require a restart?
            if (WorkspaceSwitchHelper.prepareWorkspaceSwitch(null, workspace))
            {
                MessageDialog.openInformation(null,
                        Messages.Application_RestartTitle,
                        NLS.bind(Messages.Application_RestartMessage,
                                 workspace));
                PluginActivator.logInfo("CSS Application Relaunch");
                return IApplication.EXIT_RELAUNCH;
            }
            
            // Lock the workspace
            workspace_location = Platform.getInstanceLocation();
            PluginActivator.logInfo("CSS Workspace: " + workspace_location);
            if (workspace_location.lock())
                return null;
            workspace_location = null;
            // Cannot lock the workspace.
            // Give message, then query again
            MessageDialog.openError(null,
                Messages.Application_WorkspaceInUseError,
                NLS.bind(Messages.Application_WorkspaceInUseInfo,
                         workspace));
        }
    }
}
