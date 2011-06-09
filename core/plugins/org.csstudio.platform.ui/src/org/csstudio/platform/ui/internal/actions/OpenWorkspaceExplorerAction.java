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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.platform.ui.CSSPlatformUiPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;

/**
 * Action that opens up the CSS workspace explorer.
 * 
 * @author Alexander Will
 * 
 */
public final class OpenWorkspaceExplorerAction extends Action implements
		IWorkbenchWindowActionDelegate {

    private static final Logger LOG = Logger.getLogger(CSSPlatformUiPlugin.ID);
    
	/**
	 * Reference to the associated workbench window.
	 */
	private IWorkbenchWindow _window;

	/**
	 * Standard constructor.
	 */
	public OpenWorkspaceExplorerAction() {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void run(final IAction action) {
		try {
			_window.getActivePage().showView(
					"org.eclipse.ui.views.ResourceNavigator"); //$NON-NLS-1$
		} catch (PartInitException e) {
			LOG.log(Level.FINE,"Error while opening the CSS workspace explorer!"); //$NON-NLS-1$
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void selectionChanged(final IAction action,
			final ISelection selection) {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void dispose() {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void init(final IWorkbenchWindow window) {
		_window = window;
	}
}
