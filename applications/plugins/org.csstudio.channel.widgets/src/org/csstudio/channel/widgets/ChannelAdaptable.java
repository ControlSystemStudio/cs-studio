package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.Channel;

import java.util.Collection;


/**
 * 
 * @author shroffk
 * 
 */
public interface ChannelAdaptable extends ProcessVariableAdaptable {
	public Collection<Channel> toChannels();
}
