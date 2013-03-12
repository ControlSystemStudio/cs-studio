/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.util.array;

/**
 *
 * @author carcassi
 */
public class ListNumbers {
    
    public static SortedListView sortedView(ListNumber values) {
        SortedListView view = new SortedListView(values);
        if (values.size() <= 1) {
            // Nothing to sort
            return view;
        }
        
        double value = values.getDouble(0);
        for (int i = 1; i < values.size(); i++) {
            double newValue = values.getDouble(i);
            if (value > newValue) {
                SortedListView.quicksort(view);
                return view;
            }
        }

        return view;
    }
    
    public static SortedListView sortedView(ListNumber values, ListInt indexes) {
        SortedListView view = new SortedListView(values, indexes);
        return view;
    }
}
