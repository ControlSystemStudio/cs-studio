/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.graphene;

import java.util.List;
import org.epics.pvmanager.BasicTypeSupport;
import static org.epics.pvmanager.ExpressionLanguage.*;
import org.epics.pvmanager.NotificationSupport;
import org.epics.pvmanager.TypeSupport;
import org.epics.pvmanager.vtype.DataTypeSupport;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.expression.DesiredRateExpressionListImpl;
import org.epics.pvmanager.expression.SourceRateExpression;

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
        TypeSupport.addTypeSupport(NotificationSupport.immutableTypeSupport(Graph2DResult.class));
    }

    public static AreaGraph2DExpression histogramOf(SourceRateExpression<? extends VNumber> vDoubles) {
        DesiredRateExpression<? extends List<? extends VNumber>> queue = newValuesOf(vDoubles);
        return new AreaGraph2DExpression(queue, new AreaGraph2DFunction(queue.getFunction()), "histogram");
    }

    public static AreaGraph2DExpression histogramOf(DesiredRateExpression<? extends List<? extends VNumber>> vDoubles) {
        return new AreaGraph2DExpression(vDoubles, new AreaGraph2DFunction(vDoubles.getFunction()), "histogram");
    }

    public static LineGraph2DExpression lineGraphOf(DesiredRateExpression<? extends VNumberArray> vDoubleArray) {
        return new LineGraph2DExpression(vDoubleArray, new LineGraph2DFunction(vDoubleArray.getFunction()), "lineGraph");
    }

    public static LineGraph2DExpression lineGraphOf(DesiredRateExpression<? extends VNumberArray> yArray,
            DesiredRateExpression<? extends VNumber> xInitialOffset,
            DesiredRateExpression<? extends VNumber> xIncrementSize) {
        return new LineGraph2DExpression(new DesiredRateExpressionListImpl<Object>().and(yArray).and(xInitialOffset).and(xIncrementSize),
                new LineGraph2DFunction(yArray.getFunction(), xInitialOffset.getFunction(), xIncrementSize.getFunction()), "lineGraph");
    }

    public static LineGraph2DExpression lineGraphOf(DesiredRateExpression<? extends VNumberArray> xVDoubleArray, DesiredRateExpression<? extends VNumberArray> yVDoubleArray) {
        return new LineGraph2DExpression(new DesiredRateExpressionListImpl<Object>().and(xVDoubleArray).and(yVDoubleArray),
                new LineGraph2DFunction(xVDoubleArray.getFunction(), yVDoubleArray.getFunction()), "lineGraph");
    }

    public static ScatterGraph2DExpression scatterGraphOf(
	    DesiredRateExpression<? extends VNumberArray> xNumberArray,
	    DesiredRateExpression<? extends VNumberArray> yNumberArray) {
	return new ScatterGraph2DExpression(new DesiredRateExpressionListImpl<>().and(
		xNumberArray).and(yNumberArray), new ScatterGraph2DFunction(
		yNumberArray.getFunction(), xNumberArray.getFunction()),
		"ScatterGraph");

    }

}
