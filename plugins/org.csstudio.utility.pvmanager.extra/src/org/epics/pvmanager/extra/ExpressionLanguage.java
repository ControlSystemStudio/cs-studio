/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.extra;

import org.epics.vtype.VNumberArray;
import org.epics.vtype.VNumber;
import org.epics.pvmanager.vtype.DataTypeSupport;
import java.util.List;
import org.epics.pvmanager.BasicTypeSupport;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.expression.DesiredRateExpressionList;
import org.epics.pvmanager.expression.SourceRateExpression;
import org.epics.pvmanager.expression.SourceRateExpressionList;
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
        // Add support for Basic types
        BasicTypeSupport.install();
    }

    /**
     * Aggregates the sample at the scan rate and takes the average.
     * 
     * @param arrayPv the expression to take the average of; can't be null
     * @return an expression representing the average of the expression
     */
    public static <T extends VNumberArray> WaterfallPlot  waterfallPlotOf(SourceRateExpression<T> arrayPv) {
        DesiredRateExpression<List<T>> queue = newValuesOf(arrayPv);
        return new WaterfallPlot(queue, "waterfallOf(" + arrayPv.getName() + ")");
    }

    /**
     * Creates a waterfall plot from a series of scalar pvs.
     * 
     * @param vDoubles the expression to take the average of; can't be null
     * @return an expression representing the average of the expression
     */
    public static <T extends VNumber> WaterfallPlot waterfallPlotOf(SourceRateExpressionList<T> vDoubles) {
        DesiredRateExpressionList<List<T>> queue = newValuesOf(vDoubles);
        return new WaterfallPlot(queue, "waterfallOf");
    }
    
    /**
     * Creates a group of dynamically managed expressions.
     * 
     * @return a new group
     */
    public static DynamicGroup group() {
        return new DynamicGroup();
    }

}
