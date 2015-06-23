package org.csstudio.opibuilder.widgets.extra;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.utility.pvmanager.widgets.VImageWidget;
import org.eclipse.swt.widgets.Composite;

public class VImageDisplayFigure extends AbstractSelectionWidgetFigure<VImageWidget> {

    public VImageDisplayFigure(AbstractBaseEditPart editPart) {
        super(editPart);
    }

    @Override
    protected VImageWidget createSWTWidget(Composite parent, int style) {
        return new VImageWidget(parent);
    }

    public boolean isRunMode() {
        return runmode;
    }
}
