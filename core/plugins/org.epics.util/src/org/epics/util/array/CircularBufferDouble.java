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

    public CircularBufferDouble(int capacity) {
        data = new double[capacity+1];
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
    
}
