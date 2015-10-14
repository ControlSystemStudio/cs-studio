package org.csstudio.utility.pvmanager.ui.toolbox;

import org.diirt.datasource.ChannelHandler;
import org.diirt.datasource.CompositeDataSource;
import org.diirt.datasource.PVManager;

public class DataSourceChannel implements Comparable<DataSourceChannel> {
    private final String dataSource;
    private final ChannelHandler channel;

    public DataSourceChannel(String dataSource, ChannelHandler channel) {
        this.dataSource = dataSource;
        this.channel = channel;
    }

    public boolean isConnected() {
        return channel.isConnected();
    }

    public String getChannelName() {
        return channel.getChannelName();
    }

    private String getDelimiter() {
        return ((CompositeDataSource) PVManager.getDefaultDataSource()).getConfiguration().getDelimiter();
    }

    public String getFullChannelName() {
        return dataSource + getDelimiter() + getChannelName();
    }

    @Override
    public int compareTo(DataSourceChannel otherChannel) {
        return getFullChannelName().compareTo(otherChannel.getFullChannelName());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DataSourceChannel) {
            return getFullChannelName().equals(((DataSourceChannel) obj).getFullChannelName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getFullChannelName().hashCode();
    }

    public int getTotalUsageCounter() {
        return channel.getUsageCounter();
    }

    public int getReadUsageCounter() {
        return channel.getReadUsageCounter();
    }

    public int getWriteUsageCounter() {
        return channel.getWriteUsageCounter();
    }


}
