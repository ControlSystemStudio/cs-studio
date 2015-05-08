/**
 *
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.IntensityGraph2DWidget;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.eclipse.swt.widgets.Composite;

/**
 * @author shroffk
 *
 */
public class IntensityGraph2DWidgetFigure extends AbstractPointDatasetGraph2DWidgetFigure<IntensityGraph2DWidget> {

    public IntensityGraph2DWidgetFigure(AbstractBaseEditPart editpart) {
        super(editpart);
    }

    @Override
    protected IntensityGraph2DWidget createSWTWidget(Composite parent, int style) {
        return new IntensityGraph2DWidget(parent, style);
    }

}
