package org.csstudio.channel.opiwidgets;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;

import org.csstudio.channel.widgets.ConfigurableWidget;
import org.eclipse.core.runtime.IAdapterFactory;

/**The adaptor factory to make a PV widget as a PV provider for css context menu.
 * @author Xihui Chen
 *
 */
public class ChannelEditPartAdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
		if (adaptableObject instanceof AbstractChannelWidgetEditPart) {
			AbstractChannelWidgetFigure<?> figure = ((AbstractChannelWidgetEditPart<?,?>)adaptableObject).getFigure();
			if (adapterType == Channel[].class) {
				return figure.getSelectedChannels();
			}
			
			if (adapterType == ChannelQuery[].class) {
				return figure.getSelectedChannelQuery();
			}
			
			if (adapterType == ConfigurableWidget.class && figure.isRunMode()) {
				Object widget = figure.getSWTWidget();
				if (widget instanceof ConfigurableWidget) {
					ConfigurableWidget confWidget = (ConfigurableWidget) widget;
					if (confWidget.isConfigurable())
						return confWidget;
				}
			}
		}
		return null;
	}

	public Class<?>[] getAdapterList() {
        return new Class<?>[] { Channel[].class, ChannelQuery[].class, ConfigurableWidget.class };
	}

}
