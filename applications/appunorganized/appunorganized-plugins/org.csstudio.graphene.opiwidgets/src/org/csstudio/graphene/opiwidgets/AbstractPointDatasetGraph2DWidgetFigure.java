package org.csstudio.graphene.opiwidgets;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.widgets.extra.AbstractSelectionWidgetFigure;
import org.eclipse.swt.widgets.Composite;

/**
 *
 * TODO: this may actually be superflous at this point.
 *
 * @author carcassi
 *
 * @param <T> the widget type
 */
public abstract class AbstractPointDatasetGraph2DWidgetFigure<T extends Composite> extends AbstractSelectionWidgetFigure<T> {

    public AbstractPointDatasetGraph2DWidgetFigure(AbstractBaseEditPart editPart) {
        super(editPart);
    }

}
