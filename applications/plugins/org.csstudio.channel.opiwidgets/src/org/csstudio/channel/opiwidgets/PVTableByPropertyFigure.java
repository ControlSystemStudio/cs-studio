package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.PVTableByPropertyWidget;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class PVTableByPropertyFigure extends AbstractChannelWidgetFigure<PVTableByPropertyWidget> {
	
	public PVTableByPropertyFigure(Composite composite, AbstractContainerModel parentModel) {
		super(composite, parentModel);
		widget = new PVTableByPropertyWidget(composite, SWT.NONE);
	}
	
}
