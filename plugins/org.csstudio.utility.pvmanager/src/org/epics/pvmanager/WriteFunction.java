/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

/**
 * A basic building block in the PVManager framework that can write an object
 * of a given type.
 *
 * @param <A> argument type
 * @author carcassi
 */
public interface WriteFunction<A> {

    /**
     * Takes the value and consumes it.
     *
     * @param newValue a value
     */
    public void writeValue(A newValue);
    
}
