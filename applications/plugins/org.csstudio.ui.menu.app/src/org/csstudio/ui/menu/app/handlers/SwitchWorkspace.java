/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.menu.app.handlers;

import org.csstudio.platform.workspace.RelaunchConstants;
import org.csstudio.platform.workspace.WorkspaceInfo;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/** Command handler to allow selection of different workspace
 *  @author Kay Kasemir
 */
public class SwitchWorkspace extends AbstractHandler
{
    /** Command-line switch for workspace location */
    final private static String CMD_DATA       = "-data";           //$NON-NLS-1$

    /** Command-line switch for JVM args */
    final private static String CMD_VMARGS     = "-vmargs";         //$NON-NLS-1$

    /** Newline */
    final private static String NEW_LINE       = "\n";              //$NON-NLS-1$

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
        final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);

        // Prompt for new workspace
        final WorkspaceInfo workspace_info =
            new WorkspaceInfo(Platform.getInstanceLocation().getURL(), false);
        WorkspaceDialog ws_dialog = new WorkspaceDialog(null, workspace_info, false);
        if (ws_dialog.open() != Window.OK)
            return null;
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
        return null;
    }

    /** Create a command line that will launch a new workbench
     *  that is the same as the currently running
     *  one, but using the argument directory as its workspace.
     *
     *  @param workspace Directory to use as the new workspace
     *  @return New command line or <code>null</code> on error
     */
    @SuppressWarnings("nls")
    private String buildCommandLine(final String workspace)
    {
        String property = System.getProperty(RelaunchConstants.PROP_VM);
        if (property == null)
        {
            MessageDialog.openError(null,
                "Error",
                "Cannot determine virtual machine, need '"
                + RelaunchConstants.PROP_VM + " ...' command-line argument\n"
                + "Workspace switch does not work when started from within IDE!");
            return null;
        }

        final StringBuffer result = new StringBuffer(512);
        result.append(property);
        result.append(NEW_LINE);

        // append the vmargs and commands. Assume that these already end in \n
        final String vmargs = System.getProperty(RelaunchConstants.PROP_VMARGS);
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
