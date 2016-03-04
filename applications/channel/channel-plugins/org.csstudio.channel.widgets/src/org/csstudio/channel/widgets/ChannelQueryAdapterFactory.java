package org.csstudio.channel.widgets;

import java.util.Collection;

import gov.bnl.channelfinder.api.ChannelQuery;

import org.eclipse.core.runtime.IAdapterFactory;

/**
 * Adapter factor for a selection of {@link ChannelQueryAdaptable} to the
 * appropriate objects and arrays.
 *
 * @author shroffk
 *
 */
public class ChannelQueryAdapterFactory implements IAdapterFactory {

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
    if (adaptableObject instanceof ChannelQueryAdaptable) {
        ChannelQueryAdaptable channelQueryAdaptable = (ChannelQueryAdaptable) adaptableObject;
        Collection<ChannelQuery> channelQueries = channelQueryAdaptable
            .toChannelQueries();
        if (adapterType == ChannelQuery.class) {
        if (channelQueries != null && channelQueries.size() == 1)
            return channelQueries.iterator().next();
        } else if (adapterType == ChannelQuery[].class) {
        if (channelQueries != null && !channelQueries.isEmpty())
            return channelQueries
                .toArray(new ChannelQuery[channelQueries.size()]);
        }
    }
    return null;
    }

    @Override
    public Class[] getAdapterList() {
        return new Class[] { ChannelQuery.class, ChannelQuery[].class };
    }

}
