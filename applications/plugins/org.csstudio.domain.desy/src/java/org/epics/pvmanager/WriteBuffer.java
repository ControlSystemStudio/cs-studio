/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.Collections;
import java.util.Map;
import org.epics.pvmanager.WriteCache;

/**
 * Represents all the values, channel names and ordering information needed
 * for writing
 *
 * @author carcassi
 */
public class WriteBuffer {
    private final Map<String, WriteCache<?>> caches;

    WriteBuffer(Map<String, WriteCache<?>> caches) {
        this.caches = Collections.unmodifiableMap(caches);
    }
    
    /**
     * Returns the write caches used by this buffer.
     * 
     * @return the caches for each channel
     */
    public Map<String, WriteCache<?>> getWriteCaches() {
        return caches;
    }
    
}
