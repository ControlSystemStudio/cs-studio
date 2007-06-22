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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
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
    /** The workbench window. */
    private IWorkbenchWindow _window;
    
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
        final String workspace =
            WorkspaceSwitchHelper.promptForWorkspace(shell, true);
        // Aborted?
        if (workspace == null)
            return;
        // Restart required?
        if (WorkspaceSwitchHelper.prepareWorkspaceSwitch(shell, workspace))
            _window.getWorkbench().restart();
    }
}
