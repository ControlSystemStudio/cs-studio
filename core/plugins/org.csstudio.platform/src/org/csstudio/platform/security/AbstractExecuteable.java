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
package org.csstudio.platform.security;

/**
 * This is the superclass of all executeables that do not need an input or
 * output in <code>doWork()</code>.
 * 
 * @author Kai Meyer & Torsten Witte & Alexander Will & Sven Wende
 */
public abstract class AbstractExecuteable {

	/**
	 * 
	 */
	private String _rightId;

	/**
	 * 
	 * 
	 * @param rightId 
	 */
	public AbstractExecuteable(String rightId) {
		assert rightId != null;
		_rightId = rightId;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public final String getRightId() {
		return _rightId;
	}

	/**
	 * Method to execute this AbstractExecutable. This method is called by the
	 * SecureContainer, if the User is allowed to run this. If you want to do
	 * something before or after the actual work in <code>doWork()</code> in
	 * every subclass, you can do it here.
	 */
	public final void execute() {
		if (ExecutionService.getInstance().canExecute(getRightId())) {
			doWork();
		}
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public final boolean canExecute() {
		return ExecutionService.getInstance().canExecute(getRightId());
	}

	/**
	 * This method holds the protected code. It's called by
	 * <code>execute()</code>.
	 */
	protected abstract void doWork();

}
