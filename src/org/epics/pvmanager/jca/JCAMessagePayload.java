/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR;
import gov.aps.jca.event.MonitorEvent;

/**
 *
 * @author carcassi
 */
public class JCAMessagePayload {
    private final DBR metadata;
    private final MonitorEvent event;

    JCAMessagePayload(DBR metadata, MonitorEvent event) {
        this.metadata = metadata;
        this.event = event;
    }

    public MonitorEvent getEvent() {
        return event;
    }

    public DBR getMetadata() {
        return metadata;
    }
    
}
