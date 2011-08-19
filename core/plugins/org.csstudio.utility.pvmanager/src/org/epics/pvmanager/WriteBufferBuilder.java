/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager;

import java.util.HashMap;
import java.util.Map;
import org.epics.pvmanager.WriteCache;

/**
 *
 * @author carcassi
 */
public class WriteBufferBuilder {

    private final Map<String, WriteCache<?>> caches;

    public WriteBufferBuilder() {
        caches = new HashMap<String, WriteCache<?>>();
    }

    public WriteBufferBuilder addCaches(Map<String, WriteCache<?>> newCaches) {
        caches.putAll(newCaches);
        return this;
    }

    public WriteBufferBuilder addBuffer(WriteBufferBuilder buffer) {
        caches.putAll(buffer.caches);
        return this;
    }

    public WriteBuffer build() {
        return new WriteBuffer(new HashMap<String, WriteCache<?>>(caches));
    }
}
