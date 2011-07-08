/*
 * Copyright 2008-2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

/**
 *
 * @author carcassi
 */
public interface ChannelWriteCallback {
    public void channelWritten(Exception ex);
}
