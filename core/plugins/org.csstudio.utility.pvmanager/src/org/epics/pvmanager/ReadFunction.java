/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

/**
 * A basic building block in the PVManager framework that can return a result
 * of a given type.
 *
 * @param <R> result type
 * @author carcassi
 */
public interface ReadFunction<R> {

    /**
     * Calculates, if needed, and then returns the value for this function.
     *
     * @return a value
     */
    public R readValue();
}
