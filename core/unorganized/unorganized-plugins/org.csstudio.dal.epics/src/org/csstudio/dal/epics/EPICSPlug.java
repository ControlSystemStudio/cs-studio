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

package org.csstudio.dal.epics;

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

import java.util.Iterator;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import org.apache.log4j.Logger;
import org.csstudio.dal.EventSystemListener;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.SimpleProperty;
import org.csstudio.dal.Timestamp;
import org.csstudio.dal.context.AbstractApplicationContext;
import org.csstudio.dal.context.ConnectionException;
import org.csstudio.dal.context.PlugEvent;
import org.csstudio.dal.device.AbstractDevice;
import org.csstudio.dal.impl.DoublePropertyImpl;
import org.csstudio.dal.impl.PropertyUtilities;
import org.csstudio.dal.proxy.AbstractPlug;
import org.csstudio.dal.proxy.DeviceProxy;
import org.csstudio.dal.proxy.DirectoryProxy;
import org.csstudio.dal.proxy.PropertyProxy;
import org.csstudio.dal.simple.RemoteInfo;
import org.csstudio.dal.spi.Plugs;

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
        private final Runnable r;

        public ScheduledTask(final Runnable r) {
            this.r = r;
        }

        @Override
        public void run() {
            try {
                r.run();
            } catch (final Throwable th) {
                Logger.getLogger(this.getClass()).warn("Scheduled task had unhandled error.", th);
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
     * When DBR update comes characteristics with connected with condition change are updated.
     * Default is true.
     */
    public static final String DBR_UPDATES_CHARACTERISTICS = "EPICSPlug.property.dbr_updates_characteristics";

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
     * It is configured trough system property defined by org.csstudio.dal.spi.Plugs.CONNECTION_TIMEOUT.
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

    private boolean dbrUpdatesCharacteristics=true;

    /**
     * Create EPICS plug instance.
     * @param configuration
     * @throws RemoteException
     */
    private EPICSPlug(final Properties configuration) throws RemoteException {
        super(configuration);
        initialize();
    }

    private EPICSPlug(final AbstractApplicationContext context) throws RemoteException {
        super(context);
        initialize();
    }

    /**
     * Create new EPICS plug instance.
     * @param configuration
     * @return
     * @throws Exception
     */
    public static synchronized AbstractPlug getInstance(final Properties configuration) throws Exception {
        if (sharedInstance == null) {
            sharedInstance = new EPICSPlug(configuration);
        }
        return sharedInstance;
    }

    public static AbstractPlug getInstance(final AbstractApplicationContext ctx) throws RemoteException
    {
        return new EPICSPlug(ctx);
    }


    /* (non-Javadoc)
     * @see org.csstudio.dal.proxy.AbstractPlug#releaseInstance()
     */
    @Override
    public synchronized void releaseInstance() throws Exception {
        if (executor!=null) {
            // TODO is this OK?
            getExecutor().shutdown();
            try {
                if (!getExecutor().awaitTermination(1, TimeUnit.SECONDS)) {
                    getExecutor().shutdownNow();
                }
            } catch (final InterruptedException ie) {  }
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
    @SuppressWarnings("unchecked")
    private void initialize() throws RemoteException {
        initializeCharacteristicsOnConnect = true;
        if (System.getProperties().containsKey(INITIALIZE_CHARACTERISTICS_ON_CONNECT)) {
            initializeCharacteristicsOnConnect = new Boolean(System.getProperty(INITIALIZE_CHARACTERISTICS_ON_CONNECT, "true"));
        } else {
            initializeCharacteristicsOnConnect = new Boolean(getConfiguration().getProperty(INITIALIZE_CHARACTERISTICS_ON_CONNECT, "true"));
        }

        useCommonExecutor = false;
        if (System.getProperties().containsKey(PROPERTY_USE_COMMON_EXECUTOR)) {
            useCommonExecutor = new Boolean(System.getProperty(PROPERTY_USE_COMMON_EXECUTOR, "false"));
        } else {
            useCommonExecutor = new Boolean(getConfiguration().getProperty(PROPERTY_USE_COMMON_EXECUTOR, "false"));
        }

        dbrUpdatesCharacteristics = true;
        if (System.getProperties().containsKey(DBR_UPDATES_CHARACTERISTICS)) {
            dbrUpdatesCharacteristics = new Boolean(System.getProperty(DBR_UPDATES_CHARACTERISTICS, "true"));
        } else {
            dbrUpdatesCharacteristics = new Boolean(getConfiguration().getProperty(DBR_UPDATES_CHARACTERISTICS, "true"));
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
                final StringBuilder sb= new StringBuilder(128);
                sb.append("> EPICSPlug number of core threads can not be "+coreThreads+". It was changed to ");
                coreThreads = 0;
                sb.append(coreThreads+".");
                getLogger().warn(sb.toString());
            }
        }
        else {
            if (coreThreads < 1) {
                final StringBuilder sb= new StringBuilder(128);
                sb.append("> EPICSPlug number of core threads can not be "+coreThreads+". It was changed to ");
                coreThreads = 1;
                sb.append(coreThreads+".");
                getLogger().warn(sb.toString());
            }
            if (maxThreads < 0 || maxThreads < coreThreads) {
                final StringBuilder sb= new StringBuilder(128);
                sb.append("> EPICSPlug maximum number of threads can not be "+maxThreads+". It was changed to ");
                maxThreads = coreThreads;
                sb.append(maxThreads+".");
                getLogger().warn(sb.toString());
            }
        }

        if (System.getProperties().containsKey(DEFAULT_MONITOR_MASK)) {
            defaultMonitorMask = new Integer(System.getProperty(DEFAULT_MONITOR_MASK, new Integer(defaultMonitorMask).toString()));
        } else {
            defaultMonitorMask = new Integer(getConfiguration().getProperty(DEFAULT_MONITOR_MASK, new Integer(defaultMonitorMask).toString()));
        }

        String className;
        if (System.getProperties().containsKey(DEFAULT_PROPERTY_IMPL_CLASS)) {
            className = System.getProperty(DEFAULT_PROPERTY_IMPL_CLASS);

        } else {
            className = getConfiguration().getProperty(DEFAULT_PROPERTY_IMPL_CLASS);
        }
        if (className != null) {
            try {
                defaultPropertyImplClass = (Class<? extends SimpleProperty<?>>) Class.forName(className);
            } catch (final Exception e) {
                defaultPropertyImplClass = DEFAULT_PROP_IMPL_CLASS;
            }
        } else {
            defaultPropertyImplClass = DEFAULT_PROP_IMPL_CLASS;
        }

        if (System.getProperties().containsKey(USE_JNI)) {
            use_jni = new Boolean(System.getProperty(USE_JNI, "false"));
        } else {
            use_jni = new Boolean(getConfiguration().getProperty(USE_JNI, "false"));
        }

        if (!use_jni) {
            context = createJCAContext();
        } else {
            context = createThreadSafeContext();

            if (System.getProperties().containsKey(JNI_FLUSH_TIMER_DELAY)) {
                jniFlushTimerDelay = new Long(System.getProperty(JNI_FLUSH_TIMER_DELAY, new Long(jniFlushTimerDelay).toString()));
            } else {
                jniFlushTimerDelay = new Long(getConfiguration().getProperty(JNI_FLUSH_TIMER_DELAY, new Long(jniFlushTimerDelay).toString()));
            }

            jniFlushTimer = new Timer();
            jniFlushTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (jniFlushIO) {
                        jniFlushIO = false;
                        try {
                            getContext().flushIO();
                        } catch (final Throwable th) {
                            Logger.getLogger(this.getClass()).warn("Flush IO error.", th);
                        }
                    }

                }
            }, jniFlushTimerDelay, jniFlushTimerDelay);
        }

        // initialize supported proxy implementation
        PlugUtilities.initializeSupportedProxyImplementations(this);

        if (System.getProperties().containsKey(DEFAULT_PENDIO_TIMEOUT)) {
            pendIOTimeout = new Double(System.getProperty(DEFAULT_PENDIO_TIMEOUT, DEFAULT_PENDIO_TIMEOUT_VALUE.toString()));
        } else {
            pendIOTimeout = new Double(getConfiguration().getProperty(DEFAULT_PENDIO_TIMEOUT, DEFAULT_PENDIO_TIMEOUT_VALUE.toString()));
        }

        timeout = Plugs.getConnectionTimeout(getConfiguration(), 10000)/1000.0;

        getLogger().info("config {jni: '"+use_jni+"', addr_list: {"+System.getProperty("com.cosylab.epics.caj.CAJContext.addr_list")+"}}");

    }

    /**
     * Timer lazy initialization pattern.
     * @return timer instance.
     */
    private synchronized Timer getTimer()
    {
        if (timer == null) {
            timer = new Timer("SimulatorPlugTimer");
        }

        return timer;
    }

    /**
     * Schedule task for execution.
     * @param r ask to be scheduled.
     * @param delay delay in milliseconds before task is to be executed.
     * @param rate reschedule perion, if <code>0</code> periodic rescheduling is disabled.
     * @return <code>TimerTask</code> instance, used to cancel the task scheduling.
     */
    public TimerTask schedule(final Runnable r, final long delay, final long rate) {

        final ScheduledTask t = new ScheduledTask(r);

        if (rate > 0) {
            getTimer().scheduleAtFixedRate(t, delay, rate);
        } else {
            getTimer().schedule(t, delay);
        }
        return t;
    }

    /**
     * @see org.csstudio.dal.proxy.AbstractPlug#getDeviceImplementationClass(java.lang.String)
     */
    @Override
    protected Class<? extends AbstractDevice> getDeviceImplementationClass(final String uniqueDeviceName) {
        throw new UnsupportedOperationException("Devices not supported");
    }

    /**
     * @see org.csstudio.dal.proxy.AbstractPlug#getDeviceProxyImplementationClass(java.lang.String)
     */
    @Override
    protected Class<? extends DeviceProxy<?>> getDeviceProxyImplementationClass(final String uniqueDeviceName) {
        throw new UnsupportedOperationException("Devices not supported");
    }

    /*
     * @see org.csstudio.dal.proxy.AbstractPlug#getPropertyImplementationClass(java.lang.String)
     */
    @Override
    public Class<? extends SimpleProperty<?>> getPropertyImplementationClass(final String propertyName) {

        class ConnectionListenerImpl implements ConnectionListener {
            /*
             * @see gov.aps.jca.event.ConnectionListener#connectionChanged(gov.aps.jca.event.ConnectionEvent)
             */
            @Override
            public synchronized void connectionChanged(final ConnectionEvent event) {
                this.notifyAll();
            }
        }

        // create channel
        Channel channel = null;
        final ConnectionListenerImpl listener = new ConnectionListenerImpl();
        try {
            synchronized (listener) {
                channel = this.getContext().createChannel(propertyName, listener);
                listener.wait((long)(timeout*1000));
            }

            // if not connected this will throw exception
            final DBRType type = channel.getFieldType();
            final int elementCount = channel.getElementCount();

            return PlugUtilities.getPropertyImplForDBRType(type, elementCount);

        } catch (final IllegalStateException ise) {
            return defaultPropertyImplClass;
        } catch (final Throwable th) {
            throw new RuntimeException("Failed create CA channel to determine channel type.", th);
        }
        finally {
            if (channel != null && channel.getConnectionState() != Channel.CLOSED) {
                channel.dispose();
            }
        }
    }

    /*
     * @see org.csstudio.dal.proxy.AbstractPlug#getPropertyImplementationClass(java.lang.Class)
     */
    @Override
    public Class<? extends SimpleProperty<?>> getPropertyImplementationClass(final Class<? extends SimpleProperty<?>> type, final String propertyName) throws RemoteException {
        if (type != null) {
            return PropertyUtilities.getImplementationClass(type);
        }
        else {
            return getPropertyImplementationClass(propertyName);
        //return super.getPropertyImplementationClass(type, propertyName);
        }

    }

    /*
     * @see org.csstudio.dal.proxy.AbstractPlug#getPropertyProxyImplementationClass(java.lang.String)
     */
    @Override
    public Class<? extends PropertyProxy<?,?>> getPropertyProxyImplementationClass(final String propertyName) {
        throw new RuntimeException("Unsupported property type.");
    }

    /*
     * @see org.csstudio.dal.proxy.AbstractPlug#createNewPropertyProxy(java.lang.String, java.lang.Class)
     */
    @Override
    protected <TT extends PropertyProxy<?,?>> TT createNewPropertyProxy(
            final String uniqueName, final Class<TT> type) throws ConnectionException {
        try {
            final PropertyProxy<?,?> p = type.getConstructor(EPICSPlug.class, String.class).newInstance(this, uniqueName);
            // add to directory cache
            if (p instanceof DirectoryProxy) {
                putDirectoryProxyToCache((DirectoryProxy<?>) p);
            }
            return type.cast(p);
        } catch (final Exception e) {
            throw new ConnectionException(this,
                    "Failed to instantiate property proxy '" + uniqueName
                            + "' for type '" + type.getName() + "'.", e);
        }
    }

    /*
     * @see org.csstudio.dal.proxy.AbstractPlug#getPlugType()
     */
    @Override
    public String getPlugType() {
        return "EPICS";
    }

    /*
     * @see org.csstudio.dal.proxy.AbstractPlug#createNewDirectoryProxy(java.lang.String)
     */
    @Override
    protected DirectoryProxy<?> createNewDirectoryProxy(final String uniqueName)
        throws ConnectionException {
        // directory is already added to cache in createNewPropertyProxy method
        throw new RuntimeException("Error in factory implementation, PropertyProxy must be created first.");
    }

    /*
     * @see org.csstudio.dal.proxy.AbstractPlug#createNewDeviceProxy(java.lang.String, java.lang.Class)
     */
    @Override
    protected <T extends DeviceProxy<?>> T createNewDeviceProxy(final String uniqueName,
            final Class<T> type) throws ConnectionException {
        throw new UnsupportedOperationException("Devices not supported");
    }

    /**
     * @see org.csstudio.dal.context.PlugContext#createRemoteInfo(java.lang.String)
     */
    @Override
    public RemoteInfo createRemoteInfo(final String uniqueName) throws NamingException {
        return new RemoteInfo(PLUG_TYPE, uniqueName, null, null);
    }

    /**
     * @see org.csstudio.dal.context.PlugContext#getDefaultDirectory()
     */
    @Override
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
            } catch (final Throwable th) {
                Logger.getLogger(this.getClass()).warn("Flush IO error: "+PlugUtilities.toShortErrorReport(th), th);
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
        if (context==null) {
            throw new IllegalStateException("Connection to EPICS has been already destroyed.");
        }
        return context;
    }

    private CAJContext createJCAContext() throws RemoteException {
        try {
            final DefaultConfiguration edconf = new DefaultConfiguration("event_dispatcher");
            edconf.setAttribute("class", QueuedEventDispatcher.class.getName());


            final DefaultConfiguration config = new DefaultConfiguration("EPICSPlugConfig");
            config.setAttribute("class", JCALibrary.CHANNEL_ACCESS_JAVA);
            config.addChild(edconf);

            // create context
            final CAJContext c= (CAJContext)JCALibrary.getInstance().createContext(config);

            // force explicit initialization
            c.initialize();

            // register all context listeners
            c.addContextExceptionListener(this);
            c.addContextMessageListener(this);

            return c;

        } catch (final Throwable th) {
            // rethrow to abort EPICS plug instance creation
            throw new RemoteException(this,"Failed to initialize EPICS plug: "+PlugUtilities.toShortErrorReport(th), th);
        }
    }

    private ThreadSafeContext createThreadSafeContext() throws RemoteException {
        try {
            final DefaultConfiguration edconf = new DefaultConfiguration("event_dispatcher");
            edconf.setAttribute("class", QueuedEventDispatcher.class.getName());


            final DefaultConfiguration config = new DefaultConfiguration("EPICSPlugConfig");
            config.setAttribute("class", JCALibrary.JNI_THREAD_SAFE);
            config.addChild(edconf);

            // create context
            final ThreadSafeContext c= (ThreadSafeContext)JCALibrary.getInstance().createContext(config);

            // force explicit initialization
            c.initialize();

            // register all context listeners
            c.addContextExceptionListener(this);
            c.addContextMessageListener(this);

            return c;

        } catch (final Throwable th) {
            // rethrow to abort EPICS plug instance creation
            throw new RemoteException(this,"Failed to initialize EPICS plug: "+PlugUtilities.toShortErrorReport(th), th);
        }
    }

    /**
     * Get timeout parameter (in seconds).
     * It is configured trough system property defined by org.csstudio.dal.spi.Plugs.CONNECTION_TIMEOUT.
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

    public boolean isDbrUpdatesCharacteristics() {
        return dbrUpdatesCharacteristics;
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
                if (!useCommonExecutor) {
                    throw new IllegalStateException("EPICSPlug is configured not to use a common executor.");
                }
                if (maxThreads == 0) {
                    throw new IllegalStateException("Maximum number of threads must be greater than 0.");
                }
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
    @Override
    @SuppressWarnings("unchecked")
    public void contextException(final ContextExceptionEvent ev) {

        if (plugListeners == null) {
            return;
        }

        synchronized (plugListeners) {
            if (plugListeners.isEmpty()) {
                return;
            }

            final PlugEvent<ContextExceptionEvent> event =
                new PlugEvent<ContextExceptionEvent>(this, ev, new Timestamp(), "Context exception", null, ContextExceptionEvent.class);

            final Iterator<EventSystemListener<PlugEvent<?>>> iter = plugListeners.iterator();
            while (iter.hasNext()) {
                iter.next().errorArrived(event);
            }
        }
    }

    /* (non-Javadoc)
     * @see gov.aps.jca.event.ContextExceptionListener#contextVirtualCircuitException(gov.aps.jca.event.ContextVirtualCircuitExceptionEvent)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void contextVirtualCircuitException(final ContextVirtualCircuitExceptionEvent ev) {

        if (plugListeners == null) {
            return;
        }

        synchronized (plugListeners) {
            if (plugListeners.isEmpty()) {
                return;
            }

            final PlugEvent<ContextVirtualCircuitExceptionEvent> event =
                new PlugEvent<ContextVirtualCircuitExceptionEvent>(this, ev, new Timestamp(), "Context virtual circuit exception", null, ContextVirtualCircuitExceptionEvent.class);

            final Iterator<EventSystemListener<PlugEvent<?>>> iter = plugListeners.iterator();
            while (iter.hasNext()) {
                iter.next().eventArrived(event);
            }
        }
    }

    /* (non-Javadoc)
     * @see gov.aps.jca.event.ContextMessageListener#contextMessage(gov.aps.jca.event.ContextMessageEvent)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void contextMessage(final ContextMessageEvent ev) {

        if (plugListeners == null) {
            return;
        }

        synchronized (plugListeners) {
            if (plugListeners.isEmpty()) {
                return;
            }

            final PlugEvent<ContextMessageEvent> event =
                new PlugEvent<ContextMessageEvent>(this, ev, new Timestamp(), "Context message", null, ContextMessageEvent.class);

            final Iterator<EventSystemListener<PlugEvent<?>>> iter = plugListeners.iterator();
            while (iter.hasNext()) {
                iter.next().eventArrived(event);
            }
        }
    }
}


