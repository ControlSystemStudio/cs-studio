/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.CAException;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.Monitor;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.data.DataTypeSupport;
import com.cosylab.epics.caj.CAJContext;
import gov.aps.jca.jni.JNIContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * A data source that uses jca.
 * <p>
 * Type support can be configured by passing a custom {@link JCATypeSupport}
 * to the constructor.
 * 
 * @author carcassi
 */
public class JCADataSource extends DataSource {

    static {
        // Install type support for the types it generates.
        DataTypeSupport.install();
    }

    private static final Logger log = Logger.getLogger(JCADataSource.class.getName());

    private final Context ctxt;
    private final int monitorMask;
    private final boolean varArraySupported;
    private final boolean dbePropertySupported;
    private final JCATypeSupport typeSupport;

    static final JCADataSource INSTANCE = new JCADataSource();

    /**
     * Creates a new data source using pure Java implementation
     */
    public JCADataSource() {
        this(JCALibrary.CHANNEL_ACCESS_JAVA, Monitor.VALUE | Monitor.ALARM);
    }
    
    /**
     * Creates a new data source using the given context. The context will
     * never be closed.
     * 
     * @param jcaContext the context to be used
     * @param monitorMask Monitor.VALUE, ...
     */
    public JCADataSource(Context jcaContext, int monitorMask) {
        this(jcaContext, monitorMask, new JCATypeSupport(new JCAVTypeAdapterSet()));
    }

    /**
     * Creates a new data source using the className to create the context.
     *
     * @param className JCALibrary.CHANNEL_ACCESS_JAVA, JCALibrary.JNI_THREAD_SAFE, ...
     * @param monitorMask Monitor.VALUE, ...
     */
    public JCADataSource(String className, int monitorMask) {
        this(createContext(className), monitorMask);
    }
    
    private static Context createContext(String className) {
        try {
            JCALibrary jca = JCALibrary.getInstance();
            return jca.createContext(className);
        } catch (CAException ex) {
            log.log(Level.SEVERE, "JCA context creation failed", ex);
            throw new RuntimeException("JCA context creation failed", ex);
        }
    }
    
    /**
     * Creates a new data source using the given context. The context will
     * never be closed. The type mapping con be configured with a custom
     * type support.
     * 
     * @param jcaContext the context to be used
     * @param monitorMask Monitor.VALUE, ...
     * @param typeSupport type support to be used
     */
    public JCADataSource(Context jcaContext, int monitorMask, JCATypeSupport typeSupport) {
        this(jcaContext, monitorMask, typeSupport, false, isVarArraySupported(jcaContext));
    }
    
    /**
     * Creates a new data source using the given context. The context will
     * never be closed. The type mapping con be configured with a custom
     * type support.
     * 
     * @param jcaContext the context to be used
     * @param monitorMask Monitor.VALUE, ...
     * @param typeSupport type support to be used
     * @param dbePropertySupported whether metadata monitors should be used
     * @param varArraySupported true if var array should be used 
     */
    public JCADataSource(Context jcaContext, int monitorMask, JCATypeSupport typeSupport, boolean dbePropertySupported, boolean varArraySupported) {
        super(true);
        this.ctxt = jcaContext;
        this.monitorMask = monitorMask;
        this.typeSupport = typeSupport;
        this.dbePropertySupported = dbePropertySupported;
        this.varArraySupported = varArraySupported;
    }

    @Override
    public void close() {
        super.close();
        ctxt.dispose();
    }

    /**
     * The context used by the data source.
     * 
     * @return the data source context
     */
    public Context getContext() {
        return ctxt;
    }

    /**
     * The monitor mask used for this data source.
     * 
     * @return the monitor mask
     */
    public int getMonitorMask() {
        return monitorMask;
    }

    /**
     * Whether the metadata monitor should be established.
     * 
     * @return true if using metadata monitors
     */
    public boolean isDbePropertySupported() {
        return dbePropertySupported;
    }

    @Override
    protected ChannelHandler createChannel(String channelName) {
        return new JCAChannelHandler(channelName, this);
    }

    JCATypeSupport getTypeSupport() {
        return typeSupport;
    }
    
    /**
     * True whether the context can use variable arrays (all
     * array monitor request will have an element count of 0).
     * 
     * @return true if variable size arrays are supported
     */
    public boolean isVarArraySupported() {
        return varArraySupported;
    }
    
    /**
     * Determines whether the context supports variable arrays
     * or not.
     * 
     * @param context a JCA Context
     * @return true if supports variable sized arrays
     */
    public static boolean isVarArraySupported(Context context) {
        try {
            Class cajClazz = Class.forName("com.cosylab.epics.caj.CAJContext");
            if (cajClazz.isInstance(context)) {
                return !(context.getVersion().getMajorVersion() <= 1 && context.getVersion().getMinorVersion() <= 1 && context.getVersion().getMaintenanceVersion() <=9);
            }
        } catch (ClassNotFoundException ex) {
            // Can't be CAJ, fall back to JCA
        }
        
        if (context instanceof JNIContext) {
            try {
                Class<?> jniClazz = Class.forName("gov.aps.jca.jni.JNI");
                final Method method = jniClazz.getDeclaredMethod("_ca_getRevision", new Class<?>[0]);
                // The field is actually private, so we need to make it accessible
                AccessController.doPrivileged(new PrivilegedAction<Object>() {

                    @Override
                    public Object run() {
                        method.setAccessible(true);
                        return null;
                    }
                    
                });
                Integer integer = (Integer) method.invoke(null, new Object[0]);
                return (integer >= 13);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException("Cannot detect: no CAJContext or JNI classes can be loaded.", ex);
            } catch (NoSuchMethodException ex) {
                throw new RuntimeException("Cannot detect: no CAJContext or JNI._ca_getRevision found.", ex);
            } catch (SecurityException ex) {
                throw new RuntimeException("Cannot detect: no CAJContext and no permission to access JNI._ca_getRevision.", ex);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException("Cannot detect: no CAJContext and cannot invoke JNI._ca_getRevision.", ex);
            } catch (IllegalArgumentException ex) {
                throw new RuntimeException("Cannot detect: no CAJContext and cannot invoke JNI._ca_getRevision.", ex);
            } catch (InvocationTargetException ex) {
                throw new RuntimeException("Cannot detect: no CAJContext and cannot invoke JNI._ca_getRevision.", ex);
            }
            
        }
        
        throw new RuntimeException("Couldn't detect");
    }
    
}
