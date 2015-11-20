/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.csstudio.alarm.diirt.datasource;

import org.csstudio.alarm.beast.client.AlarmTreePV;


/**
 * @author Kunal Shroff
 *
 */
public class BeastMessagePayload {

    private final AlarmTreePV pv;
    private final boolean active;
    private final boolean acknowledged;
    private final boolean enable;

    public BeastMessagePayload(AlarmTreePV pv, boolean active, boolean acknowledged, boolean enable) {
        this.pv = pv;
        this.active = active;
        this.acknowledged = acknowledged;
        this.enable = enable;
    }
    
    public String getName(){
        return pv.getName();
    }
    
    public String getDescription(){
        return pv.getDescription();
    }

    public String getAlarmStatus() {
        return pv.getSeverity().getDisplayName();
    }

    public String getValue() {
        return pv.getValue();
    }
    
    public boolean isAcknowledged(){
        return this.acknowledged;
    }
    
    public boolean isActive(){
        return this.active;
    }

    public boolean getEnable() {
        return this.enable;
    }

}
