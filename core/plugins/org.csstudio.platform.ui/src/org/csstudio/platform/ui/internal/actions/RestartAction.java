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
package org.csstudio.platform.ui.internal.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Action that restarts the workbench.
 * 
 * @author Alexander Will
 * 
 */
public class RestartAction extends Action implements
		IWorkbenchWindowActionDelegate {

	/**
	 * System property "eclipse.vm".
	 */
	private static final String PROP_VM = "eclipse.vm"; //$NON-NLS-1$

	/**
	 * System property "eclipse.vmargs".
	 */
	private static final String PROP_VMARGS = "eclipse.vmargs"; //$NON-NLS-1$

	/**
	 * System property "eclipse.exitcode".
	 */
	private static final String PROP_EXIT_CODE = "eclipse.exitcode"; //$NON-NLS-1$

	/**
	 * System property "eclipse.exitdata".
	 */
	private static final String PROP_EXIT_DATA = "eclipse.exitdata"; //$NON-NLS-1$

	/**
	 * System property "-vmargs".
	 */
	private static final String CMD_VMARGS = "-vmargs"; //$NON-NLS-1$

	/**
	 * Reference to the associated workbench window.
	 */
	private IWorkbenchWindow _window;

	/**
	 * Standard constructor.
	 */
	public RestartAction() {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	public final void run(final IAction action) {
		String commandLine = buildCommandLine();

		System.out.println(commandLine);

		if (commandLine == null) {
			return;
		}

		System.setProperty(PROP_EXIT_CODE, Integer.toString(24));
		System.setProperty(PROP_EXIT_DATA, commandLine);
		_window.getWorkbench().restart();
	}

	/**
	 * {@inheritDoc}
	 */
	public final void selectionChanged(final IAction action,
			final ISelection selection) {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	public void dispose() {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	public final void init(final IWorkbenchWindow window) {
		_window = window;
	}

	/**
	 * Create and return a string with command line options for eclipse.exe that
	 * will launch a new workbench that is the same as the currently running
	 * one, but using the argument directory as its workspace.
	 * 
	 * @return a string of command line options or null on error
	 */
	private String buildCommandLine() {
		String property = System.getProperty(PROP_VM);

		StringBuffer result = new StringBuffer(512);

		result.append(property);
		result.append("\n"); //$NON-NLS-1$

		// append the vmargs and commands. Assume that these already end in \n
		String vmargs = System.getProperty(PROP_VMARGS);
		if (vmargs != null) {
			result.append(vmargs);
			result.append(CMD_VMARGS);
			result.append("\n"); //$NON-NLS-1$
			result.append(vmargs);
		}

		return result.toString();
	}
}
