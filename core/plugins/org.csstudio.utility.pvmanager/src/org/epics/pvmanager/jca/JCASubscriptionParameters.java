/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBRType;

/**
 *
 * @author carcassi
 */
public class JCASubscriptionParameters {
    
    private final DBRType epicsValueType;
    private final DBRType epicsMetaType;
    private final int count;

    public JCASubscriptionParameters(DBRType epicsValueType, DBRType epicsMetaType, int count) {
        this.epicsValueType = epicsValueType;
        this.epicsMetaType = epicsMetaType;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public DBRType getEpicsMetaType() {
        return epicsMetaType;
    }

    public DBRType getEpicsValueType() {
        return epicsValueType;
    }
    
}
