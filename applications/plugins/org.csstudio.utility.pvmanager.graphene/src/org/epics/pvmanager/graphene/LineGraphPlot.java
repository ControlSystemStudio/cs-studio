/*
 * Copyright 2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.graphene;

import org.epics.graphene.LineGraphRendererUpdate;
import org.epics.pvmanager.data.VImage;
import org.epics.pvmanager.expression.DesiredRateExpressionImpl;
import org.epics.pvmanager.expression.DesiredRateExpressionList;

/**
 *
 * @author carcassi
 */
public class LineGraphPlot extends DesiredRateExpressionImpl<Plot2DResult> {

    public LineGraphPlot(DesiredRateExpressionList<?> childExpressions, LineGraphFunction function, String defaultName) {
        super(childExpressions, function, defaultName);
    }
    
    public void update(LineGraphRendererUpdate update) {
        ((LineGraphFunction) getFunction()).update(update);
    }
}
