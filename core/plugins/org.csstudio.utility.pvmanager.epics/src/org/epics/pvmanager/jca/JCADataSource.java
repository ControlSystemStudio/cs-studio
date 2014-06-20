/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
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
import org.epics.pvmanager.vtype.DataTypeSupport;
import com.cosylab.epics.caj.CAJContext;
import gov.aps.jca.jni.JNIContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.epics.pvmanager.util.Executors.namedPool;

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
    private final boolean rtypValueOnly;
    private final boolean honorZeroPrecision;

    /**
     * Creates a new data source using pure Java implementation and all the
     * defaults described in {@link JCADataSourceBuilder}.
     */
    public JCADataSource() {
        this(new JCADataSourceBuilder());
    }
    
    /**
     * Creates a new data source using the given context. The context will
     * never be closed.
     * 
     * @param jcaContext the context to be used
     * @param monitorMask Monitor.VALUE, ...
     * @deprecated use {@link JCADataSourceBuilder}
     */
    @Deprecated
    public JCADataSource(Context jcaContext, int monitorMask) {
        this(new JCADataSourceBuilder().jcaContext(jcaContext).monitorMask(monitorMask));
    }

    /**
     * Creates a new data source using the className to create the context.
     *
     * @param className JCALibrary.CHANNEL_ACCESS_JAVA, JCALibrary.JNI_THREAD_SAFE, ...
     * @param monitorMask Monitor.VALUE, ...
     * @deprecated use {@link JCADataSourceBuilder}
     */
    @Deprecated
    public JCADataSource(String className, int monitorMask) {
        this(new JCADataSourceBuilder().jcaContextClass(className).monitorMask(monitorMask));
    }
    
    /**
     * Creates a new data source using the given context. The context will
     * never be closed. The type mapping con be configured with a custom
     * type support.
     * 
     * @param jcaContext the context to be used
     * @param monitorMask Monitor.VALUE, ...
     * @param typeSupport type support to be used
     * @deprecated use {@link JCADataSourceBuilder}
     */
    @Deprecated
    public JCADataSource(Context jcaContext, int monitorMask, JCATypeSupport typeSupport) {
        this(new JCADataSourceBuilder().jcaContext(jcaContext).monitorMask(monitorMask)
                .typeSupport(typeSupport));
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
     * @deprecated use {@link JCADataSourceBuilder}
     */
    @Deprecated
    public JCADataSource(Context jcaContext, int monitorMask, JCATypeSupport typeSupport, boolean dbePropertySupported, boolean varArraySupported) {
        this(new JCADataSourceBuilder().jcaContext(jcaContext).monitorMask(monitorMask)
                .typeSupport(typeSupport).dbePropertySupported(dbePropertySupported)
                .varArraySupported(varArraySupported));
    }
    
    protected JCADataSource(JCADataSourceBuilder builder) {
        super(true);
        // Some properties are not pre-initialized to the default,
        // so if they were not set, we should initialize them.
        
        // Default JCA context is pure Java
        if (builder.jcaContext == null) {
            ctxt = JCADataSourceBuilder.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
        } else {
            ctxt = builder.jcaContext;
        }

        try {
            if (ctxt instanceof CAJContext) {
                ((CAJContext) ctxt).setDoNotShareChannels(true);
            }
        } catch (Throwable t) {
            log.log(Level.WARNING, "Couldn't change CAJContext to doNotShareChannels: this may cause some rare notification problems.", t);
        }
        
        // Default type support are the VTypes
        if (builder.typeSupport == null) {
            typeSupport = new JCATypeSupport(new JCAVTypeAdapterSet());
        } else {
            typeSupport = builder.typeSupport;
        }

        // Default support for var array needs to be detected
        if (builder.varArraySupported == null) {
            varArraySupported = JCADataSourceBuilder.isVarArraySupported(ctxt);
        } else {
            varArraySupported = builder.varArraySupported;
        }
        
        monitorMask = builder.monitorMask;
        dbePropertySupported = builder.dbePropertySupported;
        rtypValueOnly = builder.rtypValueOnly;
        honorZeroPrecision = builder.honorZeroPrecision;
        
        if (useContextSwitchForAccessRightCallback()) {
            contextSwitch = Executors.newSingleThreadExecutor(namedPool("PVMgr JCA Workaround "));
        } else {
            contextSwitch = null;
        }
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
     * True if should only ask value for RTYP fields.
     * 
     * @return true if asking for value only
     */
    public boolean isRtypValueOnly() {
        return rtypValueOnly;
    }

    /**
     * True if zero precision should be honored, or disregarded.
     * 
     * @return true if zero precision setting is honored
     */
    public boolean isHonorZeroPrecision() {
        return honorZeroPrecision;
    }
    
    /**
     * Determines whether the context supports variable arrays
     * or not.
     * 
     * @param context a JCA Context
     * @return true if supports variable sized arrays
     */
    @Deprecated
    public static boolean isVarArraySupported(Context context) {
        return JCADataSourceBuilder.isVarArraySupported(context);
    }
    
    final boolean useContextSwitchForAccessRightCallback() {
        if (ctxt instanceof JNIContext) {
            return true;
        }
        return false;
    }
    
    ExecutorService getContextSwitch() {
        return contextSwitch;
    }
    
    private final ExecutorService contextSwitch;
    
}
