package org.csstudio.trends.databrowser.plotpart;

import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.InteractiveChart;
import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.ModelListener;
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
    private final Model model;
    private ModelListener listener;

    private InteractiveChart i_chart;
    private boolean scroll_enable = true;
    private Button scroll_pause;
    
    static private ImageRegistry images = null;
    static private final String SCROLL_ON = "scroll_on"; //$NON-NLS-1$
    static private final String SCROLL_OFF = "scroll_off"; //$NON-NLS-1$

    /** Create browser UI
     *  @param listener BrowserUIListener
     *  @param parent Parent widget
     *  @param style SWT style
     */
    public BrowserUI(Model model, Composite parent, int style)
    {
        super(parent, style);
        this.model = model;
        // Initialize the singleton image registry
        if (images == null)
        {
            images = new ImageRegistry(parent.getDisplay());
            images.put(SCROLL_ON, Plugin.getImageDescriptor("icons/scroll_on.gif")); //$NON-NLS-1$
            images.put(SCROLL_OFF, Plugin.getImageDescriptor("icons/scroll_off.gif")); //$NON-NLS-1$
        }
        makeGUI();
    }
    
    /** Must be called to clean up. */
    @Override
    public void dispose()
    {
        model.removeListener(listener);
    }
    
    /** @return Returns the chart widget. */
    public Chart getChart()
    {
        return i_chart.getChart();
    }
    
    /** @return Returns <code>true</code> if scrolling is enabled (requested). */
    public boolean isScrollEnabled()
    {
        return scroll_enable;
    }
    
    /** @return Returns <code>true</code> if scrolling is enabled (requested). */
    public void enableScrolling(boolean enable)
    {
        scroll_enable = enable;
        updateScrollPauseButton();
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
                
        // Add scroll button
        scroll_pause = new Button(i_chart.getButtonBar(), SWT.CENTER);        
        updateScrollPauseButton();
        scroll_pause.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                scroll_enable = !scroll_enable;
                updateScrollPauseButton();
            }
        });
        
        Button time_config = new Button(i_chart.getButtonBar(), SWT.CENTER);
        time_config.setText(Messages.TimeConfig);
        time_config.setToolTipText(Messages.TimeConfig_TT);
        time_config.addSelectionListener(new SelectionAdapter()
        {
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
                    scroll_enable = dlg.isEndNow();
                    updateScrollPauseButton();
                    model.setTimeSpecifications(dlg.getStartSpecification(),
                                                 dlg.getEndSpecification());
                }
                catch (Exception ex)
                {
                    Plugin.logException("Error: ", ex);
                }
            }
        });
        
        // Listen to model's time range
        listener = new ModelListener()
        {
            public void timeSpecificationsChanged()
            {}
            
            public void timeRangeChanged()
            {   // Adjust the x axis to the "current" model time range
                final double low = model.getStartTime().toDouble();
                final double high = model.getEndTime().toDouble();
                getChart().getXAxis().setValueRange(low, high);
            }
            
            public void periodsChanged()
            {}

            public void entriesChanged()
            {}
            
            public void entryAdded(IModelItem new_item) 
            {}
            
            public void entryConfigChanged(IModelItem item) 
            {}
            
            public void entryLookChanged(IModelItem item) 
            {}
            
            public void entryArchivesChanged(IModelItem item)
            {}

            public void entryRemoved(IModelItem removed_item) 
            {}
        };
        model.addListener(listener);
        
        // Initialize the time axis
        listener.timeRangeChanged();
    }
   
    /** Update the scroll button's image and tooltip. */
    private void updateScrollPauseButton()
    {
        if (scroll_enable)
        {
            scroll_pause.setImage(images.get(SCROLL_ON));
            scroll_pause.setToolTipText(Messages.StopScroll);
        }
        else
        {
            scroll_pause.setImage(images.get(SCROLL_OFF));
            scroll_pause.setToolTipText(Messages.StartScroll);
        }
    }
}
