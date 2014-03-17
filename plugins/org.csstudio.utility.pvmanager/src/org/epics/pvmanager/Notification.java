/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

/**
 * Used by {@link NotificationSupport} to communicate whether a new notification
 * is needed, and what should be the type to be notified.
 * 
 * @param <T> the type of the new value
 * @author carcassi
 */
public class Notification<T> {
    private boolean notificationNeeded;
    private T newValue;

    /**
     * Creates a new notification.
     * 
     * @param notificationNeeded true if a notification is needed
     * @param newValue the new value to be sent
     */
    public Notification(boolean notificationNeeded, T newValue) {
        this.notificationNeeded = notificationNeeded;
        this.newValue = newValue;
    }

    /**
     * True if the reader needs to be notified.
     * 
     * @return true if notification should be sent
     */
    public boolean isNotificationNeeded() {
        return notificationNeeded;
    }

    /**
     * The value to be sent if the notification is needed.
     * 
     * @return the new value to be sent
     */
    public T getNewValue() {
        return newValue;
    }
}
