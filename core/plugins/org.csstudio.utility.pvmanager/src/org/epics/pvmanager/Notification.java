/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

/**
 * @author carcassi
 * @since 19.01.2011
 */
public class Notification<T> {
    private boolean notificationNeeded;
    private T newValue;

    public Notification(boolean notificationNeeded, T newValue) {
        this.notificationNeeded = notificationNeeded;
        this.newValue = newValue;
    }

    public boolean isNotificationNeeded() {
        return notificationNeeded;
    }

    public T getNewValue() {
        return newValue;
    }
}
