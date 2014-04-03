/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.graphene;

import org.epics.graphene.NLineGraphs2DRendererUpdate;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.expression.DesiredRateExpressionImpl;
import static org.epics.pvmanager.graphene.ExpressionLanguage.functionOf;

/**
 *
 * @author carcassi
 */
public class NLineGraphs2DExpression extends DesiredRateExpressionImpl<Graph2DResult> implements Graph2DExpression<NLineGraphs2DRendererUpdate> {

    NLineGraphs2DExpression(DesiredRateExpression<?> tableData,
	    DesiredRateExpression<?> xColumnName,
	    DesiredRateExpression<?> yColumnName) {
        super(ExpressionLanguage.<Object>createList(tableData, xColumnName, yColumnName),
                new NLineGraphs2DFunction(functionOf(tableData),
                functionOf(xColumnName), functionOf(yColumnName)),
                "Multi-axis Line Graph");
    }
    
    @Override
    public void update(NLineGraphs2DRendererUpdate update) {
        ((NLineGraphs2DFunction) getFunction()).getRendererUpdateQueue().writeValue(update);
    }

    @Override
    public NLineGraphs2DRendererUpdate newUpdate() {
        return new NLineGraphs2DRendererUpdate();
    }
}
