/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.graphene;

import org.epics.graphene.MultiAxisLineGraph2DRendererUpdate;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.expression.DesiredRateExpressionImpl;
import static org.epics.pvmanager.graphene.ExpressionLanguage.functionOf;

/**
 *
 * @author carcassi
 */
public class MultiYAxisGraph2DExpression extends DesiredRateExpressionImpl<Graph2DResult> implements Graph2DExpression<MultiAxisLineGraph2DRendererUpdate> {

    MultiYAxisGraph2DExpression(DesiredRateExpression<?> tableData,
	    DesiredRateExpression<?> xColumnName,
	    DesiredRateExpression<?> yColumnName) {
        super(ExpressionLanguage.<Object>createList(tableData, xColumnName, yColumnName),
                new MultiYAxisGraph2DFunction(functionOf(tableData),
                functionOf(xColumnName), functionOf(yColumnName)),
                "Multi-axis Line Graph");
    }
    
    @Override
    public void update(MultiAxisLineGraph2DRendererUpdate update) {
        ((MultiYAxisGraph2DFunction) getFunction()).getRendererUpdateQueue().writeValue(update);
    }

    @Override
    public MultiAxisLineGraph2DRendererUpdate newUpdate() {
        return new MultiAxisLineGraph2DRendererUpdate();
    }
}
