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

package org.epics.css.dal.impl.test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.context.ConnectionEvent;
import org.epics.css.dal.context.ConnectionListener;


/**
 * <code>ChannelListener</code> ...  DOCUMENT ME!
 *
 * @author <a href="mailto:igor.kriznar@cosylab.com">Igor Kriznar</a>
 *
 * @since Jun 23, 2004.
 */
public class PropertyListener implements ConnectionListener,
	DynamicValueListener, PropertyChangeListener
{
	/** DOCUMENT ME! */
	public int connecting = 0;

	/** DOCUMENT ME! */
	public int connectionFailed = 0;

	/** DOCUMENT ME! */
	public int destroyed = 0;

	/** DOCUMENT ME! */
	public int disconnecting = 0;

	/** DOCUMENT ME! */
	public int initialState = 0;

	/** DOCUMENT ME! */
	public int resumed = 0;

	/** DOCUMENT ME! */
	public int suspended = 0;

	/** DOCUMENT ME! */
	public int connected = 0;

	/** DOCUMENT ME! */
	public int connectionLost = 0;

	/** DOCUMENT ME! */
	public int valueChanged = 0;

	/** DOCUMENT ME! */
	public int timelagStarts = 0;

	/** DOCUMENT ME! */
	public int timelagStops = 0;

	/** DOCUMENT ME! */
	public int timeoutStarts = 0;

	/** DOCUMENT ME! */
	public int timeoutStops = 0;

	/** DOCUMENT ME! */
	public int valueUpdated = 0;
	public int conditionChange = 0;
	public int errorResponse = 0;
	public int ready = 0;
	public int disconnected = 0;
	private int operational = 0;

	/** DOCUMENT ME! */
	public Map characteristics = null;

	public PropertyChangeEvent event;

	public DynamicValueCondition lastCondition;


	/**
	 * DOCUMENT ME!
	 */
	public void reset()
	{
		connecting = 0;
		connectionFailed = 0;
		destroyed = 0;
		disconnecting = 0;
		initialState = 0;
		resumed = 0;
		suspended = 0;
		connected = 0;
		operational = 0;
		connectionLost = 0;
		valueChanged = 0;
		timelagStarts = 0;
		timelagStops = 0;
		timeoutStarts = 0;
		timeoutStops = 0;
		valueUpdated = 0;
		conditionChange = 0;
		errorResponse = 0;
		ready = 0;
		disconnected = 0;
		event=null;
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		event=evt;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DynamicValueListener#conditionChange(org.epics.css.dal.DynamicValueEvent)
	 */
	public void conditionChange(DynamicValueEvent event)
	{
		conditionChange++;
		lastCondition= event.getCondition();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DynamicValueListener#errorResponse(org.epics.css.dal.DynamicValueEvent)
	 */
	public void errorResponse(DynamicValueEvent event)
	{
		errorResponse++;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DynamicValueListener#timelagStarts(org.epics.css.dal.DynamicValueEvent)
	 */
	public void timelagStarts(DynamicValueEvent event)
	{
		timelagStarts++;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DynamicValueListener#timelagStops(org.epics.css.dal.DynamicValueEvent)
	 */
	public void timelagStops(DynamicValueEvent event)
	{
		timelagStops++;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DynamicValueListener#timeoutStarts(org.epics.css.dal.DynamicValueEvent)
	 */
	public void timeoutStarts(DynamicValueEvent event)
	{
		timelagStarts++;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DynamicValueListener#timeoutStops(org.epics.css.dal.DynamicValueEvent)
	 */
	public void timeoutStops(DynamicValueEvent event)
	{
		timeoutStops++;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DynamicValueListener#valueChanged(org.epics.css.dal.DynamicValueEvent)
	 */
	public void valueChanged(DynamicValueEvent event)
	{
		//System.err.println(">>> "+event.getValue());
		//Thread.dumpStack();
		valueChanged++;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DynamicValueListener#valueUpdated(org.epics.css.dal.DynamicValueEvent)
	 */
	public void valueUpdated(DynamicValueEvent event)
	{
		valueUpdated++;

		synchronized (this) {
			notifyAll();
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.ConnectionListener#connecting(org.epics.css.dal.context.ConnectionEvent)
	 */
	public void connecting(ConnectionEvent e)
	{
		connecting++;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.ConnectionListener#connectionFailed(org.epics.css.dal.context.ConnectionEvent)
	 */
	public void connectionFailed(ConnectionEvent e)
	{
		connectionFailed++;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.ConnectionListener#destroyed(org.epics.css.dal.context.ConnectionEvent)
	 */
	public void destroyed(ConnectionEvent e)
	{
		destroyed++;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.ConnectionListener#disconnecting(org.epics.css.dal.context.ConnectionEvent)
	 */
	public void disconnecting(ConnectionEvent e)
	{
		disconnecting++;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.ConnectionListener#initialState(org.epics.css.dal.context.ConnectionEvent)
	 */
	public void initialState(ConnectionEvent e)
	{
		initialState++;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.ConnectionListener#ready(org.epics.css.dal.context.ConnectionEvent)
	 */
	public void ready(ConnectionEvent e)
	{
		ready++;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.LinkListener#connected(org.epics.css.dal.context.ConnectionEvent)
	 */
	public void connected(ConnectionEvent e)
	{
		connected++;
	}
	
	public void operational(ConnectionEvent e) {
		operational++;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.LinkListener#connectionLost(org.epics.css.dal.context.ConnectionEvent)
	 */
	public void connectionLost(ConnectionEvent e)
	{
		connectionLost++;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.LinkListener#disconnected(org.epics.css.dal.context.ConnectionEvent)
	 */
	public void disconnected(ConnectionEvent e)
	{
		disconnected++;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.LinkListener#resumed(org.epics.css.dal.context.ConnectionEvent)
	 */
	public void resumed(ConnectionEvent e)
	{
		resumed++;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.LinkListener#suspended(org.epics.css.dal.context.ConnectionEvent)
	 */
	public void suspended(ConnectionEvent e)
	{
		suspended++;
	}
}

/* __oOo__ */
