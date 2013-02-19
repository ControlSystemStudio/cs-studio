/*
 * Copyright 2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.graphene;

import org.epics.vtype.VNumberArray;
import org.epics.vtype.VNumber;
import org.epics.vtype.VImage;
import org.epics.vtype.ValueUtil;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.epics.graphene.*;
import org.epics.pvmanager.QueueCollector;
import org.epics.pvmanager.ReadFunction;

/**
 *
 * @author carcassi
 */
class LineGraphFunction implements ReadFunction<Plot2DResult> {
    
    private ReadFunction<? extends VNumberArray> yArray;
    private ReadFunction<? extends VNumberArray> xArray;
    private ReadFunction<? extends VNumber> xInitialOffset;
    private ReadFunction<? extends VNumber> xIncrementSize;
    
    private LineGraph2DRenderer renderer = new LineGraph2DRenderer();
    
    private VImage previousImage;
    private final QueueCollector<LineGraph2DRendererUpdate> rendererUpdateQueue = new QueueCollector<>(100);

    public LineGraphFunction(ReadFunction<? extends VNumberArray> argument) {
        this.yArray = argument;
    }

    public LineGraphFunction(ReadFunction<? extends VNumberArray> xArray, ReadFunction<? extends VNumberArray> yArray) {
        this.xArray = xArray;
        this.yArray = yArray;
    }

    public LineGraphFunction(ReadFunction<? extends VNumberArray> yArray, ReadFunction<? extends VNumber> xInitialOffset, ReadFunction<? extends VNumber> xIncrementSize) {
        this.xInitialOffset = xInitialOffset;
        this.xIncrementSize = xIncrementSize;
        this.yArray = yArray;
    }

    public QueueCollector<LineGraph2DRendererUpdate> getRendererUpdateQueue() {
        return rendererUpdateQueue;
    }

    @Override
    public Plot2DResult readValue() {
        VNumberArray newData = yArray.readValue();
        
        // No data, no plot
        if (newData == null || newData.getData() == null)
            return null;
        
        // Re-create the dataset
        Point2DDataset dataset = null;
        if (xArray != null) {
            // Plot with two arrays
            VNumberArray xData = xArray.readValue();
            if (xData != null && newData.getData() != null) {
                dataset = org.epics.graphene.Point2DDatasets.lineData(xData.getData(), newData.getData());
            }
            
        } else if (xInitialOffset != null && xIncrementSize != null) {
            // Plot with one array rescaled
            VNumber initialOffet = xInitialOffset.readValue();
            VNumber incrementSize = xIncrementSize.readValue();
            
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
        List<LineGraph2DRendererUpdate> updates = rendererUpdateQueue.readValue();
        for (LineGraph2DRendererUpdate rendererUpdate : updates) {
            renderer.update(rendererUpdate);
        }
        
        // If no size is set, don't calculate anything
        if (renderer.getImageHeight() == 0 && renderer.getImageWidth() == 0)
            return null;
        
        BufferedImage image = new BufferedImage(renderer.getImageWidth(), renderer.getImageHeight(), BufferedImage.TYPE_3BYTE_BGR);
        renderer.draw(image.createGraphics(), dataset);
        
        previousImage = ValueUtil.toVImage(image);
        return new Plot2DResult(previousImage,
                new PlotDataRange(renderer.getXPlotRange(), dataset.getXStatistics(), renderer.getXAggregatedRange()),
                new PlotDataRange(renderer.getYPlotRange(), dataset.getYStatistics(), renderer.getYAggregatedRange()));
    }
    
}
