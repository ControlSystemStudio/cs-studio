/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.csstudio.alarm.diirt.datasource;

public class BeastConnectionPayload {

    private final boolean connected;

    public BeastConnectionPayload(boolean connected) {
        this.connected = connected;
    }

    public boolean isConnected(){
        return this.connected;
    }
}
