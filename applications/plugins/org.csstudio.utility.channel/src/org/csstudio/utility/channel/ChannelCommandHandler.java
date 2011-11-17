package org.csstudio.utility.channel;

import gov.bnl.channelfinder.api.Channel;

/**
 * Abstract class for all commands that use channels.
 * 
 * @author carcassi
 *
 */
public abstract class ChannelCommandHandler extends AbstractAdaptedHandler<Channel> {

	public ChannelCommandHandler() {
		super(Channel.class);
	}

}