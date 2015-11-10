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
    private final String filter;

    public BeastConnectionPayload(BeastChannelHandler channelHandle) {
        this.beastDataSource = channelHandle.getBeastDatasource();
        this.readType = channelHandle.getReadType();
        this.writetype = channelHandle.getWriteType();
        this.filter = channelHandle.getSelector();
    }

    public String getReadType() {
        return this.readType;
    }

    public String getWriteType() {
        return this.writetype;
    }
    
    public String getFilter() {
        return this.filter;
    }
}
