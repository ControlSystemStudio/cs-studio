/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.xygraph.dataprovider;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**A particular circular buffer. New arrived data will be appended to the tail of the buffer.
 * When buffer is full, the oldest data will be deleted when new data arrived.
 *
 * @author Xihui Chen
 */
public class CircularBuffer<T> extends AbstractCollection<T> {
    private int bufferSize =0;
    private T[] buffer;
    private int head;
    private int tail;
    private int count;

    public CircularBuffer(int bufferSize) {
        if(bufferSize <=0)
            throw new IllegalArgumentException("Buffer size must be greater than zero.");
        this.setBufferSize(bufferSize, true);
    }

    /** Add an element.
     * @param element
     */
    @Override
    public synchronized boolean add(T element){
        if(tail == head && count == bufferSize) { //buffer is full
            buffer[tail] = element;
            head = (head + 1) % bufferSize;
            tail = (tail + 1) % bufferSize;
            return true;
        }
        else{//buffer is not full
            buffer[tail] = element;
            tail = (tail + 1) % bufferSize;
            count++;
            return true;
        }
    }

    /**Get element
     * @param index the index of the element in the buffer.
     * @return the element. null if the data at the index doesn't exist.
     */
    public synchronized T getElement(int index){
        if(index < count)
            return buffer[(head + index) % bufferSize];
        else
            return null;
    }

    /**Get head element
     * @return the head element. null if the buffer is empty.
     */
    public synchronized T getHead(){
        if(count > 0)
            return buffer[head];
        else
            return null;
    }

    /**Get tail element
     * @return the tail element. null if the buffer is empty.
     */
    public synchronized T getTail(){
        if(count > 0)
            return buffer[(head+count-1)%bufferSize];
        else
            return null;
    }



    /**
     * clear the buffer;
     */
    @Override
    public synchronized void clear(){
        // Configure buffer to appear empty
        head = 0;
        tail = 0;
        count = 0;
        // Keep the actual buffer, but release its elements
        Arrays.fill(buffer, 0, buffer.length, null);
    }

    /**Set the buffer size.
     * @param bufferSize the bufferSize to set
     * @param clear clear the buffer if true. Otherwise keep the exist data;
     * Oldest data will be omitted if the new bufferSize is less
     * than the exist data count.
     */
    @SuppressWarnings("unchecked")
    public synchronized void setBufferSize(int bufferSize, boolean clear) {
        assert bufferSize > 0;
        if(this.bufferSize != bufferSize){
            if(clear){//clear
                buffer = (T[]) new Object[bufferSize];
                clear();
            }else{// keep the exist data
                T[] tempBuffer = (T[]) toArray();
                buffer = (T[]) new Object[bufferSize];
                if (bufferSize < count) {
                    System.arraycopy(tempBuffer, count-bufferSize, buffer, 0, bufferSize);
                } else {
                    System.arraycopy(tempBuffer, 0, buffer, 0, count);
                }
                count = Math.min(bufferSize, count);
                head =0;
                tail = count%bufferSize;
            }
            this.bufferSize = bufferSize;
        }
    }

    /**
     * @return the bufferSize
     */
    public synchronized int getBufferSize() {
        return bufferSize;
    }


    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>(){
            private int index=0;

            @Override
            public boolean hasNext() {
                return index < count;
            }
            @Override
            public T next() {
                if(!hasNext())
                    throw new NoSuchElementException();
                return buffer[(head+index++)%bufferSize];
            }
            @Override
            public void remove() {}
        };
    }

    @Override
    public int size() {
        return count;
    }



}
