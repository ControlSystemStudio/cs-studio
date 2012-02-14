/*
 * Copyright 2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author carcassi
 */
public class Dataset1DUpdate {
    
    protected Collection<IteratorDouble> newData = new ArrayList<IteratorDouble>();
    protected boolean clear;
    
    public Dataset1DUpdate addData(IteratorDouble data) {
        newData.add(data);
        return this;
    }
    
    public Dataset1DUpdate addData(double[] data) {
        return addData(Iterators.arrayIterator(data));
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
    
    public IteratorDouble getNewData() {
        return Iterators.combine(newData);
    }
    
}
