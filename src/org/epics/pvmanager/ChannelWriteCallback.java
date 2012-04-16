/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

/**
 * Called by the ChannelHandler once a write is completed.
 *
 * @author carcassi
 */
public interface ChannelWriteCallback {
    
    /**
     * Called when a write is completed. If completed without error,
     * the argument is null.
     * 
     * @param ex the exception if the write failed, null otherwise
     */
    public void channelWritten(Exception ex);
}
