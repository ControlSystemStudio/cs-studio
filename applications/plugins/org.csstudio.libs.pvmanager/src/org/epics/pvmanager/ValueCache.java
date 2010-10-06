/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager;

/**
 *
 * @author carcassi
 */
public class ValueCache<T> extends Function<T> {

    private T value;

    public ValueCache(Class<T> dataType) {
        super(dataType);
        this.value = null;
    }

    @Override
    public T getValue() {
        return value;
    }

    public void setValue(T newValue) {
        this.value = newValue;
    }

}
