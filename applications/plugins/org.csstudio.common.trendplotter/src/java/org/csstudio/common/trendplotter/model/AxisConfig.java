/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.model;

import java.io.PrintWriter;

import org.csstudio.apputil.xml.DOMHelper;
import org.csstudio.apputil.xml.XMLWriter;
import org.eclipse.swt.graphics.RGB;
import org.w3c.dom.Element;

/** Information about configuration of an axis
 *  @author Kay Kasemir
 */
public class AxisConfig
{
    /** Model to which this axis belongs */
    private Model model = null;
    
    /** Visible? */
    private boolean visible;
    
    /** Name, axis label */
    private String name;
    
    /** Color */
    private RGB rgb;
    
    /** Axis range */
    private double min, max;

    /** Auto-scale? */
    private boolean auto_scale;

    /** Logarithmic scale? */
    private boolean log_scale;

    /** Initialize
     * @param name
     *  @param rgb
     *  @param min
     *  @param max
     *  @param auto_scale 
     *  @param log_scale
     */
    public AxisConfig(final Boolean visible, final String name, final RGB rgb, final double min,
            final double max, final boolean auto_scale, final boolean log_scale)
    {
    	this.visible = visible;
        this.name = name;
        this.rgb = rgb;
        this.min = min;
        this.max = max;
        this.auto_scale = auto_scale;
        this.log_scale = log_scale;
    }

    /** Initialize with defaults
     *  @param name
     */
    public AxisConfig(final String name)
    {
        this(true, name, new RGB(0, 0, 0), 0.0, 10.0, false, false);
    }

    /** @param model Model to which this item belongs */
    void setModel(final Model model)
    {
        this.model = model;
    }

    /** @return <code>true</code> if axis should be displayed */
    public boolean isVisible()
    {
		return visible;
	}

    /** @param visible Should axis be displayed? */
	public void setVisible(final boolean visible)
	{
		this.visible = visible;
        fireAxisChangeEvent();
	}

	/** @return Axis title */
    public String getName()
    {
        return name;
    }

    /** @param name New axis title */
    public void setName(final String name)
    {
        this.name = name;
        fireAxisChangeEvent();
    }

    /** @return Color */
    public RGB getColor()
    {
        return rgb;
    }

    /** @param color New color */
    public void setColor(final RGB color)
    {
        rgb = color;
        fireAxisChangeEvent();
    }

    /** @return Axis range minimum */
    public double getMin()
    {
        return min;
    }

    /** @return Axis range maximum */
    public double getMax()
    {
        return max;
    }
    
    /** @param limit New axis range maximum */
    public void setRange(final double min, final double max)
    {
        // Ignore empty range
        if (min == max)
            return;
        // Assert min is below max
        if (min < max)
        {
            this.min = min;
            this.max = max;
        }
        else
        {
            this.min = max;
            this.max = min;
        }
        fireAxisChangeEvent();
    }

    /** @return Auto-scale? */
    public boolean isAutoScale()
    {
        return auto_scale;
    }

    /** @param auto_scale Should axis auto-scale? */
    public void setAutoScale(final boolean auto_scale)
    {
        this.auto_scale = auto_scale;
        fireAxisChangeEvent();
    }
    
    /** @return Logarithmic scale? */
    public boolean isLogScale()
    {
        return log_scale;
    }

    /** @param log_scale Use logarithmic scale? */
    public void setLogScale(final boolean log_scale)
    {
        this.log_scale = log_scale;
        fireAxisChangeEvent();
    }

    /** Notify model about changes */
    private void fireAxisChangeEvent()
    {
        if (model != null)
            model.fireAxisChangedEvent(this);
    }

    /** Write XML formatted axis configuration
     *  @param writer PrintWriter
     */
    public void write(final PrintWriter writer)
    {
        XMLWriter.start(writer, 2, Model.TAG_AXIS);
        writer.println();
        XMLWriter.XML(writer, 3, Model.TAG_NAME, name);
        XMLWriter.start(writer, 3, Model.TAG_COLOR);
        writer.println();
        XMLWriter.XML(writer, 4, Model.TAG_RED, rgb.red);
        XMLWriter.XML(writer, 4, Model.TAG_GREEN, rgb.green);
        XMLWriter.XML(writer, 4, Model.TAG_BLUE, rgb.blue);
        XMLWriter.end(writer, 3, Model.TAG_COLOR);
        writer.println();
        XMLWriter.XML(writer, 3, Model.TAG_MIN, min);
        XMLWriter.XML(writer, 3, Model.TAG_MAX, max);
        XMLWriter.XML(writer, 3, Model.TAG_LOG_SCALE, Boolean.toString(log_scale));
        XMLWriter.XML(writer, 3, Model.TAG_AUTO_SCALE, Boolean.toString(auto_scale));
        XMLWriter.XML(writer, 3, Model.TAG_VISIBLE, Boolean.toString(visible));
        XMLWriter.end(writer, 2, Model.TAG_AXIS);
        writer.println();
    }

    /** Create Axis info from XML document
     *  @param  node
     *  @return AxisConfig
     *  @throws Exception on error
     */
    public static AxisConfig fromDocument(final Element node)  throws Exception
    {
        final String name = DOMHelper.getSubelementString(node, Model.TAG_NAME);
        final double min = DOMHelper.getSubelementDouble(node, Model.TAG_MIN, 0.0);
        final double max = DOMHelper.getSubelementDouble(node, Model.TAG_MAX, 10.0);
        final boolean auto_scale = DOMHelper.getSubelementBoolean(node, Model.TAG_AUTO_SCALE, false);
        final boolean log_scale = DOMHelper.getSubelementBoolean(node, Model.TAG_LOG_SCALE, false);
    	final boolean visible = DOMHelper.getSubelementBoolean(node, Model.TAG_VISIBLE, true);
        RGB rgb = Model.loadColorFromDocument(node);
        if (rgb == null)
            rgb = new RGB(0, 0, 0);
        return new AxisConfig(visible, name, rgb, min, max, auto_scale, log_scale);
    }
    
    /** @return String representation for debugging */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "Axis '" + name + "', range " + min + " ... " + max + ", " + rgb.toString();
    }

    /** @return Copied axis configuration. Not associated with a model */
    public AxisConfig copy()
    {
        return new AxisConfig(visible, name, rgb, min, max, auto_scale, log_scale);
    }
}
