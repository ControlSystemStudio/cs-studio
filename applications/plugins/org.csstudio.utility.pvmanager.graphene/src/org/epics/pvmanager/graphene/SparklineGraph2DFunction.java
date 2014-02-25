/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.graphene;

import org.epics.vtype.VNumberArray;
import org.epics.vtype.VImage;
import org.epics.vtype.ValueUtil;
import java.awt.image.BufferedImage;
import java.util.List;
import org.epics.graphene.*;
import org.epics.pvmanager.QueueCollector;
import org.epics.pvmanager.ReadFunction;
import static org.epics.pvmanager.graphene.ArgumentExpressions.stringArgument;
import org.epics.vtype.VTable;
import org.epics.vtype.VType;

/**
 *
 * @author carcassi
 */
class SparklineGraph2DFunction implements ReadFunction<Graph2DResult> {
    
    private ReadFunction<VType> tableData;
    private ReadFunctionArgument<String> xColumnName;
    private ReadFunctionArgument<String> yColumnName;
    
    private SparklineGraph2DRenderer renderer = new SparklineGraph2DRenderer(300, 200);
    
    private VImage previousImage;
    private final QueueCollector<SparklineGraph2DRendererUpdate> rendererUpdateQueue = new QueueCollector<>(100);

    SparklineGraph2DFunction(ReadFunction<?> tableData,
	    ReadFunction<?> xColumnName,
	    ReadFunction<?> yColumnName) {
        this.tableData = new CheckedReadFunction<VType>(tableData, "Data", VTable.class, VNumberArray.class);
        this.xColumnName = stringArgument(xColumnName, "X Column");
        this.yColumnName = stringArgument(yColumnName, "Y Column");
    }

    public QueueCollector<SparklineGraph2DRendererUpdate> getRendererUpdateQueue() {
        return rendererUpdateQueue;
    }

    @Override
    public Graph2DResult readValue() {
        VType vType = tableData.readValue();
        xColumnName.readNext();
        yColumnName.readNext();
        
        // Table and columns must be available
        if (vType == null || xColumnName.isMissing() || yColumnName.isMissing()) {
            return null;
        }

        // Prepare new dataset
        Point2DDataset dataset;
        if (vType instanceof VNumberArray) {
            dataset = Point2DDatasets.lineData(((VNumberArray) vType).getData());
        } else {
            dataset = DatasetConversions.point2DDatasetFromVTable((VTable) vType, xColumnName.getValue(), yColumnName.getValue());
        }
        
        // Process all renderer updates
        List<SparklineGraph2DRendererUpdate> updates = rendererUpdateQueue.readValue();
        for (SparklineGraph2DRendererUpdate rendererUpdate : updates) {
            renderer.update(rendererUpdate);
        }
        
        // If no size is set, don't calculate anything
        if (renderer.getImageHeight() == 0 && renderer.getImageWidth() == 0)
            return null;
        
        BufferedImage image = new BufferedImage(renderer.getImageWidth(), renderer.getImageHeight(), BufferedImage.TYPE_3BYTE_BGR);
        renderer.draw(image.createGraphics(), dataset);
        
        previousImage = ValueUtil.toVImage(image);
        return new Graph2DResult(vType, previousImage,
                new GraphDataRange(renderer.getXPlotRange(), dataset.getXStatistics(), renderer.getXAggregatedRange()),
                new GraphDataRange(renderer.getYPlotRange(), dataset.getYStatistics(), renderer.getYAggregatedRange()),
                -1);
    }
    
}
