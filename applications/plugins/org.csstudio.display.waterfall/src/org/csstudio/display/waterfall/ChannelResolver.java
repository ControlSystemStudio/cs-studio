package org.csstudio.display.waterfall;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelFinderClient;
import gov.bnl.channelfinder.api.ChannelUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ChannelResolver {
	public static List<String> resolveTag(String tag) {
		try {
			Collection<Channel> channels = ChannelFinderClient.getInstance().findChannelsByTag(tag);
			if (channels == null || channels.isEmpty()) {
				return null;
			}
			
			List<String> channelNames = new ArrayList<String>();
			for (Channel channel : channels) {
				channelNames.add(channel.getName());
			}
			return channelNames;
		} catch (Exception e) {
			return null;
		}
	}
}
