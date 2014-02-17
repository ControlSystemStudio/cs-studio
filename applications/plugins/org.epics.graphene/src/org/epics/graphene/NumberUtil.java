/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public class NumberUtil {
    
    public static double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }
    
    public static double scale(double value, double min, double max, double newWidth) {
        return (value - min) * newWidth / (max - min);
    }
    
}
