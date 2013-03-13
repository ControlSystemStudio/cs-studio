/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import org.epics.util.array.ListDouble;
import org.epics.util.array.ListNumber;

/**
 * TODO: move this in epics-util
 *
 * @author carcassi
 */
public class ListNumbers {
    public static ListNumber linearRange(final double minValue, final double maxValue, final int size) {
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
