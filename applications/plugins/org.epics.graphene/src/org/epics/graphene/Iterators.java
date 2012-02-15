/*
 * Copyright 2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author carcassi
 */
public class Iterators {
    public static IteratorDouble arrayIterator(final double[] data, final int startOffset, final int endOffset) {
        return new IteratorDouble() {
            
            int index = startOffset;

            @Override
            public boolean hasNext() {
                return index != endOffset;
            }

            @Override
            public double next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                double value = data[index];
                index++;
                if (index == data.length)
                    index = 0;
                return value;
            }
        };
    }
    
    public static IteratorDouble arrayIterator(final double[] data) {
        return new IteratorDouble() {
            
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < data.length;
            }

            @Override
            public double next() {
                double value = data[index];
                index++;
                return value;
            }
        };
    }
    
    public static double[] toArray(IteratorDouble iterator) {
        double[] buffer = new double[256];
        int offset = 0;
        while (iterator.hasNext()) {
            if (offset == buffer.length) {
                double[] newBuffer = new double[buffer.length * 2];
                System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
                buffer = newBuffer;
            }
            buffer[offset] = iterator.next();
            offset++;
        }
        return Arrays.copyOf(buffer, offset);
    }
    
    public static IteratorDouble combine(final Collection<IteratorDouble> iterators) {
        return new IteratorDouble() {
            
            private IteratorDouble currentIterator = null;
            private Iterator<IteratorDouble> iterator = iterators.iterator();

            @Override
            public boolean hasNext() {
                if (currentIterator != null && currentIterator.hasNext()) {
                    return true;
                }
                
                if (iterator.hasNext()) {
                    currentIterator = iterator.next();
                    return hasNext();
                }
                
                return false;
            }

            @Override
            public double next() {
                return currentIterator.next();
            }
        };
    }
}
