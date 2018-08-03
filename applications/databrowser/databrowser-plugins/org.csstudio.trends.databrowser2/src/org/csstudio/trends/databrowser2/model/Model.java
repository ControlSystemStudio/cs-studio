/*******************************************************************************
 * Copyright (c) 2010-2015 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.csstudio.apputil.macros.IMacroTableProvider;
import org.csstudio.apputil.macros.InfiniteLoopException;
import org.csstudio.apputil.macros.MacroTable;
import org.csstudio.apputil.macros.MacroUtil;
import org.csstudio.apputil.time.PeriodFormat;
import org.csstudio.apputil.time.RelativeTime;
import org.csstudio.apputil.time.StartEndTimeParser;
import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.swt.rtplot.util.RGBFactory;
import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.imports.ImportArchiveReaderFactory;
import org.csstudio.trends.databrowser2.preferences.Preferences;
import org.diirt.util.time.TimeDuration;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

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

    /** Should UI ask to save changes to the model? */
    final private AtomicBoolean save_changes = new AtomicBoolean(true);

    /** Default colors for newly added item */
    final private RGBFactory default_colors = new RGBFactory();

    /** Macros */
    private volatile IMacroTableProvider macros = new MacroTable(Collections.emptyMap());

    /** Listeners to model changes */
    final private List<ModelListener> listeners = new CopyOnWriteArrayList<>();

    /** Title */
    private volatile Optional<String> title = Optional.empty();

    /** Axes configurations */
    final private List<AxisConfig> axes = new CopyOnWriteArrayList<AxisConfig>();

    /** All the items in this model */
    final private List<ModelItem> items = new CopyOnWriteArrayList<ModelItem>();

    /** 'run' flag
     *  @see #start()
     *  @see #stop()
     */
    private volatile boolean is_running = false;

    /** Period in seconds for scrolling or refreshing */
    private volatile double update_period = Preferences.getUpdatePeriod();

    /** <code>true</code> if scrolling is enabled */
    private volatile boolean scroll_enabled = true;

    /** Scroll steps */
    private volatile Duration scroll_step = Preferences.getScrollStep();

    /** Start and end time specification */
    private volatile String start_spec, end_spec;

    /** Time span of data in seconds */
    private volatile Duration time_span = Preferences.getTimeSpan();

    /** End time of the data range */
    private volatile Instant end_time = Instant.now();

    private final boolean automaticHistoryRefresh = Preferences.isAutomaticHistoryRefresh();

    /** Show time axis grid line? */
    private volatile boolean show_grid = false;

    /** Background color */
    private volatile RGB background = new RGB(255, 255, 255);

    /** Title font */
    private volatile FontData title_font = new FontData("", 15, 0);

    /** Label font */
    private volatile FontData label_font = new FontData("", 10, 0);

    /** Scale font */
    private volatile FontData scale_font = new FontData("", 10, 0);

    /** Legend font */
    private volatile FontData legend_font = new FontData("", 10, 0);

    /** Annotations */
    private volatile List<AnnotationInfo> annotations = Collections.emptyList();

    /** How should plot rescale when archived data arrives? */
    private volatile ArchiveRescale archive_rescale = Preferences.getArchiveRescale();

    /** Show toolbar*/
    private boolean show_toolbar = true;

    /** Show legend*/
    private boolean show_legend = false;

    public Model()
    {
        final Display display = Display.getCurrent();
        if (display != null)
        {   // Based on system font, use BOLD for labels, and smaller version for scales
            final FontData default_font = display.getSystemFont().getFontData()[0];
            title_font = new FontData(default_font.getName(), (default_font.getHeight()*3)/2, SWT.BOLD);
            label_font = new FontData(default_font.getName(), default_font.getHeight(), SWT.BOLD);
            scale_font = new FontData(default_font.getName(), default_font.getHeight()-1, SWT.NORMAL);
            legend_font = new FontData(default_font.getName(), default_font.getHeight()-1, SWT.NORMAL);
        }
        start_spec = "-" + PeriodFormat.formatSeconds(TimeDuration.toSecondsDouble(time_span));
        end_spec = RelativeTime.NOW;
    }

    /** @return Should UI ask to save changes to the model? */
    public boolean shouldSaveChanges()
    {
        return save_changes.get();
    }

    /** @param save_changes Should UI ask to save changes to the model? */
    public void setSaveChanges(final boolean save_changes)
    {
        if (this.save_changes.getAndSet(save_changes) != save_changes)
            for (ModelListener listener : listeners)
                listener.changedSaveChangesBehavior(save_changes);
    }

    /** @param macros Macros to use in this model */
    public void setMacros(final IMacroTableProvider macros)
    {
        this.macros = Objects.requireNonNull(macros);
    }

    /** Resolve macros
     *  @param text Text that might contain "$(macro)"
     *  @return Text with all macros replaced by their value
     */
    public String resolveMacros(final String text)
    {
        try
        {
            return MacroUtil.replaceMacros(text, macros);
        }
        catch (InfiniteLoopException ex)
        {
            Activator.getLogger().log(Level.WARNING,
                    "Problem in macro {0}: {1}", new Object[] { text, ex.getMessage()});
            return text;
        }
    }

    /** @param listener New listener to notify */
    public void addListener(final ModelListener listener)
    {
        listeners.add(Objects.requireNonNull(listener));
    }

    /** @param listener Listener to remove */
    public void removeListener(final ModelListener listener)
    {
        listeners.remove(Objects.requireNonNull(listener));
    }

    /** @param title Title, may be <code>null</code> or empty */
    public void setTitle(final String title)
    {
        if (title == null  ||   title.isEmpty())
            this.title = Optional.empty();
        else
            this.title = Optional.of(title);
        for (ModelListener listener : listeners)
            listener.changedTitle();
    }

    /** @return Title */
    public Optional<String> getTitle()
    {
        return title;
    }

    /** @return Read-only, thread safe {@link AxisConfig}s */
    public Iterable<AxisConfig> getAxes()
    {
        return axes;
    }

    /** Get number of axes
     *
     *  <p>Thread-safe access to multiple axes should use <code>getAxes()</code>
     *
     *  @return Number of axes
     */
    public int getAxisCount()
    {
        return axes.size();
    }

    /** Get specific axis. If the axis for the specified index does not exist, method returns null.
     *
     *  <p>Thread-safe access to multiple axes should use <code>getAxes()</code>
     *
     *  @param index Axis index
     *  @return {@link AxisConfig} or null if the config for the given index does not exist
     */
    public AxisConfig getAxis(final int index)
    {
        try
        {
            return axes.get(index);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
    }

    /** Locate index of value axis
     *  @param axis Value axis configuration
     *  @return Index of axis (0, ...) or -1 if not in Model
     */
    public int getAxisIndex(final AxisConfig axis)
    {
        return axes.indexOf(Objects.requireNonNull(axis));
    }

    /** @param axis Axis to test
     *  @return First ModelItem that uses the axis, visible or not.
     *          <code>null</code> if axis is empty
     */
    public ModelItem getFirstItemOnAxis(final AxisConfig axis)
    {
        Objects.requireNonNull(axis);
        for (ModelItem item : items)
            if (item.getAxis() == axis)
                return item;
        return null;
    }

    /** @param axis Axis to test
     *  @return <code>true</code> if there is any visible item on the axis
     */
    public boolean hasAxisActiveItems(final AxisConfig axis)
    {
        Objects.requireNonNull(axis);
        for (ModelItem item : items)
            if (item.getAxis() == axis && item.isVisible())
                return true;
        return false;
    }

    /** @return First unused axis (no items on axis) */
    public Optional<AxisConfig> getEmptyAxis()
    {
        for (AxisConfig axis : axes)
            if (getFirstItemOnAxis(axis) == null)
                return Optional.of(axis);
        return Optional.empty();
    }

    /** Add value axis with default settings
     *  Sets name of new axis to Value N
     *  N is found by searching for all the existing axes with the name Value X; N is set to the highest value of X found + 1
     *  (this scheme should avoid the creation of axes with duplicate names of the format Value N)
     *  @return Newly added axis configuration
     */
    public AxisConfig addAxis()
    {
        final String regex = Messages.Plot_ValueAxisName + " \\d+";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        int max_default_axis_num = 0;
        for (AxisConfig axis : axes) {
            String existing_axis_name = axis.getName();
            Matcher matcher = pattern.matcher(existing_axis_name);
            while (matcher.find()) {
                int default_axis_num = Integer.parseInt(matcher.group(0).replace(Messages.Plot_ValueAxisName+" ",""));
                if (default_axis_num > max_default_axis_num)
                    max_default_axis_num = default_axis_num;
            }
        }
        final String name = NLS.bind(Messages.Plot_ValueAxisNameFMT, max_default_axis_num + 1);
        final AxisConfig axis = new AxisConfig(name);
        axis.setColor(new RGB(0, 0, 0));
        addAxis(axis);
        return axis;
    }

    /** @param axis New axis to add */
    public void addAxis(final AxisConfig axis)
    {
        axes.add(Objects.requireNonNull(axis));
        axis.setModel(this);
        fireAxisChangedEvent(Optional.empty());
    }

    /** Add axis at given index.
     *  Adding at '1' means the new axis will be at index '1',
     *  and what used to be at '1' will be at '2' and so on.
     *  @param index Index where axis will be placed.
     *  @param axis New axis to add
     */
    public void addAxis(final int index, final AxisConfig axis)
    {
        axes.add(index, Objects.requireNonNull(axis));
        axis.setModel(this);
        fireAxisChangedEvent(Optional.empty());
    }

    /** @param axis Axis to remove
     *  @throws Error when axis not in model, or axis in use by model item
     */
    public void removeAxis(final AxisConfig axis)
    {
        if (! axes.contains(Objects.requireNonNull(axis)))
            throw new Error("Unknown AxisConfig");
        for (ModelItem item : items)
            if (item.getAxis() == axis)
                throw new Error("Cannot removed AxisConfig while in use");
        axis.setModel(null);
        axes.remove(axis);
        fireAxisChangedEvent(Optional.empty());
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

    /** @return {@link ModelItem}s as thread-safe read-only {@link Iterable} */
    public Iterable<ModelItem> getItems()
    {
        return items;
    }

    /** Locate item by name.
     *
     *  <p>Note that the model may contain multiple items for the same
     *  name. The first occurrence will be returned.
     *  If no item is found with the given
     *  name, <code>null</code> will be returned.
     *  Now that this model may have different items with the same name,
     *  this method is not recommended to locate an item. This method
     *  just returns an item which just happens to have the given name.
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
        boolean already_used;
        RGB color;
        int attempts = 10;
        do
        {
            -- attempts;
            color = default_colors.next();
            already_used = false;
            for (ModelItem item : items)
                if (color.equals(item.getColor()))
                {
                    already_used = true;
                    break;
                }
        }
        while (attempts > 0  &&  already_used);
        return color;
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
     */
    public void addItem(final ModelItem item) throws Exception
    {
        Objects.requireNonNull(item);
        // A new item with the same PV name are allowed to be added in the
        // model. This way Data Browser can show the trend of the same PV
        // in different axes or with different waveform indexes. For example,
        // one may want to show the first element of epics://aaa:bbb in axis 1
        // while showing the third element of the same PV in axis 2 to compare
        // their trends in one chart.
        // But, if exactly the same instance of the given ModelItem already exists in this
        // model, it will not be added.
        if (items.indexOf(item) != -1)
            throw new RuntimeException("Item " + item.getName() + " already in Model");

        // Assign default color
        if (item.getColor() == null)
            item.setColor(getNextItemColor());

        // Force item to be on an axis
        if (item.getAxis() == null)
            item.setAxis(axes.get(0));
        // Check item axis
        if (! axes.contains(item.getAxis()))
            throw new Exception("Item " + item.getName() + " added with invalid axis " + item.getAxis());

        // Add to model
        items.add(item);
        item.setModel(this);
        if (is_running  &&  item instanceof PVItem)
            ((PVItem)item).start();
        // Notify listeners of new item
        for (ModelListener listener : listeners)
            listener.itemAdded(item);
    }

    /** Remove item from the model.
     *  <p>
     *  If the model and thus item are 'running',
     *  the item will be 'stopped'.
     *  @param item
     *  @throws RuntimeException if item not in model
     */
    public void removeItem(final ModelItem item)
    {
        Objects.requireNonNull(item);
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

    /** Move item in model.
     *  <p>
     *  @param item
     *  @param up Up? Otherwise down
     *  @throws RuntimeException if item null or not in model
     */
    public void moveItem(final ModelItem item, final boolean up)
    {
        final int pos = items.indexOf(Objects.requireNonNull(item));
        if (pos < 0)
            throw new RuntimeException("Unknown item " + item.getName());
        if (up)
        {
            if (pos == 0)
                return;
            items.remove(pos);
            items.add(pos-1, item);
        }
        else
        {    // Move down
            if (pos >= items.size() -1)
                return;
            items.remove(pos);
            items.add(pos+1, item);
        }

        // Notify listeners of moved item
        for (ModelListener listener : listeners)
        {
            listener.itemRemoved(item);
            listener.itemAdded(item);
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
            listener.changedTiming();
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
    public boolean isScrollEnabled()
    {
        return scroll_enabled;
    }

    /** @param scroll_enabled Should scrolling be enabled? */
    public void enableScrolling(final boolean scroll_enabled)
    {
        if (this.scroll_enabled == scroll_enabled)
            return;
        this.scroll_enabled = scroll_enabled;
        // Notify listeners
        for (ModelListener listener : listeners)
            listener.scrollEnabled(scroll_enabled);
    }

    /** @return Scroll step size */
    public Duration getScrollStep()
    {
        return scroll_step;
    }

    /** @param step New scroll step
     *  @throws Exception if step size cannot be used
     */
    public void setScrollStep(final Duration step) throws Exception
    {
        if (step.compareTo(Duration.ofSeconds(1)) < 0)
            throw new Exception("Scroll steps are too small: " + step);
        if (step.compareTo(scroll_step) == 0)
            return;
       scroll_step = step;
       for (ModelListener listener : listeners)
           listener.changedTiming();
    }

    /** @return time span of data
     *  @see #isScrollEnabled()
     */
    synchronized public Duration getTimespan()
    {
        return time_span;
    }

    /** Set absolute time range
     *  @param start Start time
     *  @param end End time
     */
    public void setTimerange(final Instant start, final Instant end)
    {
        final Duration new_span = Duration.between(Objects.requireNonNull(start), Objects.requireNonNull(end));
        if (new_span.isZero() || new_span.isNegative())
            return;

        synchronized (this)
        {
            // Format that's understood by StartEndTimeParser
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
            this.start_spec = formatter.format(ZonedDateTime.ofInstant(start, ZoneId.systemDefault()));
            this.end_spec = formatter.format(ZonedDateTime.ofInstant(end, ZoneId.systemDefault()));
            this.end_time = end;
            time_span = new_span;
            scroll_enabled = false;
        }
        // Notify listeners
        for (ModelListener listener : listeners)
            listener.changedTimerange();
    }

    /** Set absolute or relative time range.
     *  <p>In 'scroll' mode, this determines the displayed time range.
     *  Otherwise, it determines the absolute start and end times
     *  @param start_spec Start and ..
     *  @param end_spec   end time specification of the range to display
     *  @throws Exception on error in the time specifications
     */
    public void setTimerange(final String start_spec, final String end_spec) throws Exception
    {
        final StartEndTimeParser times =
                new StartEndTimeParser(Objects.requireNonNull(start_spec), Objects.requireNonNull(end_spec));
        final Instant start_time = times.getStart().toInstant();
        final Instant end_time = times.getEnd().toInstant();
        final Duration new_span = Duration.between(start_time, end_time);
        if (! (new_span.isZero() || new_span.isNegative()))
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
                scroll_enabled = times.isEndNow();
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
    synchronized public Instant getStartTime()
    {
        return getEndTime().minus(time_span);
    }

    /** @return End time of the data range
     *  @see #isScrollEnabled()
     */
    synchronized public Instant getEndTime()
    {
        if (scroll_enabled)
            end_time = Instant.now();
        return end_time;
    }

    /** Returns true if the automatic history refresh is turned on for this model.
     *  The property is read from the preferences at the construction of the model.
     *  After the construction, the property is locked and all items that are added
     *  to this model have the same value as the model (which might be different
     *  to the current preferences).
     *
     *  @return true if automatic history refresh is on or false if it is off
     */
    public boolean isAutomaticHistoryRefresh()
    {
        return automaticHistoryRefresh;
    }

    /** @return String representation of start time. While scrolling, this is
     *          a relative time, otherwise an absolute date/time.
     */
    synchronized public String getStartSpecification()
    {
        if (scroll_enabled)
            return new RelativeTime(-TimeDuration.toSecondsDouble(time_span)).toString();
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
        if (background.equals(Objects.requireNonNull(rgb)))
            return;
        background = rgb;
        for (ModelListener listener : listeners)
            listener.changedColorsOrFonts();
    }

    /** @return <code>true</code> if toolbar is visible*/
    public boolean isToolbarVisible()
    {
        return show_toolbar;
    }

    /** @param visible Should toolbar be visible? */
    public void setToolbarVisible(final boolean toolbar)
    {
        if (show_toolbar == toolbar)
            return;
        show_toolbar = toolbar;
        for (ModelListener listener : listeners)
            listener.changedLayout();
    }

    /** @return <code>true</code> if toolbar is visible*/
    public boolean isLegendVisible()
    {
        return show_legend;
    }

    /** @param visible Should toolbar be visible? */
    public void setLegendVisible(final boolean legend)
    {
        if (show_legend == legend)
            return;
        show_legend = legend;
        for (ModelListener listener : listeners)
            listener.changedLayout();
    }

    /** @return <code>true</code> if grid lines are drawn */
    public boolean isGridVisible()
    {
        return show_grid;
    }

    /** @param visible Should grid be visible? */
    public void setGridVisible(final boolean grid)
    {
        if (show_grid == grid)
            return;
        show_grid = grid;
        for (ModelListener listener : listeners)
            listener.changeTimeAxisConfig();
    }

    /** @return Title font */
    public FontData getTitleFont()
    {
        return title_font;
    }

    /** @param font Title font */
    public void setTitleFont(final FontData font)
    {
        title_font = font;
        for (ModelListener listener : listeners)
            listener.changedColorsOrFonts();
    }

    /** @return Label font */
    public FontData getLabelFont()
    {
        return label_font;
    }

    /** @param font Label font */
    public void setLabelFont(final FontData font)
    {
        label_font = font;
        for (ModelListener listener : listeners)
            listener.changedColorsOrFonts();
    }

    /** @return Scale font */
    public FontData getScaleFont()
    {
        return scale_font;
    }

    /** @param font Scale font */
    public void setScaleFont(final FontData font)
    {
        scale_font = font;
        for (ModelListener listener : listeners)
            listener.changedColorsOrFonts();
    }

    /** @return Legend font */
    public FontData getLegendFont()
    {
        return legend_font;
    }

    /** @param font Scale font */
    public void setLegendFont(final FontData font)
    {
        legend_font = font;
        for (ModelListener listener : listeners)
            listener.changedColorsOrFonts();
    }

    /** @param annotations Annotations to keep in model */
    public void setAnnotations(final List<AnnotationInfo> annotations)
    {
        this.annotations = Objects.requireNonNull(annotations);
        for (ModelListener listener : listeners)
            listener.changedAnnotations();
    }

    /** @return Annotation infos of model */
    public List<AnnotationInfo> getAnnotations()
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
            pv_item.start();
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
     *  @param axis Axis that changed, empty to add/remove
     */
    public void fireAxisChangedEvent(final Optional<AxisConfig> axis)
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

    void fireItemRefreshRequested(final PVItem item)
    {
        for (ModelListener listener : listeners)
            listener.itemRefreshRequested(item);
    }

    public void fireSelectedSamplesChanged()
    {
        for (ModelListener listener : listeners)
            listener.selectedSamplesChanged();
    }

    /** Find a formula that uses a model item as an input.
     *  @param item Item that's potentially used in a formula
     *  @return First Formula found that uses this item, or <code>null</code> if none found
     */
    public Optional<FormulaItem> getFormulaWithInput(final ModelItem item)
    {
        Objects.requireNonNull(item);
        // Update any formulas
        for (ModelItem i : items)
        {
            if (! (i instanceof FormulaItem))
                continue;
            final FormulaItem formula = (FormulaItem) i;
            if (formula.usesInput(item))
                return Optional.of(formula);
        }
        return Optional.empty();
    }
}
