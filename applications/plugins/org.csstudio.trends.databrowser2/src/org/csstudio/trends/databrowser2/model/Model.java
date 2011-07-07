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
import java.util.Timer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.csstudio.apputil.time.RelativeTime;
import org.csstudio.apputil.time.StartEndTimeParser;
import org.csstudio.apputil.xml.DOMHelper;
import org.csstudio.apputil.xml.XMLWriter;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.preferences.Preferences;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.RGB;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Data Browser model
 *  <p>
 *  Maintains a list of {@link ModelItem}s
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Model
{
    /** File extension for data browser config files.
     *  plugin.xml registers the editor for this file extension
     */
    final public static String FILE_EXTENSION = "plt"; //$NON-NLS-1$

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
    /** Listeners to model changes */
    final private ArrayList<ModelListener> listeners = new ArrayList<ModelListener>();

    /** Axes configurations */
    final private ArrayList<AxisConfig> axes = new ArrayList<AxisConfig>();

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

    /** Time span of data in seconds */
    private double time_span = Preferences.getTimeSpan();

    /** End time of the data range */
    private ITimestamp end_time = TimestampFactory.now();

    /** Background color */
    private RGB background = new RGB(255, 255, 255);

    /** How should plot rescale when archived data arrives? */
    private ArchiveRescale archive_rescale = Preferences.getArchiveRescale();

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
    public AxisConfig addAxis()
    {
        final AxisConfig axis = new AxisConfig(
                NLS.bind(Messages.Plot_ValueAxisNameFMT, getAxisCount()+1));
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

    /** Locate item by name
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
        // Prohibit duplicate items
        if (getItem(item.getName()) != null)
                throw new RuntimeException("Item " + item.getName() + " already in Model");
        // Assign default color
        if (item.getColor() == null)
            item.setColor(getNextItemColor());

        // Force item to be on an axis
        if (item.getAxis() == null)
        {
            if (axes.size() == 0)
                addAxis();
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

    /** @param start_time Start and ..
     *  @param end_time   end time of the range to display
     */
    public void setTimerange(final ITimestamp start_time, final ITimestamp end_time)
    {
        final double new_span = end_time.toDouble() - start_time.toDouble();
        if (new_span > 0)
        {
            synchronized (this)
            {
                this.end_time = end_time;
                time_span = new_span;
            }
        }
        // Notify listeners
        for (ModelListener listener : listeners)
            listener.changedTimerange();
    }

    /** @param time_span time span of data in seconds
     *  @see #isScrollEnabled()
     */
    public void setTimespan(final double time_span)
    {
        if (time_span > 0)
        {
            synchronized (this)
            {
                this.time_span = time_span;
            }
        }
        // Notify listeners
        for (ModelListener listener : listeners)
            listener.changedTimerange();
    }

    /** @return Start time of the data range
     *  @see #isScrollEnabled()
     */
    synchronized public ITimestamp getStartTime()
    {
        return TimestampFactory.fromDouble(getEndTime().toDouble() - time_span);
    }

    /** @return End time of the data range
     *  @see #isScrollEnabled()
     */
    synchronized public ITimestamp getEndTime()
    {
        if (scroll_enabled)
            end_time = TimestampFactory.now();
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
            return getStartTime().toString();
    }

    /** @return String representation of end time. While scrolling, this is
     *          a relative time, otherwise an absolute date/time.
     */
    synchronized public String getEndSpecification()
    {
        if (scroll_enabled)
            return RelativeTime.NOW;
        else
            return end_time.toString();
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
        for (ModelListener listener : listeners)
            listener.changedColors();
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
        XMLWriter.start(writer, 0, TAG_DATABROWSER);
        writer.println();
        // Time axis
        XMLWriter.XML(writer, 1, TAG_SCROLL, isScrollEnabled());
        XMLWriter.XML(writer, 1, TAG_UPDATE_PERIOD, getUpdatePeriod());
        if (isScrollEnabled())
        {
            XMLWriter.XML(writer, 1, TAG_START, new RelativeTime(-time_span));
            XMLWriter.XML(writer, 1, TAG_END, RelativeTime.NOW);
        }
        else
        {
            XMLWriter.XML(writer, 1, TAG_START, getStartTime());
            XMLWriter.XML(writer, 1, TAG_END, getEndTime());
        }
        // Misc.
        writeColor(writer, 1, TAG_BACKGROUND, background);
        XMLWriter.XML(writer, 1, TAG_ARCHIVE_RESCALE, archive_rescale.name());
        // Value axes
        XMLWriter.start(writer, 1, TAG_AXES);
        writer.println();
        for (AxisConfig axis : axes)
            axis.write(writer);
        XMLWriter.end(writer, 1, TAG_AXES);
        writer.println();
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

        scroll_enabled = DOMHelper.getSubelementBoolean(root_node, TAG_SCROLL, scroll_enabled);
        update_period = DOMHelper.getSubelementDouble(root_node, TAG_UPDATE_PERIOD, update_period);

        final String start = DOMHelper.getSubelementString(root_node, TAG_START);
        final String end = DOMHelper.getSubelementString(root_node, TAG_END);
        if (start.length() > 0  &&  end.length() > 0)
        {
            final StartEndTimeParser times = new StartEndTimeParser(start, end);
            setTimerange(TimestampFactory.fromCalendar(times.getStart()),
                         TimestampFactory.fromCalendar(times.getEnd()));
        }

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

        // Load Axes
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
