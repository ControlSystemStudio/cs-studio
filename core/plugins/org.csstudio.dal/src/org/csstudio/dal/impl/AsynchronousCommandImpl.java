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

package org.csstudio.dal.impl;

import org.csstudio.dal.RemoteException;
import org.csstudio.dal.Request;
import org.csstudio.dal.ResponseEvent;
import org.csstudio.dal.ResponseListener;
import org.csstudio.dal.commands.AsynchronousCommand;
import org.csstudio.dal.proxy.CommandProxy;


/**
 * Default asynchronus command implementation.
 */
public class AsynchronousCommandImpl extends CommandImpl
	implements AsynchronousCommand
{
	protected CommandProxy proxy;
	protected AbstractDeviceImpl owner;
	protected ResponseListener<?> defaultResponseListener;

	private class ResponseForwarder implements ResponseListener
	{
		private ResponseListener<?> callback;

		ResponseForwarder(ResponseListener<?> callback)
		{
			this.callback = callback;
		}

		public void responseReceived(ResponseEvent event)
		{
			defaultResponseListener.responseReceived(event);
			callback.responseReceived(event);
		}

		public void responseError(ResponseEvent event)
		{
			defaultResponseListener.responseError(event);
			callback.responseError(event);
		}
	}

	/**
	 * Constructor for asynchronus command
	 * @param p    Command proxy
	 * @param ctx    Device
	 */
	public AsynchronousCommandImpl(CommandProxy p, AbstractDeviceImpl ctx,
	    ResponseListener<?> responseListener)
	{
		super(p, ctx);
		proxy = p;
		owner = ctx;
		this.defaultResponseListener = responseListener;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.commands.AsynchronousCommand#executeAsync(java.lang.Object...)
	 */
	public Request<?> executeAsync(ResponseListener<?> listener, Object... parameters)
		throws RemoteException
	{
		return proxy.execute(listener == null ? defaultResponseListener
		    : new ResponseForwarder(listener), parameters);
	}

	public boolean isAsynchronous()
	{
		return true;
	}
}

/* __oOo__ */
