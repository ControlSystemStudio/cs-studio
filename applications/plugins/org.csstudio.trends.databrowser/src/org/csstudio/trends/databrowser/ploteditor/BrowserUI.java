package org.csstudio.trends.databrowser.ploteditor;

import org.csstudio.archive.util.TimestampUtil;
import org.csstudio.platform.util.ITimestamp;
import org.csstudio.platform.util.TimestampFactory;
import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.InteractiveChart;
import org.csstudio.swt.chart.axes.XAxis;
import org.csstudio.trends.databrowser.Plugin;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridData;

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
        i_chart.getChart().getXAxis().setValueRange(now, now+60);
                
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
        
        
        Label l1 = new Label(i_chart.getButtonBar(), SWT.LEFT | SWT.CENTER);
        l1.setText(" " + Messages.ShowLast); //$NON-NLS-1$

        final Combo timeScale = new Combo(i_chart.getButtonBar(), SWT.READ_ONLY);
        String item;
        // Let's fill the combo with date range options.
        for(int i = 1; i <= 10; i++) 
        {
        	item = String.valueOf(12 * i) + " h";
        	if((i % 2) == 0) {
        		item += " (" + String.valueOf(i / 2) + " d)";
        	}
        	timeScale.add(item);
        }
        timeScale.addSelectionListener(new SelectionAdapter() 
        {
        	public void widgetSelected(SelectionEvent e)
            {
        		updateChartRange((Integer.valueOf(timeScale.getSelectionIndex()) + 1) * 12);
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
                ITimestamp start = TimestampUtil.fromDouble(xaxis.getLowValue());
                ITimestamp end = TimestampUtil.fromDouble(xaxis.getHighValue());
                StartEndDialog dlg = new StartEndDialog(getShell(), start, end);
                if (dlg.open() == StartEndDialog.OK)
                {
                    scroll_enable = false;
                    updateScrollPauseButton();
                    double low = dlg.getStart().toDouble();
                    double high = dlg.getEnd().toDouble();
                    if (low < high)
                        xaxis.setValueRange(low, high);
                }
            }
        });
    }
   
    private void updateChartRange(int hours)
    {
    	scroll_enable = false;
        updateScrollPauseButton();
        
        i_chart.getChart().getXAxis().setValueRange(60 * 60 * hours);
        i_chart.getChart().autozoom();
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
