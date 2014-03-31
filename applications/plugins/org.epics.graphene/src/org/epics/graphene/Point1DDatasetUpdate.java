/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import java.util.ArrayList;
import java.util.Collection;
import org.epics.util.array.IteratorDouble;
import org.epics.util.array.IteratorNumber;
import org.epics.util.array.ListNumber;

/**
 *
 * @author carcassi
 */
public class Point1DDatasetUpdate {
    
    protected Collection<IteratorNumber> newData = new ArrayList<IteratorNumber>();
    protected boolean clear;
    
    /**
     *Appends an <code>IteratorNumber</code> object to the end of this object's Collection object.
     * @param data - IteratorNumber to be added to this Point1DDatasetUpdate object's Collection object.
     * @return Point1DDatasetUpdate
     */
    public Point1DDatasetUpdate addData(IteratorNumber data) {
        newData.add(data);
        return this;
    }
    
    /**
     *Converts data to an IteratorNumber and calls addData(IteratorNumber)
     * @param data - Array of doubles to be added to this Point1DDatasetUpdate object's Collection object
     * @return Point1DDatasetUpdate
     */
    public Point1DDatasetUpdate addData(double[] data) {
        return addData(Iterators.arrayIterator(data));
    }
    
    /**
     *Converts data to an IteratorNumber and calls addData(IteratorNumber)
     * @param data - ListNumber to be added to this Point1DDatasetUpdate object's Collection object.
     * @return Point1DDatasetUpdate
     */
    public Point1DDatasetUpdate addData(ListNumber data) {
        return addData(data.iterator());
    }
    
    /**
     *Converts data to an IteratorNumber and calls addData(IteratorNumber)
     * @param data - double to be added to this Point1DDatasetUpdate object's Collection object.
     * @return Point1DDatasetUpdate
     */
    public Point1DDatasetUpdate addData(double data) {
        return addData(Iterators.arrayIterator(new double[] {data}));
    }
    
    /**
     *Clears this object's Collection object (newData), sets "clear" to true.
     * @return Point1DDatasetUpdate
     */
    public Point1DDatasetUpdate clearData() {
        clear = true;
        newData.clear();
        return this;
    }
    
    /**
     *Returns whether this object should clear it's Collection object.
     * @return boolean
     */
    public boolean isToClear() {
        return clear;
    }
    
    /**
     *Returns an IteratorNumber pointing to null with an Iterator object containing all IteratorNumbers in this object's collection.
     * @return IteratorNumber
     */
    public IteratorNumber getNewData() {
        return Iterators.combine(newData);
    }
    
}
