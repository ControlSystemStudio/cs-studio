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
 * Represent the payload produced at each monitor event, consisting of
 * both the metadata and the event data.
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

    /**
     * The event returned by the monitor.
     * 
     * @return the monitor event
     */
    public MonitorEvent getEvent() {
        return event;
    }

    /**
     * The data taken with a GET at connection time.
     * 
     * @return the dbr type for the metadata
     */
    public DBR getMetadata() {
        return metadata;
    }
    
}
