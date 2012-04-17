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

/**
 * A data source that uses jca.
 * <p>
 * NOTE: this class is extensible as per Bastian request so that DESY can hook
 * a different type factory. This is a temporary measure until the problem
 * is solved in better, more general way, so that data sources
 * can work only with data source specific types, while allowing
 * conversions to normalized type through operators. The contract of this
 * class is, therefore, expected to change.
 * <p>
 * Related changes are marked so that they are not accidentally removed in the
 * meantime, and can be intentionally removed when a better solution is implemented.
 * 
 * @author carcassi
 */
public class JCADataSource extends DataSource {

    static {
        // Install type support for the types it generates.
        DataTypeSupport.install();
    }

    private static final Logger log = Logger.getLogger(JCADataSource.class.getName());

    private volatile Context ctxt = null;
    private final int monitorMask;

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
        super(true);
        this.ctxt = jcaContext;
        this.monitorMask = monitorMask;
    }

    /**
     * Creates a new data source using the className to create the context.
     *
     * @param className JCALibrary.CHANNEL_ACCESS_JAVA, JCALibrary.JNI_THREAD_SAFE, ...
     * @param monitorMask Monitor.VALUE, ...
     */
    public JCADataSource(String className, int monitorMask) {
        super(true);
        try {
            JCALibrary jca = JCALibrary.getInstance();
            this.ctxt = jca.createContext(className);
        } catch (CAException ex) {
            log.log(Level.SEVERE, "JCA context creation failed", ex);
            throw new RuntimeException("JCA context creation failed", ex);
        }
        this.monitorMask = monitorMask;
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

    @Override
    protected ChannelHandler<?> createChannel(String channelName) {
        return new JCAChannelHandler(channelName, ctxt, monitorMask);
    }

}
