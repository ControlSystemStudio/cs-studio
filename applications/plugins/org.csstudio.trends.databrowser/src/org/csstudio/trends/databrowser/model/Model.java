package org.csstudio.trends.databrowser.model;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.swt.chart.TraceType;
import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.util.swt.DefaultColors;
import org.csstudio.util.time.StartEndTimeParser;
import org.csstudio.util.xml.DOMHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Data model for a chart. 
 *  <p>
 *  Holds a list of PVs, subscribes to new values for those PVs.
 *  <p>
 *  For life values, the model behaves like the EPICS StripTool
 *  (see <a href="http://www.aps.anl.gov/epics">http://www.aps.anl.gov</a>):
 *  While the control system provides good time stamps for the life values,
 *  it is really tricky to use those.
 *  Assume it's 12:00:00, and the last sample we received was stamped
 *  11:59:30. So we draw a plot with line at that (old) value,
 *  using the same value up to 'now', since that's the last we know.
 *  <p>
 *  If the value indeed didn't change, and thus we receive no update, that's OK.
 *  But what if now a network update arrives with a new value, stamped 11:59:58?
 *  The next time we redraw the graph, the value that used to be plotted for the
 *  time 11:59:58 jumps to the newly received value.
 *  That looks very disconcerting (I've tried it in an early EDM xy-chart widget.
 *  Nobody liked it.).
 *  <p>
 *  So this model behaves like StripTool: The network updates for a PV are cached,
 *  i.e. we remember the "most recent" value as the "current" value.
 *  Periodically, the ChartItems are asked to add the current value to their
 *  sequence of samples, using the current host clock as a time stamp.
 *  
 *  @author Kay Kasemir
 */
public class Model
{
    /** Start and end of the time range to display.
     *  @see #getStartSpecification()
     */
    private String start_specification, end_specification;

    /** Default for start_specification */
    private static final String DEFAULT_END_SPEC = "now"; //$NON-NLS-1$

    /** Default for end_specification */
    private static final String DEFAULT_START_SPEC = "-1 hour"; //$NON-NLS-1$
    
    /** Start and end of the time range as time stamps.
     *  @see #getStartSpecification()
     */
    ITimestamp start_time, end_time;
    
    /** Scan period for 'live' data in seconds. */
    private double scan_period = 0.5;
    
    /** Minimum for scan_period. */
    public static final double MIN_SCAN_RATE = 0.1;
    
    /** Update period of the plot in seconds. */
    private double update_period = 1.0;
    
    /** Minumum for update_period. */
    public static final double MIN_UPDATE_RATE = 0.5;

    /** Ring buffer size, number of elements, for 'live' data. */
    private int ring_size = 1024;

    private ArrayList<ModelItem> items = new ArrayList<ModelItem>();
    
    /** <code>true</code> if all model items were 'started'. */
    private boolean is_running = false;

    private ArrayList<ModelListener> listeners = 
        new ArrayList<ModelListener>();
    
    /** Construct a new model. */
    @SuppressWarnings("nls")
    public Model()
    {
        try
        {
            // Set time defaults, since otherwise start/end would be null.
            setTimeSpecifications(DEFAULT_START_SPEC, DEFAULT_END_SPEC);
        }
        catch (Exception ex)
        {
            Plugin.logException("Cannot init. Model", ex);
        }
    }
    
    /** Must be called to dispose the model. */
    public void dispose()
    {
        disposeItems();
    }

    /** Peoperly clear the item list. */
    private void disposeItems()
    {
        for (ModelItem item : items)
            item.dispose();
        items.clear();
    }
    
    /** Add a listener. */
    public void addListener(ModelListener listener)
    {
        if (listeners.contains(listener))
            throw new Error("Listener added more than once."); //$NON-NLS-1$
        listeners.add(listener);
    }
    
    /** Remove a listener. */
    public void removeListener(ModelListener listener)
    {
        if (!listeners.contains(listener))
            throw new Error("Unknown listener."); //$NON-NLS-1$
        listeners.remove(listener);
    }

    /** Get the start specification that is held 'permamently' when
     *  the model is saved and re-loaded.
     *  <p>
     *  When the specifications are initially loaded or later changed,
     *  the current start and end time is computed from the specs.
     *  <p>
     *  At runtime, scroll or pan operations will update the currently
     *  displayed start and end time, but that won't affect this
     *  permanent start/end specification.
     *  <p>
     *  This way, one can configure a model to show a default time range
     *  of "-8 hours" ... "now", then zoom and pan a bit to look at details,
     *  but what's saved will be the original "-8 hours" ... "now"
     *  specification.
     *  <p>
     *  The config view has buttons to force an update of the specification
     *  from the current start/end times and vice versa.
     *
     *  @see #getStartTime()
     *  @return Start specification.
     */
    public String getStartSpecification()
    {   return start_specification;  }

    /** @see #getStartSpecification()
     *  @return End specification.
     */
    public String getEndSpecification()
    {   return end_specification; }
    
    /** The current start time, which might be different from what you get
     *  when you evluate the start specification 'right now'.
     *  @see #getStartSpecification()
     *  @return Start time, evaluated when start specification was set.
     */
    public ITimestamp getStartTime()
    {   return start_time; }
    
    /** @see #getStartTime()
     *  @return End time, evaluated when end specification was set.
     */
    public ITimestamp getEndTime()
    {   return end_time; }

    /** @return Returns the scan period in seconds. */
    public double getScanPeriod()
    {   return scan_period; }

    /** @return Returns the update period in seconds. */
    public double getUpdatePeriod()
    {   return update_period; }
    
    /** Set a new start and end time specification.
     *  <p>
     *  Also updates the current start and end time with
     *  values computed from the specs "right now".
     *  @see org.csstudio.util.time.StartEndTimeParser
     *  @see #getStartSpecification()
     *  @see #setTimeRange(ITimestamp, ITimestamp)
     *  @exception Exception on parse error of specs.
     */
    public void setTimeSpecifications(String start_specification,
                                      String end_specification) 
        throws Exception
    {
        StartEndTimeParser start_end =
            new StartEndTimeParser(start_specification, end_specification);
        // In case of parse errors, we won't reach this point
        start_time = TimestampFactory.fromCalendar(start_end.getStart());
        end_time = TimestampFactory.fromCalendar(start_end.getEnd());
        this.start_specification = start_specification;
        this.end_specification = end_specification;
        // fireTimeSpecificationsChanged, fireTimeRangeChanged
        for (ModelListener l : listeners)
        {
            l.timeSpecificationsChanged();
            l.timeRangeChanged();
        }
    }
 
    /** Update the current time range.
     *  <p>
     *  The time specifications stay as they are.
     *  @see #setTimeSpecifications(String, String)
     *  @exception Exception when start &gt; end.
     */
    @SuppressWarnings("nls")
    public void setTimeRange(ITimestamp start, ITimestamp end) throws Exception
    {
        if (start_time.isGreaterThan(end))
            throw new Exception("Time stamps out of order, start "
                            + start + "  >  " + end);
        start_time = start;
        end_time = end;
        // ,fireTimeRangeChanged
        for (ModelListener l : listeners)
            l.timeRangeChanged();
    }
    
    /** Set new scan and update periods.
     *  <p>
     *  Actual periods might differ because of enforced minumum etc.
     *
     *  @param scan Scan period in seconds.
     *  @param update Update period in seconds.
     */
    public void setPeriods(double scan, double update)
    {
        // Don't allow 'too fast'
        if (scan < MIN_SCAN_RATE)
            scan = MIN_SCAN_RATE;
        if (update < MIN_UPDATE_RATE)
            update = MIN_UPDATE_RATE;
        // No sense in redrawing faster than the data can change.
        if (update < scan)
            update = scan;
        scan_period = scan;
        update_period = update;
        // firePeriodsChanged
        for (ModelListener l : listeners)
            l.periodsChanged();
    }
    
    /** @return Returns the current ring buffer size. */
    public int getRingSize()
    {   return ring_size; }

    /** @param ring_size The ring_size to set. */
    public void setRingSize(int ring_size)
    {
        this.ring_size = ring_size;
        for (ModelItem item : items)
            item.setRingSize(ring_size);
    }

    /** @return Returns the number of chart items. */
    public int getNumItems()
    {   return items.size(); }
    
    /** @return Returns the chart item of given index. */
    public IModelItem getItem(int i)
    {   return items.get(i); }

    /** Add a new item to the model.
     * 
     *  @param pv_name The PV to add.
     *  @return Returns the newly added chart item.
     */
    public IModelItem add(String pv_name)
    {
        return add(pv_name, -1);
    }
    
    /** Add a new item to the model.
     * 
     *  @param pv_name The PV to add.
     *  @param axis_index The Y axis to use [0, 1, ...] or -1 for new axis.
     *  @return Returns the newly added chart item.
     */
    public IModelItem add(String pv_name, int axis_index)
    {
        int c = items.size();
        if (axis_index < 0)
        {
        	axis_index = 0;
            for (int i=0; i<c; ++i)
                if (axis_index < items.get(i).getAxisIndex() + 1)
                    axis_index = items.get(i).getAxisIndex() + 1;
        }
        int line_width = 0;
        return add(pv_name, axis_index, DefaultColors.getRed(c),
                DefaultColors.getGreen(c), DefaultColors.getBlue(c),
                line_width);
    }
    
    /** Add a new item to the model.
     * 
     *  @param pv_name The PV to add.
     *  @param axis_index The Y axis to use [0, 1, ...]
     *  @param red,
     *  @param green,
     *  @param blue The color to use.
     *  @param line_width The line width.
     *  @return Returns the newly added chart item, or <code>null</code>.
     */
    private IModelItem add(String pv_name, int axis_index,
            int red, int green, int blue, int line_width)
    {
        // Do not allow duplicate PV names.
        int i = findEntry(pv_name);
        if (i >= 0)
            return items.get(i);
        // Default low..high range
        double low = 0.0;
        double high = 10.0;
        final boolean visible = true;
        boolean auto_scale = false;
        boolean log_scale = false;
        TraceType trace_type = TraceType.Lines;
        // Use settings of existing item for that axis - if there is one
        for (ModelItem item : items)
            if (item.getAxisIndex() == axis_index)
            {
                low = item.getAxisLow();
                high = item.getAxisHigh();
                auto_scale = item.getAutoScale();
                log_scale = item.getLogScale();
                trace_type = item.getTraceType();
                break;
            }
        ModelItem item = new ModelItem(this, pv_name, ring_size,
        		axis_index, low, high, visible, auto_scale,
                red, green, blue, line_width, trace_type,
                log_scale);
        silentAdd(item);
        fireEntryAdded(item);
        return item;
    }

    /** Set axis limits of all items on given axis. */
    public void setAxisLimits(int axis_index, double low, double high)
    {
        for (ModelItem item : items)
        {
            if (item.getAxisIndex() != axis_index)
                continue;
            // Don't call setAxisMin(), Max(), since that would recurse.
            item.setAxisLimitsSilently(low, high);
            fireEntryConfigChanged(item);
        }
    }
    
    /** Set axis type (log, linear) of all items on given axis. */
    void setLogScale(int axis_index, boolean use_log_scale)
    {
        for (ModelItem item : items)
        {
            if (item.getAxisIndex() != axis_index)
                continue;
            if (item.getLogScale() != use_log_scale)
            {
                item.setLogScaleSilently(use_log_scale);
                fireEntryConfigChanged(item);
            }
        }
    }

    /** Set auto scale option of all items on given axis.
     *  <p>
     *  Also updates the auto scaling of all other items on same axis.
     */
    void setAutoScale(int axis_index, boolean use_auto_scale)
    {
        for (ModelItem item : items)
        {
            if (item.getAxisIndex() != axis_index)
                continue;
            if (item.getAutoScale() != use_auto_scale)
            {
                item.setAutoScaleSilently(use_auto_scale);
                fireEntryConfigChanged(item);
            }
        }
    }

    
    /** Add an archive data source to all items in the model.
     *  @see IModelItem#addArchiveDataSource(IArchiveDataSource)
     */
    public void addArchiveDataSource(IArchiveDataSource archive)
    {
        for (IModelItem item : items)
            item.addArchiveDataSource(archive);
    }
    
    /** As <code>add()</code>, but without listener notification.
     *  @see #add()
     */
    private void silentAdd(ModelItem item)
    {
        items.add(item);
        if (is_running)
            item.start();
    }
    
    /** Remove item with given PV name. */
    public void remove(String pv_name)
    {
        int i = findEntry(pv_name);
        if (i < 0)
            return;
        ModelItem item = items.remove(i);
        item.dispose();
        fireEntryRemoved(item);
    }
    
    /** @return Returns index of entry with given PV name or <code>-1</code>. */
    int findEntry(String pv_name)
    {
        for (int i=0; i<items.size(); ++i)
            if (items.get(i).getName().equals(pv_name))
                return i;
        return -1;
    }
    
    /** @return Returns <code>true</code> if running.
     *  @see #start
     *  @see #stop
     */
    public boolean isRunning()
    {
        return is_running;
    }
    
    /** Start the model (subscribe, ...) */
    public final void start()
    {
        if (!is_running)
        {
            for (ModelItem item : items)
                item.start();
            is_running = true;
        }
    }

    /** Stop the model (subscribe, ...) */
    public final void stop()
    {
        if (is_running)
        {
            for (ModelItem item : items)
                item.stop();
            is_running = false;
        }
    }
    
    /** Add all the current values to the chart items.
     *  <p>
     *  See long discussion in the description of the ChartModel.
     */
    public final ITimestamp addCurrentValuesToChartItems()
    {
        ITimestamp now = TimestampFactory.now();
        for (ModelItem item : items)
            item.addCurrentValueToSamples(now);
        return now;
    }
    
    /** @return Returns the whole model as an XML string. */
    @SuppressWarnings("nls")
    public String getXMLContent()
    {
        StringBuffer b = new StringBuffer(1024);
        b.append("<databrowser>\n");
        b.append("    <start>" + start_specification + "</start>\n");
        b.append("    <end>" + end_specification + "</end>\n");
        b.append("    <scan_period>" + scan_period + "</scan_period>\n");
        b.append("    <update_period>" + update_period + "</update_period>\n");
        b.append("    <ring_size>" + ring_size + "</ring_size>\n");
        b.append("    <pvlist>\n");
        for (ModelItem item : items)
            b.append(item.getXMLContent());
        b.append("    </pvlist>\n"); 
        b.append("</databrowser>");
        String s = b.toString();
        return s;
    }
    
    /** Load model from XML file stream. */
    public void load(InputStream stream) throws Exception
    {
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder();
        Document doc = docBuilder.parse(stream);
        loadFromDocument(doc);
    }
    
    /** Load model from DOM document. */
    @SuppressWarnings("nls")
    private void loadFromDocument(Document doc) throws Exception
    {
        boolean was_running = is_running;
        if (was_running)
            stop();
        disposeItems();

        // Check if it's a <databrowser/>.
        doc.getDocumentElement().normalize();
        Element root_node = doc.getDocumentElement();
        final String root_name = root_node.getNodeName();
        if (!root_name.equals("databrowser")) 
            throw new Exception("Expected <databrowser>, found <" + root_name
                    + ">");
        // Get the period entries
        start_specification = DOMHelper.getSubelementString(root_node, "start");
        end_specification = DOMHelper.getSubelementString(root_node, "end");
        if (start_specification.length() < 1  ||
            end_specification.length() < 1)
        {
            start_specification = DEFAULT_START_SPEC;
            end_specification = DEFAULT_END_SPEC;
        }
        double scan = DOMHelper.getSubelementDouble(root_node, "scan_period");
        double update = DOMHelper.getSubelementDouble(root_node, "update_period");
        ring_size = DOMHelper.getSubelementInt(root_node, "ring_size");
        Element pvlist = DOMHelper.findFirstElementNode(root_node
                .getFirstChild(), "pvlist");
        if (pvlist != null)
        {
            Element pv = DOMHelper.findFirstElementNode(
            		pvlist.getFirstChild(), ModelItem.TAG_PV);
            while (pv != null)
            {
                silentAdd(ModelItem.loadFromDOM(this, pv, ring_size));
                pv = DOMHelper.findNextElementNode(pv, ModelItem.TAG_PV);
            }
        }
        // This also notifies listeners about the new periods:
        setPeriods(scan, update);
        fireEntriesChanged();
        if (was_running)
            start();
    }

    /** @see ModelListener#entryConfigChanged(IModelItem) */
    void fireEntryConfigChanged(IModelItem item)
    {
        for (ModelListener l : listeners)
            l.entryConfigChanged(item);
    }
    
    /** @see ModelListener#entryLookChanged(IModelItem) */
    void fireEntryLookChanged(IModelItem item)
    {
        for (ModelListener l : listeners)
            l.entryLookChanged(item);
    }
    
    /** @see ModelListener#entryArchivesChanged(IModelItem) */
    void fireEntryArchivesChanged(IModelItem item)
    {
        for (ModelListener l : listeners)
            l.entryArchivesChanged(item);
    }

    /** @see ModelListener#entryAdded(IModelItem) */
    void fireEntryAdded(ModelItem item)
    {
        for (ModelListener l : listeners)
            l.entryAdded(item);
    }
        
    /** @see ModelListener#entryRemoved(IModelItem) */
    void fireEntryRemoved(ModelItem item)
    {
        for (ModelListener listener : listeners)
            listener.entryRemoved(item);
    }

    /** @see ModelListener#entriesChanged() */
    private void fireEntriesChanged()
    {
        for (ModelListener l : listeners)
            l.entriesChanged();
    }
}
