/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
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
    private ValueScale xValueScale;
    private ValueScale yValueScale;
    
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
    
    public T xValueScale(ValueScale xValueScale) {
        this.xValueScale = xValueScale;
        return self();
    }
    
    public T yValueScale(ValueScale yValueScale) {
        this.yValueScale = yValueScale;
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

    public ValueScale getXValueScale() {
        return xValueScale;
    }

    public ValueScale getYValueScale() {
        return yValueScale;
    }
    
    
}
