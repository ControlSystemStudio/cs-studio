/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.epics.pvmanager.util.TimeDuration;

/**
 * Object responsible to notify the PVReader of changes on the appropriate thread.
 *
 * @author carcassi
 */
class Notifier<T> {

    private final WeakReference<PVReaderImpl<T>> pvRef;
    private final Function<T> function;
    private final Executor notificationExecutor;
    private final ScheduledExecutorService scannerExecutor;
    private volatile PVRecipe pvRecipe;
    private volatile ScheduledFuture<?> scanTaskHandle;
    private final ExceptionHandler exceptionHandler;

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
    Notifier(PVReaderImpl<T> pv, Function<T> function, ScheduledExecutorService scannerExecutor, Executor notificationExecutor, ExceptionHandler exceptionHandler) {
        this.pvRef = new WeakReference<PVReaderImpl<T>>(pv);
        this.function = function;
        this.notificationExecutor = notificationExecutor;
        this.scannerExecutor = scannerExecutor;
        this.exceptionHandler = exceptionHandler;
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
        } else {
            if (pvRecipe != null) {
                pvRecipe.getDataSource().disconnect(pvRecipe.getDataSourceRecipe());
                pvRecipe = null;
            }
            return false;
        }
    }
    
    /**
     * Notifies the PVReader of a new value.
     */
    void notifyPv() {
        try {
            // The data will be shipped as part of the task,
            // which is properly synchronized by the executor
            final T newValue = function.getValue();

            notificationExecutor.execute(new Runnable() {

                @Override
                public void run() {
                    PVReaderImpl<T> pv = pvRef.get();
                    if (pv != null && newValue != null) {
                        Notification<T> notification =
                                NotificationSupport.notification(pv.getValue(), newValue);
                        if (notification.isNotificationNeeded()) {
                            pv.setValue(notification.getNewValue());
                        }
                    }
                }
            });
        } catch(RuntimeException ex) {
            exceptionHandler.handleException(ex);
        }
    }

    void setPvRecipe(PVRecipe pvRecipe) {
        this.pvRecipe = pvRecipe;
    }

    PVRecipe getPvRecipe() {
        return pvRecipe;
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
        }, 0, duration.getNanoSec(), TimeUnit.NANOSECONDS);
    }
    
    void timeout(TimeDuration timeout, final String timeoutMessage) {
        scannerExecutor.schedule(new Runnable() {

            @Override
            public void run() {
                PVReaderImpl<T> pv = pvRef.get();
                if (pv != null && pv.getValue() == null) {
                    exceptionHandler.handleException(new TimeoutException(timeoutMessage));
                }
            }
        }, timeout.getNanoSec(), TimeUnit.NANOSECONDS);
    }
    
    void stopScan() {
        scanTaskHandle.cancel(false);
    }

}
