package org.csstudio.trends.databrowser.plotpart;

import java.util.ArrayList;

import org.csstudio.apputil.ui.time.StartEndDialog;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.InteractiveChart;
import org.csstudio.swt.chart.axes.YAxis;
import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.model.Model;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

/** The user interface of the data browser.
 *  <p>
 *  It contains an InteractiveChart with added 'scroll' button.
 *  It scrolls the chart in response to model time range changes.
 * 
 *  @author Kay Kasemir
 */
public class BrowserUI extends Composite
{
    /** Model */
    final private Model model;

    private InteractiveChart i_chart;
    private Button undo;
    private Button scroll_pause;
    
    static private final String SCROLL_ON = "scroll_on"; //$NON-NLS-1$
    static private final String SCROLL_OFF = "scroll_off"; //$NON-NLS-1$
    static private final String UNDO = "undo"; //$NON-NLS-1$
    static private ImageRegistry images = null;

    /** Size of undo-stack; older entries are dropped */
    private static final int MAX_UNDO_STACK_SIZE = 50;
    /** Visible size, since all won't fit on screen */
    private static final int VISIBLE_UNDO_STACK_SIZE = 10;
    
    final private ArrayList<ZoomState> undo_stack = new ArrayList<ZoomState>();

    /** Create browser UI
     *  @param parent Parent widget
     *  @param style SWT style
     */
    public BrowserUI(final Model model, final Composite parent, final int style)
    {
        super(parent, style);
        this.model = model;
        // Initialize the singleton image registry
        if (images == null)
        {
            images = new ImageRegistry(parent.getDisplay());
            images.put(SCROLL_ON, Plugin.getImageDescriptor("icons/scroll_on.gif")); //$NON-NLS-1$
            images.put(SCROLL_OFF, Plugin.getImageDescriptor("icons/scroll_off.gif")); //$NON-NLS-1$
            images.put(UNDO, Plugin.getImageDescriptor("icons/undo.gif")); //$NON-NLS-1$
        }
        makeGUI();
    }
        
    /** @return Returns the InteractiveChart. */
    public InteractiveChart getInteractiveChart()
    {
        return i_chart;
    }
    
    /** Create the GUI elements. */
    @SuppressWarnings("nls")
    private void makeGUI()
    {
        // Create chart
        setLayout(new FillLayout());
        i_chart = new InteractiveChart(this,
                        Chart.TIME_CHART
                      | Chart.USE_TRACE_NAMES
                      | InteractiveChart.ZOOM_X_FROM_END);
        setTimeRange(model.getStartTime(), model.getEndTime());
                
        // Undo Button that opens popup with undo-able steps
        // TODO Would like to use the widget that the IDE's undo/redo,
        // last/previous edit location buttons use, but don't know how that's
        // done. So we use a plain button with popup, and we handle the popup
        // activation ourselves.
        undo = new Button(i_chart.getButtonBar(), SWT.CENTER);
        undo.setImage(images.get(UNDO));
        undo.setToolTipText(Messages.Undo_TT);
        undo.setEnabled(false);
        final MenuManager undo_menu_manager = new MenuManager("#PopupMenu");
        undo_menu_manager.setRemoveAllWhenShown(true);
        undo_menu_manager.addMenuListener(new IMenuListener()
        {
            public void menuAboutToShow(IMenuManager manager)
            {
                // Show undo stack in reverse order, up tp VISIBLE.. elements
                final int last = undo_stack.size();
                final int first = (last > VISIBLE_UNDO_STACK_SIZE)
                    ? last - VISIBLE_UNDO_STACK_SIZE
                    : 0;
                for (int i=last-1;  i>=first;  --i)
                    manager.add(undo_stack.get(i));
            }
        });
        final Menu undo_menu = undo_menu_manager.createContextMenu(undo);
        undo.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                final Rectangle rect = undo.getBounds ();
                final Point menu_pt = 
                    i_chart.getButtonBar().toDisplay(rect.x, rect.y + rect.height);
                undo_menu.setLocation(menu_pt);
                undo_menu.setVisible (true);
            }
        });
        
        /// Scroll button
        scroll_pause = new Button(i_chart.getButtonBar(), SWT.CENTER);        
        updateScrollPauseButton(model.isScrollEnabled());
        scroll_pause.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                addCurrentZoomToUndoStack(scroll_pause.getToolTipText());
                // Toggle scroll mode
                model.enableScroll(! model.isScrollEnabled());
            }
        });
        
        // Start/end time button
        Button time_config = new Button(i_chart.getButtonBar(), SWT.CENTER);
        time_config.setText(Messages.TimeConfig);
        time_config.setToolTipText(Messages.TimeConfig_TT);
        time_config.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                StartEndDialog dlg =
                    new StartEndDialog(getShell(),
                                       model.getStartSpecification(),
                                       model.getEndSpecification());
                if (dlg.open() != StartEndDialog.OK)
                    return;
                addCurrentZoomToUndoStack(Messages.TimeConfig_TT);
                try
                {
                    // If we request some_start ... 'now',
                    // assume we want to scroll from then on.
                    model.setTimeSpecifications(dlg.getStartSpecification(),
                                                dlg.getEndSpecification());
                }
                catch (Exception ex)
                {
                    Plugin.getLogger().error("Error: ", ex);
                }
            }
        });
    }
   
    public void setTimeRange(final ITimestamp start, final ITimestamp end)
    {
        i_chart.getChart().getXAxis().setValueRange(start.toDouble(),
                                                    end.toDouble());
    }

    /** Update the scroll button's image and tooltip. */
    public void updateScrollPauseButton(final boolean scroll)
    {
        if (scroll)
        {
            scroll_pause.setImage(images.get(SCROLL_ON));
            scroll_pause.setToolTipText(Messages.StopScroll_TT);
        }
        else
        {
            scroll_pause.setImage(images.get(SCROLL_OFF));
            scroll_pause.setToolTipText(Messages.StartScroll_TT);
        }
    }
    
    /** Memorize the current zoom state for 'undo' */
    public void addCurrentZoomToUndoStack(final String description)
    {
        // Get current state
        final Chart chart = i_chart.getChart();
        final int N = chart.getNumYAxes();
        final double starts[] = new double[N];
        final double ends[] = new double[N];
        for (int i=0; i<N; ++i)
        {
            final YAxis yaxis = chart.getYAxis(i);
            starts[i] = yaxis.getLowValue();
            ends[i] = yaxis.getHighValue();
        }
        // Wrap as ZoomState and remember in undo stack
        final ZoomState zoom_state = new ZoomState(this,
                NLS.bind(Messages.Undo_Format, description),
                model.isScrollEnabled(),
                model.getStartSpecification(), model.getEndSpecification(),
                starts, ends);
        // Prevent undo-stack from growing out of bounds
        if (undo_stack.size() > MAX_UNDO_STACK_SIZE)
            undo_stack.remove(0);
        // Add new item to end of array
        undo_stack.add(zoom_state);
        // Now have something to undo
        if (!undo.getEnabled())
            undo.setEnabled(true);
    }

    /** Restore model to given zoom state */
    public void restoreZoomState(final ZoomState zoom_state)
    {
        // Remove zoom_state and subsequent entries from undo stack
        for (int i = undo_stack.size()-1;  i >= 0;  --i)
        {
            final boolean found = undo_stack.get(i) == zoom_state;
            undo_stack.remove(i);
            if (found)
                break;
        }
        if (undo_stack.size() <= 0)
            undo.setEnabled(false);
        // Restore the settings stored in the zoom_state: Y Axes
        final int N = zoom_state.getYAxisInfoCount();
        for (int i=0; i<N; ++i)
        {
            i_chart.getChart().getYAxis(i).setValueRange(
                    zoom_state.getYAxisStart(i),
                    zoom_state.getYAxisEnd(i));
        }
        // Time Axis and scrolling
        try
        {
            model.setTimeSpecifications(zoom_state.getStartSpecification(),
                    zoom_state.getEndSpecification());
        }
        catch (Exception ex)
        {
            Plugin.getLogger().error(ex);
        }
        if (zoom_state.wasScrolling() != model.isScrollEnabled())
            model.enableScroll(zoom_state.wasScrolling());
    }    
}
