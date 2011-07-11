/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.loc;

import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.data.DataTypeSupport;

/**
 * Data source for locally written data. Each instance of this
 * data source will have its own separate channels and values.
 *
 * @author carcassi
 */
public final class LocalDataSource extends DataSource {

    static {
        // Install type support for the types it generates.
        DataTypeSupport.install();
    }

    /**
     * Creates a new data source.
     */
    public LocalDataSource() {
        super(true);
    }

    @Override
    protected ChannelHandler<?> createChannel(String channelName) {
        return new LocalChannelHandler(channelName);
    }

}
