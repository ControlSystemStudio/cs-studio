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
import org.epics.util.array.ArrayDouble;
import org.epics.vtype.VImage;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VTable;
import org.epics.vtype.VType;
import org.epics.vtype.ValueUtil;

/**
 *
 * @author carcassi
 */
class IntensityGraph2DFunction implements ReadFunction<Graph2DResult> {
    
    private ReadFunction<VNumberArray> arrayData;
    
    private IntensityGraph2DRenderer renderer = new IntensityGraph2DRenderer(300, 200);
    
    private Graph2DResult previousImage;
    private final QueueCollector<IntensityGraph2DRendererUpdate> rendererUpdateQueue = new QueueCollector<>(100);

    public IntensityGraph2DFunction(ReadFunction<?> arrayData) {
        this.arrayData = new CheckedReadFunction<VNumberArray>(arrayData, "Data", VNumberArray.class);
    }
    
    public QueueCollector<IntensityGraph2DRendererUpdate> getUpdateQueue() {
        return rendererUpdateQueue;
    }

    @Override
    public Graph2DResult readValue() {
        VNumberArray data = arrayData.readValue();
        
        // Data must be available
        if (data == null) {
            return null;
        }
        
        // TODO: check array is one dimensional

        Cell2DDataset dataset = null;
        try {
            if (data.getSizes().size() == 1) {
                dataset = Cell2DDatasets.datasetFrom(data.getData(), new ArrayDouble(0, 1),
                        data.getDimensionDisplay().get(0).getCellBoundaries());
            } else {
                dataset = Cell2DDatasets.datasetFrom(data.getData(), data.getDimensionDisplay().get(1).getCellBoundaries(),
                        data.getDimensionDisplay().get(0).getCellBoundaries());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        // Process all renderer updates
        for (IntensityGraph2DRendererUpdate rendererUpdate : getUpdateQueue().readValue()) {
            renderer.update(rendererUpdate);
        }
        
        // If no size is set, don't calculate anything
        if (renderer.getImageHeight() == 0 && renderer.getImageWidth() == 0)
            return null;
        
        BufferedImage image = new BufferedImage(renderer.getImageWidth(), renderer.getImageHeight(), BufferedImage.TYPE_3BYTE_BGR);
        renderer.draw(image.createGraphics(), dataset);
        
        return new Graph2DResult(null, ValueUtil.toVImage(image),
                null, null, -1);
//                new GraphDataRange(renderer.getXPlotRange(), dataset.getXRange(), renderer.getXAggregatedRange()),
//                new GraphDataRange(renderer.getYPlotRange(), dataset.getStatistics(), renderer.getYAggregatedRange()),
//                -1);
    }
    
}
