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
import org.epics.pvmanager.data.VDouble;
import org.epics.pvmanager.data.VDoubleArray;
import org.epics.pvmanager.data.VImage;
import org.epics.pvmanager.data.ValueUtil;

/**
 *
 * @author carcassi
 */
class LineGraphFunction extends Function<VImage> {
    
    private Function<VDoubleArray> argument;
    private LineGraphRenderer renderer = new LineGraphRenderer();
    private VImage previousImage;
    private List<LineGraphRendererUpdate> rendererUpdates = Collections.synchronizedList(new ArrayList<LineGraphRendererUpdate>());

    public LineGraphFunction(Function<VDoubleArray> argument) {
        this.argument = argument;
    }
    
    public void update(LineGraphRendererUpdate update) {
        // Already synchronized
        rendererUpdates.add(update);
    }

    @Override
    public VImage getValue() {
        VDoubleArray newData = argument.getValue();
        
        // No data, no plot
        if (newData == null || newData.getArray() == null)
            return null;
        
        // Re-create the dataset
        OrderedDataset2D dataset = org.epics.graphene.Arrays.lineData(newData.getArray());

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
        return previousImage;
    }
    
}
