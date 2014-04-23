/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public class TemporalGraph2DRendererUpdate<T extends TemporalGraph2DRendererUpdate<T>> {
    
    private Integer imageHeight;
    private Integer imageWidth;
    private AxisRange axisRange;
    private TimeAxisRange timeAxisRange;
    private ValueScale valueScale;
    private TimeScale timeScale;
    
    protected T self() {
        return (T) this;
    }
    
    public T imageHeight(int height) {
        this.imageHeight = height;
        return self();
    }
    
    public T imageWidth(int width) {
        this.imageWidth = width;
        return self();
    }
    
    public T axisRange(AxisRange axisRange) {
        this.axisRange = axisRange;
        return self();
    }
    
    public T timeAxisRange(TimeAxisRange timeAxisRange) {
        this.timeAxisRange = timeAxisRange;
        return self();
    }
    
    public T valueScale(ValueScale valueScale) {
        this.valueScale = valueScale;
        return self();
    }
    
    public T timeScale(TimeScale timeScale) {
        this.timeScale = timeScale;
        return self();
    }
    
    public Integer getImageHeight() {
        return imageHeight;
    }

    public Integer getImageWidth() {
        return imageWidth;
    }

    public AxisRange getAxisRange() {
        return axisRange;
    }

    public TimeAxisRange getTimeAxisRange() {
        return timeAxisRange;
    }

    public ValueScale getValueScale() {
        return valueScale;
    }

    public TimeScale getTimeScale() {
        return timeScale;
    }
    
    
}
