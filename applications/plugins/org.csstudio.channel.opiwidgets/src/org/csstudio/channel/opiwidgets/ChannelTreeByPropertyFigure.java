package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.ChannelTreeByPropertyWidget;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ChannelTreeByPropertyFigure extends AbstractSWTWidgetFigure {
	
	public ChannelTreeByPropertyFigure(Composite composite, AbstractContainerModel parentModel) {
		super(composite, parentModel);
		widget = new ChannelTreeByPropertyWidget(composite, SWT.NONE);
	}
	
	private ChannelTreeByPropertyWidget widget;

	@Override
	public ChannelTreeByPropertyWidget getSWTWidget() {
		return widget;
	}
	
	public boolean isRunMode() {
		return runmode;
	}
}
