/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.csstudio.apputil.macros.IMacroTableProvider;
import org.csstudio.apputil.macros.InfiniteLoopException;
import org.csstudio.apputil.macros.MacroUtil;
import org.csstudio.apputil.time.PeriodFormat;
import org.csstudio.apputil.time.RelativeTime;
import org.csstudio.apputil.time.StartEndTimeParser;
import org.csstudio.apputil.xml.DOMHelper;
import org.csstudio.apputil.xml.XMLWriter;
import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.swt.xygraph.figures.Annotation.CursorLineStyle;
import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.imports.ImportArchiveReaderFactory;
import org.csstudio.trends.databrowser2.persistence.AnnotationSettings;
import org.csstudio.trends.databrowser2.persistence.AxisSettings;
import org.csstudio.trends.databrowser2.persistence.ColorSettings;
import org.csstudio.trends.databrowser2.persistence.XYGraphSettings;
import org.csstudio.trends.databrowser2.persistence.XYGraphSettingsXMLUtil;
import org.csstudio.trends.databrowser2.preferences.Preferences;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.RGB;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
    final public static String FILE_EXTENSION = "plt"; //$NON-NLS-1$

    /** Previously used file extension */
    final public static String FILE_EXTENSION_OLD = "css-plt"; //$NON-NLS-1$

    // XML file tags
    final public static String TAG_DATABROWSER = "databrowser";
    final public static String TAG_SCROLL = "scroll";
    final public static String TAG_UPDATE_PERIOD = "update_period";
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
    final public static String TAG_SCAN_PERIOD = "period";
    final public static String TAG_INPUT = "input";
    final public static String TAG_ARCHIVE = "archive";
    final public static String TAG_URL = "url";
    final public static String TAG_KEY = "key";
    final public static String TAG_START = "start";
    final public static String TAG_END = "end";
    final public static String TAG_LOG_SCALE = "log_scale";
    final public static String TAG_AUTO_SCALE = "autoscale";
    final public static String TAG_MAX = "max";
    final public static String TAG_MIN = "min";
    final public static String TAG_BACKGROUND = "background";
    final public static String TAG_ARCHIVE_RESCALE = "archive_rescale";
    final public static String TAG_REQUEST = "request";
    final public static String TAG_VISIBLE = "visible";

    final public static String TAG_ANNOTATIONS = "annotations";
    final public static String TAG_ANNOTATION = "annotation";
	public static final String TAG_ANNOTATION_CURSOR_LINE_STYLE = "line_style";
	public static final String TAG_ANNOTATION_SHOW_NAME = "show_name";
	public static final String TAG_ANNOTATION_SHOW_POSITION = "show_position";
	public static final String TAG_ANNOTATION_COLOR = "color";
	public static final String TAG_ANNOTATION_FONT = "font";

    final public static String TAG_TIME = "time";
    final public static String TAG_VALUE = "value";
    final public static String TAG_WAVEFORM_INDEX = "waveform_index";


    public static final String TAG_FONT = "font";
	public static final String TAG_SCALE_FONT = "scale_font";

	final public static String TAG_TIME_AXIS = "time_axis";


	//GRID LINE
	public static final String TAG_GRID_LINE = "grid_line";
	public static final String TAG_SHOW_GRID_LINE = "show_grid_line";
	public static final String TAG_DASH_GRID_LINE = "dash_grid_line";

	//FORMAT
	public static final String TAG_FORMAT = "format";
	public static final String TAG_AUTO_FORMAT = "auto_format";
	public static final String TAG_TIME_FORMAT = "time_format";
	public static final String TAG_FORMAT_PATTERN = "format_pattern";


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

    /** Macros */
    private IMacroTableProvider macros = null;

    /** Listeners to model changes */
    final private ArrayList<ModelListener> listeners = new ArrayList<ModelListener>();

    /** Axes configurations */
    final private ArrayList<AxisConfig> axes = new ArrayList<AxisConfig>();

    /**
     * Time Axes configurations
     * Ignore MIN-MAX part because the range is set by start & end properties
     */
    private  AxisConfig timeAxis;

    public AxisConfig getTimeAxis() {
		return timeAxis;
	}

	/** All the items in this model */
    final private ArrayList<ModelItem> items = new ArrayList<ModelItem>();

    /** 'run' flag
     *  @see #start()
     *  @see #stop()
     */
    private boolean is_running = false;

    /** Period in seconds for scrolling or refreshing */
    private double update_period = Preferences.getUpdatePeriod();

    /** Timer used to scan PVItems */
    final private Timer scanner = new Timer("ScanTimer", true);

    /** <code>true</code> if scrolling is enabled */
    private boolean scroll_enabled = true;

    /** Start and end time specification */
    private String start_spec, end_spec;
    
    /** Time span of data in seconds */
    private double time_span = Preferences.getTimeSpan();

    /** End time of the data range */
    private Timestamp end_time = Timestamp.now();

    /** Background color */
    private RGB background = new RGB(255, 255, 255);

    /** Annotations */
	private AnnotationInfo[] annotations = new AnnotationInfo[0];

    /** How should plot rescale when archived data arrives? */
    private ArchiveRescale archive_rescale = Preferences.getArchiveRescale();


    /**
     *  Manage XYGraph Configuration Settings
     *  @author L.PHILIPPE GANIL
     */
	private XYGraphSettings graphSettings = new XYGraphSettings();

	public Model()
	{
		start_spec = "-" + PeriodFormat.formatSeconds(time_span);
		end_spec = RelativeTime.NOW;
	}
	
	
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
    public int getAxisCount()
    {
        return axes.size();
    }

    /** @param axis_index Index of axis, 0 ... <code>getAxisCount()-1</code>
     *  @return {@link AxisConfig}
     */
    public AxisConfig getAxis(final int axis_index)
    {
        return axes.get(axis_index);
    }

    /**
     *  Return the AxisConfig with the specifc name or null
     *  @param axis_index Index of axis, 0 ... <code>getAxisCount()-1</code>
     *  @return {@link AxisConfig}
     */
    public AxisConfig getAxis(final String name)
    {
        for(AxisConfig axis : axes){
        	//System.err.println(axis.getName() + " == " + name + "=" + (axis.getName().equals(name)));
        	if(axis.getName().equals(name))
        		return axis;
        }

        return null;
    }

    /** Locate index of value axis
     *  @param axis Value axis configuration
     *  @return Index of axis (0, ...) or -1 if not in Model
     */
    public int getAxisIndex(final AxisConfig axis)
    {
        return axes.indexOf(axis);
    }

    /** @param axis Axis to test
     *  @return First ModelItem that uses the axis, <code>null</code> if
     *          axis is empty
     */
    public ModelItem getFirstItemOnAxis(final AxisConfig axis)
    {
        for (ModelItem item : items)
            if (item.getAxis() == axis)
                return item;
        return null;
    }
    
    /** @param axis Axis to test
     *  @return ModelItem linked to this axis count
     */
    public int countActiveItemsOnAxis(final AxisConfig axis)
    {
		int count = 0;
		for (ModelItem item : items)
			if (item.getAxis() == axis && item.isVisible())
				count++;
		return count;
    }

    /** @return First unused axis (no items on axis),
     *          <code>null</code> if none found
     */
    public AxisConfig getEmptyAxis()
    {
        for (AxisConfig axis : axes)
            if (getFirstItemOnAxis(axis) == null)
                return axis;
        return null;
    }

    /** Add value axis with default settings
     *  @return Newly added axis configuration
     */
    public AxisConfig addAxis(String name)
    {
		if (name == null)
			name = NLS.bind(Messages.Plot_ValueAxisNameFMT, getAxisCount() + 1);
		final AxisConfig axis = new AxisConfig(name);
		axis.setColor(getNextItemColor());
		addAxis(axis);
		return axis;
    }

    /** @param axis New axis to add */
    public void addAxis(final AxisConfig axis)
    {
        axes.add(axis);
        axis.setModel(this);
        fireAxisChangedEvent(null);
    }

    /** Add axis at given index.
     *  Adding at '1' means the new axis will be at index '1',
     *  and what used to be at '1' will be at '2' and so on.
     *  @param index Index where axis will be placed.
     *  @param axis New axis to add
     */
    public void addAxis(final int index, final AxisConfig axis)
    {
        axes.add(index, axis);
        axis.setModel(this);
        fireAxisChangedEvent(null);
    }

    /** @param axis Axis to remove
     *  @throws Error when axis not in model, or axis in use by model item
     */
    public void removeAxis(final AxisConfig axis)
    {
        if (! axes.contains(axis))
            throw new Error("Unknown AxisConfig");
        for (ModelItem item : items)
            if (item.getAxis() == axis)
                throw new Error("Cannot removed AxisConfig while in use");
        axis.setModel(null);
        axes.remove(axis);
        fireAxisChangedEvent(null);
    }

    /** @return How should plot rescale after archived data arrived? */
    public ArchiveRescale getArchiveRescale()
    {
        return archive_rescale;
    }

    /** @param archive_rescale How should plot rescale after archived data arrived? */
    public void setArchiveRescale(final ArchiveRescale archive_rescale)
    {
        if (this.archive_rescale == archive_rescale)
            return;
        this.archive_rescale = archive_rescale;
        for (ModelListener listener : listeners)
            listener.changedArchiveRescale();
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
        if (item.getAxis() == null)
        {
            if (axes.size() == 0)
                addAxis(item.getDisplayName());
            item.setAxis(axes.get(0));
        }
        // Check item axis
        if (! axes.contains(item.getAxis()))
            throw new Exception("Item " + item.getName() + " added with invalid axis " + item.getAxis());

        // Add to model
        items.add(item);
        item.setModel(this);
        if (is_running  &&  item instanceof PVItem)
            ((PVItem)item).start(scanner);
        // Notify listeners of new item
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
        if (is_running  &&  item instanceof PVItem)
        {
            final PVItem pv = (PVItem)item;
            pv.stop();
            // Delete its samples:
            // For one, so save memory.
            // Also, in case item is later added back in, its old samples
            // will have gaps because the item was stopped
            pv.getSamples().clear();
        }
        if (! items.remove(item))
            throw new RuntimeException("Unknown item " + item.getName());
        // Detach item from model
        item.setModel(null);

        // Notify listeners of removed item
        for (ModelListener listener : listeners)
            listener.itemRemoved(item);
        		
        // Remove axis if unused
		AxisConfig axis = item.getAxis();
		item.setAxis(null);
		if (countActiveItemsOnAxis(axis) == 0) {
			removeAxis(axis);
	        fireAxisChangedEvent(null);
		}
    }

    /** @return Period in seconds for scrolling or refreshing */
    public double getUpdatePeriod()
    {
        return update_period;
    }

    /** @param period_secs New update period in seconds */
    public void setUpdatePeriod(final double period_secs)
    {
        // Don't allow updates faster than 10Hz (0.1 seconds)
        if (period_secs < 0.1)
            update_period = 0.1;
        else
            update_period = period_secs;
        // Notify listeners
        for (ModelListener listener : listeners)
            listener.changedUpdatePeriod();
    }

    /** The model supports two types of start/end time handling:
     *  <ol>
     *  <li>Scroll mode: While <code>isScrollEnabled=true</code>,
     *      the end time is supposed to be 'now' and the start time is
     *      supposed to be <code>getTimespan()</code> seconds before 'now'.
     *  <li>Fixed start/end time: While <code>isScrollEnabled=false</code>,
     *      the methods <code>getStartTime()</code>, <code>getEndTime</code>
     *      return a fixed start/end time.
     *  </ol>
     *  @return <code>true</code> if scrolling is enabled */
    synchronized public boolean isScrollEnabled()
    {
        return scroll_enabled;
    }

    /** @param scroll_enabled Should scrolling be enabled? */
    public void enableScrolling(final boolean scroll_enabled)
    {
        synchronized (this)
        {
            if (this.scroll_enabled == scroll_enabled)
                return;
            this.scroll_enabled = scroll_enabled;
        }
        // Notify listeners
        for (ModelListener listener : listeners)
            listener.scrollEnabled(scroll_enabled);
    }

    /** @return time span of data in seconds
     *  @see #isScrollEnabled()
     */
    synchronized public double getTimespan()
    {
        return time_span;
    }

    /** Set time range.
     *  <p>In 'scroll' mode, this determines the displayed time range.
     *  Otherwise, it determines the absolute start and end times
     *  @param start_spec Start and ..
     *  @param end_spec   end time specification of the range to display
     *  @throws Exception on error in the time specifications
     */
    public void setTimerange(final String start_spec, final String end_spec) throws Exception
    {
        final StartEndTimeParser times = new StartEndTimeParser(start_spec, end_spec);
        final Timestamp start_time = TimestampHelper.fromCalendar(times.getStart());
        final Timestamp end_time = TimestampHelper.fromCalendar(times.getEnd());
        final double new_span = end_time.durationFrom(start_time).toSeconds();
        if (new_span > 0)
        {
            synchronized (this)
            {
            	if (this.start_spec.equals(start_spec)  &&
            	    this.end_spec.equals(end_spec))
            		return;
            	this.start_spec = start_spec;
            	this.end_spec = end_spec;
                this.end_time = end_time;
                time_span = new_span;
            }
            // Notify listeners
            for (ModelListener listener : listeners)
            	listener.changedTimerange();
        }
    }

    /** @return Start time specification of the data range */
    synchronized public String getStartSpec()
    {
        return start_spec;
    }

    /** @return End time specification of the data range */
    synchronized public String getEndSpec()
    {
        return end_spec;
    }
    
    /** @return Start time of the data range
     *  @see #isScrollEnabled()
     */
    synchronized public Timestamp getStartTime()
    {
        return getEndTime().minus(TimeDuration.ofSeconds(time_span));
    }

    /** @return End time of the data range
     *  @see #isScrollEnabled()
     */
    synchronized public Timestamp getEndTime()
    {
        if (scroll_enabled)
            end_time = Timestamp.now();
        return end_time;
    }

    /** @return String representation of start time. While scrolling, this is
     *          a relative time, otherwise an absolute date/time.
     */
    synchronized public String getStartSpecification()
    {
        if (scroll_enabled)
            return new RelativeTime(-time_span).toString();
        else
            return TimestampHelper.format(getStartTime());
    }

    /** @return String representation of end time. While scrolling, this is
     *          a relative time, otherwise an absolute date/time.
     */
    synchronized public String getEndSpecification()
    {
        if (scroll_enabled)
            return RelativeTime.NOW;
        else
            return TimestampHelper.format(end_time);
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
    public void setAnnotations(final AnnotationInfo[] annotations)
    {
    	setAnnotations(annotations, true);
    }

    public void setAnnotations(final AnnotationInfo[] annotations, final boolean fireChanged)
    {
    	this.annotations = annotations;
    	if (fireChanged)
    		fireAnnotationsChanged();
	}

    protected void fireAnnotationsChanged()
    {
    	for (ModelListener listener : listeners)
            listener.changedAnnotations();
    }

    /** @return Annotation infos of model */
	public AnnotationInfo[] getAnnotations()
    {
    	return annotations;
    }

	/** Start all items: Connect PVs, initiate scanning, ...
     *  @throws Exception on error
     */
    public void start() throws Exception
    {
        if (is_running)
            throw new RuntimeException("Model already started");
        for (ModelItem item : items)
        {
            if (!(item instanceof PVItem))
                continue;
            final PVItem pv_item = (PVItem) item;
            pv_item.start(scanner);
        }
        is_running = true;
    }

    /** Stop all items: Disconnect PVs, ... */
    public void stop()
    {
        if (!is_running)
            throw new RuntimeException("Model wasn't started");
        is_running = false;
        for (ModelItem item : items)
        {
            if (!(item instanceof PVItem))
                continue;
            final PVItem pv_item = (PVItem) item;
            pv_item.stop();
            ImportArchiveReaderFactory.removeCachedArchives(pv_item.getArchiveDataSources());
        }
    }

    /** Test if any ModelItems received new samples,
     *  if formulas need to be re-computed,
     *  since the last time this method was called.
     *  @return <code>true</code> if there were new samples
     */
    public boolean updateItemsAndCheckForNewSamples()
    {
        boolean anything_new = false;
        // Update any formulas
        for (ModelItem item : items)
        {
            if (item instanceof FormulaItem  &&
                ((FormulaItem)item).reevaluate())
                    anything_new = true;
        }
        // Check and reset PV Items
        for (ModelItem item : items)
        {
            if (item instanceof PVItem  &&
                item.getSamples().testAndClearNewSamplesFlag())
                anything_new = true;
        }
        return anything_new;
    }

    /** Notify listeners of changed axis configuration
     *  @param axis Axis that changed
     */
    public void fireAxisChangedEvent(final AxisConfig axis)
    {
        for (ModelListener listener : listeners)
            listener.changedAxis(axis);
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

    /** Notify listeners of changed item configuration
     *  @param item Item that changed
     */
    void fireItemDataConfigChanged(final PVItem item)
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
    public static RGB loadColorFromDocument(final Element node, final String color_tag)
    {
    	if (node == null)
    		return new RGB(0, 0, 0);
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
        XMLWriter.start(writer, 0, TAG_DATABROWSER);
        writer.println();

        // Save XYGraph settings
        XYGraphSettingsXMLUtil.write(graphSettings, writer);
        writer.println();

        // Time axis
        XMLWriter.XML(writer, 1, TAG_SCROLL, isScrollEnabled());
        XMLWriter.XML(writer, 1, TAG_UPDATE_PERIOD, getUpdatePeriod());
        synchronized (this)
        {
        	XMLWriter.XML(writer, 1, TAG_START, start_spec);
        	XMLWriter.XML(writer, 1, TAG_END, end_spec);			
		}

        XMLWriter.XML(writer, 1, TAG_ARCHIVE_RESCALE, archive_rescale.name());
        //all other settings are already included in the graphsettings
//        // Time axis config
//        if (timeAxis != null)
//        {
//            XMLWriter.start(writer, 1, TAG_TIME_AXIS);
//            writer.println();
//            timeAxis.write(writer);
//            XMLWriter.end(writer, 1, TAG_TIME_AXIS);
//            writer.println();
//        }
//        // Value axes
//        XMLWriter.start(writer, 1, TAG_AXES);
//        writer.println();
//        for (AxisConfig axis : axes)
//            axis.write(writer);
//        XMLWriter.end(writer, 1, TAG_AXES);
//        writer.println();
//
//        // Annotations
//        XMLWriter.start(writer, 1, TAG_ANNOTATIONS);
//        writer.println();
//        for (AnnotationInfo annotation : annotations)
//        	annotation.write(writer);
//        XMLWriter.end(writer, 1, TAG_ANNOTATIONS);
//        writer.println();
//        // Misc.
//        writeColor(writer, 1, TAG_BACKGROUND, background);
        
        // PVs (Formulas)
        XMLWriter.start(writer, 1, TAG_PVLIST);
        writer.println();
        for (ModelItem item : items)
            item.write(writer);
        XMLWriter.end(writer, 1, TAG_PVLIST);
        writer.println();
        XMLWriter.end(writer, 0, TAG_DATABROWSER);
        writer.close();
    }

    public void setTimeAxis(AxisConfig timeAxis) {
		this.timeAxis = timeAxis;
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
        if (!root_node.getNodeName().equals(TAG_DATABROWSER))
            throw new Exception("Wrong document type");

        synchronized (this)
        {
            scroll_enabled = DOMHelper.getSubelementBoolean(root_node, TAG_SCROLL, scroll_enabled);
        }
        update_period = DOMHelper.getSubelementDouble(root_node, TAG_UPDATE_PERIOD, update_period);

        final String start = DOMHelper.getSubelementString(root_node, TAG_START);
        final String end = DOMHelper.getSubelementString(root_node, TAG_END);
        if (start.length() > 0  &&  end.length() > 0)
            setTimerange(start, end);
        RGB color = loadColorFromDocument(root_node, TAG_BACKGROUND);
        if (color != null)
            background = color;

        try
        {
            archive_rescale = ArchiveRescale.valueOf(
                    DOMHelper.getSubelementString(root_node, TAG_ARCHIVE_RESCALE));
        }
        catch (Throwable ex)
        {
            archive_rescale = ArchiveRescale.STAGGER;
        }

        // Load Time Axis
        final Element timeAxisNode = DOMHelper.findFirstElementNode(root_node.getFirstChild(), TAG_TIME_AXIS);
        if (timeAxisNode != null)
        {
            // Load PV items
           Element axisNode = DOMHelper.findFirstElementNode(timeAxisNode.getFirstChild(), TAG_AXIS);
           timeAxis = AxisConfig.fromDocument(axisNode);
        }

        // Load value Axes
        Element list = DOMHelper.findFirstElementNode(root_node.getFirstChild(), TAG_AXES);
        if (list != null)
        {
            // Load PV items
            Element item = DOMHelper.findFirstElementNode(
                    list.getFirstChild(), TAG_AXIS);
            while (item != null)
            {
                addAxis(AxisConfig.fromDocument(item));
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

		// Load XYGraph settings
		try {
			NodeList nodeList = root_node.getElementsByTagName(XYGraphSettings.TAG_NAME);
			if (nodeList.getLength() > 0) {
				graphSettings = XYGraphSettingsXMLUtil.read(nodeList.item(0));
			} else { // retro-compatibility
				graphSettings = XYGraphSettingsXMLUtil
						.readOldSettings(root_node.getFirstChild());
			}
		} catch (Throwable ex) {
			Activator.getLogger().log(Level.INFO,
					"XML error in XYGraph settings", ex);
		}
		
		for (AxisSettings s : graphSettings.getAxisSettingsList()) {
			ColorSettings fc = s.getForegroundColor();
			ColorSettings gc = s.getMajorGridColor();
			AxisConfig config = new AxisConfig(true, s.getTitle(), 
					FontDataUtil.getFontData(s.getTitleFont()), 
					FontDataUtil.getFontData(s.getScaleFont()), 
					new RGB(fc.getRed(),fc.getGreen(),fc.getBlue()), 
					s.getRange().getLower(), s.getRange().getUpper(),
					s.isAutoScale(), s.isLogScale(), s.isShowMajorGrid(),
					s.isDashGridLine(),new RGB(gc.getRed(),gc.getGreen(),gc.getBlue()),
					s.isAutoFormat(), s.isDateEnabled(), s.getFormatPattern());
			if (timeAxis == null) {
				timeAxis = config;
			} else {
				addAxis(config);
			}
		}
		ArrayList<AnnotationInfo> infos = new ArrayList<AnnotationInfo>();
		for (AnnotationSettings s : graphSettings.getAnnotationSettingsList()) {
			ColorSettings fc = s.getAnnotationColor();
			RGB rgb = fc != null ? new RGB(fc.getRed(),fc.getGreen(),fc.getBlue()) : null;
			infos.add(new AnnotationInfo(
					TimestampHelper.fromMillisecs((long)s.getXValue()), s.getYValue(), s.getxAxis(), 
					s.getName(), CursorLineStyle.valueOf(s.getCursorLineStyle()), s.isShowName(), 
					s.isShowPosition(), s.isShowSampleInfo(), FontDataUtil.getFontData(s.getFont()),rgb));
		}
		setAnnotations(infos.toArray(new AnnotationInfo[infos.size()]));
		
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
                final PVItem model_item = PVItem.fromDocument(this, item);
                if (buffer_size > 0)
                    model_item.setLiveCapacity(buffer_size);
                // Adding item creates the axis for it if not already there
                addItem(model_item);
                // Backwards compatibility with previous data browser which
                // stored axis configuration with each item: Update axis from that.
                final AxisConfig axis = model_item.getAxis();
                String s = DOMHelper.getSubelementString(item, TAG_AUTO_SCALE);
                if (s.equalsIgnoreCase("true"))
                    axis.setAutoScale(true);
                s = DOMHelper.getSubelementString(item, TAG_LOG_SCALE);
                if (s.equalsIgnoreCase("true"))
                    axis.setLogScale(true);
                final double min = DOMHelper.getSubelementDouble(item, Model.TAG_MIN, axis.getMin());
                final double max = DOMHelper.getSubelementDouble(item, Model.TAG_MAX, axis.getMax());
                axis.setRange(min, max);

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
}
