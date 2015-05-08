/**
 *
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.ScatterGraph2DWidget;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.eclipse.swt.widgets.Composite;

/**
 * @author shroffk
 *
 */
public class ScatterGraph2DWidgetFigure extends AbstractPointDatasetGraph2DWidgetFigure<ScatterGraph2DWidget> {

    public ScatterGraph2DWidgetFigure(AbstractBaseEditPart editpart) {
        super(editpart);
    }

    @Override
    protected ScatterGraph2DWidget createSWTWidget(Composite parent, int style) {
        return new ScatterGraph2DWidget(parent, style);
    }

}
