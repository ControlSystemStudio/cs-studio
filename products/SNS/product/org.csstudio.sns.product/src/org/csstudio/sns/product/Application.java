package org.csstudio.sns.product;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.csstudio.platform.workspace.RelaunchConstants;
import org.csstudio.platform.workspace.WorkspaceDialog;
import org.csstudio.platform.workspace.WorkspaceInfo;
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

/** SNS CSS Application.
 *  <p>
 *  Per default, it prompts for a workspace, using the setting from
 *  the configuration/config.ini like this as the initial default:
 *  <code>osgi.instance.area.default=@user.home/CSS-Workspaces/Default</code>.
 *  <p>
 *  User can select a different one, which is then used immediately.
 *  User can also select to not be asked again on subsequent runs,
 *  unless there's a problem like "Workspace in use".
 *  <p>
 *  With the command-line option -force_workspace_prompt, meant for a control
 *  room  setup, the config.ini default is always the initial suggestion,
 *  and there is no option to avoid further questions.
 *  <p>
 *  In any case, one can provide the "-data ..." command-line option
 *  which skips the workspace dialog unless there are problems like
 *  "Workspace in use". 
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
    private static final String HELP = "-help"; //$NON-NLS-1$
    private static final String FORCE_WORKSPACE_PROMPT = "-force_workspace_prompt"; //$NON-NLS-1$
    
    /** {@inheritDoc} */
    public Object start(IApplicationContext context) throws Exception
    {
        final Display display = PlatformUI.createDisplay();
        if (display == null)
        {
            PluginActivator.getLogger().error("No display");
            return EXIT_OK;
        }
        
        try
        {
            // Check command-line arguments
            final String args[] =
                (String []) context.getArguments().get("application.args"); //$NON-NLS-1$
            boolean force_workspace_prompt = false;
            for (String arg : args)
            {
                if (arg.equalsIgnoreCase(HELP) ||
                    arg.equalsIgnoreCase("-?")) //$NON-NLS-1$
                {
                    showHelp();
                    // Exit ASAP, see comment below.
                    System.exit(0);
                    return EXIT_OK;
                }
                if (arg.equalsIgnoreCase(FORCE_WORKSPACE_PROMPT))
                {
                    force_workspace_prompt = true;
                    break;
                }
            }
                
            if (! checkInstanceLocation(force_workspace_prompt))
            {
                // We'd like to exit ASAP. Unfortunately, the <code>stop()</code>
                // routine of many UI plugins writes the current settings to
                // the workspace. Even though we have not yet opened any
                // workspace, this will open - even create the default workspace.
                // So exit right away:
                System.exit(0);
                //
                //Platform.endSplash();
                //return EXIT_OK;
            }

            final IProject project = openProject();
            if (project == null)
                return IApplication.EXIT_OK;
            try
            {
                return runApplication(display);
            }
            finally
            {
                closeProject(project);
            }
        }
        finally
        {   // The workspace release/unlock is actually automatic...
            display.dispose();
        }
    }

    /** Display help on stdout */
    @SuppressWarnings("nls") //$NON-NLS-1$
    private void showHelp()
    {
        System.out.println("Command-line options:"); //$NON-NLS-1$
        System.out.format("    %-30s : This help\n", HELP); //$NON-NLS-1$
        System.out.format("    %-30s : Always present workspace dialog, with preconfigured default\n", //$NON-NLS-1$
                FORCE_WORKSPACE_PROMPT);
        System.out.format("    %-30s : Log all messages to the console\n", //$NON-NLS-1$
                "-consoleLog"); //$NON-NLS-1$
        System.out.format("    %-30s : Select workspace on command-line\n", //$NON-NLS-1$
                "-data <some_workspace>"); //$NON-NLS-1$
    }

    /** Check or select the workspace.
     *  <p>
     *  See IDEApplication code from org.eclipse.ui.internal.ide.application
     *  in version 3.3.
     *  That example uses a "Shell" argument, but also has a comment about
     *  bug 84881 and thus not using the shell to force the dialogs to be
     *  top-level, so we skip the shell altogether.
     *  <p> 
     *  Note that we must be very careful with use of the CentralLogger:
     *  Its first use loads the main CSS plugin, which checks preferences
     *  and thus already activates the default workspace, after which we can no
     *  longer change it...
     *  @param force_prompt Set <code>true</code> in a Control Room
     *         setting where the initial suggestion is always the "default"
     *         workspace, and there is no way to suppress the "ask again" option.
     *         Set <code>false</code> in an Office setting where users can
     *         uncheck the "ask again" option and use the last workspace as
     *         a default. 
     *  @return <code>true</code> if all OK
     */
    private boolean checkInstanceLocation(final boolean force_prompt)
    {
        // Was "-data @none" specified on command line?
        final Location instanceLoc = Platform.getInstanceLocation();
        if (instanceLoc == null)
        {
            MessageDialog.openError(null,
                    "No workspace", //$NON-NLS-1$
                    "Cannot run without a workspace"); //$NON-NLS-1$
            return false;
        }
        
        // -data "/some/path" was provided...
        if (instanceLoc.isSet())
        {
            try
            {   // Lock
                if (instanceLoc.lock())
                    return true;
                // Two possibilities:
                // 1. directory is already in use
                // 2. directory could not be created
                final File ws_dir = new File(instanceLoc.getURL().getFile());
                if (ws_dir.exists())
                    MessageDialog.openError(null,
                            org.csstudio.platform.workspace.Messages.Workspace_InUseErrorTitle, 
                            NLS.bind(org.csstudio.platform.workspace.Messages.Workspace_InUseError,
                                    ws_dir.getCanonicalPath()));
                else
                    MessageDialog.openError(null, 
                            org.csstudio.platform.workspace.Messages.Workspace_DirectoryErrorTitle,
                            org.csstudio.platform.workspace.Messages.Workspace_DirectoryError);
            }
            catch (IOException ex)
            {
                MessageDialog.openError(null,
                        org.csstudio.platform.workspace.Messages.Workspace_LockErrorTitle,
                        org.csstudio.platform.workspace.Messages.Workspace_LockError
                        + ex.getMessage());
            }
            return false;
        }
        
        // -data @noDefault or -data not specified, prompt and set
        final WorkspaceInfo workspace_info =
            new WorkspaceInfo(instanceLoc.getDefault(), !force_prompt);
        // Prompt in any case? Or did user decide to be asked again?
        boolean show_dialog = force_prompt | workspace_info.getShowDialog();
        while (true)
        {
            if (show_dialog)
            {
                final WorkspaceDialog workspace_dialog =
                    new WorkspaceDialog(null, workspace_info, !force_prompt);
                if (! workspace_dialog.prompt())
                    return false; // cancelled
            }
            // In case of errors, we will have to ask...
            show_dialog = true;

            try
            {
                // the operation will fail if the url is not a valid
                // instance data area, so other checking is unneeded
                URL workspaceUrl = new URL("file:" + workspace_info.getSelectedWorkspace()); //$NON-NLS-1$
                if (instanceLoc.setURL(workspaceUrl, true)) // set & lock
                {
                    workspace_info.writePersistedData();
                    return true;
                }
            }
            catch (Exception ex)
            {
                MessageDialog.openError(null,
                        org.csstudio.platform.workspace.Messages.Workspace_GenericErrorTitle,
                        org.csstudio.platform.workspace.Messages.Workspace_GenericError + ex.getMessage());
                return false;
            }
            // by this point it has been determined that the workspace is
            // already in use -- force the user to choose again
            MessageDialog.openError(null,
                    org.csstudio.platform.workspace.Messages.Workspace_InUseErrorTitle, 
                    NLS.bind(org.csstudio.platform.workspace.Messages.Workspace_InUseError,
                            workspace_info.getSelectedWorkspace()));
        }
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
                PluginActivator.getLogger().error(
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
            PluginActivator.getLogger().error(
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
            PluginActivator.getLogger().error(
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
    private Object runApplication(final Display display)
    {
        // Run the workbench
        PluginActivator.getLogger().info("CSS Application Running: " //$NON-NLS-1$
                + Platform.getInstanceLocation().getURL().getFile());
        final int returnCode = PlatformUI.createAndRunWorkbench(display,
                        new ApplicationWorkbenchAdvisor());

        // Plain exit from IWorkbench.close()
        if (returnCode != PlatformUI.RETURN_RESTART)
            return EXIT_OK;
        
        // Something called IWorkbench.restart().
        // Is this supposed to be a RESTART or RELAUNCH?
        final Integer exit_code =
            Integer.getInteger(RelaunchConstants.PROP_EXIT_CODE);
        if (EXIT_RELAUNCH.equals(exit_code))
        {   // RELAUCH with new command line
            PluginActivator.getLogger().debug(
                    "RELAUNCH, command line:\n" //$NON-NLS-1$
                    + System.getProperty(RelaunchConstants.PROP_EXIT_DATA));
            return EXIT_RELAUNCH;
        }
        // RESTART without changes
        return EXIT_RESTART;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    public void stop()
    {   // From IDEApplication.stop
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
