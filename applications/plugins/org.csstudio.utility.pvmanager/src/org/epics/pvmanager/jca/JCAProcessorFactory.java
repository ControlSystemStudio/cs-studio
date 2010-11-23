/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR_CTRL_Double;
import gov.aps.jca.dbr.DBR_LABELS_Enum;
import gov.aps.jca.dbr.DBR_TIME_Byte;
import gov.aps.jca.dbr.DBR_TIME_Double;
import gov.aps.jca.dbr.DBR_TIME_Enum;
import gov.aps.jca.dbr.DBR_TIME_Float;
import gov.aps.jca.dbr.DBR_TIME_Int;
import gov.aps.jca.dbr.DBR_TIME_Short;
import gov.aps.jca.dbr.DBR_TIME_String;
import gov.aps.jca.event.MonitorEvent;
import java.util.HashMap;
import java.util.Map;
import org.epics.pvmanager.Collector;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.DataSource.ValueProcessor;
import org.epics.pvmanager.ExceptionHandler;
import org.epics.pvmanager.ValueCache;
import org.epics.pvmanager.data.VByteArray;
import org.epics.pvmanager.data.VDouble;
import org.epics.pvmanager.data.VDoubleArray;
import org.epics.pvmanager.data.VEnum;
import org.epics.pvmanager.data.VFloatArray;
import org.epics.pvmanager.data.VInt;
import org.epics.pvmanager.data.VIntArray;
import org.epics.pvmanager.data.VShortArray;
import org.epics.pvmanager.data.VString;
import org.epics.pvmanager.data.VStringArray;

/**
 * Factory class for ValueProcessors.
 *
 * @author carcassi
 */
abstract class JCAProcessorFactory<T> {

    private static Map<Class<?>, JCAProcessorFactory<?>> factories;
    private static JCAProcessorFactory<? extends Object> composite = new JCAProcessorFactory<Object>() {

        @Override
        ValueProcessor<MonitorEvent, Object> createProcessor(Channel channel, Collector collector, ValueCache<Object> cache, ExceptionHandler handler) throws CAException {
            @SuppressWarnings("unchecked")
            JCAProcessorFactory<Object> factory = (JCAProcessorFactory<Object>) factories.get(cache.getType());
            if (factory == null) {
                throw new RuntimeException("Type " + cache.getType() + " not supported");
            } else {
                return factory.createProcessor(channel, collector, cache, handler);
            }
        }
    };

    public static JCAProcessorFactory getFactory() {
        return composite;
    }

    static {
        Map<Class<?>, JCAProcessorFactory<?>> newFactories = new HashMap<Class<?>, JCAProcessorFactory<?>>();
        newFactories.put(VDouble.class, new JCAProcessorFactory<VDouble>() {

            @Override
            ValueProcessor<MonitorEvent, VDouble> createProcessor(Channel channel, Collector collector, ValueCache<VDouble> cache, ExceptionHandler handler) throws CAException {
                return new SingleValueProcessor<VDouble, DBR_TIME_Double, DBR_CTRL_Double>(channel, collector, cache, handler,
                        DBR_TIME_Double.TYPE, DBR_CTRL_Double.TYPE) {

                    @Override
                    protected VDouble createValue(DBR_TIME_Double value, DBR_CTRL_Double metadata, boolean disconnected) {
                        return new VDoubleFromDbr(value, metadata, disconnected);
                    }
                };
            }
        });
        newFactories.put(VEnum.class, new JCAProcessorFactory<VEnum>() {

            @Override
            ValueProcessor<MonitorEvent, VEnum> createProcessor(Channel channel, Collector collector, ValueCache<VEnum> cache, ExceptionHandler handler) throws CAException {
                return new SingleValueProcessor<VEnum, DBR_TIME_Enum, DBR_LABELS_Enum>(channel, collector, cache, handler,
                        DBR_TIME_Enum.TYPE, DBR_LABELS_Enum.TYPE) {

                    @Override
                    protected VEnum createValue(DBR_TIME_Enum value, DBR_LABELS_Enum metadata, boolean disconnected) {
                        return new VEnumFromDbr(value, metadata, disconnected);
                    }
                };
            }
        });
        newFactories.put(VInt.class, new JCAProcessorFactory<VInt>() {

            @Override
            ValueProcessor<MonitorEvent, VInt> createProcessor(Channel channel, Collector collector, ValueCache<VInt> cache, ExceptionHandler handler) throws CAException {
                return new SingleValueProcessor<VInt, DBR_TIME_Int, DBR_CTRL_Double>(channel, collector, cache, handler,
                        DBR_TIME_Int.TYPE, DBR_CTRL_Double.TYPE) {

                    @Override
                    protected VInt createValue(DBR_TIME_Int value, DBR_CTRL_Double metadata, boolean disconnected) {
                        return new VIntFromDbr(value, metadata, disconnected);
                    }
                };
            }
        });
        newFactories.put(VString.class, new JCAProcessorFactory<VString>() {

            @Override
            ValueProcessor<MonitorEvent, VString> createProcessor(Channel channel, Collector collector, ValueCache<VString> cache, ExceptionHandler handler) throws CAException {
                return new SingleValueProcessor<VString, DBR_TIME_String, DBR_TIME_String>(channel, collector, cache, handler,
                        DBR_TIME_String.TYPE, null) {

                    @Override
                    protected VString createValue(DBR_TIME_String value, DBR_TIME_String metadata, boolean disconnected) {
                        return new VStringFromDbr(value, disconnected);
                    }
                };
            }
        });
        newFactories.put(VDoubleArray.class, new JCAProcessorFactory<VDoubleArray>() {

            @Override
            ValueProcessor<MonitorEvent, VDoubleArray> createProcessor(Channel channel, Collector collector, ValueCache<VDoubleArray> cache, ExceptionHandler handler) throws CAException {
                return new ArrayProcessor<VDoubleArray, DBR_TIME_Double, DBR_CTRL_Double>(channel, collector, cache, handler,
                        DBR_TIME_Double.TYPE, DBR_CTRL_Double.TYPE) {

                    @Override
                    protected VDoubleArray createValue(DBR_TIME_Double value, DBR_CTRL_Double metadata, boolean disconnected) {
                        return new VDoubleArrayFromDbr(value, metadata, disconnected);
                    }
                };
            }
        });
        newFactories.put(VFloatArray.class, new JCAProcessorFactory<VFloatArray>() {

            @Override
            ValueProcessor<MonitorEvent, VFloatArray> createProcessor(Channel channel, Collector collector, ValueCache<VFloatArray> cache, ExceptionHandler handler) throws CAException {
                return new ArrayProcessor<VFloatArray, DBR_TIME_Float, DBR_CTRL_Double>(channel, collector, cache, handler,
                        DBR_TIME_Float.TYPE, DBR_CTRL_Double.TYPE) {

                    @Override
                    protected VFloatArray createValue(DBR_TIME_Float value, DBR_CTRL_Double metadata, boolean disconnected) {
                        return new VFloatArrayFromDbr(value, metadata, disconnected);
                    }
                };
            }
        });
        newFactories.put(VByteArray.class, new JCAProcessorFactory<VByteArray>() {

            @Override
            ValueProcessor<MonitorEvent, VByteArray> createProcessor(Channel channel, Collector collector, ValueCache<VByteArray> cache, ExceptionHandler handler) throws CAException {
                return new ArrayProcessor<VByteArray, DBR_TIME_Byte, DBR_CTRL_Double>(channel, collector, cache, handler,
                        DBR_TIME_Byte.TYPE, DBR_CTRL_Double.TYPE) {

                    @Override
                    protected VByteArray createValue(DBR_TIME_Byte value, DBR_CTRL_Double metadata, boolean disconnected) {
                        return new VByteArrayFromDbr(value, metadata, disconnected);
                    }
                };
            }
        });
        newFactories.put(VShortArray.class, new JCAProcessorFactory<VShortArray>() {

            @Override
            ValueProcessor<MonitorEvent, VShortArray> createProcessor(Channel channel, Collector collector, ValueCache<VShortArray> cache, ExceptionHandler handler) throws CAException {
                return new ArrayProcessor<VShortArray, DBR_TIME_Short, DBR_CTRL_Double>(channel, collector, cache, handler,
                        DBR_TIME_Short.TYPE, DBR_CTRL_Double.TYPE) {

                    @Override
                    protected VShortArray createValue(DBR_TIME_Short value, DBR_CTRL_Double metadata, boolean disconnected) {
                        return new VShortArrayFromDbr(value, metadata, disconnected);
                    }
                };
            }
        });
        newFactories.put(VIntArray.class, new JCAProcessorFactory<VIntArray>() {

            @Override
            ValueProcessor<MonitorEvent, VIntArray> createProcessor(Channel channel, Collector collector, ValueCache<VIntArray> cache, ExceptionHandler handler) throws CAException {
                return new ArrayProcessor<VIntArray, DBR_TIME_Int, DBR_CTRL_Double>(channel, collector, cache, handler,
                        DBR_TIME_Int.TYPE, DBR_CTRL_Double.TYPE) {

                    @Override
                    protected VIntArray createValue(DBR_TIME_Int value, DBR_CTRL_Double metadata, boolean disconnected) {
                        return new VIntArrayFromDbr(value, metadata, disconnected);
                    }
                };
            }
        });
        newFactories.put(VStringArray.class, new JCAProcessorFactory<VStringArray>() {

            @Override
            ValueProcessor<MonitorEvent, VStringArray> createProcessor(Channel channel, Collector collector, ValueCache<VStringArray> cache, ExceptionHandler handler) throws CAException {
                return new ArrayProcessor<VStringArray, DBR_TIME_String, DBR_TIME_String>(channel, collector, cache, handler,
                        DBR_TIME_String.TYPE, null) {

                    @Override
                    protected VStringArray createValue(DBR_TIME_String value, DBR_TIME_String metadata, boolean disconnected) {
                        return new VStringArrayFromDbr(value, disconnected);
                    }
                };
            }
        });
        factories = newFactories;
    }

    /**
     * Creates a ValueProcess for the given channel.
     *
     * @param <T> type of the channel
     * @param channel channel on which to connect
     * @param collector collector to notify
     * @param cache where to put updates
     * @param handler where to direct exceptions
     * @return the new processor
     */
    abstract DataSource.ValueProcessor<MonitorEvent, T> createProcessor(Channel channel, Collector collector,
            ValueCache<T> cache, ExceptionHandler handler)
            throws CAException;
}
