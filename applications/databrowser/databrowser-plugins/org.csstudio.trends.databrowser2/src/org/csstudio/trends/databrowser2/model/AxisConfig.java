/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import java.io.PrintWriter;
import java.util.Objects;
import java.util.Optional;

import org.csstudio.apputil.xml.DOMHelper;
import org.csstudio.apputil.xml.XMLWriter;
import org.csstudio.swt.rtplot.SWTMediaPool;
import org.csstudio.trends.databrowser2.persistence.XMLPersistence;
import org.csstudio.trends.databrowser2.preferences.Preferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.w3c.dom.Element;

import static org.csstudio.trends.databrowser2.persistence.XMLPersistence.*;

/** Information about configuration of an axis
 *  @author Kay Kasemir
 */
public class AxisConfig
{
    /** Model to which this axis belongs */
    private Optional<Model> model = Optional.empty();

    /** Visible? */
    private boolean visible;

    /** Name, axis label */
    private String name;

    /** Use axis name as axis label? */
    private boolean use_axis_name = true;

    /** Use trace names as axis label? */
    private boolean use_trace_names = true;

    /** Is axis on right side of plot? */
    private boolean is_right = false;

    /** Color */
    private RGB color;
    
    /** Label Font */
    private FontData label_font;

    /** Scale Font */
    private FontData scale_font;

    /** Axis range */
    private double min, max;

    /** Show grid line? */
    private boolean show_grid;

    /** Auto-scale? */
    private boolean auto_scale;

    /** Logarithmic scale? */
    private boolean log_scale;

    /** Initialize with defaults
     *  @param name
     */
    public AxisConfig(final String name)
    {
        this(true, name, true, Preferences.useTraceNames(), false, new RGB(0, 0, 0), new FontData("", 10, 0), new FontData("", 10, 0),0.0, 10.0, false, Preferences.useAutoScale(), false);
    }

    /** Initialize
     *  @param visible
     *  @param name
     *  @param use_axis_name
     *  @param use_trace_names
     *  @param is_right
     *  @param rgb
     *  @param min
     *  @param max
     *  @param auto_scale
     *  @param log_scale
     */
    public AxisConfig(final boolean visible, final String name,
            final boolean use_axis_name,
            final boolean use_trace_names,
            final boolean is_right,
            final RGB rgb,
            final FontData label_font,
            final FontData scale_font,
            final double min,
            final double max,
            final boolean show_grid,
            final boolean auto_scale,
            final boolean log_scale)
    {
        this.visible = visible;
        this.name = Objects.requireNonNull(name);
        this.use_axis_name = use_axis_name;
        this.use_trace_names = use_trace_names;
        this.is_right = is_right;
        this.color = Objects.requireNonNull(rgb);
        this.label_font = label_font;
        this.scale_font = scale_font;
        this.min = min;
        this.max = max;
        this.show_grid = show_grid;
        this.auto_scale = auto_scale;
        this.log_scale = log_scale;
    }

    /** @param model Model to which this item belongs */
    void setModel(final Model model)
    {
        this.model = Optional.ofNullable(model);
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

    /** @return Axis title, may include macros */
    public String getName()
    {
        return name;
    }

    /** @return Axis title, macros have been resolved */
    public String getResolvedName()
    {
        if (model.isPresent())
            return model.get().resolveMacros(name);
        else
            return name;
    }

    /** @param name New axis title */
    public void setName(final String name)
    {
        this.name = name;
        fireAxisChangeEvent();
    }

    /** @return <code>true</code>if axis name is used */
    public boolean isUsingAxisName()
    {
        return use_axis_name;
    }

    /** @param use_axis_name If <code>true</code>, show axis name */
    public void useAxisName(final boolean use_axis_name)
    {
        this.use_axis_name = use_axis_name;
        fireAxisChangeEvent();
    }

    /** @return <code>true</code> if using trace names as axis label */
    public boolean isUsingTraceNames()
    {
        return use_trace_names;
    }

    /** @param use_trace_names <code>true</code> to use trace names as axis label */
    public void useTraceNames(final boolean use_trace_names)
    {
        this.use_trace_names = use_trace_names;
        fireAxisChangeEvent();
    }

    /** Is axis on right side of plot? */
    public boolean isOnRight()
    {
        return is_right;
    }

    /** @param is_right Is axis on right side of plot? */
    public void setOnRight(final boolean is_right)
    {
        this.is_right = is_right;
        fireAxisChangeEvent();
    }

    /** @return Color */
    public RGB getColor()
    {
        return color;
    }

    /** @param color New color */
    public void setColor(final RGB color)
    {
        this.color = Objects.requireNonNull(color);
        fireAxisChangeEvent();
    }

    public FontData getLabelFont() {
        return label_font;
    }

    public void setLabelFont(FontData label_font) {
        this.label_font = label_font;
        fireAxisChangeEvent();
    }

    public FontData getScaleFont() {
        return scale_font;
    }

    public void setScaleFont(FontData scale_font) {
        this.scale_font = scale_font;
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

    /** @param min New axis range maximum
     *  @param max New axis range maximum
     */
    public void setRange(final double min, final double max)
    {
        // Ignore empty range
        if (min == max) return;
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

    /** @return <code>true</code> if grid lines are drawn */
    public boolean isGridVisible()
    {
        return show_grid;
    }

    /** @param visible Should grid be visible? */
    public void setGridVisible(final boolean grid)
    {
        show_grid = grid;
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
        if (model.isPresent())
            model.get().fireAxisChangedEvent(Optional.of(this));
    }

    /** Write XML formatted axis configuration
     *  @param writer PrintWriter
     */
    public void write(final PrintWriter writer)
    {
        XMLWriter.start(writer, 2, XMLPersistence.TAG_AXIS);
        writer.println();
        XMLWriter.XML(writer, 3, XMLPersistence.TAG_VISIBLE, Boolean.toString(visible));
        XMLWriter.XML(writer, 3, XMLPersistence.TAG_NAME, name);
        XMLWriter.XML(writer, 3, XMLPersistence.TAG_USE_AXIS_NAME, Boolean.toString(use_axis_name));
        XMLWriter.XML(writer, 3, XMLPersistence.TAG_USE_TRACE_NAMES, Boolean.toString(use_trace_names));
        XMLWriter.XML(writer, 3, XMLPersistence.TAG_RIGHT, Boolean.toString(is_right));
        if (color != null)
            XMLPersistence.writeColor(writer, 3, XMLPersistence.TAG_COLOR, color);
        if (label_font != null)
            XMLWriter.XML(writer, 3, XMLPersistence.TAG_LABEL_FONT, SWTMediaPool.getFontDescription(label_font));
        if (scale_font != null)
            XMLWriter.XML(writer, 3, XMLPersistence.TAG_SCALE_FONT, SWTMediaPool.getFontDescription(scale_font));
        XMLWriter.XML(writer, 3, XMLPersistence.TAG_MIN, min);
        XMLWriter.XML(writer, 3, XMLPersistence.TAG_MAX, max);
        XMLWriter.XML(writer, 3, XMLPersistence.TAG_GRID, Boolean.toString(show_grid));
        XMLWriter.XML(writer, 3, XMLPersistence.TAG_AUTO_SCALE, Boolean.toString(auto_scale));
        XMLWriter.XML(writer, 3, XMLPersistence.TAG_LOG_SCALE, Boolean.toString(log_scale));

        XMLWriter.end(writer, 2, XMLPersistence.TAG_AXIS);
        writer.println();
    }

    /** Create Axis info from XML document
     *  @param node
     *  @return AxisConfig
     *  @throws Exception on error
     */
    public static AxisConfig fromDocument(final Element node) throws Exception
    {
        final boolean visible = DOMHelper.getSubelementBoolean(node, XMLPersistence.TAG_VISIBLE, true);
        final String name = DOMHelper.getSubelementString(node, XMLPersistence.TAG_NAME);
        final boolean use_axis_name = DOMHelper.getSubelementBoolean(node, XMLPersistence.TAG_USE_AXIS_NAME, true);
        final boolean use_trace_names = DOMHelper.getSubelementBoolean(node, XMLPersistence.TAG_USE_TRACE_NAMES, Preferences.useTraceNames());
        final boolean right = DOMHelper.getSubelementBoolean(node, XMLPersistence.TAG_RIGHT, false);
        final RGB rgb = XMLPersistence.loadColorFromDocument(node).orElse(new RGB(0, 0, 0));
        Element root_node = node.getOwnerDocument().getDocumentElement();
        FontData default_label_font = loadFontFromDocument(root_node, XMLPersistence.TAG_LABEL_FONT).orElse(new FontData("", 10, SWT.BOLD));
        FontData default_scale_font = loadFontFromDocument(root_node, XMLPersistence.TAG_SCALE_FONT).orElse(new FontData("", 10, SWT.BOLD));
        final FontData label_font = loadFontFromDocument(node, XMLPersistence.TAG_LABEL_FONT).orElse(default_label_font);
        final FontData scale_font = loadFontFromDocument(node, XMLPersistence.TAG_SCALE_FONT).orElse(default_scale_font);
        final double min = DOMHelper.getSubelementDouble(node, XMLPersistence.TAG_MIN, 0.0);
        final double max = DOMHelper.getSubelementDouble(node, XMLPersistence.TAG_MAX, 10.0);
        final boolean show_grid = DOMHelper.getSubelementBoolean(node, XMLPersistence.TAG_GRID, false);
        final boolean auto_scale = DOMHelper.getSubelementBoolean(node, XMLPersistence.TAG_AUTO_SCALE, Preferences.useAutoScale());
        final boolean log_scale = DOMHelper.getSubelementBoolean(node, XMLPersistence.TAG_LOG_SCALE, false);
        return new AxisConfig(visible, name, use_axis_name, use_trace_names, right, rgb, label_font, scale_font, min, max, show_grid, auto_scale, log_scale);
    }

    /** @return Copied axis configuration. Not associated with a model */
    public AxisConfig copy()
    {
        return new AxisConfig(visible, name, use_axis_name, use_trace_names,
                              is_right, color, label_font, scale_font, min, max, show_grid, auto_scale, log_scale);
    }

    /** @return String representation for debugging */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "Axis '" + name + "', range " + min + " ... " + max + ", "
                + color.toString();
    }
}
