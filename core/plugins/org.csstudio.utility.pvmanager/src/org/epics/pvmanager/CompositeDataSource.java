/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.Map.Entry;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
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

    // Need to remember how the recipes where split, so that they can be
    // re-sent for disconnection
    private Map<DataRecipe, Map<String, DataRecipe>> splitRecipes =
            new ConcurrentHashMap<DataRecipe, Map<String, DataRecipe>>();
    private Map<WriteBuffer, Map<String, WriteBuffer>> writeBuffers =
            new ConcurrentHashMap<WriteBuffer, Map<String, WriteBuffer>>();
    
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

    @Override
    public void connect(DataRecipe recipe) {
        Map<String, DataRecipe> splitRecipe = new HashMap<String, DataRecipe>();

        // Iterate through the recipe to understand how to distribute
        // the calls
        Map<String, Collection<ChannelRecipe>> routingRecipes = new HashMap<String, Collection<ChannelRecipe>>();
        for (ChannelRecipe channelRecipe : recipe.getChannelRecipes()) {
            String name = nameOf(channelRecipe.getChannelName());
            String dataSource = sourceOf(channelRecipe.getChannelName());

            if (dataSource == null)
                throw new IllegalArgumentException("Channel " + name + " uses the default data source but one was never set.");

            // Add recipe for the target dataSource
            if (routingRecipes.get(dataSource) == null)
                routingRecipes.put(dataSource, new HashSet<ChannelRecipe>());
            routingRecipes.get(dataSource).add(new ChannelRecipe(name, channelRecipe.getReadSubscription()));
        }
        
        // Create the recipes
        for (Entry<String, Collection<ChannelRecipe>> entry : routingRecipes.entrySet()) {
            splitRecipe.put(entry.getKey(), new DataRecipe(entry.getValue()));
        }

        splitRecipes.put(recipe, splitRecipe);

        // Dispatch calls to all the data sources
        for (Map.Entry<String, DataRecipe> entry : splitRecipe.entrySet()) {
            try {
                DataSource dataSource = dataSources.get(entry.getKey());
                if (dataSource == null)
                    throw new IllegalArgumentException("DataSource '" + entry.getKey() + "://' was not configured.");
                dataSource.connect(entry.getValue());
            } catch (RuntimeException ex) {
                // If data source fail, still go and connect the others
                recipe.getExceptionHandler().handleException(ex);
            }
        }
    }

    @Override
    public void disconnect(DataRecipe recipe) {
        Map<String, DataRecipe> splitRecipe = splitRecipes.get(recipe);
        if (splitRecipe == null) {
            log.log(Level.WARNING, "DataRecipe {0} was disconnected but was never connected. Ignoring it.", recipe);
            return;
        }

        // Dispatch calls to all the data sources
        for (Map.Entry<String, DataRecipe> entry : splitRecipe.entrySet()) {
            try {
                dataSources.get(entry.getKey()).disconnect(entry.getValue());
            } catch(RuntimeException ex) {
                // If a data source fails, still go and disconnect the others
                recipe.getExceptionHandler().handleException(ex);
            }
        }

        splitRecipes.remove(recipe);
    }

    @Override
    public void prepareWrite(WriteBuffer writeBuffer, ExceptionHandler exceptionHandler) {
        // Chop the buffer along different data sources
        Map<String, Map<String, WriteCache<?>>> buffers = new HashMap<String, Map<String, WriteCache<?>>>();
        for (Map.Entry<String, WriteCache<?>> en : writeBuffer.getWriteCaches().entrySet()) {
            String channelName = nameOf(en.getKey());
            String dataSource = sourceOf(en.getKey());
            WriteCache<?> cache = en.getValue();
            Map<String, WriteCache<?>> buffer = buffers.get(dataSource);
            if (buffer == null) {
                buffer = new HashMap<String, WriteCache<?>>();
                buffers.put(dataSource, buffer);
            }
            buffer.put(channelName, cache);
        }
        
        Map<String, WriteBuffer> splitBuffers = new HashMap<String, WriteBuffer>();
        for (Map.Entry<String, Map<String, WriteCache<?>>> en : buffers.entrySet()) {
            String dataSource = en.getKey();
            Map<String, WriteCache<?>> val = en.getValue();
            WriteBuffer newWriteBuffer = new WriteBufferBuilder().addCaches(val).build();
            splitBuffers.put(dataSource, newWriteBuffer);
            dataSources.get(dataSource).prepareWrite(newWriteBuffer, exceptionHandler);
        }
        
        writeBuffers.put(writeBuffer, splitBuffers);
    }

    @Override
    public void concludeWrite(WriteBuffer writeBuffer, ExceptionHandler exceptionHandler) {
        Map<String, WriteBuffer> splitBuffer = writeBuffers.remove(writeBuffer);
        if (splitBuffer == null) {
            log.log(Level.WARNING, "WriteBuffer {0} was unregistered but was never registered. Ignoring it.", writeBuffer);
            return;
        }
        
        for (Map.Entry<String, WriteBuffer> en : splitBuffer.entrySet()) {
            String dataSource = en.getKey();
            WriteBuffer splitWriteBuffer = en.getValue();
            dataSources.get(dataSource).concludeWrite(splitWriteBuffer, exceptionHandler);
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
