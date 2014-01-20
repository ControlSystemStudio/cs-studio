/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.vtype;

import java.util.List;
import org.epics.util.array.ListNumber;

/**
 * Numeric array with alarm, timestamp, display and control information.
 * <p>
 * This class allows to use any numeric array (i.e. {@link VIntArray} or
 * {@link VDoubleArray}) through the same interface.
 *
 * @author carcassi
 */
public interface VNumberArray extends Array, Alarm, Time, Display, VType {
    @Override
    ListNumber getData();
    
    /**
     * Returns the boundaries of each cell
     * 
     * @return 
     */
    List<ArrayDimensionDisplay> getDimensionDisplay();
}
