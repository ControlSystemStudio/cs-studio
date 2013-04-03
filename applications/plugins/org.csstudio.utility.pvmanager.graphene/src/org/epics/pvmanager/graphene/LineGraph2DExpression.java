/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.graphene;

import org.epics.graphene.LineGraph2DRendererUpdate;
import org.epics.pvmanager.expression.DesiredRateExpressionImpl;
import org.epics.pvmanager.expression.DesiredRateExpressionList;

/**
 *
 * @author carcassi
 */
public class LineGraph2DExpression extends DesiredRateExpressionImpl<Graph2DResult> implements Graph2DExpression<LineGraph2DRendererUpdate> {

    LineGraph2DExpression(DesiredRateExpressionList<?> childExpressions, LineGraph2DFunction function, String defaultName) {
        super(childExpressions, function, defaultName);
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
