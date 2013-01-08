/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.util.array;

/**
 * A collection of {@code short}s.
 *
 * @author Gabriele Carcassi
 */
public interface CollectionShort extends CollectionNumber {

    @Override
    IteratorShort iterator();
    
}
