/**
 * 
 */
package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.ChannelViewerWidget;
import org.csstudio.opibuilder.widgets.extra.AbstractSelectionWidgetModelDescription;


/**
 * @author shroffk
 * 
 */
public class ChannelViewerModel extends AbstractChannelWidgetModel {
	
	public ChannelViewerModel() {
		super(AbstractSelectionWidgetModelDescription.newModelFrom(ChannelViewerWidget.class));
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
