/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.xygraph.dataprovider;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class CircularBufferTest {

    private CircularBuffer<String> circularBuffer;

    @Test
    public void testCircularBuffer() {
        circularBuffer = new CircularBuffer<String>(5);
    }

    @Test
    public void testAdd() {
        circularBuffer = new CircularBuffer<String>(5);
        for(int i=0; i<10; i++){
            circularBuffer.add("e" + i);
            //System.out.println(circularBuffer);
        }
    }

    @Test
    public void testToArray() {
        circularBuffer = new CircularBuffer<String>(5);
        for(int i=0; i<10; i++){
            circularBuffer.add("e" + i);
        }
        assertArrayEquals(new String[]{"e5", "e6", "e7", "e8", "e9"}, circularBuffer.toArray());

    }
    @Test
    public void testClear() {
        circularBuffer = new CircularBuffer<String>(5);
        for(int i=0; i<10; i++){
            circularBuffer.add("e" + i);
        }
        //System.out.println(circularBuffer);
        circularBuffer.clear();
        //System.out.println(circularBuffer);
    }



    @Test
    public void testSetBufferSize() {
        circularBuffer = new CircularBuffer<String>(5);
        for(int i=0; i<2; i++){
            circularBuffer.add("e" + i);
        }
        //System.out.println(circularBuffer);
        //test expand
        circularBuffer.setBufferSize(8, false);
        for(int i=0; i<6; i++){
            circularBuffer.add("e" + (i+2));
        }
        //System.out.println(circularBuffer);
        assertEquals("[e0, e1, e2, e3, e4, e5, e6, e7]", circularBuffer.toString());
        //test shrink
        circularBuffer.setBufferSize(5, false);
        //System.out.println(circularBuffer);
        assertEquals("[e3, e4, e5, e6, e7]", circularBuffer.toString());
        //test add
        for(int i=0; i<2; i++){
            circularBuffer.add("e" + i);
        }
        //System.out.println(circularBuffer);
        assertEquals("[e5, e6, e7, e0, e1]", circularBuffer.toString());
    }


    @Test
    public void testIterator() {
        circularBuffer = new CircularBuffer<String>(102400);
        for(int i=0; i<200000; i++){
            circularBuffer.add("e" + i);
        }
        int i=200000-102400;
        ////System.out.println(circularBuffer);
        for(final String s : circularBuffer){
            assertEquals("e"+i, s);
            i++;
        }
    }

}
