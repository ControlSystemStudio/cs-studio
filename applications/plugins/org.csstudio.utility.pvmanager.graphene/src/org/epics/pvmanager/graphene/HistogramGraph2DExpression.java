/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.graphene;

import org.epics.graphene.AreaGraph2DRendererUpdate;
import org.epics.graphene.Histogram1DUpdate;
import org.epics.pvmanager.ReadFunction;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.vtype.VImage;
import org.epics.pvmanager.expression.DesiredRateExpressionImpl;
import org.epics.pvmanager.expression.DesiredRateExpressionList;
import static org.epics.pvmanager.graphene.ExpressionLanguage.functionOf;

/**
 *
 * @author carcassi
 */
public class HistogramGraph2DExpression extends DesiredRateExpressionImpl<Graph2DResult> implements Graph2DExpression<AreaGraph2DRendererUpdate> {

    HistogramGraph2DExpression(DesiredRateExpression<?> arrayData) {
        super(ExpressionLanguage.<Object>createList(arrayData),
                new HistogramGraph2DFunction(functionOf(arrayData)),
                "Histogram Graph");
    }
    
    @Override
    public void update(AreaGraph2DRendererUpdate update) {
        ((HistogramGraph2DFunction) getFunction()).getUpdateQueue().writeValue(update);
    }

    @Override
    public AreaGraph2DRendererUpdate newUpdate() {
        return new AreaGraph2DRendererUpdate();
    }
}
