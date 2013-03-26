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
 * TODO better ways to handle null parts to the selection
 * @author shroffk
 * 
 */
public class LineGraph2DSelection implements YAxisProcessVariableAdaptable,
	XAxisProcessVariableAdaptable, ConfigurableWidgetAdaptable {

    private final Collection<ProcessVariable> YPvs;
    private final Collection<ProcessVariable> XPvs;
    private final LineGraph2DWidget line2dPlotWidget;

    public LineGraph2DSelection(Collection<ProcessVariable> yPvs,
	    Collection<ProcessVariable> xPvs, LineGraph2DWidget line2dPlotWidget) {
	super();
	YPvs = yPvs;
	XPvs = xPvs;
	this.line2dPlotWidget = line2dPlotWidget;
    }

    public LineGraph2DSelection(ProcessVariable yPv, ProcessVariable xPv,
	    LineGraph2DWidget line2dPlotWidget) {
	super();
	YPvs = new HashSet<ProcessVariable>(Arrays.asList(yPv));
	XPvs = new HashSet<ProcessVariable>(Arrays.asList(xPv));
	this.line2dPlotWidget = line2dPlotWidget;
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
	return line2dPlotWidget;
    }

}
