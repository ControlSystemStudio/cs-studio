/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.graphene;

import org.epics.graphene.AreaGraph2DRendererUpdate;
import org.epics.graphene.Histogram1DUpdate;
import org.epics.pvmanager.ReadFunction;
import org.epics.vtype.VImage;
import org.epics.pvmanager.expression.DesiredRateExpressionImpl;
import org.epics.pvmanager.expression.DesiredRateExpressionList;

/**
 *
 * @author carcassi
 */
public class AreaGraph2DExpression extends DesiredRateExpressionImpl<Graph2DResult> implements Graph2DExpression<AreaGraph2DRendererUpdate> {

    AreaGraph2DExpression(DesiredRateExpressionList<?> childExpressions, AreaGraph2DFunction function, String defaultName) {
        super(childExpressions, function, defaultName);
    }
    
    public void update(Histogram1DUpdate update) {
        ((AreaGraph2DFunction) getFunction()).update(update);
    }
    
    @Override
    public void update(AreaGraph2DRendererUpdate update) {
        ((AreaGraph2DFunction) getFunction()).getUpdateQueue().writeValue(update);
    }

    @Override
    public AreaGraph2DRendererUpdate newUpdate() {
        return new AreaGraph2DRendererUpdate();
    }
}
