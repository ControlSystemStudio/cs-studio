/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.csstudio.alarm.diirt.datasource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.client.AlarmTreeRoot;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModelListener;
import org.diirt.datasource.ChannelHandler;
import org.diirt.datasource.ChannelReadRecipe;
import org.diirt.datasource.ChannelWriteRecipe;
import org.diirt.datasource.DataSource;
import org.diirt.datasource.MultiplexedChannelHandler;
import org.diirt.datasource.ReadRecipe;
import org.diirt.datasource.WriteRecipe;
import org.diirt.datasource.util.FunctionParser;
import org.diirt.datasource.vtype.DataTypeSupport;

/**
 * @author Kunal Shroff
 *
 */
public class BeastDataSource extends DataSource {

    private static final Logger log = Logger.getLogger(BeastDataSource.class.getName());

    private final BeastDataSourceConfiguration configuration;
    private final BeastTypeSupport typeSupport;

    private AlarmTreeRoot alarmTreeRoot;
    private AlarmClientModel model;

    private List<String> activeAlarms = new ArrayList<String>();
    private List<String> acknowledgedAlarms = new ArrayList<String>();
    
    static {
        // Install type support for the types it generates.
        DataTypeSupport.install();
    }

    public BeastDataSource(BeastDataSourceConfiguration configuration) {
        super(true);
        this.configuration = configuration;

        typeSupport = new BeastTypeSupport();
        
        try {
            
         // Create an instance to the AlarmClientModel
            
            // TODO: this might have to be moved to a different thread
            if (configuration.getConfigName() != null && !configuration.getConfigName().isEmpty()) {
                model = AlarmClientModel.getInstance(configuration.getConfigName());
            } else{
                model = AlarmClientModel.getInstance();
            }
            model.addListener(new AlarmClientModelListener() {
                
                @Override
                public void newAlarmConfiguration(AlarmClientModel model) {
                    log.fine("newAlarmConfiguration");
                    synchronized (model) {
                        activeAlarms = Collections.synchronizedList(Arrays
                                .asList(model.getActiveAlarms()).stream()
                                .map(AlarmTreePV::getName)
                                .collect(Collectors.<String> toList()));
                        acknowledgedAlarms = Collections.synchronizedList(Arrays
                                .asList(model.getAcknowledgedAlarms()).stream()
                                .map(AlarmTreePV::getName)
                                .collect(Collectors.<String> toList()));
                    }
                    for (String channelName : map.keySet()) {
                        BeastChannelHandler channel = (BeastChannelHandler) getChannels().get(channelHandlerLookupName(channelName));
                        channel.reconnect();
                    }
                }

                @Override
                public void serverTimeout(AlarmClientModel model) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void serverModeUpdate(AlarmClientModel model, boolean maintenance_mode) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void newAlarmState(AlarmClientModel model, AlarmTreePV pv, boolean parent_changed) {
                    log.fine("newAlarmState");
                    synchronized (model) {
                        activeAlarms = Collections.synchronizedList(Arrays
                                .asList(model.getActiveAlarms()).stream()
                                .map(AlarmTreePV::getName)
                                .collect(Collectors.<String> toList()));
                        acknowledgedAlarms = Collections.synchronizedList(Arrays
                                .asList(model.getAcknowledgedAlarms()).stream()
                                .map(AlarmTreePV::getName)
                                .collect(Collectors.<String> toList()));
                    }
                    if(pv != null){
                        log.fine(pv.getName());
                        List<Consumer> handlers = map.get(pv.getName());
                        if (handlers != null) {
                            for (Consumer consumer : handlers) {
                                consumer.accept(pv);
                            }
                        }
                    }
                }
            });
            alarmTreeRoot = AlarmClientModel.getInstance().getConfigTree().getRoot();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected boolean isActive(String channelName) {
        synchronized (model) {
            return activeAlarms.contains(channelName);
        }
    }

    protected boolean isAcknowledged(String channelName) {
        synchronized (model) {
            return acknowledgedAlarms.contains(channelName);
        }
    }
    
    protected boolean isEnabled(String channelName){
        synchronized (model) {
            return model.findPV(channelName).isEnabled();
        }
    }

    private Map<String, List<Consumer>> map = Collections.synchronizedMap(new HashMap<String, List<Consumer>>());
    
    @Override
    protected ChannelHandler createChannel(String channelName) {
        /**
         * Parse the name to support defining the read and write types / the
         * sytax is as follows
         * 
         * jms://topic_name<readType, writeType>{filter}
         **/
        
        return new BeastChannelHandler(channelName, this);
    }

    @Override
    public void close() {
        super.close();
        model.release();
    }

    public BeastTypeSupport getTypeSupport() {
        return typeSupport;
    }

    protected void add(String channelName, Consumer beastChannelHandler){
        synchronized (map) {
            if (!map.containsKey(channelName) || map.get(channelName) == null) {
                map.put(channelName, new ArrayList<Consumer>());
            }
            map.get(channelName).add(beastChannelHandler);
        }
    }

    protected void remove(String channelName, Consumer beastChannelHandler) {
        synchronized (map) {
            if (map.containsKey(channelName)) {
                map.get(channelName).remove(beastChannelHandler);
            }
        }
    }

    protected AlarmTreePV findPV(String channelName){
        return model.findPV(channelName);
    }

    protected boolean isConnected() {
        return model.isServerAlive();
    }

    protected void acknowledge(String channelName, boolean acknowledge){
        model.acknowledge(model.findPV(channelName), acknowledge);
    }

    protected void enable(String channelName, boolean enable) throws Exception {
        model.enable(model.findPV(channelName), enable);
    }
}
