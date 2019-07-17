/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;


import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.text.DecimalFormat;

import org.csstudio.swt.widgets.introspection.Introspectable;
import org.csstudio.swt.widgets.introspection.ScaleWidgetIntrospector;
import org.csstudio.swt.xygraph.linearscale.AbstractScale;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Base figure for a widget based on {@link AbstractScaledWidgetModel}.
 *
 * @author Xihui Chen
 *
 */
public abstract class AbstractScaledWidgetFigure extends Figure implements Introspectable{

    protected AbstractScale scale;

    protected boolean transparent = true;

    protected double value = 50;

    protected double minimum = 0;

    protected double maximum = 100;

    protected int majorTickMarkStepHint =30;

    protected boolean showMinorTicks = true;

    protected boolean showScale = true;

    protected boolean logScale = false;

    protected String valueLabelFormat = ""; //$NON-NLS-1$

    @Override
    public BeanInfo getBeanInfo() throws IntrospectionException {
        return new ScaleWidgetIntrospector().getBeanInfo(this.getClass());
    }

    /**
     * @return the coerced value of the widget based on the scale range.
     */
    public double getCoercedValue(){
        return getCoercedValue(value);
    }

    /**Coerce a value into range of the scale.
     * @param v the value to be coerced.
     * @return the coerced value
     */
    public double getCoercedValue(double v){
        Range range = scale.getRange();
        if(range.inRange(v))
            return v;
        else {
            if(range.getUpper() >= range.getLower())
                return v > range.getUpper()? range.getUpper() : range.getLower();
            else
                return v > range.getLower()?range.getLower(): range.getUpper();
        }
    }
    /**
     * @return the majorTickMarkStepHint
     */
    public int getMajorTickMarkStepHint() {
        return majorTickMarkStepHint;
    }
    /**
     * @return the maximum
     */
    public double getMaximum() {
        return maximum;
    }

    /**
     * @return the minimum
     */
    public double getMinimum() {
        return minimum;
    }

    public Range getRange(){
        return new Range(minimum, maximum);
    }

    /**
     * @return the scale
     */
    public AbstractScale getScale() {
        return scale;
    }

    public double getValue() {
        return value;
    }

    public String getValueLabelFormat() {
        return valueLabelFormat;
    }

    /**
     * @return the value text after format.
     */
    public String getValueText(){
        if(valueLabelFormat.trim().equals("")){ //$NON-NLS-1$
            return getScale().format(getValue());
        }else {
            return new DecimalFormat(valueLabelFormat).format(getValue());
        }

    }

    /**
     * @return the logScale
     */
    public boolean isLogScale() {
        return logScale;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }


    /**
     * @return the showMinorTicks
     */
    public boolean isShowMinorTicks() {
        return showMinorTicks;
    }

    /**
     * @return the showScale
     */
    public boolean isShowScale() {
        return showScale;
    }

    /**
     * @return the transparent
     */
    public boolean isTransparent() {
        return transparent;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void paintFigure(final Graphics graphics) {
        if (!transparent) {
            graphics.setBackgroundColor(this.getBackgroundColor());
            Rectangle bounds = this.getBounds().getCopy();
            bounds.crop(this.getInsets());
            graphics.fillRectangle(bounds);
        }
        super.paintFigure(graphics);
    }
    /**
     * @param logScale the logScale to set
     */
    public void setLogScale(final boolean logScale) {
        if(this.logScale == logScale)
            return;
        this.logScale = logScale;
        scale.setLogScale(logScale);
        scale.setRange(new Range(minimum, maximum));
        repaint();
    }
    /**
     * @param majorTickMarkStepHint the majorTickMarkStepHint to set
     */
    public void setMajorTickMarkStepHint(int majorTickMarkStepHint) {
        if(this.majorTickMarkStepHint == majorTickMarkStepHint || majorTickMarkStepHint <=0)
            return;
        this.majorTickMarkStepHint = majorTickMarkStepHint;
        scale.setMajorTickMarkStepHint(majorTickMarkStepHint);
        repaint();
    }
    /**
     * set the range of the scale
     * @param min
     * @param max
     */
    public void setRange(final double min, final double max) {
        scale.setRange(min, max);
        this.maximum = scale.getRange().getUpper();
        this.minimum = scale.getRange().getLower();
        repaint();
    }
    public void setRange(Range range){
        setRange(range.getLower(), range.getUpper());
    }

    /**
     * @param scale the scale to set
     */
    public void setScale(AbstractScale scale) {
        this.scale = scale;
    }
    /**
     * @param showMinorTicks the showMinorTicks to set
     */
    public void setShowMinorTicks(final boolean showMinorTicks) {
        if(this.showMinorTicks == showMinorTicks)
            return;
        this.showMinorTicks = showMinorTicks;
        scale.setMinorTicksVisible(showMinorTicks);
        repaint();
    }
    /**
     * @param showScale the showScale to set
     */
    public void setShowScale(final boolean showScale) {
        if(this.showScale == showScale)
            return;
        this.showScale = showScale;
        scale.setVisible(showScale);
        repaint();
    }
    /**
     * Sets, if this widget should have a transparent background.
     * @param transparent
     *                 The new value for the transparent property
     */
    public void setTransparent(final boolean transparent) {
        if(this.transparent == transparent)
            return;
        this.transparent = transparent;
        repaint();
    }
    /**
     * @param value the value to set
     */
    public void setValue(final double value) {
        this.value = value;
        repaint();
    }

    /**
     * @param valueLabelFormat the numeric format pattern for value label.
     */
    public void setValueLabelFormat(String valueLabelFormat) {
         try {
                 new DecimalFormat(valueLabelFormat);
             } catch (NullPointerException e) {
                 throw e;
             } catch (IllegalArgumentException e){
                 throw e;
             }
        this.valueLabelFormat = valueLabelFormat;
        setValue(value);
    }


}
