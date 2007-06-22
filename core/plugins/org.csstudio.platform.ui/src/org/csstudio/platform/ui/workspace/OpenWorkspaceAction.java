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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/** Implements the open workspace action. Opens a dialog prompting for a
 *  directory and then restarts the IDE on that workspace.
 *  <p>
 *  Implements <code>IWorkbenchWindowActionDelegate</code> for hooking
 *  into file menu via plugin.xml, but also offers a method
 *  <code>runWorkbenchQuery</code> than can be invoked directly for example
 *  by application startup code.
 *  <p>
 *  <b>Code is based upon
 *  <code>org.eclipse.ui.internal.ide.actions.OpenWorkspaceAction</code> in
 *  plugin <code>org.eclipse.ui.ide</code>.</b>
 *  </p>
 * 
 *  @author Alexander Will
 *  @author Kay Kasemir
 *  @version $Revision$
 */
public final class OpenWorkspaceAction extends Action implements
                IWorkbenchWindowActionDelegate
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

    /**
     * The workbench window.
     */
    private IWorkbenchWindow    _window;
    
    
    /** Open dialog for entry of a new workspace.
     *  <p>
     *  In case a workbench restart is required,
     *  the appropriate system properties for the relaunch are
     *  prepared.
     *  
     *  @param shell parent shell or <code>null</code>
     *  @param suppressAskAgain Show the 'use as default, don't ask again?' option?
     *  
     *  @return <code>false</code> if no change is needed because user canceled
     *          or selected the current workspace. <code>true</code> if
     *          a workbench restart is required.
     */
    public static boolean performWorkbenchQuery(final Shell shell,
                                                final boolean suppressAskAgain)
    {
        // The 'current' workspace seems to end in '/',
        // while the user input might or might not.
        // Equalize by chopping trailing '/'.
        String new_ws = promptForWorkspace(shell, suppressAskAgain);
        // Aborted?
        if (new_ws == null)
            return false;
        new_ws = new_ws.trim();
        if (new_ws.endsWith("/")) //$NON-NLS-1$
            new_ws = new_ws.substring(0, new_ws.length() -1);
        // Empty?
        if (new_ws.length() < 1)
            return false;
        // Equalize by chopping trailing '/'.
        String current_ws = Platform.getInstanceLocation().getURL().getPath();
        if (current_ws.endsWith("/")) //$NON-NLS-1$
            current_ws = current_ws.substring(0, current_ws.length() -1);
        // No change?
        if (new_ws.equals(current_ws))
            return false;
        // Create command line with new "-data ..." info
        final String commandLine = buildCommandLine(shell, new_ws);
        if (commandLine == null)
            return false;
        // Prepare for RELAUNCH
        System.setProperty(PROP_EXIT_CODE, Integer.toString(24));
        System.setProperty(PROP_EXIT_DATA, commandLine);
        return true;
    }

    /** Remember the window */
    public void init(IWorkbenchWindow window)
    {
        this._window = window;
    }

    /** Release the window. */
    public void dispose()
    {
        _window = null;
    }

    /** {@inheritDoc} */
    public void selectionChanged(IAction action, ISelection selection)
    {
        // NOP
    }

    /** Query user for new workspace and trigger a relaunch.
     */
    public void run(IAction action)
    {
        final Shell shell = _window.getShell();
        if (performWorkbenchQuery(shell, true))
            _window.getWorkbench().restart();
    }

    /**
     * Use the ChooseWorkspaceDialog to get the new workspace from the user.
     * 
     * @return a string naming the new workspace and null if cancel was selected
     */
    private static String promptForWorkspace(final Shell shell,
                    boolean suppressAskAgain)
    {
        // get the current workspace as the default
        ChooseWorkspaceData data = new ChooseWorkspaceData(Platform
                        .getInstanceLocation().getURL());
        ChooseWorkspaceDialog dialog = new ChooseWorkspaceDialog(shell, data, suppressAskAgain, false);
        dialog.prompt(suppressAskAgain);

        // return null if the user changed their mind
        final String selection = data.getSelection();
        if (selection == null)
            return null;

        // otherwise store the new selection and return the selection
        data.writePersistedData();
        return selection;
    }

    /**
     * Create and return a string with command line options for eclipse.exe that
     * will launch a new workbench that is the same as the currently running
     * one, but using the argument directory as its workspace.
     * 
     * @param workspace
     *            the directory to use as the new workspace
     * @return a string of command line options or null on error
     */
    private static String buildCommandLine(final Shell shell, final String workspace)
    {
        String property = System.getProperty(PROP_VM);
        if (property == null)
        {
            MessageDialog.openError(shell,
                    Messages.getString("OpenWorkspaceAction.PROBLEM_TITLE"), //$NON-NLS-1$
                    NLS.bind(Messages.getString("OpenWorkspaceAction.PROBLEM_MESSAGE"), //$NON-NLS-1$
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
