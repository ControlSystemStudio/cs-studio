/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import java.util.Arrays;
import java.util.Collection;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.graphene.LineGraph2DWidget;
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
public class LineGraph2DWidgetEditpart extends AbstractWidgetEditPart implements
		YAxisProcessVariableAdaptable, XAxisProcessVariableAdaptable,
		ConfigurableWidgetAdaptable {

	@Override
	protected IFigure doCreateFigure() {
		LineGraph2DWidgetFigure figure = new LineGraph2DWidgetFigure(this);
		configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
		return figure;
	}

	private static void configure(LineGraph2DWidget widget,
			LineGraph2DWidgetModel model, boolean runMode) {
		if (runMode) {
			widget.setDataFormula(model.getDataFormula());
			widget.setXColumnFormula(model.getXColumnFormula());
			widget.setYColumnFormula(model.getYColumnFormula());
			widget.setTooltipColumnFormula(model.getTooltipFormula());
			widget.setShowAxis(model.getShowAxis());
			widget.setConfigurable(model.isConfigurable());
		}
	}

	@Override
	public LineGraph2DWidgetModel getWidgetModel() {
		LineGraph2DWidgetModel widgetModel = (LineGraph2DWidgetModel) super
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
						((AbstractSWTWidgetFigure<LineGraph2DWidget>) getFigure())
								.getSWTWidget(), getWidgetModel(),
						((LineGraph2DWidgetFigure) getFigure()).isRunMode());
				return false;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME, reconfigure);
		setPropertyChangeHandler(LineGraph2DWidgetModel.PROP_DATA_FORMULA, reconfigure);
		setPropertyChangeHandler(LineGraph2DWidgetModel.PROP_X_FORMULA, reconfigure);
		setPropertyChangeHandler(LineGraph2DWidgetModel.PROP_Y_FORMULA, reconfigure);
		setPropertyChangeHandler(LineGraph2DWidgetModel.PROP_TOOLTIP_FORMULA, reconfigure);
		setPropertyChangeHandler(LineGraph2DWidgetModel.CONFIGURABLE, reconfigure);
		setPropertyChangeHandler(LineGraph2DWidgetModel.PROP_SHOW_AXIS, reconfigure);
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
		if (((LineGraph2DWidgetFigure) getFigure()).getSelectionProvider() == null)
			return null;
		return Arrays.asList(AdapterUtil.convert(
				((LineGraph2DWidgetFigure) getFigure()).getSelectionProvider()
						.getSelection(), clazz));
	}

}
