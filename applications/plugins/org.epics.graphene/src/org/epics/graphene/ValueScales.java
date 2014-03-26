/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

/**
 * The <code>ValueScales</code> class is useful to create
 * different types of <code>ValueScale</code>s.
 * It cannot be instantiated.
 * 
 * <p>Allows for a both a linear and logarithmic scale to be created.
 * The <code>ValueScale</code> objects handle the scaling themselves.
 * 
 * @author carcassi
 */
public class ValueScales {
    
    /**
     * Cannot be instantiated.
     */
    private ValueScales(){
    }
    
    /**
     * Returns a linear scale to handle value scaling.
     * @return linear scale
     */
    public static ValueScale linearScale() {
        return new LinearValueScale();
    }
    
    /**
     * Returns a logarithmic scale to handle value scaling.
     * @return logarithmic scale
     */
    public static ValueScale logScale() {
        return new LogValueScale();
    }
}
