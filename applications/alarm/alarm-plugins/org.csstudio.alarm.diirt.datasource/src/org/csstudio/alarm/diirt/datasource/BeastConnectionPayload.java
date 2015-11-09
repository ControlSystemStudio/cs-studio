/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.csstudio.alarm.diirt.datasource;

public class BeastConnectionPayload {

    private final BeastDataSource beastDataSource;
    // private final Channel channel;
    private final String readType;
    private final String writetype;

    public BeastConnectionPayload(BeastChannelHandler channelHandle) {
        this.beastDataSource = channelHandle.getBeastDatasource();
        this.readType = channelHandle.getReadType();
        this.writetype = channelHandle.getWriteType();
    }

    public String getReadType() {
        return this.readType;
    }

    public String getWriteType() {
        return this.writetype;
    }
}
