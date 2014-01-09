/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public class MathIgnoreNaN {
    public static double min(double a, double b) {
        if (Double.isNaN(a)) {
            return b;
        }
        
        if (Double.isNaN(b)) {
            return a;
        }
        
        return Math.min(a, b);
    }
    
    /**
     *Returns the max of the two numbers, even if a or b is NaN
     * @param a double, can be NaN
     * @param b double, can be NaN
     * @return <ul>
     *  <li>NaN if both a and b are NaN</li>
     *  <li>b if a is NaN</li>
     *  <li>a if b is NaN</li>
     *  <li>max of a and b if both are numbers</li>
     * </ul>
     */
    public static double max(double a, double b) {
        if (Double.isNaN(a)) {
            return b;
        }
        
        if (Double.isNaN(b)) {
            return a;
        }
        
        return Math.max(a, b);
    }
}
