/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.epics.pvmanager.expression.WriteExpression;
import org.epics.util.time.TimeDuration;

/**
 * Orchestrates the different elements of pvmanager to make a writer functional.
 * <p>
 * This class is responsible for the correct writer operation, including:
 * <ul>
 * <li>Setting up the collector for notifications</li>
 * <li>Setting up the collector for connection notification</li>
 * <li>Building connection recipes and forwarding them to the datasource<li>
 * <li>Managing the scanning task and notification for connection status
 * or errors</li>
 * <li>Managing the write process, both synchronous and asynchronous</li>
 * <li>Disconnecting the expressions from the datasources if the reader is closed
 * or if it's garbage collected</li>
 * </ul>
 *
 * @param <T> value type for the writer managed by this director
 * @author carcassi
 */
public class PVWriterDirector<T> {
    
    private static final Logger log = Logger.getLogger(PVWriterDirector.class.getName());
    
    private volatile boolean notificationInFlight = false;
    
    // Required for connection and exception notification
    
    /** Executor used to scan the connection/exception queues */
    private final ScheduledExecutorService scannerExecutor;
    private volatile ScheduledFuture<?> scanTaskHandle;
    private final WeakReference<PVWriterImpl<T>> pvRef;
    private final ConnectionCollector connCollector =
            new ConnectionCollector();
    private final QueueCollector<Exception> exceptionCollector;
    
    // Required to process the write
    
    private final WriteFunction<T> writeFunction;
    /** Executor to use to process the write */
    private final ScheduledExecutorService writeExecutor;
    private final TimeDuration timeout;
    private final String timeoutMessage;
    
    // Required to connect/disconnect expressions
    
    private final Map<WriteExpression<?>, WriteRecipe> recipes =
            new HashMap<>();
    
    // Required for multiple operations
    
    private final Object lock = new Object();
    /** Executor to use for notification from the scanner (connection/exceptions) or from the write (success/fail) */
    private final Executor notificationExecutor;
    /** DataSource to use for connect/disconnect expression and for write */
    private final DataSource dataSource;
    private WriteRecipe currentWriteRecipe;

    PVWriterDirector(PVWriterImpl<T> pvWriter, WriteFunction<T> writeFunction, DataSource dataSource,
            ScheduledExecutorService writeExecutor, Executor notificationExecutor,
            ScheduledExecutorService scannerExecutor, TimeDuration timeout, String timeoutMessage,
            ExceptionHandler exceptionHandler) {
        this.pvRef = new WeakReference<>(pvWriter);
        this.writeFunction = writeFunction;
        this.dataSource = dataSource;
        this.writeExecutor = writeExecutor;
        this.notificationExecutor = notificationExecutor;
        this.scannerExecutor = scannerExecutor;
        this.timeout = timeout;
        this.timeoutMessage = timeoutMessage;
        if (exceptionHandler == null) {
            exceptionCollector = new QueueCollector<>(1);
        } else {
            exceptionCollector = new LastExceptionCollector(1, exceptionHandler);
        }
    }
    
    /**
     * Simulate a static connection in which the channel name has one exception
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
     * Connects the given expression.
     * <p>
     * This can be used for dynamic expression to add and connect child expressions.
     * The added expression will be automatically closed when the associated
     * reader is closed, if it's not disconnected first.
     * 
     * @param expression the expression to connect
     */
    public void connectExpression(WriteExpression<?> expression) {
        WriteRecipeBuilder builder = new WriteRecipeBuilder();
        expression.fillWriteRecipe(this, builder);
        WriteRecipe recipe = builder.build(exceptionCollector, connCollector);
        synchronized(lock) {
            recipes.put(expression, recipe);
        }
        if (!recipe.getChannelWriteRecipes().isEmpty()) {
            try {
                dataSource.connectWrite(recipe);
            } catch(Exception ex) {
                exceptionCollector.writeValue(ex);
            }
            updateWriteRecipe();
        }
    }
    
    /**
     * Disconnects the given expression.
     * <p>
     * This can be used for dynamic expression, to remove and disconnects child
     * expressions.
     *
     * @param expression the expression to disconnect
     */
    public void disconnectExpression(WriteExpression<?> expression) {
        WriteRecipe recipe;
        synchronized(lock) {
            recipe = recipes.remove(expression);
        }
        if (recipe == null) {
            log.log(Level.SEVERE, "Director was asked to disconnect expression '" + expression + "' which was not found.");
        }
        
        if (!recipe.getChannelWriteRecipes().isEmpty()) {
            try {
                dataSource.disconnectWrite(recipe);
            } catch(Exception ex) {
                exceptionCollector.writeValue(ex);
            }
            updateWriteRecipe();
        }
    }
    
    private void updateWriteRecipe() {
        synchronized(lock) {
            Set<ChannelWriteRecipe> channelRecipe = new HashSet<>();
            for (WriteRecipe writeRecipe : recipes.values()) {
                channelRecipe.addAll(writeRecipe.getChannelWriteRecipes());
            }
            currentWriteRecipe = new WriteRecipe(channelRecipe);
        }
    }
    
    /**
     *
     */
    public void close() {
        synchronized(lock) {
            while (!recipes.isEmpty()) {
                WriteExpression<?> expression = recipes.keySet().iterator().next();
                disconnectExpression(expression);
            }
        }
    }
    
    void write(final T newValue, final PVWriterImpl<T> pvWriter) {
        WriteTask newTask = new WriteTask(pvWriter, newValue);
        writeExecutor.execute(newTask);
        if (timeout != null) {
            writeExecutor.schedule(newTask.timeout(), timeout.toNanosLong(), TimeUnit.NANOSECONDS);
        }
    }
    
    private class WriteTask implements Runnable {
        final PVWriterImpl<T> pvWriter;
        final T newValue;
        private AtomicBoolean done = new AtomicBoolean();

        public WriteTask(PVWriterImpl<T> pvWriter, T newValue) {
            this.pvWriter = pvWriter;
            this.newValue = newValue;
        }
        
        private Runnable timeout() {
            return new Runnable() {

                @Override
                public void run() {
                    if (!done.get()) {
                        exceptionCollector.writeValue(new TimeoutException(timeoutMessage));
                    }
                }
            };
        }

        @Override
        public void run() {
            synchronized(lock) {
                writeFunction.writeValue(newValue);
                dataSource.write(currentWriteRecipe, new Runnable() {

                    @Override
                    public void run() {
                        done.set(true);
                        notificationExecutor.execute(new Runnable() {

                            @Override
                            public void run() {
                                pvWriter.fireWriteSuccess();
                            }
                        });
                    }
                }, new ExceptionHandler() {

                    @Override
                    public void handleException(final Exception ex) {
                        boolean previousDone = done.getAndSet(true);
                        if (!previousDone) {
                            notificationExecutor.execute(new Runnable() {

                                @Override
                                public void run() {
                                    pvWriter.fireWriteFailure(ex);
                                }
                            });
                        } else {
                            pvWriter.setLastWriteException(ex);
                        }
                    }
                    
                });
            }
        }
    
    };
    
    void syncWrite(final T newValue, final PVWriterImpl<T> pvWriter) {
        log.finest("Sync write: creating latch");
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Exception> exception = new AtomicReference<Exception>();
        writeExecutor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    synchronized(lock) {
                        writeFunction.writeValue(newValue);
                        dataSource.write(currentWriteRecipe, new Runnable() {

                            @Override
                            public void run() {
                                log.finest("Writing done, releasing latch");
                                notificationExecutor.execute(new Runnable() {

                                    @Override
                                    public void run() {
                                        pvWriter.fireWriteSuccess();
                                        latch.countDown();
                                    }
                                });
                            }
                        }, new ExceptionHandler() {

                            @Override
                            public void handleException(final Exception ex) {
                                exception.set(ex);
                                notificationExecutor.execute(new Runnable() {

                                    @Override
                                    public void run() {
                                        pvWriter.fireWriteFailure(ex);
                                        latch.countDown();
                                    }
                                });
                            }

                        });
                    }
                } catch (RuntimeException ex) {
                    exception.set(ex);
                    latch.countDown();
                    exceptionCollector.writeValue(ex);
                }
            }
        });
        log.finest("Write request submitted. Waiting.");
        
        boolean done;
        try {
            if (timeout != null) {
                done = latch.await(timeout.toNanosLong(), TimeUnit.NANOSECONDS);
            } else {
                latch.await();
                done = true;
            }
        } catch(InterruptedException ex) {
            throw new RuntimeException("Interrupted", ex);
        }
        if (exception.get() != null) {
            throw new RuntimeException("Write failed", exception.get());
        }
        if (done == false) {
            TimeoutException ex = new TimeoutException(timeoutMessage);
            exceptionCollector.writeValue(ex);
            throw ex;
        }
        log.finest("Waiting done. No exceptions.");
    }
    
    /**
     * Determines whether the notifier is active or not.
     * <p>
     * The notifier becomes inactive if the PVWriter is closed or is garbage collected.
     * The first time this function determines that the notifier is inactive,
     * it will ask the data source to close all channels relative to the
     * pv.
     *
     * @return true if new notification should be performed
     */
    boolean isActive() {
        // Making sure to get the reference once for thread safety
        final PVWriter<T> pv = pvRef.get();
        if (pv != null && !pv.isClosed()) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Notifies the PVReader of a new value.
     */
    void notifyPv() {
        // Don't even calculate if notification is in flight.
        // This makes pvManager automatically throttle back if the consumer
        // is slower than the producer.
        if (notificationInFlight)
            return;
        
        // Calculate new connection
        final boolean connected = connCollector.readValue();
        List<Exception> exceptions = exceptionCollector.readValue();
        final Exception lastException;
        if (exceptions.isEmpty()) {
            lastException = null;
        } else {
            lastException = exceptions.get(exceptions.size() - 1);
        }

        // If the connection flag is the same, don't notify
        final PVWriterImpl<T> pv = pvRef.get();
        if (pv == null || (pv.isWriteConnected() == connected && lastException == null)) {
            return;
        }

        
        notificationInFlight = true;
        notificationExecutor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    pv.setLastWriteException(lastException);
                    pv.setWriteConnected(connected);
                    pv.firePvWritten();
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
                    notifyPv();
                } else {
                    stopScan();
                    close();
                }
            }
        }, 0, duration.toNanosLong(), TimeUnit.NANOSECONDS);
    }
    
    void stopScan() {
        if (scanTaskHandle != null) {
            scanTaskHandle.cancel(false);
            scanTaskHandle = null;
        } else {
            throw new IllegalStateException("Scan was never started");
        }
    }

    WriteRecipe getCurrentWriteRecipe() {
        return currentWriteRecipe;
    }
    
}
