package org.csstudio.trends.databrowser.plotpart;

import org.csstudio.swt.chart.Chart;
import org.csstudio.trends.databrowser.model.Model;

/** Perform periodic 'scans' of the model, and redraw the UI.
 *  <p>
 *  This scanner hooks into the display's timerExec() mechanism.
 *  When launched from the 'File/New' wizard, that creates
 *  a problem:
 *  The Wizard starts the editor in a modal context,
 *  and if the editor ends up starting the ScannerAndScroller,
 *  it ends up adding timer tasks to the modal context, so that
 *  the modal context never finishes its event queue, and hence
 *  the wizard stays up and never finishes.
 *  <br>
 *  So now the Controller takes care to only start the model
 *  and the scanner when the first entries are added to the model,
 *  which means: Nothing happens initially when launched on an empty
 *  file via the File/New wizard.
 *  
 *  @author Kay Kasemir
 */
public class ScannerAndScroller
{
    private BrowserUI browser_ui;
    private Model model;
    private ScannerAndScrollerListener listener;
    private Chart chart;
    
    private int scans;
    private int scans_between_redraws;
    private Runnable task = new Runnable()
    {
        public void run()
        {   // Quit when receiver quit.
            if (chart.isDisposed())
                return;
            ++scans;
            // Scroll or simply redraw w/o scroll.
            if (scans >= scans_between_redraws)
            {
                listener.scan(true);
                scans = 0;
            }
            else
                listener.scan(false);
            schedule();
        }
    };

    /** Construct scanner/scroller for given UI and model. */
    public ScannerAndScroller(BrowserUI ui, Model model,
                    ScannerAndScrollerListener listener)
    {
        // System.out.println("ScannerAndScroller started...");
        browser_ui = ui;
        this.model = model;
        this.listener = listener;
        chart = browser_ui.getChart();
        scans = 0;
        // Trigger initial period-related calculations.
        periodsChanged();
        // Initial kick-off, re-scheduled in 'task'.
        schedule();
    }
    
    /** Configure the periodic behavior.
     *  @see org.csstudio.trends.databrowser.model.ModelListener
     */
    public void periodsChanged()
    {
        double update = model.getUpdatePeriod();
        double scan = model.getScanPeriod();
        // The model should enforce these constraints
        if (scan == 0.0)
            throw new Error("Zero scan period"); //$NON-NLS-1$
        if (scan > update)
            throw new Error("Scan period exceeds update period"); //$NON-NLS-1$
        scans_between_redraws =
            (int)(update / scan);
    }
    
    /** Schedule the 'task' to get executed (again). */
    private void schedule()
    {   // scan_period <= update_period; run the task at the 'faster' rate.
        chart.getDisplay().timerExec(
                (int)(1000 * model.getScanPeriod()), task);
    }
}
