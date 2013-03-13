/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager.graphene;

import org.epics.graphene.Range;

/**
 *
 * @author carcassi
 */
public class PlotDataRange {
    private final double startPlotRange;
    private final double endPlotRange;
    private final double startDataRange;
    private final double endDataRange;
    private final double startIntegratedDataRange;
    private final double endIntegratedDataRange;

    PlotDataRange(double startPlotRange, double endPlotRange, double startDataRange, double endDataRange, double startIntegratedDataRange, double endIntegratedDataRange) {
        this.startPlotRange = startPlotRange;
        this.endPlotRange = endPlotRange;
        this.startDataRange = startDataRange;
        this.endDataRange = endDataRange;
        this.startIntegratedDataRange = startIntegratedDataRange;
        this.endIntegratedDataRange = endIntegratedDataRange;
    }

    PlotDataRange(Range plotRange, Range dataRange, Range integratedRange) {
        this.startPlotRange = plotRange.getMinimum().doubleValue();
        this.endPlotRange = plotRange.getMaximum().doubleValue();
        this.startDataRange = dataRange.getMinimum().doubleValue();
        this.endDataRange = dataRange.getMaximum().doubleValue();
        this.startIntegratedDataRange = integratedRange.getMinimum().doubleValue();
        this.endIntegratedDataRange = integratedRange.getMaximum().doubleValue();
    }
    
    

    public double getEndDataRange() {
        return endDataRange;
    }

    public double getEndIntegratedDataRange() {
        return endIntegratedDataRange;
    }

    public double getEndPlotRange() {
        return endPlotRange;
    }

    public double getStartDataRange() {
        return startDataRange;
    }

    public double getStartIntegratedDataRange() {
        return startIntegratedDataRange;
    }

    public double getStartPlotRange() {
        return startPlotRange;
    }
    
    
}
