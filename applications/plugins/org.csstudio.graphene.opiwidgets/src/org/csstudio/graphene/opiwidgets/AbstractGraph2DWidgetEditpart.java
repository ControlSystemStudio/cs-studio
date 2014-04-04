/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.AbstractGraph2DWidget;
import org.csstudio.graphene.AbstractPointDatasetGraph2DWidget;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.extra.AbstractSelectionWidgetEditpart;
import org.eclipse.draw2d.IFigure;

/**
 * 
 * 
 * @author shroffk
 * 
 */
public abstract class AbstractGraph2DWidgetEditpart<F extends AbstractPointDatasetGraph2DWidgetFigure<? extends AbstractGraph2DWidget<?, ?>>,
M extends AbstractPointDatasetGraph2DWidgetModel> extends AbstractSelectionWidgetEditpart<F, M> {

	private IWidgetPropertyChangeHandler reconfigureWidgetPropertyChangeHandler = new IWidgetPropertyChangeHandler() {
		public boolean handleChange(final Object oldValue,
				final Object newValue, final IFigure figure) {
			configure(getFigure(), getWidgetModel());
			return false;
		}
	};
	
	/**
	 * Returns an IWidgetPropertyChangeHandler that calls the configure function;
	 * 
	 * @return the property change handler
	 */
	protected IWidgetPropertyChangeHandler getReconfigureWidgetPropertyChangeHandler() {
		return reconfigureWidgetPropertyChangeHandler;
	}

	protected void configure(F figure, M model) {
		AbstractGraph2DWidget<?, ?> widget = figure.getSWTWidget();
		if (figure.isRunMode()) {
			widget.setDataFormula(model.getDataFormula());
			widget.setConfigurable(model.isConfigurable());
		} else {
			widget.setConfigurable(false);
		}
		widget.setResizableAxis(model.isResizableAxis());
	}
	
	@Override
	protected void registerPropertyChangeHandlers() {
		setPropertyChangeHandler(AbstractPointDatasetGraph2DWidgetModel.PROP_DATA_FORMULA, getReconfigureWidgetPropertyChangeHandler());
		setPropertyChangeHandler(AbstractPointDatasetGraph2DWidgetModel.CONFIGURABLE, getReconfigureWidgetPropertyChangeHandler());
		setPropertyChangeHandler(AbstractPointDatasetGraph2DWidgetModel.PROP_RESIZABLE_AXIS, getReconfigureWidgetPropertyChangeHandler());
	}
	
}
