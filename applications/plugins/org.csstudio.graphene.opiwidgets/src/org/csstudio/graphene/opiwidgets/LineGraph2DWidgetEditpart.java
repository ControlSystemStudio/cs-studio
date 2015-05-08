/**
 *
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.LineGraph2DWidget;
import org.eclipse.draw2d.IFigure;
import static org.csstudio.graphene.opiwidgets.ModelPropertyConstants.*;

/**
 * @author shroffk
 *
 */
public class LineGraph2DWidgetEditpart extends AbstractPointDatasetGraph2DWidgetEditpart<LineGraph2DWidgetFigure, LineGraph2DWidgetModel> {

    @Override
    protected IFigure doCreateFigure() {
        LineGraph2DWidgetFigure figure = new LineGraph2DWidgetFigure(this);
        configure(figure, getWidgetModel());
        return figure;
    }

    @Override
    protected void configure(LineGraph2DWidgetFigure figure, LineGraph2DWidgetModel model) {
        super.configure(figure, model);
        LineGraph2DWidget widget = figure.getSWTWidget();
        if (figure.isRunMode()) {
            widget.setHighlightSelectionValue(model.isHighlightSelectionValue());
            widget.setSelectionValuePv(model.getSelectionValuePv());
        }
    }

    @Override
    protected void registerPropertyChangeHandlers() {
        super.registerPropertyChangeHandlers();
        setPropertyChangeHandler(PROP_HIGHLIGHT_SELECTION_VALUE, getReconfigureWidgetPropertyChangeHandler());
        setPropertyChangeHandler(PROP_SELECTION_VALUE_PV, getReconfigureWidgetPropertyChangeHandler());
    }

}
