/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.platform.ui.workspace;

import org.csstudio.platform.ui.internal.localization.Messages;
import org.csstudio.platform.ui.internal.workspace.ChooseWorkspaceData;
import org.csstudio.platform.ui.internal.workspace.ChooseWorkspaceDialog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/** Helper for selecting/switching the workspace.
 *  <b>Code is based upon
 *  <code>org.eclipse.ui.internal.ide.actions.OpenWorkspaceAction</code> in
 *  plugin <code>org.eclipse.ui.ide</code>.</b>
 *  </p>
 * 
 *  @author Alexander Will
 *  @author Kay Kasemir
 *  @version $Revision$
 */
public final class WorkspaceSwitchHelper
{
    /**
     * VM argument "eclipse.vm".
     */
    private static final String PROP_VM        = "eclipse.vm";      //$NON-NLS-1$

    /**
     * VM argument "eclipse.vmargs".
     */
    private static final String PROP_VMARGS    = "eclipse.vmargs";  //$NON-NLS-1$

    /**
     * VM argument "eclipse.commands".
     */
    private static final String PROP_COMMANDS  = "eclipse.commands"; //$NON-NLS-1$

    /**
     * VM argument "eclipse.exitcode".
     */
    private static final String PROP_EXIT_CODE = "eclipse.exitcode"; //$NON-NLS-1$

    /**
     * VM argument "eclipse.exitdata".
     */
    private static final String PROP_EXIT_DATA = "eclipse.exitdata"; //$NON-NLS-1$

    /**
     * Command lind argument "-data".
     */
    private static final String CMD_DATA       = "-data";           //$NON-NLS-1$

    /**
     * Command line argument "-vmargs".
     */
    private static final String CMD_VMARGS     = "-vmargs";         //$NON-NLS-1$

    /**
     * Line break sequence.
     */
    private static final String NEW_LINE       = "\n";              //$NON-NLS-1$

    /** Prompt user for workspace.
     *  <p>
     *  The resulting workspace name might be a new one, an existing workspace,
     *  or the current workspace.
     *  <p>
     *  @param shell Shell or <code>null</code>
     *  @param suppressAskAgain Same arg that the OpenWorkspaceAction used.
     *         Behavior a bit fuzzy.
     *         
     *  @return Workspace name, or <code>null</code> if cancel was selected.
     */
    public static String promptForWorkspace(final Shell shell,
                    boolean suppressAskAgain)
    {
        // get the current workspace as the default
        final ChooseWorkspaceData data = new ChooseWorkspaceData(Platform
                        .getInstanceLocation().getURL());
        final ChooseWorkspaceDialog dialog =
            new ChooseWorkspaceDialog(shell, data, suppressAskAgain, false);
        dialog.prompt(suppressAskAgain);
    
        // return null if the user changed their mind
        final String selection = data.getSelection();
        if (selection == null)
            return null;
    
        // otherwise store the new selection and return the selection
        data.writePersistedData();
        return selection;
    }

    /** Prepare a switch to the given workspace.
     *  <p>
     *  In case the given workspace matches the current one,
     *  nothing else is done and <code>false</code> is returned.
     *  <p>
     *  In case a workbench restart is required,
     *  the appropriate system properties for the relaunch are
     *  prepared and <code>true</code> is returned.
     *  
     *  @param shell Shell (or <code>null</code>) for use by error dialog.
     *  @param workspace The directory to use as the new workspace
     *  
     *  @return <code>true</code> if a workbench restart is required.
     */
    public static boolean prepareWorkspaceSwitch(final Shell shell,
                                                 String workspace)
    {
        if (workspace == null)
            return false;
        workspace = workspace.trim();
        // In case there's no intitial '/':
        if (! workspace.startsWith("/"))
        	workspace = "/" + workspace;
        // In case we get windows '\' slashes, normalize them to the
        // Unix/OS X slashes that the Platform provides for the current
        // workspace.
        workspace = workspace.replace('\\', '/');
        // The 'current' workspace seems to end in '/',
        // while the user input might or might not.
        // Equalize by chopping trailing '/'.
        if (workspace.endsWith("/")) //$NON-NLS-1$
            workspace = workspace.substring(0, workspace.length() -1);
        // Empty?
        if (workspace.length() < 1)
            return false;
        // Equalize by chopping trailing '/'.
        String current_ws = Platform.getInstanceLocation().getURL().getPath();
        if (current_ws.endsWith("/")) //$NON-NLS-1$
            current_ws = current_ws.substring(0, current_ws.length() -1);
        // No change?
        if (workspace.equals(current_ws))
            return false;
        // Create command line with new "-data ..." info
        final String commandLine = buildCommandLine(shell, workspace);
        if (commandLine == null)
            return false;
        // Prepare for RELAUNCH
        System.setProperty(PROP_EXIT_CODE, Integer.toString(24));
        System.setProperty(PROP_EXIT_DATA, commandLine);
        return true;
    }

    /** Create and return a string with command line options for eclipse.exe that
     *  will launch a new workbench that is the same as the currently running
     *  one, but using the argument directory as its workspace.
     * 
     *  @param shell Shell (or <code>null</code>) for use by error dialog.
     *  @param workspace The directory to use as the new workspace
     *  @return a string of command line options or null on error
     */
    private static String buildCommandLine(final Shell shell, final String workspace)
    {
        String property = System.getProperty(PROP_VM);
        if (property == null)
        {
            MessageDialog.openError(shell,
                    Messages.OpenWorkspaceAction_PROBLEM_TITLE,
                    NLS.bind(Messages.OpenWorkspaceAction_PROBLEM_MESSAGE,
                             PROP_VM));
            return null;
        }

        StringBuffer result = new StringBuffer(512);
        result.append(property);
        result.append(NEW_LINE);

        // append the vmargs and commands. Assume that these already end in \n
        String vmargs = System.getProperty(PROP_VMARGS);
        if (vmargs != null)
        {
            result.append(vmargs);
        }

        // append the rest of the args, replacing or adding -data as required
        property = System.getProperty(PROP_COMMANDS);
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
            int cmdDataPos = property.lastIndexOf(CMD_DATA);
            if (cmdDataPos != -1)
            {
                cmdDataPos += CMD_DATA.length() + 1;
                result.append(property.substring(0, cmdDataPos));
                result.append(workspace);
                result.append(property.substring(property.indexOf('\n',
                                cmdDataPos)));
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
