/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import java.util.Arrays;
import java.util.Collection;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.graphene.ScatterGraph2DWidget;
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
public class ScatterGraph2DWidgetEditPart extends AbstractWidgetEditPart
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
	ScatterGraph2DWidgetFigure figure = new ScatterGraph2DWidgetFigure(this);
	configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
	return figure;
    }

    private static void configure(ScatterGraph2DWidget widget,
	    ScatterGraph2DWidgetModel model, boolean runMode) {
	if (runMode) {
	    widget.setPvName(model.getProcessVariable().getName());
	    widget.setXPvName(model.getXPvName());
	    widget.setShowAxis(model.getShowAxis());
	    widget.setConfigurable(model.isConfigurable());
	}
    }

    @Override
    public ScatterGraph2DWidgetModel getWidgetModel() {
	ScatterGraph2DWidgetModel widgetModel = (ScatterGraph2DWidgetModel) super
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
			((AbstractSWTWidgetFigure<ScatterGraph2DWidget>) getFigure())
				.getSWTWidget(), getWidgetModel(),
			((ScatterGraph2DWidgetFigure) getFigure()).isRunMode());
		return false;
	    }
	};
	setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME, reconfigure);
	setPropertyChangeHandler(ScatterGraph2DWidgetModel.PROP_XPVNAME,
		reconfigure);
	setPropertyChangeHandler(ScatterGraph2DWidgetModel.CONFIGURABLE,
		reconfigure);
	setPropertyChangeHandler(ScatterGraph2DWidgetModel.PROP_SHOW_AXIS,
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
	if (((ScatterGraph2DWidgetFigure) getFigure()).getSelectionProvider() == null)
	    return null;
	return Arrays.asList(AdapterUtil.convert(
		((ScatterGraph2DWidgetFigure) getFigure())
			.getSelectionProvider().getSelection(), clazz));
    }

}
