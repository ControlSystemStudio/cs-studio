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
package org.csstudio.platform.ui.security;

import org.csstudio.platform.security.ExecutionService;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * This is the superclass for any IWorkbenchWindowActionDelegate that depends on the rights of the current user.
 * @author Kai Meyer & Torsten Witte & Alexander Will & Sven Wende
 */
public abstract class AbstractUserDependentActionDelegate extends AbstractUserDependentAction implements IWorkbenchWindowActionDelegate {

	/**
	 * 
	 */
	private IAction _action;
	
	/**
	 * 
	 * 
	 * @param rightId 
	 */
	public AbstractUserDependentActionDelegate(String rightId) {
		super(rightId);
	}

	/**
	 * 
	 * 
	 * @param action 
	 */
	public void run(IAction action) {
		_action = action;
		updateState();
		run();
	}

	/**
	 * 
	 * 
	 * @param action 
	 * @param selection 
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		_action = action;
		updateState();
	}

	/**
	 * 
	 */
	protected void updateState() {
		if (_action != null) {
			_action.setEnabled(ExecutionService.getInstance().canExecute(getRightId()));
		}
	}
	
}
