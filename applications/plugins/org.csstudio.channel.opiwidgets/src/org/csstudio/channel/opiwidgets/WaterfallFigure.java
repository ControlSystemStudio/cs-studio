package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.WaterfallWidget;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.eclipse.swt.SWT;

public class WaterfallFigure extends AbstractChannelWidgetFigure<WaterfallWidget> {
	
	public WaterfallFigure(AbstractBaseEditPart editPart) {
		super(editPart);
		widget = new WaterfallWidget(composite, SWT.NONE);
	}
}
