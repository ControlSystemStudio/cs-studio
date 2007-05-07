package org.csstudio.trends.databrowser.ploteditor;

import org.csstudio.platform.util.TimestampFactory;
import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.InteractiveChart;
import org.csstudio.swt.chart.axes.XAxis;
import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.util.time.swt.StartEndDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/** The user interface for the data browser.
 *  <p>
 *  It contains an InteractiveChart with added 'scroll' button.
 * 
 *  @author Kay Kasemir
 */
public class BrowserUI extends Composite
{
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
    public BrowserUI(Composite parent, int style)
    {
        super(parent, style);
        // Initialize the singleton image registry
        if (images == null)
        {
            images = new ImageRegistry(parent.getDisplay());
            images.put(SCROLL_ON, Plugin.getImageDescriptor("icons/scroll_on.gif")); //$NON-NLS-1$
            images.put(SCROLL_OFF, Plugin.getImageDescriptor("icons/scroll_off.gif")); //$NON-NLS-1$
        }
        makeGUI();
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
        // Init the X (Time) axis range
        long now = TimestampFactory.now().seconds();
        i_chart.getChart().getXAxis().setValueRange(now-3600, now);
                
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
                XAxis xaxis = i_chart.getChart().getXAxis();
                /* TODO
                ITimestamp start = TimestampUtil.fromDouble(xaxis.getLowValue());
                ITimestamp end = TimestampUtil.fromDouble(xaxis.getHighValue());
                */
                StartEndDialog dlg = new StartEndDialog(getShell());
                if (dlg.open() == StartEndDialog.OK)
                {
                    scroll_enable = false;
                    updateScrollPauseButton();
                    double low = dlg.getStartCalendar().getTimeInMillis()/1000.0;
                    double high = dlg.getEndCalendar().getTimeInMillis()/1000.0;
                    if (low < high)
                        xaxis.setValueRange(low, high);
                }
            }
        });
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
