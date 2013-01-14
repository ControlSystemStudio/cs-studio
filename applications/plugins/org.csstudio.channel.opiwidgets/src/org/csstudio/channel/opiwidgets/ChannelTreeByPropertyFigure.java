package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.ChannelTreeByPropertyWidget;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ChannelTreeByPropertyFigure extends AbstractChannelWidgetFigure<ChannelTreeByPropertyWidget> {
	
	public ChannelTreeByPropertyFigure(AbstractBaseEditPart editPart) {
		super(editPart);
	}
	
	@Override
	protected ChannelTreeByPropertyWidget createSWTWidget(Composite parent, int style) {
		return new ChannelTreeByPropertyWidget(parent, SWT.NONE);
	}
}
