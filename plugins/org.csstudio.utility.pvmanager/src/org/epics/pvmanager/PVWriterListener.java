/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

/**
 * Callback for delivery notification of new value. 
 *
 * @param <T> the type of writer for the listener
 * @author carcassi
 */
public interface PVWriterListener<T> {
    
    /**
     * Notified when the value was written.
     * 
     * @param event the writer event
     */
    public void pvChanged(PVWriterEvent<T> event);
}
