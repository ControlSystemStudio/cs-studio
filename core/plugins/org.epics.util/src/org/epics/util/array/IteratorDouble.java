/**
 * Copyright (C) 2012-14 epics-util developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.util.array;

/**
 * An iterator of {@code double}s.
 *
 * @author Gabriele Carcassi
 */
public abstract class IteratorDouble implements IteratorNumber {

    @Override
    public float nextFloat() {
        return (float) nextDouble();
    }

    @Override
    public byte nextByte() {
        return (byte) nextDouble();
    }

    @Override
    public short nextShort() {
        return (short) nextDouble();
    }

    @Override
    public int nextInt() {
        return (int) nextDouble();
    }

    @Override
    public long nextLong() {
        return (long) nextDouble();
    }
    
}
