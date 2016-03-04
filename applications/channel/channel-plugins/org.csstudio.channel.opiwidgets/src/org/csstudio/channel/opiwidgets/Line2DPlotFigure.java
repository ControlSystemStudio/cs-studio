/**
 *
 */
package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.ChannelLinePlotWidget;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author shroffk
 *
 */
public class Line2DPlotFigure extends AbstractChannelWidgetFigure<ChannelLinePlotWidget> {

    public Line2DPlotFigure(AbstractBaseEditPart editPart) {
        super(editPart);
    }

    /* (non-Javadoc)
     * @see org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure#createSWTWidget(org.eclipse.swt.widgets.Composite, int)
     */
    @Override
    protected ChannelLinePlotWidget createSWTWidget(Composite parent, int style) {
        return new ChannelLinePlotWidget(parent, SWT.NONE);
    }

}
