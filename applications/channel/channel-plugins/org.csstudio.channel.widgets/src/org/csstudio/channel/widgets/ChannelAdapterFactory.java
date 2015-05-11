package org.csstudio.channel.widgets;

import java.util.Collection;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.utility.pvmanager.widgets.ConfigurableWidget;
import org.csstudio.utility.pvmanager.widgets.ConfigurableWidgetAdaptable;
import org.csstudio.utility.pvmanager.widgets.ProcessVariableAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;

/**
 * Adapter factor for the common adaptables. This will adapt a selection of
 * {@link ChannelAdaptable}
 *
 * @author shroffk
 *
 */
public class ChannelAdapterFactory implements IAdapterFactory {

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
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
    return null;
    }

    @Override
    public Class[] getAdapterList() {
    return new Class[] { Channel.class, Channel[].class };
    }

}
