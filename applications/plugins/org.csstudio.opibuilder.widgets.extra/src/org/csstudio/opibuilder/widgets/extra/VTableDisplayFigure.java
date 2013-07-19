package org.csstudio.opibuilder.widgets.extra;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.utility.pvmanager.widgets.VTableWidget;
import org.eclipse.swt.widgets.Composite;

public class VTableDisplayFigure extends AbstractSelectionWidgetFigure<VTableWidget> {

	public VTableDisplayFigure(AbstractBaseEditPart editPart) {
		super(editPart);
	}

	@Override
	protected VTableWidget createSWTWidget(Composite parent, int style) {
		return new VTableWidget(parent);
	}

	public boolean isRunMode() {
		return runmode;
	}
}
