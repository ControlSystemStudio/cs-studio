/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
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
public class Dataset1DUpdate {
    
    protected Collection<IteratorNumber> newData = new ArrayList<IteratorNumber>();
    protected boolean clear;
    
    public Dataset1DUpdate addData(IteratorNumber data) {
        newData.add(data);
        return this;
    }
    
    public Dataset1DUpdate addData(double[] data) {
        return addData(Iterators.arrayIterator(data));
    }
    
    public Dataset1DUpdate addData(ListNumber data) {
        return addData(data.iterator());
    }
    
    public Dataset1DUpdate addData(double data) {
        return addData(Iterators.arrayIterator(new double[] {data}));
    }
    
    public Dataset1DUpdate clearData() {
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
