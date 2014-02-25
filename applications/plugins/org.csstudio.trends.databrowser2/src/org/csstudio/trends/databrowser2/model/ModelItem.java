/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import java.io.PrintWriter;

import org.csstudio.apputil.xml.DOMHelper;
import org.csstudio.apputil.xml.XMLWriter;
import org.csstudio.trends.databrowser2.preferences.Preferences;
import org.eclipse.swt.graphics.RGB;
import org.w3c.dom.Element;

/** Base of {@link PVItem} and {@link FormulaItem},
 *  i.e. the items held by the {@link Model}.
 *
 *  @author Kay Kasemir
 */
abstract public class ModelItem implements Cloneable
{	
    /** Name by which the item is identified: PV name, formula */
    private String name;

    /** Model that contains this item or <code>null</code> while not
     *  assigned to a model
     */
    protected Model model = null;

    /** Preferred display name, used in plot legend */
    private String display_name;

    /** Show item's samples? */
    private boolean visible = true;

    /** RGB for item's color
     *  <p>
     *  Technically, swt.graphics.RGB adds a UI dependency to the Model.
     *  As long as the Model can still run without a Display
     *  or Shell, this might be OK.
     */
    private RGB rgb = null;

    /** Line width [pixel] */
    private int line_width = Preferences.getLineWidths();

    /** How to display the trace */
    private TraceType trace_type = Preferences.getTraceType();

    /** Y-Axis */
    private AxisConfig axis = null;

    /** Initialize
     *  @param name Name of the PV or the formula
     */
    public ModelItem(final String name)
    {
        this.name = name;
        this.display_name = name;
    }

    /** @return Model that contains this item */
    public Model getModel()
    {
        return model;
    }

    /** Called by Model to add item to Model or remove it from model.
     *  Should not be called by other code!
     *  @param model Model to which item was added or <code>null</code> when removed
     */
    void setModel(final Model model)
    {
        if (this.model == model)
            throw new RuntimeException("Item re-assigned to same model: " + name); //$NON-NLS-1$
        this.model = model;
    }

    /** @return Name of this item (PV, Formula, ...), may contain macros */
    public String getName()
    {
        return name;
    }

    /** @return Name of this item (PV, Formula, ...) with all macros resolved */
    public String getResolvedName()
    {
    	if (model == null)
    		return name;
        return model.resolveMacros(name);
    }

    /** @param new_name New item name
     *  @see #getName()
     *  @return <code>true</code> if name was actually changed
     *  @throws Exception on error (cannot create PV for new name, ...)
     *
     */
    public boolean setName(String new_name) throws Exception
    {
        new_name = new_name.trim();
        if (new_name.equals(name))
            return false;
        name = new_name;
        fireItemLookChanged();
        return true;
    }

    /** @return Preferred display name, used in plot legend.
     *  May contain macros.
     */
    public String getDisplayName()
    {
        return display_name;
    }

    /** @return Preferred display name, used in plot legend,
     *          with macros resolved.
     */
    public String getResolvedDisplayName()
    {
    	if (model == null)
    		return display_name;
    	return model.resolveMacros(display_name);
    }

    /** @param new_display_name New display name
     *  @see #getDisplayName()
     */
    public void setDisplayName(String new_display_name)
    {
        new_display_name = new_display_name.trim();
        if (new_display_name.equals(display_name))
            return;
        display_name = new_display_name;
        fireItemLookChanged();
    }

    /** @return <code>true</code> if item should be displayed */
    public boolean isVisible()
    {
        return visible;
    }

    /** @param visible Should item be displayed? */
    public void setVisible(final boolean visible)
    {
        if (this.visible == visible)
            return;
        this.visible = visible;
        if (model != null)
            model.fireItemVisibilityChanged(this);
    }

    /** If (!) assigned to a model, inform it about a configuration change */
    protected void fireItemLookChanged()
    {
    	if (model != null)
            model.fireItemLookChanged(this);
    }

    /** Get item's color.
     *  For new items, the color is <code>null</code> until it's
     *  either set via setColor() or by adding it to a {@link Model}.
     *  @return Item's color
     *  @see #setColor(RGB)
     */
    public RGB getColor()
    {
        return rgb;
    }

    /** @param new_rgb New color for this item */
    public void setColor(final RGB new_rgb)
    {
        if (new_rgb.equals(rgb))
            return;
        rgb = new_rgb;
        fireItemLookChanged();
    }

    /** @return Line width */
    public int getLineWidth()
    {
        return line_width;
    }

    /** @param width New line width */
    public void setLineWidth(int width)
    {
        if (width < 0)
            width = 0;
        if (width == this.line_width)
            return;
        line_width = width;
        fireItemLookChanged();
    }

    /** @return {@link TraceType} for displaying the trace */
    public TraceType getTraceType()
    {
        return trace_type;
    }

    /** @param trace_type New {@link TraceType} for displaying the trace */
    public void setTraceType(final TraceType trace_type)
    {
        if (this.trace_type == trace_type)
            return;
        this.trace_type = trace_type;
        fireItemLookChanged();
    }

    /** @return Y-Axis */
    public AxisConfig getAxis()
    {
        return axis;
    }

    /** @return Index of Y-Axis in model */
    public int getAxisIndex()
    {   // Allow this to work in Tests w/o model
        if (model == null)
            return 0;
        return model.getAxisIndex(axis);
    }

    /** @param axis New X-Axis index */
    public void setAxis(final AxisConfig axis)
    {   // Comparing exact AxisConfig reference, not equals()!
    	if (axis == this.axis)
            return;
        this.axis = axis;
        fireItemLookChanged();
    }

    /**
     * This method should be overridden if the instance needs
     * to change its behavior according to waveform index.
     * If it is not overridden, this method always return 0.
     * @return Waveform index */
    public int getWaveformIndex()
    {
        return 0;
    }

    /**
     * This method should be overridden if the instance needs
     * to change its behavior according to waveform index.
     * @param index New waveform index */
    public void setWaveformIndex(int index)
    {
    	// Do nothing.
    }

    /** @return Samples held by this item */
    abstract public PlotSamples getSamples();

    @Override
    public String toString()
    {
        return name;
    }

    /** Write XML formatted item configuration
     *  @param writer PrintWriter
     */
    abstract public void write(final PrintWriter writer);

    /** Write XML configuration common to all Model Items
     *  @param writer PrintWriter
     */
    protected void writeCommonConfig(final PrintWriter writer)
    { 
    	XMLWriter.XML(writer, 3, Model.TAG_NAME, getName());
        XMLWriter.XML(writer, 3, Model.TAG_VISIBLE, Boolean.toString(isVisible()));
        XMLWriter.XML(writer, 3, Model.TAG_AXIS, model.getAxisIndex(getAxis()));
        XMLWriter.XML(writer, 3, Model.TAG_WAVEFORM_INDEX, getWaveformIndex());
    	//other settings are included in the graph settings   
//        XMLWriter.XML(writer, 3, Model.TAG_DISPLAYNAME, getDisplayName());
//        XMLWriter.XML(writer, 3, Model.TAG_VISIBLE, Boolean.toString(isVisible()));
//        XMLWriter.XML(writer, 3, Model.TAG_AXIS, model.getAxisIndex(getAxis()));
//        XMLWriter.XML(writer, 3, Model.TAG_WAVEFORM_INDEX, getWaveformIndex());
    }

    /** Load common XML configuration elements into this item
     *  @param model Model to which this item will belong (but doesn't, yet)
     *  @param node XML document node for this item
     */
    protected void configureFromDocument(final Model model, final Element node)
    {
        display_name = DOMHelper.getSubelementString(node, Model.TAG_DISPLAYNAME, display_name);
        visible = DOMHelper.getSubelementBoolean(node, Model.TAG_VISIBLE, true);
        // Ideally, configuration should define all axes before they're used,
        // but as a fall-back create missing axes
        final int axis_index = DOMHelper.getSubelementInt(node, Model.TAG_AXIS, 0);
        while (model.getAxisCount() <= axis_index)
            model.addAxis(display_name);
        axis = model.getAxis(axis_index);
        line_width = DOMHelper.getSubelementInt(node, Model.TAG_LINEWIDTH, line_width);
        rgb = Model.loadColorFromDocument(node);
        final String type = DOMHelper.getSubelementString(node, Model.TAG_TRACE_TYPE, TraceType.AREA.name());
        try
        {
            trace_type = TraceType.valueOf(type);
        }
        catch (Throwable ex)
        {
            trace_type = TraceType.AREA;
        }

        final int waveform_index = DOMHelper.getSubelementInt(node, Model.TAG_WAVEFORM_INDEX, 0);

        // If this method is overridden by the child class, the child's method will be called
        // to set the waveform index. If it is not overridden, ModelItem's method will be called,
        // which does nothing.
        setWaveformIndex(waveform_index);
    }
    
	public ModelItem clone() {
		try {
			ModelItem ret = (ModelItem)super.clone();
			ret.name = name;
			ret.model = model;
			ret.display_name = display_name;
			ret.visible = visible;
			ret.rgb = rgb;
			ret.line_width = line_width;
			ret.trace_type = trace_type;
			ret.axis = axis;
			return ret;
		} catch (CloneNotSupportedException ex) {
			throw new RuntimeException(ex);
		}
	}
}
