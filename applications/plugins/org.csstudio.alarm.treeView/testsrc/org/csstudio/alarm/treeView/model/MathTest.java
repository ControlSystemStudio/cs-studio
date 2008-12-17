package org.csstudio.alarm.treeView.model;

import static org.junit.Assert.*;
import static java.lang.Math.*;

import org.junit.Test;


public class MathTest {

    @Test
    public void testSquare() {
        assertEquals(1000.0d, sqrt(1000000.0d), 0.0d);
        assertEquals(2.0d, sqrt(4.0d), 0.0d);
        assertEquals(1.0d, sqrt(1.0d), 0.0d);
        assertEquals(0.0d, sqrt(0.0d), 0.0d);
        assertEquals(Double.NaN, sqrt(Double.NaN), 0.0d);
        assertEquals(Double.NaN, sqrt(-1.0d), 0.0d);
  }
}
