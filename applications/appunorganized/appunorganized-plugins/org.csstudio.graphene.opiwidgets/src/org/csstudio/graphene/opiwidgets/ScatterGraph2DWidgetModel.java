/**
 *
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.ScatterGraph2DWidget;
import org.csstudio.opibuilder.widgets.extra.AbstractSelectionWidgetModelDescription;



/**
 * @author shroffk
 *
 */
public class ScatterGraph2DWidgetModel extends AbstractPointDatasetGraph2DWidgetModel {

    public ScatterGraph2DWidgetModel() {
        super(AbstractSelectionWidgetModelDescription.newModelFrom(ScatterGraph2DWidget.class));
    }

    public final String ID = "org.csstudio.graphene.opiwidgets.ScatterGraph2D"; //$NON-NLS-1$

    @Override
    public String getTypeID() {
        return ID;
    }

}
