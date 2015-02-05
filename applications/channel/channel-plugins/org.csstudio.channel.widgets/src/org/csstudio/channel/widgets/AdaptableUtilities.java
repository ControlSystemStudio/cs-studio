package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;

import java.util.ArrayList;
import java.util.Collection;

import org.csstudio.csdata.ProcessVariable;

public class AdaptableUtilities {
	
	public static Collection<ProcessVariable> toProcessVariables(Collection<Channel> channels) {
		if (channels == null)
			return null;

		Collection<ProcessVariable> result = new ArrayList<ProcessVariable>();
		for (Channel channel : channels) {
			result.add(new ProcessVariable(channel.getName()));
		}
		return result;
	}
	
	public static Collection<Channel> toChannels(Collection<ChannelQuery> channelQueries) {
		if (channelQueries == null)
			return null;
		
		Collection<Channel> result = new ArrayList<Channel>();
		for (ChannelQuery query : channelQueries) {
			Collection<Channel> channels = query.getResult().channels;
			if (channels != null)
				result.addAll(channels);
		}
		
		if (result.isEmpty())
			return null;
		return result;
	}
}
