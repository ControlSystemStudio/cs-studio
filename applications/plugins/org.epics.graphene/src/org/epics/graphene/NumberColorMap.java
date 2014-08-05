/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import org.epics.util.stats.Range;

/**
 * A function that associates a numeric value with an RBG color.
 * <p>
 * This class represents the abstract definition, that can be either
 * absolute or relative to the data range being displayed. Given the actual
 * range of the values, a {@link NumberColorMapInstance} is created
 * which actually does the mapping.
 * 
 * @author carcassi
 */
public interface NumberColorMap {
    
    /**
     * Given the range of the values to be displayed, creates an instance
     * that can map values to colors.
     * 
     * @param range the range to be displayed; can't be null
     * @return the new instance
     */
    public NumberColorMapInstance createInstance(Range range);
    
}
