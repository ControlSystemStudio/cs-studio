/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

/**
 * A basic building block in the PVManager framework that can return a result
 * of a given type.
 *
 * @param <R> result type
 * @author carcassi
 */
public abstract class Function<R> {

    /**
     * Calculates, if needed, and then returns the value for this function.
     *
     * @return a value
     */
    public abstract R getValue();
}
