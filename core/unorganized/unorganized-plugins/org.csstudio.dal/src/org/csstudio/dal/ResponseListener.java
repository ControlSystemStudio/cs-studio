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

package org.csstudio.dal;

import java.util.EventListener;


/**
 * Each object that implements the <code>AsynchronousAccess</code>
 * interafce, must provide listener registration / deregistration methods for
 * listeners of this type. Whenever an asynchronous method (set / get / actions etc)
 * is invoked, a request object is issued as the return value of that method. Whenever
 * a response is provided for that request by the underlying implementation, all
 * response listeners are notified. If a listener is interested in the completion
 * of a specific request, it must first enquire the event object to see if the event
 * notification is being delivered for that specific request.
 */
public interface ResponseListener<T> extends EventListener
{
	/**
	 * Event notification specifying that the request state has
	 * changed. This happens in all cases where the request is modified by the
	 * underlying implementation.  Examples are: the arrival of new response,
	 * timeout or error condition, successful completion of the request etc.
	 *
	 * @param event the event carrying the new response and the request for
	 *        which the notification is being delivered
	 */
	public void responseReceived(ResponseEvent<T> event);

	/**
	 * Event notification specifying that the response indicates an
	 * error condition. This may mean that either the request as a whole
	 * indicates an error (timeout in response delivery, or error while
	 * submitting a request), or that a single response contains data that
	 * indicate an error response. The implementor of Datatypes sets the
	 * criteria for success / failure. The user should examine the event and
	 * its encapsulated response and request objects to determine the exact
	 * nature of an error.
	 *
	 * @param event the event carrying the request for which the notification
	 *        is being delivered; and possibly a response that indicates an
	 *        error state
	 */
	public void responseError(ResponseEvent<T> event);
}

/* __oOo__ */
