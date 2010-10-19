/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.jca;

import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
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
import org.epics.pvmanager.data.VDouble;
import org.epics.pvmanager.data.VEnum;
import org.epics.pvmanager.data.VInt;
import org.epics.pvmanager.data.VString;

/**
 * A data source that uses jca.
 *
 * @author carcassi
 */
public class JCADataSource extends DataSource {
    private static final Logger logger = Logger.getLogger(JCADataSource.class.getName());
    // Get the JCALibrary instance.
    private static JCALibrary jca = JCALibrary.getInstance();
    private volatile Context ctxt = null;

    static final JCADataSource INSTANCE = new JCADataSource();

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
                ctxt = jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
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
                if (entry.getValue().getType().equals(VDouble.class)) {
                    @SuppressWarnings("unchecked")
                    ValueCache<VDouble> cache = (ValueCache<VDouble>) entry.getValue();
                    ValueProcessor processor = monitorVDouble(entry.getKey(), collector, cache, dataRecipe.getExceptionHandler());
                    if (processor != null)
                        processors.add(processor);
                } else if (entry.getValue().getType().equals(VInt.class)) {
                    @SuppressWarnings("unchecked")
                    ValueCache<VInt> cache = (ValueCache<VInt>) entry.getValue();
                    ValueProcessor processor = monitorVInt(entry.getKey(), collector, cache, dataRecipe.getExceptionHandler());
                    if (processor != null)
                        processors.add(processor);
                } else if (entry.getValue().getType().equals(VString.class)) {
                    @SuppressWarnings("unchecked")
                    ValueCache<VString> cache = (ValueCache<VString>) entry.getValue();
                    ValueProcessor processor = monitorVString(entry.getKey(), collector, cache, dataRecipe.getExceptionHandler());
                    if (processor != null)
                        processors.add(processor);
                } else if (entry.getValue().getType().equals(VEnum.class)) {
                    @SuppressWarnings("unchecked")
                    ValueCache<VEnum> cache = (ValueCache<VEnum>) entry.getValue();
                    ValueProcessor processor = monitorVEnum(entry.getKey(), collector, cache, dataRecipe.getExceptionHandler());
                    if (processor != null)
                        processors.add(processor);
                } else {
                    throw new UnsupportedOperationException("Type "
                            + entry.getValue().getType().getName()
                            + " is not yet supported");
                }
            }
        }
        connectedProcessors.put(dataRecipe, processors);
    }

    private Map<DataRecipe, Set<ValueProcessor>> connectedProcessors = new ConcurrentHashMap<DataRecipe, Set<ValueProcessor>>();

    private VDoubleProcessor monitorVDouble(String pvName, Collector collector,
            ValueCache<VDouble> cache, ExceptionHandler handler) {
        try {
            Channel channel = ctxt.createChannel(pvName);
            VDoubleProcessor doubleProcessor = new VDoubleProcessor(channel, collector, cache, handler);
            ctxt.flushIO();
            return doubleProcessor;
        } catch (Exception e) {
            handler.handleException(e);
        }
        return null;
    }

    private VIntProcessor monitorVInt(String pvName, Collector collector,
            ValueCache<VInt> cache, ExceptionHandler handler) {
        try {
            Channel channel = ctxt.createChannel(pvName);
            VIntProcessor processor = new VIntProcessor(channel, collector, cache, handler);
            ctxt.flushIO();
            return processor;
        } catch (Exception e) {
            handler.handleException(e);
        }
        return null;
    }

    private VStringProcessor monitorVString(String pvName, Collector collector,
            ValueCache<VString> cache, ExceptionHandler handler) {
        try {
            Channel channel = ctxt.createChannel(pvName);
            VStringProcessor processor = new VStringProcessor(channel, collector, cache, handler);
            ctxt.flushIO();
            return processor;
        } catch (Exception e) {
            handler.handleException(e);
        }
        return null;
    }

    private VEnumProcessor monitorVEnum(String pvName, Collector collector,
            ValueCache<VEnum> cache, ExceptionHandler handler) {
        try {
            Channel channel = ctxt.createChannel(pvName);
            VEnumProcessor processor = new VEnumProcessor(channel, collector, cache, handler);
            ctxt.flushIO();
            return processor;
        } catch (Exception e) {
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
