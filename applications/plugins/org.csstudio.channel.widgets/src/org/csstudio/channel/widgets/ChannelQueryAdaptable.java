package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.ChannelQuery;

import java.util.Collection;

/**
 * 
 * @author shroffk
 * 
 */
public interface ChannelQueryAdaptable extends ChannelAdaptable {
	public Collection<ChannelQuery> toChannelQueries();
}
