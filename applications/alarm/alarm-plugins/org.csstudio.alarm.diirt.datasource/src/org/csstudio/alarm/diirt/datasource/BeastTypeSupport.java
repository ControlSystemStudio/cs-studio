/**
 *
 */
package org.csstudio.alarm.diirt.datasource;

import java.util.Arrays;
import java.util.List;

import org.diirt.datasource.DataSourceTypeAdapter;
import org.diirt.datasource.DataSourceTypeSupport;
import org.diirt.datasource.ValueCache;

/**
 * @author Kunal Shroff
 *
 */
public class BeastTypeSupport extends DataSourceTypeSupport {

    private final static List<String> beastChannelTypes = Arrays.asList(
            Messages.Active,
            Messages.Enable,
            Messages.AlarmSeverity,
            "RawTable",
            "Default");
    
    private final BeastTypeAdapterSet adapters;

    public BeastTypeSupport() {
        this.adapters = new BeastTypeAdapterSet();
    }

    public BeastTypeAdapter find(ValueCache<?> cache, BeastConnectionPayload channel){
        return find(adapters.getAdapters(), cache, channel);
    }

    @Override
    protected String formatMessage(ValueCache<?> cache, Object connection,
            int match,
            List<? extends DataSourceTypeAdapter<?, ?>> matchedConverters) {
        // TODO Auto-generated method stub
        return super.formatMessage(cache, connection, match, matchedConverters);
    }
    
    /**
     *  checks if the channel name has one of the special types defined
     * @param channelName
     * @return return channel type
     */
    protected static String getChannelType(String channelName){
        if (channelName.contains(".")) {
            String type = channelName.substring(channelName.lastIndexOf(".") + 1);
            if (beastChannelTypes.contains(type))
                return type;
        }
        return "Default";
    }
    
    /**
     * Strips out any type information in the channel name and returns the channel name alone 
     * @param channelName
     * @return only channel name
     */
    protected static String getStrippedChannelName(String channelName){
        if (channelName.contains(".")) {
            String type = channelName.substring(channelName.lastIndexOf(".") + 1);
            if (beastChannelTypes.contains(type))
                return channelName.substring(0, channelName.lastIndexOf("."));
        }
        return channelName;
    }
}
