/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager;

/**
 *
 * @author carcassi
 */
public abstract class Function<T> {

    private Class<T> type;

    public Function(Class<T> type) {
        this.type = type;
    }

    public abstract T getValue();
    public Class<T> getType() {
        return type;
    }
}
