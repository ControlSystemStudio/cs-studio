package org.csstudio.utility.channel;

import gov.bnl.channelfinder.api.Channel;

public interface ICSSChannelFactory {

	public ICSSChannel getCSSChannel(Channel channel);
	
}
