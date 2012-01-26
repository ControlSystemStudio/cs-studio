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

package org.csstudio.dal.context;

import java.util.EventObject;


/**
 * A simple JavaBeans compatible event dispatched to
 * <code>ConnectionListeners</code> and <code>LinkListeners</code>.
 *
 * @author Gasper Tkacik
 *
 * @param <C> type extending Linkable
 */
public class ConnectionEvent<C extends Linkable> extends EventObject
{
	private static final long serialVersionUID = 6050731671839362554L;
	protected ConnectionState state;
	private Throwable error = null;

	/**
	 * Creates a new <code>ConnectionEvent</code>.
	 *
	 * @param source the <code>Connectable</code> instance reporting its change
	 *        in status
	 */
	public ConnectionEvent(C source, ConnectionState state)
	{
		this(source, state, null);
	}

	/**
	     * Creates a new <code>ConnectionEvent</code> with error.
	     *
	     * @param source the <code>Connectable</code> instance reporting its change
	     *        in status
	     * @param error an error which migth occured during connection process, can
	     *        be <code>null</code>
	     */
	public ConnectionEvent(C source, ConnectionState state, Throwable error)
	{
		super(source);

		this.error = error;
		this.state = state;

		if (state == null) {
			throw new NullPointerException("state");
		}
	}

	/**
	 * Returns source as <code>Connectable</code>.
	 *
	 * @return source as <code>Connectable</code>
	 */
	@SuppressWarnings("unchecked")
	public C getConnectable()
	{	
		return (C)getSource();
	}

	/**
	 * Exception which migth occured during connection process,
	 * <code>null</code> if no exception.
	 *
	 * @return na exception which migth occured during connection process
	 */
	public Throwable getError()
	{
		return error;
	}

	/**
	 * Returns state published by this event.
	 *
	 * @return new state
	 */
	public ConnectionState getState()
	{
		return state;
	}
	
	@Override
	public String toString() {
		StringBuilder sb= new StringBuilder(128);
		sb.append(this.getClass().getName());
		sb.append('{');
		sb.append(getConnectable().getIdentifier().getUniqueName());
		sb.append(", ");
		sb.append(getState());
		sb.append('}');
		return sb.toString();
	}
}

/* __oOo__ */
