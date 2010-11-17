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
import gov.aps.jca.Monitor;
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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.pvs.DALPropertyFactoriesProvider;
import org.epics.css.dal.EventSystemListener;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.SimpleProperty;
import org.epics.css.dal.Timestamp;
import org.epics.css.dal.context.AbstractApplicationContext;
import org.epics.css.dal.context.ConnectionException;
import org.epics.css.dal.context.PlugEvent;
import org.epics.css.dal.device.AbstractDevice;
import org.epics.css.dal.impl.DoublePropertyImpl;
import org.epics.css.dal.impl.PropertyUtilities;
import org.epics.css.dal.proxy.AbstractPlug;
import org.epics.css.dal.proxy.DeviceProxy;
import org.epics.css.dal.proxy.DirectoryProxy;
import org.epics.css.dal.proxy.PropertyProxy;
import org.epics.css.dal.simple.RemoteInfo;
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
	 * Parameter name for expert monitor creation. 
	 * Value is of type Integer and provides mask value for EPICS monitor creation.
	 */
	public static final String PARAMETER_MONITOR_MASK = "EPICSPlug.monitor.mask";
	
	/**
	 * Property name for default pendIO timeout property. 
	 * Value is of type Double and provides the default timeout for pendIO.
	 */
	public static final String DEFAULT_PENDIO_TIMEOUT = "EPICSPlug.default_pendIO_timeout";
	
	/**
	 * Property name for default monitor property. 
	 * Value is of type Integer and provides mask value for default EPICS monitor creation.
	 */
	public static final String DEFAULT_MONITOR_MASK = "EPICSPlug.default_monitor_mask";
	
	/**
	 * Property name for default property implementation class that is used when
	 * implementation class can not be determined because channel is not connected. 
	 * Value is of type String and must represent a fully qualified name of a property 
	 * implementation class.
	 */
	public static final String DEFAULT_PROPERTY_IMPL_CLASS = "EPICSPlug.default_property_impl_class";
	
	private static final Class<? extends SimpleProperty<?>> DEFAULT_PROP_IMPL_CLASS = DoublePropertyImpl.class;
	
	/**
	 * Property name for JNI flush timer delay.
	 * The default value is 100 ms and it is overridden if provided in the configuration.
	 * Property defined in System properties take precedence before property in defined in configuration.
	 */
	public static final String JNI_FLUSH_TIMER_DELAY = "EPICSPlug.jni_flush_timer_delay";
	
	/**
	 * Property name for initialization of characteristics on connect.
	 * The default value is true and it is overridden if provided in the configuration.
	 */
	public static final String INITIALIZE_CHARACTERISTICS_ON_CONNECT = "EPICSPlug.initialize_characteristics_on_connect";
	
	/**
	 * Defines if characteristics should be initialized on connect event.
	 */
	private boolean initializeCharacteristicsOnConnect;
	
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

	private static final Double DEFAULT_PENDIO_TIMEOUT_VALUE = 1.0;
	
	/*
	 * Timeout for calling PendIO. 
	 * Units are seconds.
	 */
	private double pendIOTimeout = DEFAULT_PENDIO_TIMEOUT_VALUE;
	
	/*
	 * Timeout for various operations.
	 * It is configured trough system property defined by org.epics.css.dal.spi.Plugs.CONNECTION_TIMEOUT.
	 * Units are seconds.
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
	 * Flag that indicates if JNI is used.
	 */
	private boolean use_jni = false;
	
	/**
	 * Default monitor mask used for creation of monitors.
	 */
	private int defaultMonitorMask = Monitor.ALARM | Monitor.VALUE;
	
	/**
	 * Default property implementation class that is used because it can not 
	 * be determined on a channel that is not connected.
	 */
	private Class<? extends SimpleProperty<?>> defaultPropertyImplClass = DEFAULT_PROP_IMPL_CLASS;
	
	/**
	 * If JNI is used, this flag indicates if <code>flushIO</code> method has been
	 * called and flushIO should be called on context on next run of
	 * <code>jniFlushTimer</code>.
	 */
	private boolean jniFlushIO = false;
	
	/**
	 * Timer that is used for flushingIO when JNI is used.
	 */
	private Timer jniFlushTimer;
	
	/**
	 * Delay for <code>jniFlushTimer</code> that is used for flushingIO when JNI
	 * is used.
	 */
	private long jniFlushTimerDelay = 100;
	
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
	public static synchronized AbstractPlug getInstance(Properties configuration) throws Exception {
		if (sharedInstance == null) {
			sharedInstance = new EPICSPlug(configuration);
		}
		return sharedInstance;
	}
	
	public static boolean hasInstance(){
		return sharedInstance!=null;
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
	
		initializeCharacteristicsOnConnect = getBooleanProperty(INITIALIZE_CHARACTERISTICS_ON_CONNECT, "true");
		
		useCommonExecutor = getBooleanProperty(PROPERTY_USE_COMMON_EXECUTOR, "false");
		
		coreThreads = getIntegerProperty(PROPERTY_CORE_THREADS, "2");

		maxThreads =  getIntegerProperty(PROPERTY_MAX_THREADS, "10");
		
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

		
		defaultMonitorMask =  getIntegerProperty(DEFAULT_MONITOR_MASK, Integer.toString(defaultMonitorMask));
		
		String className = getStringProperty(DEFAULT_PROPERTY_IMPL_CLASS);
		if (className != null) {
			try {
				defaultPropertyImplClass = (Class<? extends SimpleProperty<?>>) Class.forName(className);
			} catch (Exception e) {
				defaultPropertyImplClass = DEFAULT_PROP_IMPL_CLASS;
			}
		}
		else defaultPropertyImplClass = DEFAULT_PROP_IMPL_CLASS;
		Properties configuration = DALPropertyFactoriesProvider.getInstance().getApplicationContext().getConfiguration();
		Enumeration<Object> keys = System.getProperties().keys(); 
		while (keys.hasMoreElements()) {
			Object object = (Object) keys.nextElement();
			System.out.println(object.toString());
		}
		
		Enumeration<Object> keys2 = configuration.keys();
		while (keys2.hasMoreElements()) {
			String object = (String) keys2.nextElement();
			System.out.println(object.toString() + " \t " + configuration.getProperty(object));
		}
		Enumeration<Object> keys3 = getConfiguration().keys();
		while (keys3.hasMoreElements()) {
			String object = (String) keys3.nextElement();
			System.out.println(object.toString() + " \t " + getConfiguration().getProperty(object));
		}
		use_jni = getBooleanProperty(USE_JNI, "false");
		
		if (!use_jni) {
			context = createJCAContext();
		} else {
			System.out.println("> EPICSPlug using JNI");
			context = createThreadSafeContext();
			
			jniFlushTimerDelay = getLongProperty(JNI_FLUSH_TIMER_DELAY, Long.toString(jniFlushTimerDelay));
			
			jniFlushTimer = new Timer();
			jniFlushTimer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					if (jniFlushIO) {
						jniFlushIO = false;
						try {
							getContext().flushIO();
						} catch (Throwable th) {
							th.printStackTrace();
						}
					}
					
				}
			}, jniFlushTimerDelay, jniFlushTimerDelay);
		}
		
		// initialize supported proxy implementation
		PlugUtilities.initializeSupportedProxyImplementations(this);
	
		pendIOTimeout = getDoubleProperty(DEFAULT_PENDIO_TIMEOUT, DEFAULT_PENDIO_TIMEOUT_VALUE.toString());
		
		timeout = Plugs.getConnectionTimeout(getConfiguration(), 10000)/1000.0;
		
	}

	/**
	 * 
	 * @param key
	 *            the Property key.
	 * @param def
	 *            set default null for non default or the default value.
	 * @return if key exist return the Property, if a default set return the
	 *         default otherwise null.
	 */
	public Boolean getBooleanProperty(String key, String def) {
		String temp;
		try {
			if (System.getProperties().containsKey(key)) {
				temp = System.getProperty(key, def);
			} else {
				temp = getConfiguration().getProperty(key,
						def);
			}
		} catch (Exception e) {
			temp = null;
		}
		if(temp==null){
			return null;
		}
		return Boolean.valueOf(temp);
	}

	/**
	 * 
	 * @param key
	 *            the Property key.
	 * @param def
	 *            set default null for non default or the default value.
	 * @return if key exist return the Property, if a default set return the
	 *         default otherwise null.
	 */
	public Double getDoubleProperty(String key, String def) {
		Double temp;
		try{
			if (System.getProperties().containsKey(key)) {
				temp = new Double(System.getProperty(key, def));
			} else {
				temp = new Double(getConfiguration().getProperty(key,
						def));
			}
		} catch (NumberFormatException e) {
			temp = null;
		}

		return temp;
	}
	
	/**
	 * 
	 * @param key
	 *            the Property key.
	 * @param def
	 *            set default null for non default or the default value.
	 * @return if key exist return the Property, if a default set return the
	 *         default otherwise null.
	 */
	public Integer getIntegerProperty(String key, String def) {
		Integer temp;
		try {
			if (System.getProperties().containsKey(key)) {
				temp = new Integer(System.getProperty(key, def));
			} else {
				temp = new Integer(getConfiguration().getProperty(key,
						def));
			}
		} catch (NumberFormatException e) {
			temp = null;
		}
		return temp;
	}

	/**
	 * 
	 * @param key
	 *            the Property key.
	 * @param def
	 *            set default null for non default or the default value.
	 * @return if key exist return the Property, if a default set return the
	 *         default otherwise null.
	 */
	public Long getLongProperty(String key, String def) {
		Long temp;
		try {
			if (System.getProperties().containsKey(key)) {
				temp = new Long(System.getProperty(key, def));
			} else {
				temp = new Long(getConfiguration().getProperty(key,
						def));
			}
		} catch (NumberFormatException e) {
			temp = null;
		}

		return temp;
	}

	public String getStringProperty(String key) {
		String temp;
		if (System.getProperties().containsKey(key)) {
			temp = System.getProperty(key);
		} else {
			temp = getConfiguration().getProperty(key);
		}
		return temp;
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
			
		} catch (IllegalStateException ise) {
			return defaultPropertyImplClass;
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
		return new RemoteInfo(PLUG_TYPE, uniqueName, null, null);
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
		if (use_jni) {
			jniFlushIO = true;
		} else {
			try {
				// CAJ will take care of optimization
				getContext().flushIO();
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}

	/*
	 * @see Context.pendIO(double)
	 */
	public void pendIO() throws CAException, TimeoutException, RemoteException {
		getContext().pendIO(pendIOTimeout);
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
	 * It is configured trough system property defined by org.epics.css.dal.spi.Plugs.CONNECTION_TIMEOUT.
	 * @return timeout (in seconds)
	 */
	public double getTimeout() {
		return timeout;
	}
	
	/**
	 * Gets the default monitor mask.
	 * @return the default monitor mask
	 */
	public int getDefaultMonitorMask() {
		return defaultMonitorMask;
	}
	
	/**
	 * Gets the {@link #initializeCharacteristicsOnConnect} property.
	 * @return <code>true</code> if characteristics should be initialized on connect and <code>false</code> otherwise.
	 */
	public boolean isInitializeCharacteristicsOnConnect() {
		return initializeCharacteristicsOnConnect;
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
			                new LinkedBlockingQueue<Runnable>());
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
	
	@Override
	public Properties getConfiguration() {
		return DALPropertyFactoriesProvider.getInstance().getApplicationContext().getConfiguration();
	}
}


