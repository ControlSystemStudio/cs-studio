/**
 * Copyright (C) 2012-14 epics-util developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.util.array;

/**
 * A collection of {@code int}s.
 *
 * @author Gabriele Carcassi
 */
public interface CollectionInt extends CollectionNumber {

    @Override
    IteratorInt iterator();
    
}
