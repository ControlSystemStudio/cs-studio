/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.graphene;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.epics.graphene.*;
import org.epics.pvmanager.QueueCollector;
import org.epics.pvmanager.ReadFunction;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListMath;
import org.epics.vtype.ArrayDimensionDisplay;
import org.epics.vtype.VImage;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VTable;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;
import org.epics.vtype.ValueUtil;

/**
 *
 * @author carcassi
 */
class IntensityGraph2DFunction implements ReadFunction<Graph2DResult> {
    
    private ReadFunction<VNumberArray> arrayData;
    
    private IntensityGraph2DRenderer renderer = new IntensityGraph2DRenderer(300, 200);
    
    private VNumberArray oldData;
    private Graph2DResult previousResult;
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

        List<IntensityGraph2DRendererUpdate> updates = getUpdateQueue().readValue();
        
        // If data is old and no updates, return the previous result
        if (data == oldData && updates.isEmpty()) {
            return previousResult;
        }
        
        oldData = data;
        
        // TODO: check array is one dimensional

        Cell2DDataset dataset = DatasetConversions.cell2DDatasetsFromVNumberArray(data);
        
        // Process all renderer updates
        for (IntensityGraph2DRendererUpdate rendererUpdate : updates) {
            renderer.update(rendererUpdate);
        }
        
        // If no size is set, don't calculate anything
        if (renderer.getImageHeight() == 0 && renderer.getImageWidth() == 0)
            return null;
        
        GraphBuffer buffer = new GraphBuffer(renderer);
        renderer.draw(buffer, dataset);
        
        return new Graph2DResult(data, ValueUtil.toVImage(buffer.getImage()),
                new GraphDataRange(renderer.getXPlotRange(), dataset.getXRange(), dataset.getXRange()),
                new GraphDataRange(renderer.getYPlotRange(), dataset.getYRange(), dataset.getYRange()),
                -1, selectionData(data, renderer));
    }
    
    private VNumberArray selectionData(VNumberArray data, IntensityGraph2DRenderer renderer) {
        if (renderer.getXIndexSelectionRange() == null ||
                renderer.getYIndexSelectionRange() == null) {
            return null;
        }
        
        ArrayDimensionDisplay xDisplay = data.getDimensionDisplay().get(1);
        int leftIndex = renderer.getXIndexSelectionRange().getMinimum().intValue();
        int rightIndex = renderer.getXIndexSelectionRange().getMaximum().intValue();
        final int xSize = rightIndex - leftIndex + 1;
        if (xDisplay.isReversed()) {
            leftIndex = data.getSizes().getInt(1) - 1 - leftIndex;
            rightIndex = data.getSizes().getInt(1) - 1 - rightIndex;
        }
        
        ArrayDimensionDisplay yDisplay = data.getDimensionDisplay().get(0);
        int bottomIndex = renderer.getYIndexSelectionRange().getMinimum().intValue();
        int topIndex = renderer.getYIndexSelectionRange().getMaximum().intValue();
        if (yDisplay.isReversed()) {
            bottomIndex = data.getSizes().getInt(0) - 1 - bottomIndex;
            topIndex = data.getSizes().getInt(0) - 1 - topIndex;
        }
        final int ySize = topIndex - bottomIndex + 1;

        return ValueFactory.newVNumberArray(new ListDouble() {

            @Override
            public double getDouble(int index) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int size() {
                return xSize * ySize;
            }
        }, new ArrayInt(ySize, xSize), Arrays.asList(ValueFactory.newDisplay(ListMath.limit(yDisplay.getCellBoundaries(), bottomIndex, topIndex + 1), yDisplay.getUnits()),
                ValueFactory.newDisplay(ListMath.limit(xDisplay.getCellBoundaries(), leftIndex, rightIndex + 1), yDisplay.getUnits())), data, data, data);
    }
    
}
