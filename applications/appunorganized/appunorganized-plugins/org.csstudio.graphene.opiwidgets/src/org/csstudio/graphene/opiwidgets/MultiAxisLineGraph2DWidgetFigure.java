/**
 *
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.MultiAxisLineGraph2DWidget;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.eclipse.swt.widgets.Composite;

/**
 * @author shroffk
 *
 */
public class MultiAxisLineGraph2DWidgetFigure extends
        AbstractPointDatasetGraph2DWidgetFigure<MultiAxisLineGraph2DWidget> {

    public MultiAxisLineGraph2DWidgetFigure(AbstractBaseEditPart editpart) {
        super(editpart);
    }

    @Override
    protected MultiAxisLineGraph2DWidget createSWTWidget(Composite parent, int style) {
        return new MultiAxisLineGraph2DWidget(parent, style);
    }

}
