/*
 * Copyright 2008-2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.Map;

/**
 * Represents all the values, channel names and ordering information needed
 * for writing
 *
 * @author carcassi
 */
public class WriteBuffer {
    private final Map<String, WriteCache<?>> caches;

    public WriteBuffer(Map<String, WriteCache<?>> caches) {
        this.caches = caches;
    }
    
    public Map<String, WriteCache<?>> getWriteCaches() {
        return caches;
    }
    
}
