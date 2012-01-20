/**
 * 
 */
package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;

import org.csstudio.csdata.ProcessVariable;
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
			} else if (adapterType == ProcessVariable.class) {
				if (node.getNodeChannels().size() == 1) {
					return new ProcessVariable(node.getNodeChannels().get(0).getName());
				}
			} else if (adapterType == ProcessVariable[].class) {
				if (node.getNodeChannels().isEmpty())
					return null;
				
				ProcessVariable[] pvs = new ProcessVariable[node.getNodeChannels().size()];
				for (int i = 0; i < node.getNodeChannels().size(); i++) {
					pvs[i] = new ProcessVariable(node.getNodeChannels().get(i).getName());
				}
				return pvs;
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
		return ChannelQuery.query(node.getSubQuery())
				.result(node.getNodeChannels(), null).build();
	}

	@Override
	public Class[] getAdapterList() {
		return new Class[] { Channel.class, Channel[].class, ChannelQuery.class, ChannelQuery[].class, ProcessVariable.class, ProcessVariable[].class, ConfigurableWidget.class };
	}

}
