/*
 * Copyright 2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.extra;

import java.util.List;
import org.epics.pvmanager.data.Display;
import org.epics.pvmanager.util.TimeDuration;
import org.epics.pvmanager.util.TimeStamp;


/**
 *
 * @author carcassi
 */
public interface DoubleArrayTimeCache {
    
    public interface Data {
        public TimeStamp getBegin();
        
        public TimeStamp getEnd();
        
        public int getNArrays();
        
        public double[] getArray(int index);
        
        public TimeStamp getTimeStamp(int index);
    }
    
    public Data getData(TimeStamp begin, TimeStamp end);
    
    /**
     * Each segment of the new data ends with an array of old data.
     * Two regions can be requested: an update region, where only updates
     * are going to be returned, and a new region, where all data is going to
     * be returned.
     * 
     * @return 
     */
    public List<Data> newData(TimeStamp beginUpdate, TimeStamp endUpdate, TimeStamp beginNew, TimeStamp endNew);
    
    public Display getDisplay();
}
