package org.csstudio.channel.opiwidgets;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;

import org.eclipse.core.runtime.IAdapterFactory;

/**The adaptor factory to make a PV widget as a PV provider for css context menu.
 * @author Xihui Chen
 *
 */
public class ChannelEditPartAdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
		if (adaptableObject instanceof AbstractChannelWidgetEditPart) {
			AbstractChannelWidgetFigure<?> figure = ((AbstractChannelWidgetEditPart)adaptableObject).getFigure();
			if (adapterType == Channel[].class) {
				return figure.getSelectedChannels();
			}
			
			if (adapterType == ChannelQuery[].class) {
				return figure.getSelectedChannelQuery();
			}
		}
		return null;
	}

	public Class<?>[] getAdapterList() {
        return new Class<?>[] { Channel[].class, ChannelQuery[].class };
	}

}
