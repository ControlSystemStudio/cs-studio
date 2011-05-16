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
package org.csstudio.auth.security;

import org.csstudio.auth.internal.usermanagement.LoginContext;

/**
 * Abstract super class for executables. An executable encapsulates code that
 * should only be executed, if the currently logged in user has the right to do
 * so. An executable must be equipped with an abstract right ID that will be
 * exposed to the system administrator to configure which users can perform which
 * executables.
 * 
 * @author Kai Meyer & Torsten Witte & Alexander Will & Sven Wende
 */
public abstract class AbstractExecuteable {

	/**
	 * The abstract right ID of this executable.
	 */
	private String _rightId;

	/**
	 * Standard constructor.
	 * 
	 * @param rightId
	 *            The abstract right ID of this executable.
	 */
	public AbstractExecuteable(final String rightId) {
		assert rightId != null;
		_rightId = rightId;
	}

	/**
	 * Return the abstract right ID of this executable.
	 * 
	 * @return The abstract right ID of this executable.
	 */
	public final String getRightId() {
		return _rightId;
	}

	/**
	 * Method to execute this AbstractExecutable. This method only performs an
	 * action, if the currently logged in user is allowed to do so.
	 */
	public final void execute() {
		if (SecurityFacade.getInstance().canExecute(getRightId())) {
			doWork();
		}
	}
	
	/**
	 * Method to execute this AbstractExecutable. This method only performs an
	 * action, if the currently logged in user is allowed to do so.
	 * @param LoginContext
	 * 			The LoginContext, which contains the User
	 */
	public final void executeAs(final LoginContext lc) {
		if (SecurityFacade.getInstance().canExecute(getRightId(), lc, true)) {
			doWork();
		}
	}

	/**
	 * Checks if this AbstractExecutable can be run by the currently logged in
	 * user.
	 * 
	 * @return True, if this AbstractExecutable can be run by the currently
	 *         logged in user.
	 */
	public final boolean canExecute() {
		return SecurityFacade.getInstance().canExecute(getRightId());
	}

	/**
	 * This method holds the protected code. It's called by
	 * <code>execute()</code>.
	 */
	protected abstract void doWork();

}
