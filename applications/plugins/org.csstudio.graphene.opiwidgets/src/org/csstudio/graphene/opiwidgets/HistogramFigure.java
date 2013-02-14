package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.HistogramWidget;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class HistogramFigure extends AbstractSWTWidgetFigure<HistogramWidget> {

    public HistogramFigure(AbstractBaseEditPart editPart) {
	super(editPart);
    }

    @Override
    protected HistogramWidget createSWTWidget(Composite parent, int style) {
	return new HistogramWidget(parent, SWT.NONE);
    }

    public boolean isRunMode() {
	return runmode;
    }
}
