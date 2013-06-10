/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.AbstractPointDatasetGraph2DWidget;
import org.csstudio.opibuilder.widgets.extra.AbstractSelectionWidgetEditpart;

/**
 * 
 * TODO: this may actually be superflous at this point.
 * 
 * @author shroffk
 * 
 */
public abstract class AbstractPointDatasetGraph2DWidgetEditpart<F extends AbstractPointDatasetGraph2DWidgetFigure<? extends AbstractPointDatasetGraph2DWidget<?, ?>>,
M extends AbstractPointDatasetGraph2DWidgetModel> extends AbstractSelectionWidgetEditpart<F, M> {

	protected void configure(F figure,
			M model, boolean runMode) {
		AbstractPointDatasetGraph2DWidget<?, ?> widget = figure.getSWTWidget();
		if (runMode) {
			widget.setDataFormula(model.getDataFormula());
			widget.setXColumnFormula(model.getXColumnFormula());
			widget.setYColumnFormula(model.getYColumnFormula());
			widget.setConfigurable(model.isConfigurable());
		} else {
			widget.setConfigurable(false);
		}
		widget.setResizableAxis(model.isResizableAxis());
	}
}
