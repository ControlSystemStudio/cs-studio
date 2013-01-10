package org.csstudio.channel.widgets;

import java.util.Collection;
import java.util.Collections;

import org.csstudio.csdata.ProcessVariable;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;

public class WaterfallSelection implements ChannelQueryAdaptable, ConfigurableWidgetAdaptable {
	
	private final ChannelQuery channelQuery;
	private final WaterfallWidget waterfallWidget;
	
	public WaterfallSelection(ChannelQuery channelQuery,
			WaterfallWidget waterfallWidget) {
		this.channelQuery = channelQuery;
		this.waterfallWidget = waterfallWidget;
	}

	@Override
	public Collection<Channel> toChannels() {
		return AdaptableUtilities.toChannels(toChannelQueries());
	}

	@Override
	public Collection<ProcessVariable> toProcesVariables() {
		return AdaptableUtilities.toProcessVariables(toChannels());
	}

	@Override
	public ConfigurableWidget toConfigurableWidget() {
		return waterfallWidget;
	}

	@Override
	public Collection<ChannelQuery> toChannelQueries() {
		if (channelQuery == null)
			return null;
		return Collections.singleton(channelQuery);
	}

	
	

}
