/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.ui;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.apputil.time.AbsoluteTimeParser;
import org.csstudio.apputil.time.PeriodFormat;
import org.csstudio.apputil.time.RelativeTime;
import org.csstudio.archive.reader.UnknownChannelException;
import org.csstudio.csdata.ProcessVariable;
import org.csstudio.swt.rtplot.Annotation;
import org.csstudio.swt.rtplot.Trace;
import org.csstudio.swt.rtplot.undo.UndoableActionManager;
import org.csstudio.swt.rtplot.util.NamedThreadFactory;
import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.archive.ArchiveFetchJob;
import org.csstudio.trends.databrowser2.archive.ArchiveFetchJobListener;
import org.csstudio.trends.databrowser2.imports.FileImportDialog;
import org.csstudio.trends.databrowser2.imports.ImportArchiveReaderFactory;
import org.csstudio.trends.databrowser2.model.AnnotationInfo;
import org.csstudio.trends.databrowser2.model.ArchiveDataSource;
import org.csstudio.trends.databrowser2.model.ArchiveRescale;
import org.csstudio.trends.databrowser2.model.AxisConfig;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.csstudio.trends.databrowser2.model.ModelListener;
import org.csstudio.trends.databrowser2.model.ModelListenerAdapter;
import org.csstudio.trends.databrowser2.model.PVItem;
import org.csstudio.trends.databrowser2.model.TimeHelper;
import org.csstudio.trends.databrowser2.preferences.Preferences;
import org.csstudio.trends.databrowser2.propsheet.AddArchiveCommand;
import org.csstudio.trends.databrowser2.propsheet.AddAxisCommand;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** Controller that interfaces the {@link Model} with the {@link ModelBasedPlot}:
 *  <ul>
 *  <li>For each item in the Model, create a trace in the plot.
 *  <li>Perform scrolling of the time axis.
 *  <li>When the plot is interactively zoomed, update the Model's time range.
 *  <li>Get archived data whenever the time axis changes.
 *  </ul>
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Controller
{
    /** Optional shell used to track shell state */
    final private Shell shell;

    /** Display used for dialog boxes etc. */
    final private Display display;

    /** Model with data to display */
    final private Model model;

    /** Listener to model that informs this controller */
    private ModelListener model_listener;

    /** GUI for displaying the data */
    final private ModelBasedPlot plot;

    /** Timer that triggers scrolling or trace redraws */
    final private static ScheduledExecutorService update_timer =
            Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("DataBrowserUpdates"));

    /** Task executed by update_timer.
     *  Only changed on UI thread
     */
    private ScheduledFuture<?> update_task = null;

    /** Delay to avoid flurry of archive requests */
    final private long archive_fetch_delay = Preferences.getArchiveFetchDelay();

    /** Delayed task to avoid flurry of archive requests
     *  @see #scheduleArchiveRetrieval(ITimestamp, ITimestamp)
     */
    private ScheduledFuture<?> archive_fetch_delay_task = null;

    /** Currently active archive jobs, used to prevent multiple requests
     *  for the same model item.
     */
    final private ArrayList<ArchiveFetchJob> archive_fetch_jobs =
        new ArrayList<ArchiveFetchJob>();

    /** Is the window (shell) iconized? */
    private volatile boolean window_is_iconized = false;

    /** Should we perform redraws, or is the window hidden and we should suppress them? */
    private boolean suppress_redraws = false;

    final private ArchiveFetchJobListener archive_fetch_listener = new ArchiveFetchJobListener()
    {
        @Override
        public void fetchCompleted(final ArchiveFetchJob job)
        {
            synchronized (archive_fetch_jobs)
            {
                archive_fetch_jobs.remove(job);
                // System.out.println("Completed " + job + ", " + archive_fetch_jobs.size() + " left");
                if (!archive_fetch_jobs.isEmpty())
                    return;
            }
            // All completed. Do something to the plot?
            final ArchiveRescale rescale = model.getArchiveRescale();
            if (rescale == ArchiveRescale.NONE)
                return;
            if (display == null  ||  display.isDisposed())
                return;
            display.asyncExec(() ->
            {
                if (display.isDisposed())
                    return;
                if (rescale == ArchiveRescale.STAGGER)
                    plot.getPlot().stagger();
            });
        }

        @Override
        public void archiveFetchFailed(final ArchiveFetchJob job,
                final ArchiveDataSource archive, final Exception error)
        {
            final String message = NLS.bind(Messages.ArchiveAccessMessageFmt,
                    job.getPVItem().getDisplayName());

            if (Preferences.doPromptForErrors())
            {
                if (display == null  ||  display.isDisposed())
                    return;
                display.asyncExec(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (display.isDisposed())
                            return;
                        ExceptionDetailsErrorDialog.openError(shell, Messages.Information, message, error);
                        job.getPVItem().removeArchiveDataSource(archive);
                    }
                });
            }
            else if (error instanceof UnknownChannelException)
                Logger.getLogger(getClass().getName()).log(Level.FINE,
                            "No archived data for " + job.getPVItem().getDisplayName(), error);
            else
                Logger.getLogger(getClass().getName()).log(Level.WARNING,
                        "No archived data for " + job.getPVItem().getDisplayName(), error);
        }
    };

    /** Initialize
     *  @param shell Shell
     *  @param model Model that has the data
     *  @param plot Plot for displaying the Model
     *  @throws Error when called from non-UI thread
     */
    public Controller(final Shell shell, final Model model, final ModelBasedPlot plot)
    {
        this.shell = shell;
        this.model = model;
        this.plot = plot;

        if (shell == null)
        {
            display = Display.getCurrent();
            if (display == null)
                throw new Error("Must be called from UI thread"); //$NON-NLS-1$
        }
        else
        {
            display = shell.getDisplay();
            // Update 'iconized' state from shell
            shell.addShellListener(new ShellAdapter()
            {
            	//Remove Override annotation, because this method does not exist in RAP
                //@Override
                public void shellIconified(ShellEvent e)
                {
                    window_is_iconized = true;
                }

                //Remove Override annotation, because this method does not exist in RAP
                //@Override
                public void shellDeiconified(ShellEvent e)
                {
                    window_is_iconized = false;
                }
            });
            window_is_iconized = shell.getMinimized();
        }
        createPlotTraces();

        // Listen to user input from Plot UI, update model
        plot.addListener(new PlotListener()
        {
            @Override
            public void timeConfigRequested()
            {
                StartEndTimeAction.run(shell, model, plot.getPlot().getUndoableActionManager());
            }

            @Override
            public void timeAxisChanged(final boolean scrolling, final Instant start, final Instant end)
            {
                model.enableScrolling(scrolling);
                final String start_spec, end_spec;
                if (scrolling)
                {   // Scrolling, adjust relative time, i.e. width of plot
                    final Duration duration = Duration.between(start, end);
                    start_spec = "-" + PeriodFormat.formatSeconds(TimeHelper.toSeconds(duration));
                    end_spec = RelativeTime.NOW;
                }
                else
                {
                    final ZoneId zone = ZoneId.systemDefault();
                    Calendar cal = GregorianCalendar.from(ZonedDateTime.ofInstant(start, zone));
                    start_spec = AbsoluteTimeParser.format(cal);
                    cal = GregorianCalendar.from(ZonedDateTime.ofInstant(end, zone));
                    end_spec = AbsoluteTimeParser.format(cal);
                }
                // Update model's time range
                try
                {
                    model.setTimerange(start_spec, end_spec);
                }
                catch (Exception ex)
                {
                    Logger.getLogger(Controller.class.getName()).log(Level.WARNING,
                        "Cannot adjust time range to " + start_spec + " .. " + end_spec, ex);
                }
                // Controller's ModelListener will fetch new archived data
            }

            @Override
            public void valueAxisChanged(final int index, final double lower, final double upper)
            {   // Update axis range in model, using UI thread because event may come from 'stagger' background thread
                final AxisConfig axis = model.getAxis(index);
                display.asyncExec(() -> axis.setRange(lower, upper));
            }

            @Override
            public void droppedName(final String name)
            {
                // Offer potential PV name in dialog so user can edit/cancel
                final AddPVAction add = new AddPVAction(plot.getPlot().getUndoableActionManager(), shell, model, false);
                // Allow passing in many names, assuming that white space separates them
                final String[] names = name.split("[\\r\\n\\t ]+"); //$NON-NLS-1$
                for (String one_name : names)
                {   // Might also have received "[pv1, pv2, pv2]", turn that into "pv1", "pv2", "pv3"
                    String suggestion = one_name;
                    if (suggestion.startsWith("["))
                        suggestion = suggestion.substring(1);
                    if (suggestion.endsWith("]")  &&  !suggestion.contains("["))
                        suggestion = suggestion.substring(0, suggestion.length()-1);
                    if (suggestion.endsWith(","))
                        suggestion = suggestion.substring(0, suggestion.length()-1);
                    if (! add.runWithSuggestedName(suggestion, null))
                        break;
                }
            }

            @Override
            public void droppedPVName(final ProcessVariable name, final ArchiveDataSource archive)
            {
                if (name == null)
                {
                    if (archive == null)
                        return;
                    // Received only an archive. Add to all PVs
                    for (ModelItem item : model.getItems())
                    {
                        if (! (item instanceof PVItem))
                            continue;
                        final PVItem pv = (PVItem) item;
                        if (pv.hasArchiveDataSource(archive))
                            continue;
                        new AddArchiveCommand(plot.getPlot().getUndoableActionManager(), pv, archive);
                    }
                }
                else
                {   // Received PV name

                    // Add the given PV to the model anyway even if the same PV
                    // exists in the model.
                    final UndoableActionManager operations_manager = plot.getPlot().getUndoableActionManager();

                    // Add to first empty axis, or create new axis
                    final AxisConfig axis = model.getEmptyAxis().orElseGet(
                            () -> new AddAxisCommand(operations_manager, model).getAxis() );

                    // Add new PV
                    AddModelItemCommand.forPV(shell, operations_manager,
                            model, name.getName(), Preferences.getScanPeriod(),
                            axis, archive);
                    return;
                }
            }

            @Override
            public void droppedFilename(String file_name)
            {
                final FileImportDialog dlg = new FileImportDialog(shell, file_name);
                if (dlg.open() != Window.OK)
                    return;

                final UndoableActionManager operations_manager = plot.getPlot().getUndoableActionManager();

                // Add to first empty axis, or create new axis
                final AxisConfig axis = model.getEmptyAxis().orElseGet(
                        () -> new AddAxisCommand(operations_manager, model).getAxis() );

                // Add archivedatasource for "import:..." and let that load the file
                final String type = dlg.getType();
                file_name = dlg.getFileName();
                final String url = ImportArchiveReaderFactory.createURL(type, file_name);
                final ArchiveDataSource imported = new ArchiveDataSource(url, 1, type);
                // Add PV Item with data to model
                AddModelItemCommand.forPV(shell, operations_manager,
                        model, dlg.getItemName(), Preferences.getScanPeriod(),
                        axis, imported);
            }

            @Override
            public void changedAnnotations(final List<AnnotationInfo> annotations)
            {
                model.setAnnotations(annotations);
            }

            @Override
            public void selectedSamplesChanged()
            {
                model.fireSelectedSamplesChanged();
            }
        });

        model_listener = new ModelListenerAdapter()
        {
            @Override
            public void changedTiming()
            {
                plot.getPlot().setScrollStep(model.getScrollStep());
                if (update_task != null)
                    createUpdateTask();
            }

            @Override
            public void changedColorsOrFonts()
            {
                plot.getPlot().setBackground(model.getPlotBackground());
                plot.getPlot().setLabelFont(model.getLabelFont());
                plot.getPlot().setScaleFont(model.getScaleFont());
            }

            @Override
            public void scrollEnabled(boolean scroll_enabled)
            {
                plot.getPlot().setScrolling(scroll_enabled);
            }

            @Override
            public void changedTimerange()
            {
                // Update plot's time range
                if (model.isScrollEnabled())
                    plot.setTimeRange(model.getStartTime(), model.getEndTime().plus(model.getScrollStep()));
                else
                    plot.setTimeRange(model.getStartTime(), model.getEndTime());

                // Get matching archived data
                scheduleArchiveRetrieval();
            }

            @Override
            public void changeTimeAxisConfig()
            {
                plot.getPlot().getXAxis().setGridVisible(model.isGridVisible());
            }

            @Override
            public void changedAxis(final Optional<AxisConfig> axis)
            {
                if (axis.isPresent())
                {   // Update specific axis
                    final AxisConfig the_axis = axis.get();
                    int i = 0;
                    for (AxisConfig axis_config : model.getAxes())
                    {
                        if (axis_config == the_axis)
                        {
                            plot.updateAxis(i, the_axis);
                            return;
                        }
                        ++i;
                    }
                }
                else  // New or removed axis: Recreate the whole plot
                    createPlotTraces();
            }

            @Override
            public void itemAdded(final ModelItem item)
            {
                if (item.isVisible())
                    plot.addTrace(item);
                // Get archived data for new item (NOP for non-PVs)
                getArchivedData(item, model.getStartTime(), model.getEndTime());
            }

            @Override
            public void itemRemoved(final ModelItem item)
            {
                plot.removeTrace(item);
            }

            @Override
            public void changedItemVisibility(final ModelItem item)
            {   // Add/remove from plot, but don't need to get archived data
                if (item.isVisible())
                    // itemAdded(item) would also get archived data
                    plot.addTrace(item);
                else
                    plot.removeTrace(item);
            }

            @Override
            public void changedItemLook(final ModelItem item)
            {
                plot.updateTrace(item);
            }

            @Override
            public void changedItemDataConfig(final PVItem item)
            {
                getArchivedData(item, model.getStartTime(), model.getEndTime());
            }

            @Override
            public void itemRefreshRequested(final PVItem item)
            {
                getArchivedData(item, model.getStartTime(), model.getEndTime());
            }
        };
        model.addListener(model_listener);
    }

    /** @param suppress_redraws <code>true</code> if controller should suppress
     *        redraws because window is hidden
     */
    public void suppressRedraws(final boolean suppress_redraws)
    {
        if (this.suppress_redraws == suppress_redraws)
            return;
        this.suppress_redraws = suppress_redraws;
        if (!suppress_redraws)
            plot.redrawTraces();
    }

    /** Schedule fetching archived data.
     *
     *  <p>When the user moves the time axis around, archive requests for the
     *  new time range are delayed to avoid a flurry of archive
     *  requests while the user is still moving around.
     *  This request is therefore a little delayed, and a follow-up
     *  request will cancel an ongoing, scheduled, request.
     */
    public void scheduleArchiveRetrieval()
    {
        if (archive_fetch_delay_task != null)
            archive_fetch_delay_task.cancel(true);
        archive_fetch_delay_task = update_timer.schedule(this::getArchivedData, archive_fetch_delay, TimeUnit.MILLISECONDS);
    }

    /** Start model items and initiate scrolling/updates
     *  @throws Exception on error: Already running, problem starting threads, ...
     *  @see #isRunning()
     */
    public void start() throws Exception
    {
        if (isRunning())
            throw new IllegalStateException("Already started");

        plot.getPlot().setBackground(model.getPlotBackground());
        plot.getPlot().getXAxis().setGridVisible(model.isGridVisible());
        plot.getPlot().setLabelFont(model.getLabelFont());
        plot.getPlot().setScaleFont(model.getScaleFont());
        plot.getPlot().setScrollStep(model.getScrollStep());

        final List<Trace<Instant>> traces = new ArrayList<>();
        for (Trace<Instant> trace : plot.getPlot().getTraces())
            traces.add(trace);
        for (AnnotationInfo info : model.getAnnotations())
        {
            final Trace<Instant> trace = traces.get(info.getItemIndex());
            final Annotation<Instant> annotation =
                new Annotation<Instant>(trace , info.getTime(), info.getValue(), info.getOffset(), info.getText());
            plot.getPlot().addAnnotation(annotation);
        }
        createUpdateTask();

        model.start();

        // Initial time range setup, schedule archive fetch
        if (!model.isScrollEnabled())
            plot.getPlot().setScrolling(false);
        model_listener.changedTimerange();
    }

    /** @return <code>true</code> while running
     *  @see #stop()
     */
    public boolean isRunning()
    {
        return update_task != null;
    }

    /** Create or re-schedule update task
     *  @see #start()
     */
    private void createUpdateTask()
    {
        // Can't actually re-schedule, so stop one that might already be running
        if (update_task != null)
        {
            update_task.cancel(true);
            update_task = null;
        }

        final long update_delay = (long) (model.getUpdatePeriod() * 1000);
        update_task = update_timer.scheduleAtFixedRate(this::doUpdate, update_delay, update_delay, TimeUnit.MILLISECONDS);
    }

    private void doUpdate()
    {
        try
        {
            // Skip updates while nobody is watching
            if (window_is_iconized || suppress_redraws)
                return;
            // Check if anything changed, which also updates formulas
            if (model.updateItemsAndCheckForNewSamples())
                plot.redrawTraces();
        }
        catch (Throwable ex)
        {
            Activator.getLogger().log(Level.WARNING, "Error in Plot refresh timer", ex); //$NON-NLS-1$
        }
    }

    /** Stop scrolling and model items
     *  @throws IllegalStateException when not running
     */
    public void stop()
    {
        if (! isRunning())
            throw new IllegalStateException("Not started"); //$NON-NLS-1$
        // Stop ongoing archive access
        synchronized (archive_fetch_jobs)
        {
            for (ArchiveFetchJob job : archive_fetch_jobs)
                job.cancel();
            archive_fetch_jobs.clear();
        }
        // Stop update task
        model.stop();
        model.removeListener(model_listener);
        update_task.cancel(true);
        update_task = null;
    }

    /** (Re-) create traces in plot for each item in the model */
    public void createPlotTraces()
    {
        plot.removeAll();
        int i = 0;
        for (AxisConfig axis : model.getAxes())
            plot.updateAxis(i++, axis);
        for (ModelItem item : model.getItems())
            if (item.isVisible())
                plot.addTrace(item);
    }

    /** Initiate archive data retrieval for all model items
     *  @param start Start time
     *  @param end End time
     */
    private void getArchivedData()
    {
        final Instant start = model.getStartTime();
        final Instant end = model.getEndTime();
        for (ModelItem item : model.getItems())
            getArchivedData(item, start, end);
    }

    /** Initiate archive data retrieval for a specific model item
     *  @param item Model item. NOP for non-PVItem
     *  @param start Start time
     *  @param end End time
     */
    private void getArchivedData(final ModelItem item,
            final Instant start, final Instant end)
    {
        // Only useful for PVItems with archive data source
        if (!(item instanceof PVItem))
            return;
        final PVItem pv_item = (PVItem) item;
        if (pv_item.getArchiveDataSources().length <= 0)
            return;

        ArchiveFetchJob job;

        // Stop ongoing jobs for this item
        synchronized (archive_fetch_jobs)
        {
            for (int i=0; i<archive_fetch_jobs.size(); ++i)
            {
                job = archive_fetch_jobs.get(i);
                if (job.getPVItem() != pv_item)
                    continue;
                // System.out.println("Request for " + item.getName() + " cancels " + job);
                job.cancel();
                archive_fetch_jobs.remove(job);
            }
            // Start new job
            job = new ArchiveFetchJob(pv_item, start, end, archive_fetch_listener);
            archive_fetch_jobs.add(job);
        }
        job.schedule();
    }
}
