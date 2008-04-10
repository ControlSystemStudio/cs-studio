package org.csstudio.platform.workspace.actions;

import org.csstudio.platform.workspace.RelaunchConstants;
import org.csstudio.platform.workspace.WorkspaceDialog;
import org.csstudio.platform.workspace.WorkspaceInfo;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;


/** Action that restarts the workspace with a new workspace location.
 *  @author Kay Kasemir
 */
public class SwitchWorkspaceAction implements IWorkbenchWindowActionDelegate
{
    /** Command-line switch for workspace location */
    final private static String CMD_DATA       = "-data";           //$NON-NLS-1$
 
    /** Command-line switch for JVM args */
    final private static String CMD_VMARGS     = "-vmargs";         //$NON-NLS-1$
 
    /** Newline */
    final private static String NEW_LINE       = "\n";              //$NON-NLS-1$

    /** Workspace window */
    private IWorkbenchWindow window;
    
    /** Remember the workspace window */
    public void init(IWorkbenchWindow window)
    {
        this.window = window;
    }

    /** Release the workspace window */
    public void dispose()
    {
        window = null;
    }

    /** Restart the workspace with a new workspace. */
    @SuppressWarnings("nls")
    public void run(IAction action)
    {
        // Prompt for new workspace
        final WorkspaceInfo workspace_info =
            new WorkspaceInfo(Platform.getInstanceLocation().getURL(), false);
        WorkspaceDialog ws_dialog = new WorkspaceDialog(null, workspace_info, false);
        if (!ws_dialog.prompt())
            return;
        workspace_info.writePersistedData();
        // Restart with a command line that uses the new workspace
        // Like org.eclipse.ui.internal.ide.actions.OpenWorkspaceAction
        // we even restart if the selected workspace matches the current one.
        final String commandline =
            buildCommandLine(workspace_info.getSelectedWorkspace());
        System.setProperty(RelaunchConstants.PROP_EXIT_CODE,
                           IApplication.EXIT_RELAUNCH.toString());
        System.setProperty(RelaunchConstants.PROP_EXIT_DATA, commandline);
        window.getWorkbench().restart();
    }

    /** {@inheritDoc} */
    public void selectionChanged(IAction action, ISelection selection)
    {
        // ignore
    }
    
    /** Create a command line that will launch a new workbench
     *  that is the same as the currently running
     *  one, but using the argument directory as its workspace.
     * 
     *  @param workspace Directory to use as the new workspace
     *  @return New command line or <code>null</code> on error
     */
    @SuppressWarnings("nls")
    private String buildCommandLine(String workspace)
    {
        String property = System.getProperty(RelaunchConstants.PROP_VM);
        if (property == null)
        {
            MessageDialog.openError(window.getShell(),
                "Error",
                "Cannot determine virtual machine, need '"
                + RelaunchConstants.PROP_VM + " ...' command-line argument\n"
                + "Workspace switch does not work when started from within IDE!");
            
            return null;
        }

        StringBuffer result = new StringBuffer(512);
        result.append(property);
        result.append(NEW_LINE);

        // append the vmargs and commands. Assume that these already end in \n
        String vmargs = System.getProperty(RelaunchConstants.PROP_VMARGS);
        if (vmargs != null)
            result.append(vmargs);

        // append the rest of the args, replacing or adding -data as required
        property = System.getProperty(RelaunchConstants.PROP_COMMANDS);
        if (property == null)
        {
            result.append(CMD_DATA);
            result.append(NEW_LINE);
            result.append(workspace);
            result.append(NEW_LINE);
        }
        else
        {
            // find the index of the arg to replace its value
            int cmd_data_pos = property.lastIndexOf(CMD_DATA);
            if (cmd_data_pos != -1)
            {
                cmd_data_pos += CMD_DATA.length() + 1;
                result.append(property.substring(0, cmd_data_pos));
                result.append(workspace);
                result.append(property.substring(property.indexOf('\n',
                                cmd_data_pos)));
            }
            else
            {
                result.append(CMD_DATA);
                result.append(NEW_LINE);
                result.append(workspace);
                result.append(NEW_LINE);
                result.append(property);
            }
        }

        // put the vmargs back at the very end (the eclipse.commands property
        // already contains the -vm arg)
        if (vmargs != null)
        {
            result.append(CMD_VMARGS);
            result.append(NEW_LINE);
            result.append(vmargs);
        }

        return result.toString();
    }
}
