/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.util.array;

/**
 * A collection of {@code double}s.
 *
 * @author Gabriele Carcassi
 */
public interface CollectionDouble extends CollectionNumber {

    @Override
    IteratorDouble iterator();
    
}
