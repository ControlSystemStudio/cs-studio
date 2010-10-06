/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager;

import java.util.List;

/**
 * Operators to constructs expression of PVs that the {@link PVManager} will
 * be able to monitor.
 *
 * @author carcassi
 */
public class ExpressionLanguage {
    private ExpressionLanguage() {}

    public static <T> DesiredRateExpression<List<T>>
            queueOf(SourceRateExpression<T> expression) {
        return new DesiredRateExpression<List<T>>(expression,
                new QueueCollector<T>(expression.getFunction()),
                expression.getDefaultName());
    }

    public static <T> DesiredRateExpression<List<T>>
            timedCacheOf(SourceRateExpression<T> expression, TimeDuration maxIntervalBetweenSamples) {
        return new DesiredRateExpression<List<T>>(expression,
                new TimedCacheCollector<T>(expression.getFunction(), maxIntervalBetweenSamples),
                expression.getDefaultName());
    }

    public static <T> DesiredRateExpression<T> latestValueOf(SourceRateExpression<T> expression) {
        DesiredRateExpression<List<T>> queue = queueOf(expression);
        return new DesiredRateExpression<T>(queue,
                new LastValueAggregator<T>(expression.getFunction().getType(), (Collector<T>) queue.getFunction()),
                expression.getDefaultName());
    }

}
