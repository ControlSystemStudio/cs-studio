/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

/**
 * Callback for delivery notification of new value. 
 *
 * @author carcassi
 */
public interface PVWriterListener {
    
    /**
     * Notified when the value was written.
     */
    public void pvWritten();
}
