/**
 * 
 */
package org.csstudio.graphene;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.ConfigurableWidget;
import org.csstudio.ui.util.ConfigurableWidgetAdaptable;
import org.csstudio.ui.util.XAxisProcessVariable;
import org.csstudio.ui.util.XAxisProcessVariableAdaptable;
import org.csstudio.ui.util.YAxisProcessVariable;
import org.csstudio.ui.util.YAxisProcessVariableAdaptable;

/**
 * @author shroffk
 * 
 */
public class Scatter2DPlotSelection implements YAxisProcessVariableAdaptable,
	XAxisProcessVariableAdaptable, ConfigurableWidgetAdaptable {

    private final Collection<ProcessVariable> YPvs;
    private final Collection<ProcessVariable> XPvs;
    private final Scatter2DPlotWidget scatter2DPlotWidget;

    public Scatter2DPlotSelection(Collection<ProcessVariable> yPvs,
	    Collection<ProcessVariable> xPvs,
	    Scatter2DPlotWidget scatter2DPlotWidget) {
	super();
	YPvs = yPvs;
	XPvs = xPvs;
	this.scatter2DPlotWidget = scatter2DPlotWidget;
    }

    public Scatter2DPlotSelection(ProcessVariable yPv, ProcessVariable xPv,
	    Scatter2DPlotWidget scatter2DPlotWidget) {
	super();
	YPvs = new HashSet<ProcessVariable>(Arrays.asList(yPv));
	XPvs = new HashSet<ProcessVariable>(Arrays.asList(xPv));
	this.scatter2DPlotWidget = scatter2DPlotWidget;
    }

    @Override
    public YAxisProcessVariable getYAxisProcessVariables() {
	return new YAxisProcessVariable(YPvs);
    }

    @Override
    public XAxisProcessVariable getXAxisProcessVariables() {
	return new XAxisProcessVariable(XPvs);
    }

    @Override
    public Collection<ProcessVariable> toProcessVariables() {
	Collection<ProcessVariable> processVariables = new HashSet<ProcessVariable>();
	processVariables.addAll(YPvs);
	processVariables.addAll(XPvs);
	processVariables.remove(null);
	return processVariables;
    }

    @Override
    public ConfigurableWidget toConfigurableWidget() {
	return scatter2DPlotWidget;
    }

}
