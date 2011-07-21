/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.extra;

import java.util.List;
import org.epics.pvmanager.DesiredRateExpression;
import org.epics.pvmanager.data.VDoubleArray;
import org.epics.pvmanager.data.VImage;

/**
 * A waterfall plot.
 *
 * @author carcassi
 */
public class WaterfallPlot extends DesiredRateExpression<VImage> {

    WaterfallPlot(DesiredRateExpression<List<VDoubleArray>> queue, String name) {
        super(queue, new WaterfallPlotFunction(queue.getFunction(), WaterfallPlotParameters.defaults().internalCopy()), name);
    }
    
    private volatile WaterfallPlotParameters parameters = WaterfallPlotParameters.defaults();

    WaterfallPlotFunction getPlotter() {
        return (WaterfallPlotFunction) getFunction();
    }
    
    /**
     * Changes parameters of the waterfall plot.
     * 
     * @param newParameters parameters to change
     * @return this
     */
    public WaterfallPlot with(WaterfallPlotParameters... newParameters) {
        parameters = new WaterfallPlotParameters(parameters, newParameters);
        WaterfallPlotParameters.InternalCopy copy = parameters.internalCopy();
        getPlotter().setParameters(copy);
        return this;
    }
    
    /**
     * Returns the full set of parameters currently being used.
     * 
     * @return the current parameters
     */
    public WaterfallPlotParameters getParameters() {
        return parameters;
    }
}
