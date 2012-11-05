/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.epics.util.time.TimeDuration;

/**
 * Object responsible to notify the PVWriter of changes on the appropriate thread.
 * <p>
 * TODO: This may be an overkill of since the only thing needed to be notified is
 * the connection. This may be the perfect opportunity to start working on a
 * "throttled synchronous notification", in which there is no continuous task,
 * the first notification goes right through but the second may be throttled back
 * so that no more than N notifications per second are sent. Or maybe just
 * throttle back if the client is busy.
 *
 * @author carcassi
 */
class WriteNotifier<T> {

    private final WeakReference<PVWriterImpl<T>> pvRef;
    private final Function<Boolean> connFunction;
    private final Executor notificationExecutor;
    private final ScheduledExecutorService scannerExecutor;
    private volatile ScheduledFuture<?> scanTaskHandle;
    private final ExceptionHandler exceptionHandler;

    /**
     * Creates a new notifier. The new notifier will notifier the given pv writer
     * with the connection flag calculated by the function and will
     * use notificationExecutor to perform the notifications.
     *
     * @param pv the pv on which to notify
     * @param function the function used to calculate new values
     * @param notificationExecutor the thread switching mechanism
     */
    WriteNotifier(PVWriterImpl<T> pv, Function<Boolean> connFunction, ScheduledExecutorService scannerExecutor, Executor notificationExecutor, ExceptionHandler exceptionHandler) {
        this.pvRef = new WeakReference<PVWriterImpl<T>>(pv);
        this.connFunction = connFunction;
        this.notificationExecutor = notificationExecutor;
        this.scannerExecutor = scannerExecutor;
        this.exceptionHandler = exceptionHandler;
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
        
        // Calculate new connection
        final boolean connected = connFunction.getValue();

        // If the connection flag is the same, don't notify
        final PVWriterImpl<T> pv = pvRef.get();
        if (pv == null || pv.isWriteConnected() == connected) {
            return;
        }

        
        notificationInFlight = true;
        notificationExecutor.execute(new Runnable() {

            @Override
            public void run() {
                try {
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

}
