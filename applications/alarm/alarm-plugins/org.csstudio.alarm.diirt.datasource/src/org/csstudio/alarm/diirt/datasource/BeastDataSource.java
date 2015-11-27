/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.csstudio.alarm.diirt.datasource;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
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

    private final BeastTypeSupport typeSupport;

    // The model, activeAlarms and acknowledgedAlarms is shared by the entire
    // datasource, the benefit of does this at the datasource level instead of
    // in each channel is that they need to be computed only once and only a single
    // copy needs to be maintained.
    private AlarmClientModel model;

    private List<String> activeAlarms = new ArrayList<String>();
    private List<String> acknowledgedAlarms = new ArrayList<String>();

    private Map<String, List<Consumer>> map = Collections.synchronizedMap(new HashMap<String, List<Consumer>>());

    private Executor executor = Executors.newScheduledThreadPool(4);
    
//    private final Map<String, AlarmClientModel> configModels = Collections.synchronizedMap(new HashMap<String, AlarmClientModel>());

    static {
        // Install type support for the types it generates.
        DataTypeSupport.install();
    }

    public BeastDataSource(BeastDataSourceConfiguration configuration) {
        super(true);

        typeSupport = new BeastTypeSupport();
        
        try {

            // Create an instance to the AlarmClientModel
            final CompletableFuture<Void> future = CompletableFuture
                    .supplyAsync(() -> initialize(configuration), executor)
                    .thenAccept((model) -> {
                        this.model = model;
                        this.model.addListener(new AlarmClientModelListener() {

                            @Override
                            public void newAlarmConfiguration(AlarmClientModel model) {
                                log.fine("newAlarmConfiguration");
                                synchronized (model) {
                                    activeAlarms = Collections.synchronizedList(Arrays
                                            .asList(model.getActiveAlarms())
                                            .stream()
                                            .map(AlarmTreePV::getName)
                                            .collect(Collectors.<String> toList()));
                                    acknowledgedAlarms = Collections.synchronizedList(Arrays
                                            .asList(model.getAcknowledgedAlarms())
                                            .stream()
                                            .map(AlarmTreePV::getName)
                                            .collect(Collectors.<String> toList()));
                                }
                                for (String channelName : map.keySet()) {
                                    BeastChannelHandler channel = (BeastChannelHandler) getChannels()
                                            .get(channelHandlerLookupName(channelName));
                                    channel.reconnect();
                                }
                            }

                            @Override
                            public void serverTimeout(AlarmClientModel model) {
                                // TODO Auto-generated method stub
                            }

                            @Override
                            public void serverModeUpdate(
                                    AlarmClientModel model,
                                    boolean maintenance_mode) {
                                // TODO Auto-generated method stub
                            }

                            @Override
                            public void newAlarmState(AlarmClientModel model, AlarmTreePV pv, boolean parent_changed) {
                                log.fine("newAlarmState");
                                synchronized (model) {
                                    activeAlarms = Collections.synchronizedList(Arrays
                                            .asList(model.getActiveAlarms())
                                            .stream()
                                            .map(AlarmTreePV::getName)
                                            .collect(Collectors.<String> toList()));
                                    acknowledgedAlarms = Collections.synchronizedList(Arrays
                                            .asList(model.getAcknowledgedAlarms())
                                            .stream()
                                            .map(AlarmTreePV::getName)
                                            .collect(Collectors.<String> toList()));
                                }
                                if (pv != null) {
                                    log.info(pv.getPathName());
                                    List<Consumer> handlers = map.get(pv.getPathName());
                                    if (handlers != null) {
                                        for (Consumer consumer : handlers) {
                                            consumer.accept(pv);
                                        }
                                    }
                                }
                            }
                        });
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private AlarmClientModel initialize(BeastDataSourceConfiguration configuration) {
        AlarmClientModel alarmModel;
        try {
            if (configuration.getConfigName() != null && !configuration.getConfigName().isEmpty()) {
                alarmModel = AlarmClientModel.getInstance(configuration.getConfigName());
            } else{
                alarmModel = AlarmClientModel.getInstance();
            }
            return alarmModel;
        } catch (Exception e) {
            return null;
        }
    }

    protected boolean isActive(String channelName) {
        synchronized (model) {
            return activeAlarms.contains(channelName);
        }
    }
    
    protected boolean isEnabled(String channelName){
        synchronized (model) {
            return model.findPV(channelName).isEnabled();
        }
    }

    @Override
    protected ChannelHandler createChannel(String channelName) {
        URI uri;
        String pvName = channelName;
        
        try {
            uri = URI.create(URLEncoder.encode(channelName, "UTF-8"));
            pvName = uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1);
            AlarmTreePV alarmTreePV = findPV(pvName);
            if (alarmTreePV != null) {
                return new BeastChannelHandler(alarmTreePV.getPathName(), this);
            } else {
                String path = URLDecoder.decode(uri.getPath(), "UTF-8");
//                AlarmTreeItem alarmTreeItem = model.getConfigTree().getItemByPath(path);
                return new BeastChannelHandler(path, this);
            }
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("unable to create channel " + channelName);
        }
    }

    @Override
    public void close() {
        super.close();
        if (model != null) model.release();
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
    	if (model == null) return null;
        return model.findPV(channelName);
    }
    
    protected AlarmTreeItem getState(String channelName) throws Exception{
    	URI uri = URI.create(URLEncoder.encode(channelName, "UTF-8"));
        String pvName = uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1);
        AlarmTreePV alarmTreePV = findPV(pvName);
        if (alarmTreePV != null) {
            return alarmTreePV;
        } else {
            String path = URLDecoder.decode(uri.getPath(), "UTF-8");
            AlarmTreeItem alarmTreeItem = model.getConfigTree().getItemByPath(path);
            return alarmTreeItem;
        }
    }

    protected boolean isConnected() {
        return model.isServerAlive();
    }

    protected void acknowledge(String channelName, boolean acknowledge) throws Exception{
        getState(channelName).acknowledge(acknowledge);
    }

    protected void enable(String channelName, boolean enable) throws Exception {
        model.enable(model.findPV(channelName), enable);
    }
}
