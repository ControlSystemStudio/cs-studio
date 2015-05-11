/**
 *
 */
package org.csstudio.graphene.opiwidgets;

import static org.csstudio.graphene.opiwidgets.ModelPropertyConstants.*;

import org.csstudio.graphene.AbstractGraph2DWidget;
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
M extends AbstractGraph2DWidgetModel> extends AbstractSelectionWidgetEditpart<F, M> {

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
        widget.setXAxisRange(model.getXAxisRange());
        widget.setYAxisRange(model.getYAxisRange());
    }

    @Override
    protected void registerPropertyChangeHandlers() {
        setPropertyChangeHandler(PROP_DATA_FORMULA, getReconfigureWidgetPropertyChangeHandler());
        setPropertyChangeHandler(AbstractPointDatasetGraph2DWidgetModel.CONFIGURABLE, getReconfigureWidgetPropertyChangeHandler());
        setPropertyChangeHandler(PROP_RESIZABLE_AXIS, getReconfigureWidgetPropertyChangeHandler());
        setPropertyChangeHandler(PROP_X_AXIS_RANGE, getReconfigureWidgetPropertyChangeHandler());
        setPropertyChangeHandler(PROP_Y_AXIS_RANGE, getReconfigureWidgetPropertyChangeHandler());
    }

}
