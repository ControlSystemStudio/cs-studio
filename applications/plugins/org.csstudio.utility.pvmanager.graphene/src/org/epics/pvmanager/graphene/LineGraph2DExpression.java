/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.graphene;

import org.epics.graphene.LineGraph2DRendererUpdate;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.expression.DesiredRateExpressionImpl;
import org.epics.pvmanager.expression.DesiredRateExpressionList;
import static org.epics.pvmanager.graphene.ExpressionLanguage.functionOf;

/**
 *
 * @author carcassi
 */
public class LineGraph2DExpression extends DesiredRateExpressionImpl<Graph2DResult> implements Graph2DExpression<LineGraph2DRendererUpdate> {

    LineGraph2DExpression(DesiredRateExpression<?> tableData,
	    DesiredRateExpression<?> xColumnName,
	    DesiredRateExpression<?> yColumnName,
	    DesiredRateExpression<?> tooltipColumnName) {
        super(ExpressionLanguage.<Object>createList(tableData, xColumnName, yColumnName, tooltipColumnName),
                new LineGraph2DFunction(functionOf(tableData),
                functionOf(xColumnName), functionOf(yColumnName), functionOf(tooltipColumnName)),
                "Scatter Graph");
    }
    
    @Override
    public void update(LineGraph2DRendererUpdate update) {
        ((LineGraph2DFunction) getFunction()).getRendererUpdateQueue().writeValue(update);
    }

    @Override
    public LineGraph2DRendererUpdate newUpdate() {
        return new LineGraph2DRendererUpdate();
    }
}
