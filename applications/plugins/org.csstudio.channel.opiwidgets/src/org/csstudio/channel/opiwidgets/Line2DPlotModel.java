/**
 * 
 */
package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.ChannelLinePlotWidget;
import org.csstudio.opibuilder.widgets.extra.AbstractSelectionWidgetModelDescription;


/**
 * @author shroffk
 *
 */
public class Line2DPlotModel extends AbstractChannelWidgetModel {
	
	public Line2DPlotModel() {
		super(AbstractSelectionWidgetModelDescription.newModelFrom(ChannelLinePlotWidget.class));
	}

	public final String ID = "org.csstudio.channel.opiwidgets.Line2DPlot"; //$NON-NLS-1$
	
	@Override
	protected void configureProperties() {
	}

	/* (non-Javadoc)
	 * @see org.csstudio.opibuilder.model.AbstractWidgetModel#getTypeID()
	 */
	@Override
	public String getTypeID() {
		return ID;
	}

}
