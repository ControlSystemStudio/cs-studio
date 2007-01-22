/*
 * Copyright (c) 2006 by Cosylab d.o.o.
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

package org.epics.css.dal.epics;

import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.TimeoutException;
import gov.aps.jca.configuration.DefaultConfiguration;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;
import gov.aps.jca.event.ContextExceptionEvent;
import gov.aps.jca.event.ContextExceptionListener;
import gov.aps.jca.event.ContextMessageEvent;
import gov.aps.jca.event.ContextMessageListener;
import gov.aps.jca.event.ContextVirtualCircuitExceptionEvent;
import gov.aps.jca.event.QueuedEventDispatcher;

import java.util.Iterator;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import org.epics.css.dal.EventSystemListener;
import org.epics.css.dal.SimpleProperty;
import org.epics.css.dal.Timestamp;
import org.epics.css.dal.context.ConnectionException;
import org.epics.css.dal.context.PlugEvent;
import org.epics.css.dal.context.RemoteInfo;
import org.epics.css.dal.impl.PropertyUtilities;
import org.epics.css.dal.proxy.AbstractPlug;
import org.epics.css.dal.proxy.DeviceProxy;
import org.epics.css.dal.proxy.DirectoryProxy;
import org.epics.css.dal.proxy.PropertyProxy;

import com.cosylab.epics.caj.CAJContext;

/**
 * Implementation of EPICS plugin.
 * 
 * @author ikriznar
 */
public class EPICSPlug extends AbstractPlug 
	implements ContextMessageListener, ContextExceptionListener {

	/**
	 * Wrapper class of <code>Runnable</code> to <code>TimerTask</code>.
	 */
	class ScheduledTask extends TimerTask {
		private Runnable r;

		public ScheduledTask(Runnable r) {
			this.r = r;
		}

		public void run() {
			try {
				r.run();
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}

	/**
	 * Plug type string.
	 */
	public static final String PLUG_TYPE = "EPICS";

	/**
	 * Plug scheme suffix.
	 */
	public static final String SCHEME_SUFFIX = "EPICS";

	/**
	 * Default authority.
	 */
	public static final String DEFAULT_AUTHORITY = "DEFAULT";

	/**
	 * Timer instance (used for on-time monitors).
	 */
	private Timer timer;

	/**
	 * PendIO timeout.
	 * TODO to be configurable
	 */
	private double timeout = 5.0;

	/**
	 * CAJ context.
	 */
	private CAJContext context;

	/**
	 * Create EPICS plug instance.
	 * @param configuration
	 */
	private EPICSPlug(Properties configuration) {
		super(configuration);
		initialize();
	}
	
	/**
	 * Create new EPICS plug instance.
	 * @param configuration
	 * @return
	 * @throws Exception
	 */
	public static AbstractPlug getInstance(Properties configuration) throws Exception {
		// always create new instance (to separete contexts).
		return new EPICSPlug(configuration);
	}
	
	
	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractPlug#releaseInstance()
	 */
	public void releaseInstance() throws Exception {
		if (context != null)
			context.destroy();
	}

	/**
	 * Initialize EPICS plug.
	 */
	private void initialize() {
		try {
			DefaultConfiguration edconf = new DefaultConfiguration("event_dispatcher");
			edconf.setAttribute("class", QueuedEventDispatcher.class.getName());

			DefaultConfiguration config = new DefaultConfiguration("EPICSPlugConfig");
		    config.setAttribute("class", JCALibrary.CHANNEL_ACCESS_JAVA);
			config.addChild(edconf);
		    
			// create context
		    context = (CAJContext)JCALibrary.getInstance().createContext(config);
		    
			// force explicit initialization
			context.initialize();

			// register all context listeners
			context.addContextExceptionListener(this);
			context.addContextMessageListener(this);
			
			// initialize supported proxy implementation
			PlugUtilities.initializeSupportedProxyImplementations(this);
			
		} catch (Throwable th) {
			th.printStackTrace();
			// rethrow to abort EPICS plug instance creation
			throw new RuntimeException("Failed to initilze EPICS plug", th);
		}
	}

	/**
	 * Timer lazy initialization pattern.
	 * @return timer instance.
	 */
	private synchronized Timer getTimer()
	{
		if (timer == null)
			timer = new Timer("SimulatorPlugTimer");

		return timer;
	}
	
	/**
	 * Schedule task for execution.
	 * @param r ask to be scheduled.
	 * @param delay delay in milliseconds before task is to be executed.
	 * @param rate reschedule perion, if <code>0</code> periodic rescheduling is disabled.
	 * @return <code>TimerTask</code> instance, used to cancel the task scheduling.
	 */
	public TimerTask schedule(Runnable r, long delay, long rate) {

		ScheduledTask t = new ScheduledTask(r);
		
		if (rate > 0) {
			getTimer().scheduleAtFixedRate(t, delay, rate);
		} else {
			getTimer().schedule(t, delay);
		}
		return t;
	}

	/*
	 * @see org.epics.css.dal.proxy.AbstractPlug#getPropertyImplementationClass(java.lang.String)
	 */
	@Override
	public Class<? extends SimpleProperty> getPropertyImplementationClass(String propertyName) {

		class ConnectionListenerImpl implements ConnectionListener {
			/*
			 * @see gov.aps.jca.event.ConnectionListener#connectionChanged(gov.aps.jca.event.ConnectionEvent)
			 */
			public synchronized void connectionChanged(ConnectionEvent event) {
				this.notifyAll();
			}
		}
		
		// create channel
		Channel channel = null;
		ConnectionListenerImpl listener = new ConnectionListenerImpl();
		try {
			synchronized (listener) {
				channel = this.getContext().createChannel(propertyName, listener);
				listener.wait((long)(timeout*1000));
			}
	
			// if not connected this will throw exception
			DBRType type = channel.getFieldType();
			int elementCount = channel.getElementCount();
			
			return PlugUtilities.getPropertyImplForDBRType(type, elementCount);
			
		} catch (Throwable th) {
			throw new RuntimeException("Failed create CA channel to detemrine channel type.", th);
		}
		finally {
			if (channel != null && channel.getConnectionState() != Channel.CLOSED)
				channel.dispose();
		}
	}
	
	/*
	 * @see org.epics.css.dal.proxy.AbstractPlug#getPropertyImplementationClass(java.lang.Class)
	 */
	@Override
	public Class<? extends SimpleProperty> getPropertyImplementationClass(Class<? extends SimpleProperty> type, String propertyName) {
		if (type != null)
			return PropertyUtilities.getImplementationClass(type);
		else
			return getPropertyImplementationClass(propertyName); 
	}
	
	/*
	 * @see org.epics.css.dal.proxy.AbstractPlug#getPropertyProxyImplementationClass(java.lang.String)
	 */
	@Override
	public Class<? extends PropertyProxy> getPropertyProxyImplementationClass(String propertyName) {
		throw new RuntimeException("Unsupported property type.");
	}
	
	/*
	 * @see org.epics.css.dal.proxy.AbstractPlug#createNewPropertyProxy(java.lang.String, java.lang.Class)
	 */
	protected <TT extends PropertyProxy> TT createNewPropertyProxy(
			String uniqueName, Class<TT> type) throws ConnectionException {
		try {
			PropertyProxy p = type.getConstructor(EPICSPlug.class, String.class).newInstance(this, uniqueName);
			// add to directory cache
			if (p instanceof DirectoryProxy)
				putDirectoryProxyToCache((DirectoryProxy) p);
			return type.cast(p);
		} catch (Exception e) {
			throw new ConnectionException(this,
					"Failed to instantiate property proxy '" + uniqueName
							+ "' for type '" + type.getName() + "'.", e);
		}
	}

	/*
	 * @see org.epics.css.dal.proxy.AbstractPlug#getPlugType()
	 */
	public String getPlugType() {
		return "EPICS";
	}

	/*
	 * @see org.epics.css.dal.proxy.AbstractPlug#createNewDirectoryProxy(java.lang.String)
	 */
	protected DirectoryProxy createNewDirectoryProxy(String uniqueName)
		throws ConnectionException {
		// directory is already added to cache in createNewPropertyProxy method
		throw new RuntimeException("Error in factory implementation, PropertyProxy must be created first.");
	}

	/*
	 * @see org.epics.css.dal.proxy.AbstractPlug#createNewDeviceProxy(java.lang.String, java.lang.Class)
	 */
	protected <T extends DeviceProxy> T createNewDeviceProxy(String uniqueName,
			Class<T> type) throws ConnectionException {
		throw new UnsupportedOperationException("Devices not supported");
	}

	/**
	 * @see org.epics.css.dal.context.PlugContext#createRemoteInfo(java.lang.String)
	 */
	public RemoteInfo createRemoteInfo(String uniqueName) throws NamingException {
		return new RemoteInfo(uniqueName, DEFAULT_AUTHORITY, PLUG_TYPE);
	}

	/**
	 * @see org.epics.css.dal.context.PlugContext#getDefaultDirectory()
	 */
	public DirContext getDefaultDirectory() {
		// TODO implement
		return null;
	}

	/*
	 * @see Context.flushIO(double)
	 */
	public void flushIO() {
		try {
			// CAJ will take care of optimization
			context.flushIO();
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

	/*
	 * @see Context.pendIO(double)
	 */
	public void pendIO() throws CAException, TimeoutException {
		context.pendIO(timeout);
	}

	/*
	 * @see Context
	 */
	public Context getContext() {
		return context;
	}

	/* (non-Javadoc)
	 * @see gov.aps.jca.event.ContextExceptionListener#contextException(gov.aps.jca.event.ContextExceptionEvent)
	 */
	@SuppressWarnings("unchecked")
	public void contextException(ContextExceptionEvent ev) {

		if (plugListeners == null)
			return;

		synchronized (plugListeners) {
			if (plugListeners.isEmpty())
				return;
			
			PlugEvent<ContextExceptionEvent> event =
				new PlugEvent<ContextExceptionEvent>(this, ev, new Timestamp(), "Context exception", null, ContextExceptionEvent.class);
			
			Iterator<EventSystemListener<PlugEvent>> iter = plugListeners.iterator();
			while (iter.hasNext()) {
				iter.next().errorArrived(event);
			}
		}
	}

	/* (non-Javadoc)
	 * @see gov.aps.jca.event.ContextExceptionListener#contextVirtualCircuitException(gov.aps.jca.event.ContextVirtualCircuitExceptionEvent)
	 */
	@SuppressWarnings("unchecked")
	public void contextVirtualCircuitException(ContextVirtualCircuitExceptionEvent ev) {

		if (plugListeners == null)
			return;

		synchronized (plugListeners) {
			if (plugListeners.isEmpty())
				return;
			
			PlugEvent<ContextVirtualCircuitExceptionEvent> event =
				new PlugEvent<ContextVirtualCircuitExceptionEvent>(this, ev, new Timestamp(), "Context virtual circuit exception", null, ContextVirtualCircuitExceptionEvent.class);
			
			Iterator<EventSystemListener<PlugEvent>> iter = plugListeners.iterator();
			while (iter.hasNext()) {
				iter.next().eventArrived(event);
			}
		}
	}

	/* (non-Javadoc)
	 * @see gov.aps.jca.event.ContextMessageListener#contextMessage(gov.aps.jca.event.ContextMessageEvent)
	 */
	@SuppressWarnings("unchecked")
	public void contextMessage(ContextMessageEvent ev) {

		if (plugListeners == null)
			return;

		synchronized (plugListeners) {
			if (plugListeners.isEmpty())
				return;
			
			PlugEvent<ContextMessageEvent> event =
				new PlugEvent<ContextMessageEvent>(this, ev, new Timestamp(), "Context message", null, ContextMessageEvent.class);
			
			Iterator<EventSystemListener<PlugEvent>> iter = plugListeners.iterator();
			while (iter.hasNext()) {
				iter.next().eventArrived(event);
			}
		}
	}

	
}


