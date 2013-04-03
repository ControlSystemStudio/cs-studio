/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
