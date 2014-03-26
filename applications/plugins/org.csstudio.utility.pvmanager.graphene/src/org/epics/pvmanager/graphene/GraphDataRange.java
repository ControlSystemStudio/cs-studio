/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.graphene;

import org.epics.graphene.Range;

/**
 * Range information of a drawn graph.
 * <p>
 * For an axis, it will give the range of the plot, the range of the current
 * data being visualize and the integrated range of all data displayed in the past.
 *
 * @author carcassi
 */
public class GraphDataRange {
    
    private final Range plotRange;
    private final Range dataRange;
    private final Range integratedRange;

    GraphDataRange(Range plotRange, Range dataRange, Range integratedRange) {
        this.plotRange = plotRange;
        this.dataRange = dataRange;
        this.integratedRange = integratedRange;
    }

    public Range getPlotRange() {
        return plotRange;
    }

    public Range getDataRange() {
        return dataRange;
    }

    public Range getIntegratedRange() {
        return integratedRange;
    }
    
}
