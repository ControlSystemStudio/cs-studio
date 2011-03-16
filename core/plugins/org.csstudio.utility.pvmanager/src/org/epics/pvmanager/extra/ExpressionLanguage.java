/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.extra;

import org.epics.pvmanager.data.*;
import java.util.ArrayList;
import java.util.List;
import org.epics.pvmanager.DesiredRateExpression;
import org.epics.pvmanager.Collector;
import org.epics.pvmanager.NotificationSupport;
import org.epics.pvmanager.SourceRateExpression;
import org.epics.pvmanager.Function;
import org.epics.pvmanager.TimeSupport;
import org.epics.pvmanager.util.TimeDuration;
import static org.epics.pvmanager.ExpressionLanguage.*;

/**
 * PVManager expression language support for additional operations.
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
     * Aggregates the sample at the scan rate and takes the average.
     * @param doublePv the expression to take the average of; can't be null
     * @return an expression representing the average of the expression
     */
    public static DesiredRateExpression<VImage> waterfallPlotOf(SourceRateExpression<VDoubleArray> arrayPv) {
        DesiredRateExpression<List<VDoubleArray>> queue = newValuesOf(arrayPv);
        return new DesiredRateExpression<VImage>(queue,
                new WaterfallPlotter(queue.getFunction()), "waterfallOf(" + arrayPv.getDefaultName() + ")");
    }

    public static DesiredRateExpression<VImage> waterfallPlotOf(SourceRateExpression<VDoubleArray> arrayPv,
            WaterfallPlotParameters parameters) {
        DesiredRateExpression<List<VDoubleArray>> queue = newValuesOf(arrayPv);
        return new DesiredRateExpression<VImage>(queue,
                new WaterfallPlotter(queue.getFunction(), parameters), "waterfallOf(" + arrayPv.getDefaultName() + ")");
    }

}
