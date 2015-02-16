package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;

import java.util.Collection;
import java.util.Collections;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.utility.pvmanager.widgets.ConfigurableWidget;
import org.csstudio.utility.pvmanager.widgets.ConfigurableWidgetAdaptable;

public class ChannelLinePlotSelection implements ChannelQueryAdaptable,
		ConfigurableWidgetAdaptable {

	private final ChannelQuery channelQuery;
	private final ChannelLinePlotWidget line2dPlotWidget;

	public ChannelLinePlotSelection(ChannelQuery channelQuery,
			ChannelLinePlotWidget line2dPlotWidget) {
		super();
		this.channelQuery = channelQuery;
		this.line2dPlotWidget = line2dPlotWidget;
	}

	@Override
	public Collection<Channel> toChannels() {
		return AdaptableUtilities.toChannels(toChannelQueries());
	}

	@Override
	public Collection<ProcessVariable> toProcessVariables() {
		return AdaptableUtilities.toProcessVariables(toChannels());
	}

	@Override
	public ConfigurableWidget toConfigurableWidget() {
		return line2dPlotWidget;
	}

	@Override
	public Collection<ChannelQuery> toChannelQueries() {
		if (channelQuery == null)
			return null;
		return Collections.singleton(channelQuery);
	}
}
