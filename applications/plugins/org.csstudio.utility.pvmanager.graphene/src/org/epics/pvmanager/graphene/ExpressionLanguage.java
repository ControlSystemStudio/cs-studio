/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.graphene;

import org.epics.pvmanager.data.*;
import java.util.List;
import org.epics.pvmanager.BasicTypeSupport;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.expression.DesiredRateExpressionList;
import org.epics.pvmanager.expression.SourceRateExpression;
import org.epics.pvmanager.expression.SourceRateExpressionList;
import static org.epics.pvmanager.ExpressionLanguage.*;
import org.epics.pvmanager.expression.DesiredRateExpressionImpl;

/**
 *
 * @author carcassi
 */
public class ExpressionLanguage {
    private ExpressionLanguage() {}

    static {
        // Add support for Epics types.
        DataTypeSupport.install();
        // Add support for Basic types
        BasicTypeSupport.install();
    }

    public static Histogram1DPlot histogramOf(SourceRateExpression<VDouble> vDoubles) {
        DesiredRateExpression<List<VDouble>> queue = newValuesOf(vDoubles);
        return new Histogram1DPlot(queue, new Histogram1DFunction(queue.getFunction()), "histogram");
    }

    public static LineGraphPlot lineGraphOf(SourceRateExpression<VDoubleArray> vDoubleArray) {
        DesiredRateExpression<VDoubleArray> queue = latestValueOf(vDoubleArray);
        return new LineGraphPlot(queue, new LineGraphFunction(queue.getFunction()), "lineGraph");
    }

}
