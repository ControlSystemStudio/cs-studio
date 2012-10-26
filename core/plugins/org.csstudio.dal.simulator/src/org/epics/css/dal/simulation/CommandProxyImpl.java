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

package org.epics.css.dal.simulation;

import java.lang.reflect.Method;

import org.csstudio.dal.RemoteException;
import org.csstudio.dal.Request;
import org.csstudio.dal.ResponseListener;
import org.csstudio.dal.commands.CommandSupport;
import org.csstudio.dal.impl.RequestImpl;
import org.csstudio.dal.impl.ResponseImpl;
import org.csstudio.dal.proxy.CommandProxy;
import org.csstudio.dal.proxy.DeviceProxy;


/**
 * Default CommandProxy implementation.
 *
 * @author ikriznar
 */
public class CommandProxyImpl extends CommandSupport implements CommandProxy
{
	protected DeviceProxy owner;

	/**
	 * Creates a new CommandProxyImpl object.
	 *
	 * @param owner command parent context
	 * @param host the host commands are called on
	 * @param method the method that the command executes
	 */
	public CommandProxyImpl(DeviceProxy owner, Object host, Method method)
	{
		super(null, host, method);
		this.owner = owner;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.CommandProxy#isAsyncrhonous()
	 */
	public boolean isAsynchronous()
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.CommandProxy#execute(org.epics.css.dal.ResponseListener, java.lang.Object...)
	 */
	public <T> Request<T> execute(ResponseListener<T> callback, Object... parameters)
		throws RemoteException
	{
		RequestImpl<T> r = new RequestImpl<T>(owner, callback);
		Object ret = execute(parameters);
		r.addResponse(new ResponseImpl<T>(owner, r, (T)ret, getName(), true, null,
		        null, null, true));

		return r;
	}
}

/* __oOo__ */
