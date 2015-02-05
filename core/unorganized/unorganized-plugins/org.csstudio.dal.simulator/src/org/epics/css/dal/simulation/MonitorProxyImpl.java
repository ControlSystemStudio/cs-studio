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

/**
 *
 */
package org.epics.css.dal.simulation;

import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.csstudio.dal.DataExchangeException;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.Request;
import org.csstudio.dal.ResponseListener;
import org.csstudio.dal.impl.RequestImpl;
import org.csstudio.dal.impl.ResponseImpl;
import org.csstudio.dal.proxy.MonitorProxy;


/**
 * Simulation implementation of MonitorProxy.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public class MonitorProxyImpl<T> extends RequestImpl<T> implements MonitorProxy,
	Runnable
{
	protected PropertyProxyImpl<T> proxy;
	protected long timerTrigger = 1000;
	protected boolean heartbeat = true;
	protected TimerTask task;
	protected boolean destroyed = false;

	/**
	 * Creates new instance.
	 *
	 * @param proxy parent proxy object
	 * @param l listener for notifications
	 */
	public MonitorProxyImpl(PropertyProxyImpl<T> proxy, ResponseListener<T> l)
	{
		super(proxy, l);
		this.proxy = proxy;
		proxy.addMonitor(this);
		resetTimer();
	}

	public void reInitialize(PropertyProxyImpl<T> proxy) throws RemoteException
	{
		this.source = proxy;
		this.proxy = proxy;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.MonitorProxy#getRequest()
	 */
	public Request<T> getRequest()
	{
		return this;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.SimpleMonitor#getTimerTrigger()
	 */
	public long getTimerTrigger() throws DataExchangeException
	{
		return timerTrigger;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.SimpleMonitor#setTimerTrigger(long)
	 */
	public void setTimerTrigger(long trigger)
		throws DataExchangeException, UnsupportedOperationException
	{
		timerTrigger = trigger;
		resetTimer();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.SimpleMonitor#setHeartbeat(boolean)
	 */
	public void setHeartbeat(boolean heartbeat)
		throws DataExchangeException, UnsupportedOperationException
	{
		this.heartbeat = heartbeat;
		resetTimer();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.SimpleMonitor#isHeartbeat()
	 */
	public boolean isHeartbeat()
	{
		return heartbeat;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.SimpleMonitor#getDefaultTimerTrigger()
	 */
	public long getDefaultTimerTrigger() throws DataExchangeException
	{
		return 1000;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.SimpleMonitor#isDefault()
	 */
	public boolean isDefault()
	{
		return true;
	}

	private void fireValueEvent()
	{
		try {
			ResponseImpl<T> r = new ResponseImpl<T>(proxy, this,
				    proxy.getValueSync(), "value", true, null,
				    proxy.getCondition(), null, false);
			addResponse(r);
		} catch (DataExchangeException e) {
			Logger.getLogger(this.getClass()).warn("Simulator error.", e);
		}
	}

	/**
	 * Fires value change event if monitor is not in heartbeat mode.
	 */
	public void fireValueChange()
	{
		if (!heartbeat) {
			fireValueEvent();
		}
	}

	/**
	 * Run method executed at schedulet time intervals.
	 */
	public void run()
	{
		fireValueEvent();
	}

	private synchronized void resetTimer()
	{
		if (task != null) {
			task.cancel();
		}

		if (heartbeat) {
			task = SimulatorPlug.getInstance().schedule(this, timerTrigger);
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.SimpleMonitor#destroy()
	 */
	public void destroy()
	{
		if (task != null) {
			task.cancel();
		}

		destroyed = true;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.SimpleMonitor#isDestroyed()
	 */
	public boolean isDestroyed()
	{
		return destroyed;
	}

	public void refresh()
	{
		// Override in order to clean up cached values.
	}
}

/* __oOo__ */
