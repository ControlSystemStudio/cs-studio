/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.CAException;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.Monitor;
import gov.aps.jca.jni.JNIContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Builder for {@link JCADataSource}. Given the moderate number of configuration
 * parameters, the builder allows to set only the ones the user needs to set.
 * <p>
 * Refer to each parameter for their meaning and default.
 *
 * @author carcassi
 */
public class JCADataSourceBuilder {
    private static final Logger log = Logger.getLogger(JCADataSource.class.getName());
    
    Context jcaContext;
    int monitorMask = Monitor.VALUE | Monitor.ALARM;
    JCATypeSupport typeSupport;
    boolean dbePropertySupported  = false;
    Boolean varArraySupported;
    boolean rtypValueOnly = false;
    boolean honorZeroPrecision = true;

    /**
     * The class name for the implementation of JCA.
     * <p>
     * Default is {@link JCALibrary#CHANNEL_ACCESS_JAVA}.
     * 
     * @param className the class name of the jca implementation
     * @return this
     */
    public JCADataSourceBuilder jcaContextClass(String className) {
        if (jcaContext != null) {
            throw new IllegalStateException("You should call either jcaContextClass or jcaContext once.");
        }
        this.jcaContext = createContext(className);
        return this;
    }
    
    /**
     * The jca context to use. This allows complete customization
     * of the jca context.
     * <p>
     * By default, will be automatically
     * created from the {@link #jcaContextClass(java.lang.String) }.
     * 
     * @param jcaContext the context
     * @return this
     */
    public JCADataSourceBuilder jcaContext(Context jcaContext) {
        if (jcaContext == null) {
            throw new IllegalStateException("You should call once either jcaContextClass or jcaContext.");
        }
        this.jcaContext = jcaContext;
        return this;
    }

    /**
     * The mask used for the monitor notifications. This should be a combination
     * of {@link Monitor#VALUE}, {@link Monitor#ALARM}, ...
     * <p>
     * Default is {@code Monitor.VALUE | Monitor.ALARM }.
     * 
     * @param monitorMask the monitor mask
     * @return this
     */
    public JCADataSourceBuilder monitorMask(int monitorMask) {
        this.monitorMask = monitorMask;
        return this;
    }
    
    /**
     * Changes the way JCA DBR types are mapped to types supported in pvamanger.
     * <p>
     * Default includes support for the VTypes (i.e. {@link JCAVTypeAdapterSet}).
     * 
     * @param typeSupport the custom type support
     * @return this
     */
    public JCADataSourceBuilder typeSupport(JCATypeSupport typeSupport) {
        this.typeSupport = typeSupport;
        return this;
    }

    /**
     * Whether a separate monitor should be used for listening to metadata
     * changes.
     * <p>
     * Default is false.
     * 
     * @param dbePropertySupported if true, metadata changes will trigger notification
     * @return this
     */
    public JCADataSourceBuilder dbePropertySupported(boolean dbePropertySupported) {
        this.dbePropertySupported = dbePropertySupported;
        return this;
    }

    /**
     * If true, monitors will setup using "0" length, which will make
     * the server a variable length array in return (if supported) or a "0"
     * length array (if not supported). Variable array support was added
     * to EPICS 3.14.12.2 and to CAJ 1.1.10.
     * <p>
     * By default it tries to auto-detected whether the client library
     * implements the proper checks.
     * 
     * @param varArraySupported true will enable
     * @return this
     */
    public JCADataSourceBuilder varArraySupported(boolean varArraySupported) {
        this.varArraySupported = varArraySupported;
        return this;
    }
    
    /**
     * If true, for fields that match ".*\.RTYP.*" only the value will be
     * read; alarm and time will be created at client side. Version of EPICS
     * before 3.14.11 do not send correct data (would send only the value),
     * which would make the client behave incorrectly.
     * <p>
     * Default is false.
     * 
     * @param rtypValueOnly true will enable
     * @return this
     */
    public JCADataSourceBuilder rtypValueOnly(boolean rtypValueOnly) {
        this.rtypValueOnly = rtypValueOnly;
        return this;
    }
    
    /**
     * If true, the formatter returned by the VType will show
     * no decimal digits (assumes it was configured);
     * if false, it will return all the digit (assumes it wasn't configured).
     * <p>
     * Default is true.
     * 
     * @param honorZeroPrecision whether the formatter should treat 0 precision as meaningful
     * @return this
     */
    public JCADataSourceBuilder honorZeroPrecision(boolean honorZeroPrecision) {
        this.honorZeroPrecision = honorZeroPrecision;
        return this;
    }
    
    /**
     * Creates a new data source.
     * 
     * @return a new data source
     */
    public JCADataSource build() {
        return new JCADataSource(this);
    }
    
    /**
     * Determines whether the context supports variable arrays
     * or not.
     * 
     * @param context a JCA Context
     * @return true if supports variable sized arrays
     */
    static boolean isVarArraySupported(Context context) {
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
            } catch (Exception ex) {
                log.log(Level.SEVERE, "Couldn't detect varArraySupported", ex);
            }
        }
        
        return true;
    }
    
    /**
     * Creates a context from the class name.
     * 
     * @param className the class name
     * @return a new context
     */
    static Context createContext(String className) {
        try {
            JCALibrary jca = JCALibrary.getInstance();
            return jca.createContext(className);
        } catch (CAException ex) {
            log.log(Level.SEVERE, "JCA context creation failed", ex);
            throw new RuntimeException("JCA context creation failed", ex);
        }
    }    
}
