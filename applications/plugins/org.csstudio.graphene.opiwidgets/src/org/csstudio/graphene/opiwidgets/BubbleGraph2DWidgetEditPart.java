/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import java.util.Arrays;
import java.util.Collection;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.graphene.BubbleGraph2DWidget;
import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
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
public class BubbleGraph2DWidgetEditPart extends AbstractWidgetEditPart
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
		BubbleGraph2DWidgetFigure figure = new BubbleGraph2DWidgetFigure(this);
		configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
		return figure;
	}

	private static void configure(BubbleGraph2DWidget widget,
			BubbleGraph2DWidgetModel model, boolean runMode) {
		if (runMode) {
			widget.setDataFormula(model.getDataFormula());
			widget.setXColumnFormula(model.getXColumnFormula());
			widget.setYColumnFormula(model.getYColumnFormula());
			widget.setTooltipColumnFormula(model.getTooltipFormula());
			widget.setShowAxis(model.getShowAxis());
			//widget.setConfigurable(model.isConfigurable());
		}
	}

	@Override
	public BubbleGraph2DWidgetModel getWidgetModel() {
		BubbleGraph2DWidgetModel widgetModel = (BubbleGraph2DWidgetModel) super
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
						((AbstractSWTWidgetFigure<BubbleGraph2DWidget>) getFigure())
								.getSWTWidget(), getWidgetModel(),
						((BubbleGraph2DWidgetFigure) getFigure()).isRunMode());
				return false;
			}
		};
		setPropertyChangeHandler(ScatterGraph2DWidgetModel.PROP_DATA_FORMULA, reconfigure);
		setPropertyChangeHandler(ScatterGraph2DWidgetModel.PROP_X_FORMULA, reconfigure);
		setPropertyChangeHandler(ScatterGraph2DWidgetModel.PROP_Y_FORMULA, reconfigure);
		setPropertyChangeHandler(ScatterGraph2DWidgetModel.PROP_TOOLTIP_FORMULA, reconfigure);
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
