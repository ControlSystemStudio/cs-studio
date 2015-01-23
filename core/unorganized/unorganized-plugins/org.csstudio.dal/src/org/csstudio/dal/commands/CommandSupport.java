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

package org.csstudio.dal.commands;

import org.csstudio.dal.RemoteException;

import java.lang.reflect.Method;


/**
 * Default command implementation.
 *
 * @author Blaz Hostnik
 */
public class CommandSupport implements Command
{
	private CommandContext owner;
	private Object host;
	private Method method;

	/**
	     * General constructor.
	     *
	     * @param owner    in this context is method present.
	     * @param host on it, this command is called.
	     * @param method this command represents it.
	     */
	public CommandSupport(CommandContext owner, Object host, Method method)
	{
		this.owner = owner;
		this.host = host;
		this.method = method;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.csstudio.dal.commands.Command#getName()
	 */
	public String getName()
	{
		return method.getName();
	}

	/*
	 *  (non-Javadoc)
	 * @see org.csstudio.dal.commands.Command#execute(java.lang.Object[])
	 */
	public Object execute(Object... parameters) throws RemoteException
	{
		try {
			return method.invoke(host, parameters);
		} catch (Throwable e) {
			throw new RemoteException(this, "Unable to execute command", e);
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see org.csstudio.dal.commands.Command#getParameterTypes()
	 */
	public Class[] getParameterTypes()
	{
		return method.getParameterTypes();
	}

	/*
	 *  (non-Javadoc)
	 * @see org.csstudio.dal.commands.Command#getOwner()
	 */
	public CommandContext getOwner()
	{
		return owner;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.csstudio.dal.commands.Command#getReturnedType()
	 */
	public Class getReturnedType()
	{
		return method.getReturnType();
	}

	public boolean isAsynchronous()
	{
		return false;
	}
} /* __oOo__ */


/* __oOo__ */
