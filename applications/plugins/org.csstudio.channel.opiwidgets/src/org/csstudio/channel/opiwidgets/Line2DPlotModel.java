/**
 * 
 */
package org.csstudio.channel.opiwidgets;


/**
 * @author shroffk
 *
 */
public class Line2DPlotModel extends AbstractChannelWidgetModel {
	
	public Line2DPlotModel() {
		super(true);
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
