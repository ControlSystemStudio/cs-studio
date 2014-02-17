/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.util.time.TimeDuration;

/**
 * Orchestrates the different elements of pvmanager to make a reader functional.
 * <p>
 * This class is responsible for the correct read operation, including:
 * <ul>
 * <li>Setting up the collector for notifications</li>
 * <li>Setting up the collector for connection notification</li>
 * <li>Building connection recipes and forwarding them to the datasource<li>
 * <li>Managing the scanning task and notification for new values, connection status
 * or errors</li>
 * <li>Disconnecting the expressions from the datasources if the reader is closed
 * or if it's garbage collected</li>
 * </ul>
 *
 * @param <T> value type for the reader managed by this director
 * @author carcassi
 */
public class PVReaderDirector<T> {
    
    private static final Logger log = Logger.getLogger(PVReaderDirector.class.getName());

    // Required for connection and exception notification

    /** Executor used to notify of new values/connection/exception */
    private final Executor notificationExecutor;
    /** Executor used to scan the connection/exception queues */
    private final ScheduledExecutorService scannerExecutor;
    private volatile ScheduledFuture<?> scanTaskHandle;
    /** PVReader to update during the notification */
    private final WeakReference<PVReaderImpl<T>> pvRef;
    /** Function for the new value */
    private final ReadFunction<T> function;
    
    // Required to connect/disconnect expressions
    private final DataSource dataSource;
    private final Object lock = new Object();
    private final Map<DesiredRateExpression<?>, ReadRecipe> recipes =
            new HashMap<>();

    // Required for multiple operations
    /** Connection collector required to connect/disconnect expressions and for connection notification */
    private final ConnectionCollector connCollector =
            new ConnectionCollector();
    /** Exception queue to be used to connect/disconnect expression and for exception notification */
    private final QueueCollector<Exception> exceptionCollector;
    
    
    ReadRecipe getCurrentReadRecipe() {
        ReadRecipeBuilder builder = new ReadRecipeBuilder();
        for (Map.Entry<DesiredRateExpression<?>, ReadRecipe> entry : recipes.entrySet()) {
            ReadRecipe readRecipe = entry.getValue();
            for (ChannelReadRecipe channelReadRecipe : readRecipe.getChannelReadRecipes()) {
                builder.addChannel(channelReadRecipe.getChannelName(), channelReadRecipe.getReadSubscription().getValueCache());
            }
        }
        return builder.build(exceptionCollector, connCollector);
    }
    
    /**
     * Connects the given expression.
     * <p>
     * This can be used for dynamic expression to add and connect child expressions.
     * The added expression will be automatically closed when the associated
     * reader is closed, if it's not disconnected first.
     * 
     * @param expression the expression to connect
     */
    public void connectExpression(DesiredRateExpression<?> expression) {
        ReadRecipeBuilder builder = new ReadRecipeBuilder();
        expression.fillReadRecipe(this, builder);
        ReadRecipe recipe = builder.build(exceptionCollector, connCollector);
        synchronized(lock) {
            recipes.put(expression, recipe);
        }
        if (!recipe.getChannelReadRecipes().isEmpty()) {
            try {
                dataSource.connectRead(recipe);
            } catch(Exception ex) {
                recipe.getChannelReadRecipes().iterator().next().getReadSubscription().getExceptionWriteFunction().writeValue(ex);
            }
        }
    }
    
    /**
     * Simulate a static connection in which the channel has one exception
     * and the connection will never change.
     * <p>
     * This is a temporary method an will be subject to change in the future.
     * The aim is to allow to connect expressions that are not channels
     * but can influence exception and connection state. For example,
     * to report problems encountered during expression creation as runtime
     * problems through the normal exception/connection methods.
     * <p>
     * In the future, this should be generalized to allow fully fledged expressions
     * that connect/disconnect and can report errors.
     * 
     * @param ex the exception to queue
     * @param connection the connection flag
     * @param channelName the channel name
     */
    public void connectStatic(Exception ex, boolean connection, String channelName) {
        exceptionCollector.writeValue(ex);
        connCollector.addChannel(channelName).writeValue(connection);
    }
    
    /**
     * Disconnects the given expression.
     * <p>
     * This can be used for dynamic expression, to remove and disconnects child
     * expressions.
     *
     * @param expression the expression to disconnect
     */
    public void disconnectExpression(DesiredRateExpression<?> expression) {
        ReadRecipe recipe;
        synchronized(lock) {
            recipe = recipes.remove(expression);
        }
        if (recipe == null) {
            log.log(Level.SEVERE, "Director was asked to disconnect expression '" + expression + "' which was not found.");
        }
        
        if (!recipe.getChannelReadRecipes().isEmpty()) {
            try {
                dataSource.disconnectRead(recipe);
            } catch(Exception ex) {
                recipe.getChannelReadRecipes().iterator().next().getReadSubscription().getExceptionWriteFunction().writeValue(ex);
            }
        }
    }
    
    private volatile boolean closed = false;
    
    void close() {
        closed = true;
    }

    /**
     * Closed and disconnects all the child expressions.
     */
    private void disconnect() {
        synchronized(lock) {
            while (!recipes.isEmpty()) {
                DesiredRateExpression<?> expression = recipes.keySet().iterator().next();
                disconnectExpression(expression);
            }
        }
    }

    /**
     * Creates a new notifier. The new notifier will notifier the given pv
     * with new values calculated by the function, and will use onThread to
     * perform the notifications.
     * <p>
     * After construction, one MUST set the pvRecipe, so that the
     * dataSource is appropriately closed.
     *
     * @param pv the pv on which to notify
     * @param function the function used to calculate new values
     * @param notificationExecutor the thread switching mechanism
     */
    PVReaderDirector(PVReaderImpl<T> pv, ReadFunction<T> function, ScheduledExecutorService scannerExecutor,
            Executor notificationExecutor, DataSource dataSource, ExceptionHandler exceptionHandler) {
        this.pvRef = new WeakReference<>(pv);
        this.function = function;
        this.notificationExecutor = notificationExecutor;
        this.scannerExecutor = scannerExecutor;
        this.dataSource = dataSource;
        if (exceptionHandler == null) {
            exceptionCollector = new QueueCollector<>(1);
        } else {
            exceptionCollector = new LastExceptionCollector(1, exceptionHandler);
        }
    }

    /**
     * Determines whether the notifier is active or not.
     * <p>
     * The notifier becomes inactive if the PVReader is closed or is garbage collected.
     * The first time this function determines that the notifier is inactive,
     * it will ask the data source to close all channels relative to the
     * pv.
     *
     * @return true if new notification should be performed
     */
    boolean isActive() {
        // Making sure to get the reference once for thread safety
        final PVReader<T> pv = pvRef.get();
        if (pv != null && !pv.isClosed()) {
            return true;
        } else if (pv == null && closed != true) {
            log.warning("PVReader wasn't properly closed and it was garbage collected. Closing the associated connections...");
            return false;
        } else {
            return false;
        }
    }
    
    /**
     * Checks whether the pv is paused
     * 
     * @return true if paused
     */
    boolean isPaused() {
        final PVReader<T> pv = pvRef.get();
        if (pv == null || pv.isPaused()) {
            return true;
        } else {
            return false;
        }
    }
    
    private volatile boolean notificationInFlight = false;
    
    /**
     * Notifies the PVReader of a new value.
     */
    void notifyPv() {
        // Don't even calculate if notification is in flight.
        // This makes pvManager automatically throttle back if the consumer
        // is slower than the producer.
        if (notificationInFlight)
            return;
        
        // Calculate new value
        T newValue = null;
        boolean calculationSucceeded = false;
        try {
            // Tries to calculate the value
            newValue = function.readValue();
            if (newValue != null) {
                NotificationSupport.findNotificationSupportFor(newValue);
            }
            calculationSucceeded = true;
        } catch(RuntimeException ex) {
            // Calculation failed
            exceptionCollector.writeValue(ex);
        }
        
        // Calculate new connection
        final boolean connected = connCollector.readValue();
        List<Exception> exceptions = exceptionCollector.readValue();
        final Exception lastException;
        if (exceptions.isEmpty()) {
            lastException = null;
        } else {
            lastException = exceptions.get(exceptions.size() - 1);
        }
        
        // TODO: if payload is immutable, the difference test should be done here
        // and not in the runnable (to save SWT time)
        
        // Prepare values to ship to the other thread.
        // The data will be shipped as part of the task,
        // which is properly synchronized by the executor
        final T finalValue = newValue;
        final boolean finalCalculationSucceeded = calculationSucceeded;
        notificationInFlight = true;
        notificationExecutor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    PVReaderImpl<T> pv = pvRef.get();
                    // Proceed with notification only if PVReader was not garbage
                    // collected
                    if (pv != null) {
                        
                        // Atomicity guaranteed by:
                        //  - all the modification on the PVReader
                        //    are done here, on the same thread where the listeners will be called.
                        //    This means the callbacks are guaranteed to run after all
                        //    changes are done
                        //  - notificationInFlight guarantees that no other notification
                        //    will run while one notification is running. This means
                        //    the next event is serialized after the end of this one.
                        pv.setConnected(connected);
                        if (lastException != null) {
                            pv.setLastException(lastException);
                        }
                        
                        // XXX Are we sure that we should skip notifications if values are null?
                        if (finalCalculationSucceeded && finalValue != null) {
                            Notification<T> notification =
                                    NotificationSupport.notification(pv.getValue(), finalValue);
                            // Remember to notify anyway if an exception need to be notified
                            if (notification.isNotificationNeeded()) {
                                pv.setValue(notification.getNewValue());
                            } else if (pv.isLastExceptionToNotify() || pv.isReadConnectionToNotify()) {
                                pv.firePvValueChanged();
                            }
                        } else {
                            // Remember to notify anyway if an exception need to be notified
                            if (pv.isLastExceptionToNotify() || pv.isReadConnectionToNotify()) {
                                pv.firePvValueChanged();
                            }
                        }
                    }
                } finally {
                    notificationInFlight = false;
                }
            }
        });
    }
    
    void startScan(TimeDuration duration) {
        scanTaskHandle = scannerExecutor.scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                if (isActive()) {
                    // If paused, simply skip without stopping the scan
                    if (!isPaused()) {
                        notifyPv();
                    }
                } else {
                    stopScan();
                    disconnect();
                }
            }
        }, 0, duration.toNanosLong(), TimeUnit.NANOSECONDS);
    }
    
    void timeout(TimeDuration timeout, final String timeoutMessage) {
        scannerExecutor.schedule(new Runnable() {

            @Override
            public void run() {
                PVReaderImpl<T> pv = pvRef.get();
                if (pv != null && !pv.isSentFirsEvent()) {
                    exceptionCollector.writeValue(new TimeoutException(timeoutMessage));
                }
            }
        }, timeout.toNanosLong(), TimeUnit.NANOSECONDS);
    }
    
    void stopScan() {
        if (scanTaskHandle != null) {
            scanTaskHandle.cancel(false);
            scanTaskHandle = null;
        } else {
            throw new IllegalStateException("Scan was never started");
        }
    }

}
