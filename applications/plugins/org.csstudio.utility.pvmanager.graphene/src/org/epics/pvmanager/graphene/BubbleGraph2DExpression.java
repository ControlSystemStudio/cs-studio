/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.graphene;

import org.epics.graphene.BubbleGraph2DRendererUpdate;
import org.epics.pvmanager.ReadFunction;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.expression.DesiredRateExpressionImpl;
import org.epics.pvmanager.expression.DesiredRateExpressionList;
import static org.epics.pvmanager.graphene.ExpressionLanguage.*;
        
/**
 * @author shroffk
 *
 */
public class BubbleGraph2DExpression extends DesiredRateExpressionImpl<Graph2DResult> implements Graph2DExpression<BubbleGraph2DRendererUpdate> {

    BubbleGraph2DExpression(DesiredRateExpressionList<?> childExpressions,
            ReadFunction<Graph2DResult> function, String defaultName) {
        super(childExpressions, function, defaultName);
    }

    BubbleGraph2DExpression(DesiredRateExpression<?> tableData,
	    DesiredRateExpression<?> xColumnName,
	    DesiredRateExpression<?> yColumnName,
	    DesiredRateExpression<?> sizeColumnName,
	    DesiredRateExpression<?> tooltipColumnName) {
        super(ExpressionLanguage.<Object>createList(tableData, xColumnName, yColumnName, sizeColumnName, tooltipColumnName),
                new BubbleGraph2DFunction(functionOf(tableData),
                functionOf(xColumnName), functionOf(yColumnName), functionOf(sizeColumnName), functionOf(tooltipColumnName)),
                "Bubble Graph");
    }

    @Override
    public void update(BubbleGraph2DRendererUpdate update) {
        ((BubbleGraph2DFunction) getFunction()).getRendererUpdateQueue().writeValue(update);
    }

    @Override
    public BubbleGraph2DRendererUpdate newUpdate() {
        return new BubbleGraph2DRendererUpdate();
    }
}
