/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.graphene;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.epics.graphene.*;
import org.epics.pvmanager.QueueCollector;
import org.epics.pvmanager.ReadFunction;
import org.epics.vtype.VImage;
import org.epics.vtype.VNumber;
import org.epics.vtype.ValueUtil;

/**
 *
 * @author carcassi
 */
class AreaGraph2DFunction implements ReadFunction<Graph2DResult> {
    
    private ReadFunction<? extends List<? extends VNumber>> argument;
    private Point1DCircularBuffer dataset = new Point1DCircularBuffer(1000000);
    private Histogram1D histogram = Histograms.createHistogram(dataset);
    private AreaGraph2DRenderer renderer = new AreaGraph2DRenderer(300, 200);
    private Graph2DResult previousImage;
    private List<Histogram1DUpdate> histogramUpdates = Collections.synchronizedList(new ArrayList<Histogram1DUpdate>());
    private QueueCollector<AreaGraph2DRendererUpdate> rendererUpdateQueue = new QueueCollector<>(100);

    public AreaGraph2DFunction(ReadFunction<? extends List<? extends VNumber>> argument) {
        this.argument = argument;
    }
    
    public void update(Histogram1DUpdate update) {
        // Already synchronized
        histogramUpdates.add(update);
    }
    
    public QueueCollector<AreaGraph2DRendererUpdate> getUpdateQueue() {
        return rendererUpdateQueue;
    }

    @Override
    public Graph2DResult readValue() {
        List<? extends VNumber> newData = argument.readValue();
        List<AreaGraph2DRendererUpdate> rendererUpdates = rendererUpdateQueue.readValue();
        if (newData.isEmpty() && previousImage != null && histogramUpdates.isEmpty() && rendererUpdates.isEmpty())
            return previousImage;
        
        // Update the dataset
        Point1DDatasetUpdate update = new Point1DDatasetUpdate();
        for (VNumber vNumber : newData) {
            update.addData(vNumber.getValue().doubleValue());
        }
        dataset.update(update);
        
        // Process all updates
        synchronized(histogramUpdates) {
            for (Histogram1DUpdate histogramUpdate : histogramUpdates) {
                histogram.update(histogramUpdate);
            }
            histogramUpdates.clear();
        }
        histogram.update(new Histogram1DUpdate().recalculateFrom(dataset));

        // Process all renderer updates
        for (AreaGraph2DRendererUpdate rendererUpdate : rendererUpdates) {
            renderer.update(rendererUpdate);
        }
        
        // If no size is set, don't calculate anything
        if (renderer.getImageHeight() == 0 && renderer.getImageWidth() == 0)
            return null;
        
        BufferedImage image = new BufferedImage(renderer.getImageWidth(), renderer.getImageHeight(), BufferedImage.TYPE_3BYTE_BGR);
        renderer.draw(image.createGraphics(), histogram);
        
        return new Graph2DResult(null, ValueUtil.toVImage(image),
                new GraphDataRange(renderer.getXPlotRange(), histogram.getXRange(), renderer.getXAggregatedRange()),
                new GraphDataRange(renderer.getYPlotRange(), histogram.getStatistics(), renderer.getYAggregatedRange()),
                -1);
    }
    
}
