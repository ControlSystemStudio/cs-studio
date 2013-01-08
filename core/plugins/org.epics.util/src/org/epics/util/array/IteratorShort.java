/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.util.array;

/**
 * An iterator of {@code short}s.
 *
 * @author Gabriele Carcassi
 */
public abstract class IteratorShort implements IteratorNumber {

    @Override
    public float nextFloat() {
        return (float) nextShort();
    }

    @Override
    public double nextDouble() {
        return (double) nextShort();
    }

    @Override
    public byte nextByte() {
        return (byte) nextShort();
    }

    @Override
    public int nextInt() {
        return (int) nextShort();
    }

    @Override
    public long nextLong() {
        return (long) nextShort();
    }
    
}
