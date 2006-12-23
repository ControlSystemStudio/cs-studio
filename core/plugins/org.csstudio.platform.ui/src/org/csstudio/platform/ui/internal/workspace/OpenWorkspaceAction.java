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
package org.csstudio.platform.ui.internal.workspace;

import org.csstudio.platform.ui.internal.localization.Messages;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

/**
 * Implements the open workspace action. Opens a dialog prompting for a
 * directory and then restarts the IDE on that workspace.
 * 
 * <p>
 * <b>Code is based upon
 * <code>org.eclipse.ui.internal.ide.actions.OpenWorkspaceAction</code> in
 * plugin <code>org.eclipse.ui.ide</code>.</b>
 * </p>
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public final class OpenWorkspaceAction extends Action implements
		IWorkbenchAction {

	/**
	 * VM argument "eclipse.vm".
	 */
	private static final String PROP_VM = "eclipse.vm"; //$NON-NLS-1$

	/**
	 * VM argument "eclipse.vmargs".
	 */
	private static final String PROP_VMARGS = "eclipse.vmargs"; //$NON-NLS-1$

	/**
	 * VM argument "eclipse.commands".
	 */
	private static final String PROP_COMMANDS = "eclipse.commands"; //$NON-NLS-1$

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
	private static final String CMD_DATA = "-data"; //$NON-NLS-1$

	/**
	 * Command line argument "-vmargs".
	 */
	private static final String CMD_VMARGS = "-vmargs"; //$NON-NLS-1$

	/**
	 * Line break sequence.
	 */
	private static final String NEW_LINE = "\n"; //$NON-NLS-1$

	/**
	 * The workbench window.
	 */
	private IWorkbenchWindow _window;

	/**
	 * Set definition for this action and text so that it will be used for File
	 * -&gt; Open Workspace in the argument window.
	 * 
	 * @param window
	 *            the window in which this action should appear
	 */
	public OpenWorkspaceAction(final IWorkbenchWindow window) {
		super(Messages.getString("OpenWorkspaceAction.TITLE")); //$NON-NLS-1$

		if (window == null) {
			throw new IllegalArgumentException();
		}

		this._window = window;
		setToolTipText(Messages.getString("OpenWorkspaceAction.MESSAGE")); //$NON-NLS-1$
		setActionDefinitionId("org.csstudio.platform.ui.openWorkspace"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		String path = promptForWorkspace();
		if (path == null) {
			return;
		}

		String commandLine = buildCommandLine(path);
		if (commandLine == null) {
			return;
		}

		System.setProperty(PROP_EXIT_CODE, Integer.toString(24));
		System.setProperty(PROP_EXIT_DATA, commandLine);
		_window.getWorkbench().restart();
	}

	/**
	 * Use the ChooseWorkspaceDialog to get the new workspace from the user.
	 * 
	 * @return a string naming the new workspace and null if cancel was selected
	 */
	private String promptForWorkspace() {
		// get the current workspace as the default
		ChooseWorkspaceData data = new ChooseWorkspaceData(Platform
				.getInstanceLocation().getURL());
		ChooseWorkspaceDialog dialog = new ChooseWorkspaceDialog(_window
				.getShell(), data, true, false);
		dialog.prompt(true);

		// return null if the user changed their mind
		String selection = data.getSelection();
		if (selection == null) {
			return null;
		}

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
	private String buildCommandLine(final String workspace) {
		String property = System.getProperty(PROP_VM);
		if (property == null) {
			MessageDialog.openError(_window.getShell(), Messages
					.getString("OpenWorkspaceAction.PROBLEM_TITLE"), //$NON-NLS-1$
					NLS.bind(Messages
							.getString("OpenWorkspaceAction.PROBLEM_MESSAGE"), //$NON-NLS-1$
							PROP_VM));
			return null;
		}

		StringBuffer result = new StringBuffer(512);
		result.append(property);
		result.append(NEW_LINE);

		// append the vmargs and commands. Assume that these already end in \n
		String vmargs = System.getProperty(PROP_VMARGS);
		if (vmargs != null) {
			result.append(vmargs);
		}

		// append the rest of the args, replacing or adding -data as required
		property = System.getProperty(PROP_COMMANDS);
		if (property == null) {
			result.append(CMD_DATA);
			result.append(NEW_LINE);
			result.append(workspace);
			result.append(NEW_LINE);
		} else {
			// find the index of the arg to replace its value
			int cmdDataPos = property.lastIndexOf(CMD_DATA);
			if (cmdDataPos != -1) {
				cmdDataPos += CMD_DATA.length() + 1;
				result.append(property.substring(0, cmdDataPos));
				result.append(workspace);
				result.append(property.substring(property.indexOf('\n',
						cmdDataPos)));
			} else {
				result.append(CMD_DATA);
				result.append(NEW_LINE);
				result.append(workspace);
				result.append(NEW_LINE);
				result.append(property);
			}
		}

		// put the vmargs back at the very end (the eclipse.commands property
		// already contains the -vm arg)
		if (vmargs != null) {
			result.append(CMD_VMARGS);
			result.append(NEW_LINE);
			result.append(vmargs);
		}

		return result.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public void dispose() {
		_window = null;
	}
}
