/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.csstudio.alarm.diirt.datasource;

public class BeastConnectionPayload {

    private final boolean connected;
    private final String type;

    public BeastConnectionPayload(boolean connected, String type) {
        this.connected = connected;
        this.type = type;
    }

    public boolean isConnected(){
        return this.connected;
    }

    public String getType() {
        return type;
    }
}
