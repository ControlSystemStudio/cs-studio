/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import java.util.List;
import org.epics.pvmanager.data.Enum;
import org.epics.util.array.ListInt;

/**
 *
 * @author carcassi
 */
public interface VEnumArray extends Array, Enum, Alarm, Time, VType {
    @Override
    List<String> getData();
    
    /**
     * Returns the indexes instead of the labels.
     * 
     * @return an array of indexes
     */
    ListInt getIndexes();
}
