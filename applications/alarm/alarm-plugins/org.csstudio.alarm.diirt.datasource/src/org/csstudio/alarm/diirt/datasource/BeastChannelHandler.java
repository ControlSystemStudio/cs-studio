/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.csstudio.alarm.diirt.datasource;

import java.util.function.Consumer;
import java.util.logging.Logger;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.diirt.datasource.ChannelWriteCallback;
import org.diirt.datasource.MultiplexedChannelHandler;
import org.diirt.datasource.ValueCache;

/**
 * @author Kunal Shroff
 *
 */
public class BeastChannelHandler extends
        MultiplexedChannelHandler<BeastConnectionPayload, BeastMessagePayload>
        implements Consumer {

    private static final Logger log = Logger.getLogger(BeastChannelHandler.class.getName());

    private BeastDataSource datasource;
    private final String channelType;

    public BeastChannelHandler(String channelName, String channelType, BeastDataSource beastDataSource) {
        super(channelName);
        this.channelType = channelType;
        this.datasource = beastDataSource;
        // we do not want diirt to resend the last message when we call processConnection() only,
        // because of the delay between the AlarmServer becoming unavailable and AlarmClientModelListener's
        // serverTimeout() being called: during that delay, a PVs AlarmSeverity might have changed, but diirt
        // would resend the old values
        this.setProcessMessageOnDisconnect(false);
        this.setProcessMessageOnReconnect(false);
    }

    @Override
    protected boolean isConnected(BeastConnectionPayload payload) {
        return payload.isConnected();
    };

    @Override
    protected void disconnect() {
        log.fine("disconnect: " + getChannelName());
        datasource.remove(getChannelName(), this);
    }

    /**
     * TODO the newValue has only been tested with String. The handling on writing boolean objects needs to be tested.
     */
    @Override
    public void write(Object newValue, ChannelWriteCallback callback) {
        log.fine("write");
        try {
            if (newValue instanceof String) {
                switch ((String) newValue) {
                case "ack":
                case "ACK":
                    datasource.acknowledge(getChannelName(), true);
                    break;
                case "unack":
                case "UNACK":
                    datasource.acknowledge(getChannelName(), false);
                    break;
                case "enable":
                    datasource.enable(getChannelName(), true);
                    break;
                case "disable":
                    datasource.enable(getChannelName(), false);
                    break;
                default:
                    break;
                }
            } else if (newValue instanceof Boolean) {
                if(Messages.Enable.equals(channelType)){
                    datasource.enable(getChannelName(), (boolean) newValue);
                }else if(Messages.Acknowledge.equals(channelType)){
                    datasource.acknowledge(getChannelName(), (boolean) newValue);
                }
            }
        } catch (Exception e) {
            callback.channelWritten(e);
        }
        callback.channelWritten(null);
    }

    @Override
    protected boolean isWriteConnected(BeastConnectionPayload payload) {
        return datasource.isWriteAllowed();
    }

    @Override
    protected BeastTypeAdapter findTypeAdapter(ValueCache<?> cache, BeastConnectionPayload connPayload) {
        return datasource.getTypeSupport().find(cache, connPayload);
    }

    @Override
    protected void connect() {
        log.fine("connect: " + getChannelName());
        datasource.add(getChannelName(), this);
        initialize();
    }

    protected void reconnect() {
        log.fine("reconnect: " + getChannelName());
        initialize();
    }

    private void initialize() {
        log.fine("initialize: " + getChannelName());
        AlarmTreeItem initialState;
        try {
            initialState = datasource.getState(getChannelName());
            processConnection(new BeastConnectionPayload(datasource.isConnected(), channelType)); // always send at least the connection state
            if (initialState != null) {
                processMessage(new BeastMessagePayload(initialState));
            }
        } catch (Exception e) {
            reportExceptionToAllReadersAndWriters(e);
        }
    }

    protected void connectionStateChanged(boolean connected) {
//        log.info("connectionStateChanged called: " + getChannelName() + " (connected: " + connected + ")");
        try {
            processConnection(new BeastConnectionPayload(connected, channelType));
        } catch (Exception e) {
            log.warning("connectionStateChange: processConnection threw an exception: " + e.toString());
        }
    }

    @Override
    public void accept(Object o) {
        // Call onMessage or onConnection
        if (o instanceof AlarmTreeItem) {
            log.fine("processing" + ((AlarmTreeItem) o).getName());
            processMessage(new BeastMessagePayload((AlarmTreeItem) o));
        } else {
            log.fine(o.toString());
        }
    }
}
