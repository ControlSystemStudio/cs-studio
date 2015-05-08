/**
 *
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.BubbleGraph2DWidget;
import org.eclipse.draw2d.IFigure;
import static org.csstudio.graphene.opiwidgets.ModelPropertyConstants.*;

/**
 * @author shroffk
 *
 */
public class BubbleGraph2DWidgetEditPart extends AbstractPointDatasetGraph2DWidgetEditpart<BubbleGraph2DWidgetFigure, BubbleGraph2DWidgetModel> {

    @Override
    protected IFigure doCreateFigure() {
        BubbleGraph2DWidgetFigure figure = new BubbleGraph2DWidgetFigure(this);
        configure(figure, getWidgetModel());
        return figure;
    }

    @Override
    protected void configure(BubbleGraph2DWidgetFigure figure, BubbleGraph2DWidgetModel model) {
        super.configure(figure, model);
        BubbleGraph2DWidget widget = figure.getSWTWidget();
        if (figure.isRunMode()) {
            widget.setHighlightSelectionValue(model.isHighlightSelectionValue());
            widget.setSelectionValuePv(model.getSelectionValuePv());
            widget.setSizeColumnFormula(model.getSizeColumnFormula());
            widget.setColorColumnFormula(model.getColorColumnFormula());
        }
    }

    @Override
    protected void registerPropertyChangeHandlers() {
        super.registerPropertyChangeHandlers();
        setPropertyChangeHandler(PROP_HIGHLIGHT_SELECTION_VALUE, getReconfigureWidgetPropertyChangeHandler());
        setPropertyChangeHandler(PROP_SELECTION_VALUE_PV, getReconfigureWidgetPropertyChangeHandler());
        setPropertyChangeHandler(BubbleGraph2DWidgetModel.PROP_SIZE_FORMULA, getReconfigureWidgetPropertyChangeHandler());
        setPropertyChangeHandler(BubbleGraph2DWidgetModel.PROP_COLOR_FORMULA, getReconfigureWidgetPropertyChangeHandler());
    }

}
