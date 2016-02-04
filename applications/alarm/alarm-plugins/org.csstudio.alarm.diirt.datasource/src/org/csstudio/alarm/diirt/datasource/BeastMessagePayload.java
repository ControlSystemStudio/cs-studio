/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.csstudio.alarm.diirt.datasource;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;


/**
 * @author Kunal Shroff
 *
 */
public class BeastMessagePayload {

    private final AlarmTreeItem pv;

    public BeastMessagePayload(AlarmTreeItem initialState) {
        this.pv = initialState;
    }

    public String getName(){
        return pv.getName();
    }

    public String getDescription(){
        return pv.getMessage();
    }

    public String getAlarmStatus() {
        return pv.getSeverity().getDisplayName();
    }

    public String getValue() {
        if(pv instanceof AlarmTreePV){
            return ((AlarmTreePV) pv).getValue();
        } else{
            return pv.getToolTipText();
        }
    }

    public boolean isActive(){
        return pv.getSeverity().isActive();
    }

    public boolean getEnable() {
        if(pv instanceof AlarmTreePV){
            return ((AlarmTreePV) pv).isEnabled();
        } else{
            //TODO return enable status for item
            return true;
        }
    }

    public String getCurrentState(){
        return pv.getCurrentSeverity().getDisplayName();
    }

    public String getType(){
        if(pv instanceof AlarmTreePV){
            return "leaf";
        }else {
            return "node";
        }
    }
}
