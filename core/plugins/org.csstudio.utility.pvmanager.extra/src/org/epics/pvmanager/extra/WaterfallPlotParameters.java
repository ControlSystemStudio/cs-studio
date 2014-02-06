/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.extra;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.epics.util.time.TimeDuration;

/**
 * Parameters for the waterfall plot
 *
 * @author carcassi
 */
public class WaterfallPlotParameters extends Parameters {

    static class InternalCopy {
        final Integer height;
        final ColorScheme colorScheme;
        final Boolean adaptiveRange;
        final Boolean scrollDown;
        final TimeDuration pixelDuration;
        final Integer backgroundColor;
        
        private InternalCopy(Map<Object, Object> params) {
            height = (Integer) params.get("height");
            colorScheme = (ColorScheme) params.get("colorScheme");
            adaptiveRange = (Boolean) params.get("adaptiveRange");
            scrollDown = (Boolean) params.get("scrollDown");
            pixelDuration = (TimeDuration) params.get("pixelDuration");
            backgroundColor = (Integer) params.get("backgroundColor");
        }
        
    }

    WaterfallPlotParameters(Object name, Object value) {
        super(Collections.singletonMap(name, value));
    }

    WaterfallPlotParameters(Map<Object, Object> map) {
        super(map);
    }

    WaterfallPlotParameters(WaterfallPlotParameters defaults, WaterfallPlotParameters... newValues) {
        super(defaults, newValues);
    }

    /**
     * Defaults parameters for the waterfall plot.
     * 
     * @return set of default parameters
     */
    public static WaterfallPlotParameters defaults() {
        Map<Object, Object> defaults = new HashMap<Object, Object>();
        defaults.put("height", 50);
        defaults.put("colorScheme", ColorScheme.singleRangeGradient(Color.BLACK, Color.WHITE));
        defaults.put("adaptiveRange", false);
        defaults.put("scrollDown", false);
        defaults.put("pixelDuration", TimeDuration.ofMillis(10));
        return new WaterfallPlotParameters(defaults);
    }
    
    InternalCopy internalCopy() {
        return new InternalCopy(getParameters());
    }
    
    /**
     * Height of the plot in pixels.
     * 
     * @param height height of the plot in pixels
     * @return a new parameter
     */
    public static WaterfallPlotParameters height(int height) {
        return new WaterfallPlotParameters("height", height);
    }

    /**
     * The color scheme to convert a value to an rgb pixel in the plot.
     * 
     * @param colorScheme color scheme
     * @return a new parameter
     */
    public static WaterfallPlotParameters colorScheme(ColorScheme colorScheme) {
        return new WaterfallPlotParameters("colorScheme", colorScheme);
    }

    /**
     * True if the range for the display should be the automatically
     * determined or should be desumed by the data.
     * 
     * @param adaptiveRange true automatic range, false use the data form the channel
     * @return  a new parameter
     */
    public static WaterfallPlotParameters adaptiveRange(boolean adaptiveRange) {
        return new WaterfallPlotParameters("adaptiveRange", adaptiveRange);
    }

    /**
     * Whether the plot should scroll down or up.
     * 
     * @param scrollDown true if scrolls down (current value at the top),
     * false if scrolls down (current value at the bottom)
     * @return a new parameter
     */
    public static WaterfallPlotParameters scrollDown(boolean scrollDown) {
        return new WaterfallPlotParameters("scrollDown", scrollDown);
    }

    /**
     * How much time should be allocated to each line of the plot.
     * 
     * @param pixelDuration amount of time for each pixel
     * @return a new parameter
     */
    public static WaterfallPlotParameters pixelDuration(TimeDuration pixelDuration) {
        return new WaterfallPlotParameters("pixelDuration", pixelDuration);
    }

    /**
     * The background color for the plot.
     * 
     * @param rgb background color for the plot
     * @return a new parameter
     */
    public static WaterfallPlotParameters backgroundColor(int rgb) {
        return new WaterfallPlotParameters("backgroundColor", rgb);
    }
    
    /**
     * Returns a new set of parameters with the given changes;
     * 
     * @param newParameters parameters to change
     * @return a new set of parameters
     */
    public WaterfallPlotParameters with(WaterfallPlotParameters... newParameters) {
        return new WaterfallPlotParameters(this, newParameters);
    }

    /**
     * The height of the plot in pixels.
     * 
     * @return height of the plot
     */
    public int getHeight() {
        return internalCopy().height;
    }

    /**
     * True if range is automatic, false if taken from data.
     * 
     * @return true if using automatic range
     */
    public boolean isAdaptiveRange() {
        return internalCopy().adaptiveRange;
    }
    
    /**
     * Whether the plot scrolls up or down.
     * 
     * @return true if scrolls down
     */
    public boolean isScrollDown() {
        return internalCopy().scrollDown;
    }

    /**
     * Color scheme used for the plot.
     * 
     * @return a color scheme
     */
    public ColorScheme getColorScheme() {
        return internalCopy().colorScheme;
    }
    
    /**
     * Time interval for each line.
     * 
     * @return the duration for each line
     */
    public TimeDuration getPixelDuration() {
        return internalCopy().pixelDuration;
    }
    
}
