/*******************************************************************************
 * Copyright (c) 2010-2015 Oak Ridge National Laboratory.
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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.apputil.time.AbsoluteTimeParser;
import org.csstudio.apputil.time.PeriodFormat;
import org.csstudio.apputil.time.RelativeTime;
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
import org.csstudio.trends.databrowser2.preferences.Preferences;
import org.csstudio.trends.databrowser2.propsheet.AddArchiveCommand;
import org.csstudio.trends.databrowser2.propsheet.AddAxisCommand;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.diirt.util.time.TimeDuration;
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

    /** Prevent loop between model and plot when changing their annotations */
    private boolean changing_annotations = false;

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

        private void executeOnUIThread(Consumer<Void> consumer)
        {
            if (!display.isDisposed())
                display.asyncExec(() ->
                {
                    if (!display.isDisposed())
                        consumer.accept(null);
                });
        }
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
            if (rescale == ArchiveRescale.STAGGER)
                plot.getPlot().stagger();
            else
                doUpdate();
        }

        private void reportError(final String displayName, final Exception error)
        {
            final String message = NLS.bind(Messages.ArchiveAccessMessageFmt, displayName);
            executeOnUIThread(e -> ExceptionDetailsErrorDialog.openError(shell, Messages.Information, message, error));
        }

        @Override
        public void archiveFetchFailed(final ArchiveFetchJob job,
                final ArchiveDataSource archive, final Exception error)
        {

            if (Preferences.doPromptForErrors())
                reportError(job.getPVItem().getResolvedDisplayName(), error);
            else
                Logger.getLogger(getClass().getName()).log(Level.WARNING,
                        "No archived data for " + job.getPVItem().getDisplayName(), error);
            // always remove the problematic archive data source, but has to happen in UI thread
            executeOnUIThread(e -> job.getPVItem().removeArchiveDataSource(archive));
        }

        @Override
        public void channelNotFound(final ArchiveFetchJob job, final boolean channelFoundAtLeastOnce,
            final ArchiveDataSource[] archivesThatFailed)
        {
            // no need to reuse this source if the channel is not in it, but it has to happen in the UI thread, because
            // of the way the listeners of the pv item are implemented
            executeOnUIThread(e -> job.getPVItem().removeArchiveDataSource(archivesThatFailed));
            // if channel was found at least once, we do not need to report anything
            if (!channelFoundAtLeastOnce)
            {
                if (Preferences.doPromptForErrors())
                    reportError(job.getPVItem().getResolvedDisplayName(), null);
                else
                    Logger.getLogger(getClass().getName()).log(Level.FINE,
                        "Channel " + job.getPVItem().getResolvedDisplayName() + " not found in any of the archived sources.");
            }
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
                @Override
                public void shellIconified(ShellEvent e)
                {
                    window_is_iconized = true;
                }

                //Remove Override annotation, because this method does not exist in RAP
                //@Override
                @Override
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
                    start_spec = "-" + PeriodFormat.formatSeconds(TimeDuration.toSecondsDouble(duration));
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
                if (axis != null) {
                    //only update if the model has that axis. If the trend is empty, the model may not have that axis
                    display.asyncExec(() -> axis.setRange(lower, upper));
                }
            }

            @Override
            public void droppedNames(final String[] names)
            {
                // Offer potential PV name in dialog so user can edit/cancel
                final AddPVAction add = new AddPVAction(plot.getPlot().getUndoableActionManager(), shell, model, false);
                for (String one_name : names)
                    if (! add.runWithSuggestedName(one_name, null))
                        break;
            }

            @Override
            public void droppedPVNames(final ProcessVariable[] names, final ArchiveDataSource[] archives)
            {
                if (names == null)
                {
                    if (archives == null)
                        return;
                    // Received only archives. Add to all PVs
                    for (ArchiveDataSource archive : archives)
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
                {   // Received PV names, maybe with archive
                    final UndoableActionManager operations_manager = plot.getPlot().getUndoableActionManager();

                    // When multiple PVs are dropped, assert that there is at least one axis.
                    // Otherwise dialog cannot offer adding all PVs onto the same axis.
                    if (names.length > 1  &&  model.getAxisCount() <= 0)
                        new AddAxisCommand(operations_manager, model);

                    final AddPVDialog dlg = new AddPVDialog(shell, names.length, model, false);
                    for (int i=0; i<names.length; ++i)
                        dlg.setName(i, names[i].getName());
                    if (dlg.open() != Window.OK)
                        return;

                    for (int i=0; i<names.length; ++i)
                    {
                        final AxisConfig axis;
                        if (dlg.getAxisIndex(i) >= 0)
                            axis = model.getAxis(dlg.getAxisIndex(i));
                        else // Use first empty axis, or create a new one
                            axis = model.getEmptyAxis().orElseGet(() -> new AddAxisCommand(operations_manager, model).getAxis());

                        // Add new PV
                        final ArchiveDataSource archive =
                                (archives == null || i>=archives.length) ? null : archives[i];
                        AddModelItemCommand.forPV(shell, operations_manager,
                                model, dlg.getName(i), dlg.getScanPeriod(i),
                                axis, archive);
                    }
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
                if (changing_annotations)
                    return;
                changing_annotations = true;
                model.setAnnotations(annotations);
                changing_annotations = false;
            }

            @Override
            public void selectedSamplesChanged()
            {
                model.fireSelectedSamplesChanged();
            }

            @Override
            public void changedToolbar(final boolean visible)
            {
                model.setToolbarVisible(visible);
            }

            @Override
            public void changedLegend(final boolean visible)
            {
                model.setLegendVisible(visible);
            }

            @Override
            public void autoScaleChanged(int index, boolean autoScale)
            {
                final AxisConfig axis = model.getAxis(index);
                if (axis != null)
                    display.asyncExec(() -> axis.setAutoScale(autoScale));
            }
        });


        model_listener = new ModelListenerAdapter()
        {
            @Override
            public void changedTitle()
            {
                Optional<String> title = model.getTitle();
                if (title.isPresent())
                    title = Optional.of(model.resolveMacros(title.get()));
                plot.getPlot().setTitle(title);
            }

            @Override
            public void changedLayout()
            {
                plot.getPlot().showToolbar(model.isToolbarVisible());
                plot.getPlot().showLegend(model.isLegendVisible());
            }

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
                plot.getPlot().setTitleFont(model.getTitleFont());
                plot.getPlot().setLabelFont(model.getLabelFont());
                plot.getPlot().setScaleFont(model.getScaleFont());
                plot.getPlot().setLegendFont(model.getLegendFont());
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
                // Item may be added in 'middle' of existing traces
                createPlotTraces();
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
                // When made visible, note that item could be in 'middle'
                // of existing traces, so need to re-create all
                if (item.isVisible())
                    createPlotTraces();
                else // To hide, simply remove
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

            @Override
            public void changedAnnotations()
            {
                if (changing_annotations)
                    return;
                changing_annotations = true;
                plot.setAnnotations(model.getAnnotations());
                changing_annotations = false;
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
        // Cancel pending request, but don't interrupt if already ongoing.
        // A follow-up request will cancel ongoing archive fetch jobs and
        // allow smoother shutdown of active statements.
        if (archive_fetch_delay_task != null)
            archive_fetch_delay_task.cancel(false);
        archive_fetch_delay_task = update_timer.schedule(() -> getArchivedData(), archive_fetch_delay, TimeUnit.MILLISECONDS);
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
        plot.getPlot().showToolbar(model.isToolbarVisible());
        plot.getPlot().showLegend(model.isLegendVisible());
        plot.getPlot().setTitleFont(model.getTitleFont());
        plot.getPlot().setLabelFont(model.getLabelFont());
        plot.getPlot().setScaleFont(model.getScaleFont());
        plot.getPlot().setLegendFont(model.getLegendFont());
        Optional<String> title = model.getTitle();
        if (title.isPresent())
            title = Optional.of(model.resolveMacros(title.get()));
        plot.getPlot().setTitle(title);
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

        // Determine ongoing jobs for this item
        final List<ArchiveFetchJob> ongoing = new ArrayList<>();
        final ArchiveFetchJob new_job = new ArchiveFetchJob(pv_item, start, end, archive_fetch_listener);
        synchronized (archive_fetch_jobs)
        {
            for (Iterator<ArchiveFetchJob> iter = archive_fetch_jobs.iterator();  iter.hasNext();  /**/)
            {
                final ArchiveFetchJob job = iter.next();
                if (job.getPVItem() == pv_item)
                {
                    ongoing.add(job);
                    iter.remove();
                }
            }
            // Track new job
            archive_fetch_jobs.add(new_job);
        }

        Activator.getThreadPool().execute(() ->
        {
            // In background, stop ongoing jobs
            for (ArchiveFetchJob running : ongoing)
            {
                try
                {
                    running.cancel();
                    running.join(10000, null);
                }
                catch (Exception ex)
                {
                    Activator.getLogger().log(Level.WARNING, "Cannot cancel " + running, ex);
                }
            }
            // .. then start new one
            new_job.schedule();
        });
    }
}
