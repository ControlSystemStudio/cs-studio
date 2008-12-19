package org.csstudio.trends.databrowser.model;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.csstudio.apputil.time.RelativeTime;
import org.csstudio.apputil.time.StartEndTimeParser;
import org.csstudio.apputil.xml.DOMHelper;
import org.csstudio.apputil.xml.XMLHelper;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.swt.chart.DefaultColors;
import org.csstudio.swt.chart.TraceType;
import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.preferences.Preferences;
import org.eclipse.swt.graphics.RGB;
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
    /** If the defaults from prefs aren't usable, this is the start spec. */
    private static final String FALLBACK_START_TIME = "-10 min"; //$NON-NLS-1$

    /** Background, foreground, grid colors */
    private RGB background = new RGB(255, 255, 255),
    		    foreground = new RGB(0, 0, 0),
    		    grid_color = new RGB(0, 0, 0);

	/** Start- and end time specifications. */
    private StartEndTimeParser start_end_times;
    
    /** Should we scroll or not?
     *  When the end time is absolute, we certainly don't scroll.
     *  But if it's 'now', we can still see that as a one-time
     *  request, or as a continually updated 'now', i.e.
     *  a scrolling mode.
     */
    private boolean scroll;
    
    private ArchiveRescale archive_rescale;

    /** Scan period for 'live' data in seconds. */
    private double scan_period = 0.5;
    
    /** Update period of the plot in seconds. */
    private double update_period = 1.0;
    
    /** Ring buffer size, number of elements, for 'live' data. */
    private int live_buffer_size = 1024;

    /** Items (PVs, Formulas) in model */
    private ArrayList<AbstractModelItem> items = new ArrayList<AbstractModelItem>();
    
    /** Plot Markers, index by YAxis and marker on that axis */
    private MarkerInfo markers[][] = new MarkerInfo[0][0];
    
    /** <code>true</code> if all model items were 'started'. */
    private boolean is_running = false;

    private ArrayList<ModelListener> listeners = 
        new ArrayList<ModelListener>();

	/** Construct a new model. */
    @SuppressWarnings("nls")
    public Model()
    {
        String start, end;
        try
        {
            start = Preferences.getStartSpecification();
            end = Preferences.getEndSpecification();
            setPeriods(Preferences.getScanPeriod(),
                    Preferences.getUpdatePeriod());
            setLiveBufferSize(Preferences.getLiveBufferSize());
            archive_rescale = Preferences.getArchiveRescale();
        }
        catch (Exception ex)
        {   // No prefs because running as unit test?
            start = FALLBACK_START_TIME;
            end = RelativeTime.NOW;
        }
        try
        {
            // Set time defaults, since otherwise start/end would be null.
            setTimeSpecifications(start, end);
        }
        catch (Exception ex)
        {
            Plugin.getLogger().error("Cannot init. time range", ex);
        }
        scroll = start_end_times.isEndNow();
    }
    
    /** Must be called to dispose the model. */
    public void dispose()
    {
        disposeItems();
    }

    /** Peoperly clear the item list. */
    private void disposeItems()
    {
        for (AbstractModelItem item : items)
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

    /** @param Background color */
    public void setPlotBackground(final RGB color)
    {
    	background  = color;
    	firePlotColorsChanged();
    }
    
    /** @return Background color */
    public RGB getPlotBackground()
	{
		return background;
	}

    /** @param Foreground color */
    public void setPlotForeground(final RGB color)
    {
    	foreground  = color;
        firePlotColorsChanged();
    }
    
    /** @return Foreground color */
    public RGB getPlotForeground()
	{
		return foreground;
	}

    /** @param Grid color */
    public void setPlotGrid(final RGB color)
    {
    	grid_color  = color;
        firePlotColorsChanged();
    }
    
    /** @return Grid color */
    public RGB getPlotGrid()
	{
		return grid_color;
	}

    /** Set markers
     *  @param markers 2D array of markers.
     *                 First array index iterates over Y-Axes,
     *                 second array index over markers for that Y-Axis.
     */
    public void setMarkers(final MarkerInfo markers[][])
    {
        this.markers = markers;
        fireMarkersChanged();
    }
    
    /** @return Markers, indexed by Y-Axis, then marker on that axis. */
    public MarkerInfo[][] getMarkers()
    {
        return markers;
    }

    /** Set a new start and end time specification.
     *  <p>
     *  Also updates the current start and end time with
     *  values computed from the specs "right now".
     *  @see org.csstudio.apputil.time.StartEndTimeParser
     *  @see #getStartSpecification()
     *  @see #setTimeRange(ITimestamp, ITimestamp)
     *  @exception Exception on parse error of specs.
     */
    public void setTimeSpecifications(String start_specification,
                                      String end_specification) 
        throws Exception
    {
        start_end_times =
            new StartEndTimeParser(start_specification, end_specification);
        final ITimestamp start =
            TimestampFactory.fromCalendar(start_end_times.getStart());
        final ITimestamp end =
            TimestampFactory.fromCalendar(start_end_times.getEnd());
        if (start.isGreaterOrEqual(end))
            start_end_times =
                new StartEndTimeParser(FALLBACK_START_TIME,
                                       RelativeTime.NOW);
        // In case of parse errors, we won't reach this point
        // fireTimeSpecificationsChanged, fireTimeRangeChanged
        for (ModelListener l : listeners)
        {
            l.timeSpecificationsChanged();
            l.timeRangeChanged();
        }
    }
    
    /** Get the start specification that is held 'permamently' when
     *  the model is saved and re-loaded.
     *  <p>
     *  When the specifications are initially loaded or later changed,
     *  the current start and end time is computed from the specs.
     *  <p>
     *  At runtime, scroll operations will update the currently
     *  displayed start and end time by re-evaluating a
     *  relative start specification of for example "-30 min",
     *  but that won't affect the actual start/end specification.
     *  <p>
     *  The config view has buttons to force an update of the specification
     *  from the current start/end times and vice versa.
     *
     *  @see #getStartTime()
     *  @return Start specification.
     */
    public String getStartSpecification()
    {   return start_end_times.getStartSpecification();  }

    /** @see #getStartSpecification()
     *  @return End specification.
     */
    public String getEndSpecification()
    {   return start_end_times.getEndSpecification(); }
    
    /** Re-evaluate the start/end specifications.
     *  <p>
     *  In case of absolute start/end time specs, nothing changes.
     *  For relative start/end time specs, the 'current' start and
     *  end times get updated.
     */
    public void updateStartEndTime()
    {
        try
        {
            if (start_end_times.eval())
            {
                // fireTimeRangeChanged
                for (ModelListener l : listeners)
                    l.timeRangeChanged();
            }
        }
        catch (Exception ex)
        {
            Plugin.getLogger().error("Model start/end time update error", ex); //$NON-NLS-1$
        }
    }
    
    /** The start time according to the most recent evaluation
     *  of the start specification.
     *  This is the time where the plot should start.
     *  @see #getStartSpecification()
     *  @return Start time.
     */
    public ITimestamp getStartTime()
    {   return TimestampFactory.fromCalendar(start_end_times.getStart()); }
    
    /** The end time according to the most recent evaluation
     *  of the end specification.
     *  This is the time where the plot should end.
     *  @see #getStartTime()
     *  @return End time.
     */
    public ITimestamp getEndTime()
    {   return TimestampFactory.fromCalendar(start_end_times.getEnd()); }

    /** @return <code>true</code> if the end time is 'now', i.e. we
     *          should continually scroll.
     */
    public boolean isScrollEnabled()
    {
        return scroll;
    }
    
    /** Enable or disable the scroll mode. */
    public void enableScroll(final boolean scroll)
    {
        this.scroll = scroll;
        for (ModelListener listener : listeners)
            listener.timeSpecificationsChanged();
    }
    
    /** Configure archive rescale behavior
     *  @param rescale New setting
     */
    public void setArchiveRescale(final ArchiveRescale rescale)
    {
        archive_rescale = rescale;
        // Not the ideal event, but it suffices to mark the model 'dirty'
        fireSamplingChanged();
    }
    
    /** @return Archive data rescale behavior */
    public ArchiveRescale getArchiveRescale()
    {
        return archive_rescale;
    }

    /** @return Returns the scan period in seconds. */
    public double getScanPeriod()
    {   return scan_period; }

    /** @return Returns the update period in seconds. */
    public double getUpdatePeriod()
    {   return update_period; }
    
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
        if (scan < Preferences.MIN_SCAN_PERIOD)
            scan = Preferences.MIN_SCAN_PERIOD;
        if (update < Preferences.MIN_UPDATE_PERIOD)
            update = Preferences.MIN_UPDATE_PERIOD;
        // No sense in redrawing faster than the data can change.
        if (update < scan)
            update = scan;
        scan_period = scan;
        update_period = update;
        fireSamplingChanged();
    }

    /** @return Returns the current ring buffer size. */
    public int getLiveBufferSize()
    {   return live_buffer_size; }

    /** @param ring_size The ring_size to set.
     *  @throws Exception on out-of-mem error
     */
    public void setLiveBufferSize(final int ring_size) throws Exception
    {
        for (AbstractModelItem item : items)
        {
            if (item instanceof PVModelItem)
                ((PVModelItem)item).setLiveBufferSize(ring_size);
        }
        this.live_buffer_size = ring_size;
        fireSamplingChanged();
    }

    /** @return Returns the number of chart items. */
    public int getNumItems()
    {   return items.size(); }
    
    /** @return Returns the chart item of given index. */
    public IModelItem getItem(int i)
    {   return items.get(i); }

    /** Locate a model item by name.
     *  @param name The PV or formula name to locate.
     *  @return The model item with given name or <code>null</code>.
     */
    public IModelItem findItem(final String name)
    {
        for (IModelItem item : items)
            if (item.getName().equals(name))
                return item;
        return null;
    }
    
    public enum ItemType
    {
        /** A live or archived PV */
        ProcessVariable,
        /** A computed item */
        Formula
    };

    /** Add a new item to the model.
     * 
     *  @param pv_name The PV to add.
     *  @return Returns the newly added chart item.
     */
    public IPVModelItem addPV(String pv_name)
    {
        return (IPVModelItem) add(ItemType.ProcessVariable, pv_name, -1);
    }
    
    /** Add a new item to the model.
     * 
     *  @param pv_name The PV to add.
     *  @param axis_index The Y axis to use [0, 1, ...] or -1 for new axis.
     *  @return Returns the newly added chart item.
     */
    public IPVModelItem addPV(String pv_name, int axis_index)
    {
        return (IPVModelItem) add(ItemType.ProcessVariable, pv_name, axis_index);
    }
    
    /** Add the default archive data sources as per Preferences to item */
    public void addDefaultArchiveSources(IPVModelItem pv_item)
    {
        IArchiveDataSource archives[] = Preferences.getArchiveDataSources();
        for (IArchiveDataSource arch : archives)
            pv_item.addArchiveDataSource(arch);
    }

    /** Add a new item to the model.
     * 
     *  @param type Describes the type of PV to add
     *  @param pv_name The PV to add.
     *  @return Returns the newly added chart item.
     */
    public IModelItem add(ItemType type, String pv_name)
    {
        return add(type, pv_name, -1);
    }

    /** Add a new item to the model.
     *
     *  @param type Describes the type of PV to add
     *  @param pv_name The PV to add.
     *  @param axis_index The Y axis to use [0, 1, ...] or -1 for new axis.
     *  @return Returns the newly added chart item.
     */
    public IModelItem add(ItemType type, String pv_name, int axis_index)
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
        return add(type, pv_name, axis_index, DefaultColors.getRed(c),
                DefaultColors.getGreen(c), DefaultColors.getBlue(c),
                line_width);
    }
    
    /** Add a new item to the model.
     * 
     *  @param type Describes the type of PV to add
     *  @param pv_name The PV to add.
     *  @param axis_index The Y axis to use [0, 1, ...]
     *  @param red,
     *  @param green,
     *  @param blue The color to use.
     *  @param line_width The line width.
     *  @return Returns the newly added chart item, or <code>null</code>.
     */
    private IModelItem add(ItemType type, String pv_name, int axis_index,
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
        boolean auto_scale;
        try
        {
            auto_scale = Preferences.getAutoScale();
        }
        catch (Exception ex)
        {   // No prefs because in unit test
            auto_scale = false;
        }
        boolean log_scale = false;
        // Default trace type for new items
        TraceType trace_type = TraceType.Area;
        // Use settings of existing item for that axis - if there is one
        for (IModelItem item : items)
            if (item.getAxisIndex() == axis_index)
            {
                low = item.getAxisLow();
                high = item.getAxisHigh();
                auto_scale = item.getAutoScale();
                log_scale = item.getLogScale();
                trace_type = item.getTraceType();
                break;
            }
        AbstractModelItem item = null;
        switch (type)
        {
        case ProcessVariable:
            item = new PVModelItem(this, pv_name, live_buffer_size,
                            		axis_index, low, high, visible, auto_scale,
                                    red, green, blue, line_width, trace_type,
                                    log_scale,
                                    IPVModelItem.RequestType.OPTIMIZED);
            break;
        case Formula:
            item = new FormulaModelItem(this, pv_name, 
                                    axis_index, low, high, visible, auto_scale,
                                    red, green, blue, line_width, trace_type,
                                    log_scale);
            if (items.size() > 0)
            {  
                // Create a dummy example formula
                // that doubles the first PV
                FormulaInput inputs[] = new FormulaInput[]
                {
                    new FormulaInput(items.get(0), "x") //$NON-NLS-1$
                };
                try
                {
                    ((FormulaModelItem)item).setFormula("2*x", inputs); //$NON-NLS-1$
                }
                catch (Exception ex)
                {
                    Plugin.getLogger().error("Setting formula", ex); //$NON-NLS-1$
                }
            }
            break;
        }
        silentAdd(item);
        fireEntryAdded(item);
        return item;
    }

    /** Set axis limits of all items on given axis. */
    public void setAxisLimits(final int axis_index,
                              final double low, final double high)
    {
        for (AbstractModelItem item : items)
        {
            if (item.getAxisIndex() == axis_index &&
                (item.getAxisLow()  != low ||
                 item.getAxisHigh() != high))
            {
                // Don't call setAxisMin(), Max(), since that would recurse.
                item.setAxisLimitsSilently(low, high);
                fireEntryConfigChanged(item);
            }
        }
    }
    
    /** Set axis visibility of all items on given axis. */
    void setAxisVisible(final int axis_index, final boolean visible)
    {
        for (AbstractModelItem item : items)
        {
            if (item.getAxisIndex() == axis_index &&
                item.isAxisVisible() != visible)
            {
                item.setAxisVisibleSilently(visible);
                fireEntryConfigChanged(item);
            }
        }
    }

    /** Set axis type (log, linear) of all items on given axis. */
    void setLogScale(final int axis_index, final boolean use_log_scale)
    {
        for (AbstractModelItem item : items)
        {
            if (item.getAxisIndex() == axis_index  &&
                item.getLogScale() != use_log_scale)
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
    void setAutoScale(final int axis_index, final boolean use_auto_scale)
    {
        for (AbstractModelItem item : items)
        {
            if (item.getAxisIndex() == axis_index  &&
                item.getAutoScale() != use_auto_scale)
            {
                item.setAutoScaleSilently(use_auto_scale);
                fireEntryConfigChanged(item);
            }
        }
    }
    
    /** Add an archive data source to all items in the model.
     *  @see IModelItem#addArchiveDataSource(IArchiveDataSource)
     */
    public void addArchiveDataSource(final IArchiveDataSource archive)
    {
        for (IModelItem item : items)
            if (item instanceof IPVModelItem)
                ((IPVModelItem) item).addArchiveDataSource(archive);
    }
    
    /** As <code>add()</code>, but without listener notification.
     *  @see #add()
     */
    private void silentAdd(final AbstractModelItem item)
    {
        items.add(item);
        if (is_running  &&  item instanceof PVModelItem)
            ((PVModelItem)item).start();
    }
    
    /** Remove item with given PV name. */
    public void remove(final String pv_name)
    {
        int i = findEntry(pv_name);
        if (i < 0)
            return;
        final AbstractModelItem item = items.remove(i);
        item.dispose();
        fireEntryRemoved(item);
    }
    
    /** @return Returns index of entry with given PV name or <code>-1</code>. */
    private int findEntry(final String pv_name)
    {
        for (int i=0; i<items.size(); ++i)
            if (items.get(i).getName().equals(pv_name))
                return i;
        return -1;
    }
    
    /** Check if a PV is used in a formula.
     *  @param pv_name PV to check
     *  @return Name of the formula that uses the PV, or <code>null</code>.
     */
    public String isUsedInFormula(final String pv_name)
    {
        for (int i=0; i<items.size(); ++i)
        {
            final AbstractModelItem item = items.get(i);
            if (item instanceof FormulaModelItem)
            {
                final FormulaModelItem formula = (FormulaModelItem) item;
                if (formula.usesInputPV(pv_name))
                    return formula.getName();
            }
        }
        return null;
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
            for (AbstractModelItem item : items)
                if (item instanceof PVModelItem)
                    ((PVModelItem)item).start();
            is_running = true;
        }
    }

    /** Stop the model (subscribe, ...) */
    public final void stop()
    {
        if (is_running)
        {
            for (AbstractModelItem item : items)
                if (item instanceof PVModelItem)
                    ((PVModelItem)item).stop();
            is_running = false;
        }
    }
    
    /** Scan PVs. */
    public final void scan()
    {
        final ITimestamp now = TimestampFactory.now();
        for (AbstractModelItem item : items)
            if (item instanceof PVModelItem)
                ((PVModelItem)item).addCurrentValueToSamples(now);
    }

    /** Update (re-compute) formulas. */
    public final void updateFormulas()
    {
        for (AbstractModelItem item : items)
            if (item instanceof FormulaModelItem)
                ((FormulaModelItem)item).compute();
    }
    
    /** @return Returns the whole model as an XML string. */
    @SuppressWarnings("nls")
    public String getXMLContent()
    {
        final StringBuilder b = new StringBuilder(1024);
        b.append("<databrowser>\n");
        appendColorXML(b, "background", background);
        appendColorXML(b, "foreground", foreground);
        appendColorXML(b, "grid_color", grid_color);
        XMLHelper.XML(b, 1, "start", start_end_times.getStartSpecification());
        XMLHelper.XML(b, 1, "end", start_end_times.getEndSpecification());
        XMLHelper.XML(b, 1, "scroll", Boolean.toString(scroll));
        XMLHelper.XML(b, 1, "archive_rescale", archive_rescale.name());        
        XMLHelper.XML(b, 1, "scan_period", Double.toString(scan_period));
        XMLHelper.XML(b, 1, "update_period", Double.toString(update_period));
        XMLHelper.XML(b, 1, "ring_size", Integer.toString(live_buffer_size));
        b.append("    <pvlist>\n");
        for (AbstractModelItem item : items)
            b.append(item.getXMLContent());
        b.append("    </pvlist>\n");
        b.append("    <markers>\n");
        for (int y=0; y<markers.length; ++y)
        {
            b.append("        <axis y=\"" + y + "\">\n");
            for (MarkerInfo marker : markers[y])
            {
                b.append("            " + marker.getXML() + "\n");
            }
            b.append("        </axis>\n");
        }
        b.append("    </markers>\n");
        b.append("</databrowser>");
        String s = b.toString();
        return s;
    }
    
    /** Add XML for color to buffer
     *  @param buf Buffer
     *  @param tag Tag name to use for color
     *  @param color Color
     */
    @SuppressWarnings("nls")
	private void appendColorXML(final StringBuilder buf, final String tag, final RGB color)
    {
        buf.append("    <" + tag + ">\n");
        XMLHelper.XML(buf, 2, AbstractModelItem.TAG_RED, Integer.toString(color.red));
        XMLHelper.XML(buf, 2, AbstractModelItem.TAG_GREEN, Integer.toString(color.green));
        XMLHelper.XML(buf, 2, AbstractModelItem.TAG_BLUE, Integer.toString(color.blue));
        buf.append("    </" + tag + ">\n");
	}

	/** Load model from XML file stream. */
    public void load(final InputStream stream) throws Exception
    {
        final DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder();
        final Document doc = docBuilder.parse(stream);
        loadFromDocument(doc);
    }
    
    /** Load model from DOM document. */
    @SuppressWarnings("nls")
    private void loadFromDocument(final Document doc) throws Exception
    {
        final boolean was_running = is_running;
        if (was_running)
            stop();
        disposeItems();

        // Check if it's a <databrowser/>.
        doc.getDocumentElement().normalize();
        final Element root_node = doc.getDocumentElement();
        final String root_name = root_node.getNodeName();
        if (!root_name.equals("databrowser")) 
            throw new Exception("Expected <databrowser>, found <" + root_name
                    + ">");
        
        // Get back/fore/grid colors
    	int[] rgb = AbstractModelItem.loadColorFromDOM(root_node,
    			"background", null);
    	if (rgb != null)
        	background = new RGB(rgb[0], rgb[1], rgb[2]);
    	rgb = AbstractModelItem.loadColorFromDOM(root_node,
    			"foreground", null);
    	if (rgb != null)
        	foreground = new RGB(rgb[0], rgb[1], rgb[2]);
    	rgb = AbstractModelItem.loadColorFromDOM(root_node,
    			"grid_color", null);
    	if (rgb != null)
        	grid_color = new RGB(rgb[0], rgb[1], rgb[2]);

        // Get the period entries
        String start_specification = DOMHelper.getSubelementString(root_node, "start");
        String end_specification = DOMHelper.getSubelementString(root_node, "end");
        if (start_specification.length() < 1  ||
            end_specification.length() < 1)
        {
            start_specification = Preferences.getStartSpecification();
            end_specification = Preferences.getEndSpecification();
        }
        start_end_times = new StartEndTimeParser(start_specification,
                                                 end_specification);
        scroll = DOMHelper.getSubelementBoolean(root_node, "scroll",
                                                start_end_times.isEndNow());
        try
        {
            archive_rescale = ArchiveRescale.valueOf(DOMHelper.getSubelementString(root_node, "archive_rescale"));
        }
        catch (Throwable ex)
        {
            archive_rescale = ArchiveRescale.STAGGER;
        }
        final double scan = DOMHelper.getSubelementDouble(root_node, "scan_period");
        final double update = DOMHelper.getSubelementDouble(root_node, "update_period");
        live_buffer_size = DOMHelper.getSubelementInt(root_node, "ring_size");
        final Element pvlist = DOMHelper.findFirstElementNode(root_node
                .getFirstChild(), "pvlist");
        if (pvlist != null)
        {
            // Load the PV items
            Element pv = DOMHelper.findFirstElementNode(
            		pvlist.getFirstChild(), PVModelItem.TAG_PV);
            while (pv != null)
            {
                silentAdd(PVModelItem.loadFromDOM(this, pv, live_buffer_size));
                pv = DOMHelper.findNextElementNode(pv, PVModelItem.TAG_PV);
            }
            // Load the Formula items
            pv = DOMHelper.findFirstElementNode(
                    pvlist.getFirstChild(), PVModelItem.TAG_FORMULA);
            while (pv != null)
            {
                silentAdd(FormulaModelItem.loadFromDOM(this, pv));
                pv = DOMHelper.findNextElementNode(pv, PVModelItem.TAG_FORMULA);
            }
        }
        final Element markers_node = DOMHelper.findFirstElementNode(root_node
                .getFirstChild(), "markers");
        loadMarkersFromNode(markers_node);
        
        // This also notifies listeners about the new periods:
        setPeriods(scan, update);
        fireEntriesChanged();
        if (was_running)
            start();
    }

    /** Load {@link MarkerInfo} elements from XML
     *  @param markers_node Node where to start
     *  @throws Exception on error
     */
    @SuppressWarnings("nls")
    private void loadMarkersFromNode(final Element markers_node) throws Exception
    {
        if (markers_node == null)
            return;
        // <axis y=1>
        //  <marker><position>...</position><value>...</value><text>...</text></marker>
        // </axis>
        final ArrayList<ArrayList<MarkerInfo>> mark_array =
            new ArrayList<ArrayList<MarkerInfo>>();
        Element axis = DOMHelper.findFirstElementNode(markers_node.getFirstChild(), "axis");
        while (axis != null)
        {
            int y = Integer.parseInt(axis.getAttribute("y"));
            while (mark_array.size() <= y)
                mark_array.add(new ArrayList<MarkerInfo>());
            Element marker_node = DOMHelper.findFirstElementNode(axis.getFirstChild(), "marker");
            while (marker_node != null)
            {
                final MarkerInfo marker = MarkerInfo.fromDOM(marker_node);
                mark_array.get(y).add(marker);
                marker_node = DOMHelper.findNextElementNode(marker_node, "marker");
            }
            axis = DOMHelper.findNextElementNode(axis, "axis");
        }
        
        // Convert to plain array
        markers = new MarkerInfo[mark_array.size()][];
        for (int y=0; y<markers.length; ++y)
            markers[y] = mark_array.get(y).toArray(new MarkerInfo[mark_array.get(y).size()]);
    }

    /** @see ModelListener#plotColorsChangedChanged() */
    private void firePlotColorsChanged()
    {
        for (ModelListener l : listeners)
            l.plotColorsChangedChanged();
    }
    
    /** @see ModelListener#markersChanged() */
    private void fireMarkersChanged()
    {
        for (ModelListener l : listeners)
            l.markersChanged();
    }

    /** @see ModelListener#entryConfigChanged(IModelItem) */
    void fireEntryConfigChanged(IModelItem item)
    {
        for (ModelListener l : listeners)
            l.entryConfigChanged(item);
    }
    
    /** @see ModelListener#fireSamplingChanged() */
    private void fireSamplingChanged()
    {
        for (ModelListener l : listeners)
        {
            try
            {
                l.samplingChanged();
            }
            catch (Throwable ex)
            {
                Plugin.getLogger().error(ex);
            }
        }
    }
    
    /** @see ModelListener#entryMetaDataChanged(IModelItem) */
    void fireEntryMetadataChanged(IModelItem item)
    {
        for (ModelListener l : listeners)
            l.entryMetaDataChanged(item);
    }
    
    /** @see ModelListener#entryArchivesChanged(IModelItem) */
    void fireEntryArchivesChanged(IModelItem item)
    {
        for (ModelListener l : listeners)
            l.entryArchivesChanged(item);
    }

    /** @see ModelListener#entryAdded(IModelItem) */
    void fireEntryAdded(IModelItem item)
    {
        for (ModelListener l : listeners)
            l.entryAdded(item);
    }
        
    /** @see ModelListener#entryRemoved(IModelItem) */
    void fireEntryRemoved(IModelItem item)
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

    /** @return Some string description of Model for debug/log purposes */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        if (items.size() <= 0)
            return "empty Model";
        return "Model (" + items.get(0).getName() + ", ...";
    }
}
