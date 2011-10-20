/**
 * 
 */
package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.Channel;

import org.eclipse.core.runtime.IAdapterFactory;

/**
 * @author shroffk
 * 
 */
public class ChannelTreeByPropertyNodeAdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		System.out.println("Adapting " + adaptableObject);
		if (adapterType == Channel[].class) {
			ChannelTreeByPropertyNode node = ((ChannelTreeByPropertyNode) adaptableObject);
			return node.getNodeChannels().toArray(new Channel[node.getNodeChannels().size()]);
		} else if (adapterType == Channel.class) {
				ChannelTreeByPropertyNode node = ((ChannelTreeByPropertyNode) adaptableObject);
				if (node.getNodeChannels().isEmpty())
					return null;
				else
					return node.getNodeChannels().get(0);
		} else {
			return null;
		}
	}

	@Override
	public Class[] getAdapterList() {
		return new Class[] { Channel.class, Channel[].class };
	}

}
