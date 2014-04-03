/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.graphene;

import org.epics.graphene.LineGraph2DRendererUpdate;
import org.epics.graphene.MultilineGraph2DRendererUpdate;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.expression.DesiredRateExpressionImpl;
import org.epics.pvmanager.expression.DesiredRateExpressionList;
import static org.epics.pvmanager.graphene.ExpressionLanguage.functionOf;

/**
 *
 * @author carcassi
 */
public class MultilineGraph2DExpression extends DesiredRateExpressionImpl<Graph2DResult> implements Graph2DExpression<MultilineGraph2DRendererUpdate> {

    MultilineGraph2DExpression(DesiredRateExpression<?> tableData,
	    DesiredRateExpression<?> xColumnName,
	    DesiredRateExpression<?> yColumnName) {
        super(ExpressionLanguage.<Object>createList(tableData, xColumnName, yColumnName),
                new MultilineGraph2DFunction(functionOf(tableData),
                functionOf(xColumnName), functionOf(yColumnName)),
                "Multiline Graph");
    }
    
    @Override
    public void update(MultilineGraph2DRendererUpdate update) {
        ((MultilineGraph2DFunction) getFunction()).getRendererUpdateQueue().writeValue(update);
    }

    @Override
    public MultilineGraph2DRendererUpdate newUpdate() {
        return new MultilineGraph2DRendererUpdate();
    }
}
