/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager;

import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Object responsible to notify the PV of changes on the appropriate thread.
 *
 * @author carcassi
 */
class Notifier<T> {

    private final WeakReference<PV<T>> pvRef;
    private final Function<T> function;
    private final ThreadSwitch onThread;
    private volatile PVRecipe pvRecipe;
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
     * @param onThread the thread switching mechanism
     */
    Notifier(PV<T> pv, Function<T> function, ThreadSwitch onThread, ExceptionHandler exceptionHandler) {
        this.pvRef = new WeakReference<PV<T>>(pv);
        this.function = function;
        this.onThread = onThread;
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Determines whether the notifier is active or not.
     * <p>
     * The notifier becomes inactive if the PV is closed or is garbage collected.
     * The first time this function determines that the notifier is inactive,
     * it will ask the data source to close all channels relative to the
     * pv.
     *
     * @return true if new notification should be performed
     */
    boolean isActive() {
        // Making sure to get the reference once for thread safety
        final PV<T> pv = pvRef.get();
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

    /*
     * Concurrent queue to safely publish objects.
     * The timer thread will put objects in the queue, while the notification
     * thread will take them.
     * Given that the queue does not accept null, publish nullValue object
     * instead of null.
     */
    private Queue<Object> publishingQueue = new ConcurrentLinkedQueue<Object>();
    private static final Object nullValue = new Object();

    private void push(T element) {
        if (element != null) {
            publishingQueue.add(element);
        } else {
            publishingQueue.add(nullValue);
        }
    }

    private T pop() {
        Object element = publishingQueue.poll();
        if (element == nullValue)
            return null;
        else {
            @SuppressWarnings("unchecked")
            T popped = (T) element;
            return popped;
        }
    }

    /**
     * Notifies the PV of a new value.
     */
    void notifyPv() {
        try {
            // Using concurrent queue to safely publish object
            T newValue = function.getValue();
            push(newValue);

            onThread.post(new Runnable() {

                @Override
                public void run() {
                    T safeValue = pop();
                    PV<T> pv = pvRef.get();
                    if (pv != null && safeValue != null) {
                        Notification<T> notification =
                                NotificationSupport.notification(pv.getValue(), safeValue);
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

}
