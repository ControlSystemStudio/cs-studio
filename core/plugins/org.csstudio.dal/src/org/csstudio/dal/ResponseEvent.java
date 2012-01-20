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

import java.util.EventObject;


/**
 * A base class for events used in asynchronous mode notifications.
 * Whenever a class declares the <code>AsynchronousAccess</code> interface, it
 * must provide listener registration and deregistration methods for
 * <code>ResponseListeenrs</code> to which events of this type are delivered.
 * These events contain the reference to the request object for which they are
 * being dispatched, along with the associated response that is being deliverd
 * by this event notification.<p>This class must be subclassed to have its
 * abstract method implemented; in  addition, the subtype may provide
 * additional access to the containing request /  response pair or other
 * functions specific to the underlying implementation.</p>
 */
public class ResponseEvent<T> extends EventObject
{
	private static final long serialVersionUID = 1L;
	/**
	 * Variable holding a reference to the request object. The request
	 * contained by this field is the request for which a new response is
	 * being delivered.
	 */
	protected Request<T> request = null;
	protected Response<T> response = null;

	/**
	     * Creates a new instance of the event, by specifying the
	     * <code>AsynchronousAccess</code> source that generated the event and the
	     * request object which caused the notification to occur.
	     *
	     * @param source the source firing the event
	     * @param req request the status of which has changed
	     */
	public ResponseEvent(Object source, Request<T> req, Response<T> res)
	{
		super(source);
		assert (req != null);
		this.request = req;
		response = res;
	}

	/**
	 * Returns the request specified as a constructor parameter. This
	 * event instance is delivering a response for the request returned by
	 * this  method.
	 *
	 * @return Object the request object
	 */
	public Request<T> getRequest()
	{
		return request;
	}

	/**
	 * Implementations of this method must return the response object
	 * that is  causing this notification to be delivered. The response object
	 * should  contain the reason for this event, for example new value
	 * delivered, timeout, error, successful completion etc.
	 *
	 * @return Object the response object that is causing this event
	 */
	public Response<T> getResponse()
	{
		return response;
	}

	/**
	 * Returns <code>true</code> if this event is the last event in the
	 * series. In  other words, <code>true</code> indicates that no more
	 * events are forthcomming from the given request.
	 *
	 * @return <code>true</code> if no more events will be delivered for the
	 *         request contained in this event
	 */
	public boolean isLast()
	{
		return response.isLast();
	}
}

/* __oOo__ */
