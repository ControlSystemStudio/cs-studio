/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
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
 * <p>
 * To implement a datasource, one has to implement the {@link #createChannel(java.lang.String) }
 * method, and the requested will be forwarded to the channel accordingly.
 * The channels are automatically cached and reused. The name under which
 * the channels are looked up in the cache or registered in the cache is configurable.
 * <p>
 * Channel handlers can be implemented from scratch, or one can use the {@link MultiplexedChannelHandler}
 * for handlers that want to open a single connection which is going to be
 * shared by all readers and writers.
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
        ChannelHandler channel = usedChannels.get(channelHandlerLookupName(channelName));
        if (channel == null) {
            channel = createChannel(channelName);
            if (channel == null)
                return null;
            usedChannels.put(channelHandlerRegisterName(channelName, channel), channel);
        }
        return channel;
    }
    
    /**
     * Returns the lookup name to use to find the channel handler in
     * the cache. By default, it returns the channel name itself.
     * If a datasource needs multiple different channel names to
     * be the same channel handler (e.g. parts of the channel name
     * are initialization parameters) then it can override this method
     * to change the lookup.
     * 
     * @param channelName the channel name
     * @return the channel handler to look up in the cache
     */
    protected String channelHandlerLookupName(String channelName) {
        return channelName;
    }
    
    /**
     * Returns the name the given handler should be registered as.
     * By default, it returns the lookup name, so that lookup and
     * registration in the cache are consistent. If a datasource
     * needs multiple different channel names to be the same 
     * channel handler (e.g. parts of the channel name are read/write
     * parameters) then it can override this method to change the
     * registration.
     * 
     * @param channelName the name under which the ChannelHandler was created
     * @param handler the handler to register
     * @return the name under which to register in the cache
     */
    protected String channelHandlerRegisterName(String channelName, ChannelHandler handler) {
        return channelHandlerLookupName(channelName);
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
    
    // Keeps track of the recipes that were opened with
    // this data source.
    private Set<ChannelReadRecipe> readRecipes = new CopyOnWriteArraySet<ChannelReadRecipe>();
    private Set<ChannelWriteRecipe> writeRecipes = new CopyOnWriteArraySet<ChannelWriteRecipe>();

    /**
     * Connects to a set of channels based on the given recipe.
     * <p>
     * The data source must update the value caches relative to each channel.
     * Before updating any cache, it must lock the collector relative to that
     * cache and after any update it must notify the collector.
     *
     * @param readRecipe the instructions for the data connection
     */
    public void connectRead(final ReadRecipe readRecipe) {
        // Add the recipe first, so that if a problem comes out
        // while processing the request, we still keep
        // track of it.
        readRecipes.addAll(readRecipe.getChannelReadRecipes());

        // Let's go through all the recipes first, so if something
        // breaks unexpectadely, either everything works or nothing works
        final Map<ChannelHandler, Collection<ChannelReadRecipe>> handlersWithSubscriptions =
                new HashMap<>();
        for (final ChannelReadRecipe channelRecipe : readRecipe.getChannelReadRecipes()) {
            try {
                String channelName = channelRecipe.getChannelName();
                ChannelHandler channelHandler = channel(channelName);
                if (channelHandler == null) {
                    throw new RuntimeException("Channel named '" + channelName + "' not found");
                }
                Collection<ChannelReadRecipe> channelSubscriptions = handlersWithSubscriptions.get(channelHandler);
                if (channelSubscriptions == null) {
                    channelSubscriptions = new HashSet<>();
                    handlersWithSubscriptions.put(channelHandler, channelSubscriptions);
                }
                channelSubscriptions.add(channelRecipe);
            } catch (Exception ex) {
                // If any error happens while creating the channel,
                // report it to the exception handler of that channel
                channelRecipe.getReadSubscription().getExceptionWriteFunction().writeValue(ex);
            }
            
        }
        
        // Now that we went through all channels,
        // add a monitor to the ones that were found
        exec.execute(new Runnable() {

            @Override
            public void run() {
                for (Map.Entry<ChannelHandler, Collection<ChannelReadRecipe>> entry : handlersWithSubscriptions.entrySet()) {
                    ChannelHandler channelHandler = entry.getKey();
                    Collection<ChannelReadRecipe> channelRecipes = entry.getValue();
                    for (ChannelReadRecipe channelRecipe : channelRecipes) {
                        try {
                            channelHandler.addReader(channelRecipe.getReadSubscription());
                        } catch(Exception ex) {
                            // If an error happens while adding the read subscription,
                            // notify the appropriate handler
                            channelRecipe.getReadSubscription().getExceptionWriteFunction().writeValue(ex);
                        }
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
     * @param readRecipe the instructions for the data connection
     */
    public void disconnectRead(ReadRecipe readRecipe) {
        for (ChannelReadRecipe channelRecipe : readRecipe.getChannelReadRecipes()) {
            if (!readRecipes.contains(channelRecipe)) {
                log.log(Level.WARNING, "ChannelReadRecipe {0} was disconnected but was never connected. Ignoring it.", channelRecipe);
            } else {
                String channelName = channelRecipe.getChannelName();
                ChannelHandler channelHandler = channel(channelName);
                // If the channel is not found, it means it was not found during
                // connection and a proper notification was sent then. Silently
                // ignore it.
                if (channelHandler != null) {
                    channelHandler.removeReader(channelRecipe.getReadSubscription());
                }
                readRecipes.remove(channelRecipe);
            }
        }
    }
    
    /**
     * Prepares the channels defined in the write recipe for writes.
     * <p>
     * If these are channels over the network, it will create the 
     * network connections with the underlying libraries.
     * 
     * @param writeRecipe the recipe that will contain the write data
     */
    public void connectWrite(final WriteRecipe writeRecipe) {
        if (!isWriteable()) {
            throw new RuntimeException("Data source is read only");
        }
        
        // Register right away, so that if a failure happen
        // we still keep track of it
        writeRecipes.addAll(writeRecipe.getChannelWriteRecipes());
        
        // Let's go through the whole request first, so if something
        // breaks unexpectadely, either everything works or nothing works
        final Map<ChannelHandler, Collection<ChannelHandlerWriteSubscription>> handlers = new HashMap<>();
        for (ChannelWriteRecipe channelWriteRecipe : writeRecipe.getChannelWriteRecipes()) {
            try {
                String channelName = channelWriteRecipe.getChannelName();
                ChannelHandler handler = channel(channelName);
                if (handler == null) {
                    throw new RuntimeException("Channel " + channelName + " does not exist");
                }
                Collection<ChannelHandlerWriteSubscription> channelSubscriptions = handlers.get(handler);
                if (channelSubscriptions == null) {
                    channelSubscriptions = new HashSet<>();
                    handlers.put(handler, channelSubscriptions);
                }
                channelSubscriptions.add(channelWriteRecipe.getWriteSubscription());
            } catch (Exception ex) {
                channelWriteRecipe.getWriteSubscription().getExceptionWriteFunction().writeValue(ex);
            }
        }

        // Connect using another thread
        exec.execute(new Runnable() {

            @Override
            public void run() {
                for (Map.Entry<ChannelHandler, Collection<ChannelHandlerWriteSubscription>> entry : handlers.entrySet()) {
                    ChannelHandler channelHandler = entry.getKey();
                    Collection<ChannelHandlerWriteSubscription> subscriptions = entry.getValue();
                    for (ChannelHandlerWriteSubscription subscription : subscriptions) {
                        try {
                            channelHandler.addWriter(subscription);
                        } catch (Exception ex) {
                            // If an error happens while adding the write subscription,
                            // notify the appropriate handler
                            subscription.getExceptionWriteFunction().writeValue(ex);
                        }
                    }
                }
            }
        });
    }
    
    /**
     * Releases the resources associated with the given write recipe.
     * <p>
     * Will close network channels and deallocate memory needed.
     * 
     * @param writeRecipe the recipe that will no longer be used
     */
    public void disconnectWrite(final WriteRecipe writeRecipe) {
        if (!isWriteable()) {
            throw new RuntimeException("Data source is read only");
        }
        
        final Map<ChannelHandler, ChannelHandlerWriteSubscription> handlers = new HashMap<ChannelHandler, ChannelHandlerWriteSubscription>();
        for (ChannelWriteRecipe channelWriteRecipe : writeRecipe.getChannelWriteRecipes()) {
            if (!writeRecipes.contains(channelWriteRecipe)) {
                log.log(Level.WARNING, "ChannelWriteRecipe {0} was unregistered but was never registered. Ignoring it.", channelWriteRecipe);
            } else {
                try {
                    String channelName = channelWriteRecipe.getChannelName();
                    ChannelHandler handler = channel(channelName);
                    // If the channel does not exist, simply skip it: it must have
                    // not be there while preparing the write, so an appropriate
                    // notification has already been sent
                    if (handler != null) {
                        handlers.put(handler, channelWriteRecipe.getWriteSubscription());
                    }
                } catch (Exception ex) {
                    // No point in sending the exception through the exception handler:
                    // nothing will be listening by now. Just log the exception
                    log.log(Level.WARNING, "Error while preparing channel '" + channelWriteRecipe.getChannelName() + "' for closing.", ex);
                }
                writeRecipes.remove(channelWriteRecipe);
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
     * Writes the contents in the given write recipe to the channels
     * of this data sources.
     * <p>
     * The write recipe needs to be first prepared with {@link #connectWrite(org.epics.pvmanager.WriteRecipe) }
     * and then cleaned up with {@link #disconnectWrite(org.epics.pvmanager.WriteRecipe)  }.
     * 
     * @param writeRecipe the recipe containing the data to write
     * @param callback function to call when the write is concluded
     * @param exceptionHandler where to report the exceptions
     */
    public void write(final WriteRecipe writeRecipe, final Runnable callback, final ExceptionHandler exceptionHandler) {
        if (!isWriteable())
            throw new UnsupportedOperationException("This data source is read only");
        
        final WritePlanner planner = new WritePlanner();
        for (ChannelWriteRecipe channelWriteRecipe : writeRecipe.getChannelWriteRecipes()) {
            ChannelHandler channel = channel(channelWriteRecipe.getChannelName());
            planner.addChannel(channel, channelWriteRecipe.getWriteSubscription().getWriteCache().getValue(),
                    channelWriteRecipe.getWriteSubscription().getWriteCache().getPrecedingChannels());
        }

        // Connect using another thread
        exec.execute(new Runnable() {

            private void scheduleNext() {
                for (Map.Entry<ChannelHandler, Object> entry : planner.nextChannels().entrySet()) {
                    final String channelName = entry.getKey().getChannelName();
                    try {
                        entry.getKey().write(entry.getValue(), new ChannelWriteCallback() {

                            AtomicInteger counter = new AtomicInteger();

                            @Override
                            public void channelWritten(Exception ex) {
                                planner.removeChannel(channelName);

                                // If there was an error, notify the exception
                                // and don't schedule anything else
                                if (ex != null) {
                                    exceptionHandler.handleException(ex);
                                    return;
                                }

                                // Notify only when the last channel was written
                                if (planner.isDone()) {
                                    callback.run();
                                } else {
                                    scheduleNext();
                                }
                            }
                        });
                    } catch (RuntimeException ex) {
                        exceptionHandler.handleException(ex);
                    }
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
