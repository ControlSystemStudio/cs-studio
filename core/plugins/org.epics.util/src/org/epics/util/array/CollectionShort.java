/**
 * Copyright (C) 2012-14 epics-util developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
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
