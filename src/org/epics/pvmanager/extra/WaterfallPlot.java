/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.extra;

import java.util.ArrayList;
import java.util.List;
import org.epics.pvmanager.Function;
import org.epics.pvmanager.data.VDouble;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.expression.DesiredRateExpressionImpl;
import org.epics.pvmanager.data.VDoubleArray;
import org.epics.pvmanager.data.VImage;
import org.epics.pvmanager.expression.DesiredRateExpressionList;

/**
 * A waterfall plot.
 *
 * @author carcassi
 */
public class WaterfallPlot extends DesiredRateExpressionImpl<VImage> {

    WaterfallPlot(DesiredRateExpression<List<VDoubleArray>> expression, String name) {
        super(expression, new WaterfallPlotFunction(new DoubleArrayTimeCacheFromVDoubleArray(expression.getFunction()), WaterfallPlotParameters.defaults().internalCopy()), name);
    }

    WaterfallPlot(DesiredRateExpressionList<List<VDouble>> expressions, String name) {
        super(expressions, new WaterfallPlotFunction(new DoubleArrayTimeCacheFromVDoubles(getFunctions(expressions)), WaterfallPlotParameters.defaults().internalCopy()), name);
    }
    
    private static List<Function<List<VDouble>>> getFunctions(DesiredRateExpressionList<List<VDouble>> exp) {
        List<Function<List<VDouble>>> functions = new ArrayList<Function<List<VDouble>>>();
        for (DesiredRateExpression<List<VDouble>> desiredRateExpression : exp.getDesiredRateExpressions()) {
            functions.add(desiredRateExpression.getFunction());
        }
        return functions;
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
