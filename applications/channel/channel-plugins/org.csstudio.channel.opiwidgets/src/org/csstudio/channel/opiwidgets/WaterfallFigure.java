package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.WaterfallWidget;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class WaterfallFigure extends AbstractChannelWidgetFigure<WaterfallWidget> {
	
	public WaterfallFigure(AbstractBaseEditPart editPart) {
		super(editPart);
	}
	
	@Override
	protected WaterfallWidget createSWTWidget(Composite parent, int style) {
		return new WaterfallWidget(parent, SWT.NONE);
	}
}
