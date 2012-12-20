package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.csstudio.csdata.ProcessVariable;

public class Line2DPlotSelection implements ChannelQueryAdaptable,
		ConfigurableWidgetAdaptable {

	private final ChannelQuery channelQuery;
	private final Line2DPlotWidget line2dPlotWidget;

	public Line2DPlotSelection(ChannelQuery channelQuery,
			Line2DPlotWidget line2dPlotWidget) {
		super();
		this.channelQuery = channelQuery;
		this.line2dPlotWidget = line2dPlotWidget;
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
		return line2dPlotWidget;
	}

	@Override
	public Collection<ChannelQuery> toChannelQueries() {
		if (channelQuery == null)
			return null;
		return Collections.singleton(channelQuery);
	}
}
