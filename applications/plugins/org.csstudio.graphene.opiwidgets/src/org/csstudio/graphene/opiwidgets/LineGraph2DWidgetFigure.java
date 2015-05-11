/**
 *
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.LineGraph2DWidget;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.eclipse.swt.widgets.Composite;

/**
 * @author shroffk
 *
 */
public class LineGraph2DWidgetFigure extends
        AbstractPointDatasetGraph2DWidgetFigure<LineGraph2DWidget> {

    public LineGraph2DWidgetFigure(AbstractBaseEditPart editpart) {
        super(editpart);
    }

    @Override
    protected LineGraph2DWidget createSWTWidget(Composite parent, int style) {
        return new LineGraph2DWidget(parent, style);
    }

}
