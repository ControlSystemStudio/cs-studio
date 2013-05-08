/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.graphene;

import java.awt.image.BufferedImage;
import java.util.List;

import org.epics.graphene.Point2DDataset;
import org.epics.graphene.ScatterGraph2DRenderer;
import org.epics.graphene.ScatterGraph2DRendererUpdate;
import org.epics.pvmanager.QueueCollector;
import org.epics.pvmanager.ReadFunction;
import org.epics.vtype.VImage;
import org.epics.vtype.VString;
import org.epics.vtype.VTable;
import org.epics.vtype.ValueUtil;

/**
 * @author shroffk
 *
 */
public class ScatterGraph2DTableFunction implements ReadFunction<Graph2DResult> {

    private ReadFunction<? extends VTable> tableData;
    private ReadFunction<? extends VString> xColumnName;
    private ReadFunction<? extends VString> yColumnName;
    private ReadFunction<? extends VString> tooltipColumnName;
    private ScatterGraph2DRenderer renderer = new ScatterGraph2DRenderer(300,
            200);
    private VImage previousImage;
    private final QueueCollector<ScatterGraph2DRendererUpdate> rendererUpdateQueue = new QueueCollector<>(
            100);

    public ScatterGraph2DTableFunction(ReadFunction<?> tableData,
	    ReadFunction<?> xColumnName,
	    ReadFunction<?> yColumnName,
	    ReadFunction<?> tooltipColumnName) {
        this.tableData = new CheckedReadFunction<>(VTable.class, tableData, "Data");
        this.xColumnName = new CheckedReadFunction<>(VString.class, xColumnName, "X Column");
        this.yColumnName = new CheckedReadFunction<>(VString.class, yColumnName, "Y Column");
        this.tooltipColumnName = new CheckedReadFunction<>(VString.class, tooltipColumnName, "Tooltip Column");
    }

    public QueueCollector<ScatterGraph2DRendererUpdate> getRendererUpdateQueue() {
        return rendererUpdateQueue;
    }

    @Override
    public Graph2DResult readValue() {
        VTable vTable = tableData.readValue();
        VString xVString = xColumnName.readValue();
        VString yVString = yColumnName.readValue();
        
        // Table must be available
        if (vTable == null) {
            return null;
        }

        // Extract column names
        String xColumn = null;
        if (xVString != null) {
            xColumn = xVString.getValue();
        }
        String yColumn = null;
        if (yVString != null) {
            yColumn = yVString.getValue();
        }

        Point2DDataset dataset = DatasetConversions.point2DDatasetFromVTable(vTable, xColumn, yColumn);

        List<ScatterGraph2DRendererUpdate> updates = rendererUpdateQueue
                .readValue();
        for (ScatterGraph2DRendererUpdate scatterGraph2DRendererUpdate : updates) {
            renderer.update(scatterGraph2DRendererUpdate);
        }

        if (renderer.getImageHeight() == 0 && renderer.getImageWidth() == 0) {
            return null;
        }

        BufferedImage image = new BufferedImage(renderer.getImageWidth(),
                renderer.getImageHeight(), BufferedImage.TYPE_3BYTE_BGR);
        renderer.draw(image.createGraphics(), dataset);

        previousImage = ValueUtil.toVImage(image);
        return new Graph2DResult(previousImage,
                new GraphDataRange(renderer.getXPlotRange(), renderer.getXPlotRange(), renderer.getXAggregatedRange()), new GraphDataRange(
                renderer.getYPlotRange(), renderer.getYPlotRange(), renderer.getYAggregatedRange()));

    }
}
