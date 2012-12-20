/**
 * 
 */
package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.Line2DPlotWidget;
import org.csstudio.channel.widgets.TunerWidget;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author shroffk
 *
 */
public class TunerFigure extends AbstractChannelWidgetFigure<TunerWidget> {

	/**
	 * @param editPart
	 */
	public TunerFigure(AbstractBaseEditPart editPart) {
		super(editPart);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure#createSWTWidget(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	protected TunerWidget createSWTWidget(Composite parent, int style) {
		return new TunerWidget(parent, SWT.NONE);
	}

}
