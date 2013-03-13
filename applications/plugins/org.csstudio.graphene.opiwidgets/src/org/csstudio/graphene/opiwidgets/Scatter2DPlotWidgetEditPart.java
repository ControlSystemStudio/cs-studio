/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import java.util.Arrays;
import java.util.Collection;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.graphene.Scatter2DPlotWidget;
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
public class Scatter2DPlotWidgetEditPart extends AbstractWidgetEditPart
	implements ConfigurableWidgetAdaptable, XAxisProcessVariableAdaptable,
	YAxisProcessVariableAdaptable {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.csstudio.opibuilder.editparts.AbstractBaseEditPart#doCreateFigure()
     */
    @Override
    protected IFigure doCreateFigure() {
	Scatter2DPlotWidgetFigure figure = new Scatter2DPlotWidgetFigure(this);
	configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
	return figure;
    }

    private static void configure(Scatter2DPlotWidget widget,
	    Scatter2DPlotWidgetModel model, boolean runMode) {
	if (runMode) {
	    widget.setPvName(model.getProcessVariable().getName());
	    widget.setXPvName(model.getXPvName());
	    widget.setShowAxis(model.getShowAxis());
	    widget.setConfigurable(model.isConfigurable());
	}
    }

    @Override
    public Scatter2DPlotWidgetModel getWidgetModel() {
	Scatter2DPlotWidgetModel widgetModel = (Scatter2DPlotWidgetModel) super
		.getWidgetModel();
	return widgetModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.csstudio.opibuilder.editparts.AbstractBaseEditPart#
     * registerPropertyChangeHandlers()
     */
    @Override
    protected void registerPropertyChangeHandlers() {
	IWidgetPropertyChangeHandler reconfigure = new IWidgetPropertyChangeHandler() {
	    @SuppressWarnings("unchecked")
	    public boolean handleChange(final Object oldValue,
		    final Object newValue, final IFigure figure) {
		configure(
			((AbstractSWTWidgetFigure<Scatter2DPlotWidget>) getFigure())
				.getSWTWidget(), getWidgetModel(),
			((Scatter2DPlotWidgetFigure) getFigure()).isRunMode());
		return false;
	    }
	};
	setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME, reconfigure);
	setPropertyChangeHandler(Scatter2DPlotWidgetModel.PROP_XPVNAME,
		reconfigure);
	setPropertyChangeHandler(Scatter2DPlotWidgetModel.CONFIGURABLE,
		reconfigure);
	setPropertyChangeHandler(Scatter2DPlotWidgetModel.PROP_SHOW_AXIS,
		reconfigure);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.csstudio.ui.util.ProcessVariableAdaptable#toProcessVariables()
     */
    @Override
    public Collection<ProcessVariable> toProcessVariables() {
	return selectionToType(ProcessVariable.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.csstudio.ui.util.YAxisProcessVariableAdaptable#getYAxisProcessVariables
     * ()
     */
    @Override
    public YAxisProcessVariable getYAxisProcessVariables() {
	Collection<YAxisProcessVariable> adapted = selectionToType(YAxisProcessVariable.class);
	if (adapted != null && adapted.size() == 1) {
	    return adapted.iterator().next();
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.csstudio.ui.util.XAxisProcessVariableAdaptable#getXAxisProcessVariables
     * ()
     */
    @Override
    public XAxisProcessVariable getXAxisProcessVariables() {
	Collection<XAxisProcessVariable> adapted = selectionToType(XAxisProcessVariable.class);
	if (adapted != null && adapted.size() == 1) {
	    return adapted.iterator().next();
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.csstudio.ui.util.ConfigurableWidgetAdaptable#toConfigurableWidget()
     */
    @Override
    public ConfigurableWidget toConfigurableWidget() {
	Collection<ConfigurableWidget> adapted = selectionToType(ConfigurableWidget.class);
	if (adapted != null && adapted.size() == 1) {
	    return adapted.iterator().next();
	}
	return null;
    }

    private <T> Collection<T> selectionToType(Class<T> clazz) {
	if (((Scatter2DPlotWidgetFigure) getFigure()).getSelectionProvider() == null)
	    return null;
	return Arrays.asList(AdapterUtil.convert(
		((Scatter2DPlotWidgetFigure) getFigure())
			.getSelectionProvider().getSelection(), clazz));
    }

}
