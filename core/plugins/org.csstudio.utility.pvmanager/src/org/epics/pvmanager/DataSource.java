/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.*;
import java.util.concurrent.*;
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
    private Map<String, ChannelHandler> usedChannels = new ConcurrentHashMap<String, ChannelHandler>();

    /**
     * Returns a channel from the given name, either cached or it
     * will create it.
     * 
     * @param channelName name of a channel
     * @return a new or cached handler
     */
    ChannelHandler channel(String channelName) {
        String channelHandlerName = channelHandlerFor(channelName);
        ChannelHandler channel = usedChannels.get(channelHandlerName);
        if (channel == null) {
            channel = createChannel(channelHandlerName);
            if (channel == null)
                return null;
            usedChannels.put(channelHandlerName, channel);
        }
        return channel;
    }
    
    /**
     * Returns the channel handler name to be used for the given channel.
     * By default, it returns the channel name itself. If a datasource
     * needs to multiple different channels to the same channel handler
     * (e.g. parts of the channel name are parameters for the read/write)
     * then it can override this method to do the appropriate mapping.
     * 
     * @param channelName
     * @return the channel handler name
     */
    String channelHandlerFor(String channelName) {
        return channelName;
    }

    /**
     * Creates a channel handler for the given name. In the simplest
     * case, this is the only method a data source needs to implement.
     * 
     * @param channelName the name for a new channel
     * @return a new handler
     */
    protected abstract ChannelHandler createChannel(String channelName);

    // The executor used by the data source to perform asynchronous operations,
    // such as connections and writes. I am current using a single thread for
    // all data sources, which can be changed if needed.
    // Since it's a single executor for all data sources, it should not be
    // shut down at data source close.
    private static ExecutorService exec = Executors.newSingleThreadExecutor(namedPool("PVMgr DataSource Worker "));
    
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
        // Add the recipe first, so that if a problem comes out
        // while processing the request, we still keep
        // track of it.
        recipes.add(recipe);

        // Let's go through all the recipes first, so if something
        // breaks unexpectadely, either everything works or nothing works
        final Map<ChannelHandler, ChannelRecipe> handlersWithSubscriptions =
                new HashMap<ChannelHandler, ChannelRecipe>();
        for (final ChannelRecipe channelRecipe : recipe.getChannelRecipes()) {
            try {
                String channelName = channelRecipe.getChannelName();
                ChannelHandler channelHandler = channel(channelName);
                if (channelHandler == null) {
                    throw new ReadFailException("Channel named '" + channelName + "' not found");
                }
                handlersWithSubscriptions.put(channelHandler, channelRecipe);
            } catch (Exception ex) {
                // If any error happens while creating the channel,
                // report it to the exception handler of that channel
                channelRecipe.getReadSubscription().getHandler().handleException(ex);
            }
            
        }
        
        // Now that we went through all channels,
        // add a monitor to the ones that were found
        exec.execute(new Runnable() {

            @Override
            public void run() {
                for (Map.Entry<ChannelHandler, ChannelRecipe> entry : handlersWithSubscriptions.entrySet()) {
                    ChannelHandler channelHandler = entry.getKey();
                    ChannelRecipe channelRecipe = entry.getValue();
                    try {
                        channelHandler.addMonitor(channelRecipe.getReadSubscription());
                    } catch(Exception ex) {
                        // If an error happens while adding the read subscription,
                        // notify the appropriate handler
                        channelRecipe.getReadSubscription().getHandler().handleException(ex);
                    }
                }
            }
        });
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

        
        for (ChannelRecipe channelRecipe : recipe.getChannelRecipes()) {
            Collector<?> collector = channelRecipe.getReadSubscription().getCollector();
            String channelName = channelRecipe.getChannelName();
            ChannelHandler channelHandler = usedChannels.get(channelName);
            // If the channel is not found, it means it was not found during
            // connection and a proper notification was sent then. Silently
            // ignore it.
            if (channelHandler != null) {
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
        
        // Register right away, so that if a failure happen
        // we still keep track of it
        registeredBuffers.add(writeBuffer);
        
        // Let's go through the whole request first, so if something
        // breaks unexpectadely, either everything works or nothing works
        final Map<ChannelHandler, ChannelHandlerWriteSubscription> handlers = new HashMap<ChannelHandler, ChannelHandlerWriteSubscription>();
        for (ChannelWriteBuffer channelWriteBuffer : writeBuffer.getChannelWriteBuffers()) {
            try {
                String channelName = channelWriteBuffer.getChannelName();
                ChannelHandler handler = channel(channelName);
                if (handler == null)
                    throw new WriteFailException("Channel " + channelName + " does not exist");
                handlers.put(handler, channelWriteBuffer.getWriteSubscription());
            } catch (Exception ex) {
                channelWriteBuffer.getWriteSubscription().getHandler().handleException(ex);
            }
        }

        // Connect using another thread
        exec.execute(new Runnable() {

            @Override
            public void run() {
                for (Map.Entry<ChannelHandler, ChannelHandlerWriteSubscription> entry : handlers.entrySet()) {
                    try {
                        ChannelHandler channelHandler = entry.getKey();
                        ChannelHandlerWriteSubscription subscription = entry.getValue();
                        channelHandler.addWriter(subscription);
                    } catch (Exception ex) {
                        // If an error happens while adding the write subscription,
                        // notify the appropriate handler
                        entry.getValue().getHandler().handleException(ex);
                    }
                }
            }
        });
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
        final Map<ChannelHandler, ChannelHandlerWriteSubscription> handlers = new HashMap<ChannelHandler, ChannelHandlerWriteSubscription>();
        for (ChannelWriteBuffer channelWriteBuffer : writeBuffer.getChannelWriteBuffers()) {
            try {
                String channelName = channelWriteBuffer.getChannelName();
                ChannelHandler handler = channel(channelName);
                // If the channel does not exist, simply skip it: it must have
                // not be there while preparing the write, so an appropriate
                // notification has already been sent
                if (handler != null) {
                    handlers.put(handler, channelWriteBuffer.getWriteSubscription());
                }
            } catch (Exception ex) {
                // No point in sending the exception through the exception handler:
                // nothing will be listening by now. Just log the exception
                log.log(Level.WARNING, "Error while preparing channel '" + channelWriteBuffer.getChannelName() + "' for closing.", ex);
            }
        }

        // Connect using another thread
        exec.execute(new Runnable() {

            @Override
            public void run() {
                for (Map.Entry<ChannelHandler, ChannelHandlerWriteSubscription> entry : handlers.entrySet()) {
                    ChannelHandler channelHandler = entry.getKey();
                    ChannelHandlerWriteSubscription channelHandlerWriteSubscription = entry.getValue();
                    channelHandler.removeWrite(channelHandlerWriteSubscription);
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
        for (ChannelWriteBuffer channelWriteBuffer : writeBuffer.getChannelWriteBuffers()) {
            ChannelHandler channel = channel(channelWriteBuffer.getChannelName());
            planner.addChannel(channel, channelWriteBuffer.getWriteSubscription().getCache().getValue(),
                    channelWriteBuffer.getWriteSubscription().getCache().getPrecedingChannels());
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
    public Map<String, ChannelHandler> getChannels() {
        return Collections.unmodifiableMap(usedChannels);
    }

    /**
     * Closes the DataSource and the resources associated with it.
     */
    public void close() {
    }
    
}
