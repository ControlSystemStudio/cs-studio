package org.csstudio.sns.product;

import java.io.IOException;

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
@SuppressWarnings("nls")
public class Application implements IApplication
{
    /** The display, set in <code>start()</code> */
    private Display display = null;
    
    /** The workspace location, set in <code>checkWorkspace()</code> */
    private Location workspace_location = null;
    
    /** {@inheritDoc} */
    public Object start(IApplicationContext context) throws Exception
    {
        display = PlatformUI.createDisplay();
        if (display == null)
        {
            PluginActivator.logError("No display");
            return IApplication.EXIT_OK;
        }
        final Object return_code = checkWorkspace();
        if (return_code != null)
            return return_code;
        try
        {
            final IProject project = openProject();
            if (project == null)
                return IApplication.EXIT_OK;
            try
            {
                return runApplication();
            }
            finally
            {
                closeProject(project);
            }
        }
        finally
        {
            // Unlock workspace
            if (workspace_location != null && workspace_location.isSet())
            {
                workspace_location.release();
                workspace_location = null;
            }
            // Release display
            display.dispose();
            display = null;
        }
    }

    /** Check the workspace.
     *  <p>
     *  Cannot really change the current workspace for this application
     *  instance, only query the user, and trigger a restart with another
     *  workspace.
     *  <p>
     *  See also: Eclipse help, Platform Plug-in Devel Guide,
     *            Reference, other ref info,
     *            - Runtime options
     *            - Multi-user install
     * @return <code>null</code> if all OK, otherwise an IApplication exit code.
     */
    private Object checkWorkspace()
    {
        while (true)
        {
            // Query user.
            String workspace = null;
            final Location current_workspace = Platform.getInstanceLocation();
            try
            {
                workspace = WorkspaceSwitchHelper.promptForWorkspace(null, true);
            }
            catch (Throwable ex)
            {
                // WorkspaceSwitchHelper is inside the CSS platform plugin,
                // which tries to get at preferences when opened,
                // and that in turn tries to use the workspace.
                // If the 'current' workspace cannot be accessed
                // because of e.g. file permissions, the whole plugin won't load,
                // and we get an "unknown class" error.
                MessageDialog.openError(null,
                        Messages.Application_FatalWorkspaceError,
                        NLS.bind(Messages.Application_FatalWorkspaceMessage,
                                 current_workspace.getURL().getFile()));
                // Can't do anything but quit.
                return IApplication.EXIT_OK;
            }
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
            
            // We are in the requested workspace.
            workspace_location = Platform.getInstanceLocation();
            PluginActivator.logInfo("CSS Workspace: "
                            + workspace_location.getURL());
            // Lock the workspace
            try
            {
                if (workspace_location.lock())
                    break;
            }
            catch (IOException ex)
            {
                PluginActivator.logException("Cannot lock workspace", ex);
            }
            // Cannot lock the workspace
            workspace_location = null;
            // Give message, then query again
            MessageDialog.openError(null,
                Messages.Application_WorkspaceInUseError,
                NLS.bind(Messages.Application_WorkspaceInUseInfo,
                         workspace));
        }
        return null;
    }

    /** Open/Create the main project.
     *  @return Project or <code>null</code>
     */
    private IProject openProject()
    {
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
                                NLS.bind(Messages.Application_ProjectInitErrorMessage,
                                          project.getName()));
                // Give up, quit.
                return null;
            }
        }
        // .. and open it
        try
        {
            project.open(new NullProgressMonitor());
            return project;
        }
        catch (CoreException ex)
        {
            PluginActivator.logException(
                            "Cannot open " + project.getName(), ex); //$NON-NLS-1$
            MessageDialog.openError(null,
                            Messages.Application_ProjectError,
                            NLS.bind(Messages.Application_ProjectInitErrorMessage,
                                      project.getName()));
        }
        return null;
    }
    
    /** Close the project, handling all exceptions */
    private void closeProject(final IProject project)
    {
        if (project == null)
            return;
        try
        {
            project.close(new NullProgressMonitor());
        }
        catch (CoreException ex)
        {
            PluginActivator.logException(
                            "Error closing " + project.getName(), ex); //$NON-NLS-1$
            MessageDialog.openError(null,
                            Messages.Application_ProjectError,
                            NLS.bind(Messages.Application_ProjectExitErrorMessage,
                                      project.getName()));
        }
    }

    /** Run the application.
     *  @return IApplication exit code.
     */
    private Object runApplication()
    {
        // Run the workbench
        PluginActivator.logInfo("CSS Application Running"); //$NON-NLS-1$
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
        // Plain exit from IWorkbench.close()
        return IApplication.EXIT_OK;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    public void stop()
    {
        PluginActivator.logInfo("CSS Application stopped"); //$NON-NLS-1$
        final IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench == null  ||  display == null)
            return;
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
