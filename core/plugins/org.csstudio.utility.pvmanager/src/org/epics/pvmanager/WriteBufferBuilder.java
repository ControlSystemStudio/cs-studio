/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author carcassi
 */
class WriteBufferBuilder {

    private final Map<String, WriteCache<?>> caches;

    WriteBufferBuilder() {
        caches = new HashMap<String, WriteCache<?>>();
    }

    WriteBufferBuilder addCaches(Map<String, WriteCache<?>> newCaches) {
        caches.putAll(newCaches);
        return this;
    }

    WriteBufferBuilder addBuffer(WriteBufferBuilder buffer) {
        caches.putAll(buffer.caches);
        return this;
    }

    WriteBuffer build() {
        return new WriteBuffer(new HashMap<String, WriteCache<?>>(caches));
    }
}
