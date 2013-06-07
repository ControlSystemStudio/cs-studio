/**
 * 
 */
package org.csstudio.channel.opiwidgets;


/**
 * @author shroffk
 * 
 */
public class ChannelViewerModel extends AbstractChannelWidgetModel {
	
	public ChannelViewerModel() {
		super(false);
	}

	public final String ID = "org.csstudio.channel.opiwidgets.ChannelViewer"; //$NON-NLS-1$

	@Override
	protected void configureProperties() {
	}

	@Override
	public String getTypeID() {
		return ID;
	}

}
