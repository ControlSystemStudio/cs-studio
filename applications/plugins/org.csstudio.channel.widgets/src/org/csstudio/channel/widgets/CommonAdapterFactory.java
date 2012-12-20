package org.csstudio.channel.widgets;

import java.util.Collection;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;

import org.csstudio.csdata.ProcessVariable;
import org.eclipse.core.runtime.IAdapterFactory;

/**
 * Adapter factor for the common adaptables. This will adapt a selection of
 * {@link ConfigurableWidgetAdaptable}, {@link ProcessVariableAdaptable},
 * {@link ChannelAdaptable} and {@link ChannelQueryAdaptable} to the appropriate
 * objects and arrays so that the logic does not have to be replicated over and
 * over.
 * 
 * @author shroffk
 * 
 */
public class CommonAdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof ProcessVariableAdaptable) {
			ProcessVariableAdaptable processVariableAdaptable = (ProcessVariableAdaptable) adaptableObject;
			Collection<ProcessVariable> pvs = processVariableAdaptable
					.toProcesVariables();
			if (adapterType == ProcessVariable.class) {
				if (pvs != null && pvs.size() == 1)
					return pvs.iterator().next();
			} else if (adapterType == ProcessVariable[].class) {
				if (pvs != null && !pvs.isEmpty())
					return pvs.toArray(new ProcessVariable[pvs.size()]);
			}
		} 
		if (adaptableObject instanceof ChannelAdaptable) {
			ChannelAdaptable channelAdaptable = (ChannelAdaptable) adaptableObject;
			Collection<Channel> channels = channelAdaptable.toChannels();
			if (adapterType == Channel.class) {
				if (channels != null && channels.size() == 1)
					return channels.iterator().next();
			} else if (adapterType == Channel[].class) {
				if (channels != null && !channels.isEmpty())
					return channels.toArray(new Channel[channels.size()]);
			}
		} 
		if (adaptableObject instanceof ChannelQueryAdaptable) {
			ChannelQueryAdaptable channelQueryAdaptable = (ChannelQueryAdaptable) adaptableObject;
			Collection<ChannelQuery> channelQueries = channelQueryAdaptable
					.toChannelQueries();
			if (adapterType == ChannelQuery.class) {
				if (channelQueries != null && channelQueries.size() == 1)
					return channelQueries.iterator().next();
			} else if (adapterType == ChannelQuery[].class) {
				if (channelQueries != null && !channelQueries.isEmpty())
					return channelQueries.toArray(new ChannelQuery[channelQueries
							.size()]);
			}
		} 
		if (adaptableObject instanceof ConfigurableWidgetAdaptable) {
			if (adapterType == ConfigurableWidget.class) {
				ConfigurableWidgetAdaptable configurableWidgetAdaptable = (ConfigurableWidgetAdaptable) adaptableObject;
				if (configurableWidgetAdaptable != null && configurableWidgetAdaptable.toConfigurableWidget() != null &&
						configurableWidgetAdaptable.toConfigurableWidget().isConfigurable())
					return configurableWidgetAdaptable.toConfigurableWidget();
				else
					return null;
			}

		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return new Class[] { Channel.class, Channel[].class, ChannelQuery.class, ChannelQuery[].class,
				ProcessVariable.class, ProcessVariable[].class,
				ConfigurableWidget.class };
	}

}
