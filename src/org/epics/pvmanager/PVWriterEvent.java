/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

/**
 *
 * @author carcassi
 */
public class PVWriterEvent<T> {
    
    public static int CONNECTION_MASK      = 0b000001;
    public static int EXCEPTION_MASK       = 0b000010;
    public static int WRITE_SUCCEEDED_MASK = 0b000100;
    public static int WRITE_FAILED_MASK    = 0b001000;
    
    private final int notificationMask;
    private final PVWriter<T> pvWriter;

    PVWriterEvent(int notificationMask, PVWriter<T> pvWriter) {
        this.notificationMask = notificationMask;
        this.pvWriter = pvWriter;
    }

    public PVWriter<T> getPvWriter() {
        return pvWriter;
    }

    public int getNotificationMask() {
        return notificationMask;
    }
    
    public boolean isConnectionChanged() {
        return (notificationMask & CONNECTION_MASK) != 0;
    }
    
    public boolean isWriteSucceeded() {
        return (notificationMask & WRITE_SUCCEEDED_MASK) != 0;
    }
    
    public boolean isWriteFailed() {
        return (notificationMask & WRITE_FAILED_MASK) != 0;
    }
    
    public boolean isExceptionChangesd() {
        return (notificationMask & EXCEPTION_MASK) != 0;
    }
    
    
}
