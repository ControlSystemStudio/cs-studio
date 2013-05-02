/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.model;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import org.csstudio.apputil.xml.DOMHelper;
import org.csstudio.apputil.xml.XMLWriter;
import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider;
import org.csstudio.swt.xygraph.dataprovider.IDataProvider;
import org.csstudio.trends.sscan.preferences.Preferences;
import org.eclipse.swt.graphics.RGB;
import org.w3c.dom.Element;

/** Base of {@link ScanItem} and {@link FormulaItem},
 *  i.e. the items held by the {@link Model}.
 * 
 *  @author Kay Kasemir
 */
public class ModelItem
{
    /** Name by which the item is identified: PV name, formula */
    private String name;

    /** Model that contains this item or <code>null</code> while not
     *  assigned to a model
     */
    protected Model model = null;

    CircularBufferDataProvider traceDataProvider = new CircularBufferDataProvider(false);
    
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
    private TraceType trace_type = TraceType.SINGLE_LINE;

    /** Axes Positioner Detector*/
    private AxesConfig axes = null;
    private Positioner positioner = null;
    private Detector detector = null;
    private Sscan sscan = null;
    
    /** Initialize
     *  @param name Name of the PV or the formula
     */
    public ModelItem(final String name)
    {
        this.name = name;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy k:m:s");
        this.display_name = name+": "+dateFormat.format(calendar.getTime());
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
       System.out.println("ModelItem.fireItemLookChanged()");
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
    
    /** @return Axes */
    public AxesConfig getAxes()
    {
        return axes;
    }

    /** @return Index of Axes in model */
    public int getAxesIndex()
    {   // Allow this to work in Tests w/o model
        if (model == null)
            return 0;
        return model.getAxesIndex(axes);
    }

    /** @param axis Axis index */
    public void setAxes(final AxesConfig axes)
    {   // Comparing exact AxisConfig reference, not equals()!
        System.out.println("ModelItem.setAxis() " + axes.getName());
    	
    	if (axes == this.axes)
            return;
        this.axes = axes;
        
   
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
    public IDataProvider getLiveSamples()
    {
    	traceDataProvider.setBufferSize(1000);
    	traceDataProvider.setConcatenate_data(false);
    	if(this.getPositioner().getCurrentData()!=null && this.getDetector().getCurrentData()!=null){
    		traceDataProvider.setCurrentXDataArray((double[]) resizeArray(this.getPositioner().getCurrentData(),this.getPositioner().getCurrentPoint()));
    		traceDataProvider.setCurrentYDataArray((double[]) resizeArray(convertFloatsToDoubles(this.getDetector().getCurrentData()),this.getPositioner().getCurrentPoint()));
    	}else{
    		double[] zero = {0};
    		traceDataProvider.setCurrentXDataArray(zero);
    		traceDataProvider.setCurrentYDataArray(zero);
    	}
		
		
		return traceDataProvider;
    }
    
    public IDataProvider getSamples()
    {
    	return traceDataProvider;
    }
    
    /**
    * Reallocates an array with a new size, and copies the contents
    * of the old array to the new array.
    * @param oldArray  the old array, to be reallocated.
    * @param newSize   the new array size.
    * @return          A new array with the same contents.
    */
    private static Object resizeArray (Object oldArray, int newSize) {
       int oldSize = java.lang.reflect.Array.getLength(oldArray);
       Class elementType = oldArray.getClass().getComponentType();
       Object newArray = java.lang.reflect.Array.newInstance(
             elementType,newSize);
       int preserveLength = Math.min(oldSize,newSize);
       if (preserveLength > 0)
          System.arraycopy (oldArray,0,newArray,0,preserveLength);
       return newArray; }
    
	private static double[] convertFloatsToDoubles(float[] input)
	{
	    if (input == null)
	    {
	        return null; // Or throw an exception - your choice
	    }
	    double[] output = new double[input.length];
	    for (int i = 0; i < input.length; i++)
	    {
	        output[i] = input[i];
	    }
	    return output;
	}
    
    @Override
    public String toString()
    {
        return name;
    }

    /** Write XML formatted item configuration
     *  @param writer PrintWriter
     */
    public void write(final PrintWriter writer)
    {};

    /** Write XML configuration common to all Model Items
     *  @param writer PrintWriter
     */
    protected void writeCommonConfig(final PrintWriter writer)
    {
        XMLWriter.XML(writer, 3, Model.TAG_NAME, getName());
        XMLWriter.XML(writer, 3, Model.TAG_DISPLAYNAME, getDisplayName());
        XMLWriter.XML(writer, 3, Model.TAG_VISIBLE, Boolean.toString(isVisible()));
        XMLWriter.XML(writer, 3, Model.TAG_AXIS, model.getAxesIndex(getAxes()));
        XMLWriter.XML(writer, 3, Model.TAG_LINEWIDTH, getLineWidth());
        Model.writeColor(writer, 3, Model.TAG_COLOR, getColor());
        XMLWriter.XML(writer, 3, Model.TAG_TRACE_TYPE, getTraceType().name());
        XMLWriter.XML(writer, 3, Model.TAG_WAVEFORM_INDEX, getWaveformIndex());
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
        while (model.getAxesCount() <= axis_index)
            model.addAxes();
        axes = model.getAxes(axis_index);
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

	public Positioner getPositioner() {
            return positioner;
	}
	
	public int getPositionerIndex() {
		if (sscan == null)
            return 0;
        return sscan.getPositionerIndex(positioner);
	}
	
    public void setPositioner(final Positioner positioner)
    {   // Comparing exact AxisConfig reference, not equals()!
        System.out.println("ModelItem.setPositioner " + positioner.getPositionerPV());
    	
    	if (positioner == this.positioner)
            return;
        this.positioner = positioner;

        fireItemLookChanged();
    }

	public Detector getDetector() {
        return detector;
	}
	
	public int getDetectorIndex() {
		if (sscan == null)
            return 0;
        return sscan.getDetectorIndex(detector);
	}


	public void setDetector(Detector detector) {
		// Comparing exact AxisConfig reference, not equals()!
        System.out.println("ModelItem.setDetector " + detector.getDetectorPV());
    	
    	if (detector == this.detector)
            return;
        this.detector = detector;

        fireItemLookChanged();
	}
	
	public int getSscanIndex() {
		if (model == null)
            return 0;
        return model.getSscanIndex(sscan);
	}

	public void setSscan(Sscan sscan) {
		// Comparing exact AxisConfig reference, not equals()!
        System.out.println("ModelItem.setSscan() " + sscan.getName());
    	
    	if (sscan == this.sscan)
            return;
        this.sscan = sscan;

        //fireItemLookChanged();
	}

	public Sscan getSscan() {
		return sscan;
	}

	public static ModelItem fromDocument(Model model2, Element item) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
