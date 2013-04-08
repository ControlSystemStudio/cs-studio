/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.graphene;

import org.epics.vtype.VImage;

/**
 *
 * @author carcassi
 */
public class Graph2DResult {
    private final VImage image;
    private final GraphDataRange xRange;
    private final GraphDataRange yRange;

    Graph2DResult(VImage image, GraphDataRange xRange, GraphDataRange yRange) {
        this.image = image;
        this.xRange = xRange;
        this.yRange = yRange;
    }

    public VImage getImage() {
        return image;
    }

    public GraphDataRange getxRange() {
        return xRange;
    }

    public GraphDataRange getyRange() {
        return yRange;
    }
    
}
