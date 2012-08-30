/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.test;

import org.epics.pvmanager.BasicTypeSupport;
import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.DataSource;

/**
 * Data source for testing, error conditions in particular. Each instance of this
 * data source will have its own separate channels and values.
 *
 * @author carcassi
 */
public final class TestDataSource extends DataSource {

    static {
        // Install type support for the types it generates.
        BasicTypeSupport.install();
    }

    /**
     * Creates a new data source.
     */
    public TestDataSource() {
        super(true);
    }

    @Override
    protected ChannelHandler createChannel(String channelName) {
        if ("delayedWrite".equals(channelName)) {
            return new DelayedWriteChannel(channelName);
        }
        if ("delayedConnection".equals(channelName)) {
            return new DelayedConnectionChannel(channelName);
        }
        if ("delayedConnectionError".equals(channelName)) {
            return new DelayedConnectionErrorChannel(channelName);
        }
        return null;
    }

}
