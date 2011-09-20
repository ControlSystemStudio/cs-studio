/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.epics.pvmanager.util.Executors.*;

/**
 * A source for data that is going to be processed by the PVManager.
 * PVManager can work with more than one source at a time. Support
 * for each different source can be added by external libraries.
 *
 * @author carcassi
 */
public abstract class DataSource {

    private static final Logger log = Logger.getLogger(DataSource.class.getName());

    private final boolean writeable;

    /**
     * Returns true whether the channels of this data source can be
     * written to.
     * 
     * @return true if data source accept write operations
     */
    public boolean isWriteable() {
        return writeable;
    }
    
    /**
     * Creates a new data source.
     * 
     * @param writeable whether the data source implements write operations
     */
    public DataSource(boolean writeable) {
        this.writeable = writeable;
    }

    // Keeps track of the currently created channels
    private Map<String, ChannelHandler<?>> usedChannels = new ConcurrentHashMap<String, ChannelHandler<?>>();

    /**
     * Returns a channel from the given name, either cached or it
     * will create it.
     * 
     * @param channelName name of a channel
     * @return a new or cached handler
     */
    ChannelHandler<?> channel(String channelName) {
        ChannelHandler<?> channel = usedChannels.get(channelName);
        if (channel == null) {
            channel = createChannel(channelName);
            if (channel == null)
                return null;
            usedChannels.put(channelName, channel);
        }
        return channel;
    }

    /**
     * Creates a channel handler for the given name. In the simplest
     * case, this is the only method a data source needs to implement.
     * 
     * @param channelName the name for a new channel
     * @return a new handler
     */
    protected abstract ChannelHandler<?> createChannel(String channelName);

    // The executor used by the data source to perform asynchronous operations,
    // such as connections and writes. I am current using a single thread for
    // all data sources, which can be changed if needed.
    private static Executor exec = Executors.newSingleThreadExecutor(namedPool("PVMgr DataSource Worker "));
    
    // Keeps track of the recipes and buffers that were opened with
    // this data source.
    private Set<DataRecipe> recipes = new CopyOnWriteArraySet<DataRecipe>();
    private Set<WriteBuffer> registeredBuffers = new CopyOnWriteArraySet<WriteBuffer>();

    /**
     * Connects to a set of channels based on the given recipe.
     * <p>
     * The data source must update the value caches relative to each channel.
     * Before updating any cache, it must lock the collector relative to that
     * cache and after any update it must notify the collector.
     *
     * @param recipe the instructions for the data connection
     */
    public void connect(final DataRecipe recipe) {
        for (Map.Entry<Collector<?>, Map<String, ValueCache>> collEntry : recipe.getChannelsPerCollectors().entrySet()) {
            final Collector<?> collector = collEntry.getKey();
            for (Map.Entry<String, ValueCache> entry : collEntry.getValue().entrySet()) {
                String channelName = entry.getKey();
                final ChannelHandler<?> channelHandler = channel(channelName);
                if (channelHandler == null)
                    throw new ReadFailException();
                final ValueCache cache = entry.getValue();

                // Add monitor on other thread in case it triggers notifications
                exec.execute(new Runnable() {

                    @Override
                    public void run() {
                        channelHandler.addMonitor(collector, cache, recipe.getExceptionHandler());
                    }
                });
            }
        }
        recipes.add(recipe);
    }

    /**
     * Disconnects the set of channels given by the recipe.
     * <p>
     * The disconnect call is guaranteed to be given the same object,
     * so that the recipe itself can be used as a key in a map to retrieve
     * the list of resources needed to be closed.
     *
     * @param recipe the instructions for the data connection
     */
    public void disconnect(DataRecipe recipe) {
        if (!recipes.contains(recipe)) {
            log.log(Level.WARNING, "DataRecipe {0} was disconnected but was never connected. Ignoring it.", recipe);
            return;
        }

        for (Map.Entry<Collector<?>, Map<String, ValueCache>> collEntry : recipe.getChannelsPerCollectors().entrySet()) {
            Collector<?> collector = collEntry.getKey();
            for (Map.Entry<String, ValueCache> entry : collEntry.getValue().entrySet()) {
                String channelName = entry.getKey();
                ChannelHandler<?> channelHandler = usedChannels.get(channelName);
                if (channelHandler == null) {
                    log.log(Level.WARNING, "Channel {0} should have been connected, but is not found during disconnection. Ignoring it.", channelName);
                }
                channelHandler.removeMonitor(collector);
            }
        }

        recipes.remove(recipe);
    }
    
    /**
     * Prepares the channels defined in the write buffer for writes.
     * <p>
     * If these are channels over the network, it will create the 
     * network connections with the underlying libraries.
     * 
     * @param writeBuffer the buffer that will contain the write data
     * @param exceptionHandler where to report the exceptions
     */
    public void prepareWrite(final WriteBuffer writeBuffer, final ExceptionHandler exceptionHandler) {
        if (!isWriteable())
            throw new WriteFailException("Data source is read only");
        
        final Set<ChannelHandler> handlers = new HashSet<ChannelHandler>();
        for (String channelName : writeBuffer.getWriteCaches().keySet()) {
            ChannelHandler handler = channel(channelName);
            if (handler == null)
                throw new WriteFailException("Channel " + channelName + " does not exist");
            handlers.add(handler);
            }

        // Connect using another thread
        exec.execute(new Runnable() {

            @Override
            public void run() {
                for (ChannelHandler channelHandler : handlers) {
                    channelHandler.addWriter(exceptionHandler);
                }
            }
        });
        registeredBuffers.add(writeBuffer);
    }
    
    /**
     * Releases the resources associated with the given write buffer.
     * <p>
     * Will close network channels and deallocate memory needed.
     * 
     * @param writeBuffer the buffer that will no longer be used
     * @param exceptionHandler where to report the exceptions
     */
    public void concludeWrite(final WriteBuffer writeBuffer, final ExceptionHandler exceptionHandler) {
        if (!isWriteable())
            throw new WriteFailException("Data source is read only");
        
        if (!registeredBuffers.contains(writeBuffer)) {
            log.log(Level.WARNING, "WriteBuffer {0} was unregistered but was never registered. Ignoring it.", writeBuffer);
            return;
        }

        registeredBuffers.remove(writeBuffer);
        final Set<ChannelHandler> handlers = new HashSet<ChannelHandler>();
        for (String channelName : writeBuffer.getWriteCaches().keySet()) {
            handlers.add(channel(channelName));
        }

        // Connect using another thread
        exec.execute(new Runnable() {

            @Override
            public void run() {
                for (ChannelHandler channelHandler : handlers) {
                    channelHandler.removeWrite(exceptionHandler);
                }
            }
        });
    }
    
    /**
     * Writes the contents in the given write buffers to the channels
     * of this data sources.
     * <p>
     * The write buffer need to be first prepared with {@link #prepareWrite(org.epics.pvmanager.WriteBuffer, org.epics.pvmanager.ExceptionHandler) }
     * and then cleaned up with {@link #concludeWrite(org.epics.pvmanager.WriteBuffer, org.epics.pvmanager.ExceptionHandler) }.
     * 
     * @param writeBuffer the buffer containing the data to write
     * @param callback function to call when the write is concluded
     * @param exceptionHandler where to report the exceptions
     */
    public void write(final WriteBuffer writeBuffer, final Runnable callback, final ExceptionHandler exceptionHandler) {
        if (!isWriteable())
            throw new UnsupportedOperationException("This data source is read only");
        
        final WritePlanner planner = new WritePlanner();
        for (Map.Entry<String, WriteCache<?>> entry : writeBuffer.getWriteCaches().entrySet()) {
            ChannelHandler<?> channel = channel(entry.getKey());
            planner.addChannel(channel, entry.getValue().getValue(), entry.getValue().getPrecedingChannels());
        }

        // Connect using another thread
        exec.execute(new Runnable() {

            private void scheduleNext() {
                for (Map.Entry<ChannelHandler, Object> entry : planner.nextChannels().entrySet()) {
                    final String channelName = entry.getKey().getChannelName();
                    entry.getKey().write(entry.getValue(), new ChannelWriteCallback() {

                        AtomicInteger counter = new AtomicInteger();

                        @Override
                        public void channelWritten(Exception ex) {
                            planner.removeChannel(channelName);
                            // Notify only when the last channel was written
                            if (planner.isDone()) {
                                callback.run();
                            } else {
                                scheduleNext();
                            }
                        }
                    });
                }
            }

            @Override
            public void run() {
                scheduleNext();
            }
        });
    }

    /**
     * Returns the channel handlers for this data source.
     * 
     * @return an unmodifiable collection
     */
    public Map<String, ChannelHandler<?>> getChannels() {
        return Collections.unmodifiableMap(usedChannels);
    }

    /**
     * Closes the DataSource and the resources associated with it.
     */
    public void close() {
        
    }
    
}
