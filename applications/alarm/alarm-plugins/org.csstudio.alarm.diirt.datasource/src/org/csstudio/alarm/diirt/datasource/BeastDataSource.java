/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.csstudio.alarm.diirt.datasource;

import static org.csstudio.alarm.diirt.datasource.BeastTypeSupport.getChannelType;
import static org.csstudio.alarm.diirt.datasource.BeastTypeSupport.getStrippedChannelName;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
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


    // The model, activeAlarms and acknowledgedAlarms is shared by the entire
    // datasource, the benefit of does this at the datasource level instead of
    // in each channel is that they need to be computed only once and only a single
    // copy needs to be maintained.
    private AlarmClientModel model;

    private Map<String, List<Consumer>> map = Collections.synchronizedMap(new HashMap<String, List<Consumer>>());

    private Executor executor = Executors.newScheduledThreadPool(4);

    private BeastTypeSupport typeSupport;

    static {
        // Install type support for the types it generates.
        DataTypeSupport.install();
    }

    public BeastDataSource(BeastDataSourceConfiguration configuration) {
        super(true);

        try {

            // Create an instance to the AlarmClientModel
            final CompletableFuture<Void> future = CompletableFuture
                    .supplyAsync(() -> initialize(configuration), executor)
                    .thenAccept((model) -> {
                        this.model = model;
                        this.model.addListener(new AlarmClientModelListener() {

                            @Override
                            public void newAlarmConfiguration(AlarmClientModel model) {
                                log.config("beast  datasource: new alarm configuration --- " + model);
                                for (String channelName : map.keySet()) {
                                    BeastChannelHandler channel = (BeastChannelHandler) getChannels()
                                            .get(channelHandlerLookupName(channelName));

                                    channel.reconnect();
                                }
                            }

                            @Override
                            public void serverTimeout(AlarmClientModel model) {
                                log.warning("beast datasource: server timeout (server alive: " + model.isServerAlive() + ")");
                                for (String channelName : map.keySet()) {
                                    BeastChannelHandler channel = (BeastChannelHandler) getChannels()
                                            .get(channelHandlerLookupName(channelName));
                                    // notify the ChannelHandler that we lost connection
                                    // (causes ConnectionChanged event + Listeners' PVReader.isConnected() will return the correct state)
                                    channel.connectionStateChanged(false);
                                }
                            }

                            @Override
                            public void serverModeUpdate(AlarmClientModel model, boolean maintenance_mode) {
                                log.fine("beast  datasource: server mode update");
                            }

                            @SuppressWarnings({ "rawtypes", "unchecked" })
                            @Override
                            public void newAlarmState(AlarmClientModel alarmModel, AlarmTreePV pv, boolean parent_changed) {
                                if (pv != null) {
                                    log.fine(pv.getPathName());
                                    map.forEach((key, pathHandlers) -> {
                                        if(getStrippedChannelName(key).equals(pv.getPathName().substring(1)) || getStrippedChannelName(key).equals(pv.getName())){
                                            if (pathHandlers != null) {
                                                for (Consumer consumer : pathHandlers) {
                                                    consumer.accept(pv);
                                                }
                                            }
                                        }
                                    });
                                    // Notify parent nodes (regardless of parent_changed - because the parents' AlarmPVsCount)
                                    AlarmTreeItem parent = pv.getParent();
                                    while (parent != null) {
                                        String parentPath = parent.getPathName();
                                        map.forEach((key, pathHandlers) -> {
                                            if(getStrippedChannelName(key).equals(parentPath.substring(1))) {
                                                if (pathHandlers != null) {
                                                    for (Consumer consumer : pathHandlers) {
                                                        try {
                                                            consumer.accept(getState(parentPath));
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                        parent = parent.getParent();
                                    }
                                } else {
                                    // The AlarmClientModel has recovered from a disconnection or is notifying us that the first
                                    // messages have been received after initial connection.
                                    for (String channelName : map.keySet()) {
                                        BeastChannelHandler channel = (BeastChannelHandler) getChannels()
                                                .get(channelHandlerLookupName(channelName));
                                        if(channel!=null)
                                            channel.reconnect(); // will send connection state + current AlarmTreeItem state
                                    }
                                }
                            }
                        });
                    });
            typeSupport = new BeastTypeSupport();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AlarmClientModel initialize(BeastDataSourceConfiguration configuration) {
        AlarmClientModel alarmModel;
        try {
            if (configuration.getConfigName() != null && !configuration.getConfigName().isEmpty()) {
                alarmModel = AlarmClientModel.getInstance(configuration.getConfigName());
            } else {
                alarmModel = AlarmClientModel.getInstance();
            }
            return alarmModel;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected ChannelHandler createChannel(String channelName) {
        return new BeastChannelHandler(channelName, getChannelType(channelName), this);
    }

    @Override
    public void close() {
        super.close();
        model.release();
    }

    /**
     * Override of default channelHandlerLookupName.
     * This implementation makes a leading and trailing forward slash ("/") optional.
     * All four of these will resolve to the same channel:
     * "/demo/test/", "/demo/test", "demo/test/" & "demo/test".
     *
     * @see org.diirt.datasource.DataSource#channelHandlerLookupName(java.lang.String)
     */
    @Override
    protected String channelHandlerLookupName(String channelName) {
        String channel = channelName;
        if (channel != null && !channel.equals("/") && !channel.isEmpty()) {
            if (channel.endsWith("/"))
                channel = channel.substring(0, channel.length() - 1);
            if (channel.startsWith("/"))
                channel = channel.substring(1);
        }

        return channel;
    }

    @SuppressWarnings("rawtypes")
    protected void add(String channelName, Consumer beastChannelHandler) {
        String beastChannel = channelHandlerLookupName(channelName);
        synchronized (map) {
            List<Consumer> list = map.get(beastChannel);
            if (list == null) {
                list = new ArrayList<Consumer>();
                map.put(beastChannel, list);
            }
            list.add(beastChannelHandler);
        }
    }

    @SuppressWarnings("rawtypes")
    protected void remove(String channelName, Consumer beastChannelHandler) {
        String beastChannel = channelHandlerLookupName(channelName);
        synchronized (map) {
            if (map.containsKey(beastChannel)) {
                map.get(beastChannel).remove(beastChannelHandler);
            }
        }
    }

    protected AlarmTreeItem getState(String channelName) throws Exception {
        URI uri = URI.create(URLEncoder.encode(getStrippedChannelName(channelName), "UTF-8"));
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

    protected boolean isWriteAllowed() {
        if (model != null) {
            return model.isServerAlive() && model.isWriteAllowed();
        } else {
            return false;
        }
    }

    protected void acknowledge(String channelName, boolean acknowledge) throws Exception {
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
                            new Exception("Failed to enable/disable : " + ((AlarmTreePV) item).getName(), e);
                        }
                    }
                });
    }

    /** @param pvs List where PVs to enable/disable will be added
     *  @param item Item for which to locate PVs, recursively
     */
    protected void addPVs(final List<AlarmTreePV> pvs, final AlarmTreeItem item, boolean enable) {
        if (item instanceof AlarmTreePV) {
            final AlarmTreePV pv = (AlarmTreePV) item;
            if (pv.isEnabled() != enable)
                pvs.add(pv);
        } else {
            final int N = item.getChildCount();
            for (int i=0; i<N; ++i)
                addPVs(pvs, item.getChild(i), enable);
        }
    }

    public BeastTypeSupport getTypeSupport() {
        return typeSupport;
    }
}
