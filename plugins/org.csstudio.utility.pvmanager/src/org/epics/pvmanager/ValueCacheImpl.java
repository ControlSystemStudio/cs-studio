/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

/**
 * Represent a building block that can store a particular value
 *
 * @param <T> the type of the value held by the cache
 * @author carcassi
 */
public class ValueCacheImpl<T> implements ValueCache<T> {

    /**
     * The value cached
     */
    private T value;

    /**
     * The type of objects this cache can store
     */
    private final Class<T> type;

    /**
     * Creates a new cache for the given data type.
     *
     * @param dataType class token for the data type
     */
    public ValueCacheImpl(Class<T> dataType) {
        this.value = null;
        type = dataType;
    }

    /**
     * Returns the value in the cache.
     *
     * @return value in the cache
     */
    @Override
    public T readValue() {
        return value;
    }

    /**
     * Changes the value in the cache.
     *
     * @param newValue the new value
     */
    @Override
    public void writeValue(Object newValue) {
        // TODO should add type check
        this.value = type.cast(newValue);
    }

    /**
     * The type of objects that this cache can contain.
     *
     * @return the class token
     */
    @Override
    public Class<T> getType() {
        return type;
    }

}
