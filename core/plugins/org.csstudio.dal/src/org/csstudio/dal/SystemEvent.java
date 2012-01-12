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
 * General EventSystem event.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 */
public class SystemEvent<T, S> extends EventObject
{
	private static final long serialVersionUID = 1L;
	
	protected Timestamp timestamp;
	protected String message = null;
	protected T value;
	protected Exception error;
	protected Object eventID;
	protected S source;

	/**
	 * Creates new event object.
	 * @param source the source that generated the event
	 * @param value the event value
	 * @param timestamp the timestamp of the event
	 * @param message event message
	 * @param error event exception
	 * @param type the event identification tag
	 */
	public SystemEvent(S source, T value, Timestamp timestamp, String message,
	    Exception error, Object type)
	{
		super(source);
		this.source = source;

		if (timestamp == null) {
			this.timestamp = new Timestamp();
		} else {
			this.timestamp = timestamp;
		}

		this.message = message;
		this.eventID = type;
		this.error = error;
		this.value = value;
	}

	/**
	 * Returns event message.
	 *
	 * @return Event message string
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Returns event timestamp.
	 *
	 * @return Event timestamp
	 */
	public Timestamp getTimestamp()
	{
		return timestamp;
	}

	/**
	 * Returns the event value
	 *
	 * @return Event value
	 */
	public T getValue()
	{
		return value;
	}

	/**
	 * Returns event exception.
	 *
	 * @return Event exception
	 */
	public Exception getError()
	{
		return error;
	}

	/**
	 *
	 *    Returns the eventType.
	 *
	 * @return Returns the eventType.
	 */
	public Object getEventID()
	{
		return eventID;
	}

	/* (non-Javadoc)
	 * @see java.util.EventObject#getSource()
	 */
	@Override
	public S getSource()
	{
		return source;
	}
}

/* __oOo__ */
