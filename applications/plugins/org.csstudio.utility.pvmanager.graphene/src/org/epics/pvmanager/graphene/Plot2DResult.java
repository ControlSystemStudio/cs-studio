/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager.graphene;

import org.epics.pvmanager.data.VImage;

/**
 *
 * @author carcassi
 */
public class Plot2DResult {
    private final VImage image;
    private final PlotDataRange xRange;
    private final PlotDataRange yRange;

    Plot2DResult(VImage image, PlotDataRange xRange, PlotDataRange yRange) {
        this.image = image;
        this.xRange = xRange;
        this.yRange = yRange;
    }

    public VImage getImage() {
        return image;
    }

    public PlotDataRange getxRange() {
        return xRange;
    }

    public PlotDataRange getyRange() {
        return yRange;
    }
    
}
