/*
 * Copyright 2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.graphene;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.epics.graphene.*;
import org.epics.pvmanager.Function;
import org.epics.pvmanager.data.*;

/**
 *
 * @author carcassi
 */
class LineGraphFunction extends Function<Plot2DResult> {
    
    private Function<? extends VNumberArray> yArray;
    private Function<? extends VNumberArray> xArray;
    private Function<? extends VNumber> xInitialOffset;
    private Function<? extends VNumber> xIncrementSize;
    
    private LineGraphRenderer renderer = new LineGraphRenderer();
    
    private VImage previousImage;
    private final List<LineGraphRendererUpdate> rendererUpdates = Collections.synchronizedList(new ArrayList<LineGraphRendererUpdate>());

    public LineGraphFunction(Function<? extends VNumberArray> argument) {
        this.yArray = argument;
    }

    public LineGraphFunction(Function<? extends VNumberArray> xArray, Function<? extends VNumberArray> yArray) {
        this.xArray = xArray;
        this.yArray = yArray;
    }

    public LineGraphFunction(Function<? extends VNumberArray> yArray, Function<? extends VNumber> xInitialOffset, Function<? extends VNumber> xIncrementSize) {
        this.xInitialOffset = xInitialOffset;
        this.xIncrementSize = xIncrementSize;
        this.yArray = yArray;
    }
    
    public void update(LineGraphRendererUpdate update) {
        // Already synchronized
        rendererUpdates.add(update);
    }

    @Override
    public Plot2DResult getValue() {
        VNumberArray newData = yArray.getValue();
        
        // No data, no plot
        if (newData == null || newData.getData() == null)
            return null;
        
        // Re-create the dataset
        Point2DDataset dataset = null;
        if (xArray != null) {
            // Plot with two arrays
            VNumberArray xData = xArray.getValue();
            if (xData != null && newData.getData() != null) {
                dataset = org.epics.graphene.Point2DDatasets.lineData(xData.getData(), newData.getData());
            }
            
        } else if (xInitialOffset != null && xIncrementSize != null) {
            // Plot with one array rescaled
            VNumber initialOffet = xInitialOffset.getValue();
            VNumber incrementSize = xIncrementSize.getValue();
            
            if (initialOffet != null && initialOffet.getValue() != null &&
                    incrementSize != null && incrementSize.getValue() != null) {
                dataset = org.epics.graphene.Point2DDatasets.lineData(newData.getData(), initialOffet.getValue().doubleValue(), incrementSize.getValue().doubleValue());
            }
        }
        
        if (dataset == null) {
            // Default to single array not rescaled
            dataset = org.epics.graphene.Point2DDatasets.lineData(newData.getData());
        }

        // Process all renderer updates
        synchronized(rendererUpdates) {
            for (LineGraphRendererUpdate rendererUpdate : rendererUpdates) {
                renderer.update(rendererUpdate);
            }
            rendererUpdates.clear();
        }
        
        // If no size is set, don't calculate anything
        if (renderer.getImageHeight() == 0 && renderer.getImageWidth() == 0)
            return null;
        
        BufferedImage image = new BufferedImage(renderer.getImageWidth(), renderer.getImageHeight(), BufferedImage.TYPE_3BYTE_BGR);
        renderer.draw(image.createGraphics(), dataset);
        
        previousImage = ValueUtil.toVImage(image);
        return new Plot2DResult(previousImage,
                new PlotDataRange(renderer.getStartPlotX(), renderer.getEndPlotX(), dataset.getXMinValue(), dataset.getXMaxValue(), renderer.getIntegratedMinX(), renderer.getIntegratedMaxX()),
                new PlotDataRange(renderer.getStartPlotY(), renderer.getEndPlotY(), dataset.getYMinValue(), dataset.getYMaxValue(), renderer.getIntegratedMinY(), renderer.getIntegratedMaxY()));
    }
    
}
