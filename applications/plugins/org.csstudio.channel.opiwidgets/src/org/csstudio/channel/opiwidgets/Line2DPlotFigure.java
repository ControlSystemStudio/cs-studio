/**
 * 
 */
package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.Line2DPlotWidget;
import org.csstudio.channel.widgets.WaterfallWidget;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author shroffk
 *
 */
public class Line2DPlotFigure extends AbstractChannelWidgetFigure<Line2DPlotWidget> {

	public Line2DPlotFigure(AbstractBaseEditPart editPart) {
		super(editPart);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure#createSWTWidget(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	protected Line2DPlotWidget createSWTWidget(Composite parent, int style) {
		return new Line2DPlotWidget(parent, SWT.NONE);
	}

}
