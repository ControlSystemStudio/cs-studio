/**
 * 
 */
package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;

import org.eclipse.core.runtime.IAdapterFactory;

/**
 * @author shroffk
 * 
 */
public class ChannelTreeByPropertyNodeAdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof ChannelTreeByPropertyNode) {
			ChannelTreeByPropertyNode node = ((ChannelTreeByPropertyNode) adaptableObject);
			if (adapterType == Channel[].class) {
				return node.getNodeChannels().toArray(new Channel[node.getNodeChannels().size()]);
			} else if (adapterType == Channel.class) {
				if (node.getNodeChannels().isEmpty())
					return null;
				else
					return node.getNodeChannels().get(0);
			} else if (adapterType == ChannelQuery.class) {
				return toQuery(node);
			} else if (adapterType == ChannelQuery[].class) {
				ChannelQuery query = toQuery(node);
				if (query == null)
					return null;
				return new ChannelQuery[] {query};
			} else if (adapterType == ConfigurableWidget.class) {
				ConfigurableWidget widget = node.getConfigurableWidget();
				if (widget.isConfigurable())
					return widget;
			}
		}
		return null;
	}
	
	private ChannelQuery toQuery(ChannelTreeByPropertyNode node) {
		if (!node.isSubQuery()) {
			return null;
		}
		return ChannelQuery.Builder.query(node.getSubQuery())
				.result(node.getNodeChannels(), null).create();
	}

	@Override
	public Class[] getAdapterList() {
		return new Class[] { Channel.class, Channel[].class, ChannelQuery.class, ChannelQuery[].class, ConfigurableWidget.class };
	}

}
