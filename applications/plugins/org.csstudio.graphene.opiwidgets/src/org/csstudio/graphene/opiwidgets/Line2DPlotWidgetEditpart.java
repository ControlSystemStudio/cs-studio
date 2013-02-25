/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import java.util.Arrays;
import java.util.Collection;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.graphene.Line2DPlotWidget;
import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.csstudio.ui.util.AdapterUtil;
import org.csstudio.ui.util.ConfigurableWidget;
import org.csstudio.ui.util.ConfigurableWidgetAdaptable;
import org.csstudio.ui.util.XAxisProcessVariable;
import org.csstudio.ui.util.XAxisProcessVariableAdaptable;
import org.csstudio.ui.util.YAxisProcessVariable;
import org.csstudio.ui.util.YAxisProcessVariableAdaptable;
import org.eclipse.draw2d.IFigure;

/**
 * @author shroffk
 * 
 */
public class Line2DPlotWidgetEditpart extends AbstractWidgetEditPart implements
	YAxisProcessVariableAdaptable, XAxisProcessVariableAdaptable,
	ConfigurableWidgetAdaptable {

    @Override
    protected IFigure doCreateFigure() {
	Line2DPlotWidgetFigure figure = new Line2DPlotWidgetFigure(this);
	configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
	return figure;
    }

    private static void configure(Line2DPlotWidget widget,
	    Line2DPlotWidgetModel model, boolean runMode) {
	if (runMode) {
	    widget.setPvName(model.getProcessVariable().getName());
	    widget.setXPvName(model.getXPvName());
	    widget.setShowAxis(model.getShowAxis());
	    widget.setConfigurable(model.isConfigurable());
	}
    }

    @Override
    public Line2DPlotWidgetModel getWidgetModel() {
	Line2DPlotWidgetModel widgetModel = (Line2DPlotWidgetModel) super
		.getWidgetModel();
	return widgetModel;
    }

    @Override
    protected void registerPropertyChangeHandlers() {
	// The handler when PV value changed.
	IWidgetPropertyChangeHandler reconfigure = new IWidgetPropertyChangeHandler() {
	    @SuppressWarnings("unchecked")
	    public boolean handleChange(final Object oldValue,
		    final Object newValue, final IFigure figure) {
		configure(
			((AbstractSWTWidgetFigure<Line2DPlotWidget>) getFigure())
				.getSWTWidget(), getWidgetModel(),
			((Line2DPlotWidgetFigure) getFigure()).isRunMode());
		return false;
	    }
	};
	setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME, reconfigure);
	setPropertyChangeHandler(Line2DPlotWidgetModel.PROP_XPVNAME,
		reconfigure);
	setPropertyChangeHandler(Line2DPlotWidgetModel.CONFIGURABLE,
		reconfigure);
	setPropertyChangeHandler(Line2DPlotWidgetModel.PROP_SHOW_AXIS,
		reconfigure);
    }

    @Override
    public Collection<ProcessVariable> toProcessVariables() {
	return selectionToType(ProcessVariable.class);
    }

    @Override
    public ConfigurableWidget toConfigurableWidget() {
	Collection<ConfigurableWidget> adapted = selectionToType(ConfigurableWidget.class);
	if (adapted != null && adapted.size() == 1) {
	    return adapted.iterator().next();
	}
	return null;
    }

    @Override
    public XAxisProcessVariable getXAxisProcessVariables() {
	Collection<XAxisProcessVariable> adapted = selectionToType(XAxisProcessVariable.class);
	if (adapted != null && adapted.size() == 1) {
	    return adapted.iterator().next();
	}
	return null;
    }

    @Override
    public YAxisProcessVariable getYAxisProcessVariables() {
	Collection<YAxisProcessVariable> adapted = selectionToType(YAxisProcessVariable.class);
	if (adapted != null && adapted.size() == 1) {
	    return adapted.iterator().next();
	}
	return null;
    }

    private <T> Collection<T> selectionToType(Class<T> clazz) {
	if (((Line2DPlotWidgetFigure) getFigure()).getSelectionProvider() == null)
	    return null;
	return Arrays.asList(AdapterUtil.convert(
		((Line2DPlotWidgetFigure) getFigure()).getSelectionProvider()
			.getSelection(), clazz));
    }

}
