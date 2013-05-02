/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.csstudio.apputil.macros.IMacroTableProvider;
import org.csstudio.apputil.macros.InfiniteLoopException;
import org.csstudio.apputil.macros.MacroUtil;
import org.csstudio.apputil.time.RelativeTime;
import org.csstudio.apputil.time.StartEndTimeParser;
import org.csstudio.apputil.xml.DOMHelper;
import org.csstudio.apputil.xml.XMLWriter;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.trends.sscan.Activator;
import org.csstudio.trends.sscan.Messages;
import org.csstudio.trends.sscan.preferences.Preferences;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.RGB;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Data Browser model
 *  <p>
 *  Maintains a list of {@link ModelItem}s
 *
 *  @author Kay Kasemir
 *  @author Takashi Nakamoto changed the model to accept multiple items with
 *                           the same name so that Data Browser can show the
 *                           trend of the same PV in different axes or with
 *                           different waveform indexes.
 */
@SuppressWarnings("nls")
public class Model
{
    /** File extension for data browser config files.
     *  plugin.xml registers the editor for this file extension
     */
    final public static String FILE_EXTENSION = "scn"; //$NON-NLS-1$

    // XML file tags
    final public static String TAG_SSCAN = "sscan";
    final public static String TAG_SCROLL = "scroll";
    final public static String TAG_LIVE_SAMPLE_BUFFER_SIZE = "ring_size";
    final public static String TAG_PVLIST = "pvlist";
    final public static String TAG_PV = "pv";
    final public static String TAG_NAME = "name";
    final public static String TAG_DISPLAYNAME = "display_name";
    final public static String TAG_FORMULA = "formula";
    final public static String TAG_AXES = "axes";
    final public static String TAG_AXIS = "axis";
    final public static String TAG_LINEWIDTH = "linewidth";
    final public static String TAG_COLOR = "color";
    final public static String TAG_RED = "red";
    final public static String TAG_GREEN = "green";
    final public static String TAG_BLUE = "blue";
    final public static String TAG_TRACE_TYPE = "trace_type";
    final public static String TAG_INPUT = "input";
    final public static String TAG_URL = "url";
    final public static String TAG_KEY = "key";
    final public static String TAG_XLOG_SCALE = "Xlog_scale";
    final public static String TAG_XAUTO_SCALE = "Xautoscale";
    final public static String TAG_YLOG_SCALE = "Ylog_scale";
    final public static String TAG_YAUTO_SCALE = "Yautoscale";
    final public static String TAG_XMAX = "Xmax";
    final public static String TAG_XMIN = "Xmin";
    final public static String TAG_YMAX = "Ymax";
    final public static String TAG_YMIN = "Ymin";
    final public static String TAG_BACKGROUND = "background";
    final public static String TAG_REQUEST = "request";
    final public static String TAG_VISIBLE = "visible";
  
    final public static String TAG_ANNOTATIONS = "annotations";
    final public static String TAG_ANNOTATION = "annotation";
	public static final String TAG_ANNOTATION_CURSOR_LINE_STYLE = "line_style";
	public static final String TAG_ANNOTATION_SHOW_NAME = "show_name";
	public static final String TAG_ANNOTATION_SHOW_POSITION = "show_position";
	public static final String TAG_ANNOTATION_COLOR = "color";
	public static final String TAG_ANNOTATION_FONT = "font";
	
    final public static String TAG_X = "x";
    final public static String TAG_VALUE = "value";
    final public static String TAG_WAVEFORM_INDEX = "waveform_index";
    
     
    /**AJOUT XYGraphMemento
     * @author L.PHILIPPE GANIL
     */
    final public static String TAG_TITLE = "title";
    final public static String TAG_TITLE_TEXT = "text";
    final public static String TAG_TITLE_COLOR= "color";
    final public static String TAG_TITLE_FONT ="font";
    
    public static final String TAG_FONT = "font";
	public static final String TAG_SCALE_FONT = "scale_font";
	
	final public static String TAG_X_AXIS = "x_axis";
	
	
	//GRID LINE
	public static final String TAG_GRID_LINE = "grid_line";
	public static final String TAG_SHOW_GRID_LINE = "show_grid_line";
	public static final String TAG_DASH_GRID_LINE = "dash_grid_line";
	
	//FORMAT
	public static final String TAG_FORMAT = "format";
	public static final String TAG_AUTO_FORMAT = "auto_format";
	public static final String TAG_TIME_FORMAT = "time_format";
	public static final String TAG_FORMAT_PATTERN = "format_pattern";

    
    public static final String TAG_GRAPH_SETTINGS = "graph_settings";
    public static final String TAG_SHOW_TITLE = "show_title";
    public static final String TAG_SHOW_LEGEND = "show_legend";
    public static final String TAG_SHOW_PLOT_AREA_BORDER = "show_plot_area_border";
    public static final String TAG_TRANSPARENT = "transparent";

    /** Default colors for newly added item, used over when reaching the end.
     *  <p>
     *  Very hard to find a long list of distinct colors.
     *  This list is definitely too short...
     */
    final private static RGB[] default_colors =
    {
        new RGB( 21,  21, 196), // blue
        new RGB(242,  26,  26), // red
        new RGB( 33, 179,  33), // green
        new RGB(  0,   0,   0), // black
        new RGB(128,   0, 255), // violett
        new RGB(255, 170,   0), // (darkish) yellow
        new RGB(255,   0, 240), // pink
        new RGB(243, 132, 132), // peachy
        new RGB(  0, 255,  11), // neon green
        new RGB(  0, 214, 255), // neon blue
        new RGB(114,  40,   3), // brown
        new RGB(219, 128,   4), // orange
    };

	private final PropertyChangeListener positioner_listener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			if (Arrays.asList("positioner").contains(event.getPropertyName())) {
				for(ModelItem item: items)
					fireItemDataChanged(item);
			}
		}
	};

    /** Macros */
    private IMacroTableProvider macros = null;
    
    /** Listeners to model changes */
    final private ArrayList<ModelListener> listeners = new ArrayList<ModelListener>();

    /** Axes configurations */
    final private ArrayList<AxesConfig> axesList = new ArrayList<AxesConfig>();
    
    /** Sscan configurations */
    final private ArrayList<Sscan> sscanList = new ArrayList<Sscan>();

	/** All the items in this model */
    final private ArrayList<ModelItem> items = new ArrayList<ModelItem>();

    /** 'run' flag
     *  @see #start()
     *  @see #stop()
     */
    private boolean is_running = false;

    /** Background color */
    private RGB background = new RGB(255, 255, 255);

    /** Annotations */
	private AnnotationInfo[] annotations = new AnnotationInfo[0];

    /**
     *  Manage XYGraph Configuration Settings
     *  @author L.PHILIPPE GANIL
     */
	private XYGraphSettings graphSettings = new XYGraphSettings();

    public XYGraphSettings getGraphSettings() {
		return graphSettings;
	}

	public void setGraphSettings(XYGraphSettings xYGraphMem) {
		graphSettings = xYGraphMem;
		//fireXYGraphMemChanged(settings);
	}
	
	public void fireGraphConfigChanged() {
		
		for (ModelListener listener : listeners)
	            listener.changedXYGraphConfig();
	}
	
    /** @param macros Macros to use in this model */
    public void setMacros(final IMacroTableProvider macros)
    {
    	this.macros = macros;
    }
    
    /** Resolve macros
     *  @param text Text that might contain "$(macro)"
     *  @return Text with all macros replaced by their value
     */
    public String resolveMacros(final String text)
    {
    	if (macros == null)
    		return text;
    	try
        {
	        return MacroUtil.replaceMacros(text, macros);
        }
        catch (InfiniteLoopException ex)
        {
        	Activator.getLogger().log(Level.WARNING,
        			"Problem in macro {0}: {1}", new Object[] { text, ex.getMessage()});
        	return "Macro Error";
        }
    }

	/** @param listener New listener to notify */
    public void addListener(final ModelListener listener)
    {
        listeners.add(listener);
    }

    /** @param listener Listener to remove */
    public void removeListener(final ModelListener listener)
    {
        listeners.remove(listener);
    }

    /** @return Number of axes in model */
    public int getAxesCount()
    {
        return axesList.size();
    }

    /** @param axis_index Index of axis, 0 ... <code>getAxisCount()-1</code>
     *  @return {@link AxisConfig}
     */
    public AxesConfig getAxes(final int axes_index)
    {
    	AxesConfig axes;
    	try{
    		axes = axesList.get(axes_index);
    	} catch (Exception e) {
    		axes = null;
    	}
        return axes;
    }
    
    /** 
     *  Return the AxisConfig with the specifc name or null
     *  @param axis_index Index of axis, 0 ... <code>getAxisCount()-1</code>
     *  @return {@link AxisConfig}
     */
    public AxesConfig getAxes(final String name)
    {
        for(AxesConfig axes : axesList){
        	//System.err.println(axis.getName() + " == " + name + "=" + (axis.getName().equals(name)));
        	if(axes.getName().equals(name))
        		return axes;
        }
        
        return null;
    }

    /** Locate index of value axis
     *  @param axis Value axis configuration
     *  @return Index of axis (0, ...) or -1 if not in Model
     */
    public int getAxesIndex(final AxesConfig axes)
    {
        return axesList.indexOf(axes);
    }

    /** @param axis Axis to test
     *  @return First ModelItem that uses the axis, <code>null</code> if
     *          axis is empty
     */
    public ModelItem getFirstItemOnAxes(final AxesConfig axes)
    {
        for (ModelItem item : items)
            if (item.getAxes() == axes)
                return item;
        return null;
    }

    /** @return First unused axis (no items on axis),
     *          <code>null</code> if none found
     */
    public AxesConfig getEmptyAxes()
    {
        for (AxesConfig axes : axesList)
            if (getFirstItemOnAxes(axes) == null)
                return axes;
        return null;
    }

    /** Add value axis with default settings
     *  @return Newly added axis configuration
     */
    public AxesConfig addAxes()
    {
    	RGB color = getNextItemColor();
        final AxisConfig xAxis = new AxisConfig(
                NLS.bind(Messages.Plot_ValueAxisNameFMT, getAxesCount()+1));
        xAxis.setColor(color);
        
        final AxisConfig yAxis = new AxisConfig(
                NLS.bind(Messages.Plot_ValueAxisNameFMT, getAxesCount()+1));
        AxesConfig axes = new AxesConfig(NLS.bind(Messages.Plot_ValueAxisNameFMT, getAxesCount()+1), xAxis,yAxis);
        yAxis.setColor(color);
        
        addAxes(axes);
        return axes;
    }

    /** @param axis New axes to add */
    public void addAxes(final AxesConfig axes)
    {
        axesList.add(axes);
        axes.setModel(this);
        fireAxisChangedEvent(null);
    }

    /** Add axis at given index.
     *  Adding at '1' means the new axis will be at index '1',
     *  and what used to be at '1' will be at '2' and so on.
     *  @param index Index where axis will be placed.
     *  @param axis New axis to add
     */
    public void addAxes(final int index, final AxesConfig axes)
    {
        axesList.add(index, axes);
        axes.setModel(this);
        fireAxisChangedEvent(null);
    }

    /** @param axis Axis to remove
     *  @throws Error when axis not in model, or axis in use by model item
     */
    public void removeAxes(final AxesConfig axes)
    {
        if (! axesList.contains(axes))
            throw new Error("Unknown AxisConfig");
        for (ModelItem item : items)
            if (item.getAxes() == axes)
                throw new Error("Cannot removed AxisConfig while in use");
        axes.setModel(null);
        axesList.remove(axes);
        fireAxisChangedEvent(null);
    }

    /** @return {@link ModelItem} count in model */
    public int getItemCount()
    {
        return items.size();
    }

    /** Get one {@link ModelItem}
     *  @param i 0... getItemCount()-1
     *  @return {@link ModelItem}
     */
    public ModelItem getItem(final int i)
    {
        return items.get(i);
    }

    /** Locate item by name.
     *  If different items with the same exist in this model, the first
     *  occurrence will be returned. If no item is found with the given
     *  name, <code>null</code> will be returned.
     *  Now that this model may have different items with the same name,
     *  this method is not recommended to locate an item. This method
     *  just returns an item which just happens to have the given name.  
     *  Use {@link #indexOf(ModelItem)} or {@link #getItem(int)} to locate
     *  an item in this model.   
     *  @param name
     *  @return ModelItem by that name or <code>null</code>
     */
    public ModelItem getItem(final String name)
    {
        for (ModelItem item : items)
            if (item.getName().equals(name))
                return item;
        return null;
    }
    
    /** Returns the index of the specified item, or -1 if this list does not contain
     *  the item.
     *  @param item
     *  @return ModelItem
     */
    public int indexOf(final ModelItem item)
    {
    	return items.indexOf(item);
    }

    /** Called by items to set their initial color
     *  @return 'Next' suggested item color
     */
    private RGB getNextItemColor()
    {
        return default_colors[items.size() % default_colors.length];
    }

    /** Add item to the model.
     *  <p>
     *  If the item has no color, this will define its color based
     *  on the model's next available color.
     *  <p>
     *  If the model is already 'running', the item will be 'start'ed.
     *
     *  @param item {@link ModelItem} to add
     *  @throws RuntimeException if item is already in model
     *  @throws Exception on error trying to start a PV Item that's added to a
     *          running model
     */
    public void addItem(final ModelItem item) throws Exception
    {
    	// A new item with the same PV name are allowed to be added in the
    	// model. This way Data Browser can show the trend of the same PV
    	// in different axes or with different waveform indexes. For example,
    	// one may want to show the first element of epics://aaa:bbb in axis 1
    	// while showing the third element of the same PV in axis 2 to compare
    	// their trends in one chart.
    	//
        // if (getItem(item.getName()) != null)
        //        throw new RuntimeException("Item " + item.getName() + " already in Model");
    	
    	// But, if exactly the same instance of the given ModelItem already exists in this
    	// model, it will not be added.
    	if (items.indexOf(item) != -1)
    		throw new RuntimeException("Item " + item.getName() + " already in Model");
    	
        // Assign default color
        if (item.getColor() == null)
            item.setColor(getNextItemColor());

        // Force item to be on an axis
        if (item.getAxes() == null)
        {
            if (axesList.size() == 0)
                addAxes();
            item.setAxes(axesList.get(0));
        }
        
        // Force item to a sscan
        if (item.getSscan() == null)
        {
            if (sscanList.size() == 0)
            	addSscan(item.getName());
            item.setSscan(sscanList.get(0));
        }
        
        // Force item to a positioner
        if (item.getDetector() == null)
        {
            item.setDetector(item.getSscan().getDetector(0));
        }
        
        // Force item to a detector
        if (item.getPositioner() == null)
        {
        	item.setPositioner(item.getSscan().getPositioner(0));
        }
        
        // Check item axis
        if (! axesList.contains(item.getAxes()))
            throw new Exception("Item " + item.getName() + " added with invalid axes " + item.getAxes());

        // Add to model
        items.add(item);
        item.setModel(this);
        for (ModelListener listener : listeners)
            listener.itemAdded(item);
    }

	/** Remove item from the model.
     *  <p>
     *  If the model and thus item are 'running',
     *  the item will be 'stopped'.
     *  @param item
     *  @throws RuntimeException if item is already in model
     */
    public void removeItem(final ModelItem item)
    {
        if (is_running  &&  item instanceof ModelItem)
        {
            final ModelItem pv = (ModelItem)item;

            item.getSscan().close();
            //pv.getSamples()).clear();
        }
        if (! items.remove(item))
            throw new RuntimeException("Unknown item " + item.getName());
        // Detach item from model
        item.setModel(null);

        // Notify listeners of removed item
        for (ModelListener listener : listeners)
            listener.itemRemoved(item);
    }

    /** @return Background color */
    public RGB getPlotBackground()
    {
        return background;
    }

    /** @param rgb New background color */
    public void setPlotBackground(final RGB rgb)
    {
        if (background.equals(rgb))
            return;
        background = rgb;
        // Notify listeners
        System.out.println("**** Model.setPlotBackground() ****");
        
        for (ModelListener listener : listeners)
            listener.changedColors();
    }

    /** @param annotations Annotations to keep in model */
    public void setAnnotations(AnnotationInfo[] annotations)
    {
    	setAnnotations(annotations, true);
    }
    
    public void setAnnotations(AnnotationInfo[] annotations, boolean fireChanged) {
		// TODO Auto-generated method stub
    	this.annotations = annotations;
    	if(fireChanged)
    		fireAnnotationsChanged();
	}
    
    protected void fireAnnotationsChanged(){
    	for (ModelListener listener : listeners)
            listener.changedAnnotations();
    }

    /** @return Annotation infos of model */
	public AnnotationInfo[] getAnnotations()
    {
    	return annotations;
    }

    /** Stop all items: Disconnect PVs, ... */
    public void stop()
    {
    	for(Sscan sscan: sscanList){
    		sscan.close();
    		for(int i=0; i<=sscan.getPositionerCount()-1;i++){
        		sscan.getPositioner(i).removePropertyChangeListener(positioner_listener);
        	}
    	}
    	
    }

    /** Notify listeners of changed axis configuration
     *  @param axis Axis that changed
     */
    public void fireAxisChangedEvent(final AxisConfig axis)
    {
        for (ModelListener listener : listeners){
            listener.changedAxis(axis);
        }
    }

    /** Notify listeners of changed item visibility
     *  @param item Item that changed
     */
    void fireItemVisibilityChanged(final ModelItem item)
    {
        for (ModelListener listener : listeners)
            listener.changedItemVisibility(item);
    }

    /** Notify listeners of changed item configuration
     *  @param item Item that changed
     */
    void fireItemLookChanged(final ModelItem item)
    {
        for (ModelListener listener : listeners)
            listener.changedItemLook(item);
    }
    
    /** Notify listeners of changed item data
     *  @param item Item that changed
     */
    void fireItemDataChanged(final ModelItem item)
    {
        for (ModelListener listener : listeners)
            listener.changedItemData(item);
    }
    
    /** Notify listeners of changed item configuration
     *  @param item Item that changed
     */
    void fireItemDataConfigChanged(final ModelItem item)
    {
        for (ModelListener listener : listeners)
            listener.changedItemDataConfig(item);
    }

    /** Find a formula that uses a model item as an input.
     *  @param item Item that's potentially used in a formula
     *  @return First Formula found that uses this item, or <code>null</code> if none found
     */
    public FormulaItem getFormulaWithInput(final ModelItem item)
    {
        // Update any formulas
        for (ModelItem i : items)
        {
            if (! (i instanceof FormulaItem))
                continue;
            final FormulaItem formula = (FormulaItem) i;
            if (formula.usesInput(item))
                return formula;
        }
        return null;
    }

    /** Write RGB color to XML document
     *  @param writer
     *  @param level Indentation level
     *  @param tag_name
     *  @param color
     */
    static void writeColor(final PrintWriter writer, final int level,
            final String tag_name, final RGB color)
    {
        XMLWriter.start(writer, level, tag_name);
        writer.println();
        XMLWriter.XML(writer, level+1, Model.TAG_RED, color.red);
        XMLWriter.XML(writer, level+1, Model.TAG_GREEN, color.green);
        XMLWriter.XML(writer, level+1, Model.TAG_BLUE, color.blue);
        XMLWriter.end(writer, level, tag_name);
        writer.println();
    }

    /** Load RGB color from XML document
     *  @param node Parent node of the color
     *  @param color_tag Name of tag that contains the color
     *  @return RGB or <code>null</code> if no color found
     */
    static RGB loadColorFromDocument(final Element node, final String color_tag)
    {
        final Element color =
            DOMHelper.findFirstElementNode(node.getFirstChild(), color_tag);
        if (color == null)
            return null;
        final int red = DOMHelper.getSubelementInt(color, Model.TAG_RED, 0);
        final int green = DOMHelper.getSubelementInt(color, Model.TAG_GREEN, 0);
        final int blue = DOMHelper.getSubelementInt(color, Model.TAG_BLUE, 0);
        return new RGB(red, green, blue);
    }

    /** Load RGB color from XML document
     *  @param node Parent node of the color
     *  @return RGB or <code>null</code> if no color found
     */
    static RGB loadColorFromDocument(final Element node)
    {
        return loadColorFromDocument(node, Model.TAG_COLOR);
    }

    /** Write XML formatted Model content.
     *  @param out OutputStream, will be closed when done.
     */
    public void write(final OutputStream out)
    {
        final PrintWriter writer = new PrintWriter(out);

        XMLWriter.header(writer);
        XMLWriter.start(writer, 0, TAG_SSCAN);
        writer.println();
       
        //L.PHILIPPE
        //Save config graph settings
        XYGraphSettingsXMLUtil XYGraphMemXML = new XYGraphSettingsXMLUtil(graphSettings);
        XYGraphMemXML.write(writer);        
        
        // Misc.
        writeColor(writer, 1, TAG_BACKGROUND, background);
        
        // Value axes
        XMLWriter.start(writer, 1, TAG_AXES);
        writer.println();
        for (AxesConfig axes : axesList)
           //TODO: axes.write(writer);
        XMLWriter.end(writer, 1, TAG_AXES);
        writer.println();
        
        // Annotations
        XMLWriter.start(writer, 1, TAG_ANNOTATIONS);
        writer.println();
        for (AnnotationInfo annotation : annotations)
        	annotation.write(writer);
        XMLWriter.end(writer, 1, TAG_ANNOTATIONS);
        writer.println();
        
        // PVs (Formulas)
        XMLWriter.start(writer, 1, TAG_PVLIST);
        writer.println();
        for (ModelItem item : items)
            item.write(writer);
        XMLWriter.end(writer, 1, TAG_PVLIST);
        writer.println();
        XMLWriter.end(writer, 0, TAG_SSCAN);
        writer.close();
    }

	/** Read XML formatted Model content.
     *  @param stream InputStream, will be closed when done.
     *  @throws Exception on error
     *  @throws RuntimeException if model was already in use
     */
    public void read(final InputStream stream) throws Exception
    {
        final DocumentBuilder docBuilder =
            DocumentBuilderFactory.newInstance().newDocumentBuilder();
        final Document doc = docBuilder.parse(stream);
        loadFromDocument(doc);
    }

    /** Load model
     *  @param doc DOM document
     *  @throws Exception on error
     *  @throws RuntimeException if model was already in use
     */
    private void loadFromDocument(final Document doc) throws Exception
    {
        if (is_running || items.size() > 0)
            throw new RuntimeException("Model was already in use");

        // Check if it's a <databrowser/>.
        doc.getDocumentElement().normalize();
        final Element root_node = doc.getDocumentElement();
        if (!root_node.getNodeName().equals(TAG_SSCAN))
            throw new Exception("Wrong document type");

        RGB color = loadColorFromDocument(root_node, TAG_BACKGROUND);
        if (color != null)
            background = color;

        // Load Time Axe
        Element timeAxeNode = DOMHelper.findFirstElementNode(root_node.getFirstChild(), TAG_X_AXIS);  
        if (timeAxeNode != null)
        {
            // Load PV items  
           Element axisNode = DOMHelper.findFirstElementNode(timeAxeNode.getFirstChild(), TAG_AXIS);
        }
        
        // Load Axes
        Element list = DOMHelper.findFirstElementNode(root_node.getFirstChild(), TAG_AXES);
        if (list != null)
        {
            // Load PV items
            Element item = DOMHelper.findFirstElementNode(
                    list.getFirstChild(), TAG_AXIS);
            while (item != null)
            {
                //TODO: addAxes(AxesConfig.fromDocument(item));
                item = DOMHelper.findNextElementNode(item, TAG_AXIS);
            }
        }

        // Load Annotations
        list = DOMHelper.findFirstElementNode(root_node.getFirstChild(), TAG_ANNOTATIONS);
        if (list != null)
        {
            // Load PV items
            Element item = DOMHelper.findFirstElementNode(
                    list.getFirstChild(), TAG_ANNOTATION);
            final List<AnnotationInfo> infos = new ArrayList<AnnotationInfo>();
            try
            {
	            while (item != null)
	            {
	            	final AnnotationInfo annotation = AnnotationInfo.fromDocument(item);
	            	infos.add(annotation);
	                item = DOMHelper.findNextElementNode(item, TAG_ANNOTATION);
	            }
            }
            catch (Throwable ex)
            {
            	Activator.getLogger().log(Level.INFO, "XML error in Annotation", ex);
            }
            // Add to document
            annotations = infos.toArray(new AnnotationInfo[infos.size()]);
        }
        
        //ADD by Laurent PHILIPPE
        // Load Title and graph settings
       
        if (list != null)
        {
        	
        	try{
        		graphSettings = XYGraphSettingsXMLUtil.fromDocument(root_node.getFirstChild());
 
        	}catch (Throwable ex)
            {
        		Activator.getLogger().log(Level.INFO, "XML error in Title or  graph settings", ex);
            }
            // Add to document
        }
        
        // Backwards compatibility with previous data browser which
        // used global buffer size for all PVs
        final int buffer_size = DOMHelper.getSubelementInt(root_node, Model.TAG_LIVE_SAMPLE_BUFFER_SIZE, -1);

        // Load PVs/Formulas
        list = DOMHelper.findFirstElementNode(root_node.getFirstChild(), TAG_PVLIST);
        if (list != null)
        {
            // Load PV items
            Element item = DOMHelper.findFirstElementNode(
                    list.getFirstChild(), TAG_PV);
            while (item != null)
            {
                final ModelItem model_item = ModelItem.fromDocument(this, item);
                
                // Adding item creates the axis for it if not already there
                addItem(model_item);
                // Backwards compatibility with previous data browser which
                // stored axis configuration with each item: Update axis from that.
                final AxesConfig axes = model_item.getAxes();
                String s = DOMHelper.getSubelementString(item, TAG_XAUTO_SCALE);
                if (s.equalsIgnoreCase("true")){
                    axes.getXAxis().setAutoScale(true);
                }
                s = DOMHelper.getSubelementString(item, TAG_XLOG_SCALE);
                if (s.equalsIgnoreCase("true")){
                    axes.getXAxis().setLogScale(true);
                }
                s = DOMHelper.getSubelementString(item, TAG_YAUTO_SCALE);
                if (s.equalsIgnoreCase("true")){
                    axes.getYAxis().setAutoScale(true);
                }
                s = DOMHelper.getSubelementString(item, TAG_YLOG_SCALE);
                if (s.equalsIgnoreCase("true")){
                    axes.getYAxis().setLogScale(true);
                }
                final double xmin = DOMHelper.getSubelementDouble(item, Model.TAG_XMIN, axes.getXAxis().getMin());
                final double xmax = DOMHelper.getSubelementDouble(item, Model.TAG_XMAX, axes.getXAxis().getMax());
                final double ymin = DOMHelper.getSubelementDouble(item, Model.TAG_YMIN, axes.getYAxis().getMin());
                final double ymax = DOMHelper.getSubelementDouble(item, Model.TAG_YMAX, axes.getYAxis().getMax());
                axes.getXAxis().setRange(xmin, xmax);
                axes.getYAxis().setRange(ymin, ymax);

                item = DOMHelper.findNextElementNode(item, TAG_PV);
            }
            // Load Formulas
            item = DOMHelper.findFirstElementNode(
                    list.getFirstChild(), TAG_FORMULA);
            while (item != null)
            {
                addItem(FormulaItem.fromDocument(this, item));
                item = DOMHelper.findNextElementNode(item, TAG_FORMULA);
            }
        }
    }
    /** Add Sscan with default settings
     *  @return Newly added sscan configuration
     */
    public Sscan addSscan(String name)
    {
        final Sscan sscan = new Sscan(name, getSscanCount()+1);
 		int total = sscan.getPositionerCount();
 		// TODO: make listener interface for positioner
 		for(int i=0; i<=total-1; i++){
 			sscan.getPositioner(i).addPropertyChangeListener(positioner_listener);
 		}
        addSscan(sscan);
        return sscan;
    }

    public int getSscanCount() {
    	 return sscanList.size();
	}

	/** @param sscan New sscan to add */
    public void addSscan(final Sscan sscan)
    {
        sscanList.add(sscan);
        sscan.setModel(this);
        //fireAxisChangedEvent(null);
    }

    /** Add sscan at given index.
     *  Adding at '1' means the new sscan will be at index '1',
     *  and what used to be at '1' will be at '2' and so on.
     *  @param index Index where sscan will be placed.
     *  @param sscan New sscan to add
     */
    public void addSscan(final int index, final Sscan sscan)
    {
        sscanList.add(index, sscan);
        sscan.setModel(this);
        fireAxisChangedEvent(null);
    }

    /** @param sscan Sscan to remove
     *  @throws Error when sscan not in model, or sscan in use by model item
     */
    public void removeSscan(final Sscan sscan)
    {
        if (! sscanList.contains(sscan))
            throw new Error("Unknown Sscan");
        for (ModelItem item : items)
            if (item.getSscan() == sscan)
                throw new Error("Cannot removed Sscan while in use");
        sscan.setModel(null);
        sscanList.remove(sscan);
        fireAxisChangedEvent(null);
    }

	public int getSscanIndex(Sscan sscan) {
		return sscanList.indexOf(sscan);
	}
	
	/** @param sscan_index Index of sscan, 0 ... <code>getSscanCount()-1</code>
     *  @return {@link Sscan}
     */
    public Sscan getSscan(final int index)
    {
        return sscanList.get(index);
    }
}
