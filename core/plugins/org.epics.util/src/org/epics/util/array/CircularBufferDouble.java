/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.util.array;

/**
 *
 * @author carcassi
 */
public class CircularBufferDouble extends ListDouble {
    
    private double[] data;
    private int startOffset;
    private int endOffset;
    private final int maxCapacity;
    private boolean reachedMax;

    public CircularBufferDouble(int maxCapacity) {
        this(Math.min(10, maxCapacity), maxCapacity);
    }

    public CircularBufferDouble(int initialCapacity, int maxCapacity) {
        data = new double[initialCapacity];
        this.maxCapacity = maxCapacity;
    }
    
    private void resize() {
        int oldSize = data.length;
        int newSize = oldSize * 2;
        if (newSize > maxCapacity) {
            newSize = maxCapacity + 1;
            reachedMax = true;
        }
        double[] newData = new double[newSize];
        System.arraycopy(data, 0, newData, 0, oldSize);
        data = newData;
    }

    @Override
    public double getDouble(int index) {
        index += startOffset;
        if (index >= data.length) {
            index -= data.length;
        }
        return data[index];
    }

    @Override
    public int size() {
        int size = endOffset - startOffset;
        if (size < 0) {
            size += data.length;
        }
        return size;
    }
    
    public void addDouble(double value) {
        data[endOffset] = value;
        endOffset++;
        
        // Grow the buffer if needed
        if (endOffset == data.length && !reachedMax)
            resize();
        
        // Loop over and advance the start point if needed
        if (endOffset == data.length) {
            endOffset = 0;
        }
        if (endOffset == startOffset)
            startOffset++;
        if (startOffset == data.length)
            startOffset = 0;
    }
    
    public void clear() {
        startOffset = 0;
        endOffset = 0;
    }
    
    public int getCurrentCapacity() {
        return reachedMax ? maxCapacity : data.length;
    }
}
