/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import org.epics.pvmanager.expression.DesiredRateExpressionImpl;
import java.util.ArrayList;
import java.util.List;
import org.epics.pvmanager.expression.ChannelExpression;
import org.epics.pvmanager.expression.ChannelExpressionList;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.Collector;
import org.epics.pvmanager.expression.SourceRateExpression;
import org.epics.pvmanager.Function;
import org.epics.pvmanager.expression.DesiredRateExpressionList;
import org.epics.pvmanager.expression.DesiredRateExpressionListImpl;
import org.epics.pvmanager.expression.Expressions;
import org.epics.pvmanager.expression.SourceRateExpressionImpl;
import org.epics.pvmanager.expression.SourceRateExpressionList;
import org.epics.pvmanager.util.TimeDuration;
import org.epics.pvmanager.util.TimeStamp;
import static org.epics.pvmanager.ExpressionLanguage.*;

/**
 * PVManager expression language support for EPICS types.
 *
 * @author carcassi
 */
public class ExpressionLanguage {
    private ExpressionLanguage() {}

    static {
        // Add support for Epics types.
        DataTypeSupport.install();
    }
    
    /**
     * Expects a numeric scalar (VDouble or VInt) and converts it to
     * a VDouble.
     * 
     * @param expression an expression that returns a numeric scalar
     * @return a new expression
     */
    public static SourceRateExpression<VDouble> vDoubleOf(SourceRateExpression<?> expression) {
        return new SourceRateExpressionImpl<VDouble>(expression, new ConverterVDoubleFunction(expression.getFunction()), expression.getName());
    }
    
    /**
     * Expects a numeric array (VDoubleArray, VFloatArray, VIntArray, VShortArray
     * or VByteArray) and converts it to a VDoubleArray.
     * 
     * @param expression an expression that returns a numeric array
     * @return a new expression
     */
    public static SourceRateExpression<VDoubleArray> vDoubleArrayOf(SourceRateExpression<?> expression) {
        return new SourceRateExpressionImpl<VDoubleArray>(expression, new ConverterVDoubleArrayFunction(expression.getFunction()), expression.getName());
    }

    /**
     * A channel with the given name of type VDouble.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static ChannelExpression<VDouble, Double> vDouble(String name) {
        return channel(name, VDouble.class, Double.class);
    }

    /**
     * A channel with the given name of type VFloatArray.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static ChannelExpression<VFloatArray, float[]> vFloatArray(String name) {
        return channel(name, VFloatArray.class, float[].class);
    }

    /**
     * A channel with the given name of type VDoubleArray.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static ChannelExpression<VDoubleArray, float[]> vDoubleArray(String name) {
        return channel(name, VDoubleArray.class, float[].class);
    }

    /**
     * A channel with the given name of type VInt.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static ChannelExpression<VInt, Integer> vInt(String name) {
        return channel(name, VInt.class, Integer.class);
    }

    /**
     * A channel with the given name of type VByteArray.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static ChannelExpression<VByteArray, byte[]> vByteArray(String name) {
        return channel(name, VByteArray.class, byte[].class);
    }

    /**
     * A channel with the given name of type VShortArray.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static ChannelExpression<VShortArray, short[]> vShortArray(String name) {
        return channel(name, VShortArray.class, short[].class);
    }

    /**
     * A channel with the given name of type VIntArray.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static ChannelExpression<VIntArray, int[]> vIntArray(String name) {
        return channel(name, VIntArray.class, int[].class);
    }

    /**
     * A channel with the given name of type VString.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static ChannelExpression<VString, String> vString(String name) {
        return channel(name, VString.class, String.class);
    }

    /**
     * A channel with the given name of type VStringArray.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static ChannelExpression<VStringArray, String[]> vStringArray(String name) {
        return channel(name, VStringArray.class, String[].class);
    }

    /**
     * A channel with the given name of type VEnum.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static ChannelExpression<VEnum, Integer> vEnum(String name) {
        return channel(name, VEnum.class, Integer.class);
    }

    /**
     * A list of channels with the given names, all of type VDouble.
     *
     * @param names the channel names; can't be null
     * @return a list of expressions representing the channels
     */
    public static ChannelExpressionList<VDouble, Double> vDoubles(List<String> names) {
        return channels(names, VDouble.class, Double.class);
    }

    /**
     * A list of constant expressions of type VDouble.
     */
    public static DesiredRateExpressionList<VDouble> vDoubleConstants(List<Double> values) {
        DesiredRateExpressionList<VDouble> list = new DesiredRateExpressionListImpl<VDouble>();
        for (Double value : values) {
            list.and(constant(ValueFactory.newVDouble(value, AlarmSeverity.NONE, AlarmStatus.NONE, TimeStamp.now(), null, null, null, null, null, null, null, null, null, null, null)));
        }
        return list;
    }

    /**
     * A list of constant expressions of type VDouble.
     */
    public static DesiredRateExpressionList<VInt> vIntConstants(List<Integer> values) {
        DesiredRateExpressionList<VInt> list = new DesiredRateExpressionListImpl<VInt>();
        for (Integer value : values) {
            list.and(constant(ValueFactory.newVInt(value, AlarmSeverity.NONE, AlarmStatus.NONE, TimeStamp.now(), null, null, null, null, null, null, null, null, null, null, null)));
        }
        return list;
    }

    /**
     * A list of constant expressions of type VString.
     */
    public static DesiredRateExpressionList<VString> vStringConstants(List<String> values) {
        DesiredRateExpressionList<VString> list = new DesiredRateExpressionListImpl<VString>();
        for (String value : values) {
            list.and(constant(ValueFactory.newVString(value, AlarmSeverity.NONE, AlarmStatus.NONE, TimeStamp.now(), null)));
        }
        return list;
    }

    /**
     * Aggregates the sample at the scan rate and takes the average.
     * @param doublePv the expression to take the average of; can't be null
     * @return an expression representing the average of the expression
     */
    public static DesiredRateExpression<VDouble> averageOf(SourceRateExpression<VDouble> doublePv) {
        DesiredRateExpression<List<VDouble>> queue = newValuesOf(doublePv);
        Collector<VDouble> collector = (Collector<VDouble>) queue.getFunction();
        return new DesiredRateExpressionImpl<VDouble>(queue,
                new AverageAggregator(collector), "avg(" + doublePv.getName() + ")");
    }

    /**
     * Aggregates the sample at the scan rate and calculates statistical information.
     *
     * @param doublePv the expression to calculate the statistics information on; can't be null
     * @return an expression representing the statistical information of the expression
     */
    public static DesiredRateExpression<VStatistics> statisticsOf(SourceRateExpression<VDouble> doublePv) {
        DesiredRateExpression<List<VDouble>> queue = newValuesOf(doublePv);
        Collector<VDouble> collector = (Collector<VDouble>) queue.getFunction();
        return new DesiredRateExpressionImpl<VStatistics>(queue,
                new StatisticsDoubleAggregator(collector), "stats(" + doublePv.getName() + ")");
    }

    /**
     * Applies {@link #statisticsOf(org.epics.pvmanager.SourceRateExpression)} to all
     * arguments.
     *
     * @param doubleExpressions a list of double expressions
     * @return a list of statistical expressions
     */
    public static DesiredRateExpressionList<VStatistics> statisticsOf(SourceRateExpressionList<VDouble> doubleExpressions) {
        DesiredRateExpressionList<VStatistics> expressions = new DesiredRateExpressionListImpl<VStatistics>();
        for (SourceRateExpression<VDouble> doubleExpression : doubleExpressions.getSourceRateExpressions()) {
            expressions.and(statisticsOf(doubleExpression));
        }
        return expressions;
    }

    /**
     * A synchronized array from the given expression.
     *
     * @param tolerance maximum time difference between samples
     * @param expressions the expressions from which to reconstruct the array
     * @return an expression for the array
     */
    public static DesiredRateExpression<VMultiDouble>
            synchronizedArrayOf(TimeDuration tolerance, SourceRateExpressionList<VDouble> expressions) {
        return synchronizedArrayOf(tolerance, tolerance.multiplyBy(10), expressions);
    }

    /**
     * A synchronized array from the given expression.
     *
     * @param tolerance maximum time difference between samples in the
     * reconstructed array
     * @param cacheDepth maximum time difference between samples in the caches
     * used to reconstruct the array
     * @param expressions the expressions from which to reconstruct the array
     * @return an expression for the array
     */
    public static DesiredRateExpression<VMultiDouble>
            synchronizedArrayOf(TimeDuration tolerance, TimeDuration cacheDepth, SourceRateExpressionList<VDouble> expressions) {
        if (cacheDepth.equals(TimeDuration.ms(0)))
            throw new IllegalArgumentException("Distance between samples must be non-zero");
        List<String> names = new ArrayList<String>();
        List<Function<List<VDouble>>> collectors = new ArrayList<Function<List<VDouble>>>();
        DesiredRateExpressionList<List<VDouble>> desiredRateExpressions = new DesiredRateExpressionListImpl<List<VDouble>>();
        for (SourceRateExpression<VDouble> expression : expressions.getSourceRateExpressions()) {
            DesiredRateExpression<List<VDouble>> collectorExp = timedCacheOf(expression, cacheDepth);
            desiredRateExpressions.and(collectorExp);
            collectors.add(collectorExp.getFunction());
            names.add(expression.getName());
        }
        SynchronizedVDoubleAggregator aggregator =
                new SynchronizedVDoubleAggregator(names, collectors, tolerance);
        return new DesiredRateExpressionImpl<VMultiDouble>(desiredRateExpressions,
                (Function<VMultiDouble>) aggregator, "syncArray");
    }

    /**
     * A column for an aggregated vTable.
     * 
     * @param name the name of the column
     * @param values the value of the column
     * @return the column
     */
    public static VTableColumn column(String name, DesiredRateExpressionList<?> values) {
        return new VTableColumn(name, values);
    }
    
    /**
     * Creates a vTable by aggregating different values from different pvs.
     * 
     * @param columns columns of the table
     * @return an expression for the table
     */
    public static DesiredRateExpression<VTable> vTable(VTableColumn... columns) {
        DesiredRateExpressionListImpl<Object> list = new DesiredRateExpressionListImpl<Object>();
        List<List<Function<?>>> functions = new ArrayList<List<Function<?>>>();
        List<String> names = new ArrayList<String>();
        for (VTableColumn column : columns) {
            functions.add(Expressions.functionsOf(column.getValueExpressions()));
            list.and(column.getValueExpressions());
            names.add(column.getName());
        }
        return new DesiredRateExpressionImpl<VTable>(list, new VTableAggregationFunction(functions, names), "table");
    }
}
