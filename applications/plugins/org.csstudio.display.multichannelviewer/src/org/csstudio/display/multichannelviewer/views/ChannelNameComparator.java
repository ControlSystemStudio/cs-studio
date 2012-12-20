package org.csstudio.display.multichannelviewer.views;

import gov.bnl.channelfinder.api.Channel;

import java.util.Comparator;

public class ChannelNameComparator implements Comparator<Channel> {
	
	@Override
	public int compare(Channel ch1, Channel ch2) {
		return ch1.getName().compareTo(ch2.getName());
	}

}
