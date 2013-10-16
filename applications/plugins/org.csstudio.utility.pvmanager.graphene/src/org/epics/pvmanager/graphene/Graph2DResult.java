/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.graphene;

import org.epics.vtype.VImage;
import org.epics.vtype.VType;

/**
 *
 * @author carcassi
 */
public class Graph2DResult {
    private final VType data;
    private final VImage image;
    private final GraphDataRange xRange;
    private final GraphDataRange yRange;
    private final int focusDataIndex;

    Graph2DResult(VType data, VImage image, GraphDataRange xRange, GraphDataRange yRange, int focusDataIndex) {
        this.data = data;
        this.image = image;
        this.xRange = xRange;
        this.yRange = yRange;
        this.focusDataIndex = focusDataIndex;
    }
    
    public VType getData() {
        return data;
    }
    
    public int focusDataIndex() {
        return focusDataIndex;
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
