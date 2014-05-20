/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

/**
 * An instance of a {@link NumberColorMap} that associates a numeric value
 * to an RGB color.
 *
 * @author sjdallst
 */
public interface NumberColorMapInstance {
    
    /**
     * Returns the color associated to the value. The color is an integer
     * enconding an ARGB (Alpha-Red-Green-Blue) value. Each component is 8 bits,
     * making 32 bit total.
     * 
     * @param value the value to be mapped
     * @return the color corresponding to the value
     */
    public int colorFor(double value);
}
