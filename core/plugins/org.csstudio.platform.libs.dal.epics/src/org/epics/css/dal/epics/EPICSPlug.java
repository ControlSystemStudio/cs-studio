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
import gov.aps.jca.jni.ThreadSafeContext;

import java.util.Iterator;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import org.csstudio.platform.logging.CentralLogger;
import org.epics.css.dal.EventSystemListener;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.SimpleProperty;
import org.epics.css.dal.Timestamp;
import org.epics.css.dal.context.AbstractApplicationContext;
import org.epics.css.dal.context.ConnectionException;
import org.epics.css.dal.context.PlugEvent;
import org.epics.css.dal.context.RemoteInfo;
import org.epics.css.dal.device.AbstractDevice;
import org.epics.css.dal.impl.PropertyUtilities;
import org.epics.css.dal.proxy.AbstractPlug;
import org.epics.css.dal.proxy.DeviceProxy;
import org.epics.css.dal.proxy.DirectoryProxy;
import org.epics.css.dal.proxy.PropertyProxy;
import org.epics.css.dal.spi.Plugs;

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
	 * Property name for JCA context type flag.  
	 * If <code>false</code> or not defined then by default CAJ instance of JCA context is used.
	 * If value set to <code>true</code> in System properties or in configuration properties, then JNI (thread safe) 
	 * instance of JCA context is used.
	 * Property defined in System properties take precedence before property in defined in configuration.
	 */
	public static final String USE_JNI = "EPICSPlug.use_jni";
	
	/**
	 * Property name for use common executor flag: {@link #useCommonExecutor}
	 */
	public static final String PROPERTY_USE_COMMON_EXECUTOR = "EPICSPlug.property.use_common_executor";
	
	/**
	 * Property name for core threads property: {@link #coreThreads}
	 * <p>
	 * The number of core threads must be non-negative.
	 * </p>
	 */
	public static final String PROPERTY_CORE_THREADS = "EPICSPlug.property.core_threads";
	
	/**
	 * Property name for max threads property: {@link #maxThreads}
	 * <p>
	 * The number of core threads must be non-negative and greater than the number of core threads.
	 * </p>
	 */
	public static final String PROPERTY_MAX_THREADS = "EPICSPlug.property.max_threads";
	
	/**
	 * Defines if a common <code>Executor</code> from this <code>EPICSPlug</code> should be used instead of
	 * individual <code>Executor<code>s in <code>PropertyProxyImpl</code>s.
	 * 
	 * @see PropertyProxyImpl
	 */
	private boolean useCommonExecutor;
	
	/**
	 * Defines the number of core threads to be used with <code>ThreadPoolExecutor</code> from this
	 * <code>EPICSPlug</code> or <code>PropertyProxyImpl</code>.
	 * 
	 * @see PropertyProxyImpl
	 */
	private int coreThreads;
	
	/**
	 * Defines the maximum number of threads to be used with <code>ThreadPoolExecutor</code> from this
	 * <code>EPICSPlug</code> or <code>PropertyProxyImpl</code>.
	 * 
	 * @see PropertyProxyImpl
	 */
	private int maxThreads;
	
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
	 * Context.
	 */
	private Context context;

	private static EPICSPlug sharedInstance;
	
	/**
	 * <code>ThreadPoolExecutor</code> used by this <code>EPICSPlug</code> if {@link #useCommonExecutor}
	 * is selected.
	 */
	private ThreadPoolExecutor executor;
	
	/**
	 * Create EPICS plug instance.
	 * @param configuration
	 * @throws RemoteException 
	 */
	private EPICSPlug(Properties configuration) throws RemoteException {
		super(configuration);
		initialize();
	}
	
	private EPICSPlug(AbstractApplicationContext context) throws RemoteException {
		super(context);
		initialize();
	}
	
	/**
	 * Create new EPICS plug instance.
	 * @param configuration
	 * @return
	 * @throws Exception
	 */
	public static AbstractPlug getInstance(Properties configuration) throws Exception {
		if (sharedInstance == null) {
			sharedInstance = new EPICSPlug(configuration);
		}
		return sharedInstance;
	}
	
	public static AbstractPlug getInstance(AbstractApplicationContext ctx) throws RemoteException
	{
		return new EPICSPlug(ctx);
	}
	
	
	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractPlug#releaseInstance()
	 */
	public synchronized void releaseInstance() throws Exception {
		if (executor!=null) {
			// TODO is this OK?
			getExecutor().shutdown();
	        try {
	            if (!getExecutor().awaitTermination(1, TimeUnit.SECONDS))
	                getExecutor().shutdownNow();
	        } catch (InterruptedException ie) {  }
		}
		if (context!=null) {
			if (!cachedPropertyProxiesIterator().hasNext()) {
				context.destroy();
				context=null;
			}
		}
		if (sharedInstance==this) {
			sharedInstance=null;
		}
	}

	/**
	 * Initialize EPICS plug.
	 * @throws RemoteException 
	 */
	private void initialize() throws RemoteException {
		useCommonExecutor = false;
		if (System.getProperties().containsKey(PROPERTY_USE_COMMON_EXECUTOR)) {
			useCommonExecutor = new Boolean(System.getProperty(PROPERTY_USE_COMMON_EXECUTOR, "false"));
		} else {
			useCommonExecutor = new Boolean(getConfiguration().getProperty(PROPERTY_USE_COMMON_EXECUTOR, "false"));
		}
		
		coreThreads = 2;
		if (System.getProperties().containsKey(PROPERTY_CORE_THREADS)) {
			coreThreads = new Integer(System.getProperty(PROPERTY_CORE_THREADS, "2"));
		} else {
			coreThreads = new Integer(getConfiguration().getProperty(PROPERTY_CORE_THREADS, "2"));
		}
		
		maxThreads = 10;
		if (System.getProperties().containsKey(PROPERTY_MAX_THREADS)) {
			maxThreads = new Integer(System.getProperty(PROPERTY_MAX_THREADS, "10"));
		} else {
			maxThreads = new Integer(getConfiguration().getProperty(PROPERTY_MAX_THREADS, "10"));
		}
		
		// checks for coreThreads and maxThreads values
		if (maxThreads == 0) { 
			if (coreThreads != 0) {
				System.out.print("> EPICSPlug number of core threads can not be "+coreThreads+". It was changed to ");
				coreThreads = 0;
				System.out.println(coreThreads+".");
			}
		}
		else {
			if (coreThreads < 1) {
				System.out.print("> EPICSPlug number of core threads can not be "+coreThreads+". It was changed to ");
				coreThreads = 1;
				System.out.println(coreThreads+".");
			}
			if (maxThreads < 0 || maxThreads < coreThreads) {
				System.out.print("> EPICSPlug maximum number of threads can not be "+maxThreads+". It was changed to ");
				maxThreads = coreThreads;
				System.out.println(maxThreads+".");
			}
		}
		
		
		boolean use_jni=false;
		if (System.getProperties().containsKey(USE_JNI)) {
			use_jni = new Boolean(System.getProperty(USE_JNI, "false"));
		} else {
			use_jni = new Boolean(getConfiguration().getProperty(USE_JNI, "false"));
		}
		CentralLogger.getInstance().debug(this, "pure java: " + !use_jni);
		if (!use_jni) {
			context = createJCAContext();
		} else {
			context = createThreadSafeContext();
		}

			
		// initialize supported proxy implementation
		PlugUtilities.initializeSupportedProxyImplementations(this);
	
		timeout= Plugs.getConnectionTimeout(getConfiguration(), 10000)/1000.0;
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

	/**
	 * @see org.epics.css.dal.proxy.AbstractPlug#getDeviceImplementationClass(java.lang.String)
	 */
	@Override
	protected Class<? extends AbstractDevice> getDeviceImplementationClass(String uniqueDeviceName) {
		throw new UnsupportedOperationException("Devices not supported");
	}

	/**
	 * @see org.epics.css.dal.proxy.AbstractPlug#getDeviceProxyImplementationClass(java.lang.String)
	 */
	@Override
	protected Class<? extends DeviceProxy> getDeviceProxyImplementationClass(String uniqueDeviceName) {
		throw new UnsupportedOperationException("Devices not supported");
	}

	/*
	 * @see org.epics.css.dal.proxy.AbstractPlug#getPropertyImplementationClass(java.lang.String)
	 */
	@Override
	public Class<? extends SimpleProperty<?>> getPropertyImplementationClass(String propertyName) {

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
			throw new RuntimeException("Failed create CA channel tqo determine channel type.", th);
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
	public Class<? extends SimpleProperty<?>> getPropertyImplementationClass(Class<? extends SimpleProperty<?>> type, String propertyName) throws RemoteException {
		if (type != null)
			return PropertyUtilities.getImplementationClass(type);
		else
			return getPropertyImplementationClass(propertyName); 
		//return super.getPropertyImplementationClass(type, propertyName);
		
	}
	
	/*
	 * @see org.epics.css.dal.proxy.AbstractPlug#getPropertyProxyImplementationClass(java.lang.String)
	 */
	@Override
	public Class<? extends PropertyProxy<?>> getPropertyProxyImplementationClass(String propertyName) {
		throw new RuntimeException("Unsupported property type.");
	}
	
	/*
	 * @see org.epics.css.dal.proxy.AbstractPlug#createNewPropertyProxy(java.lang.String, java.lang.Class)
	 */
	protected <TT extends PropertyProxy<?>> TT createNewPropertyProxy(
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
			getContext().flushIO();
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

	/*
	 * @see Context.pendIO(double)
	 */
	public void pendIO() throws CAException, TimeoutException, RemoteException {
		getContext().pendIO(timeout);
	}

	/*
	 * @see Context
	 */
	public synchronized Context getContext() {
		return context;
	}
	
	private CAJContext createJCAContext() throws RemoteException {
		try {
			DefaultConfiguration edconf = new DefaultConfiguration("event_dispatcher");
			edconf.setAttribute("class", QueuedEventDispatcher.class.getName());

			
			DefaultConfiguration config = new DefaultConfiguration("EPICSPlugConfig");
		    config.setAttribute("class", JCALibrary.CHANNEL_ACCESS_JAVA);
			config.addChild(edconf);
		    
			// create context
		    CAJContext c= (CAJContext)JCALibrary.getInstance().createContext(config);
		    
			// force explicit initialization
			c.initialize();

			// register all context listeners
			c.addContextExceptionListener(this);
			c.addContextMessageListener(this);
			
			return c;

		} catch (Throwable th) {
			th.printStackTrace();
			// rethrow to abort EPICS plug instance creation
			throw new RemoteException(this,"Failed to initilze EPICS plug", th);
		}
	}
	
	private ThreadSafeContext createThreadSafeContext() throws RemoteException {
		try {
			DefaultConfiguration edconf = new DefaultConfiguration("event_dispatcher");
			edconf.setAttribute("class", QueuedEventDispatcher.class.getName());

			
			DefaultConfiguration config = new DefaultConfiguration("EPICSPlugConfig");
			config.setAttribute("class", JCALibrary.JNI_THREAD_SAFE);
			config.addChild(edconf);
		    
			// create context
		    ThreadSafeContext c= (ThreadSafeContext)JCALibrary.getInstance().createContext(config);
		    
			// force explicit initialization
			c.initialize();

			// register all context listeners
			c.addContextExceptionListener(this);
			c.addContextMessageListener(this);
			
			return c;

		} catch (Throwable th) {
			th.printStackTrace();
			// rethrow to abort EPICS plug instance creation
			throw new RemoteException(this,"Failed to initilze EPICS plug", th);
		}
	}

	/**
	 * Get timeout parameter (in seconds).
	 * @return timeout (in seconds)
	 */
	public double getTimeout() {
		return timeout;
	}
	
	/**
	 * Gets {@link #useCommonExecutor} property.
	 * @return <code>true</code> if common executor should be used and <code>false</code> otherwise.
	 */
	public boolean isUseCommonExecutor() {
		return useCommonExecutor;
	}
	
	/**
	 * Gets {@link #coreThreads} property.
	 * @return the number of core threads.
	 */
	public int getCoreThreads() {
		return coreThreads;
	}
	
	/**
	 * Gets {@link #maxThreads} property.
	 * @return the maximum number of threads.
	 */
	public int getMaxThreads() {
		return maxThreads;
	}
	
	/**
	 * This method should be called only if {@link #PROPERTY_USE_COMMON_EXECUTOR} is set to 
	 * <code>true</code>. Also in order to use this method the {@link #PROPERTY_MAX_THREADS}
	 * must be greater than 0.
	 * 
	 * @return a <code>ThreadPoolExecutor</code>
	 * @throws IllegalStateException if useCommonExecutor property is set to <code>false</code>
	 * or maximum number of threads is equal to 0.
	 */
	public ThreadPoolExecutor getExecutor() {
		if (executor==null) {
			synchronized (this) {
				if (!useCommonExecutor) throw new IllegalStateException("EPICSPlug is configured not to use a common executor.");
				if (maxThreads == 0) throw new IllegalStateException("Maximum number of threads must be greater than 0.");
				if (executor==null) {
					executor= new ThreadPoolExecutor(coreThreads,maxThreads,Long.MAX_VALUE, TimeUnit.NANOSECONDS,
			                new ArrayBlockingQueue<Runnable>(maxThreads));
					executor.prestartAllCoreThreads();
				}				
			}
		}
		return executor;
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


