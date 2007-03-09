/*
 * Copyright (c) 2005 by Cosylab d.o.o.
 *
 * The full license specifying the redistribution, modification, usage and other
 * rights and obligations is included with the distribution of this project in
 * the file license.html. If the license is not included you may find a copy at
 * http://www.cosylab.com/legal/abeans_license.htm or may write to Cosylab, d.o.o.
 *
 * THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND, NOT EVEN THE
 * IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR OF THIS SOFTWARE, ASSUMES
 * _NO_ RESPONSIBILITY FOR ANY CONSEQUENCE RESULTING FROM THE USE, MODIFICATION,
 * OR REDISTRIBUTION OF THIS SOFTWARE.
 */

/**
 *
 */
package org.epics.css.dal.epics;

import gov.aps.jca.CAException;
import gov.aps.jca.Monitor;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.STS;
import gov.aps.jca.dbr.TIME;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;

import java.util.TimerTask;

import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.Request;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.Timestamp;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.impl.RequestImpl;
import org.epics.css.dal.impl.ResponseImpl;
import org.epics.css.dal.proxy.MonitorProxy;

/**
 * Simulation implementation of MonitorProxy.
 * 
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public class MonitorProxyImpl extends RequestImpl implements MonitorProxy,
		Runnable, MonitorListener {

	/**
	 * Default timer trigger (in ms).
	 */
	protected static final long DEFAULT_TIMER_TRIGGER = 1000;

	/**
	 * Property (parent) proxy impl.
	 */
	protected PropertyProxyImpl proxy;

	/**
	 * Timer trigger (in ms).
	 */
	protected long timerTrigger = 1000;

	/**
	 * Heartbeat flag.
	 */
	protected boolean heartbeat = false;

	/**
	 * Timer task.
	 */
	protected TimerTask task;

	/**
	 * Destroy flag.
	 */
	protected volatile boolean destroyed = false;

	/**
	 * Timestamp of last received value.
	 */
	protected Timestamp timestamp;

	/**
	 * Last received value.
	 */
	protected Object value;

	/**
	 * CA monitor implementation.
	 */
	protected Monitor monitor;

	/**
	 * Value synchronization object.
	 */
	protected Object valueSync = new Object();
	
	/**
	 * EPICS plug.
	 */
	protected EPICSPlug plug;

	/**
	 * Creates new instance.
	 * 
	 * @param proxy parent proxy object
	 * @param l listener for notifications
	 */
	public MonitorProxyImpl(EPICSPlug plug, PropertyProxyImpl proxy, ResponseListener l)
			throws CAException {
		super(proxy, l);
		this.proxy = proxy;
		this.plug = plug;
		
		monitor = proxy.getChannel().addMonitor(
				PlugUtilities.toTimeDBRType(proxy.getType()),
				proxy.getChannel().getElementCount(),
				Monitor.ALARM | Monitor.VALUE, this);
		plug.flushIO();

		proxy.addMonitor(this);
		
		resetTimer();
	}

	/*
	 * @see org.epics.css.dal.proxy.MonitorProxy#getRequest()
	 */
	public Request getRequest() {
		return this;
	}

	/*
	 * @see org.epics.css.dal.SimpleMonitor#getTimerTrigger()
	 */
	public long getTimerTrigger() throws DataExchangeException {
		return timerTrigger;
	}

	/*
	 * @see org.epics.css.dal.SimpleMonitor#setTimerTrigger(long)
	 */
	public void setTimerTrigger(long trigger) throws DataExchangeException,
			UnsupportedOperationException {
		
		// valid trigger check
		if (trigger <= 0)
			throw new IllegalArgumentException("trigger < 0");

		// noop check
		if (trigger == timerTrigger)
			return;
		
		timerTrigger = trigger;
		resetTimer();
	}

	/*
	 * @see org.epics.css.dal.SimpleMonitor#setHeartbeat(boolean)
	 */
	public void setHeartbeat(boolean heartbeat) throws DataExchangeException,
			UnsupportedOperationException {
		
		// noop check
		if (heartbeat == this.heartbeat)
			return;

		this.heartbeat = heartbeat;
		resetTimer();
	}

	/*
	 * @see org.epics.css.dal.SimpleMonitor#isHeartbeat()
	 */
	public boolean isHeartbeat() {
		return heartbeat;
	}

	/*
	 * @see org.epics.css.dal.SimpleMonitor#getDefaultTimerTrigger()
	 */
	public long getDefaultTimerTrigger() throws DataExchangeException {
		return DEFAULT_TIMER_TRIGGER;
	}

	/*
	 * @see org.epics.css.dal.SimpleMonitor#isDefault()
	 */
	public boolean isDefault() {
		return true;
	}

	/**
	 * Fire value event.
	 */
	private void fireValueEvent() {
		
		// noop check
		if (destroyed || proxy == null || value == null
				|| proxy.getConnectionState() != ConnectionState.CONNECTED) {
			return;
		}
		
		synchronized (valueSync)
		{
			ResponseImpl r = new ResponseImpl(proxy, this, value, "value", true,
					null, proxy.getCondition(), timestamp, false);
			addResponse(r);
		}
	}

	/**
	 * Fires value change event if monitor is not in heartbeat mode.
	 */
	public void fireValueChange() {
		if (!heartbeat) {
			fireValueEvent();
		}
	}

	/**
	 * Run method executed at schedulet time intervals.
	 */
	public void run() {
		fireValueEvent();
	}

	/**
	 * Reset timer.
	 */
	private synchronized void resetTimer() {
		
		if (destroyed)
			throw new IllegalStateException("monitor destroyed");
		
		if (task != null) {
			task.cancel();
		}

		if (heartbeat) {
			task = plug.schedule(this, 0, timerTrigger);
		}
	}

	/*
	 * @see org.epics.css.dal.SimpleMonitor#destroy()
	 */
	public synchronized void destroy() {
		
		if (destroyed)
			return;
		
		if (task != null) {
			task.cancel();
		}
		
		proxy.removeMonitor(this);
		
		// destroy remote instance
		if (monitor != null) {
			try {
				monitor.clear();
			} catch (CAException e) {
				// noop
			}
		}
		
		destroyed = true;
	}

	/*
	 * @see org.epics.css.dal.SimpleMonitor#isDestroyed()
	 */
	public boolean isDestroyed() {
		return destroyed;
	}

	/*
	 * @see gov.aps.jca.event.MonitorListener#monitorChanged(gov.aps.jca.event.MonitorEvent)
	 */
	public void monitorChanged(MonitorEvent ev) {

		DBR dbr = ev.getDBR();

		if (dbr.isSTS()) {
			proxy.updateConditionWithDBRStatus((STS) dbr);
		}

		synchronized (valueSync) {
			if (dbr.isTIME()) {
				timestamp = PlugUtilities.convertTimestamp(((TIME) dbr).getTimeStamp());
			} else {
				timestamp = new Timestamp();
			}

			value = proxy.toJavaValue(dbr);
		}
		
		// notify 
		fireValueChange();
	}

	/*
	 * @see org.epics.css.dal.proxy.MonitorProxy#refresh()
	 */
	public void refresh() {
		// noop
	}

}

/* __oOo__ */
