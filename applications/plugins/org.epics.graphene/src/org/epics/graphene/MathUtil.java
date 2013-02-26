/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import org.epics.util.array.IteratorDouble;

/**
 *
 * @author carcassi
 */
public class MathUtil {
    
    public static int orderOf(double value) {
        return (int) Math.floor(Math.log10(value));
    }
}
