/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

/**
 *
 * @author carcassi
 */
public class PVReaderEvent<T> {
    
    public static int CONNECTION_MASK = 0b000001;
    public static int EXCEPTION_MASK  = 0b000010;
    public static int VALUE_MASK      = 0b000100;
    
    private final int notificationMask;
    private final PVReader<T> pvReader;

    PVReaderEvent(int notificationMask, PVReader<T> pvReader) {
        this.notificationMask = notificationMask;
        this.pvReader = pvReader;
    }

    public PVReader<T> getPvReader() {
        return pvReader;
    }

    public int getNotificationMask() {
        return notificationMask;
    }
    
    public boolean isConnectionChanged() {
        return (notificationMask & CONNECTION_MASK) != 0;
    }
    
    public boolean isValueChanged() {
        return (notificationMask & VALUE_MASK) != 0;
    }
    
    public boolean isExceptionChangesd() {
        return (notificationMask & EXCEPTION_MASK) != 0;
    }
    
    
}
