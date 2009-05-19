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

package org.epics.css.dal.impl;

import org.epics.css.dal.Request;
import org.epics.css.dal.Response;
import org.epics.css.dal.ResponseEvent;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.context.Identifiable;

import java.util.Iterator;
import java.util.LinkedList;


/**
 * Default implementation of request object. It conveniently stores
 * responses up to the capacity and notifies request listener about new
 * responses.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public class RequestImpl<T> implements Request<T>
{
	protected LinkedList<Response<T>> responses;
	protected Identifiable source;
	protected ResponseListener<T> listener = null;
	private int capacity = 1;

	/**
	     * Creates new instance. Default response capacity is 1.
	     * @param source the source of responses
	     * @param l response listener
	     *
	     * @see #getCapacity()
	     */
	public RequestImpl(Identifiable source, ResponseListener<T> l)
	{
		this(source, l, 1);
	}

	/**
	     * Creates new instance with defined capacity for responses.
	     *
	     * @param source the source of reponses
	     * @param l listener
	     * @param capacity number of last responses stored, if 0 all responses are stored.
	     *
	     * @see #getCapacity()
	     */
	public RequestImpl(Identifiable source, ResponseListener<T> l, int capacity)
	{
		if (source == null) {
			throw new NullPointerException("source");
		}

		if (l == null) {
			throw new NullPointerException("l");
		}

		if (capacity < 0) {
			throw new IllegalArgumentException(
			    "Capaciti must be larger than 0, not " + capacity + ".");
		}

		responses = new LinkedList<Response<T>>();
		this.source = source;
		listener = l;
		this.capacity = capacity;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.Request#getSource()
	 */
	public Identifiable getSource()
	{
		return source;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.Request#hasResponse()
	 */
	public boolean hasResponse()
	{
		return responses.size() > 0;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.Request#responses()
	 */
	public Iterator<Response<T>> responses()
	{
		return responses.iterator();
	}

	/**
	 * Adds new response to this request object and dispatches it to
	 * listener.
	 *
	 * @param r new response to be dispatched
	 *
	 * @throws NullPointerException if response is null
	 * @throws IllegalArgumentException if source of response and source of this request is not equal
	 */
	public void addResponse(Response<T> r)
	{
		if (r == null) {
			throw new NullPointerException("r");
		}

		if (r.getSource() != source) {
			throw new IllegalArgumentException(
			    "Can not dispatch response which has different source identifeable.");
		}

		responses.add(r);

		while (capacity>0 && responses.size() > capacity) {
			responses.removeFirst();
		}

		if (listener != null) {
			ResponseEvent<T> e = new ResponseEvent<T>(source, this, r);

			if (r.success()) {
				listener.responseReceived(e);
			} else {
				listener.responseError(e);
			}
		}
		
		// prevent memory leak by releasing listener reference once it is not needed any more. 
		if (r.isLast()) {
			listener=null;
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.Request#isCompleted()
	 */
	public boolean isCompleted()
	{
		if (responses.size() > 0) {
			return responses.getLast().isLast();
		}

		return false;
	}

	/**
	 * Capacity number defines how many of last responses is stored in
	 * this request.  0 means that all are stored.
	 *
	 * @return Returns the capacity.
	 */
	public int getCapacity()
	{
		return capacity;
	}

	public ResponseListener<T> getResponseListener()
	{
		return listener;
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.Request#getFirstResponse()
	 */
	public Response<T> getFirstResponse() {
		return responses.getFirst();
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.Request#getLastResponse()
	 */
	public Response<T> getLastResponse() {
		return responses.getLast();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Response<T>> iterator() {
		return responses();
	}
}

/* __oOo__ */
