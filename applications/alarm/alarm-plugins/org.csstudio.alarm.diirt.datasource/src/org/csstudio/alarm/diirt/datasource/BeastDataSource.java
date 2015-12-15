/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.csstudio.alarm.diirt.datasource;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.client.AlarmTreeRoot;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModelListener;
import org.diirt.datasource.ChannelHandler;
import org.diirt.datasource.DataSource;
import org.diirt.datasource.vtype.DataTypeSupport;

import com.thoughtworks.xstream.InitializationException;

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

    private Map<String, List<Consumer>> map = Collections.synchronizedMap(new HashMap<String, List<Consumer>>());

    private Executor executor = Executors.newScheduledThreadPool(4);

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

                            @SuppressWarnings("rawtypes")
                            @Override
                            public void newAlarmState(AlarmClientModel model, AlarmTreePV pv, boolean parent_changed) {
                                log.fine("newAlarmState");
                                if (pv != null) {
                                    log.fine(pv.getPathName());
                                    List<Consumer> pathHandlers = map.get(pv.getPathName().substring(1));
                                    if (pathHandlers != null) {
                                        for (Consumer consumer : pathHandlers) {
                                            consumer.accept(pv);
                                        }
                                    }
                                    List<Consumer> pvHandlers = map.get(pv.getName());
                                    if (pvHandlers != null) {
                                        for (Consumer consumer : pvHandlers) {
                                            consumer.accept(pv);
                                        }
                                    }
                                    // Notify all parent nodes if parent changed
                                    if(parent_changed){
                                        AlarmTreeItem parent = pv.getParent();
                                        while (parent != null) {
                                            List<Consumer> parentHandlers = map.get(parent.getPathName().substring(1));
                                            if (parentHandlers != null) {
                                                for (Consumer consumer : parentHandlers) {
                                                    try {
                                                        consumer.accept(getState(parent.getPathName()));
                                                    } catch (Exception e) {
                                                        
                                                    }
                                                }
                                            }
                                            parent = parent.getParent();
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

    @Override
    protected ChannelHandler createChannel(String channelName) {
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

    protected AlarmTreeItem getState(String channelName) throws Exception {
        URI uri = URI.create(URLEncoder.encode(channelName, "UTF-8"));
        String pvName = uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1);
        if (model != null) {
            AlarmTreePV alarmTreePV = model.findPV(pvName);
            if (alarmTreePV != null) {
                return alarmTreePV;
            } else {
                String path = URLDecoder.decode(uri.getPath(), "UTF-8");
                AlarmTreeItem alarmTreeItem = model.getConfigTree().getItemByPath(path);
                return alarmTreeItem;
            }
        } else {
            throw new InitializationException("Model hasn't been created yet");
        }
    }

    protected boolean isConnected() {
        if (model != null) {
            return model.isServerAlive();
        } else {
            return false;
        }
    }

    protected boolean isWriteAllowed(){
        if (model != null) {
            return model.isWriteAllowed();
        } else {
            return false;
        }
    }
    
    protected void acknowledge(String channelName, boolean acknowledge) throws Exception{
        getState(channelName).acknowledge(acknowledge);
    }

    // implementing the enable disable mechanism using the example of the DisableComponentAction
    protected void enable(String channelName, boolean enable) throws Exception {
        AlarmTreeItem item = getState(channelName);
        List<AlarmTreePV> pvs = new ArrayList<AlarmTreePV>();
        final CompletableFuture<Void> future = CompletableFuture
                .runAsync(() -> addPVs(pvs, item, enable), executor)
                .thenRun(() -> {
                    for (AlarmTreePV alarmTreePV : pvs) {
                        try {
                            model.enable(alarmTreePV, enable);
                        } catch (Exception e) {
                            //TODO handle raising the write exception
                            e.printStackTrace();
                            new Exception("Failed to enable/disable : " + ((AlarmTreePV) item).getName(), e);
                        }
                    }
                });
    }
    
    /** @param pvs List where PVs to enable/disable will be added
     *  @param item Item for which to locate PVs, recursively
     */
    protected void addPVs(final List<AlarmTreePV> pvs, final AlarmTreeItem item, boolean enable)
    {
        if (item instanceof AlarmTreePV)
        {
            final AlarmTreePV pv = (AlarmTreePV) item;
            if (pv.isEnabled() != enable)
                pvs.add(pv);
        }
        else
        {
            final int N = item.getChildCount();
            for (int i=0; i<N; ++i)
                addPVs(pvs, item.getChild(i), enable);
        }
    }
}
