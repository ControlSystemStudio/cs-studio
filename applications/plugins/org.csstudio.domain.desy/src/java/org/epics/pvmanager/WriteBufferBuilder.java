/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.HashMap;
import java.util.Map;

/**
 * A builder for {@link WriteBuffer }.
 *
 * @author carcassi
 */
public class WriteBufferBuilder {

    private final Map<String, WriteCache<?>> caches;

    /**
     * A new builder
     */
    public WriteBufferBuilder() {
        caches = new HashMap<String, WriteCache<?>>();
    }

    /**
     * Adds a set of channel/write caches.
     * 
     * @param newCaches the channels/write chaches to add
     * @return this
     */
    public WriteBufferBuilder addCaches(Map<String, WriteCache<?>> newCaches) {
        caches.putAll(newCaches);
        return this;
    }

    /**
     * Adds another builder
     * 
     * @param buffer builder to add
     * @return this
     */
    public WriteBufferBuilder addBuffer(WriteBufferBuilder buffer) {
        caches.putAll(buffer.caches);
        return this;
    }

    /**
     * Creates a new WriteBuffer.
     * 
     * @return a new WriteBuffer
     */
    public WriteBuffer build() {
        return new WriteBuffer(new HashMap<String, WriteCache<?>>(caches));
    }
}
