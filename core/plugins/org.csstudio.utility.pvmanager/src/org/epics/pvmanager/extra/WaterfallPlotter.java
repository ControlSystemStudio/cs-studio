/*
 * Copyright 2008-2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.extra;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import org.epics.pvmanager.Function;
import org.epics.pvmanager.data.Display;
import org.epics.pvmanager.data.Util;
import org.epics.pvmanager.data.VDoubleArray;
import org.epics.pvmanager.data.VImage;

/**
 *
 * @author carcassi
 */
class WaterfallPlotter extends Function<VImage> {

    private final Function<List<VDoubleArray>> function;
    private final WaterfallPlotParameters parameters;
    private BufferedImage previousBuffer;
    private VImage previousImage;
    private AdaptiveRange adaptiveRange;

    public WaterfallPlotter(Function<List<VDoubleArray>> function) {
        this(function, new WaterfallPlotParameters());
    }

    public WaterfallPlotter(Function<List<VDoubleArray>> function, WaterfallPlotParameters parameters) {
        this.function = function;
        this.parameters = parameters;
    }

    @Override
    public VImage getValue() {
        List<VDoubleArray> newArrays = function.getValue();
        if (newArrays.isEmpty())
            return previousImage;

        if (parameters.adaptiveRange) {
            if (adaptiveRange == null) {
                adaptiveRange = new AdaptiveRange();
            }
            adaptiveRange.considerValues(newArrays);
        }

        int newWidth = 0;
        for (VDoubleArray vDoubleArray : newArrays) {
            newWidth = Math.max(vDoubleArray.getArray().length, newWidth);
        }
        if (previousImage != null)
            newWidth = Math.max(previousImage.getWidth(), newWidth);

        BufferedImage image = new BufferedImage(newWidth, parameters.maxHeight, BufferedImage.TYPE_3BYTE_BGR);
        if (previousImage != null && newArrays.size() < parameters.maxHeight) {
            Graphics2D gc = image.createGraphics();
            gc.drawImage(previousBuffer, 0, newArrays.size(), null);
        }

        int line = newArrays.size();
        for (VDoubleArray vDoubleArray : newArrays) {
            line--;
            if (line < parameters.maxHeight) {
                if (parameters.adaptiveRange) {
                    fillLine(line, vDoubleArray.getArray(), adaptiveRange, parameters.colorScheme, image);
                } else {
                    fillLine(line, vDoubleArray.getArray(), vDoubleArray, parameters.colorScheme, image);
                }
            }
        }

        previousImage = Util.toVImage(image);
        previousBuffer = image;
        return previousImage;
    }

    private static void fillLine(int y, double[] array, Display display, ColorScheme colorScheme, BufferedImage image) {
        for (int i = 0; i < array.length; i++) {
            image.setRGB(i, y, colorScheme.color(array[i], display));
        }
    }

}
