/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

/**
 * Represent a building block that can store a particular value
 *
 * @param <T> the type of the value held by the cache
 * @author carcassi
 */
public interface ValueCache<T> extends ReadFunction<T>, WriteFunction<T> {

    /**
     * The type of objects that this cache can contain.
     *
     * @return the class token
     */
    public Class<T> getType();

}
