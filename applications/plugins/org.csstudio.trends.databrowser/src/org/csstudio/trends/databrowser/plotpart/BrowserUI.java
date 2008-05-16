package org.csstudio.trends.databrowser.plotpart;

import org.csstudio.platform.data.ITimestamp;
import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.InteractiveChart;
import org.csstudio.swt.chart.axes.YAxis;
import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.util.time.swt.StartEndDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

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
    private Button scroll_pause;
    
    static private ImageRegistry images = null;
    static private final String SCROLL_ON = "scroll_on"; //$NON-NLS-1$
    static private final String SCROLL_OFF = "scroll_off"; //$NON-NLS-1$
    static private final String UNDO = "undo"; //$NON-NLS-1$

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
                
        // Add Data-Browser specific buttons to chart: Undo...
        final Button undo = new Button(i_chart.getButtonBar(), SWT.CENTER);
        undo.setImage(images.get(UNDO));
        
        /// Scroll button
        scroll_pause = new Button(i_chart.getButtonBar(), SWT.CENTER);        
        updateScrollPauseButton(model.isScrollEnabled());
        scroll_pause.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
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
    public void addCurrentZoomToUndoStack()
    {
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
        final ZoomState zoom_state = new ZoomState(model.isScrollEnabled(),
                model.getStartSpecification(), model.getEndSpecification(),
                starts, ends);
        System.out.println(zoom_state);
        // TODO Add to stack, ...
    }    
}
