package org.csstudio.trends.databrowser;

import org.csstudio.archive.util.TimestampUtil;
import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableOrArchiveDataSourceDropTarget;
import org.csstudio.platform.util.ITimestamp;
import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.ChartListener;
import org.csstudio.swt.chart.Trace;
import org.csstudio.swt.chart.axes.XAxis;
import org.csstudio.swt.chart.axes.YAxis;
import org.csstudio.swt.chart.axes.YAxisListener;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.ModelListener;
import org.csstudio.trends.databrowser.ploteditor.BrowserUI;
import org.csstudio.trends.databrowser.preferences.Preferences;
import org.eclipse.swt.dnd.DropTargetEvent;

/** Data Browser Controller: Creates model, UI and handles everything between them.
 *  @author Kay Kasemir
 */ 
public class Controller implements ScannerAndScrollerListener
{
    private final Model model;
    private final ModelListener model_listener;
    private final BrowserUI gui;
    private final Chart chart;
    private final ChartListener chart_listener;
    private ScannerAndScroller scanner_scroller;
    private boolean controller_changes_xaxis = false;
    private boolean controller_changes_yaxes = false;
    private boolean controller_changes_model = false;
    
    /** Construct controller.
     *  @param parent Parent widget (shell) under which the UI is created.
     */
    public Controller(Model model, BrowserUI gui)
    {
        this.model = model;
        this.gui = gui;
        chart = gui.getChart();

        // React to model changes
        model_listener = new ModelListener()
        {
            public void timeSpecificationsChanged()
            {
                // TODO use this instead of ChartListener?
            }
            
            public void timeRangeChanged()
            {
                // TODO use this instead of ChartListener?
            }

            public void periodsChanged()
            {
                if (scanner_scroller != null)
                    scanner_scroller.periodsChanged();
            }
    
            public void entriesChanged()
            {   handleChangedModelEntries(); }

            public void entryAdded(IModelItem new_item)
            { 
                addToDisplay(new_item);
                getArchivedData(new_item);
            }

            public void entryConfigChanged(IModelItem item)
            {   // Avoid infinite loops if we are changing the model ourselves
                if (controller_changes_model)
                    return;
                removeFromDisplay(item);
                addToDisplay(item);
                removeUnusedAxes();
                getArchivedData(item);
            }

            public void entryLookChanged(IModelItem item)
            {   entryConfigChanged(item);  }
            
            public void entryArchivesChanged(IModelItem item)
            {   getArchivedData(item); }
            
            public void entryRemoved(IModelItem removed_item)
            {   
                removeFromDisplay(removed_item);
                removeUnusedAxes();
            }
        };
        
        chart_listener = new ChartListener()
        {
            public void changedXAxis(XAxis xaxis)
            {
                if (controller_changes_xaxis)
                    return;
                final BrowserUI gui = Controller.this.gui;
                // This is a user-driven pan or zoom.
                // If we scroll, that would change what the user just did,
                // so don't
                if (gui.isScrollEnabled())
                    gui.enableScrolling(false);
                getArchivedData(null);
            }

            public void changedYAxis(YAxisListener.Aspect what, YAxis yaxis)
            {
                // Avoid infinite loop: We are changing the axes, so ignore.
                if (controller_changes_yaxes)
                    return;
                if (what == YAxisListener.Aspect.RANGE)
                {   // Range was changed interactively, update the model
                    controller_changes_model = true;
                    int axis_index = chart.getYAxisIndex(yaxis);
                    Controller.this.model.setAxisLimits(axis_index,
                                    yaxis.getLowValue(),
                                    yaxis.getHighValue());
                    controller_changes_model = false;
                }
            }

            public void pointSelected(XAxis xaxis, YAxis yaxis, double x, double y)
            {}
        };

        // Allow PV drops into the chart
        new ProcessVariableOrArchiveDataSourceDropTarget(chart)
        {
            @Override
            public void handleDrop(IProcessVariable name, DropTargetEvent event)
            {
                YAxis yaxis = chart.getYAxisAtScreenPoint(event.x, event.y);
                IModelItem item = nameDropped(name.getName(), yaxis);
                IArchiveDataSource archives[] = Preferences.getArchiveDataSources();
                for (int i = 0; i < archives.length; i++)
                    item.addArchiveDataSource(archives[i]);
            }

            @Override
            public void handleDrop(IArchiveDataSource archive, DropTargetEvent event)
            {
                Controller.this.model.addArchiveDataSource(archive);
            }

            @Override
            public void handleDrop(IProcessVariable name, IArchiveDataSource archive, DropTargetEvent event)
            {
                YAxis yaxis = chart.getYAxisAtScreenPoint(event.x, event.y);
                IModelItem item = nameDropped(name.getName(), yaxis);
                item.addArchiveDataSource(archive);
            }
        };
        
        chart.addListener(chart_listener);
        model.addListener(model_listener);
        // Initialize GUI with the current model content.
        model_listener.entriesChanged();
    }
    
    /** Must be invoked for cleanup. */
    public void dispose()
    {
        model.removeListener(model_listener);
        model.stop();
     }

    /** @return Returns the GUI. */
    public BrowserUI getBrowserUI()
    {
        return gui;
    }

    /** Add a PV. */
    public IModelItem add(String pv_name)
    {
        return nameDropped(pv_name, null);
    }

    /** Private handler for ..DropTarget interface */
    private IModelItem nameDropped(String name, YAxis yaxis)
    {
        if (yaxis == null)
        {   // If there is only one axis, and it's empty, use it:
            if (chart.getNumYAxes() == 1  &&
                chart.getYAxis(0).getNumTraces() < 1)
                yaxis = chart.getYAxis(0);
            else
                // Create new axis
                yaxis = chart.addYAxis(name);
        }
        return model.add(name, chart.getYAxisIndex(yaxis));
        // Model should now invoke entryAdded(), where we add it to the chart..
    }

    private void handleChangedModelEntries()
    {
        // Avoid infinite loops if we are changing the model ourselves
        if (controller_changes_model)
            return;
        int i;
        // Clear chart by removing all the traces
        while (chart.getNumTraces() > 0)
            chart.removeTrace(0);
        // .. and all but the first Y Axis.
        while (chart.getNumYAxes() > 1)
            chart.removeYAxis(chart.getNumYAxes()-1); // del. last axis
        // Add model data.
        for (i=0; i<model.getNumItems(); ++i)
            addToDisplay(model.getItem(i));
        getArchivedData(null);
    }
    
    /** Connect a model item to the display by adding it to the chart. */
    private void addToDisplay(IModelItem new_item)
    {
        // Avoid infinite loops if we are changing the model ourselves
        if (controller_changes_model)
            return;
        int yaxis_index = new_item.getAxisIndex();
        // Assert that the chart has the requested Y Axis.
        while (yaxis_index >= chart.getNumYAxes())
            chart.addYAxis();
        // Add new model entry to the chart.
        // For the trace name, we use the item name plus its units.
        // Remember this when later trying to locate an item
        // by its trace name!
        Trace trace = chart.addTrace(getTraceName(new_item),
                                     new_item.getSamples(),
                                     new_item.getColor(),
                                     new_item.getLineWidth(),
                                     yaxis_index,
                                     new_item.getTraceType());
        // Set initial axis range from model
        controller_changes_yaxes = true;
        YAxis yaxis = trace.getYAxis();
        yaxis.setValueRange(new_item.getAxisLow(), new_item.getAxisHigh());
        // Do we need to change the axis type?
        if (new_item.getLogScale() != yaxis.isLogarithmic())
            yaxis.setLogarithmic(new_item.getLogScale());
        if (new_item.getAutoScale() != yaxis.getAutoScale())
            yaxis.setAutoScale(new_item.getAutoScale());
        controller_changes_yaxes = false;
        
        // Model already running?
        if (model.isRunning())
            return;
        // else: Start the model
        model.start();
        scanner_scroller = new ScannerAndScroller(gui, model, this);
    }

    /** @return a trace name (item name plus units) for an item. */
    private String getTraceName(IModelItem item)
    {
        String units = item.getUnits();
        if (units.length() > 0)
            return item.getName() + Messages.UnitMarkerStart
                                    + units + Messages.UnitMarkerEnd;
        // else
        return item.getName();
    }
    
    /** @return the item name for a trace. */
    private String getModelItemName(Trace trace)
    {
        // If there are units, chop them back off
        String trace_name = trace.getName();
        int i = trace_name.indexOf(Messages.UnitMarkerStart);
        if (i < 0)
            return trace_name;
        return trace_name.substring(0, i);
    }
    
    private void removeFromDisplay(IModelItem removed_item)
    {
        for (int i=0; i<chart.getNumTraces(); ++i)
            if (getModelItemName(chart.getTrace(i))
                    .equals(removed_item.getName()))
            {
                chart.removeTrace(i);
                return;
            }
    }
    
    /** Remove axes with indices beyond the highest uses axis.
     *  <p>
     *  After changing the axis assignments,
     *  some axes might end up with no traces.
     *  When e.g. axis 2 has no traces, but axis 3 does,
     *  we can't remove #2, because that messes everything else up.
     *  But we can remove all axes beyond the last one that's used.
     */
    private void removeUnusedAxes()
    {
        for (int y = chart.getNumYAxes()-1; y > 0; --y)
            if (chart.getYAxis(y).getNumTraces() < 1)
                chart.removeYAxis(y); // Drop empty axis
            else
                return; // Done, found used axis
    }
    
    /** Scan the PVs.
     *  @see ScannerAndScrollerListener
     */
    @SuppressWarnings("nls")
    public void scan(boolean with_redraw)
    {
        // 'Scan' the PVs
        ITimestamp now = model.addCurrentValuesToChartItems();
        // Scroll or simply redraw w/o scroll.
        if (with_redraw  &&  chart.isVisible())
        {
            if (gui.isScrollEnabled())
            {   // Scroll by updating the model's "current" time range.
                // The plot should listen to the model and adjust its x axis.
                double low = model.getStartTime().toDouble();
                double high = model.getEndTime().toDouble();
                final double range = high - low;
                // Update model such that 'high' equals 'now',
                // keeping the range.
                high = now.toDouble();
                low = high - range;
                controller_changes_xaxis = true;
                try
                {
                    model.setTimeRange(TimestampUtil.fromDouble(low),
                                       TimestampUtil.fromDouble(high));
                }
                catch (Exception ex)
                {   // Prevent follow-up errors by disabling the scroll
                    gui.enableScrolling(false);
                    Plugin.logException("Cannot scroll", ex);
                }
                // redraw is implied in the x axis update
                controller_changes_xaxis = false;
            }
            else // redraw w/o scroll
                chart.redrawTraces();
        }
    }
    
    /** Get data from archive for given model item,
     *   or all if <code>item==null</code>. */
    private void getArchivedData(IModelItem item)
    {
        XAxis xaxis = chart.getXAxis();
        ITimestamp start = TimestampUtil.fromDouble(xaxis.getLowValue());
        ITimestamp end = TimestampUtil.fromDouble(xaxis.getHighValue());
        if (item == null)
        {
            for (int i=0; i<model.getNumItems(); ++i)
                getArchivedData(model.getItem(i), start, end);
        }
        else
            getArchivedData(item, start, end);
    }

    /** Get data from archive for given model item and time range. */
    private void getArchivedData(IModelItem item,
                    ITimestamp start, ITimestamp end)
    {
    	ArchiveFetchJob job = new ArchiveFetchJob(item, start, end);
        job.schedule();
    }    
}
