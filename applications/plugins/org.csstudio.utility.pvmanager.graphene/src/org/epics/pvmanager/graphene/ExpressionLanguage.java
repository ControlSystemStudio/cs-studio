/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.graphene;

import java.util.List;
import org.epics.pvmanager.BasicTypeSupport;
import static org.epics.pvmanager.ExpressionLanguage.*;
import org.epics.pvmanager.NotificationSupport;
import org.epics.pvmanager.ReadFunction;
import org.epics.pvmanager.TypeSupport;
import org.epics.pvmanager.vtype.DataTypeSupport;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.expression.DesiredRateExpressionList;
import org.epics.pvmanager.expression.DesiredRateExpressionListImpl;
import org.epics.pvmanager.expression.SourceRateExpression;
import org.epics.vtype.VString;
import org.epics.vtype.VTable;

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
    
    public static LineGraph2DExpression lineGraphOf(
	    DesiredRateExpression<?> tableData,
	    DesiredRateExpression<?> xColumnName,
	    DesiredRateExpression<?> yColumnName,
	    DesiredRateExpression<?> tooltipColumnName) {
	return new LineGraph2DExpression(tableData, xColumnName, yColumnName, tooltipColumnName);
    }
    
    public static ScatterGraph2DExpression scatterGraphOf(
	    DesiredRateExpression<?> tableData,
	    DesiredRateExpression<?> xColumnName,
	    DesiredRateExpression<?> yColumnName,
	    DesiredRateExpression<?> tooltipColumnName) {
	return new ScatterGraph2DExpression(tableData, xColumnName, yColumnName, tooltipColumnName);
    }

    @SafeVarargs
    static <T> DesiredRateExpressionList<T> createList(DesiredRateExpressionList<? extends T>... expressions) {
        DesiredRateExpressionList<T> list = new DesiredRateExpressionListImpl<T>();
        for (DesiredRateExpressionList<? extends T> exp : expressions) {
            if (exp != null) {
                list.and(exp);
            }
        }
        return list;
    }
    
    static <T> ReadFunction<T> functionOf(DesiredRateExpression<T> exp) {
        if (exp == null) {
            return null;
        } else {
            return exp.getFunction();
        }
    }

}
