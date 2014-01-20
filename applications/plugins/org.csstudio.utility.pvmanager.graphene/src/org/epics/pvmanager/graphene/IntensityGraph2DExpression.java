/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.graphene;

import org.epics.graphene.AreaGraph2DRendererUpdate;
import org.epics.graphene.Histogram1DUpdate;
import org.epics.graphene.IntensityGraph2DRendererUpdate;
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
public class IntensityGraph2DExpression extends DesiredRateExpressionImpl<Graph2DResult> implements Graph2DExpression<IntensityGraph2DRendererUpdate> {

    IntensityGraph2DExpression(DesiredRateExpression<?> arrayData) {
        super(ExpressionLanguage.<Object>createList(arrayData),
                new IntensityGraph2DFunction(functionOf(arrayData)),
                "Histogram Graph");
    }
    
    @Override
    public void update(IntensityGraph2DRendererUpdate update) {
        ((IntensityGraph2DFunction) getFunction()).getUpdateQueue().writeValue(update);
    }

    @Override
    public IntensityGraph2DRendererUpdate newUpdate() {
        return new IntensityGraph2DRendererUpdate();
    }
}
