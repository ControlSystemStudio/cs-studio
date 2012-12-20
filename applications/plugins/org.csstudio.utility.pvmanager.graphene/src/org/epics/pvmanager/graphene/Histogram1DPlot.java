/*
 * Copyright 2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.graphene;

import org.epics.graphene.Histogram1DRendererUpdate;
import org.epics.graphene.Histogram1DUpdate;
import org.epics.pvmanager.Function;
import org.epics.pvmanager.data.VImage;
import org.epics.pvmanager.expression.DesiredRateExpressionImpl;
import org.epics.pvmanager.expression.DesiredRateExpressionList;

/**
 *
 * @author carcassi
 */
public class Histogram1DPlot extends DesiredRateExpressionImpl<VImage> {

    public Histogram1DPlot(DesiredRateExpressionList<?> childExpressions, Histogram1DFunction function, String defaultName) {
        super(childExpressions, function, defaultName);
    }
    
    public void update(Histogram1DUpdate update) {
        ((Histogram1DFunction) getFunction()).update(update);
    }
    
    public void update(Histogram1DRendererUpdate update) {
        ((Histogram1DFunction) getFunction()).update(update);
    }
}
