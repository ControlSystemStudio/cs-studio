package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.ChannelTreeByPropertyWidget;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ChannelTreeByPropertyFigure extends AbstractChannelWidgetFigure<ChannelTreeByPropertyWidget> {
	
	public ChannelTreeByPropertyFigure(Composite composite, AbstractContainerModel parentModel) {
		super(composite, parentModel);
		widget = new ChannelTreeByPropertyWidget(composite, SWT.NONE);
		selectionProvider = widget.getTreeSelectionProvider();
	}
}
