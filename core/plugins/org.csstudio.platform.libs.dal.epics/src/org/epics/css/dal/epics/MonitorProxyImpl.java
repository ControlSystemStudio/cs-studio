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
package org.epics.css.dal.epics;

import gov.aps.jca.CAException;
import gov.aps.jca.Monitor;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.TIME;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;

import java.util.Map;
import java.util.TimerTask;

import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.ExpertMonitor;
import org.epics.css.dal.RemoteException;
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
public class MonitorProxyImpl<T> extends RequestImpl<T> implements MonitorProxy,
		Runnable, MonitorListener, ExpertMonitor {

	/**
	 * Default timer trigger (in ms).
	 */
	protected static final long DEFAULT_TIMER_TRIGGER = 1000;

	/**
	 * Property (parent) proxy impl.
	 */
	protected PropertyProxyImpl<T> proxy;

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
	 * Last received value add timestamp.
	 */
	protected ResponseImpl<T> response;

	/**
	 * CA monitor implementation.
	 */
	protected Monitor monitor;

	/**
	 * EPICS plug.
	 */
	protected EPICSPlug plug;
	
	/**
	 * Special parameters for expert monitor
	 */
	protected Map<String,Object> parameters;

	/**
	 * Creates new instance.
	 * 
	 * @param proxy parent proxy object
	 * @param l listener for notifications
	 */
	public MonitorProxyImpl(EPICSPlug plug, PropertyProxyImpl<T> proxy, ResponseListener<T> l, Map<String,Object> param)
			throws CAException {
		super(proxy, l);
		this.proxy = proxy;
		this.plug = plug;
		this.parameters=param;
		
		int mask= plug.getDefaultMonitorMask();
		
		if (param!=null && param.get(EPICSPlug.PARAMETER_MONITOR_MASK) instanceof Integer) {
			mask= (Integer)param.get(EPICSPlug.PARAMETER_MONITOR_MASK);
		}
		
		monitor = proxy.getChannel().addMonitor(
				PlugUtilities.toTimeDBRType(proxy.getType()),
				proxy.getChannel().getElementCount(),
				mask, this);
		plug.flushIO();

		proxy.addMonitor(this);
		
		resetTimer();
	}

	/*
	 * @see org.epics.css.dal.proxy.MonitorProxy#getRequest()
	 */
	public Request<T> getRequest() {
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
		if (destroyed || proxy == null || response == null || response.getValue() == null
				|| proxy.getConnectionState() != ConnectionState.CONNECTED) {
			return;
		}
		
		final ResponseImpl<T> r= response;
		proxy.getExecutor().execute(new Runnable() {
			public void run() {
				addResponse(r);
			}
		});
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
			} catch (RuntimeException e) {
				//might happen in the CA - disconnect monitor and it should eventually be gc
				throw e;
			} finally {
				monitor.removeMonitorListener(this);
				destroyed = true;
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
		
		if(dbr==null || dbr.getValue()==null) {
			
			proxy.updateWithDBR(dbr);
			
			final ResponseImpl<T> r= response= new ResponseImpl<T>(proxy, this, null,
				        "value", false, new NullPointerException("Invalid value."), proxy.getCondition(), new Timestamp(), false);

			proxy.getExecutor().execute(new Runnable() {
				public void run() {
					addResponse(r);
				}
			});
			
			return;
		}
		
		proxy.updateWithDBR(dbr);
		// this has been moved to PropertyProxyImpl.updateWithDBR(DBR)
//		if (dbr.isSTS()) {
//			proxy.updateConditionWithDBRStatus((STS) dbr);
//		}

		response= new ResponseImpl<T> (
				proxy, 
				this, 
				proxy.toJavaValue(dbr), 
				"value", 
				true, 
				null, 
				proxy.getCondition(), 
				(dbr.isTIME() && ((TIME) dbr).getTimeStamp() != null) ? 
						PlugUtilities.convertTimestamp(((TIME) dbr).getTimeStamp()): 
							new Timestamp(),
				false);
		
		// notify 
		fireValueChange();
	}

	/*
	 * @see org.epics.css.dal.proxy.MonitorProxy#refresh()
	 */
	public void refresh() {
		// noop
	}
	
	public Map<String, Object> getParameters() {
		return parameters;
	}
	
	public void setParameters(Map<String, Object> param) throws RemoteException {
		// not really supported, can we change parameters of existing monitor?
	}

	protected void addFallbackResponse(T defaultValue) {
		
		final ResponseImpl<T> r= response= new ResponseImpl<T>(proxy, this, defaultValue,
				"value", true, null, proxy.getCondition(), new Timestamp(), false);

		proxy.getExecutor().execute(new Runnable() {
			public void run() {
				addResponse(r);
			}
		});
	}

}

/* __oOo__ */
