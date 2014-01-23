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
    
    public Point1DDatasetUpdate addData(IteratorNumber data) {
        newData.add(data);
        return this;
    }
    
    public Point1DDatasetUpdate addData(double[] data) {
        return addData(Iterators.arrayIterator(data));
    }
    
    public Point1DDatasetUpdate addData(ListNumber data) {
        return addData(data.iterator());
    }
    
    public Point1DDatasetUpdate addData(double data) {
        return addData(Iterators.arrayIterator(new double[] {data}));
    }
    
    public Point1DDatasetUpdate clearData() {
        clear = true;
        newData.clear();
        return this;
    }
    
    public boolean isToClear() {
        return clear;
    }
    
    public IteratorNumber getNewData() {
        return Iterators.combine(newData);
    }
    
}
