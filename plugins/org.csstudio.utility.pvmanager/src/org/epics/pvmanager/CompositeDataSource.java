/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * A data source that can dispatch a request to multiple different
 * data sources.
 *
 * @author carcassi
 */
public class CompositeDataSource extends DataSource {
    
    private static Logger log = Logger.getLogger(CompositeDataSource.class.getName());

    // Stores all data sources by name
    private Map<String, DataSource> dataSources = new ConcurrentHashMap<String, DataSource>();

    private volatile String delimiter = "://";
    private volatile String defaultDataSource;

    /**
     * Creates a new CompositeDataSource.
     */
    public CompositeDataSource() {
        super(true);
    }

    /**
     * Returns the delimeter that divides the data source name from the
     * channel name. Default is "://" so that "epics://pv1" corresponds
     * to the "pv1" channel from the "epics" datasource.
     *
     * @return data source delimeter; can't be null
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * Changes the data source delimiter.
     *
     * @param delimiter new data source delimiter; can't be null
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Adds/replaces the data source corresponding to the given name.
     *
     * @param name the name of the data source
     * @param dataSource the data source to add/replace
     */
    public void putDataSource(String name, DataSource dataSource) {
        dataSources.put(name, dataSource);
    }

    /**
     * Returns which data source is used if no data source is specified in the
     * channel name.
     *
     * @return the default data source, or null if it was never set
     */
    public String getDefaultDataSource() {
        return defaultDataSource;
    }
    
    /**
     * Returns the data sources registered to this composite data source.
     * 
     * @return the registered data sources
     */
    public Map<String, DataSource> getDataSources() {
        return Collections.unmodifiableMap(dataSources);
    }

    /**
     * Sets the data source to be used if the channel does not specify
     * one explicitely. The data source must have already been added.
     *
     * @param defaultDataSource the default data source
     */
    public void setDefaultDataSource(String defaultDataSource) {
        if (!dataSources.containsKey(defaultDataSource))
            throw new IllegalArgumentException("The data source " + defaultDataSource + " was not previously added, and therefore cannot be set as default");

        this.defaultDataSource = defaultDataSource;
    }
    
    private String nameOf(String channelName) {
        int indexDelimiter = channelName.indexOf(delimiter);
        if (indexDelimiter == -1) {
            return channelName;
        } else {
            return channelName.substring(indexDelimiter + delimiter.length());
        }
    }
    
    private String sourceOf(String channelName) {
        int indexDelimiter = channelName.indexOf(delimiter);
        if (indexDelimiter == -1) {
            if (defaultDataSource == null)
                throw new IllegalArgumentException("Channel " + channelName + " uses the default data source but one was never set.");
            return defaultDataSource;
        } else {
            String source = channelName.substring(0, indexDelimiter);
            if (dataSources.containsKey(source))
                return source;
            throw new IllegalArgumentException("Data source " + source + " for " + channelName + " was not configured.");
        }
    }
    
    private Map<String, ReadRecipe> splitRecipe(ReadRecipe readRecipe) {
        Map<String, ReadRecipe> splitRecipe = new HashMap<String, ReadRecipe>();

        // Iterate through the recipe to understand how to distribute
        // the calls
        Map<String, Collection<ChannelReadRecipe>> routingRecipes = new HashMap<String, Collection<ChannelReadRecipe>>();
        for (ChannelReadRecipe channelRecipe : readRecipe.getChannelReadRecipes()) {
            String name = nameOf(channelRecipe.getChannelName());
            String dataSource = sourceOf(channelRecipe.getChannelName());

            if (dataSource == null)
                throw new IllegalArgumentException("Channel " + name + " uses the default data source but one was never set.");

            // Add recipe for the target dataSource
            if (routingRecipes.get(dataSource) == null) {
                routingRecipes.put(dataSource, new HashSet<ChannelReadRecipe>());
            }
            routingRecipes.get(dataSource).add(new ChannelReadRecipe(name, channelRecipe.getReadSubscription()));
        }
        
        // Create the recipes
        for (Entry<String, Collection<ChannelReadRecipe>> entry : routingRecipes.entrySet()) {
            splitRecipe.put(entry.getKey(), new ReadRecipe(entry.getValue()));
        }
        
        return splitRecipe;
    }

    @Override
    public void connectRead(ReadRecipe readRecipe) {
        Map<String, ReadRecipe> splitRecipe = splitRecipe(readRecipe);

        // Dispatch calls to all the data sources
        for (Map.Entry<String, ReadRecipe> entry : splitRecipe.entrySet()) {
            try {
                DataSource dataSource = dataSources.get(entry.getKey());
                if (dataSource == null) {
                    throw new IllegalArgumentException("DataSource '" + entry.getKey() + delimiter + "' was not configured.");
                }
                dataSource.connectRead(entry.getValue());
            } catch (RuntimeException ex) {
                // If data source fail, still go and connect the others
                readRecipe.getChannelReadRecipes().iterator().next().getReadSubscription().getExceptionWriteFunction().writeValue(ex);
            }
        }
    }

    @Override
    public void disconnectRead(ReadRecipe readRecipe) {
        Map<String, ReadRecipe> splitRecipe = splitRecipe(readRecipe);

        // Dispatch calls to all the data sources
        for (Map.Entry<String, ReadRecipe> entry : splitRecipe.entrySet()) {
            try {
                dataSources.get(entry.getKey()).disconnectRead(entry.getValue());
            } catch(RuntimeException ex) {
                // If a data source fails, still go and disconnect the others
                readRecipe.getChannelReadRecipes().iterator().next().getReadSubscription().getExceptionWriteFunction().writeValue(ex);
            }
        }
    }
    
    private Map<String, WriteRecipe> splitRecipe(WriteRecipe writeRecipe) {
        // Chop the recipe along different data sources
        Map<String, Collection<ChannelWriteRecipe>> recipes = new HashMap<String, Collection<ChannelWriteRecipe>>();
        for (ChannelWriteRecipe channelWriteRecipe : writeRecipe.getChannelWriteRecipes()) {
            String channelName = nameOf(channelWriteRecipe.getChannelName());
            String dataSource = sourceOf(channelWriteRecipe.getChannelName());
            Collection<ChannelWriteRecipe> channelWriteRecipes = recipes.get(dataSource);
            if (channelWriteRecipes == null) {
                channelWriteRecipes = new ArrayList<ChannelWriteRecipe>();
                recipes.put(dataSource, channelWriteRecipes);
            }
            channelWriteRecipes.add(new ChannelWriteRecipe(channelName, channelWriteRecipe.getWriteSubscription()));
        }
        
        Map<String, WriteRecipe> splitRecipes = new HashMap<String, WriteRecipe>();
        for (Map.Entry<String, Collection<ChannelWriteRecipe>> en : recipes.entrySet()) {
            String dataSource = en.getKey();
            Collection<ChannelWriteRecipe> val = en.getValue();
            WriteRecipe newWriteRecipe = new WriteRecipe(val);
            splitRecipes.put(dataSource, newWriteRecipe);
        }
        
        return splitRecipes;
    }

    @Override
    public void connectWrite(WriteRecipe writeRecipe) {
        Map<String, WriteRecipe> splitRecipes = splitRecipe(writeRecipe);
        for (Entry<String, WriteRecipe> entry : splitRecipes.entrySet()) {
            String dataSource = entry.getKey();
            WriteRecipe splitWriteRecipe = entry.getValue();
            dataSources.get(dataSource).connectWrite(splitWriteRecipe);
        }
    }

    @Override
    public void disconnectWrite(WriteRecipe writeRecipe) {
        Map<String, WriteRecipe> splitRecipe = splitRecipe(writeRecipe);
        
        for (Map.Entry<String, WriteRecipe> en : splitRecipe.entrySet()) {
            String dataSource = en.getKey();
            WriteRecipe splitWriteRecipe = en.getValue();
            dataSources.get(dataSource).disconnectWrite(splitWriteRecipe);
        }
    }
    

    @Override
    ChannelHandler channel(String channelName) {
        String name = nameOf(channelName);
        String dataSource = sourceOf(channelName);
        return dataSources.get(dataSource).channel(name);
    }
    
    @Override
    protected ChannelHandler createChannel(String channelName) {
        throw new UnsupportedOperationException("Composite data source can't create channels directly.");
    }

    /**
     * Closes all DataSources that are registered in the composite.
     */
    @Override
    public void close() {
        for (DataSource dataSource : dataSources.values()) {
            dataSource.close();
        }
    }

    @Override
    public Map<String, ChannelHandler> getChannels() {
        Map<String, ChannelHandler> channels = new HashMap<String, ChannelHandler>();
        for (Entry<String, DataSource> entry : dataSources.entrySet()) {
            String dataSourceName = entry.getKey();
            DataSource dataSource = entry.getValue();
            for (Entry<String, ChannelHandler> channelEntry : dataSource.getChannels().entrySet()) {
                String channelName = channelEntry.getKey();
                ChannelHandler channelHandler = channelEntry.getValue();
                channels.put(dataSourceName + delimiter + channelName, channelHandler);
            }
        }
        
        return channels;
    }

}
