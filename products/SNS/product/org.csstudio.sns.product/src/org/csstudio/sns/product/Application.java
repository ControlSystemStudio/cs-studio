package org.csstudio.sns.product;

import org.csstudio.platform.ui.workspace.WorkspaceSwitchHelper;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
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
 *  <p>
 *  TODO: On OS X, it's possible to close the app via Prop-Q or
 *        the leftmost menu, and it quits without running
 *        Plugin.stop().
 *        On Linux, it looks OK.
 *        
 *  @author Kay Kasemir
 */
public class Application implements IApplication
{
    /** {@inheritDoc} */
    public Object start(IApplicationContext context) throws Exception
    {
        Display display = PlatformUI.createDisplay();
        try
        {
            PluginActivator.logInfo("CSS Application started"); //$NON-NLS-1$
            
            int returnCode = 0;
            // Select the workspace
            Location workspace_location = null;
            try
            {
                boolean need_workspace = true;
                while (need_workspace)
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
                        PluginActivator.logInfo("CSS Application Canceled"); //$NON-NLS-1$
                        return IApplication.EXIT_OK;
                    }
                    
                    // Does this require a restart?
                    if (WorkspaceSwitchHelper.prepareWorkspaceSwitch(null,
                                                                    workspace))
                    {
                        MessageDialog.openInformation(null,
                                Messages.Application_RestartTitle,
                                NLS.bind(Messages.Application_RestartMessage,
                                         workspace));
                        PluginActivator.logInfo("CSS Application Relaunch"); //$NON-NLS-1$
                        return IApplication.EXIT_RELAUNCH;
                    }
                    
                    // Lock the workspace
                    workspace_location = Platform.getInstanceLocation();
                    PluginActivator.logInfo("CSS Workspace: " + workspace_location); //$NON-NLS-1$
                    if (workspace_location.lock())
                        need_workspace = false;
                    else
                    {
                        workspace_location = null;
                        // Cannot lock the workspace.
                        // Give message, then query again
                        MessageDialog.openError(null,
                            Messages.Application_WorkspaceInUseError,
                            NLS.bind(Messages.Application_WorkspaceInUseInfo,
                                     workspace));
                    }
                }
                PluginActivator.logInfo("CSS Application Running"); //$NON-NLS-1$
                
                // Assert that there is an open "CSS" project.
                // Without that, an existing 'CSS' might show up,
                // but a 'new Folder' action would run into
                // 'project not open' error...
                final IProject project = ResourcesPlugin.getWorkspace().
                                                getRoot().getProject(Messages.Application_DefaultProject);
                // Assert that it exists...
                if (!project.exists())
                {
                    try
                    {
                        project.create(new NullProgressMonitor());
                    }
                    catch (CoreException ex)
                    {
                        PluginActivator.logException(
                                        "Cannot create " + project.getName(), ex); //$NON-NLS-1$
                        MessageDialog.openError(null,
                                        Messages.Application_ProjectError,
                                        NLS.bind(Messages.Application_ProjectErrorMessage,
                                                  project.getName()));
                        // Give up, quit.
                        return IApplication.EXIT_OK;
                    }
                }
                // .. and open it
                try
                {
                    project.open(new NullProgressMonitor());
                }
                catch (CoreException ex)
                {
                    PluginActivator.logException(
                                    "Cannot open " + project.getName(), ex); //$NON-NLS-1$
                    MessageDialog.openError(null,
                                    Messages.Application_ProjectError,
                                    NLS.bind(Messages.Application_ProjectErrorMessage,
                                              project.getName()));
                    // Give up, quit.
                    return IApplication.EXIT_OK;
                }
                // Run the workbench
                returnCode = PlatformUI.createAndRunWorkbench(display,
                                new ApplicationWorkbenchAdvisor());
            }
            finally
            {
                if (workspace_location != null
                    && workspace_location.isSet())
                    workspace_location.release();
            }
            
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
            // Plain exit from IWorkbench.close()
            return IApplication.EXIT_OK;
        }
        finally
        {
            display.dispose();
        }
    }

    /** {@inheritDoc} */
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
}
