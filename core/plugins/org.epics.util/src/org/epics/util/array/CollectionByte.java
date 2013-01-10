/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.util.array;

/**
 * A collection of {@code byte}s.
 *
 * @author Gabriele Carcassi
 */
public interface CollectionByte extends CollectionNumber {

    @Override
    IteratorByte iterator();
    
}
