/**
 * 
 */
package org.csstudio.channel.opiwidgets;

/**
 * @author shroffk
 * 
 */
public class ChannelViewerModel extends AbstractChannelWidgetModel {

	public final String ID = "org.csstudio.channel.opiwidgets.ChannelViewer"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.csstudio.opibuilder.model.AbstractWidgetModel#configureProperties()
	 */
	@Override
	protected void configureProperties() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csstudio.opibuilder.model.AbstractWidgetModel#getTypeID()
	 */
	@Override
	public String getTypeID() {
		return ID;
	}

}
