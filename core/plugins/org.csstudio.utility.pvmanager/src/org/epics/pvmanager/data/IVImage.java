/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.data;

/**
 * Image implementation
 *
 * @author carcassi
 */
class IVImage implements VImage {

    private final int height;
    private final int width;
    private final byte[] data;

    public IVImage(int height, int width, byte[] data) {
        this.height = height;
        this.width = width;
        this.data = data;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public byte[] getData() {
        return data;
    }

}
