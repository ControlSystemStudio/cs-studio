/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.apputil.time.AbsoluteTimeParser;
import org.csstudio.apputil.time.PeriodFormat;
import org.csstudio.apputil.time.RelativeTime;
import org.csstudio.archive.reader.UnknownChannelException;
import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.csdata.ProcessVariable;
import org.csstudio.swt.xygraph.dataprovider.ISample;
import org.csstudio.swt.xygraph.figures.Annotation;
import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.figures.IAnnotationListener;
import org.csstudio.swt.xygraph.figures.Trace.TraceType;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.swt.xygraph.util.XYGraphMediaFactory;
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
import org.csstudio.trends.databrowser2.model.PVItem;
import org.csstudio.trends.databrowser2.preferences.Preferences;
import org.csstudio.trends.databrowser2.propsheet.AddArchiveCommand;
import org.csstudio.trends.databrowser2.propsheet.AddAxisCommand;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.epics.util.time.Timestamp;

/** Controller that interfaces the {@link Model} with the {@link Plot}:
 *  <ul>
 *  <li>For each item in the Model, create a trace in the plot.
 *  <li>Perform scrolling of the time axis.
 *  <li>When the plot is interactively zoomed, update the Model's time range.
 *  <li>Get archived data whenever the time axis changes.
 *  </ul>
 *  @author Kay Kasemir
 */
public class Controller implements ArchiveFetchJobListener
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
    final private Plot plot;

    /** Timer that triggers scrolling or trace redraws */
    final private Timer update_timer = new Timer("Update Timer", true); //$NON-NLS-1$

    /** Task executed by update_timer */
    private TimerTask update_task = null;

    /** Was scrolling off, i.e. we have not scrolled for a while? */
    private boolean scrolling_was_off = true;

    /** Delay to avoid flurry of archive requests
     *  @see #scheduleArchiveRetrieval(ITimestamp, ITimestamp)
     */
    final private long archive_fetch_delay = Preferences.getArchiveFetchDelay();

    /** Delayed task to avoid flurry of archive requests
     *  @see #scheduleArchiveRetrieval(ITimestamp, ITimestamp)
     */
    private TimerTask archive_fetch_delay_task = null;

    /** Currently active archive jobs, used to prevent multiple requests
     *  for the same model item.
     */
    final private ArrayList<ArchiveFetchJob> archive_fetch_jobs =
        new ArrayList<ArchiveFetchJob>();

    /** Is the window (shell) iconized? */
    private volatile boolean window_is_iconized = false;

    /** Should we perform redraws, or is the window hidden and we should suppress them? */
    private boolean suppress_redraws = false;

    /** Is there any Y axis that's auto-scaled? */
    private volatile boolean have_autoscale_axis = false;


    /** Initialize
     *  @param shell Shell
     *  @param model Model that has the data
     *  @param plot Plot for displaying the Model
     *  @throws Error when called from non-UI thread
     */
    public Controller(final Shell shell, final Model model, final Plot plot)
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
            shell.addShellListener(new ShellListener()
            {
				// Remove Override annotation for RAP
				// @Override
				public void shellIconified(ShellEvent e) {
					window_is_iconized = true;
				}

				// Remove Override annotation for RAP
				// @Override
				public void shellDeiconified(ShellEvent e) {
					window_is_iconized = false;
				}

                @Override
                public void shellDeactivated(ShellEvent e) { /* Ignore */  }
                @Override
                public void shellClosed(ShellEvent e)      { /* Ignore */  }
                @Override
                public void shellActivated(ShellEvent e)   { /* Ignore */  }
            });
            window_is_iconized = shell.getMinimized();
        }
        checkAutoscaleAxes();
        createPlotTraces();
        createAnnotations();
        createXYGraphSettings(); //ADD LAURENT PHILIPPE

        // Listen to user input from Plot UI, update model
        plot.addListener(new PlotListener()
        {
            @Override
            public void scrollRequested(final boolean enable_scrolling)
            {
                model.enableScrolling(enable_scrolling);
            }

            @Override
            public void timeConfigRequested()
            {
                StartEndTimeAction.run(shell, model, plot.getOperationsManager());
            }

            @Override
            public void timeAxisChanged(final long start_ms, final long end_ms)
            {
            	final String start_spec, end_spec;
                if (model.isScrollEnabled())
                {
                    final long dist = Math.abs(end_ms - System.currentTimeMillis());
                    final long range = end_ms - start_ms;
                    // Iffy range?
                    if (range <= 0)
                        return;
                    // In scroll mode, if the end time selected by the user via
                    // the GUI is close enough to 'now', scrolling remains 'on'
                    // and we'll continue to scroll with the new time range.
                    if (dist * 100 / range > 10)
                    {   // Time range 10% away from 'now', disable scrolling
                        model.enableScrolling(false);
                        // Use absolute start/end time
                        final Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(start_ms);
                        start_spec = AbsoluteTimeParser.format(cal);
                        cal.setTimeInMillis(end_ms);
                        end_spec = AbsoluteTimeParser.format(cal);
                    }
                    else if (Math.abs(100*(range - (long)(model.getTimespan()*1000))/range) <= 1)
                    {
                        // We're still scrolling, and the time span didn't really
                        // change, i.e. it's within 1% of the model's span: Ignore.
                        // This happens when scrolling moved the time axis around,
                        // the user zoomed vertically, and the plot now tells
                        // us about a new time range that resulted from scrolling.
                        return;
                    }
                    else
                    {   // Still scrolling, adjust relative time, i.e. width of plot
                        start_spec = "-" + PeriodFormat.formatSeconds(range / 1000.0);
                        end_spec = RelativeTime.NOW;
                    }
                }
                else
                {
                	final Calendar cal = Calendar.getInstance();
                	cal.setTimeInMillis(start_ms);
                	start_spec = AbsoluteTimeParser.format(cal);
                	cal.setTimeInMillis(end_ms);
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
            {   // Update axis range in model
                final AxisConfig axis = model.getAxis(index);
                axis.setRange(lower, upper);
            }

            @Override
            public void droppedName(final String name)
            {
                // Offer potential PV name in dialog so user can edit/cancel
                final AddPVAction add = new AddPVAction(plot.getOperationsManager(), shell, model, false);
                // Allow passing in many names, assuming that white space separates them
                final String[] names = name.split("[\\r\\n\\t ]+"); //$NON-NLS-1$
                for (String n : names)
                    if (! add.runWithSuggestedName(n, null))
                        break;
            }

            @Override
            public void droppedPVName(final ProcessVariable name, final ArchiveDataSource archive)
            {
                if (name == null)
                {
                    if (archive == null)
                        return;
                    // Received only an archive. Add to all PVs
                    for (int i=0; i<model.getItemCount(); ++i)
                    {
                        if (! (model.getItem(i) instanceof PVItem))
                            continue;
                        final PVItem pv = (PVItem) model.getItem(i);
                        if (pv.hasArchiveDataSource(archive))
                            continue;
                        new AddArchiveCommand(plot.getOperationsManager(), pv, archive);
                    }
                }
                else
                {   // Received PV name

                	// Add the given PV to the model anyway even if the same PV
                	// exists in the model.
                    final OperationsManager operations_manager = plot.getOperationsManager();

                    // Add to first empty axis, or create new axis
                    AxisConfig axis = model.getEmptyAxis();
                    if (axis == null)
                        axis = new AddAxisCommand(operations_manager, model).getAxis();

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

                final OperationsManager operations_manager = plot.getOperationsManager();

                // Add to first empty axis, or create new axis
                AxisConfig axis = model.getEmptyAxis();
                if (axis == null)
                    axis = new AddAxisCommand(operations_manager, model).getAxis();

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
			public void xyGraphConfigChanged(XYGraph newValue)
			{
				model.fireGraphConfigChanged();
			}

			@Override
			public void removeAnnotationChanged(Annotation oldValue)
			{
				model.setAnnotations(plot.getAnnotations());
			}

			@Override
			public void addAnnotationChanged(Annotation newValue)
			{
				model.setAnnotations(plot.getAnnotations());
			}

			@Override
			public void backgroundColorChanged(Color newValue)
			{
				model.setPlotBackground(newValue.getRGB());
			}


			@Override
			public void timeAxisForegroundColorChanged(Color oldColor,
					Color newColor)
			{
				// NOP
			}

			@Override
			public void valueAxisForegroundColorChanged(int index,
					Color oldColor, Color newColor)
			{
				final AxisConfig axis = model.getAxis(index);
	            axis.setColor(newColor.getRGB());
			}

			@Override
			public void valueAxisTitleChanged(int index, String oldTitle,
					String newTitle)
			{
				final AxisConfig axis = model.getAxis(index);
	            axis.setName(newTitle);
			}

			@Override
			public void valueAxisAutoScaleChanged(int index,
					boolean oldAutoScale, boolean newAutoScale)
			{
				final AxisConfig axis = model.getAxis(index);
	            axis.setAutoScale(newAutoScale);
			}

			@Override
			public void traceNameChanged(int index, String oldName,
					String newName)
			{
				model.getItem(index).setDisplayName(newName);
			}

			@Override
			public void traceYAxisChanged(int index, AxisConfig oldAxis, AxisConfig newAxis)
			{
				ModelItem item = model.getItem(index);
				AxisConfig c = model.getAxis(newAxis.getName());
				item.setAxis(c);
			}

			@Override
			public void traceTypeChanged(int index, TraceType old,
					TraceType newTraceType)
			{
				//DO NOTHING
				//The model trace type is not the same concept that graph settings traceType
				//The model trace type gather TraceType, PointStyle, ErrorBar graph config settings

				//ModelItem item = model.getItem(index);
				//item.setTraceType(org.csstudio.trends.databrowser2.model.TraceType.newTraceType);
			}

			@Override
			public void traceColorChanged(int index, Color old, Color newColor)
			{
				ModelItem item = model.getItem(index);
				item.setColor(newColor.getRGB());
			}

			@Override
			public void valueAxisLogScaleChanged(int index, boolean old,
					boolean logScale)
			{
				final AxisConfig axis = model.getAxis(index);
				axis.setLogScale(logScale);
			}

        });

        model_listener = new ModelListener()
        {
            @Override
            public void changedUpdatePeriod()
            {
                if (update_task != null)
                    createUpdateTask();
            }

            @Override
            public void changedArchiveRescale()
            {
                // NOP
            }

            @Override
            public void changedColors()
            {
                plot.setBackgroundColor(model.getPlotBackground());
            }

            @Override
            public void changedTimerange()
            {
                // Get matching archived data
                scheduleArchiveRetrieval();
                // Show new time range on plot?
                if (model.isScrollEnabled())
                    return; // no, scrolling will handle that
                // Yes, since the time axis is currently 'fixed'
                final long start_ms = TimestampHelper.toMillisecs(model.getStartTime());
                final long end_ms = TimestampHelper.toMillisecs(model.getEndTime());
                plot.setTimeRange(start_ms, end_ms);
            }

            @Override
            public void changedAxis(final AxisConfig axis)
            {
                checkAutoscaleAxes();
                if (axis == null)
                {
                    // New or removed axis: Recreate the whole plot
                    createPlotTraces();
                    return;
                }
                // Else: Update specific axis
                for (int i=0; i<model.getAxisCount(); ++i)
                {
                    if (model.getAxis(i) == axis)
                    {
                        plot.updateAxis(i, axis);
                        return;
                    }
                }
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
                if (item.isVisible())
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
            public void scrollEnabled(final boolean scroll_enabled)
            {
                plot.updateScrollButton(scroll_enabled);
            }

            /**
             * ADD L.PHILIPPE
             */
			@Override
			public void changedAnnotations()
			{
				// NOP
			}

		    /**
             * ADD L.PHILIPPE
             */
			@Override
			public void changedXYGraphConfig()
			{
				// NOP
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

    /** Check if there's any axis in 'auto scale' mode.
     *  @see #have_autoscale_axis
     */
    private void checkAutoscaleAxes()
    {
        have_autoscale_axis = false;
        for (int i=0;  i<model.getAxisCount(); ++i)
            if (model.getAxis(i).isAutoScale())
            {
                have_autoscale_axis = true;
                break;
            }
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
            archive_fetch_delay_task.cancel();
        archive_fetch_delay_task = new TimerTask()
        {
            @Override
            public void run()
            {
                getArchivedData();
            }
        };
        update_timer.schedule(archive_fetch_delay_task, archive_fetch_delay);
    }

    /** Start model items and initiate scrolling/updates
     *  @throws Exception on error: Already running, problem starting threads, ...
     *  @see #isRunning()
     */
    public void start() throws Exception
    {
        if (isRunning())
            throw new IllegalStateException("Already started"); //$NON-NLS-1$
        createUpdateTask();
        model.start();

        // In scroll mode, the first scroll will update the plot and get data
        if (model.isScrollEnabled())
            return;
        // In non-scroll mode, initialize plot's time range and get data
        plot.setTimeRange(model.getStartTime(), model.getEndTime());
        getArchivedData();
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
            update_task.cancel();
            update_task = null;
        }
        update_task = new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    // Skip updates while nobody is watching
                    if (window_is_iconized || suppress_redraws)
                        return;
                    // Check if anything changed, which also updates formulas
                    final boolean anything_new = model.updateItemsAndCheckForNewSamples();

                    if (anything_new  &&   have_autoscale_axis )
                        plot.updateAutoscale();

                    if (model.isScrollEnabled())
                        performScroll();
                    else
                    {
                        scrolling_was_off = true;
                        // Only redraw when needed
                        if (anything_new)
                            plot.redrawTraces();
                    }
                }
                catch (Throwable ex)
                {
                    Activator.getLogger().log(Level.WARNING, "Error in Plot refresh timer", ex); //$NON-NLS-1$
                }
            }
        };
        final long update_delay = (long) (model.getUpdatePeriod() * 1000);
        update_timer.schedule(update_task, update_delay, update_delay);
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
        update_task.cancel();
        update_task = null;
    }

    /** (Re-) create traces in plot for each item in the model */
    public void createPlotTraces()
    {
        plot.setBackgroundColor(model.getPlotBackground());
        plot.updateScrollButton(model.isScrollEnabled());
        plot.removeAll();


        //Time axe
        if(model.getTimeAxis() != null)
        	plot.updateTimeAxis( model.getTimeAxis());

        for (int i=0; i<model.getAxisCount(); ++i)
            plot.updateAxis(i, model.getAxis(i));
        for (int i=0; i<model.getItemCount(); ++i)
        {
            final ModelItem item = model.getItem(i);

            if (item.isVisible()){
            	//System.out.println("Controller.createPlotTraces() INDEX " + i + " => " + model.getItem(i).getDisplayName());
                plot.addTrace(item, i);
            }
        }
    }

    /** Add annotations from model to plot */
    private void createAnnotations()
    {
		final XYGraph graph = plot.getXYGraph();
    	final List<Axis> yaxes = graph.getYAxisList();
    	final AnnotationInfo[] annotations = model.getAnnotations();
        for (final AnnotationInfo info : annotations)
        {
			final int axis_index = info.getAxis();
			if (axis_index < 0  ||  axis_index >= yaxes.size())
				continue;
			final Axis axis = yaxes.get(axis_index);
        	final Annotation annotation = new Annotation(info.getTitle(), graph.primaryXAxis, axis);
        	//ADD Laurent PHILIPPE
			annotation.setCursorLineStyle(info.getCursorLineStyle());
        	annotation.setShowName(info.isShowName());
        	annotation.setShowPosition(info.isShowPosition());
        	annotation.setShowSampleInfo(info.isShowSampleInfo());        	
        	annotation.setValues(TimestampHelper.toMillisecs(info.getTimestamp()),
        			info.getValue());
        	
        	snapAnnotation(annotation, info);

        	if(info.getColor() != null)
        		annotation.setAnnotationColor(XYGraphMediaFactory.getInstance().getColor(info.getColor()));

        	if(info.getFontData() != null)
       			annotation.setAnnotationFont(XYGraphMediaFactory.getInstance().getFont(info.getFontData()));

        	graph.addAnnotation(annotation);
        }
    }
    
	//a workaround to snap the annotation in place
    private void snapAnnotation(final Annotation annotation, final AnnotationInfo info) {
    	annotation.addAnnotationListener(new IAnnotationListener() {
			@Override
			public void annotationMoved(double oldX, double oldY, double newX, double newY) {
				//wait for the first annotation update after the trace is plotted and resnap
				//the annotation to the correct position
				if (annotation.getTrace() != null) {
					if (annotation.getTrace().getHotSampleList().size() > 0) {
						annotation.removeAnnotationListener(this);
						double xValue = TimestampHelper.toMillisecs(info.getTimestamp());
						List<ISample> samples = annotation.getTrace().getHotSampleList();
						ISample sample = null;
						if (samples.size() > 0) {
							if (samples.get(0).getXValue() > xValue) {
								sample = samples.get(0);
							}
						}
						if (sample == null) {
							for (int i = 1; i < samples.size(); i++) {
								ISample first = samples.get(i-1);
								ISample second = samples.get(i);
								if (second.getXValue() > xValue && first.getXValue() <= xValue) {
									sample = first;
									break;
								}
							}
						}
						if (sample == null && samples.size() > 0) {
							sample = samples.get(samples.size()-1);
						}
						if (sample != null) {
							annotation.setCurrentSnappedSample(sample,false);
						}
						annotation.setValues(xValue,info.getValue());						
					}
				} else {
					annotation.removeAnnotationListener(this);
				}
				
			}
		});
    }


    /**
     * Add XYGraphMemento (Graph config settings from model to plot)
     */
    private void createXYGraphSettings() {
     	plot.setGraphSettings(model.getGraphSettings());
 	}

	/** Scroll the plot to 'now' */
    protected void performScroll()
    {
        if (! model.isScrollEnabled())
            return;
        final long end_ms = System.currentTimeMillis();
        final long start_ms = end_ms - (long) (model.getTimespan()*1000);
        plot.setTimeRange(start_ms, end_ms);
        if (scrolling_was_off)
        {   // Scrolling was just turned on.
            // Get new archived data since the new time scale
            // could be way off what's in the previous time range.
            scrolling_was_off = false;
            getArchivedData();
        }
    }

    /** Initiate archive data retrieval for all model items
     *  @param start Start time
     *  @param end End time
     */
    private void getArchivedData()
    {
        final Timestamp start = model.getStartTime();
        final Timestamp end = model.getEndTime();
        for (int i=0; i<model.getItemCount(); ++i)
            getArchivedData(model.getItem(i), start, end);
    }

    /** Initiate archive data retrieval for a specific model item
     *  @param item Model item. NOP for non-PVItem
     *  @param start Start time
     *  @param end End time
     */
    private void getArchivedData(final ModelItem item,
            final Timestamp start, final Timestamp end)
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
            job = new ArchiveFetchJob(pv_item, start, end, this);
            archive_fetch_jobs.add(job);
        }
        job.schedule();
    }

    /** @see ArchiveFetchJobListener */
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
        display.asyncExec(new Runnable() {
			
			@Override
			public void run() {
								
			}
		});
        // All completed. Do something to the plot?
        final ArchiveRescale rescale = model.getArchiveRescale();
        if (rescale == ArchiveRescale.NONE)
            return;
        if (display == null  ||  display.isDisposed())
            return;
        display.asyncExec(new Runnable()
        {
            @Override
            public void run()
            {
                if (display.isDisposed())
                    return;
                switch (rescale)
                {
                case AUTOZOOM:
                    plot.getXYGraph().performAutoScale();
                    break;
                case STAGGER:
                    plot.getXYGraph().performStagger();
                    break;
                default:
                    break;
                }
            }
        });
    }

    /** @see ArchiveFetchJobListener */
    @SuppressWarnings("nls")
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
}
