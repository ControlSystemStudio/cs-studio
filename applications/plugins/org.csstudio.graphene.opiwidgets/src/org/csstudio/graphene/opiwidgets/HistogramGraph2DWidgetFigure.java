/**
 *
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.HistogramGraph2DWidget;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.eclipse.swt.widgets.Composite;

/**
 * @author shroffk
 *
 */
public class HistogramGraph2DWidgetFigure extends AbstractPointDatasetGraph2DWidgetFigure<HistogramGraph2DWidget> {

    public HistogramGraph2DWidgetFigure(AbstractBaseEditPart editpart) {
        super(editpart);
    }

    @Override
    protected HistogramGraph2DWidget createSWTWidget(Composite parent, int style) {
        return new HistogramGraph2DWidget(parent, style);
    }

}
