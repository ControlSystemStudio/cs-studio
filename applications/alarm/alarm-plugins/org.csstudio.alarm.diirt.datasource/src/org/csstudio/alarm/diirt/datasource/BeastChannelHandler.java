/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.csstudio.alarm.diirt.datasource;

import java.util.function.Consumer;
import java.util.logging.Logger;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
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

    public BeastChannelHandler(String channelName, BeastDataSource beastDataSource) {
        super(channelName);
        this.datasource = beastDataSource;
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
                datasource.acknowledge(getChannelName(), (boolean) newValue);
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
        return new BeastVTableAdapter();
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
            processConnection(new BeastConnectionPayload(datasource.isConnected())); // always send at least the connection state
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
            processConnection(new BeastConnectionPayload(connected));
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
