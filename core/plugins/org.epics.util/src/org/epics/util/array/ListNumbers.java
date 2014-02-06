/**
 * Copyright (C) 2012-14 epics-util developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.util.array;

/**
 * Utilities for manipulating ListNumbers.
 *
 * @author carcassi
 */
public class ListNumbers {
    
    /**
     * Creates a sorted view of the given ListNumber.
     * <p>
     * The ListNumber is not sorted in place, and the data is not copied out.
     * Therefore it's intended that the ListNumber is not changed while
     * the view is used.
     * 
     * @param values the values to be sorted
     * @return the sorted view
     */
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
            value = newValue;
        }

        return view;
    }
    
    /**
     * Creates a sorted view of the given ListNumber based on the indexes provided.
     * This method can be used to sort the given values based on the ordering
     * by another (sorted) list of values.
     * <p>
     * The ListNumber is not sorted in place, and the data is not copied out.
     * Therefore it's intended that the ListNumber is not changed while
     * the view is used.
     * 
     * @param values the values to be sorted
     * @param indexes the ordering to be used for the view
     * @return the sorted view
     */
    public static SortedListView sortedView(ListNumber values, ListInt indexes) {
        SortedListView view = new SortedListView(values, indexes);
        return view;
    }
    
    /**
     * Finds the value in the list, or the one right below it.
     * 
     * @param values a list of values
     * @param value a value
     * @return the index of the value
     */
    public static int binarySearchValueOrLower(ListNumber values, double value) {
        if (value <= values.getDouble(0)) {
            return 0;
        }
        if (value >= values.getDouble(values.size() -1)) {
            return values.size() - 1;
        }
        
        int index = binarySearch(0, values.size() - 1, values, value);
        
        while (index != 0 && value == values.getDouble(index - 1)) {
            index--;
        }
        
        return index;
    }

    /**
     * Finds the value in the list, or the one right above it.
     * 
     * @param values a list of values
     * @param value a value
     * @return the index of the value
     */
    public static int binarySearchValueOrHigher(ListNumber values, double value) {
        if (value <= values.getDouble(0)) {
            return 0;
        }
        if (value >= values.getDouble(values.size() -1)) {
            return values.size() - 1;
        }
        
        int index = binarySearch(0, values.size() - 1, values, value);
        
        while (index != values.size() - 1 && value > values.getDouble(index)) {
            index++;
        }
        
        while (index != values.size() - 1 && value == values.getDouble(index + 1)) {
            index++;
        }
        
        return index;
    }

    private static int binarySearch(int low, int high, ListNumber values, double value) {
        // Taken from JDK
        while (low <= high) {
            int mid = (low + high) >>> 1;
            double midVal = values.getDouble(mid);

            if (midVal < value)
                low = mid + 1;  // Neither val is NaN, thisVal is smaller
            else if (midVal > value)
                high = mid - 1; // Neither val is NaN, thisVal is larger
            else {
                long midBits = Double.doubleToLongBits(midVal);
                long keyBits = Double.doubleToLongBits(value);
                if (midBits == keyBits)     // Values are equal
                    return mid;             // Key found
                else if (midBits < keyBits) // (-0.0, 0.0) or (!NaN, NaN)
                    low = mid + 1;
                else                        // (0.0, -0.0) or (NaN, !NaN)
                    high = mid - 1;
            }
        }
        
        return low - 1;  // key not found.
    }
    
    /**
     * Creates a list of equally spaced values given the range and the number of
     * elements.
     * <p>
     * Note that, due to rounding errors in double precision, the difference
     * between the elements may not be exactly the same.
     * 
     * @param minValue the first value in the list
     * @param maxValue the last value in the list
     * @param size the size of the list
     * @return a new list
     */
    public static ListNumber linearListFromRange(final double minValue, final double maxValue, final int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive (was " + size + " )");
        }
        return new ListDouble() {

            @Override
            public double getDouble(int index) {
                if (index < 0 || index >= size) {
                    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
                }
                return minValue + (index * (maxValue - minValue)) / (size - 1);
            }

            @Override
            public int size() {
                return size;
            }
        };
    }
    
    /**
     * Creates a list of equally spaced values given the first value, the
     * step between element and the size of the list.
     * 
     * @param initialValue the first value in the list
     * @param increment the difference between elements
     * @param size the size of the list
     * @return a new list
     */
    public static ListNumber linearList(final double initialValue, final double increment, final int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive (was " + size + " )");
        }
        return new ListDouble() {

            @Override
            public double getDouble(int index) {
                if (index < 0 || index >= size) {
                    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
                }
                return initialValue + index * increment;
            }

            @Override
            public int size() {
                return size;
            }
        };
    }
}
