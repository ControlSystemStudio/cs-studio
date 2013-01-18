/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.vtype;

import org.epics.util.array.ListByte;

/**
 * Byte array with alarm, timestamp, display and control information.
 *
 * @author carcassi
 */
public interface VByteArray extends VNumberArray, VType {
    
    /**
     * {@inheritDoc }
     * @return the data
     */
    @Override
    ListByte getData();
}
