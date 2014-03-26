/**
 * Copyright (C) 2012-14 epics-util developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
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
