/*
 * Copyright 2008-2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.extra;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.epics.pvmanager.Function;
import org.epics.pvmanager.data.Display;
import org.epics.pvmanager.data.Util;
import org.epics.pvmanager.data.VDoubleArray;
import org.epics.pvmanager.data.VImage;
import org.epics.pvmanager.data.ValueFactory;
import org.epics.pvmanager.extra.WaterfallPlotParameters.InternalCopy;
import org.epics.pvmanager.util.TimeStamp;

/**
 * Implements the image calculation.
 *
 * @author carcassi
 */
class WaterfallPlotFunction extends Function<VImage> {

    private final Function<List<VDoubleArray>> function;
    private volatile WaterfallPlotParameters.InternalCopy mutableParameters;
    private WaterfallPlotParameters.InternalCopy previousParameters;
    private BufferedImage previousBuffer;
    private VImage previousImage;
    private TimeStamp previousPlotEnd;
    private AdaptiveRange adaptiveRange;
    private List<VDoubleArray> previousValues = new LinkedList<VDoubleArray>();

    public WaterfallPlotFunction(Function<List<VDoubleArray>> function, WaterfallPlotParameters.InternalCopy parameters) {
        this.function = function;
        this.mutableParameters = parameters;
    }

    public InternalCopy getParameters() {
        return mutableParameters;
    }

    public void setParameters(InternalCopy parameters) {
        this.mutableParameters = parameters;
    }

    @Override
    public VImage getValue() {
        // Make a safe copy of the parameters
        InternalCopy parameters = mutableParameters;
        if (parameters == null)
            return null;
        
        // If parameters changed, redraw all
        boolean redrawAll = parameters != previousParameters;
        
        // Take new values, add them and reorder by time
        List<VDoubleArray> newArrays = function.getValue();
        previousValues.addAll(newArrays);
        Collections.sort(previousValues, Util.timeComparator());
        
        // If no values at all, return null
        if (previousValues.isEmpty())
            return null;

        // Initialize adaptive range
        if (parameters.adaptiveRange) {
            if (adaptiveRange == null) {
                adaptiveRange = new AdaptiveRange();
                adaptiveRange.considerValues(previousValues);
            }
            adaptiveRange.considerValues(newArrays);
        } else {
            adaptiveRange = null;
        }
        
        // Calculate new image width (max from all data)
        int newWidth = 0;
        for (VDoubleArray vDoubleArray : newArrays) {
            newWidth = Math.max(vDoubleArray.getArray().length, newWidth);
        }
        if (previousImage != null)
            newWidth = Math.max(previousImage.getWidth(), newWidth);
        
        // Calculate new end time for the plot, and how many pixels
        // should the plot scroll
        TimeStamp plotEnd;
        int nNewPixels;
        if (previousPlotEnd != null) {
            nNewPixels = (int) (TimeStamp.now().durationFrom(previousPlotEnd).getNanoSec() / parameters.pixelDuration.getNanoSec());
            plotEnd = previousPlotEnd.plus(parameters.pixelDuration.multiplyBy(nNewPixels));
        } else {
            plotEnd = TimeStamp.now();
            nNewPixels = 0;
            redrawAll = true;
        }
        
        // If we already have an image, no new data, and the plot did not move,
        // just return the same plot!
        if (previousImage != null && nNewPixels == 0 && newArrays.isEmpty()) {
            return previousImage;
        }
        
        
        // Create new image. Copy the old image if needed.
        BufferedImage image = new BufferedImage(newWidth, parameters.height, BufferedImage.TYPE_3BYTE_BGR);
        if (previousImage != null && !redrawAll) {
            drawOldImage(image, previousBuffer, nNewPixels, parameters);
        }
        
        // Calculate the rest of the time range
        TimeStamp plotStart = plotEnd.minus(parameters.pixelDuration.multiplyBy(parameters.height));
        TimeStamp pixelStart = plotStart;
        TimeStamp pixelEnd = pixelStart.plus(parameters.pixelDuration);
        
        // Remove old values, but keep one
        List<VDoubleArray> oldValues = new ArrayList<VDoubleArray>();
        for (VDoubleArray vDoubleArray : previousValues) {
            if (vDoubleArray.getTimeStamp().compareTo(plotStart) <= 0) {
                oldValues.add(vDoubleArray);
            } else {
                break;
            }
        }
        if (oldValues.size() > 1) {
            oldValues.remove(oldValues.size() - 1);
            previousValues.removeAll(oldValues);
        }
        
        // Initialize iterator. CurrentValue will hold the last value
        // taken from the iterator, that was not within the time intervale
        // of the current line. PreviousDisplayed holds the latest value
        // so that it can be redisplayed in case there are no new valued.
        Iterator<VDoubleArray> iter = previousValues.iterator();
        VDoubleArray currentValue = null;
        VDoubleArray previousDisplayed = null;
        if (!oldValues.isEmpty()) {
            currentValue = oldValues.get(oldValues.size() - 1);
            previousDisplayed = currentValue;
        } else {
            currentValue = iter.next();
        }
        
        // The values that fall within the pixel time range
        List<VDoubleArray> pixelValues = new ArrayList<VDoubleArray>();
        
        // Loop over all lines
        for (int line = parameters.height - 1; line >= 0; line--) {
            // Wether this line needs to be drawn. If everything needs
            // to be redrawn or if it's a new pixel, always draw.
            boolean drawLine = redrawAll || line < nNewPixels;

            // Accumulate values in pixel
            pixelValues.clear();
            while (currentValue != null) {
                // current value is past the pixel range
                if (currentValue.getTimeStamp().compareTo(pixelEnd) > 0) {
                    break;
                }
                
                // current value in pixel range, add
                pixelValues.add(currentValue);
                
                // If it is a new value, you must draw the line again
                drawLine = drawLine || newArrays.contains(currentValue);
                
                // Get new value if exists
                if (iter.hasNext()) {
                    currentValue = iter.next();
                } else {
                    currentValue = null;
                }
            }
            
            // Decide what to draw
            VDoubleArray toDraw = aggregate(pixelValues, newWidth);
            if (toDraw == null) {
                toDraw = previousDisplayed;
                drawLine = drawLine || newArrays.contains(previousDisplayed);
            }
            
            // Draw only if we have a line to draw and if it needs to be drawn
            if (toDraw != null && drawLine) {
                if (parameters.adaptiveRange) {
                    fillLine(line, toDraw.getArray(), adaptiveRange, parameters.colorScheme, image, parameters);
                } else {
                    fillLine(line, toDraw.getArray(), toDraw, parameters.colorScheme, image, parameters);
                }
            }
            
            // Get the latest value in case it should be displayed in the
            // next line
            if (!pixelValues.isEmpty())
                previousDisplayed = pixelValues.get(pixelValues.size() - 1);
            
            // Increase pixel range
            pixelEnd = pixelEnd.plus(parameters.pixelDuration);
        }

        previousImage = Util.toVImage(image);
        previousBuffer = image;
        previousPlotEnd = plotEnd;
        previousParameters = parameters;
        return previousImage;
    }
    
    private static VDoubleArray aggregate(List<VDoubleArray> values, int width) {
        // TODO: averaging/aggregation of arrays should be implemented somewhere else
        // for general use
        if (values.isEmpty())
            return null;
        
        if (values.size() == 1)
            return values.get(0);
        
        double[] average = new double[width];
        
        for (VDoubleArray value : values) {
            for (int i = 0; i < value.getArray().length; i++) {
                average[i] += value.getArray()[i];
            }
        }
        
        for (int i = 0; i < average.length; i++) {
            average[i] = average[i] / values.size();
        }
        
        VDoubleArray template = values.get(values.size() - 1);
        
        return ValueFactory.newVDoubleArray(average, template.getSizes(),
                template.getAlarmSeverity(), template.getAlarmStatus(),
                template.getTimeStamp(), template.getTimeUserTag(),
                template.getLowerDisplayLimit(), template.getLowerAlarmLimit(),
                template.getLowerWarningLimit(), template.getUnits(), template.getFormat(),
                template.getUpperWarningLimit(), template.getUpperAlarmLimit(),
                template.getUpperDisplayLimit(), template.getLowerCtrlLimit(), template.getUpperCtrlLimit());
    }

    private static void fillLine(int y, double[] array, Display display, ColorScheme colorScheme, BufferedImage image, InternalCopy parameters) {
        if (!parameters.scrollDown) {
            y = parameters.height - y - 1;
        }
        for (int i = 0; i < array.length; i++) {
            image.setRGB(i, y, colorScheme.color(array[i], display));
        }
    }

    private void drawOldImage(BufferedImage image, BufferedImage previousBuffer, int nNewPixels, InternalCopy parameters) {
        Graphics2D gc = image.createGraphics();
        if (parameters.scrollDown) {
            gc.drawImage(previousBuffer, 0, nNewPixels, null);
        } else {
            gc.drawImage(previousBuffer, 0, -nNewPixels, null);
        }
    }

}
