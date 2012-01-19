/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.data;

/**
 *
 * @author carcassi
 */
public interface VEnumArray extends Array<String>, Enum, Alarm, Time {
    @Override
    String[] getArray();
    
    /**
     * Returns the indexes instead of the labels.
     * 
     * @return an array of indexes
     */
    int[] getIndexes();
}
