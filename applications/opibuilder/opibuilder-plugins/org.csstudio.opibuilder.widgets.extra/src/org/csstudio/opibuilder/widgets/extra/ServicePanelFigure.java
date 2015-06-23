package org.csstudio.opibuilder.widgets.extra;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.csstudio.utility.pvmanager.widgets.ServicePanel;
import org.eclipse.swt.widgets.Composite;

public class ServicePanelFigure extends AbstractSWTWidgetFigure<ServicePanel> {

    public ServicePanelFigure(AbstractBaseEditPart editPart) {
    super(editPart);
    }

    @Override
    protected ServicePanel createSWTWidget(Composite parent, int style) {
        return new ServicePanel(parent);
    }

    public boolean isRunMode() {
        return runmode;
    }
}
