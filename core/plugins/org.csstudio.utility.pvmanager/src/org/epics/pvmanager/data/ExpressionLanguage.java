/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.data;

import java.util.ArrayList;
import java.util.List;
import org.epics.pvmanager.DesiredRateExpression;
import org.epics.pvmanager.Collector;
import org.epics.pvmanager.SourceRateExpression;
import org.epics.pvmanager.Function;
import org.epics.pvmanager.SourceRateExpressionImpl;
import org.epics.pvmanager.util.TimeDuration;
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
     * A channel with the given name of type VDouble.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static SourceRateExpression<VDouble> vDouble(String name) {
        return new SourceRateExpressionImpl<VDouble>(name, VDouble.class);
    }

    /**
     * A channel with the given name of type VFloatArray.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static SourceRateExpression<VFloatArray> vFloatArray(String name) {
        return new SourceRateExpressionImpl<VFloatArray>(name, VFloatArray.class);
    }

    /**
     * A channel with the given name of type VDoubleArray.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static SourceRateExpression<VDoubleArray> vDoubleArray(String name) {
        return new SourceRateExpressionImpl<VDoubleArray>(name, VDoubleArray.class);
    }

    /**
     * A channel with the given name of type VInt.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static SourceRateExpression<VInt> vInt(String name) {
        return new SourceRateExpressionImpl<VInt>(name, VInt.class);
    }

    /**
     * A channel with the given name of type VByteArray.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static SourceRateExpression<VByteArray> vByteArray(String name) {
        return new SourceRateExpressionImpl<VByteArray>(name, VByteArray.class);
    }

    /**
     * A channel with the given name of type VShortArray.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static SourceRateExpression<VShortArray> vShortArray(String name) {
        return new SourceRateExpressionImpl<VShortArray>(name, VShortArray.class);
    }

    /**
     * A channel with the given name of type VIntArray.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static SourceRateExpression<VIntArray> vIntArray(String name) {
        return new SourceRateExpressionImpl<VIntArray>(name, VIntArray.class);
    }

    /**
     * A channel with the given name of type VString.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static SourceRateExpression<VString> vString(String name) {
        return new SourceRateExpressionImpl<VString>(name, VString.class);
    }

    /**
     * A channel with the given name of type VStringArray.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static SourceRateExpression<VStringArray> vStringArray(String name) {
        return new SourceRateExpressionImpl<VStringArray>(name, VStringArray.class);
    }

    /**
     * A channel with the given name of type VEnum.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static SourceRateExpression<VEnum> vEnum(String name) {
        return new SourceRateExpressionImpl<VEnum>(name, VEnum.class);
    }

    /**
     * A list of channels with the given names, all of type VDouble.
     *
     * @param names the channel names; can't be null
     * @return a list of expressions representing the channels
     */
    public static List<SourceRateExpression<VDouble>> vDoubles(List<String> names) {
        List<SourceRateExpression<VDouble>> expressions = new ArrayList<SourceRateExpression<VDouble>>();
        for (String name : names) {
            expressions.add(vDouble(name));
        }
        return expressions;
    }

    /**
     * Aggregates the sample at the scan rate and takes the average.
     * @param doublePv the expression to take the average of; can't be null
     * @return an expression representing the average of the expression
     */
    public static DesiredRateExpression<VDouble> averageOf(SourceRateExpression<VDouble> doublePv) {
        DesiredRateExpression<List<VDouble>> queue = newValuesOf(doublePv);
        Collector<VDouble> collector = (Collector<VDouble>) queue.getFunction();
        return new DesiredRateExpression<VDouble>(queue,
                new AverageAggregator(collector), "avg(" + doublePv.getDefaultName() + ")");
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
        return new DesiredRateExpression<VStatistics>(queue,
                new StatisticsDoubleAggregator(collector), "stats(" + doublePv.getDefaultName() + ")");
    }

    /**
     * Applies {@link #statisticsOf(org.epics.pvmanager.SourceRateExpression)} to all
     * arguments.
     *
     * @param doubleExpressions a list of double expressions
     * @return a list of statistical expressions
     */
    public static List<DesiredRateExpression<VStatistics>> statisticsOf(List<SourceRateExpression<VDouble>> doubleExpressions) {
        List<DesiredRateExpression<VStatistics>> expressions = new ArrayList<DesiredRateExpression<VStatistics>>();
        for (SourceRateExpression<VDouble> doubleExpression : doubleExpressions) {
            expressions.add(statisticsOf(doubleExpression));
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
            synchronizedArrayOf(TimeDuration tolerance, List<SourceRateExpression<VDouble>> expressions) {
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
            synchronizedArrayOf(TimeDuration tolerance, TimeDuration cacheDepth, List<SourceRateExpression<VDouble>> expressions) {
        if (cacheDepth.equals(TimeDuration.ms(0)))
            throw new IllegalArgumentException("Distance between samples must be non-zero");
        List<String> names = new ArrayList<String>();
        List<DesiredRateExpression<?>> collectorExps = new ArrayList<DesiredRateExpression<?>>();
        List<Function<List<VDouble>>> collectors = new ArrayList<Function<List<VDouble>>>();
        for (SourceRateExpression<VDouble> expression : expressions) {
            DesiredRateExpression<List<VDouble>> collectorExp = timedCacheOf(expression, cacheDepth);
            collectorExps.add(collectorExp);
            collectors.add(collectorExp.getFunction());
            names.add(expression.getDefaultName());
        }
        SynchronizedVDoubleAggregator aggregator =
                new SynchronizedVDoubleAggregator(names, collectors, tolerance);
        return new DesiredRateExpression<VMultiDouble>(collectorExps,
                (Function<VMultiDouble>) aggregator, "syncArray");
    }

}
