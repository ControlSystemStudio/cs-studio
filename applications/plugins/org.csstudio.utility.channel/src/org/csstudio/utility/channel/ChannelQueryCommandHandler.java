package org.csstudio.utility.channel;

import gov.bnl.channelfinder.api.ChannelQuery;

/**
 * Abstract class for all commands that use channels.
 * 
 * @author carcassi
 *
 */
public abstract class ChannelQueryCommandHandler extends AbstractAdaptedHandler<ChannelQuery> {

	public ChannelQueryCommandHandler() {
		super(ChannelQuery.class);
	}

}