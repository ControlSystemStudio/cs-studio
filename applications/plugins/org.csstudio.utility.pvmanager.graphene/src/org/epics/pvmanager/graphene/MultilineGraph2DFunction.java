/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.graphene;

import org.epics.vtype.VNumberArray;
import org.epics.vtype.VImage;
import org.epics.vtype.ValueUtil;
import java.awt.image.BufferedImage;
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import org.epics.graphene.*;
import org.epics.pvmanager.QueueCollector;
import org.epics.pvmanager.ReadFunction;
import static org.epics.pvmanager.graphene.ArgumentExpressions.*;
import org.epics.util.stats.Statistics;
import org.epics.util.stats.StatisticsUtil;
import org.epics.vtype.VTable;
import org.epics.vtype.VType;

/**
 *
 * @author carcassi
 */
class MultilineGraph2DFunction implements ReadFunction<Graph2DResult> {
    
    private ReadFunction<VType> tableData;
    private ReadFunctionArgument<List<String>> xColumnNames;
    private ReadFunctionArgument<List<String>> yColumnNames;
    
    private LineGraph2DRenderer renderer = new LineGraph2DRenderer(300, 200);
    
    private VImage previousImage;
    private final QueueCollector<LineGraph2DRendererUpdate> rendererUpdateQueue = new QueueCollector<>(100);

    MultilineGraph2DFunction(ReadFunction<?> tableData,
	    ReadFunction<?> xColumnName,
	    ReadFunction<?> yColumnName) {
        this.tableData = new CheckedReadFunction<VType>(tableData, "Data", VTable.class, VNumberArray.class);
        this.xColumnNames = stringArrayArgument(xColumnName, "X Columns");
        this.yColumnNames = stringArrayArgument(yColumnName, "Y Columns");
    }

    public QueueCollector<LineGraph2DRendererUpdate> getRendererUpdateQueue() {
        return rendererUpdateQueue;
    }

    @Override
    public Graph2DResult readValue() {
        VType vType = tableData.readValue();
        xColumnNames.readNext();
        yColumnNames.readNext();
        
        // Table and columns must be available
        if (vType == null || xColumnNames.isMissing() || yColumnNames.isMissing()) {
            return null;
        }

        // Prepare new dataset
        final List<Point2DDataset> dataset;
        if (vType instanceof VNumberArray) {
            dataset = Collections.singletonList(Point2DDatasets.lineData(((VNumberArray) vType).getData()));
        } else {
            dataset = DatasetConversions.point2DDatasetsFromVTable((VTable) vType, xColumnNames.getValue(), yColumnNames.getValue());
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
        Statistics xDataRange = StatisticsUtil.statisticsOf(new AbstractList<Statistics>() {
            
            @Override
            public Statistics get(int index) {
                return dataset.get(index).getXStatistics();
            }

            @Override
            public int size() {
                return dataset.size();
            }
        });
        
        Statistics yDataRange = StatisticsUtil.statisticsOf(new AbstractList<Statistics>() {
            
            @Override
            public Statistics get(int index) {
                return dataset.get(index).getYStatistics();
            }

            @Override
            public int size() {
                return dataset.size();
            }
        });
        
        return new Graph2DResult(vType, previousImage,
                new GraphDataRange(renderer.getXPlotRange(), xDataRange, renderer.getXAggregatedRange()),
                new GraphDataRange(renderer.getYPlotRange(), yDataRange, renderer.getYAggregatedRange()),
                -1);
    }
    
}
