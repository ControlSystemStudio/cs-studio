package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.MultiChannelWaterfallWidget;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.utility.pvmanager.widgets.WaterfallWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class WaterfallFigure extends AbstractChannelWidgetFigure<WaterfallWidget> {
	
	public WaterfallFigure(Composite composite, AbstractContainerModel parentModel) {
		super(composite, parentModel);
		widget = new MultiChannelWaterfallWidget(composite, SWT.NONE);
	}
}
