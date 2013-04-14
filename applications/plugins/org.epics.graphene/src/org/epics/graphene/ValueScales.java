/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public class ValueScales {
    public static ValueScale linearScale() {
        return new LinearValueScale();
    }
    
    public static ValueScale logScale() {
        return new LogValueScale();
    }
}
