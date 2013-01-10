/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

/**
 * A collector can be written from one thread and read from another and provides
 * the point where two subsystems and their rate can be decoupled.
 *
 * @param <I> the type written in the collector
 * @param <O> the type read from the collector
 * @author carcassi
 */
public interface Collector<I, O> extends WriteFunction<I>, ReadFunction<O> {
    
}
