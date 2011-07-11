/*
 * Copyright 2008-2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

/**
 * Callback for delivery notification of new value. 
 *
 * @author carcassi
 */
public interface PVValueWriteListener {
    
    /**
     * Notified when the value was written.
     */
    public void pvValueWritten();
}
