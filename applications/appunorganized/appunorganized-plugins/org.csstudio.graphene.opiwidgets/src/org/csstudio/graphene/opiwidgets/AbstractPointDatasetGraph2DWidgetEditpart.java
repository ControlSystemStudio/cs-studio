/**
 *
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.AbstractPointDatasetGraph2DWidget;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.extra.AbstractSelectionWidgetEditpart;
import org.eclipse.draw2d.IFigure;
import static org.csstudio.graphene.opiwidgets.ModelPropertyConstants.*;

/**
 *
 *
 * @author shroffk
 *
 */
public abstract class AbstractPointDatasetGraph2DWidgetEditpart<F extends AbstractPointDatasetGraph2DWidgetFigure<? extends AbstractPointDatasetGraph2DWidget<?, ?>>,
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
        AbstractPointDatasetGraph2DWidget<?, ?> widget = figure.getSWTWidget();
        if (figure.isRunMode()) {
            widget.setDataFormula(model.getDataFormula());
            widget.setXColumnFormula(model.getXColumnFormula());
            widget.setYColumnFormula(model.getYColumnFormula());
            widget.setConfigurable(model.isConfigurable());
        } else {
            widget.setConfigurable(false);
        }
        widget.setResizableAxis(model.isResizableAxis());
        widget.setXAxisRange(model.getXAxisRange());
        widget.setYAxisRange(model.getYAxisRange());
    }

    @Override
    protected void registerPropertyChangeHandlers() {
        setPropertyChangeHandler(PROP_DATA_FORMULA, getReconfigureWidgetPropertyChangeHandler());
        setPropertyChangeHandler(PROP_X_FORMULA, getReconfigureWidgetPropertyChangeHandler());
        setPropertyChangeHandler(PROP_Y_FORMULA, getReconfigureWidgetPropertyChangeHandler());
        setPropertyChangeHandler(AbstractPointDatasetGraph2DWidgetModel.CONFIGURABLE, getReconfigureWidgetPropertyChangeHandler());
        setPropertyChangeHandler(PROP_RESIZABLE_AXIS, getReconfigureWidgetPropertyChangeHandler());
        setPropertyChangeHandler(PROP_X_AXIS_RANGE, getReconfigureWidgetPropertyChangeHandler());
        setPropertyChangeHandler(PROP_Y_AXIS_RANGE, getReconfigureWidgetPropertyChangeHandler());
    }

}
