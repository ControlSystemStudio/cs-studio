/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager;

/**
 * A basic building block in the PVManager framework that can write an object
 * of a given type.
 *
 * @param <R> argument type
 * @author carcassi
 */
public abstract class WriteFunction<A> {

    /**
     * Takes the value and consumes it.
     *
     * @param newValue a value
     */
    public abstract void setValue(A newValue);
    
}
