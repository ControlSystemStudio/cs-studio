/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.graphene;

import org.epics.graphene.ScatterGraph2DRendererUpdate;
import org.epics.pvmanager.ReadFunction;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.expression.DesiredRateExpressionImpl;
import org.epics.pvmanager.expression.DesiredRateExpressionList;
import org.epics.pvmanager.expression.DesiredRateExpressionListImpl;
import org.epics.vtype.VString;
import org.epics.vtype.VTable;
import static org.epics.pvmanager.graphene.ExpressionLanguage.*;
        
/**
 * @author shroffk
 *
 */
public class ScatterGraph2DExpression extends DesiredRateExpressionImpl<Graph2DResult> implements Graph2DExpression<ScatterGraph2DRendererUpdate> {

    ScatterGraph2DExpression(DesiredRateExpressionList<?> childExpressions,
            ReadFunction<Graph2DResult> function, String defaultName) {
        super(childExpressions, function, defaultName);
    }

    ScatterGraph2DExpression(DesiredRateExpression<?> tableData,
	    DesiredRateExpression<?> xColumnName,
	    DesiredRateExpression<?> yColumnName,
	    DesiredRateExpression<?> tooltipColumnName) {
        super(ExpressionLanguage.<Object>createList(tableData, xColumnName, yColumnName, tooltipColumnName),
                new ScatterGraph2DFunction(functionOf(tableData),
                functionOf(xColumnName), functionOf(yColumnName), functionOf(tooltipColumnName)),
                "Scatter Graph");
    }

    @Override
    public void update(ScatterGraph2DRendererUpdate update) {
        if (getFunction() instanceof ScatterGraph2DFunction) {
            ((ScatterGraph2DFunction) getFunction()).getRendererUpdateQueue().writeValue(update);
        }
    }

    @Override
    public ScatterGraph2DRendererUpdate newUpdate() {
        return new ScatterGraph2DRendererUpdate();
    }
}
