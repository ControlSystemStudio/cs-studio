package org.csstudio.graphene;

import org.csstudio.channel.opiwidgets.AbstractChannelWidgetFigure;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class HistogramFigure extends AbstractChannelWidgetFigure<HistogramWidget> {
	
	public HistogramFigure(AbstractBaseEditPart editPart) {
		super(editPart);
	}
	
	@Override
	protected HistogramWidget createSWTWidget(Composite parent, int style) {
		return new HistogramWidget(parent, SWT.NONE);
	}
}
