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
import static org.epics.pvmanager.ExpressionLanguage.*;
import static org.epics.pvmanager.data.ValueFactory.*;
import org.epics.pvmanager.util.TimeStamp;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListInt;
import org.epics.util.array.ListNumber;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;

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
     * @deprecated use {@link #vNumber(java.lang.String) }
     * @param expression an expression that returns a numeric scalar
     * @return a new expression
     */
    @Deprecated
    public static SourceRateExpression<VDouble> vDoubleOf(SourceRateExpression<?> expression) {
        return new SourceRateExpressionImpl<VDouble>(expression, new ConverterVDoubleFunction(expression.getFunction()), expression.getName());
    }
    
    /**
     * Expects a numeric array (VDoubleArray, VFloatArray, VIntArray, VShortArray
     * or VByteArray) and converts it to a VDoubleArray.
     * 
     * @deprecated use {@link #vNumberArray(java.lang.String) }
     * @param expression an expression that returns a numeric array
     * @return a new expression
     */
    @Deprecated
    public static SourceRateExpression<VDoubleArray> vDoubleArrayOf(SourceRateExpression<?> expression) {
        return new SourceRateExpressionImpl<VDoubleArray>(expression, new ConverterVDoubleArrayFunction(expression.getFunction()), expression.getName());
    }
    
    /**
     * Transforms a list of numeric scalar into a double array.
     * 
     * @param expressions a list of numeric expressions
     * @return a new double array expression
     */
    public static DesiredRateExpression<VDoubleArray>
            vDoubleArrayOf(DesiredRateExpressionList<? extends VNumber> expressions) {
        // TODO - there should be a common function to extract the list of functions
        List<Function<? extends VNumber>> functions = new ArrayList<Function<? extends VNumber>>();
        for (DesiredRateExpression<? extends VNumber> expression : expressions.getDesiredRateExpressions()) {
            functions.add(expression.getFunction());
        }
        VNumbersToVDoubleArrayConverter converter =
                new VNumbersToVDoubleArrayConverter(functions);
        return new DesiredRateExpressionImpl<VDoubleArray>(expressions, converter, "syncArray");
    }
    
    //
    // Channel expressions
    //

    /**
     * A channel with the given name that returns any of the value type.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static ChannelExpression<VType, Object> vType(String name) {
        return channel(name, VType.class, Object.class);
    }

    /**
     * A channel with the given name of type VNumber.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static ChannelExpression<VNumber, Number> vNumber(String name) {
        return channel(name, VNumber.class, Number.class);
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
     * A channel with the given name of type VInt.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static ChannelExpression<VInt, Integer> vInt(String name) {
        return channel(name, VInt.class, Integer.class);
    }

    /**
     * A channel with the given name of type VNumberArray.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static ChannelExpression<VNumberArray, ListNumber> vNumberArray(String name) {
        return channel(name, VNumberArray.class, ListNumber.class);
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
    
    //
    // Constant expressions
    //
    
    /**
     * A constant representing a double. Alarm will be none, timestamp now
     * and no display information.
     * 
     * @param value the constant value
     * @return a double expression
     */
    public static DesiredRateExpression<VDouble> vConst(double value) {
        return constant(newVDouble(value, alarmNone(), newTime(Timestamp.now()), displayNone()), Double.toString(value));
    }
    
    /**
     * A constant representing an int. Alarm will be none, timestamp now
     * and no display information.
     * 
     * @param value the constant value
     * @return an int expression
     */
    public static DesiredRateExpression<VInt> vConst(int value) {
        return constant(newVInt(value, alarmNone(), newTime(Timestamp.now()), displayNone()), Integer.toString(value));
    }
    
    /**
     * A constant representing a double array. Alarm will be none, timestamp now
     * and no display information.
     * 
     * @param values the constant values
     * @return a double array expression
     */
    public static DesiredRateExpression<VDoubleArray> vConst(double... values) {
        return constant(newVDoubleArray(values, alarmNone(), newTime(Timestamp.now()), displayNone()));
    }
    
    /**
     * A constant representing a double array. Alarm will be none, timestamp now
     * and no display information.
     * 
     * @param values the constant values
     * @return a double array expression
     */
    public static DesiredRateExpression<VDoubleArray> vConst(ListDouble values) {
        return constant(newVDoubleArray(values, alarmNone(), newTime(Timestamp.now()), displayNone()));
    }
    
    /**
     * A constant representing an int array. Alarm will be none, timestamp now
     * and no display information.
     * 
     * @param values the constant values
     * @return an int array expression
     */
    public static DesiredRateExpression<VIntArray> vConst(int... values) {
        return constant(newVIntArray(values, alarmNone(), newTime(Timestamp.now()), displayNone()));
    }
    
    /**
     * A constant representing an int array. Alarm will be none, timestamp now
     * and no display information.
     * 
     * @param values the constant values
     * @return an int array expression
     */
    public static DesiredRateExpression<VIntArray> vConst(ListInt values) {
        return constant(newVIntArray(values, alarmNone(), newTime(Timestamp.now()), displayNone()));
    }

    /**
     * A list of constant expressions of type VDouble.
     * 
     * @param values the list of constants
     * @return a list of double expression
     */
    public static DesiredRateExpressionList<VDouble> vDoubleConstants(List<Double> values) {
        DesiredRateExpressionList<VDouble> list = new DesiredRateExpressionListImpl<VDouble>();
        for (Double value : values) {
            list.and(constant(newVDouble(value, alarmNone(), newTime(Timestamp.now()), displayNone())));
        }
        return list;
    }

    /**
     * A list of constant expressions of type VDouble.
     * 
     * @param values the list of constants
     * @return a list of int expression
     */
    public static DesiredRateExpressionList<VInt> vIntConstants(List<Integer> values) {
        DesiredRateExpressionList<VInt> list = new DesiredRateExpressionListImpl<VInt>();
        for (Integer value : values) {
            list.and(constant(newVInt(value, alarmNone(), timeNow(), displayNone())));
        }
        return list;
    }

    /**
     * A list of constant expressions of type VString.
     * 
     * @param values the list of constants
     * @return a list of string expression
     */
    public static DesiredRateExpressionList<VString> vStringConstants(List<String> values) {
        DesiredRateExpressionList<VString> list = new DesiredRateExpressionListImpl<VString>();
        for (String value : values) {
            list.and(constant(newVString(value, alarmNone(), timeNow())));
        }
        return list;
    }

    /**
     * Aggregates the sample at the scan rate and takes the average.
     * 
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
     * Applies {@link #statisticsOf(org.epics.pvmanager.expression.SourceRateExpression)} to all
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
        return synchronizedArrayOf(tolerance, tolerance.multipliedBy(10), expressions);
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
        if (cacheDepth.equals(TimeDuration.ofMillis(0)) && cacheDepth.getSec() > 0)
            throw new IllegalArgumentException("Distance between samples must be non-zero and positive");
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
     * A synchronized array from the given expression.
     *
     * @param tolerance maximum time difference between samples
     * @param expressions the expressions from which to reconstruct the array
     * @return an expression for the array
     * @deprecated use {@link #synchronizedArrayOf(org.epics.util.time.TimeDuration, org.epics.pvmanager.expression.SourceRateExpressionList) }
     */
    @Deprecated
    public static DesiredRateExpression<VMultiDouble>
            synchronizedArrayOf(org.epics.pvmanager.util.TimeDuration tolerance, SourceRateExpressionList<VDouble> expressions) {
        return synchronizedArrayOf(org.epics.pvmanager.util.TimeDuration.asTimeDuration(tolerance), expressions);
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
     * @deprecated {@link #synchronizedArrayOf(org.epics.util.time.TimeDuration, org.epics.util.time.TimeDuration, org.epics.pvmanager.expression.SourceRateExpressionList) }
     */
    @Deprecated
    public static DesiredRateExpression<VMultiDouble>
            synchronizedArrayOf(org.epics.pvmanager.util.TimeDuration tolerance, org.epics.pvmanager.util.TimeDuration cacheDepth, SourceRateExpressionList<VDouble> expressions) {
        return synchronizedArrayOf(org.epics.pvmanager.util.TimeDuration.asTimeDuration(tolerance),
                org.epics.pvmanager.util.TimeDuration.asTimeDuration(cacheDepth), expressions);
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
