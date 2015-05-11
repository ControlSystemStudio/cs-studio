/**
 *
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.BubbleGraph2DWidget;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.eclipse.swt.widgets.Composite;

/**
 * @author shroffk
 *
 */
public class BubbleGraph2DWidgetFigure extends AbstractPointDatasetGraph2DWidgetFigure<BubbleGraph2DWidget> {

    public BubbleGraph2DWidgetFigure(AbstractBaseEditPart editpart) {
        super(editpart);
    }

    @Override
    protected BubbleGraph2DWidget createSWTWidget(Composite parent, int style) {
        return new BubbleGraph2DWidget(parent, style);
    }

}
