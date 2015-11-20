/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.csstudio.alarm.diirt.datasource;

import java.util.function.Consumer;
import java.util.logging.Logger;

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

    public BeastChannelHandler(String channelName,
            BeastDataSource beastDataSource) {
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
        if(newValue instanceof String){
            try {
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
            } catch (Exception e) {
                callback.channelWritten(e);
            }
        }else if(newValue instanceof Boolean){
            datasource.acknowledge(getChannelName(), (boolean) newValue);
        }
        callback.channelWritten(null);
    }

    @Override
    protected boolean isWriteConnected(BeastConnectionPayload payload) {
        return true;
    }

    @Override
    protected BeastTypeAdapter findTypeAdapter(ValueCache<?> cache, BeastConnectionPayload connPayload) {
        return new BeastVTableAdapter();
    }

    @Override
    protected void connect() {
        // TODO Auto-generated method stub
        log.fine("connect");
        datasource.add(getChannelName(), this);
        initialize();
    }
    
    protected void reconnect() {
        log.fine("reconnect");
        initialize();
    }

    private void initialize() {
        // TODO Auto-generated method stub
        log.fine("initialize");
        AlarmTreePV initialState = datasource.findPV(getChannelName());
        if (initialState != null) {
            processConnection(new BeastConnectionPayload(initialState, datasource.isConnected()));
            processMessage(new BeastMessagePayload(initialState,
                    datasource.isActive(getChannelName()),
                    datasource.isAcknowledged(getChannelName()),
                    datasource.isEnabled(getChannelName())));
        }
    }

    @Override
    public void accept(Object o) {
        // Call onMessage or onConnection
        if (o instanceof AlarmTreePV) {
            log.fine("processing" + ((AlarmTreePV) o).getName());
            processMessage(new BeastMessagePayload((AlarmTreePV) o,
                    datasource.isActive(getChannelName()),
                    datasource.isAcknowledged(getChannelName()),
                    datasource.isEnabled(getChannelName())));
        } else {
            log.fine(o.toString());
        }
    }
}
