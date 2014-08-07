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

package org.csstudio.dal.proxy;

import org.apache.log4j.Logger;
import org.csstudio.dal.DataExchangeException;
import org.csstudio.dal.ResponseEvent;
import org.csstudio.dal.ResponseListener;


/**
 * Intercepts calls to asynchronous getValue method and makes them
 * synchronous.
 *
 * @author ikriznar
 *
 */
public class GetValueInterceptor<T> implements ResponseListener<T>
{
	private DataExchangeException error;
	private boolean done = false;
	private T value;

	/**
	     * Constructor.
	     */
	public GetValueInterceptor()
	{
		super();
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.ResponseListener#responseReceived(org.csstudio.dal.ResponseEvent)
	 */
	public synchronized void responseReceived(ResponseEvent<T> event)
	{
		done = true;
		value = event.getResponse().getValue();
		notify();
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.ResponseListener#responseError(org.csstudio.dal.ResponseEvent)
	 */
	public synchronized void responseError(ResponseEvent<T> event)
	{
		error = new DataExchangeException(event.getSource(),
			    "Remote call returned error.", event.getResponse().getError());
		done = true;
		notify();
	}

	/**
	 * Executes an asynchronous getValue call and waits for it to finish or timeout.
	 *
	 * @param proxy  Proxy on which to execute getValue
	 *
	 * @return value
	 *
	 * @throws DataExchangeException is thrown if getValue timed out
	 */
	public synchronized T executeAndWait(PropertyProxy<T,?> proxy)
		throws DataExchangeException
	{
		proxy.getValueAsync(this);

		if (!done) {
			try {
				wait(GlobalPlugConfiguration.getGlobalPlugConfiguration()
				    .getDefaultTimeout());
			} catch (Exception e) {
				Logger.getLogger(this.getClass()).error("Unhandled exception.", e);
			}
		}

		if (error != null) {
			throw error;
		}

		if (!done) {
			throw new DataExchangeException(proxy, "Remote call in timeout.");
		}

		return value;
	}
}

/* __oOo__ */
