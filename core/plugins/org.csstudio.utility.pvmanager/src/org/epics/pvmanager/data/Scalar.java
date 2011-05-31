/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.data;

/**
 * Basic type definition for all scalar types. {@link #getValue()} never returns
 * null, even if the channel never connected. One <b>must always look</b>
 * at the alarm severity to be able to correctly interpret the value.
 * <p>
 * Coding to {@code Scalart<T extends Object>} allows to write a client that works with all
 * scalars, regardless of their type.
 * Coding to {@code Scalart<T extends Number>} allows to write a client that works with all
 * numbers, regardless of their type.
 *
 * @param <T> the type of the scalar
 * @author carcassi
 */
public interface Scalar<T> {

    /**
     * Returns the value. Never null.
     *
     * @return the value
     */
    T getValue();
}
