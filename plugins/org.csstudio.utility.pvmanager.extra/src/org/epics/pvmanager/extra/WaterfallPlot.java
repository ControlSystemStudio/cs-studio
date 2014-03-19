/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.extra;

import java.util.ArrayList;
import java.util.List;
import org.epics.pvmanager.ReadFunction;
import org.epics.vtype.VDouble;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.expression.DesiredRateExpressionImpl;
import org.epics.vtype.VDoubleArray;
import org.epics.vtype.VImage;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.pvmanager.expression.DesiredRateExpressionList;

/**
 * A waterfall plot.
 *
 * @author carcassi
 */
public class WaterfallPlot extends DesiredRateExpressionImpl<VImage> {

    WaterfallPlot(DesiredRateExpression<? extends List<? extends VNumberArray>> expression, String name) {
        super(expression, new WaterfallPlotFunction(new DoubleArrayTimeCacheFromVDoubleArray(expression.getFunction()), WaterfallPlotParameters.defaults().internalCopy()), name);
    }

    <T extends VNumber> WaterfallPlot(DesiredRateExpressionList<List<T>> expressions, String name) {
        super(expressions, new WaterfallPlotFunction(new DoubleArrayTimeCacheFromVDoubles(getFunctions(expressions)), WaterfallPlotParameters.defaults().internalCopy()), name);
    }
    
    private static <T extends VNumber> List<ReadFunction<List<T>>> getFunctions(DesiredRateExpressionList<List<T>> exp) {
        List<ReadFunction<List<T>>> functions = new ArrayList<ReadFunction<List<T>>>();
        for (DesiredRateExpression<List<T>> desiredRateExpression : exp.getDesiredRateExpressions()) {
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
