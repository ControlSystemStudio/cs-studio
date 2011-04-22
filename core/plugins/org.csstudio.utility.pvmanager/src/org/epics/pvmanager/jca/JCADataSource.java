/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.jca;

import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.Monitor;
import java.util.HashSet;
import org.epics.pvmanager.Collector;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.ValueCache;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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

    static final JCADataSource INSTANCE = new JCADataSource();

    /**
     * Creates a new data source using pure Java implementation
     */
    public JCADataSource() {
        this(JCALibrary.CHANNEL_ACCESS_JAVA, Monitor.VALUE | Monitor.ALARM);
    }

    /**
     * Creates a new data source using the className to create the context.
     *
     * @param className JCALibrary.CHANNEL_ACCESS_JAVA, JCALibrary.JNI_THREAD_SAFE, ...
     * @param monitorMask Monitor.VALUE, ...
     */
    public JCADataSource(String className, int monitorMask) {
        this.className = className;
        this.monitorMask = monitorMask;
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
        if (ctxt != null && connectedProcessors.isEmpty()) {
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

    @Override
    public synchronized void connect(DataRecipe dataRecipe) {
        initContext(dataRecipe.getExceptionHandler());
        Set<ValueProcessor> processors = new HashSet<ValueProcessor>();
        for (Map.Entry<Collector, Map<String, ValueCache>> collEntry : dataRecipe.getChannelsPerCollectors().entrySet()) {
            Collector collector = collEntry.getKey();
            for (Map.Entry<String, ValueCache> entry : collEntry.getValue().entrySet()) {
                ValueProcessor processor = createProcessor(entry.getKey(), collector, entry.getValue(), dataRecipe.getExceptionHandler());
                if (processor != null)
                    processors.add(processor);
            }
        }
        connectedProcessors.put(dataRecipe, processors);
    }

    private Map<DataRecipe, Set<ValueProcessor>> connectedProcessors = new ConcurrentHashMap<DataRecipe, Set<ValueProcessor>>();

    private ValueProcessor createProcessor(String pvName, Collector collector,
            ValueCache<?> cache, ExceptionHandler handler) {
        try {
            Channel channel = ctxt.createChannel(pvName);
            @SuppressWarnings("unchecked")
            ValueProcessor processor = new JCAProcessor(channel, collector, cache, handler, monitorMask);
            ctxt.flushIO();
            return processor;
        } catch (CAException e) {
            handler.handleException(e);
        } catch (RuntimeException e) {
            handler.handleException(e);
        }
        return null;
    }

    @Override
    public synchronized void disconnect(DataRecipe recipe) {
        for (ValueProcessor processor : connectedProcessors.get(recipe)) {
            try {
                processor.close();
            } catch (Exception ex) {
                recipe.getExceptionHandler().handleException(ex);
            }
        }
        connectedProcessors.remove(recipe);
        releaseContext(recipe.getExceptionHandler());
    }

}
