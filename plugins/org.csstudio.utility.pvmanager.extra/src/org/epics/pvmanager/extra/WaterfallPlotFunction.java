/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.extra;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.epics.pvmanager.ReadFunction;
import org.epics.vtype.Display;
import org.epics.vtype.ValueUtil;
import org.epics.vtype.VImage;
import org.epics.pvmanager.extra.WaterfallPlotParameters.InternalCopy;
import org.epics.util.array.ListNumber;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;

/**
 * Implements the image calculation.
 *
 * @author carcassi
 */
class WaterfallPlotFunction implements ReadFunction<VImage> {

    private volatile WaterfallPlotParameters.InternalCopy mutableParameters;
    private WaterfallPlotParameters.InternalCopy previousParameters;
    private BufferedImage previousBuffer;
    private VImage previousImage;
    private Timestamp previousPlotEnd;
    private AdaptiveRange adaptiveRange;
    private DoubleArrayTimeCache doubleArrayTimeCache;

    public WaterfallPlotFunction(DoubleArrayTimeCache doubleArrayTimeCache, WaterfallPlotParameters.InternalCopy parameters) {
        this.doubleArrayTimeCache = doubleArrayTimeCache;
        this.mutableParameters = parameters;
    }

    public InternalCopy getParameters() {
        return mutableParameters;
    }

    public void setParameters(InternalCopy parameters) {
        this.mutableParameters = parameters;
    }
    
    private VImage drawImage() {
        // Make a safe copy of the parameters
        InternalCopy parameters = mutableParameters;
        if (parameters == null)
            return null;
        
        // If parameters changed, redraw all
        boolean redrawAll = parameters != previousParameters;
        
        // Calculate new end time for the plot, and how many pixels
        // should the plot scroll
        Timestamp plotEnd;
        int nNewPixels;
        if (previousPlotEnd != null) {
            nNewPixels = Timestamp.now().durationFrom(previousPlotEnd).dividedBy(parameters.pixelDuration);
            plotEnd = previousPlotEnd.plus(parameters.pixelDuration.multipliedBy(nNewPixels));
        } else {
            plotEnd = Timestamp.now();
            nNewPixels = 0;
            redrawAll = true;
        }
        Timestamp plotStart = plotEnd.minus(parameters.pixelDuration.multipliedBy(parameters.height));
        
        List<DoubleArrayTimeCache.Data> dataToPlot;
        if (redrawAll) {
            DoubleArrayTimeCache.Data data = doubleArrayTimeCache.getData(plotStart, plotEnd);
            if (data != null && data.getNArrays() != 0) {
                dataToPlot = Collections.singletonList(data);
            } else {
                dataToPlot = new ArrayList<DoubleArrayTimeCache.Data>();
            }
        } else {
            dataToPlot = doubleArrayTimeCache.newData(plotStart, previousPlotEnd, previousPlotEnd, plotEnd);
        }
        
        // If we already have an image, no new data, and the plot did not move,
        // just return the same plot!
        if (previousImage != null && nNewPixels == 0 && dataToPlot.isEmpty()) {
            return previousImage;
        }
        
        // If we don't have an image, and we have no new data, return no image
        if (previousImage == null && dataToPlot.isEmpty()) {
            return null;
        }

        // Initialize adaptiveRange
        if (parameters.adaptiveRange) {
            if (adaptiveRange == null) {
                adaptiveRange = new AdaptiveRange();
            }
        } else {
            adaptiveRange = null;
        }
        
        // Scan new values
        // Should only scan if adaptive range is on and if parameters do not
        // have a fixed width
        int newMaxArraySize = 0;
        for (DoubleArrayTimeCache.Data data : dataToPlot) {
            for (int n = 0; n < data.getNArrays(); n++) {
                ListNumber array = data.getArray(n);
                newMaxArraySize = Math.max(newMaxArraySize, array.size());
                if (adaptiveRange != null)
                    adaptiveRange.considerValues(array);
            }
        }
        
        if (adaptiveRange != null && adaptiveRange.limitsChanged()) {
            DoubleArrayTimeCache.Data data = doubleArrayTimeCache.getData(plotStart, plotEnd);
            dataToPlot = Collections.singletonList(data);
            redrawAll = true;
        }
        
        int newWidth = calculateNewWidth(previousBuffer, parameters, newMaxArraySize);
        if (newWidth == 0) {
            // If all data was zero length, return no image
            return null;
        }
        
        
        // Create new image. Copy the old image if needed.
        BufferedImage image = new BufferedImage(newWidth, parameters.height, BufferedImage.TYPE_3BYTE_BGR);
        if (previousImage != null && !redrawAll) {
            drawOldImage(image, previousBuffer, nNewPixels, parameters);
        } else if (parameters.backgroundColor != null) {
            Graphics2D gc = image.createGraphics();
            Color background = new Color(parameters.backgroundColor);
            gc.setColor(background);
            gc.fillRect(0, 0, newWidth, parameters.height);
            gc.dispose();
        }
        
        for (DoubleArrayTimeCache.Data data : dataToPlot) {
            int pixelsFromStart = 0;
            if (data.getBegin().compareTo(plotStart) > 0) {
                pixelsFromStart = data.getBegin().durationFrom(plotStart).dividedBy(parameters.pixelDuration);
            }
            int y = image.getHeight() - pixelsFromStart - 1;
            Timestamp pixelStart = plotStart.plus(parameters.pixelDuration.multipliedBy(pixelsFromStart));
            if (parameters.adaptiveRange) {
                drawSection(image, parameters, null, adaptiveRange, parameters.colorScheme, data, pixelStart, parameters.pixelDuration, y);
            } else {
                drawSection(image, parameters, null, doubleArrayTimeCache.getDisplay(), parameters.colorScheme, data, pixelStart, parameters.pixelDuration, y);
            }
        }
        
        previousImage = ValueUtil.toVImage(image);
        previousBuffer = image;
        previousPlotEnd = plotEnd;
        previousParameters = parameters;
        return previousImage;
    }
    
    private static void drawSection(BufferedImage image, InternalCopy parameters,
            double[] positions, Display display, ColorScheme colorScheme, DoubleArrayTimeCache.Data data,
            Timestamp pixelStart, TimeDuration pixelDuration, int y) {
        int usedArrays = 0;
        Timestamp pixelEnd = pixelStart.plus(pixelDuration);
        
        // Loop until the pixel starts before the range end
        while (pixelStart.compareTo(data.getEnd()) < 0) {
            // Get all the values in the pixel
            List<ListNumber> pixelValues = valuesInPixel(pixelStart, pixelEnd, data, usedArrays);
            // Determine the data to print on screen
            ListNumber dataToDisplay = aggregate(pixelValues);
            if (dataToDisplay == null) {
                copyPreviousLine(image, y, parameters);
            } else {
                drawLine(y, dataToDisplay, positions, display, colorScheme, image, parameters);
            }
            
            y--;
            pixelStart = pixelStart.plus(pixelDuration);
            pixelEnd = pixelStart.plus(pixelDuration);
        }
    }
    
    private static int calculateNewWidth(BufferedImage previousBuffer, InternalCopy parameters, int maxArraySize) {
        if (previousBuffer == null)
            return maxArraySize;
        
        return Math.max(previousBuffer.getWidth(), maxArraySize);
    }

    private static void copyPreviousLine(BufferedImage image, int y, InternalCopy parameters) {
        if (y < 0 || y >= image.getHeight())
            return;
        
        int previousY = y + 1;
        if (previousY < 0 || previousY >= image.getHeight())
            return;
        if (!parameters.scrollDown) {
            y = parameters.height - y - 1;
            previousY = parameters.height - previousY - 1;
        }
        if (y >= 0 && y < image.getHeight()) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setRGB(x, y, image.getRGB(x, previousY));
            }
        }
    }
    
    private static ListNumber aggregate(List<ListNumber> values) {
        if (values.isEmpty())
            return null;
        
        return values.get(values.size() - 1);
    }
    
    private static List<ListNumber> valuesInPixel(Timestamp pixelStart, Timestamp pixelEnd, DoubleArrayTimeCache.Data data, int usedArrays) {
        List<ListNumber> pixelValues = new ArrayList<ListNumber>();
        int currentArray = usedArrays;
        while (currentArray < data.getNArrays() && data.getTimestamp(currentArray).compareTo(pixelEnd) <= 0) {
            pixelValues.add(data.getArray(currentArray));
            currentArray++;
        }
        return pixelValues;
    }
    
    private static void drawLine(int y, ListNumber data, double[] positions, Display display, ColorScheme colorScheme, BufferedImage image, InternalCopy parameters) {
        if (positions != null)
            throw new RuntimeException("Positions not supported yet");
        
        if (y < 0 || y >= image.getHeight())
            return;
            
        if (!parameters.scrollDown) {
            y = parameters.height - y - 1;
        }
        for (int i = 0; i < data.size(); i++) {
            image.setRGB(i, y, colorScheme.color(data.getDouble(i), display));
        }
    }

    @Override
    public VImage readValue() {
        return drawImage();
    }

    private void drawOldImage(BufferedImage image, BufferedImage previousBuffer, int nNewPixels, InternalCopy parameters) {
        Graphics2D gc = image.createGraphics();
        if (parameters.scrollDown) {
            gc.drawImage(previousBuffer, 0, nNewPixels, null);
        } else {
            gc.drawImage(previousBuffer, 0, -nNewPixels, null);
        }
        gc.dispose();
    }

}
