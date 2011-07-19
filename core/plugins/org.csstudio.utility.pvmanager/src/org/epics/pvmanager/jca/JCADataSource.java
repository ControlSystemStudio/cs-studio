/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.jca;

import gov.aps.jca.CAException;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.Monitor;
import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.DataSource;
import java.util.logging.Logger;
import org.epics.pvmanager.DataRecipe;
import org.epics.pvmanager.ExceptionHandler;
import org.epics.pvmanager.data.DataTypeSupport;

/**
 * A data source that uses jca.
 *
 * @author carcassi
 */
public class JCADataSource extends DataSource {

    static {
        // Install type support for the types it generates.
        DataTypeSupport.install();
    }

    private static final Logger logger = Logger.getLogger(JCADataSource.class.getName());
    // Get the JCALibrary instance.
    private static JCALibrary jca = JCALibrary.getInstance();
    private volatile Context ctxt = null;
    private final String className;
    private final int monitorMask;
    private final boolean destroyContextWhenDone;

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
        this.className = null;
        this.destroyContextWhenDone = false;
        this.monitorMask = monitorMask;
    }

    /**
     * Creates a new data source using the className to create the context.
     *
     * @param className JCALibrary.CHANNEL_ACCESS_JAVA, JCALibrary.JNI_THREAD_SAFE, ...
     * @param monitorMask Monitor.VALUE, ...
     */
    public JCADataSource(String className, int monitorMask) {
        this(className, monitorMask, false);
    }

    /**
     * Creates a new data source using the className to create the context, and
     * specifying whether the context should be destroy when the last
     * connection is closed.
     *
     * @param className JCALibrary.CHANNEL_ACCESS_JAVA, JCALibrary.JNI_THREAD_SAFE, ...
     * @param monitorMask Monitor.VALUE, ...
     */
    public JCADataSource(String className, int monitorMask, boolean destroyContextWhenDone) {
        super(true);
        this.className = className;
        this.monitorMask = monitorMask;
        this.destroyContextWhenDone = destroyContextWhenDone;
    }

    /*
     * This Metod will initialize the JCA context.
     */
    private void initContext(ExceptionHandler handler) {
        // Create a context which uses pure channel access java with the default
        // (System) configuration values.

        // TDB create the context reading some configuration file????
        if (ctxt == null) {
            try {
                logger.fine("Initializing JCA context");
                ctxt = jca.createContext(className);
            } catch (CAException e) {
                handler.handleException(e);
            }
        }
    }

    /**
     * Destroy JCA context.
     */
    private void releaseContext(ExceptionHandler handler) {
        if (ctxt != null && getChannels().isEmpty() && destroyContextWhenDone) {
            try {
                // If a context was created and is the last pv active,
                // destroy the context.

                // TODO Bug in JNI implementation that can't support this?
                ctxt.destroy();
                ctxt = null;
                logger.fine("JCA context destroyed");
            } catch (CAException e) {
                handler.handleException(e);
            }
        }
    }
    
    Context getContext() {
        return ctxt;
    }

    @Override
    public synchronized void connect(DataRecipe dataRecipe) {
        try {
            initContext(dataRecipe.getExceptionHandler());
            super.connect(dataRecipe);
        } finally {
            try {
                ctxt.flushIO();
            } catch (CAException ex) {
                dataRecipe.getExceptionHandler().handleException(ex);
            }
        }
    }

    @Override
    public synchronized void disconnect(DataRecipe dataRecipe) {
        try {
            initContext(dataRecipe.getExceptionHandler());
            super.disconnect(dataRecipe);
        } finally {
            try {
                ctxt.flushIO();
                releaseContext(dataRecipe.getExceptionHandler());
            } catch (CAException ex) {
                dataRecipe.getExceptionHandler().handleException(ex);
            }
        }
    }

    @Override
    protected ChannelHandler<?> createChannel(String channelName) {
        return new JCAChannelHandler(channelName, ctxt, monitorMask);
    }
    
    

}
