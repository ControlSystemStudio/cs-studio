/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.csstudio.alarm.diirt.datasource;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;

public class BeastConnectionPayload {

    private final AlarmTreeItem initial;
    private final boolean connected;

    public BeastConnectionPayload(AlarmTreeItem initialState, boolean connected) {
        this.initial = initialState;
        this.connected = connected;
    }

    public boolean isConnected(){
        return this.connected;
    }
}
